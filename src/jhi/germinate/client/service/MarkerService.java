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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link MarkerService} is a {@link RemoteService} providing methods to retrieve marker and map data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("marker")
public interface MarkerService extends RemoteService
{
	String[] COLUMNS_MAPDEFINITION_TABLE = {Group.ID, Marker.ID, Marker.MARKER_NAME, Map.ID, Map.DESCRIPTION, MapFeatureType.DESCRIPTION, MapDefinition.CHROMOSOME, MapDefinition.DEFINITION_START, Marker.SYNONYMS, Synonym.SYNONYM};
	String[] COLUMNS_MARKER_TABLE        = {Marker.ID, Marker.MARKER_NAME, MarkerType.DESCRIPTION, Marker.SYNONYMS, Synonym.SYNONYM};

	final class Inst
	{
		private static final class InstanceHolder
		{

			private static final MarkerServiceAsync INSTANCE = GWT.create(MarkerService.class);

		}

		public static MarkerServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a paginated list of {@link Marker}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Marker}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<Marker>> getMarkerForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns a paginated list of {@link Marker}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Marker}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<MapDefinition>> getMapDefinitionForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns the ids of the {@link Marker}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return The ids of the {@link Accession}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Returns the {@link Marker}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param ids        The ids of the accessions
	 * @return The {@link Marker}s with the given ids.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the specified sort column isn't valid
	 */
	ServerResult<List<Marker>> getByIds(RequestProperties properties, Pagination pagination, List<String> ids) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Exports the {@link Marker}s with the given ids and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The {@link Marker} ids
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if an I/O operation fails
	 */
	ServerResult<String> export(RequestProperties properties, List<String> ids) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports the {@link Marker}s with the given names and returns the name of the result file.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param markerNames The {@link Marker} names
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if an I/O operation fails
	 */
	ServerResult<String> export(RequestProperties properties, Set<String> markerNames) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports all the data associated with {@link Marker}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return The name of the result file.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws IOException                 Thrown if an I/O operation fails
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException;
}
