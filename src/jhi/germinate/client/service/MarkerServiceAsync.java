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
 * Async version of {@link MarkerService}.
 *
 * @author Sebastian Raubach
 */
public interface MarkerServiceAsync
{
	/**
	 * Returns a paginated list of {@link Marker}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getMarkerForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Marker>>> callback);

	/**
	 * Returns a paginated list of {@link Marker}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   {@link AsyncCallback}
	 */
	Request getMapDefinitionForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback);

	/**
	 * Returns the ids of the {@link Marker}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   {@link AsyncCallback}
	 */
	void getIdsForFilter(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns the {@link Marker}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param ids        The ids of the accessions
	 * @param callback   {@link AsyncCallback}
	 */
	Request getByIds(RequestProperties properties, Pagination pagination, List<String> ids, AsyncCallback<ServerResult<List<Marker>>> callback);

	/**
	 * Exports the {@link Marker}s with the given names and returns the name of the result file.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param markerNames The {@link Marker} names
	 * @param callback    {@link AsyncCallback}
	 */
	void export(RequestProperties properties, Set<String> markerNames, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports all the data associated with {@link Marker}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);
}
