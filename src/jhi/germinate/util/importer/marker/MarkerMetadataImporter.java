/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
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
 * {@link MarkerMetadataImporter} uses an {@link IDataReader} to read and parse the {@link Map} object and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class MarkerMetadataImporter extends DataImporter<Map>
{
	private Set<Long> createdMapIds            = new HashSet<>();
	private Set<Long> createdMapFeatureTypeIds = new HashSet<>();

	private Map            map;
	private MapFeatureType mapfeatureType;

	public static void main(String[] args)
	{
		new MarkerMetadataImporter()
				.run(args);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelMarkerMetadataReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdMapIds, "maps");
		deleteItems(createdMapFeatureTypeIds, "mapfeaturetypes");
	}

	@Override
	protected void write(Map entry) throws DatabaseException
	{
		// Create the map itself
		createOrGetMap(entry);

		// Create the map feature type
		createOrGetMapfeatureType(entry);
	}

	/**
	 * Creates the {@link Map} object itself if it doesn't already exist.
	 *
	 * @param entry The {@link Map} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetMap(Map entry) throws DatabaseException
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

	/**
	 * Imports the {@link MapFeatureType} object into the database if it doesn't already exist, otherwise returns the existing object from the
	 * database.
	 *
	 * @param entry The {@link Map} object containing the {@link MapFeatureType} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetMapfeatureType(Map entry) throws DatabaseException
	{
		String name = entry.getExtra(ExcelMarkerMetadataReader.TECHNOLOGY);

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM mapfeaturetypes WHERE description = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			mapfeatureType = MapFeatureType.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			mapfeatureType = new MapFeatureType()
					.setDescription(name);

			MapFeatureType.Writer.Inst.get().write(databaseConnection, mapfeatureType);
			createdMapFeatureTypeIds.add(mapfeatureType.getId());
		}
	}

	/**
	 * Returns the {@link Map} created by this importer
	 *
	 * @return The {@link Map} created by this importer
	 */
	public Map getMap()
	{
		return map;
	}

	/**
	 * Returns the {@link MapFeatureType} created by this importer
	 *
	 * @return The {@link MapFeatureType} created by this importer
	 */
	public MapFeatureType getMapfeatureType()
	{
		return mapfeatureType;
	}
}
