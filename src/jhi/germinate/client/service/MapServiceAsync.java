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

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public interface MapServiceAsync
{
	void getById(RequestProperties properties, Long mapId, AsyncCallback<ServerResult<Map>> async);

	/**
	 * Returns the available maps for the given user
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The available maps for the given user
	 */
	Request get(RequestProperties properties, ExperimentType experimentType, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Map>>> async);

	/**
	 * Creates a map file for the given regions in the given format and returns the relative path to the file
	 *
	 * @param properties The {@link RequestProperties}
	 * @param mapId      The id of the map to export
	 * @param format     The map format
	 * @param options    The {@link MapExportOptions}
	 */
	void getInFormat(RequestProperties properties, Long mapId, MapFormat format, MapExportOptions options, AsyncCallback<ServerResult<String>> async);

	/**
	 * Retrieves the list of chromosomes in the given map
	 *
	 * @param properties The {@link RequestProperties}
	 * @param mapId      The map id
	 */
	void getChromosomesForMap(RequestProperties properties, Long mapId, AsyncCallback<ServerResult<List<String>>> async);

	/**
	 * Returns a paginated list of {@link MapDefinition}s for the given {@link Marker} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param markerId   The {@link Marker} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getDataForMarker(RequestProperties properties, Long markerId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback);
}
