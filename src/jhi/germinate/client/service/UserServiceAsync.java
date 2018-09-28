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
 * Async version of {@link UserService}.
 *
 * @author Sebastian Raubach
 */
public interface UserServiceAsync
{
	/**
	 * Attempts to log the user in. Returns the {@link UserAuth} with some settings and the session id (either the old one if it's still valid, or a
	 * newly generated one if the login was successful).
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param credentials the {@link UserCredentials}
	 * @param callback    The {@link AsyncCallback}
	 */
	Request login(RequestProperties properties, UserCredentials credentials, AsyncCallback<UserAuth> callback);

	/**
	 * Attempts to register the new {@link UnapprovedUser}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param user       The new {@link UnapprovedUser} that should be registered
	 * @param callback   The {@link AsyncCallback}
	 */
	void register(RequestProperties properties, UnapprovedUser user, AsyncCallback<Void> callback);

	/**
	 * Returns the available {@link Institution}s in the Gatekeeper database
	 *
	 * @param callback The {@link AsyncCallback}
	 */
	void getInstitutions(AsyncCallback<List<Institution>> callback);

	/**
	 * Logs the current user out.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param async      The {@link AsyncCallback}
	 */
	void logout(RequestProperties properties, AsyncCallback<Void> async);

	/**
	 * Adds a new institution to the database.
	 *
	 * @param institution The {@link Institution} to add
	 * @param async       The {@link AsyncCallback}
	 */
	void addInstitution(Institution institution, AsyncCallback<Void> async);

	/**
	 * Returns the {@link GatekeeperUser}s for the given {@link UserGroup} or {@link Dataset id or all users if no id is specified.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param groupId    The id of the {@link UserGroup} or {@link Dataset in question. Setting this to <code>null</code> will return all users.
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getUsersForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, Long groupId, GerminateDatabaseTable table, AsyncCallback<PaginatedServerResult<List<GatekeeperUser>>> callback);

	/**
	 * Removes the objects with the given ids from the {@link UserGroup} with the given id
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The id of the {@link UserGroup}
	 * @param ids        The ids of the objects to remove
	 * @param callback   The {@link AsyncCallback}
	 */
	void removeFromGroup(RequestProperties properties, Long groupId, List<Long> ids, AsyncCallback<Void> callback);

	/**
	 * Adds the objects with the given ids to the {@link UserGroup} with the given id
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The id of the {@link UserGroup}
	 * @param ids        The ids of the objects to add
	 * @param callback   The {@link AsyncCallback}
	 */
	void addToGroup(RequestProperties properties, Long groupId, List<Long> ids, AsyncCallback<Void> callback);

	/**
	 * Adds the objects with the given ids to the {@link Dataset} permissions with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The id of the {@link Dataset}
	 * @param ids        The ids of the objects to add
	 * @param callback   The {@link AsyncCallback}
	 */
	void addToDataset(RequestProperties properties, Long datasetId, List<Long> ids, GerminateDatabaseTable table, AsyncCallback<Void> callback);

	/**
	 * Removes the objects with the given ids from the {@link Dataset} permissions with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The id of the {@link Dataset}
	 * @param ids        The ids of the objects to add
	 * @param callback   The {@link AsyncCallback}
	 */
	void removeFromDataset(RequestProperties properties, Long datasetId, List<Long> ids, GerminateDatabaseTable table, AsyncCallback<Void> callback);
}
