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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link GroupService} is a {@link RemoteService} providing methods to retrieve group data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("group")
public interface GroupService extends RemoteService
{
	String[] COLUMNS_SORTABLE = {Group.ID, Group.DESCRIPTION, Group.CREATED_BY, Group.CREATED_ON, Group.UPDATED_ON, GroupType.ID, GroupType.DESCRIPTION, Group.COUNT};

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
			private static final GroupServiceAsync INSTANCE = GWT.create(GroupService.class);
		}

		public static GroupServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a list of available {@link Group}s for a given {@link GerminateDatabaseTable}
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupType  The {@link GerminateDatabaseTable}
	 * @return A list of available {@link Group}s for a given {@link GerminateDatabaseTable}
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Group>> getForType(RequestProperties properties, GerminateDatabaseTable groupType) throws InvalidSessionException, DatabaseException;

	/**
	 * Adds the {@link DatabaseObject}s with the given ids to the {@link Group} id.
	 *
	 * @param properties   The {@link RequestProperties}
	 * @param groupId      The {@link Group} id
	 * @param groupMembers The {@link DatabaseObject} ids
	 * @return The ids that actually have been added to the group (not necessarily the same as the parameter)
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to change the group members
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	ServerResult<Set<Long>> addItems(RequestProperties properties, Long groupId, List<Long> groupMembers) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Adds the {@link DatabaseObject}s from the specified file to the {@link Group} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param filename   The name of the file that was originally uploaded by the user
	 * @return The number of items that have been added.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to change the group members
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 * @throws InvalidArgumentException         Thrown if the specified filename is not valid
	 */
	ServerResult<Integer> addItemsFromPreview(RequestProperties properties, Long groupId, String filename) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException, InvalidArgumentException;

	/**
	 * Creates a new group of the given type
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param groupName      The name of the new group
	 * @param referenceTable The {@link GerminateDatabaseTable}
	 * @return The id of the new group
	 * @throws InvalidSessionException       Thrown if the current session is invalid
	 * @throws DatabaseException             Thrown if the query fails on the server
	 * @throws SystemInReadOnlyModeException Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	ServerResult<Group> createNew(RequestProperties properties, String groupName, GerminateDatabaseTable referenceTable) throws InvalidSessionException, DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException;

	/**
	 * Deletes the {@link Group} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupIds   The {@link List} of {@link Group} id
	 * @return The {@link DebugInfo} object
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to delete the group
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	DebugInfo delete(RequestProperties properties, List<Long> groupIds) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Removes the {@link DatabaseObject}s with the given id from the {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param memberIds  The {@link DatabaseObject} ids
	 * @return The {@link DebugInfo} object
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to change the group members
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	DebugInfo removeItems(RequestProperties properties, Long groupId, List<Long> memberIds) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Returns the list of all available {@link GroupType}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The list of all available {@link GroupType}s.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<GroupType>> getTypes(RequestProperties properties) throws InvalidSessionException, DatabaseException;

	/**
	 * Sets the {@link Group}'s visibility
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param isPublic   Set the group visibility to public or private?
	 * @return The {@link DebugInfo} object
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to change the group visibility
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	DebugInfo setVisibility(RequestProperties properties, Long groupId, boolean isPublic) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Returns a paginated list of {@link Accession}s for the given {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link Accession}s for the given {@link Group} id.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the requested sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	PaginatedServerResult<List<Accession>> getAccessionItems(RequestProperties properties, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Returns a list of {@link Accession} ids for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @return A list of {@link Accession} ids for the given {@link Group}.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	ServerResult<List<String>> getAccessionItemIds(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException;

	/**
	 * Returns a paginated list of {@link Location}s for the given {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link Location}s for the given {@link Group} id.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the requested sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	PaginatedServerResult<List<Location>> getLocationItems(RequestProperties properties, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Returns a list of {@link Location} ids for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @return A list of {@link Location} ids for the given {@link Group}.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	ServerResult<List<String>> getLocationItemIds(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Returns a paginated list of {@link MapDefinition}s for the given {@link Group} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link MapDefinition}s for the given {@link Group} id.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the requested sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	PaginatedServerResult<List<Marker>> getMarkerItems(RequestProperties properties, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Returns a list of {@link MapDefinition} ids for the given {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @return A list of {@link MapDefinition} ids for the given {@link Group}.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to view the group
	 */
	ServerResult<List<String>> getMarkerItemIds(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException;

	/**
	 * Adds the {@link DatabaseObject} items from the file with the specified name to the given {@link Group}. Uses the {@link GerminateDatabaseTable}
	 * and column to identify the items in question.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param result         The name of the previously uploaded file
	 * @param referenceTable The {@link GerminateDatabaseTable} the items are from
	 * @param column         The column to use to identify the elements
	 * @param groupId        The {@link Group} id
	 * @return The number of new items that have been added and the number of items that have been skipped.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws IOException                      Thrown if the interaction with the uploaded file fails
	 * @throws InvalidColumnException           Thrown if the given column is not valid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have permissions to add group members to this group
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	ServerResult<Tuple.Pair<Integer, Integer>> addItems(RequestProperties properties, String result, GerminateDatabaseTable referenceTable, String column, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InvalidColumnException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Adds the {@link DatabaseObject} items from the array to the given {@link Group}. Uses the {@link GerminateDatabaseTable}
	 * and column to identify the items in question.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param split          The new group members
	 * @param referenceTable The {@link GerminateDatabaseTable} the items are from
	 * @param column         The column to use to identify the elements
	 * @param groupId        The {@link Group} id
	 * @return The number of new items that have been added and the number of items that have been skipped.
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidColumnException           Thrown if the given column is not valid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have permissions to add group members to this group
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	ServerResult<Tuple.Pair<Integer, Integer>> addItems(RequestProperties properties, String[] split, GerminateDatabaseTable referenceTable, String column, Long groupId) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Returns a paginated list of {@link Group}s that the given {@link Accession} id is part of.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @return A paginated list of {@link Group}s that the given {@link Accession} id is part of.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws InvalidColumnException  Thrown if the requested sort column is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	PaginatedServerResult<List<Group>> getForAccession(RequestProperties properties, Long accessionId, Pagination pagination) throws InvalidSessionException, InvalidColumnException, DatabaseException;

	/**
	 * Retrieves the list of accession groups defined by the user as well as the public groups
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The dataset for which the accession groups are valid
	 * @return The list of available groups (user-defined or public)
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Group>> getAccessionGroups(RequestProperties properties, List<Long> datasetIds, ExperimentType type) throws InvalidSessionException, DatabaseException;

	/**
	 * Retrieves the list of marker groups
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The dataset for which the marker groups are valid
	 * @return The list of available marker groups
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Group>> getMarkerGroups(RequestProperties properties, List<Long> datasetIds, ExperimentType type) throws InvalidSessionException, DatabaseException;

	PaginatedServerResult<List<Group>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	ServerResult<Void> renameGroup(RequestProperties properties, Group group) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;
}
