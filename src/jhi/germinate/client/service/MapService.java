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

package jhi.germinate.client.service;

import com.google.gwt.core.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link MapService} is a {@link RemoteService} providing methods to retrieve marker and map data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("map")
public interface MapService extends RemoteService
{
	String[] COLUMNS_MAP_SORTABLE = {Map.ID, Map.NAME, Map.DESCRIPTION, Map.CREATED_ON, Map.UPDATED_ON};

	final class Inst
	{
		private static final class InstanceHolder
		{

			private static final MapServiceAsync INSTANCE = GWT.create(MapService.class);
		}

		public static MapServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns the {@link Map} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param mapId      The id of the {@link Map}
	 * @return The {@link Map} with the given id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException Thrown if the query fails on the server
	 */
	ServerResult<Map> getById(RequestProperties properties, Long mapId) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the available maps for the given user
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The available maps for the given user
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	PaginatedServerResult<List<Map>> get(RequestProperties properties, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns the available maps for the given datasets
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The available maps for the given user
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Map>> getForDatasets(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException;

	/**
	 * Creates a map file for the given regions in the given format and returns the relative path to the file
	 *
	 * @param properties The {@link RequestProperties}
	 * @param mapId      The id of the map to export
	 * @param format     The map format
	 * @param options    The {@link MapExportOptions}
	 * @return The relative path to the created file
	 * @throws IOException             Thrown if the file creation fails
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server throws IOException Thrown if file interaction fails
	 */
	ServerResult<String> getInFormat(RequestProperties properties, Long mapId, MapFormat format, MapExportOptions options) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Retrieves the list of chromosomes in the given map
	 *
	 * @param properties The {@link RequestProperties}
	 * @param mapId      The map id
	 * @return The list of chromosomes
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getChromosomesForMap(RequestProperties properties, Long mapId) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a paginated list of {@link MapDefinition}s for the given {@link Marker} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param markerId   The {@link Marker} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link MapDefinition}s for the given {@link Marker} id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	PaginatedServerResult<List<MapDefinition>> getDataForMarker(RequestProperties properties, Long markerId, Pagination pagination) throws InvalidSessionException, DatabaseException;
}
