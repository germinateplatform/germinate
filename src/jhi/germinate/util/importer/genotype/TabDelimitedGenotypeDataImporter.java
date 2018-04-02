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
import java.util.stream.*;

import jhi.flapjack.io.cmd.*;
import jhi.germinate.server.database.*;
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

	protected File           tempFile;
	protected File           hdf5File;
	protected Dataset dataset;
	private   BufferedWriter bw;
	private   boolean firstRow = true;

	public static void main(String[] args)
	{
		new TabDelimitedGenotypeDataImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		// Import the meta-data first. Get the created dataset
		metadataImporter = new FilenameMetadataImporter(ExperimentType.genotype);
		metadataImporter.run(input, server, database, username, password, port, null);
		hdf5File = metadataImporter.getHdf5File();
		dataset = metadataImporter.getDataset();

		markerImporter = new TabDelimitedMarkerImporter();
		markerImporter.setDataset(dataset);
		markerImporter.run(input, server, database, username, password, port, TabDelimitedMarkerReader.class.getCanonicalName());

		try
		{
			tempFile = File.createTempFile("germinate_genotype_", ".txt");

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile)))
			{
				this.bw = bw;
				// Then run the rest of this importer
				super.run(input, server, database, username, password, port, readerName);
			}

			FJTabbedToHdf5Converter converter = new FJTabbedToHdf5Converter(tempFile, hdf5File);
			converter.convertToHdf5();

			System.out.println("HDF5 file created. Move this file to your instance's genotype folder: " + hdf5File.getAbsolutePath());

			tempFile.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();

			if (tempFile != null && tempFile.exists())
				tempFile.delete();

			deleteInsertedItems();
		}
	}

	@Override
	protected IDataReader getFallbackReader()
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

				// Check that the marker exists
				checkMarkers(entry);

				writeToTempFile(entry);
			}
			else
			{
				// Check that the accession exists
				Accession accession = checkAccession(entry);

				checkDatasetMember(accession);

				// Insert the cell value
				writeToTempFile(entry);
			}
		}
	}

	private void writeToTempFile(String[] entry)
	{
		try
		{
			bw.write(Arrays.stream(entry)
						   .map(s -> s == null ? "" : s)
						   .collect(Collectors.joining("\t")));
			bw.newLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void checkDatasetMember(Accession entry) throws DatabaseException
	{
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
	private Accession checkAccession(String[] entry) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry[0]))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier = ?");
		int i = 1;
		stmt.setString(i++, entry[0]);

		DatabaseResult rs = stmt.query();

		if (rs.next())
			return Accession.Parser.Inst.get().parse(rs, null, true);
		else
			throw new DatabaseException("Accession not found: " + entry[0]);
	}

	/**
	 * Checks if the {@link Marker} object exists.
	 *
	 * @param entry The {@link String[]} containing the {@link Marker} information.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void checkMarkers(String[] entry) throws DatabaseException
	{
		long counter = 0;
		int position = 1;
		while (position < entry.length)
		{
			int max = Math.min(5000, entry.length - position);
			String markers = IntStream.range(position, position + max)
									  .mapToObj(i -> "?")
									  .collect(Collectors.joining(",", "(", ")"));

//			String markers = Arrays.stream(entry)
//								   .skip(1)
//								   .map(s -> "?")
//								   .collect(Collectors.joining(",", "(", ")"));

			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT COUNT(DISTINCT id) AS count FROM markers WHERE marker_name IN " + markers);

			for (int m = position ; m < position + max; m++)
				stmt.setString(m - position + 1, entry[m]);

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				long count = rs.getLong("count");

				counter += count;
			}

			position += max;
		}

		System.out.println(counter + " =?= " + (entry.length - 1));

		if (counter != entry.length - 1)
			throw new DatabaseException("Check that all markers are imported before running this importer!");
	}
}
