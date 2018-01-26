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

package jhi.germinate.util.importer.phenotype;

import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.common.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link PhenotypeDataImporter} uses an {@link IDataReader} to read and parse {@link PhenotypeData} objects and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class PhenotypeDataImporter extends DataImporter<PhenotypeData>
{
	private HashMap<String, Accession> accessionCache = new HashMap<>();
	private HashMap<String, Phenotype> phenotypeCache = new HashMap<>();
	private HashMap<String, Treatment> treatmentCache = new HashMap<>();

	private Set<Long> createdPhenotypeDataIds = new HashSet<>();
	private Set<Long> createdTreatmentIds     = new HashSet<>();
	private Set<Long> createdAccessionIds     = new HashSet<>();

	private static Dataset dataset;

	private MetadataImporter  metadataImporter;
	private PhenotypeImporter phenotypeImporter;

	public static void main(String[] args)
	{
		new PhenotypeDataImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		// Import the meta-data first. Get the created dataset
		metadataImporter = new MetadataImporter(ExperimentType.trials);
		metadataImporter.run(input, server, database, username, password, port, ExcelMetadataReader.class.getCanonicalName());
		dataset = metadataImporter.getDataset();

		// Then import the phenotypes
		phenotypeImporter = new PhenotypeImporter();
		phenotypeImporter.run(input, server, database, username, password, port, ExcelPhenotypeReader.class.getCanonicalName());

		// Then run the rest of this importer
		super.run(input, server, database, username, password, port, readerName);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelPhenotypeDataReader();
	}

	@Override
	public void deleteInsertedItems()
	{
		metadataImporter.deleteInsertedItems();
		phenotypeImporter.deleteInsertedItems();
		deleteItems(createdPhenotypeDataIds, "phenotypedata");
		deleteItems(createdTreatmentIds, "treatments");
		deleteItems(createdAccessionIds, "germinatebase");
	}

	@Override
	protected void write(PhenotypeData entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getValue()))
		{
			// First, make sure the actual accession exists
			Accession accession = getAccession(entry.getAccession(), false);

			// Is this a rep?
			String rep = entry.getAccession().getExtra(ExcelPhenotypeDataReader.EXTRA_REP);
			String treatment = entry.getAccession().getExtra(ExcelPhenotypeDataReader.EXTRA_TREATMENT);

			// If so, make sure that rep exists as well, and remember its ID.
			if (!StringUtils.isEmpty(rep))
			{
				// Give the rep a unique name (accession + datasetId + repNumber)
				rep = accession.getName() + "-" + dataset.getId() + "-" + rep;

				accession = getAccession(new Accession()
						.setGeneralIdentifier(rep)
						.setName(rep)
						.setNumber(rep)
						.setEntityType(EntityType.PLANT_PLOT)
						.setEntityParentId(accession.getId()), true);
			}

			entry.setAccession(accession);

			if (!StringUtils.isEmpty(treatment))
				getTreatment(entry, treatment);

			// Get the phenotype for this column
			getPhenotype(entry);

			// Insert the cell value
			createPhenotypeData(entry);
		}
	}

	/**
	 * Import the {@link PhenotypeData} object into the database.
	 *
	 * @param entry The {@link PhenotypeData} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createPhenotypeData(PhenotypeData entry) throws DatabaseException
	{
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM phenotypedata WHERE phenotype_id = ? AND germinatebase_id = ? AND dataset_id <=> ? AND phenotype_value = ? AND recording_date <=> ? AND treatment_id <=> ?");
		int i = 1;
		stmt.setLong(i++, entry.getPhenotype().getId());
		stmt.setLong(i++, entry.getAccession().getId());
		stmt.setLong(i++, dataset.getId());
		stmt.setString(i++, entry.getValue());

		if (entry.getRecordingDate() != null)
			stmt.setDate(i++, new Date(entry.getRecordingDate()));
		else
			stmt.setNull(i++, Types.TIMESTAMP);

		if (entry.getTreatment() != null)
			stmt.setString(i++, entry.getTreatment().getName());
		else
			stmt.setNull(i++, Types.VARCHAR);

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			entry.setDataset(dataset);

			PhenotypeData.Writer.Inst.get().write(databaseConnection, entry);
			createdPhenotypeDataIds.add(entry.getId());
		}
	}

	/**
	 * Get the {@link Accession} object for this {@link PhenotypeData}
	 *
	 * @param entry The {@link PhenotypeData} containing the {@link Accession} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private Accession getAccession(Accession entry, boolean isRep) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry.getGeneralIdentifier()))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		Accession cached = accessionCache.get(entry.getGeneralIdentifier());

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier = ?");
			int i = 1;
			stmt.setString(i++, entry.getGeneralIdentifier());

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				cached = Accession.ImportParser.Inst.get().parse(rs, null, true);
			}
			else if (isRep)
			{
				cached = entry;
				Accession.Writer.Inst.get().write(databaseConnection, cached);
				createdAccessionIds.add(cached.getId());
			}
			else
			{
				throw new DatabaseException("Accession not found: " + entry.getGeneralIdentifier() + " Please make sure it is imported before trying to import the data.");
			}
		}

		accessionCache.put(entry.getGeneralIdentifier(), cached);
		return cached;
	}

	/**
	 * Get the {@link Treatment} object for this {@link PhenotypeData}
	 *
	 * @param entry The {@link PhenotypeData} containing the {@link Treatment} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void getTreatment(PhenotypeData entry, String treatment) throws DatabaseException
	{
		if (StringUtils.isEmpty(treatment))
			return;

		Treatment cached = treatmentCache.get(treatment);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM treatments WHERE name = ?");
			stmt.setString(1, treatment);

			DatabaseResult rs = stmt.query();

			if (rs.next())
				cached = Treatment.Parser.Inst.get().parse(rs, null, true);
			else
			{
				cached = new Treatment()
						.setName(treatment)
						.setDescription(treatment)
						.setCreatedOn(new Date())
						.setUpdatedOn(new Date());
				Treatment.Writer.Inst.get().write(databaseConnection, cached);
				createdTreatmentIds.add(entry.getId());
			}
		}

		entry.setTreatment(cached);

		treatmentCache.put(treatment, cached);
	}

	/**
	 * Get the {@link Phenotype} object for this {@link PhenotypeData}
	 *
	 * @param entry The {@link PhenotypeData} containing the {@link Phenotype} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void getPhenotype(PhenotypeData entry) throws DatabaseException
	{
		String name = entry.getPhenotype().getName();

		if (StringUtils.isEmpty(name))
			return;

		Phenotype cached = phenotypeCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM phenotypes WHERE name = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
				cached = Phenotype.Parser.Inst.get().parse(rs, null, true);
			else
				throw new DatabaseException("Phenotype not found: " + name + " Please make sure to include all phenotypes in the phenotypes tab.");
		}

		entry.setPhenotype(cached);

		phenotypeCache.put(name, cached);
	}
}
