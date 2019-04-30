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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMarkerTypeImporter} uses an {@link IDataReader} to read and parse attribute data and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class ExcelMarkerTypeImporter extends DataImporter<MarkerType>
{
	private Set<Long> createdMarkertypeIds     = new HashSet<>();
	private Set<Long> createdMapFeatureTypeIds = new HashSet<>();

	private MarkerType     markerType;
	private MapFeatureType mapFeatureType;

	public static void main(String[] args)
	{
		try
		{
			new ExcelMarkerTypeImporter()
					.run(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelMarkerTypeReader();
	}

	@Override
	public void deleteInsertedItems()
	{
		// Then delete our stuff
		deleteItems(createdMarkertypeIds, "markertypes");
		deleteItems(createdMapFeatureTypeIds, "mapfeaturetypes");
	}

	@Override
	protected void write(MarkerType entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getDescription()))
		{
			// Write the map first
			createOrGetMarkerType(entry);
			createOrGetMapFeatureType(entry);
		}
	}

	protected void createOrGetMarkerType(MarkerType entry) throws DatabaseException
	{
		String name = entry.getDescription();

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM markertypes WHERE description = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			markerType = MarkerType.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			markerType = entry;

			MarkerType.Writer.Inst.get().write(databaseConnection, markerType);
			createdMarkertypeIds.add(markerType.getId());
		}
	}

	protected void createOrGetMapFeatureType(MarkerType entry) throws DatabaseException
	{
		String name = entry.getDescription();

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM mapfeaturetypes WHERE description = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			mapFeatureType = MapFeatureType.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			mapFeatureType = new MapFeatureType()
					.setDescription(entry.getDescription());

			MapFeatureType.Writer.Inst.get().write(databaseConnection, mapFeatureType);
			createdMarkertypeIds.add(mapFeatureType.getId());
		}
	}

	public MapFeatureType getMapFeatureType()
	{
		return mapFeatureType;
	}

	public MarkerType getMarkerType()
	{
		return markerType;
	}
}
