/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * Async version of {@link ClimateService}
 *
 * @author Sebastian Raubach
 */
public interface ClimateServiceAsync
{
	/**
	 * Retrieves a list of all the {@link Climate}s.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param hasClimateData Only get the climates for which we actually have data?
	 * @param callback       The {@link AsyncCallback}
	 */
	void get(RequestProperties properties, List<Long> datasetIds, boolean hasClimateData, AsyncCallback<ServerResult<List<Climate>>> callback);

	/**
	 * Retrieves a list of {@link Climate}s having ground overlays.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getWithGroundOverlays(RequestProperties properties, AsyncCallback<ServerResult<List<Climate>>> callback);

	/**
	 * Exports the min/avg/max data for the given {@link Climate} and {@link Group} ids and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id (can be <code>null</code> to get the overall values of the whole dataset)
	 * @param callback   The {@link AsyncCallback}
	 */
	void getMinAvgMaxFile(RequestProperties properties, Long climateId, Long groupId, AsyncCallback<ServerResult<Pair<String, String>>> callback);

	/**
	 * Returns a paginated list of {@link ClimateYearData} for the given {@link Climate} and {@link Group} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id (can be <code>null</code> to get the overall values of the whole dataset)
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getGroupData(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<ClimateYearData>>> callback);

	/**
	 * Returns a list of {@link ClimateOverlay}s for the given {@link Climate} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getClimateOverlays(RequestProperties properties, Long climateId, AsyncCallback<ServerResult<List<ClimateOverlay>>> callback);

	/**
	 * Exports all the data associated with {@link Climate}s for the given {@link Climate} and {@link Group} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id (can be <code>null</code> to get the overall values of the whole dataset)
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId, AsyncCallback<ServerResult<String>> callback);
}
