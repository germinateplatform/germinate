/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.marker;

import java.io.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link MarkerImporter} uses an {@link IDataReader} to read and parse attribute data and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class MarkerImporter extends DataImporter<MapDefinition>
{
	private static Map            map;
	private static MapFeatureType mapfeatureType;

	private HashMap<String, MarkerType> markerTypeCache = new HashMap<>();
	private List<MapDefinition>         cache           = new ArrayList<>();

	private Set<Long> createdMarkerIds        = new HashSet<>();
	private Set<Long> createdMarkerTypeIds    = new HashSet<>();
	private Set<Long> createdMapDefinitionIds = new HashSet<>();
	private MarkerMetadataImporter metadataImporter;

	public static void main(String[] args)
	{
		new MarkerImporter()
				.run(args);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		// Import the metadata first
		metadataImporter = new MarkerMetadataImporter();
		metadataImporter.run(input, server, database, username, password, port, ExcelMarkerMetadataReader.class.getCanonicalName());
		// Get the map and mapfeaturetype from the importer
		map = metadataImporter.getMap();
		mapfeatureType = metadataImporter.getMapfeatureType();

		super.run(input, server, database, username, password, port, readerName);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelMarkerReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		// Remember to delete the items of the other importer
		metadataImporter.deleteInsertedItems();

		// Then delete our stuff
		deleteItems(createdMarkerIds, "markers");
		deleteItems(createdMarkerTypeIds, "markertypes");
		deleteItems(createdMapDefinitionIds, "mapdefinitions");
	}

	@Override
	protected void write(MapDefinition entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getMarker().getName()))
		{
			// Write the marker type first
			createOrGetMarkerType(entry);

			cache.add(entry);

			if (cache.size() >= 10000)
				writeCache();
		}
	}

	@Override
	protected void flush() throws DatabaseException
	{
		writeCache();
	}

	private void writeCache() throws DatabaseException
	{
		if (!CollectionUtils.isEmpty(cache))
		{
			boolean previous = databaseConnection.getAutoCommit();
			databaseConnection.setAutoCommit(false);

			writeCacheMarkers();
			writeCacheMapDefinitions();

			databaseConnection.setAutoCommit(previous);
		}

		cache.clear();
	}

	private void writeCacheMapDefinitions() throws DatabaseException
	{
		DatabaseStatement select = databaseConnection.prepareStatement("SELECT * FROM mapdefinitions WHERE mapfeaturetype_id = ? AND marker_id = ? AND map_id = ? AND definition_start <=> ? AND definition_end <=> ? AND chromosome <=> ?");
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO mapdefinitions (" + MapDefinition.MAPFEATURETYPE_ID + ", " + MapDefinition.MARKER_ID + ", " + MapDefinition.MAP_ID + ", " + MapDefinition.DEFINITION_START + ", " + MapDefinition.DEFINITION_END + ", " + MapDefinition.CHROMOSOME + ") VALUES (?, ?, ?, ?, ?, ?)");

		for (MapDefinition entry : cache)
		{
			int i = 1;
			select.setLong(i++, mapfeatureType.getId());
			select.setLong(i++, entry.getMarker().getId());
			select.setLong(i++, map.getId());
			select.setDouble(i++, entry.getDefinitionStart());
			select.setDouble(i++, entry.getDefinitionEnd());
			select.setString(i++, entry.getChromosome());

			DatabaseResult rs = select.query();

			if (!rs.next())
			{
				entry.setType(mapfeatureType)
					 .setMap(map);

				MapDefinition.Writer.Inst.get().writeBatched(insert, entry);
			}
		}

		List<Long> ids = insert.executeBatch();
		createdMapDefinitionIds.addAll(ids);
	}

	private void writeCacheMarkers() throws DatabaseException
	{
		DatabaseStatement select = databaseConnection.prepareStatement("SELECT * FROM markers WHERE marker_name = ? AND markertype_id = ?");
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO markers (" + Marker.MARKER_NAME + ", " + Marker.MARKERTYPE_ID + ", " + Marker.CREATED_ON + ", " + Marker.UPDATED_ON + ") VALUES (?, ?, ?, ?)");

		for (MapDefinition entry : cache)
		{
			int i = 1;
			select.setString(i++, entry.getMarker().getName());
			select.setLong(i++, entry.getMarker().getType().getId());

			DatabaseResult rs = select.query();

			if (rs.next())
				entry.setMarker(Marker.Parser.Inst.get().parse(rs, null, true));
			else
				Marker.Writer.Inst.get().writeBatched(insert, entry.getMarker());
		}

		List<Long> ids = insert.executeBatch();
		int counter = 0;
		for (MapDefinition md : cache)
		{
			if (md.getMarker().getId() == null)
			{
				md.getMarker().setId(ids.get(counter++));
			}
		}
		createdMarkerIds.addAll(ids);
	}

	/**
	 * Imports the {@link MarkerType} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link MapDefinition} object containing the {@link MarkerType} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetMarkerType(MapDefinition entry) throws DatabaseException
	{
		String name = entry.getMarker().getType().getDescription();

		if (StringUtils.isEmpty(name))
			return;

		MarkerType cached = markerTypeCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM markertypes WHERE description = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				cached = MarkerType.Parser.Inst.get().parse(rs, null, true);
			}
			else
			{
				cached = entry.getMarker().getType();

				MarkerType.Writer.Inst.get().write(databaseConnection, cached);
				createdMarkerTypeIds.add(cached.getId());
			}
		}

		entry.getMarker().setType(cached);
		markerTypeCache.put(name, cached);
	}
}
