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

package jhi.germinate.util.importer.marker;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMapImporter} uses an {@link IDataReader} to read and parse attribute data and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class ExcelMapImporter extends DataImporter<Map>
{
	private Set<Long> createdMapIds = new HashSet<>();

	private Map map;

	public static void main(String[] args)
	{
		new ExcelMapImporter()
				.run(args);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelMapReader();
	}

	@Override
	public void deleteInsertedItems()
	{
		// Then delete our stuff
		deleteItems(createdMapIds, "maps");
	}

	@Override
	protected void write(Map entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getDescription()))
		{
			// Write the map first
			createOrGetMap(entry);
		}
	}

	/**
	 * Imports the {@link MarkerType} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link MapDefinition} object containing the {@link MarkerType} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	protected void createOrGetMap(Map entry) throws DatabaseException
	{
		String name = entry.getDescription();

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM maps WHERE description = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			map = Map.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			map = entry;

			Map.Writer.Inst.get().write(databaseConnection, map);
			createdMapIds.add(map.getId());
		}
	}

	public Map getMap()
	{
		return map;
	}
}
