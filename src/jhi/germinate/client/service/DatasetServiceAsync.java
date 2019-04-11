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
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * Async version of {@link CommonService}
 *
 * @author Sebastian Raubach
 */
public interface DatasetServiceAsync
{
	/**
	 * Returns a paginated list of {@link Dataset}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties     The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter         The {@link PartialSearchQuery} representing the user filtering
	 * @param experimentType The {@link ExperimentType}
	 * @param pagination     The {@link Pagination} The {@link Pagination}
	 * @param callback       The {@link AsyncCallback}
	 */
	Request getForFilter(RequestProperties properties, PartialSearchQuery filter, ExperimentType experimentType, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

	/**
	 * Returns a paginated list of {@link Dataset}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param id         The {@link DatabaseObject} id
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForFilterAndTrait(RequestProperties properties, PartialSearchQuery filter, ExperimentType type, Long id, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

	/**
	 * Returns a paginated list of {@link Dataset}s that have an association with the given {@link Accession} id.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param callback    The {@link AsyncCallback}
	 */
	Request getForAccession(RequestProperties properties, Long accessionId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

	/**
	 * Exports statistics about all the datasets (the user is allowed to see) and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getDatasetStats(RequestProperties properties, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports all the data associated with {@link Dataset}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a paginated list of {@link Dataset}s that contain the {@link Marker} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param markerId   The {@link Marker} id
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForMarker(RequestProperties properties, Pagination pagination, Long markerId, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

	/**
	 * Updates the {@link LicenseLog}s within the database. This represents a user accepting a number of {@link License}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param logs       The {@link LicenseLog}s to update
	 * @param callback   The {@link AsyncCallback}
	 */
	void updateLicenseLogs(RequestProperties properties, List<LicenseLog> logs, AsyncCallback<ServerResult<Boolean>> callback);

	/**
	 * Returns all the {@link Dataset}s that have {@link License}s that the current user hasn't accepted yet.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param types      The {@link ExperimentType}s to check
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getWithUnacceptedLicense(RequestProperties properties, List<ExperimentType> types, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

	/**
	 * Returns the {@link Experiment} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The id of the {@link Experiment}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getExperiment(RequestProperties properties, Long id, AsyncCallback<ServerResult<Experiment>> callback);

	/**
	 * Tracks that the current user (represented by an {@link UnapprovedUser}) accessed the given {@link Dataset}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link List} of {@link Dataset} ids.
	 * @param user       The user that accessed the datasets.
	 * @param callback   The {@link AsyncCallback}
	 */
	void trackDatasetAccess(RequestProperties properties, List<Long> datasetIds, UnapprovedUser user, AsyncCallback<ServerResult<Boolean>> callback);

	/**
	 * Exports the dataset attributes of the given {@link Dataset}s to a file. Only includes the {@link Attribute}s with the given ids (can be <code>null</code>).
	 *
	 * @param properties   The {@link RequestProperties}
	 * @param datasetIds   The {@link List} of {@link Dataset} ids.
	 * @param attributeIds The {@link List} of {@link Attribute} ids.
	 * @param callback     The {@link AsyncCallback}
	 */
	void exportAttributes(RequestProperties properties, List<Long> datasetIds, List<Long> attributeIds, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports the dataset's dublin core to a json file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The {@link Dataset} id
	 * @param async      The {@link AsyncCallback}
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	void getDublinCoreJson(RequestProperties properties, Long datasetId, AsyncCallback<ServerResult<String>> async);
}
