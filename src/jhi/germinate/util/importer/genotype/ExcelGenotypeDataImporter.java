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

package jhi.germinate.util.importer.genotype;

import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.*;

import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.marker.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelGenotypeDataImporter} uses an {@link IDataReader} to read and parse {@link String[]} objects and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class ExcelGenotypeDataImporter extends TabDelimitedGenotypeDataImporter
{
	private BufferedWriter bw;
	private File           tempFile = null;

	public static void main(String[] args)
	{
		new ExcelGenotypeDataImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port)
	{
		try
		{
			tempFile = File.createTempFile("germinate_genotype_", ".txt");

			try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile)))
			{
				this.bw = bw;
				// Then run the rest of this importer
				super.run(input, server, database, username, password, port);
			}

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
	protected void beforeRun(File input, String server, String database, String username, String password, String port)
	{
		// Import the meta-data first. Get the created dataset
		metadataImporter = new GenotypeMetadataImporter(ExperimentType.genotype);
		metadataImporter.run(input, server, database, username, password, port);
		hdf5File = metadataImporter.getHdf5File();
		dataset = metadataImporter.getDataset();

		markerImporter = new ExcelMarkerImporter(null, null);
		markerImporter.setDataset(dataset);
		markerImporter.run(input, server, database, username, password, port);
	}

	@Override
	protected void writeHdf5(File input, File hdf5File)
	{
		try
		{
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		FJTabbedToHdf5Converter converter = new FJTabbedToHdf5Converter(tempFile, hdf5File);
		converter.convertToHdf5();
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelGenotypeDataReader();
	}

	@Override
	protected void write(String[] entry) throws DatabaseException
	{
		if (entry != null && entry.length > 0)
		{
			if (firstRow)
			{
				firstRow = false;

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
}
