/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.util.importer.mcpd;

import java.io.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.attribute.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link McpdImporter} uses an {@link IDataReader} to read and parse {@link Accession}s and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class McpdImporter extends DataImporter<Accession>
{
	private Set<Long> createdAccessionIds          = new HashSet<>();
	private Set<Long> createdSubtaxaIds            = new HashSet<>();
	private Set<Long> createdTaxonomyIds           = new HashSet<>();
	private Set<Long> createdAttributeIds          = new HashSet<>();
	private Set<Long> createdAttributeDataIds      = new HashSet<>();
	private Set<Long> createdInstitutionIds        = new HashSet<>();
	private Set<Long> createdStorageDataIds        = new HashSet<>();
	private Set<Long> createdLocationIds           = new HashSet<>();
	private Set<Long> createdPedigreeDefinitionIds = new HashSet<>();
	private Set<Long> createdPedigreeNotationIds   = new HashSet<>();

	private AttributeDataImporter attributeDataImporter;

	public static void main(String[] args)
	{
		new McpdImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		super.run(input, server, database, username, password, port, readerName);

		// Import the attribute data for the accessions as well
		attributeDataImporter = new AttributeDataImporter();
		attributeDataImporter.run(input, server, database, username, password, port, ExcelAttributeDataReader.class.getCanonicalName());
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new TabDelimitedMcpdReader();
	}

	/**
	 * Deletes all the database entries that have been created by this import tool.
	 */
	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdAccessionIds, "germinatebase");
		deleteItems(createdPedigreeDefinitionIds, "pedigreedefinitions");
		deleteItems(createdPedigreeNotationIds, "pedigreenotations");
		deleteItems(createdInstitutionIds, "institutions");
		deleteItems(createdLocationIds, "locations");
		deleteItems(createdStorageDataIds, "storagedata");
		deleteItems(createdAttributeIds, "attributes");
		deleteItems(createdAttributeDataIds, "attributedata");
		deleteItems(createdSubtaxaIds, "subtaxa");
		deleteItems(createdTaxonomyIds, "taxonomies");

		if (attributeDataImporter != null)
			attributeDataImporter.deleteInsertedItems();
	}

	@Override
	protected void write(Accession entry) throws DatabaseException
	{
		// Write or get the referenced database objects
		createOrGetInstitution(entry);
		createOrGetTaxonomy(entry);
		createOrGetSubtaxa(entry);
		getBiologicalStatus(entry);
		getCollectingSource(entry);
		getMlsStatus(entry);
		createOrGetLocation(entry);

		// Write or get the accession itself
		entry = createOrGetAccession(entry);

		// Now we've got the Accession id, so we can insert stuff tht depends on it.
		getStorage(entry);

		// Write the remarks
		Attribute remarks = createOrGetAttribute(entry);
		if (remarks != null)
		{
			AttributeData attributeData = new AttributeData()
					.setAttribute(remarks)
					.setForeign(entry)
					.setValue(entry.getExtra(McpdField.REMARKS.name()));

			if (attributeData.getValue() != null)
			{
				AttributeData.Writer.Inst.get().write(databaseConnection, attributeData);
				createdAttributeDataIds.add(attributeData.getId());
			}
		}

		// Write the pedigree information
		if (!StringUtils.isEmpty(entry.getExtra(McpdField.ANCEST.name())))
		{
			PedigreeNotation notation = createOrGetPedigreeNotation("MCPD");

			PedigreeDefinition pedigreeDefinition = new PedigreeDefinition()
					.setNotation(notation)
					.setDefinition(entry.getExtra(McpdField.ANCEST.name()))
					.setAccession(entry);

			PedigreeDefinition.Writer.Inst.get().write(databaseConnection, pedigreeDefinition);
			createdPedigreeDefinitionIds.add(pedigreeDefinition.getId());
		}
	}

	/**
	 * Sets the {@link Country} based on the 3 letter country code.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void getCountry(Accession entry) throws DatabaseException
	{
		if (entry.getLocation() == null || entry.getLocation().getCountry() == null)
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM countries WHERE country_code3 = ?");
		stmt.setString(1, entry.getLocation().getCountry().getCountryCode3());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.getLocation().setCountry(Country.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			throw new DatabaseException("Invalid 3-letter ISO 3166-1 country code: " + entry.getLocation().getCountry().getCountryCode3());
		}
	}

	/**
	 * Sets the {@link BiologicalStatus} based on the id.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void getBiologicalStatus(Accession entry) throws DatabaseException
	{
		if (entry.getBiologicalStatus() == null || entry.getBiologicalStatus().getId() == null)
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM biologicalstatus WHERE id = ?");
		stmt.setLong(1, entry.getBiologicalStatus().getId());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setBiologicalStatus(BiologicalStatus.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			throw new DatabaseException("Invalid sampstat value: " + entry.getBiologicalStatus().getId());
		}
	}

	/**
	 * Sets the {@link CollectingSource} based on the id.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void getCollectingSource(Accession entry) throws DatabaseException
	{
		if (entry.getCollSrc() == null || entry.getCollSrc().getId() == null)
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM collectingsources WHERE id = ?");
		stmt.setLong(1, entry.getCollSrc().getId());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setCollSrc(CollectingSource.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			throw new DatabaseException("Invalid collsrc value: " + entry.getCollSrc().getId());
		}
	}

	/**
	 * Sets the {@link MlsStatus} based on the id.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void getMlsStatus(Accession entry) throws DatabaseException
	{
		if (entry.getMlsStatus() == null || entry.getMlsStatus().getId() == null)
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM mlsstatus WHERE id = ?");
		stmt.setLong(1, entry.getMlsStatus().getId());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setMlsStatus(MlsStatus.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			throw new DatabaseException("Invalid mlsststus value: " + entry.getMlsStatus().getId());
		}
	}

	/**
	 * Gets or creates the {@link Accession} based on the given MCPD information. It'll check for duplicates, but won't updated existing entries based
	 * on a subset of the fields.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private Accession createOrGetAccession(Accession entry) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry.getGeneralIdentifier()))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier <=> ? AND puid <=> ? AND number <=> ? AND collnumb <=> ? AND collcode <=> ? AND collmissid <=> ? AND donor_number <=> ? AND donor_name <=> ? AND donor_code <=> ? AND name = ? AND acqdate <=> ? AND colldate <=> ? AND collname <=> ? AND breeders_code <=> ? AND breeders_name <=> ? AND othernumb <=> ? AND duplsite <=> ? ANd duplinstname <=> ? AND institution_id <=> ? AND taxonomy_id <=> ? AND subtaxa_id <=> ? AND location_id <=> ? AND biologicalstatus_id <=> ? AND collsrc_id <=> ? AND mlsstatus_id <=> ?");
		int i = 1;
		stmt.setString(i++, entry.getGeneralIdentifier());
		stmt.setString(i++, entry.getPuid());
		stmt.setString(i++, entry.getNumber());
		stmt.setString(i++, entry.getCollNumb());
		stmt.setString(i++, entry.getCollCode());
		stmt.setString(i++, entry.getCollMissId());
		stmt.setString(i++, entry.getDonorNumber());
		stmt.setString(i++, entry.getDonorName());
		stmt.setString(i++, entry.getDonorCode());
		stmt.setString(i++, entry.getName());
		stmt.setString(i++, entry.getAcqDate());
		stmt.setDate(i++, entry.getCollDate() != null ? new Date(entry.getCollDate()) : null);
		stmt.setString(i++, entry.getCollName());
		stmt.setString(i++, entry.getBreedersCode());
		stmt.setString(i++, entry.getBreedersName());
		stmt.setString(i++, entry.getOtherNumb());
		stmt.setString(i++, entry.getDuplSite());
		stmt.setString(i++, entry.getDuplInstName());
		stmt.setLong(i++, entry.getInstitution() != null ? entry.getInstitution().getId() : null);
		stmt.setLong(i++, entry.getTaxonomy() != null ? entry.getTaxonomy().getId() : null);
		stmt.setLong(i++, entry.getSubtaxa() != null ? entry.getSubtaxa().getId() : null);
		stmt.setLong(i++, entry.getLocation() != null ? entry.getLocation().getId() : null);
		stmt.setLong(i++, entry.getBiologicalStatus() != null ? entry.getBiologicalStatus().getId() : null);
		stmt.setLong(i++, entry.getCollSrc() != null ? entry.getCollSrc().getId() : null);
		stmt.setLong(i++, entry.getMlsStatus() != null ? entry.getMlsStatus().getId() : null);

		DatabaseResult rs = stmt.query();

		Accession result = entry;

		if (rs.next())
		{
			result = Accession.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			Accession.Writer.Inst.get().write(databaseConnection, result);
			createdAccessionIds.add(result.getId());
		}

		return result;
	}

	/**
	 * Creates or gets the {@link Location} and then sets it.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void createOrGetLocation(Accession entry) throws DatabaseException
	{
		if (entry.getLocation() == null || StringUtils.isEmpty(entry.getLocation().getName()))
			return;

		getCountry(entry);

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM locations WHERE site_name = ? AND latitude <=> CAST(? AS DECIMAL(64,10)) AND longitude <=> CAST(? AS DECIMAL(64,10)) AND elevation <=> ? AND coordinate_uncertainty <=> ? AND coordinate_datum <=> ? AND georeferencing_method <=> ? AND country_id = ?");
		int i = 1;
		stmt.setString(i++, entry.getLocation().getName());
		stmt.setDouble(i++, entry.getLocation().getLatitude());
		stmt.setDouble(i++, entry.getLocation().getLongitude());
		stmt.setDouble(i++, entry.getLocation().getElevation());
		stmt.setInt(i++, entry.getLocation().getCoordinateUncertainty());
		stmt.setString(i++, entry.getLocation().getCoordinateDatum());
		stmt.setString(i++, entry.getLocation().getGeoreferencingMethod());
		stmt.setLong(i++, entry.getLocation().getCountry().getId());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setLocation(Location.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Location.Writer.Inst.get().write(databaseConnection, entry.getLocation());
			createdLocationIds.add(entry.getLocation().getId());
		}
	}

	/**
	 * Creates or gets the {@link PedigreeNotation} for the given name.
	 *
	 * @param name The name of the {@link PedigreeNotation}
	 * @return The {@link PedigreeNotation}
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private PedigreeNotation createOrGetPedigreeNotation(String name) throws DatabaseException
	{
		if (StringUtils.isEmpty(name))
			return null;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM pedigreenotations WHERE name = ?");
		int i = 1;
		stmt.setString(i++, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			return PedigreeNotation.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			PedigreeNotation notation = new PedigreeNotation()
					.setName(name);

			PedigreeNotation.Writer.Inst.get().write(databaseConnection, notation);
			createdPedigreeNotationIds.add(notation.getId());

			return notation;
		}
	}

	/**
	 * Creates/gets and then sets the institute based on the MCPD fields.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void createOrGetInstitution(Accession entry) throws DatabaseException
	{
		if (entry.getInstitution() == null || StringUtils.isEmpty(entry.getInstitution().getName()))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM institutions WHERE code <=> ? AND name <=> ? AND address <=> ?");
		int i = 1;
		stmt.setString(i++, entry.getInstitution().getCode());
		stmt.setString(i++, entry.getInstitution().getName());
		stmt.setString(i++, entry.getInstitution().getAddress());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setInstitution(Institution.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Institution.Writer.Inst.get().write(databaseConnection, entry.getInstitution());
			createdInstitutionIds.add(entry.getInstitution().getId());
		}
	}

	/**
	 * Creates/gets and then sets the {@link Taxonomy} based on the MCPD fields.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void createOrGetTaxonomy(Accession entry) throws DatabaseException
	{
		if (entry.getTaxonomy() == null || StringUtils.areEmpty(entry.getTaxonomy().getGenus(), entry.getTaxonomy().getSpecies(), entry.getTaxonomy().getAuthor(), entry.getTaxonomy().getCropName()))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM taxonomies WHERE genus <=> ? AND species <=> ? AND species_author <=> ? AND cropname <=> ?");
		int i = 1;
		stmt.setString(i++, entry.getTaxonomy().getGenus());
		stmt.setString(i++, entry.getTaxonomy().getSpecies());
		stmt.setString(i++, entry.getTaxonomy().getAuthor());
		stmt.setString(i++, entry.getTaxonomy().getCropName());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setTaxonomy(Taxonomy.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Taxonomy.Writer.Inst.get().write(databaseConnection, entry.getTaxonomy());
			createdTaxonomyIds.add(entry.getTaxonomy().getId());
		}

		if (entry.getSubtaxa() != null)
			entry.getSubtaxa().setTaxonomy(entry.getTaxonomy());
	}

	/**
	 * Creates/gets and then sets the {@link Subtaxa} based on the MCPD fields.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void createOrGetSubtaxa(Accession entry) throws DatabaseException
	{
		if (entry.getTaxonomy() == null || entry.getSubtaxa() == null || StringUtils.areEmpty(entry.getSubtaxa().getTaxonomyIdentifier(), entry.getSubtaxa().getAuthor()))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM subtaxa WHERE taxonomy_id = ? AND subtaxa_author <=> ? AND taxonomic_identifier <=> ?");
		int i = 1;
		stmt.setLong(i++, entry.getTaxonomy().getId());
		stmt.setString(i++, entry.getSubtaxa().getAuthor());
		stmt.setString(i++, entry.getSubtaxa().getTaxonomyIdentifier());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setSubtaxa(Subtaxa.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Subtaxa.Writer.Inst.get().write(databaseConnection, entry.getSubtaxa());
			createdSubtaxaIds.add(entry.getSubtaxa().getId());
		}
	}

	/**
	 * Creates/gets and then sets the {@link Attribute} representing the Remarks.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private Attribute createOrGetAttribute(Accession entry) throws DatabaseException
	{
		String name = "Remarks";

		if (StringUtils.isEmpty(name, entry.getExtra(McpdField.REMARKS.name())))
			return null;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM attributes WHERE name = ? AND target_table = 'germinatebase'");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		Attribute result;

		if (rs.next())
		{
			result = Attribute.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			result = new Attribute()
					.setName(name)
					.setDescription(name)
					.setTargetTable(GerminateDatabaseTable.germinatebase.name())
					.setDataType("char");

			Attribute.Writer.Inst.get().write(databaseConnection, result);
			createdAttributeIds.add(result.getId());
		}

		return result;
	}

	/**
	 * Gets and sets the list of {@link Storage} objects.
	 *
	 * @param entry The current {@link Accession}
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void getStorage(Accession entry) throws DatabaseException
	{
		String storageIds = entry.getExtra(McpdField.STORAGE.name());

		List<Storage> result = new ArrayList<>();

		if (!StringUtils.isEmpty(storageIds))
		{
			String[] parts = storageIds.split(";");
			for (int i = 0; i < parts.length; i++)
			{
				DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM storage WHERE id = ?");
				stmt.setString(1, parts[i]);

				DatabaseResult rs = stmt.query();

				if (rs.next())
					result.add(Storage.Parser.Inst.get().parse(rs, null, true));
			}
		}

		for (Storage s : result)
		{
			StorageData data = new StorageData()
					.setAccession(entry)
					.setStorage(s);

			StorageData.Writer.Inst.get().write(databaseConnection, data);
			createdStorageDataIds.add(data.getId());
		}
	}
}
