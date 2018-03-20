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
import java.sql.*;
import java.util.Date;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.common.*;
import jhi.germinate.util.importer.phenotype.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link CompoundDataImporter} uses an {@link IDataReader} to read and parse {@link PhenotypeData} objects and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class CompoundDataImporter extends DataImporter<CompoundData>
{
	private static Dataset dataset;
	private HashMap<String, Accession> accessionCache = new HashMap<>();
	private HashMap<String, Compound>  compoundCache  = new HashMap<>();
	private Set<Long> createdCompoundDataIds = new HashSet<>();
	private Set<Long> createdAccessionIds    = new HashSet<>();
	private MetadataImporter metadataImporter;
	private CompoundImporter compoundImporter;

	public static void main(String[] args)
	{
		new CompoundDataImporter()
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
		compoundImporter = new CompoundImporter();
		compoundImporter.run(input, server, database, username, password, port, ExcelCompoundReader.class.getCanonicalName());

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
		compoundImporter.deleteInsertedItems();
		deleteItems(createdCompoundDataIds, "compounddata");
		deleteItems(createdAccessionIds, "germinatebase");
	}

	@Override
	protected void write(CompoundData entry) throws DatabaseException
	{
		if (entry.getValue() != null)
		{
			// First, make sure the actual accession exists
			Accession accession = getAccession(entry.getAccession(), false);

			entry.setAccession(accession);

			// Get the phenotype for this column
			getCompound(entry);

			// Insert the cell value
			createCompoundData(entry);
		}
	}

	/**
	 * Import the {@link PhenotypeData} object into the database.
	 *
	 * @param entry The {@link PhenotypeData} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createCompoundData(CompoundData entry) throws DatabaseException
	{
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM compounddata WHERE compound_id = ? AND germinatebase_id = ? AND dataset_id <=> ? AND compound_value = ? AND recording_date <=> ?");
		int i = 1;
		stmt.setLong(i++, entry.getCompound().getId());
		stmt.setLong(i++, entry.getAccession().getId());
		stmt.setLong(i++, dataset.getId());
		stmt.setDouble(i++, entry.getValue());

		if (entry.getRecordingDate() != null)
			stmt.setDate(i++, new Date(entry.getRecordingDate()));
		else
			stmt.setNull(i++, Types.TIMESTAMP);

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			entry.setDataset(dataset);

			CompoundData.Writer.Inst.get().write(databaseConnection, entry);
			createdCompoundDataIds.add(entry.getId());
		}
	}

	/**
	 * Get the {@link Accession} object for this {@link CompoundData}
	 *
	 * @param entry The {@link CompoundData} containing the {@link Accession} object to import.
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
	 * Get the {@link Phenotype} object for this {@link CompoundData}
	 *
	 * @param entry The {@link CompoundData} containing the {@link Compound} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void getCompound(CompoundData entry) throws DatabaseException
	{
		String name = entry.getCompound().getName();

		if (StringUtils.isEmpty(name))
			return;

		Compound cached = compoundCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM compounds WHERE name = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
				cached = Compound.Parser.Inst.get().parse(rs, null, true);
			else
				throw new DatabaseException("Compound not found: " + name + " Please make sure to include all compounds in the compounds tab.");
		}

		entry.setCompound(cached);

		compoundCache.put(name, cached);
	}
}
