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

import com.google.gwt.core.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link ClimateService} is a {@link RemoteService} providing methods to retrieve climate data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("climate")
public interface ClimateService extends RemoteService
{
	final class Inst
	{

		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
		 * InstanceHolder#INSTANCE}, not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
		 * >Initialization-on-demand holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
		 * <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final ClimateServiceAsync INSTANCE = GWT.create(ClimateService.class);
		}

		public static ClimateServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}

	}

	/**
	 * Retrieves a list of all the {@link Climate}s.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param hasClimateData Only get the climates for which we actually have data?
	 * @return A  list of all the {@link Climate}s.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Climate>> get(RequestProperties properties, List<Long> datasetIds, boolean hasClimateData) throws InvalidSessionException, DatabaseException;

	/**
	 * Retrieves a list of {@link Climate}s having ground overlays.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return A list of {@link Climate}s having ground overlays.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Climate>> getWithGroundOverlays(RequestProperties properties) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports the min/avg/max data for the given {@link Climate} and {@link Group} ids and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id (can be <code>null</code> to get the overall values of the whole dataset)
	 * @return The name of the result file.
	 * @throws InvalidSessionException   Thrown if the current session is invalid
	 * @throws DatabaseException         Thrown if the query fails on the server
	 * @throws IOException               Thrown if the file interaction fails
	 * @throws InvalidSelectionException Thrown if there is no data for the given selection
	 */
	ServerResult<Pair<String, String>> getMinAvgMaxFile(RequestProperties properties, Long climateId, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InvalidSelectionException;

	/**
	 * Returns a paginated list of {@link ClimateYearData} for the given {@link Climate} and {@link Group} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id (can be <code>null</code> to get the overall values of the whole dataset)
	 * @return A paginated list of {@link ClimateYearData} for the given {@link Climate} and {@link Group} ids.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the specified sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to view the group
	 */
	PaginatedServerResult<List<ClimateYearData>> getGroupData(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Returns a list of {@link ClimateOverlay}s for the given {@link Climate} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @return A list of {@link ClimateOverlay}s for the given {@link Climate} id.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<ClimateOverlay>> getClimateOverlays(RequestProperties properties, Long climateId) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports all the data associated with {@link Climate}s for the given {@link Climate} and {@link Group} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param climateId  The {@link Climate} id
	 * @param groupId    The {@link Group} id (can be <code>null</code> to get the overall values of the whole dataset)
	 * @return The name of the result file.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws IOException                      Thrown if an I/O operation fails
	 * @throws InvalidColumnException           Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to view the group
	 */
	ServerResult<String> export(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InvalidColumnException, InsufficientPermissionsException;
}
