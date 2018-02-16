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
 * @author Sebastian Raubach
 */
public interface CompoundServiceAsync
{
	/**
	 * Returns a list of {@link Compound}s for the given {@link Dataset} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The ids of the {@link Dataset}s
	 * @param callback   The {@link AsyncCallback}
	 */
	void getForDatasetIds(RequestProperties properties, List<Long> datasetIds, AsyncCallback<ServerResult<List<Compound>>> callback);

	/**
	 * Returns a paginated list of {@link Compound}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Compound>>> callback);

	/**
	 * Returns the ids of the {@link Compound}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsForFilter(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns a paginated list of {@link CompoundData} objectss that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getDataForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<CompoundData>>> callback);

	/**
	 * Returns the {@link Compound} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The id of the {@link Compound}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getById(RequestProperties properties, Long id, AsyncCallback<ServerResult<Compound>> callback);

	/**
	 * Returns overview stats in the for of {@link DataStats} for the given {@link Dataset} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link Dataset} ids
	 * @param callback   The {@link AsyncCallback}
	 */
	void getDataStatsForDatasets(RequestProperties properties, List<Long> datasetIds, AsyncCallback<ServerResult<List<DataStats>>> callback);

	/**
	 * Exports the {@link Compound} information to a file and returns the name of the result file.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param datasetIds  The dataset id
	 * @param groupIds    The list of groups to export
	 * @param compoundIds The list of compounds to export
	 * @param callback    The {@link AsyncCallback}
	 */
	void getExportFile(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, List<Long> compoundIds, boolean includeId, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports the {@link CompoundData} information for the given {@link Compound} id and {@link Dataset} id and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param compoundId The {@link Compound} id
	 * @param datasetId  The {@link Dataset} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getBarChartData(RequestProperties properties, Long compoundId, Long datasetId, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports all the data associated with {@link CompoundData} objects mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);
}
