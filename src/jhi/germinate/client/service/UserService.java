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
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link UserService} is a {@link RemoteService} providing methods to register users with Germinate Gatekeeper
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService
{
	final class Inst
	{
		private static final class InstanceHolder
		{
			private static final UserServiceAsync INSTANCE = GWT.create(UserService.class);
		}

		public static UserServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Attempts to log the user in. Returns the {@link UserAuth} with some settings and the session id (either the old one if it's still valid, or a
	 * newly generated one if the login was successful).
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param credentials the {@link UserCredentials}
	 * @return The {@link UserAuth} with some settings and the session id (either the old one if it's still valid, or a newly generated one if the
	 * login was successful).
	 * @throws LoginRegistrationException Thrown if the login/registration failed. Check {@link LoginRegistrationException#getReason()} for the
	 *                                    reason.
	 * @throws DatabaseException          Thrown if the interaction with the database fails
	 */
	UserAuth login(RequestProperties properties, UserCredentials credentials) throws LoginRegistrationException, DatabaseException;

	/**
	 * Logs the current user out.
	 *
	 * @param properties The {@link RequestProperties}
	 * @throws InvalidSessionException    Thrown if the current session is invalid
	 * @throws LoginRegistrationException Thrown if the logout failed. Check {@link LoginRegistrationException#getReason()} for the reason.
	 */
	void logout(RequestProperties properties) throws InvalidSessionException, LoginRegistrationException;

	/**
	 * Attempts to register the new {@link UnapprovedUser}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param user       The new {@link UnapprovedUser} that should be registered
	 * @throws DatabaseException             Thrown if the interaction with the database fails
	 * @throws LoginRegistrationException    Thrown if the login/registration failed. Check {@link LoginRegistrationException#getReason()} for the
	 *                                       reason.
	 * @throws SystemInReadOnlyModeException Thrown if the system is currently operating in readAll-only mode
	 */
	void register(RequestProperties properties, UnapprovedUser user) throws DatabaseException, LoginRegistrationException, SystemInReadOnlyModeException;

	/**
	 * Returns the available {@link Institution}s in the Gatekeeper database
	 *
	 * @return The available {@link Institution}s in the Gatekeeper database
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	List<Institution> getInstitutions() throws DatabaseException;

	/**
	 * Adds a new institution to the database.
	 *
	 * @param institution The {@link Institution} to add
	 * @throws DatabaseException             Thrown if the interaction with the database fails
	 * @throws SystemInReadOnlyModeException Thrown if the system is currently operating in readAll-only mode
	 */
	void addInstitution(Institution institution) throws DatabaseException, SystemInReadOnlyModeException;

	/**
	 * Returns the {@link GatekeeperUser}s for the given {@link UserGroup} or {@link Dataset} id or all users if no id is specified.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param groupId    The id of the {@link UserGroup} or {@link Dataset in question. Setting this to <code>null</code> will return all users.
	 * @return The {@link GatekeeperUser}s for the given {@link UserGroup} id or all users if no id is specified.
	 */
	PaginatedServerResult<List<GatekeeperUser>> getUsersForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, Long groupId, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException, InsufficientPermissionsException;

	/**
	 * Removes the objects with the given ids from the {@link UserGroup} with the given id
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The id of the {@link UserGroup}
	 * @param ids        The ids of the objects to remove
	 */
	void removeFromGroup(RequestProperties properties, Long groupId, List<Long> ids) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Adds the objects with the given ids to the {@link UserGroup} with the given id
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The id of the {@link UserGroup}
	 * @param ids        The ids of the objects to add
	 */
	void addToGroup(RequestProperties properties, Long groupId, List<Long> ids) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Adds the objects with the given ids to the {@link Dataset} permissions with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The id of the {@link Dataset}
	 * @param ids        The ids of the objects to add
	 */
	void addToDataset(RequestProperties properties, Long datasetId, List<Long> ids, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Removes the objects with the given ids from the {@link Dataset} permissions with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The id of the {@link Dataset}
	 * @param ids        The ids of the objects to add
	 */
	void removeFromDataset(RequestProperties properties, Long datasetId, List<Long> ids, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;
}
