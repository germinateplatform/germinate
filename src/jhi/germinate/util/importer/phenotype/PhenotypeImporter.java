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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link PhenotypeImporter} uses an {@link IDataReader} to read and parse {@link Phenotype}s and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class PhenotypeImporter extends DataImporter<Phenotype>
{
	private static Set<Long> createdPhenotypeIds = new HashSet<>();
	private static Set<Long> createdUnitIds      = new HashSet<>();

	public static void main(String[] args)
	{
		new PhenotypeImporter()
				.run(args);
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelPhenotypeReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdPhenotypeIds, "phenotypes");
		deleteItems(createdUnitIds, "units");
	}

	@Override
	protected void write(Phenotype entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getName()))
		{
			// Write the phenotype itself
			createPhenotype(entry);
		}
	}

	/**
	 * Imports the {@link Phenotype} into the database if it doesn't already exist
	 *
	 * @param entry The {@link Phenotype} to import
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createPhenotype(Phenotype entry) throws DatabaseException
	{
		String name = entry.getName();

		if (StringUtils.isEmpty(name))
			return;

		createOrGetUnit(entry);

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM phenotypes WHERE name = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			Phenotype.Writer.Inst.get().write(databaseConnection, entry);
			createdPhenotypeIds.add(entry.getId());
		}
	}

	/**
	 * Imports the {@link Unit} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Phenotype} object containing the {@link Unit} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetUnit(Phenotype entry) throws DatabaseException
	{
		if (entry.getUnit() == null || StringUtils.isEmpty(entry.getUnit().getName()))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM units WHERE unit_name = ? AND unit_abbreviation = ?");
		stmt.setString(1, entry.getUnit().getName());
		stmt.setString(2, entry.getUnit().getAbbreviation());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setUnit(Unit.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Unit.Writer.Inst.get().write(databaseConnection, entry.getUnit());
			createdUnitIds.add(entry.getUnit().getId());
		}
	}
}
