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

package jhi.germinate.util.importer.marker;

import java.io.*;
import java.sql.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link TabDelimitedMarkerImporter} uses an {@link IDataReader} to read and parse attribute data and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class TabDelimitedMarkerImporter extends DataImporter<MapDefinition>
{
	protected ExcelMapImporter        mapImporter;
	protected ExcelMarkerTypeImporter markerTypeImporter;
	protected Map            map;
	protected MarkerType     markerType;
	protected MapFeatureType mapFeatureType;
	private List<MapDefinition> cache = new ArrayList<>();
	private Set<Long> createdMarkerIds        = new HashSet<>();
	private Set<Long> createdMapDefinitionIds = new HashSet<>();
	private Set<Long> createdDatasetMemberIds = new HashSet<>();
	private Dataset dataset;

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		mapImporter = new FilenameMapImporter();
		mapImporter.run(input, server, database, username, password, port, null);
		map = mapImporter.getMap();

		markerTypeImporter = new SNPMarkerTypeImporter();
		markerTypeImporter.run(input, server, database, username, password, port, null);
		markerType = markerTypeImporter.getMarkerType();
		mapFeatureType = markerTypeImporter.getMapFeatureType();

		super.run(input, server, database, username, password, port, readerName);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new TabDelimitedMarkerReader();
	}

	@Override
	public void deleteInsertedItems()
	{
		mapImporter.deleteInsertedItems();
		markerTypeImporter.deleteInsertedItems();

		// Then delete our stuff
		deleteItems(createdMarkerIds, "markers");
		deleteItems(createdMapDefinitionIds, "mapdefinitions");
		deleteItems(createdDatasetMemberIds, "datasetmembers");
	}

	@Override
	protected void write(MapDefinition entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getMarker().getName()))
		{
			// Write the marker type first
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

			if (dataset != null)
			{
				writeDatasetMembers();
			}

			databaseConnection.setAutoCommit(previous);
		}


		cache.clear();
	}

	private void writeDatasetMembers() throws DatabaseException
	{
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO datasetmembers (dataset_id, foreign_id, datasetmembertype_id) VALUES (?, ?, 1)");

		for (MapDefinition entry : cache)
		{
			if (map == null || StringUtils.isEmpty(entry.getChromosome()) || entry.getDefinitionStart() == null)
				continue;

			int i = 1;
			insert.setLong(i++, dataset.getId());
			insert.setLong(i++, entry.getMarker().getId());
			insert.addBatch();
		}

		List<Long> ids = insert.executeBatch();
		createdDatasetMemberIds.addAll(ids);
	}

	private void writeCacheMapDefinitions() throws DatabaseException
	{
		DatabaseStatement select = databaseConnection.prepareStatement("SELECT * FROM mapdefinitions WHERE mapfeaturetype_id = ? AND marker_id = ? AND map_id = ? AND definition_start <=> ? AND definition_end <=> ? AND chromosome <=> ?");
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO mapdefinitions (" + MapDefinition.MAPFEATURETYPE_ID + ", " + MapDefinition.MARKER_ID + ", " + MapDefinition.MAP_ID + ", " + MapDefinition.DEFINITION_START + ", " + MapDefinition.DEFINITION_END + ", " + MapDefinition.CHROMOSOME + ") VALUES (?, ?, ?, ?, ?, ?)");

		for (MapDefinition entry : cache)
		{
			if (map == null || StringUtils.isEmpty(entry.getChromosome()) || entry.getDefinitionStart() == null)
				continue;

			entry.setType(mapFeatureType)
				 .setMap(map);

			int i = 1;
			if (mapFeatureType != null)
				select.setLong(i++, mapFeatureType.getId());
			else
				select.setNull(i++, Types.INTEGER);

			select.setLong(i++, entry.getMarker().getId());
			select.setLong(i++, map.getId());
			select.setDouble(i++, entry.getDefinitionStart());
			select.setDouble(i++, entry.getDefinitionEnd());
			select.setString(i++, entry.getChromosome());

			DatabaseResult rs = select.query();

			if (!rs.next())
			{
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
			entry.getMarker().setType(markerType);

			int i = 1;
			select.setString(i++, entry.getMarker().getName());

			if (markerType != null)
				select.setLong(i++, markerType.getId());
			else
				select.setNull(i++, Types.INTEGER);

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

	public TabDelimitedMarkerImporter setDataset(Dataset dataset)
	{
		this.dataset = dataset;
		return this;
	}
}
