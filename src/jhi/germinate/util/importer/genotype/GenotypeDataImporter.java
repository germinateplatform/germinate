/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
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
import jhi.germinate.util.importer.reader.*;

/**
 * {@link GenotypeDataImporter} uses an {@link IDataReader} to read and parse {@link String[]} objects and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class GenotypeDataImporter extends DataImporter<String[]>
{
	private GenotypeMetadataImporter metadataImporter;

	private File           tempFile;
	private File           hdf5File;
	private BufferedWriter bw;

	private boolean firstRow = true;

	public static void main(String[] args)
	{
		new GenotypeDataImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		// Import the meta-data first. Get the created dataset
		metadataImporter = new GenotypeMetadataImporter();
		metadataImporter.run(input, server, database, username, password, port, ExcelGenotypeMetadataReader.class.getCanonicalName());
		hdf5File = metadataImporter.getHdf5File();

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
		return new ExcelGenotypeDataReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		metadataImporter.deleteInsertedItems();
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
				checkAccession(entry);

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

	/**
	 * Checks if the {@link Accession} object exists.
	 *
	 * @param entry The {@link String[]} containing the {@link Accession} information.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void checkAccession(String[] entry) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry[0]))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier = ?");
		int i = 1;
		stmt.setString(i++, entry[0]);

		DatabaseResult rs = stmt.query();

		if (rs.next())
			Accession.Parser.Inst.get().parse(rs, null, true);
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
		String markers = Arrays.stream(entry)
							   .skip(1)
							   .map(s -> "?")
							   .collect(Collectors.joining(",", "(", ")"));

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT COUNT(DISTINCT id) AS count FROM markers WHERE marker_name IN " + markers);

		for (int m = 1; m < entry.length; m++)
			stmt.setString(m, entry[m]);

		DatabaseResult rs = stmt.query();

		boolean correct = false;
		if (rs.next())
		{
			long count = rs.getLong("count");

			correct = count == entry.length - 1;
		}

		if (!correct)
			throw new DatabaseException("Check that all markers are imported before running this importer!");
	}
}
