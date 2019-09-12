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
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link AccessionService} is a {@link RemoteService} providing methods to retrieve accession data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("accession")
public interface AccessionService extends RemoteService
{
	String[] COLUMNS_SORTABLE = {Accession.ID, EntityType.NAME, Location.ID, LocationType.NAME, Accession.GENERAL_IDENTIFIER, Accession.NAME, Accession.NUMBER, Accession.COLLNUMB, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Accession.COLLDATE, Country.COUNTRY_NAME, Taxonomy.GENUS, Taxonomy.SPECIES, Taxonomy.SUBTAXA, Accession.SYNONYMS, Synonym.SYNONYM, Accession.PDCI, Accession.IMAGE_COUNT, Accession.FIRST_IMAGE_PATH, BiologicalStatus.SAMPSTAT, Accession.PUID};

	final class Inst
	{
		private static final class InstanceHolder
		{
			private static final AccessionServiceAsync INSTANCE = GWT.create(AccessionService.class);
		}

		public static AccessionServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a paginated list of {@link Accession}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Accession}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<Accession>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Returns the ids of the {@link Accession}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
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
	 * Returns the ids of the entity parents of the {@link Accession}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the {@link Accession}s
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getEntityParentIds(RequestProperties properties, List<String> ids) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the ids of the entity children of the {@link Accession}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the {@link Accession}s
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getEntityChildIds(RequestProperties properties, List<String> ids) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the {@link Accession}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param ids        The ids of the accessions
	 * @return The {@link Accession}s with the given ids.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the specified sort column isn't valid
	 */
	ServerResult<List<Accession>> getByIds(RequestProperties properties, Pagination pagination, List<String> ids) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Exports all the data associated with {@link Accession}s to a flat file. Returns the name of the result file.
	 *
	 * @param properties        The {@link RequestProperties}
	 * @param idColumn          The column to use as the identifier. Accessions will also be sorted according to this column.
	 * @param groupId           The group id (can be <code>null</code> or <code>null</code> to denote "no group")
	 * @param includeAttributes Should the attribute data be included?
	 * @return The name of the result file.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws IOException                      Thrown if the file interaction fails
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	ServerResult<String> export(RequestProperties properties, String idColumn, Long groupId, boolean includeAttributes) throws InvalidSessionException, DatabaseException, IOException, InsufficientPermissionsException;

	/**
	 * Exports all the data associated with {@link Accession}s to a flat file. Returns the name of the result file.
	 *
	 * @param properties        The {@link RequestProperties}
	 * @param idColumn          The column to use as the identifier. Accessions will also be sorted according to this column.
	 * @param accessionIds      The ids of the {@link Accession}s that should be exported.
	 * @param includeAttributes Should the attribute data be included?
	 * @return The name of the result file.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws IOException                      Thrown if the file interaction fails
	 */
	ServerResult<String> export(RequestProperties properties, String idColumn, Set<String> accessionIds, boolean includeAttributes) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports all the data associated with {@link Accession}s mathing the given {@link PartialSearchQuery}.
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

	/**
	 * Returns a paginated list containing the {@link Accession}s for the given mega environment.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The {@link MegaEnvironment} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list containing the {@link Accession}s for the given mega environment.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the requested sort column is invalid
	 */
	PaginatedServerResult<List<Accession>> getForMegaEnv(RequestProperties properties, Long megaEnvId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns the ids of the {@link Accession}s that are part of the {@link MegaEnvironment} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The id of the {@link MegaEnvironment}
	 * @return The ids of the {@link Accession}s that are part of the {@link MegaEnvironment} with the given id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getIdsForMegaEnv(RequestProperties properties, Long megaEnvId) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a paginated list of {@link Accession}s for the group preview (groups created from external tools).
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filename   The name of the file containing the indentifiers of the {@link Accession}s.
	 * @return A paginated list of {@link Accession}s for the group preview (groups created from external tools).
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if an I/O operation fails
	 */
	PaginatedServerResult<List<Accession>> getForGroupPreview(RequestProperties properties, Pagination pagination, String filename) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Removes {@link Accession}s based on their id from the group preview.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the {@link Accession}s to remove
	 * @param filename   The name of the file containing the indentifiers of the {@link Accession}s.
	 */
	void removeFromGroupPreview(RequestProperties properties, List<Long> ids, String filename);

	/**
	 * Removes all {@link Accession}s from the group preview.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filename   The name of the uploaded file
	 */
	void clearGroupPreview(RequestProperties properties, String filename);

	/**
	 * Returns a paginated list of {@link Accession}s based on their distance to the given location.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param latitude   The latitude of the requested location
	 * @param longitude  The longitude of the requested location
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link Accession}s based on their distance to the given location.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if an invalid sort column is specified
	 */
	PaginatedServerResult<List<Accession>> getByDistance(RequestProperties properties, Double latitude, Double longitude, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns a paginated list of {@link Accession}s that are located in the provided polygon.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param polygon    The list of {@link LatLngPoint}s defining the polygon
	 * @return A paginated list of {@link Accession}s that are located in the provided polygon.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if an invalid sort column is specified
	 */
	PaginatedServerResult<List<Accession>> getInPolygon(RequestProperties properties, Pagination pagination, List<List<LatLngPoint>> polygon) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns the ids of the {@link Accession}s that are located in the provided polygon.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param polygon    The list of {@link LatLngPoint}s defining the polygon
	 * @return the ids of the {@link Accession}s that are located in the provided polygon.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getIdsInPolygon(RequestProperties properties, List<List<LatLngPoint>> polygon) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the {@link Mcpd} object for the {@link Accession} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The {@link Accession} id
	 * @return The {@link Mcpd} object for the {@link Accession} with the given id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<Mcpd> getMcpd(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the {@link EntityPair}s (entity parents and entity children) related to the {@link Accession} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The {@link Accession} id
	 * @param pagination The {@link Pagination}
	 * @return The {@link EntityPair}s (entity parents and entity children) related to the {@link Accession} with the given id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	PaginatedServerResult<List<EntityPair>> getEntityPairs(RequestProperties properties, Long id, Pagination pagination) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports statistics about all the accession pdci scores and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if the file creation fails
	 */
	ServerResult<String> getPDCIStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException;
}
