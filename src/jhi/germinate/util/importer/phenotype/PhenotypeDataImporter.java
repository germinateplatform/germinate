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
import java.util.*;
import java.util.Map;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
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
	protected List<PhenotypeData>    cache      = new ArrayList<>();
	private   Map<String, Accession> accessions = new HashMap<>();
	private   Map<String, Long>      treatments = new HashMap<>();
	private   Map<String, Long>      phenotypes = new HashMap<>();

	private Set<Long>         createdPhenotypeDataIds = new HashSet<>();
	private Set<Long>         createdTreatmentIds     = new HashSet<>();
	private Set<Long>         createdAccessionIds     = new HashSet<>();
	private Map<String, Long> phenotypeDatas          = new HashMap<>();

	private Dataset dataset;

	private MetadataImporter  metadataImporter;
	private PhenotypeImporter phenotypeImporter;

	public static void main(String[] args)
	{
		try
		{
			new PhenotypeDataImporter()
					.run(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port) throws Exception
	{
		// Import the meta-data first. Get the created dataset
		metadataImporter = new MetadataImporter(ExperimentType.trials);
		metadataImporter.run(input, server, database, username, password, port);
		dataset = metadataImporter.getDataset();

		// Then import the phenotypes
		phenotypeImporter = new PhenotypeImporter();
		phenotypeImporter.run(input, server, database, username, password, port);

		// Then run the rest of this importer
		super.run(input, server, database, username, password, port);
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelPhenotypeDataReader();
	}

	@Override
	protected void prepareReader(IDataReader reader)
	{
		super.prepareReader(reader);

		try
		{
			DatabaseObjectStreamer<Accession> accessionStreamer = new DatabaseObjectQuery<Accession>("SELECT * FROM `germinatebase`", null)
					.getStreamer(Accession.MinimalParser.Inst.get(), null, true);

			Accession accession;
			while ((accession = accessionStreamer.next()) != null)
				accessions.put(accession.getGeneralIdentifier(), accession);

			DatabaseObjectStreamer<Treatment> treatmentStreamer = new DatabaseObjectQuery<Treatment>("SELECT * FROM `treatments`", null)
					.getStreamer(Treatment.Parser.Inst.get(), null, true);

			Treatment treatment;
			while ((treatment = treatmentStreamer.next()) != null)
				treatments.put(treatment.getName(), treatment.getId());

			DatabaseObjectStreamer<Phenotype> phenotypeStreamer = new DatabaseObjectQuery<Phenotype>("SELECT * FROM `phenotypes`", null)
					.getStreamer(Phenotype.Parser.Inst.get(), null, true);

			Phenotype phenotype;
			while ((phenotype = phenotypeStreamer.next()) != null)
				phenotypes.put(phenotype.getName(), phenotype.getId());

			DefaultStreamer phenotypeDataStreamer = new DefaultQuery("SELECT * FROM `phenotypedata` WHERE dataset_id = ?", null)
					.setLong(dataset.getId())
					.getStreamer();

			DatabaseResult phenotypeData;
			while ((phenotypeData = phenotypeDataStreamer.next()) != null)
				phenotypeDatas.put(phenotypeData.getString("germinatebase_id") + "-" + phenotypeData.getString("phenotype_id") + "-" + phenotypeData.getString("phenotype_value"), phenotypeData.getLong("id"));
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
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
			cache.add(entry);

			// If cache full, write
			if (cache.size() >= 10000)
				writeCache();
		}
	}

	@Override
	protected void flush() throws DatabaseException
	{
		writeCache();
	}

	private void writeCache() throws DatabaseException
	{
		if (!CollectionUtils.isEmpty(cache))
		{
			boolean previous = databaseConnection.getAutoCommit();
			databaseConnection.setAutoCommit(false);

			write();

			databaseConnection.setAutoCommit(previous);
		}


		cache.clear();
	}

	private void write() throws DatabaseException
	{
		checkAccessions();
		checkPhenotypes();
		writeCachedTreatments();
		writeCachedAccessions();
		writeCachedPhenotypeData();
	}

	private void checkAccessions() throws DatabaseException
	{
		for (PhenotypeData entry : cache)
		{
			Accession cached = accessions.get(entry.getAccession().getGeneralIdentifier());

			if (cached == null)
				throw new DatabaseException("Accession not found: " + entry.getAccession().getGeneralIdentifier() + " Please make sure it is imported before trying to import the data.");
			else
				entry.getAccession().setId(cached.getId());
		}
	}

	private void checkPhenotypes() throws DatabaseException
	{
		for (PhenotypeData entry : cache)
		{
			Long cached = phenotypes.get(entry.getPhenotype().getName());

			if (cached == null)
				throw new DatabaseException("Phenotype not found: " + entry.getPhenotype().getName() + " Please make sure it is imported before trying to import the data.");
			else
				entry.getPhenotype().setId(cached);
		}
	}

	private void writeCachedTreatments() throws DatabaseException
	{
		for (PhenotypeData entry : cache)
		{
			String treatment = entry.getAccession().getExtra(ExcelPhenotypeDataReader.EXTRA_TREATMENT);

			if (!StringUtils.isEmpty(treatment))
			{
				Long id = treatments.get(treatment);

				if (id != null)
				{
					entry.setTreatment(new Treatment(id));
				}
				else
				{
					Treatment t = new Treatment()
							.setName(treatment)
							.setDescription(treatment);
					Treatment.Writer.Inst.get().write(databaseConnection, t);
					createdTreatmentIds.add(t.getId());
					entry.setTreatment(t);
					treatments.put(t.getName(), t.getId());
				}
			}
		}
	}

	private void writeCachedPhenotypeData() throws DatabaseException
	{
		DatabaseStatement insert = PhenotypeData.Writer.Inst.get().getBatchedStatement(databaseConnection);

		// Import the markers if they don't exist yet
		for (PhenotypeData entry : cache)
		{
			Long cached = phenotypeDatas.get(entry.getAccession().getId() + "-" + entry.getPhenotype().getId() + "-" + entry.getValue());
			entry.setDataset(dataset);

			// If there isn't a value for this combination
			if (cached == null)
				PhenotypeData.Writer.Inst.get().writeBatched(insert, entry);
		}

		List<Long> ids = insert.executeBatch();
		createdPhenotypeDataIds.addAll(ids);
	}

	private void writeCachedAccessions() throws DatabaseException
	{
		DatabaseStatement insert = Accession.Writer.Inst.get().getBatchedStatement(databaseConnection);

		List<PhenotypeData> toAddId = new ArrayList<>();
		// Import the phenotype data items if they don't exist yet
		for (PhenotypeData entry : cache)
		{
			// Is this a rep?
			String rep = entry.getAccession().getExtra(ExcelPhenotypeDataReader.EXTRA_REP);

			if (!StringUtils.isEmpty(rep))
			{
				// Give the rep a unique name (accession + datasetId + repNumber)
				rep = entry.getAccession().getGeneralIdentifier() + "-" + dataset.getId() + "-" + rep;

				Accession parent = accessions.get(entry.getAccession().getGeneralIdentifier());
				Accession child = accessions.get(rep);

				if (child == null)
				{
					child = new Accession()
							.setGeneralIdentifier(rep)
							.setName(rep)
							.setNumber(rep)
							.setEntityType(EntityType.PLANT_PLOT)
							.setEntityParentId(parent.getId());

					Accession.Writer.Inst.get().writeBatched(insert, child);
					accessions.put(child.getGeneralIdentifier(), child);
					toAddId.add(entry);
				}

				entry.setAccession(child);
			}
		}

		List<Long> ids = insert.executeBatch();
		assert ids.size() == toAddId.size();
		for (int i = 0; i < ids.size(); i++)
		{
			Accession accession = toAddId.get(i).getAccession();
			accession.setId(ids.get(i));
		}
		createdAccessionIds.addAll(ids);
	}
}
