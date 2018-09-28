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
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link LocationService} is a {@link RemoteService} providing methods to retrieve geographic data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("location")
public interface LocationService extends RemoteService
{
	String DISTANCE = "distance";

	String[] COLUMNS_LOCATION_SORTABLE          = {Location.ID, jhi.germinate.shared.datastructure.database.LocationType.NAME, Location.REGION, Location.STATE, Location.SITE_NAME, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Country.COUNTRY_NAME};
	String[] COLUMNS_LOCATION_DISTANCE_SORTABLE = {Location.ID, jhi.germinate.shared.datastructure.database.LocationType.NAME, Location.REGION, Location.STATE, Location.SITE_NAME, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Country.COUNTRY_NAME, DISTANCE};
	String[] COLUMNS_INSTITUTION_SORTABLE       = {Institution.ID, Institution.NAME, Institution.ACRONYM, Country.COUNTRY_NAME, Institution.CONTACT, Institution.PHONE, Institution.EMAIL, Institution.ADDRESS};

	final class Inst
	{
		private static final class InstanceHolder
		{
			private static final LocationServiceAsync INSTANCE = GWT.create(LocationService.class);
		}

		public static LocationServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a paginated list of {@link Location}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Location}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<Location>> getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Returns the ids of the {@link Location}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return The ids of the {@link Location}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Returns a paginated list of {@link Location}s that are located in the given {@link MegaEnvironment} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The {@link MegaEnvironment} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link Location}s that are located in the given {@link MegaEnvironment} id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the selected sort column is not valid
	 */
	PaginatedServerResult<List<Location>> getForMegaEnv(RequestProperties properties, Long megaEnvId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns a paginated list of {@link Location}s ordered by their distance to the requested position.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param latitude   The latitude value of the query location
	 * @param longitude  The longitude value of the query location
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link Location}s ordered by their distance to the requested position.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the selected sort column is not valid
	 */
	PaginatedServerResult<List<Location>> getByDistance(RequestProperties properties, double latitude, double longitude, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Exports the {@link Location}s of the given {@link LocationType} as Json to a file and returns the result file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param type       The {@link LocationType}
	 * @return The result file name.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if the file interaction fails
	 */
	ServerResult<String> getJsonForType(RequestProperties properties, LocationType type) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Returns a paginated list of {@link Institution}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Institution}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<Institution>> getInstitutionsForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Returns a list of {@link Country} objects with additional {@link DatabaseObject#COUNT} field representing the number of {@link Institution}s in
	 * this country.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return A list of {@link Country} objects with additional {@link DatabaseObject#COUNT} field representing the number of {@link Institution}s in
	 * this country.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if the file interaction fails
	 */
	ServerResult<List<Country>> getInstitutionsByCountry(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Returns the {@link Location}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param ids        The ids of the accessions
	 * @return The {@link Location}s with the given ids.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the specified sort column isn't valid
	 */
	ServerResult<List<Location>> getByIds(RequestProperties properties, Pagination pagination, List<String> ids) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns a paginated list of {@link MegaEnvironment}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link MegaEnvironment}s.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the specified sort column isn't valid
	 */
	PaginatedServerResult<List<MegaEnvironment>> getMegaEnvs(RequestProperties properties, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns a paginated list of {@link Location}s that are located in the polygon defined by the given list of {@link LatLngPoint}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param bounds     The list of {@link LatLngPoint}s defining the polygon
	 * @return A paginated list of {@link Location}s that are located in the polygon defined by the given list of {@link LatLngPoint}s.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws InvalidColumnException  Thrown if the specified sort column isn't valid
	 */
	PaginatedServerResult<List<Location>> getInPolygon(RequestProperties properties, Pagination pagination, List<LatLngPoint> bounds) throws InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Returns a list of {@link Location} ids that are located in the polygon defined by the given list of {@link LatLngPoint}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param polygon    The list of {@link LatLngPoint}s defining the polygon
	 * @return A list of {@link Location} ids that are located in the polygon defined by the given list of {@link LatLngPoint}s.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getIdsInPolygon(RequestProperties properties, List<LatLngPoint> polygon) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports all the data associated with {@link Location}s mathing the given {@link PartialSearchQuery}.
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
	 * Returns a list of {@link Location}s that have {@link Climate} data for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id
	 * @return A list of {@link Location}s that have {@link Climate} data for the given {@link Group}.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to access this group
	 */
	ServerResult<List<Location>> getForClimateAndGroup(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException;

	/**
	 * Returns a list of {@link Location} ids for the given {@link MegaEnvironment} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The {@link MegaEnvironment} id
	 * @return A list of {@link Location} ids for the given {@link MegaEnvironment} id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getIdsForMegaEnv(RequestProperties properties, Long megaEnvId) throws InvalidSessionException, DatabaseException;

	/**
	 * Creates a kml file for the given KmlType and returns the filename
	 *
	 * @param properties The {@link RequestProperties}
	 * @param type       The {@link KmlType} to create
	 * @param id         The overall id to use
	 * @return The filename
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws KMLException            Thrown if an error occurs while trying to create the KML file
	 */
	ServerResult<String> exportToKml(RequestProperties properties, KmlType type, Long id) throws InvalidSessionException, DatabaseException, KMLException;
}
