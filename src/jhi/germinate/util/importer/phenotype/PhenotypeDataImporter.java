/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
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

	private Set<Long> createdPhenotypeDataIds = new HashSet<>();

	private static Dataset dataset;

	private PhenotypeMetadataImporter metadataImporter;
	private PhenotypeImporter         phenotypeImporter;

	public static void main(String[] args)
	{
		new PhenotypeDataImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		// Import the meta-data first. Get the created dataset
		metadataImporter = new PhenotypeMetadataImporter();
		metadataImporter.run(input, server, database, username, password, port, ExcelPhenotypeMetadataReader.class.getCanonicalName());
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
	protected void deleteInsertedItems()
	{
		metadataImporter.deleteInsertedItems();
		phenotypeImporter.deleteInsertedItems();
		deleteItems(createdPhenotypeDataIds, "phenotypedata");
	}

	@Override
	protected void write(PhenotypeData entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getValue()))
		{
			// Get the accession for this row
			getAccession(entry);

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
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM phenotypedata WHERE phenotype_id = ? AND germinatebase_id = ? AND phenotype_value = ? AND recording_date <=> ?");
		int i = 1;
		stmt.setLong(i++, entry.getPhenotype().getId());
		stmt.setLong(i++, entry.getAccession().getId());
		stmt.setString(i++, entry.getValue());

		if (entry.getRecordingDate() != null)
			stmt.setDate(i++, new Date(entry.getRecordingDate()));
		else
			stmt.setNull(i++, Types.TIMESTAMP);

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
	private void getAccession(PhenotypeData entry) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry.getAccession().getGeneralIdentifier()))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		Accession cached = accessionCache.get(entry.getAccession().getGeneralIdentifier());

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier = ?");
			int i = 1;
			stmt.setString(i++, entry.getAccession().getGeneralIdentifier());

			DatabaseResult rs = stmt.query();

			if (rs.next())
				cached = Accession.Parser.Inst.get().parse(rs, null, true);
			else
				throw new DatabaseException("Accession not found: " + entry.getAccession().getGeneralIdentifier());
		}

		entry.setAccession(cached);

		accessionCache.put(entry.getAccession().getGeneralIdentifier(), cached);
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
				throw new DatabaseException("Phenotype not found: " + name);
		}

		entry.setPhenotype(cached);

		phenotypeCache.put(name, cached);
	}
}
