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
import jhi.germinate.shared.exception.*;

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
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
		 * InstanceHolder#INSTANCE}, not before.
		 * <p/>
		 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder idiom</a>) is
		 * thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
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

	void addInstitution(Institution institution) throws DatabaseException;
}
