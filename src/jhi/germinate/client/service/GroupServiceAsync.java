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
import jhi.germinate.shared.search.*;

/**
 * Async version of {@link GroupService}.
 *
 * @author Sebastian Raubach
 */
public interface GroupServiceAsync
{
	/**
	 * Returns a list of available {@link Group}s for a given {@link GerminateDatabaseTable}
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupType  The {@link GerminateDatabaseTable}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getForType(RequestProperties properties, GerminateDatabaseTable groupType, AsyncCallback<ServerResult<List<Group>>> callback);

	/**
	 * Adds the {@link DatabaseObject}s with the given ids to the {@link Group} id.
	 *
	 * @param properties   The {@link RequestProperties}
	 * @param groupId      The {@link Group} id
	 * @param groupMembers The {@link DatabaseObject} ids
	 * @param callback     The {@link AsyncCallback}
	 */
	void addItems(RequestProperties properties, Long groupId, List<Long> groupMembers, AsyncCallback<ServerResult<Set<Long>>> callback);

	/**
	 * Adds the {@link DatabaseObject}s from the specified file to the {@link Group} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param filename   The name of the file that was originally uploaded by the user
	 * @param callback   The {@link AsyncCallback}
	 */
	void addItemsFromPreview(RequestProperties properties, Long groupId, String filename, AsyncCallback<ServerResult<Integer>> callback);

	/**
	 * Creates a new group of the given type
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param group          The new group
	 * @param referenceTable The {@link GerminateDatabaseTable}
	 * @param callback       The {@link AsyncCallback}
	 */
	void createNew(RequestProperties properties, Group group, GerminateDatabaseTable referenceTable, AsyncCallback<ServerResult<Group>> callback);

	/**
	 * Deletes the {@link Group} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupIds   The {@link List} of {@link Group} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void delete(RequestProperties properties, List<Long> groupIds, AsyncCallback<DebugInfo> callback);

	/**
	 * Removes the {@link DatabaseObject}s with the given id from the {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param memberIds  The {@link DatabaseObject} ids
	 * @param callback   The {@link AsyncCallback}
	 */
	void removeItems(RequestProperties properties, Long groupId, List<Long> memberIds, AsyncCallback<DebugInfo> callback);

	/**
	 * Returns the list of all available {@link GroupType}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getTypes(RequestProperties properties, AsyncCallback<ServerResult<List<GroupType>>> callback);

	/**
	 * Sets the {@link Group}'s visibility
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param isPublic   Set the group visibility to public or private?
	 * @param callback   The {@link AsyncCallback}
	 */
	void setVisibility(RequestProperties properties, Long groupId, boolean isPublic, AsyncCallback<DebugInfo> callback);

	/**
	 * Returns a paginated list of {@link Accession}s for the given {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getAccessionItems(RequestProperties properties, Long groupId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Accession>>> callback);

	/**
	 * Returns a list of {@link Accession} ids for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getAccessionItemIds(RequestProperties properties, Long groupId, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns a paginated list of {@link Location}s for the given {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getLocationItems(RequestProperties properties, Long groupId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Location>>> callback);

	/**
	 * Returns a list of {@link Location} ids for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getLocationItemIds(RequestProperties properties, Long groupId, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Returns a paginated list of {@link MapDefinition}s for the given {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getMarkerItems(RequestProperties properties, Long groupId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Marker>>> callback);

	/**
	 * Returns a list of {@link MapDefinition} ids for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getMarkerItemIds(RequestProperties properties, Long groupId, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Adds the {@link DatabaseObject} items from the file with the specified name to the given {@link Group}. Uses the {@link GerminateDatabaseTable}
	 * and column to identify the items in question.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param result         The name of the previously uploaded file
	 * @param referenceTable The {@link GerminateDatabaseTable} the items are from
	 * @param column         The column to use to identify the elements
	 * @param groupId        The {@link Group} id
	 * @param callback       The {@link AsyncCallback}
	 */
	void addItems(RequestProperties properties, String result, GerminateDatabaseTable referenceTable, String column, Long groupId, AsyncCallback<ServerResult<Tuple.Pair<Integer, Integer>>> callback);

	/**
	 * Adds the {@link DatabaseObject} items from the array to the given {@link Group}. Uses the {@link GerminateDatabaseTable} and column to identify
	 * the items in question.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param split          The new group members
	 * @param referenceTable The {@link GerminateDatabaseTable} the items are from
	 * @param column         The column to use to identify the elements
	 * @param groupId        The {@link Group} id
	 * @param callback       The {@link AsyncCallback}
	 */
	void addItems(RequestProperties properties, String[] split, GerminateDatabaseTable referenceTable, String column, Long groupId, AsyncCallback<ServerResult<Tuple.Pair<Integer, Integer>>> callback);

	/**
	 * Returns a paginated list of {@link Group}s that the given {@link Accession} id is part of.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param callback    The {@link AsyncCallback}
	 */
	Request getForAccession(RequestProperties properties, Long accessionId, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Group>>> callback);

	/**
	 * Retrieves the list of accession groups defined by the user as well as the public groups
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The dataset for which the accession groups are valid
	 * @param callback   The {@link AsyncCallback}
	 */
	void getAccessionGroups(RequestProperties properties, List<Long> datasetIds, ExperimentType type, AsyncCallback<ServerResult<List<Group>>> callback);

	/**
	 * Retrieves the list of marker groups
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The dataset for which the marker groups are valid
	 * @param callback   The {@link AsyncCallback}
	 */
	void getMarkerGroups(RequestProperties properties, List<Long> datasetIds, ExperimentType type, AsyncCallback<ServerResult<List<Group>>> callback);

	Request getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Group>>> callback);

	void renameGroup(RequestProperties properties, Group group, AsyncCallback<ServerResult<Void>> callback);
}
