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

@RemoteServiceRelativePath("dataset")
public interface DatasetService extends RemoteService
{
	final class Inst
	{
		private static final class InstanceHolder
		{
			private static final DatasetServiceAsync INSTANCE = GWT.create(DatasetService.class);
		}

		public static DatasetServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a paginated list of {@link Dataset}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties     The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter         The {@link PartialSearchQuery} representing the user filtering
	 * @param experimentType The {@link ExperimentType}
	 * @param pagination     The {@link Pagination} The {@link Pagination}
	 * @return A paginated list of {@link Dataset}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException      Thrown if the search query is invalid
	 * @throws InvalidArgumentException         Thrown if one of the provided arguments for the filtering is invalid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete the request
	 */
	PaginatedServerResult<List<Dataset>> getForFilter(RequestProperties properties, PartialSearchQuery filter, ExperimentType experimentType, Pagination pagination) throws InsufficientPermissionsException, InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns a paginated list of {@link Dataset}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param id         The {@link DatabaseObject} id
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @return A paginated list of {@link Dataset}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException      Thrown if the search query is invalid
	 * @throws InvalidArgumentException         Thrown if one of the provided arguments for the filtering is invalid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete the request
	 */
	PaginatedServerResult<List<Dataset>> getForFilterAndTrait(RequestProperties properties, PartialSearchQuery filter, ExperimentType type, Long id, Pagination pagination) throws InsufficientPermissionsException, InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns a paginated list of {@link Dataset}s that have an association with the given {@link Accession} id.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @return A paginated list of {@link Dataset}s that have an association with the given {@link Accession} id.
	 * @throws InsufficientPermissionsException Thrown if the given userId does not exist in the user table, but a user is required
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the sort request is using an unavailable column
	 */
	PaginatedServerResult<List<Dataset>> getForAccession(RequestProperties properties, Long accessionId, Pagination pagination) throws InsufficientPermissionsException, InvalidSessionException, DatabaseException, InvalidColumnException;

	/**
	 * Exports statistics about all the datasets (the user is allowed to see) and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if the file creation fails
	 */
	ServerResult<String> getDatasetStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports all the data associated with {@link Dataset}s mathing the given {@link PartialSearchQuery}.
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
	ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Returns a paginated list of {@link Dataset}s that contain the {@link Marker} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param markerId   The {@link Marker} id
	 * @return A paginated list of {@link Dataset}s that contain the {@link Marker} with the given id.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException      Thrown if the search query is invalid
	 * @throws InvalidArgumentException         Thrown if one of the provided arguments for the filtering is invalid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete the request
	 */
	PaginatedServerResult<List<Dataset>> getForMarker(RequestProperties properties, Pagination pagination, Long markerId) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Updates the {@link LicenseLog}s within the database. This represents a user accepting a number of {@link License}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param logs       The {@link LicenseLog}s to update
	 * @return <code>true</code> if at least one entry was successfully updated
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException
	 */
	ServerResult<Boolean> updateLicenseLogs(RequestProperties properties, List<LicenseLog> logs) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns all the {@link Dataset}s that have {@link License}s that the current user hasn't accepted yet.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param types      The {@link ExperimentType}s to check
	 * @param pagination The {@link Pagination}
	 * @return All the {@link Dataset}s that have {@link License}s that the current user hasn't accepted yet.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete the request
	 * @throws InvalidArgumentException         Thrown if one of the provided arguments for the filtering is invalid
	 * @throws InvalidSearchQueryException      Thrown if the search query is invalid
	 * @throws InvalidColumnException           Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws DatabaseException                Thrown if the query fails on the server
	 */
	PaginatedServerResult<List<Dataset>> getWithUnacceptedLicense(RequestProperties properties, List<ExperimentType> types, Pagination pagination) throws InvalidSessionException, InsufficientPermissionsException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException;

	/**
	 * Returns the {@link Experiment} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The id of the {@link Experiment}
	 * @return The {@link Experiment} with the given id.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete the request
	 */
	ServerResult<Experiment> getExperiment(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException;

	/**
	 * Tracks that the current user (represented by an {@link UnapprovedUser}) accessed the given {@link Dataset}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link List} of {@link Dataset} ids.
	 * @param user       The user that accessed the datasets.
	 * @return <code>true</code> if the event was successfully tracked
	 * @throws InvalidSessionException       Thrown if the current session is invalid
	 * @throws DatabaseException             Thrown if the query fails on the server
	 * @throws SystemInReadOnlyModeException Thrown if Germinate is currently operating in "read-only" mode
	 */
	ServerResult<Boolean> trackDatasetAccess(RequestProperties properties, List<Long> datasetIds, UnapprovedUser user) throws InvalidSessionException, DatabaseException, SystemInReadOnlyModeException;

	/**
	 * Exports the dataset attributes of the given {@link Dataset}s to a file. Only includes the {@link Attribute}s with the given ids (can be <code>null</code>).
	 *
	 * @param properties   The {@link RequestProperties}
	 * @param datasetIds   The {@link List} of {@link Dataset} ids.
	 * @param attributeIds The {@link List} of {@link Attribute} ids.
	 * @return The dataset attributes of the given {@link Dataset}s to a file. Only includes the {@link Attribute}s with the given ids (can be <code>null</code>).
	 */
	ServerResult<String> exportAttributes(RequestProperties properties, List<Long> datasetIds, List<Long> attributeIds) throws InvalidSessionException, DatabaseException;
}
