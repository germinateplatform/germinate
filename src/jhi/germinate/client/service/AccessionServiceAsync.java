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
import jhi.germinate.shared.search.*;

/**
 * Async version of {@link AccessionService}.
 *
 * @author Sebastian Raubach
 */
public interface AccessionServiceAsync
{
	/**
	 * Returns a paginated list of {@link Accession}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Accession>>> callback);

	/**
	 * Returns the ids of the {@link Accession}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsForFilter(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns the {@link Accession}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the accessions
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getByIds(RequestProperties properties, Pagination pagination, List<String> ids, AsyncCallback<ServerResult<List<Accession>>> callback);

	/**
	 * Exports all the data associated with {@link Accession}s to a flat file. Returns the name of the result file.
	 *
	 * @param properties        The {@link RequestProperties}
	 * @param idColumn          The column to use as the identifier. Accessions will also be sorted according to this column.
	 * @param groupId           The group id (can be <code>null</code> or <code>null</code> to denote "no group")
	 * @param includeAttributes Should the attribute data be included?
	 * @param callback          The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, String idColumn, Long groupId, boolean includeAttributes, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports all the data associated with {@link Accession}s to a flat file. Returns the name of the result file.
	 *
	 * @param properties        The {@link RequestProperties}
	 * @param idColumn          The column to use as the identifier. Accessions will also be sorted according to this column.
	 * @param accessionIds      The ids of the {@link Accession}s that should be exported.
	 * @param includeAttributes Should the attribute data be included?
	 * @param callback          The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, String idColumn, Set<String> accessionIds, boolean includeAttributes, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports all the data associated with {@link Accession}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a paginated list containing the {@link Accession}s for the given mega environment.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The {@link MegaEnvironment} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForMegaEnv(RequestProperties properties, Long megaEnvId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Accession>>> callback);

	/**
	 * Returns the ids of the {@link Accession}s that are part of the {@link MegaEnvironment} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The id of the {@link MegaEnvironment}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsForMegaEnv(RequestProperties properties, Long megaEnvId, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Retrieves the path to the file containing the accession data for the given ids
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the accessions
	 * @param callback   The {@link AsyncCallback}
	 */
	void exportForIds(RequestProperties properties, List<String> ids, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a paginated list of {@link Accession}s for the group preview (groups created from external tools).
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filename   The name of the file containing the indentifiers of the {@link Accession}s.
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForGroupPreview(RequestProperties properties, Pagination pagination, String filename, AsyncCallback<PaginatedServerResult<List<Accession>>> callback);

	/**
	 * Removes {@link Accession}s based on their id from the group preview.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the {@link Accession}s to remove
	 * @param filename   The name of the file containing the indentifiers of the {@link Accession}s.
	 * @param callback   The {@link AsyncCallback}
	 */
	void removeFromGroupPreview(RequestProperties properties, List<Long> ids, String filename, AsyncCallback<Void> callback);

	/**
	 * Removes all {@link Accession}s from the group preview.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filename   The
	 * @param callback   The {@link AsyncCallback}
	 */
	void clearGroupPreview(RequestProperties properties, String filename, AsyncCallback<Void> callback);

	/**
	 * Returns a paginated list of {@link Accession}s based on their distance to the given location.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param latitude   The latitude of the requested location
	 * @param longitude  The longitude of the requested location
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getByDistance(RequestProperties properties, Double latitude, Double longitude, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Accession>>> callback);

	/**
	 * Returns a paginated list of {@link Accession}s that are located in the provided polygon.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param polygon    The list of {@link LatLngPoint}s defining the polygon
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getInPolygon(RequestProperties properties, Pagination pagination, List<LatLngPoint> polygon, AsyncCallback<PaginatedServerResult<List<Accession>>> callback);

	/**
	 * Returns the ids of the {@link Accession}s that are located in the provided polygon.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param polygon    The list of {@link LatLngPoint}s defining the polygon
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsInPolygon(RequestProperties properties, List<LatLngPoint> polygon, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns the {@link Mcpd} object for the {@link Accession} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The {@link Accession} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getMcpd(RequestProperties properties, Long id, AsyncCallback<ServerResult<Mcpd>> callback);
}
