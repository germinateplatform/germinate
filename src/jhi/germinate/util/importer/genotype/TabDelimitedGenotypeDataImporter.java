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

package jhi.germinate.util.importer.genotype;

import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.Map;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.marker.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link TabDelimitedGenotypeDataImporter} uses an {@link IDataReader} to read and parse {@link String[]} objects and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class TabDelimitedGenotypeDataImporter extends DataImporter<String[]>
{
	private Set<Long> createdDatasetMemberIds = new HashSet<>();

	protected GenotypeMetadataImporter   metadataImporter;
	protected TabDelimitedMarkerImporter markerImporter;

	protected File    hdf5File;
	protected Dataset dataset;
	protected boolean firstRow = true;

	protected Map<String, Accession> cachedAccessions     = new HashMap<>();
	protected List<Long>             cachedDatasetMembers = new ArrayList<>();

	public static void main(String[] args)
	{
		new TabDelimitedGenotypeDataImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port)
	{
		beforeRun(input, server, database, username, password, port);

		try
		{
			// Then run the rest of this importer
			super.run(input, server, database, username, password, port);
			writeHdf5(input, hdf5File);
			System.out.println("HDF5 file created. Move this file to your instance's genotype folder: " + hdf5File.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			deleteInsertedItems();
		}
	}

	protected void beforeRun(File input, String server, String database, String username, String password, String port)
	{
		String datasetName = null;
		String markerType = null;
		String map = null;
		try (BufferedReader br = new BufferedReader(new FileReader(input)))
		{
			String line;

			while ((line = br.readLine()) != null && line.startsWith("#"))
			{
				String[] parts = line.split("=", -1);

				if (parts.length == 2)
				{
					parts[0] = parts[0].replace("#", "").trim();
					parts[1] = parts[1].trim();

					switch (parts[0])
					{
						case "dataset":
							datasetName = parts[1];
							break;
						case "map":
							map = parts[1];
							break;
						case "markerType":
							markerType = parts[1];
							break;
					}
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Import the meta-data first. Get the created dataset
		metadataImporter = new TabDelimitedMetadataImporter(datasetName, ExperimentType.genotype);
		metadataImporter.run(input, server, database, username, password, port);
		hdf5File = metadataImporter.getHdf5File();
		dataset = metadataImporter.getDataset();

		markerImporter = new TabDelimitedMarkerImporter(map, markerType);
		markerImporter.setDataset(dataset);
		markerImporter.run(input, server, database, username, password, port);
	}

	protected void writeHdf5(File input, File hdf5File)
	{
		FJTabbedToHdf5Converter converter = new FJTabbedToHdf5Converter(input, hdf5File);
		converter.setSkipLines(2);
		converter.convertToHdf5();
	}

	@Override
	protected void prepareReader(IDataReader reader)
	{
		super.prepareReader(reader);

		try
		{
			// Get all the accessions in one go
			DatabaseObjectStreamer<Accession> streamer = new DatabaseObjectQuery<Accession>("SELECT * FROM `germinatebase`", null)
					.setDatabase(databaseConnection)
					.getStreamer(Accession.MinimalParser.Inst.get(), null, true);

			// Add them to the cache for faster lookup
			Accession accession;
			while ((accession = streamer.next()) != null)
				cachedAccessions.put(accession.getGeneralIdentifier(), accession);

			// Then get all the dataset members (accessions) for this dataset
			cachedDatasetMembers = new ValueQuery("SELECT `germinatebase`.`id` FROM `datasetmembers` LEFT JOIN `germinatebase` ON (`datasetmembers`.`datasetmembertype_id` = 2 AND `germinatebase`.`id` = `datasetmembers`.`foreign_id`) WHERE `dataset_id` = ?")
					.setDatabase(databaseConnection)
					.setLong(dataset.getId())
					.run("id")
					.getLongs()
					.getServerResult();

			if (cachedDatasetMembers == null)
				cachedDatasetMembers = new ArrayList<>();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected IDataReader getReader()
	{
		return new TabDelimitedGenotypeDataReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		metadataImporter.deleteInsertedItems();
		markerImporter.deleteInsertedItems();

		deleteItems(createdDatasetMemberIds, "datasetmembers");
	}

	@Override
	protected void write(String[] entry) throws DatabaseException
	{
		if (entry != null && entry.length > 0)
		{
			if (firstRow)
			{
				firstRow = false;
				// Ignore the first row, the markers have already been checked
			}
			else
			{
				// Check that the accession exists
				Accession accession = checkAccession(entry);

				checkDatasetMember(accession);
			}
		}
	}

	protected void checkDatasetMember(Accession entry) throws DatabaseException
	{
		if (cachedDatasetMembers.contains(entry.getId()))
			return;

		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO datasetmembers (dataset_id, foreign_id, datasetmembertype_id) VALUES (?, ?, 2)");

		int i = 1;
		insert.setLong(i++, dataset.getId());
		insert.setLong(i++, entry.getId());

		List<Long> ids = insert.execute();
		createdDatasetMemberIds.addAll(ids);
	}

	/**
	 * Checks if the {@link Accession} object exists.
	 *
	 * @param entry The {@link String[]} containing the {@link Accession} information.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	protected Accession checkAccession(String[] entry) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry[0]))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		Accession acc = cachedAccessions.get(entry[0]);

		if (acc != null)
		{
			return acc;
		}
		else
		{
			throw new DatabaseException("Accession not found: " + entry[0]);
		}
	}
}
