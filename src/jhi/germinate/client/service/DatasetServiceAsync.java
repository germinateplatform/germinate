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
	 * @param internal       Should only internal datasets be returned?
	 * @param pagination     The {@link Pagination} The {@link Pagination}
	 * @param callback       The {@link AsyncCallback}
	 */
	Request getForFilter(RequestProperties properties, PartialSearchQuery filter, ExperimentType experimentType, boolean internal, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

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

	void updateLicenseLogs(RequestProperties properties, List<LicenseLog> logs, AsyncCallback<ServerResult<Boolean>> callback);

	Request getWithUnacceptedLicense(RequestProperties properties, List<ExperimentType> types, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback);

	void getExperiment(RequestProperties properties, Long id, AsyncCallback<ServerResult<Experiment>> callback);

	void getJson(AsyncCallback<String> callback);
}
