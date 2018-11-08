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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link CompoundImporter} uses an {@link IDataReader} to read and parse {@link Compound}s and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class CompoundImporter extends DataImporter<Compound>
{
	private static Set<Long> createdCompoundIds = new HashSet<>();
	private static Set<Long> createdUnitIds      = new HashSet<>();

	public static void main(String[] args)
	{
		new CompoundImporter()
				.run(args);
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelCompoundReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdCompoundIds, "compounds");
		deleteItems(createdUnitIds, "units");
	}

	@Override
	protected void write(Compound entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getName()))
		{
			// Write the phenotype itself
			createCompound(entry);
		}
	}

	/**
	 * Imports the {@link Compound} into the database if it doesn't already exist
	 *
	 * @param entry The {@link Compound} to import
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createCompound(Compound entry) throws DatabaseException
	{
		String name = entry.getName();

		if (StringUtils.isEmpty(name))
			return;

		createOrGetUnit(entry);

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM compounds WHERE name = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			Compound.Writer.Inst.get().write(databaseConnection, entry);
			createdCompoundIds.add(entry.getId());
		}
	}

	/**
	 * Imports the {@link Unit} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Compound} object containing the {@link Unit} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetUnit(Compound entry) throws DatabaseException
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
