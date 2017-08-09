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

import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

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
	void login(RequestProperties properties, UserCredentials credentials, AsyncCallback<UserAuth> callback);

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

	void logout(RequestProperties properties, AsyncCallback<Void> async);

	void addInstitution(Institution institution, AsyncCallback<Void> async);
}
