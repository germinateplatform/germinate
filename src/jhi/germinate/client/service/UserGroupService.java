/*
 *  Copyright 2018 Information and Computational Sciences,
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

/**
 * {@link UserGroupService} is a {@link RemoteService} providing methods to retrieve user group data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("userpermissions")
public interface UserGroupService extends RemoteService
{
	/**
	 * Returns a paginated list of {@link UserGroup}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link UserGroup}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<UserGroup>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException, InsufficientPermissionsException;

	/**
	 * Renames the given {@link UserGroup}
	 *
	 * @param properties The {@link RequestProperties}
	 * @param group      The {@link UserGroup} with the new name and description
	 * @return Nothing
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have permissions to add group members to this group
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "read-only" mode
	 */
	ServerResult<Void> renameGroup(RequestProperties properties, UserGroup group) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Creates a new group of the given type
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param group          The new group
	 * @return The id of the new group
	 * @throws InvalidSessionException       Thrown if the current session is invalid
	 * @throws DatabaseException             Thrown if the query fails on the server
	 * @throws SystemInReadOnlyModeException Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	ServerResult<UserGroup> createNew(RequestProperties properties, UserGroup group) throws InvalidSessionException, DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException;

	/**
	 * Deletes the {@link UserGroup} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupIds   The {@link List} of {@link UserGroup} id
	 * @return The {@link DebugInfo} object
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user does not have permissions to delete the group
	 * @throws SystemInReadOnlyModeException    Thrown if Germinate is currently operating in "readAll-only" mode
	 */
	DebugInfo delete(RequestProperties properties, List<Long> groupIds) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	final class Inst
	{
		public static UserGroupServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}

		private static final class InstanceHolder
		{
			private static final UserGroupServiceAsync INSTANCE = GWT.create(UserGroupService.class);
		}
	}
}
