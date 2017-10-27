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
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.enums.LocationType;
import jhi.germinate.shared.search.*;

/**
 * Async version of {@link LocationService}.
 *
 * @author Sebastian Raubach
 */
public interface LocationServiceAsync
{
	/**
	 * Returns a paginated list of {@link Location}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Location>>> callback);

	/**
	 * Returns the ids of the {@link Location}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsForFilter(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns a paginated list of {@link Location}s that are located in the given {@link MegaEnvironment} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The {@link MegaEnvironment} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForMegaEnv(RequestProperties properties, Long megaEnvId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Location>>> callback);

	/**
	 * Returns a paginated list of {@link Location}s ordered by their distance to the requested position.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param latitude   The latitude value of the query location
	 * @param longitude  The longitude value of the query location
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getByDistance(RequestProperties properties, double latitude, double longitude, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Location>>> callback);

	/**
	 * Exports the {@link Location}s of the given {@link LocationType} as Json to a file and returns the result file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param type       The {@link LocationType}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getJsonForType(RequestProperties properties, LocationType type, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a paginated list of {@link Institution}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getInstitutionsForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Institution>>> callback);

	/**
	 * Returns a list of {@link Country} objects with additional {@link DatabaseObject#COUNT} field representing the number of {@link Institution}s in
	 * this country.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getInstitutionsByCountry(RequestProperties properties, AsyncCallback<ServerResult<List<Country>>> callback);

	/**
	 * Returns the {@link Location}s with the given ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param ids        The ids of the accessions
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getByIds(RequestProperties properties, Pagination pagination, List<String> ids, AsyncCallback<ServerResult<List<Location>>> callback);

	/**
	 * Exports the {@link Location} data and returns the name of the result file
	 *
	 * @param properties The {@link RequestProperties}
	 * @param ids        The ids of the accessions
	 * @param callback   The {@link AsyncCallback}
	 */
	void exportForIds(RequestProperties properties, List<String> ids, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a paginated list of {@link MegaEnvironment}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getMegaEnvs(RequestProperties properties, Pagination pagination, AsyncCallback<PaginatedServerResult<List<MegaEnvironment>>> callback);

	/**
	 * Returns a paginated list of {@link Location}s that are located in the polygon defined by the given list of {@link LatLngPoint}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param bounds     The list of {@link LatLngPoint}s defining the polygon
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getInPolygon(RequestProperties properties, Pagination pagination, List<LatLngPoint> bounds, AsyncCallback<PaginatedServerResult<List<Location>>> callback);

	/**
	 * Returns a list of {@link Location} ids that are located in the polygon defined by the given list of {@link LatLngPoint}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param polygon    The list of {@link LatLngPoint}s defining the polygon
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsInPolygon(RequestProperties properties, List<LatLngPoint> polygon, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Exports all the data associated with {@link Location}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a list of {@link Location}s that have {@link Climate} data for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getForClimateAndGroup(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId, AsyncCallback<ServerResult<List<Location>>> callback);

	/**
	 * Returns a list of {@link Location} ids for the given {@link MegaEnvironment} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param megaEnvId  The {@link MegaEnvironment} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsForMegaEnv(RequestProperties properties, Long megaEnvId, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Creates a kml file for the given KmlType and returns the filename
	 *
	 * @param properties The {@link RequestProperties}
	 * @param type       The {@link KmlType} to create
	 * @param id         The overall id to use
	 * @param callback   The {@link AsyncCallback}
	 */
	void exportToKml(RequestProperties properties, KmlType type, Long id, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a list of {@link Country} objects with either {@link DatabaseObject#COUNT} or {@link Country#AVERAGE} set to the {@link Phenotype}
	 * value for this {@link ExperimentType} and {@link Dataset} ids.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param datasetIds  The {@link Dataset} ids
	 * @param type        The {@link ExperimentType}
	 * @param phenotypeId The {@link Phenotype} id
	 * @param callback    The {@link AsyncCallback}
	 */
	void getCountryValues(RequestProperties properties, List<Long> datasetIds, ExperimentType type, Long phenotypeId, AsyncCallback<ServerResult<List<Country>>> callback);
}
