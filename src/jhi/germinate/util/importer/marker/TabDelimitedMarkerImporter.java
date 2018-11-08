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
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
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
	protected Map                     map;
	protected MarkerType              markerType;
	protected MapFeatureType          mapFeatureType;
	protected List<MapDefinition>     cache                   = new ArrayList<>();
	protected Set<Long>               createdMarkerIds        = new HashSet<>();
	protected Set<Long>               createdMapDefinitionIds = new HashSet<>();
	protected Set<Long>               createdDatasetMemberIds = new HashSet<>();

	protected java.util.Map<String, Long> markers        = new HashMap<>();
	protected List<Long>                  datasetMembers = new ArrayList<>();
	protected List<Long>                  mapdefinitions = new ArrayList<>();

	private Dataset dataset;

	private String mapName;
	private String markerTypeName;

	public TabDelimitedMarkerImporter(String mapName, String markerTypeName)
	{
		this.mapName = mapName;
		this.markerTypeName = markerTypeName;
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port)
	{
		preImport(input, server, database, username, password, port);

		super.run(input, server, database, username, password, port);
	}

	protected void preImport(File input, String server, String database, String username, String password, String port)
	{
		mapImporter = new TabDelimitedMapImporter(mapName);
		mapImporter.run(input, server, database, username, password, port);
		map = mapImporter.getMap();

		markerTypeImporter = new TabDelimitedMarkerTypeImporter(markerTypeName);
		markerTypeImporter.run(input, server, database, username, password, port);
		markerType = markerTypeImporter.getMarkerType();
		mapFeatureType = markerTypeImporter.getMapFeatureType();
	}

	@Override
	protected IDataReader getReader()
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
	protected void prepareReader(IDataReader reader)
	{
		super.prepareReader(reader);

		try
		{
			// Get all the markers of this type in one go
			DatabaseObjectStreamer<Marker> streamer = new DatabaseObjectQuery<Marker>("SELECT * FROM `markers` WHERE `markertype_id` = ?", null)
					.setDatabase(databaseConnection)
					.setLong(markerType.getId())
					.getStreamer(Marker.Parser.Inst.get(), null, true);

			// Remember them to be able to look them up later
			Marker marker;
			while ((marker = streamer.next()) != null)
				markers.put(marker.getName(), marker.getId());

			// Then get all the dataset members (markers)
			datasetMembers = new ValueQuery("SELECT `markers`.`id` FROM `datasetmembers` LEFT JOIN `markers` ON (`datasetmembers`.`datasetmembertype_id` = 1 AND `markers`.`id` = `datasetmembers`.`foreign_id`) WHERE `dataset_id` = ?")
					.setDatabase(databaseConnection)
					.setLong(dataset.getId())
					.run("id")
					.getLongs()
					.getServerResult();

			if (datasetMembers == null)
				datasetMembers = new ArrayList<>();

			// And all the mapdefinitions for this map
			mapdefinitions = new ValueQuery("SELECT `markers`.`id` FROM `mapdefinitions` LEFT JOIN `markers` ON (`mapdefinitions`.`marker_id` = `markers`.`id`) WHERE `map_id` = ?")
					.setDatabase(databaseConnection)
					.setLong(map.getId())
					.run("id")
					.getLongs()
					.getServerResult();

			if (mapdefinitions == null)
				mapdefinitions = new ArrayList<>();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void write(MapDefinition entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getMarker().getName()))
		{
			// Add to the cache
			cache.add(entry);

			// If cache full, write
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
				writeDatasetMembers();

			databaseConnection.setAutoCommit(previous);
		}


		cache.clear();
	}

	private void writeDatasetMembers() throws DatabaseException
	{
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO datasetmembers (dataset_id, foreign_id, datasetmembertype_id) VALUES (?, ?, 1)");

		// Import the dataset members if they don't exist yet
		for (MapDefinition entry : cache)
		{
			if (map == null || StringUtils.isEmpty(entry.getChromosome()) || entry.getDefinitionStart() == null)
				continue;

			if (datasetMembers.contains(entry.getMarker().getId()))
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
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO mapdefinitions (" + MapDefinition.MAPFEATURETYPE_ID + ", " + MapDefinition.MARKER_ID + ", " + MapDefinition.MAP_ID + ", " + MapDefinition.DEFINITION_START + ", " + MapDefinition.DEFINITION_END + ", " + MapDefinition.CHROMOSOME + ") VALUES (?, ?, ?, ?, ?, ?)");

		// Import the map definitions if they don't exist yet
		for (MapDefinition entry : cache)
		{
			if (map == null || StringUtils.isEmpty(entry.getChromosome()) || entry.getDefinitionStart() == null)
				continue;

			if (mapdefinitions.contains(entry.getMarker().getId()))
				continue;

			entry.setType(mapFeatureType)
				 .setMap(map);

			MapDefinition.Writer.Inst.get().writeBatched(insert, entry);
		}

		List<Long> ids = insert.executeBatch();
		createdMapDefinitionIds.addAll(ids);
	}

	private void writeCacheMarkers() throws DatabaseException
	{
		DatabaseStatement insert = databaseConnection.prepareStatement("INSERT INTO markers (" + Marker.MARKER_NAME + ", " + Marker.MARKERTYPE_ID + ", " + Marker.CREATED_ON + ", " + Marker.UPDATED_ON + ") VALUES (?, ?, ?, ?)");

		// Import the markers if they don't exist yet
		for (MapDefinition entry : cache)
		{
			Long marker = markers.get(entry.getMarker().getName());

			entry.getMarker().setType(markerType);

			if (marker == null)
				Marker.Writer.Inst.get().writeBatched(insert, entry.getMarker());
			else
				entry.getMarker().setId(marker);

		}

		List<Long> ids = insert.executeBatch();
		int counter = 0;
		for (MapDefinition entry : cache)
		{
			if (entry.getMarker().getId() == null)
			{
				entry.getMarker().setId(ids.get(counter++));
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
