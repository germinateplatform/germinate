/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.util.importer.compound;

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
 * {@link CompoundDataImporter} uses an {@link IDataReader} to read and parse {@link PhenotypeData} objects and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class CompoundDataImporter extends DataImporter<CompoundData>
{
	private Dataset                dataset;
	protected      List<CompoundData>     cache      = new ArrayList<>();
	private        Map<String, Accession> accessions = new HashMap<>();

	private Set<Long>         createdCompoundDataIds = new HashSet<>();
	private Set<Long>         createdAccessionIds    = new HashSet<>();
	private Map<String, Long> compounds              = new HashMap<>();
	private Map<String, Long> compoundDatas          = new HashMap<>();

	private MetadataImporter metadataImporter;
	private CompoundImporter compoundImporter;

	public static void main(String[] args)
	{
		try
		{
			new CompoundDataImporter()
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
		metadataImporter = new MetadataImporter(ExperimentType.compound);
		metadataImporter.run(input, server, database, username, password, port);
		dataset = metadataImporter.getDataset();

		// Then import the phenotypes
		compoundImporter = new CompoundImporter();
		compoundImporter.run(input, server, database, username, password, port);

		// Then run the rest of this importer
		super.run(input, server, database, username, password, port);
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelCompoundDataReader();
	}

	@Override
	public void deleteInsertedItems()
	{
		metadataImporter.deleteInsertedItems();
		compoundImporter.deleteInsertedItems();
		deleteItems(createdCompoundDataIds, "compounddata");
		deleteItems(createdAccessionIds, "germinatebase");
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

			DatabaseObjectStreamer<Compound> compoundStreamer = new DatabaseObjectQuery<Compound>("SELECT * FROM `compounds`", null)
					.getStreamer(Compound.Parser.Inst.get(), null, true);

			Compound compound;
			while ((compound = compoundStreamer.next()) != null)
				compounds.put(compound.getName(), compound.getId());

			DefaultStreamer compoundDataStreamer = new DefaultQuery("SELECT * FROM `compounddata` WHERE dataset_id = ?", null)
					.setLong(dataset.getId())
					.getStreamer();

			DatabaseResult compoundData;
			while ((compoundData = compoundDataStreamer.next()) != null)
				compoundDatas.put(compoundData.getString("germinatebase_id") + "-" + compoundData.getString("compound_id"), compoundData.getLong("id"));
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void write(CompoundData entry) throws DatabaseException
	{
		if (entry.getValue() != null)
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
		checkCompounds();
		writeCachedCompoundData();
	}

	private void checkAccessions() throws DatabaseException
	{
		for (CompoundData entry : cache)
		{
			Accession cached = accessions.get(entry.getAccession().getGeneralIdentifier());

			if (cached == null)
				throw new DatabaseException("Accession not found: " + entry.getAccession().getGeneralIdentifier() + " Please make sure it is imported before trying to import the data.");
			else
				entry.getAccession().setId(cached.getId());
		}
	}

	private void checkCompounds() throws DatabaseException
	{
		for (CompoundData entry : cache)
		{
			Long cached = compounds.get(entry.getCompound().getName());

			if (cached == null)
				throw new DatabaseException("Compound not found: " + entry.getCompound().getName() + " Please make sure it is imported before trying to import the data.");
			else
				entry.getCompound().setId(cached);
		}
	}

	private void writeCachedCompoundData() throws DatabaseException
	{
		DatabaseStatement insert = CompoundData.Writer.Inst.get().getBatchedStatement(databaseConnection);

		// Import the markers if they don't exist yet
		for (CompoundData entry : cache)
		{
			Long cached = compoundDatas.get(entry.getAccession().getId() + "-" + entry.getCompound().getId());
			entry.setDataset(dataset);

			// If there isn't a value for this combination
			if (cached == null)
				CompoundData.Writer.Inst.get().writeBatched(insert, entry);
		}

		List<Long> ids = insert.executeBatch();
		createdCompoundDataIds.addAll(ids);
	}
}
