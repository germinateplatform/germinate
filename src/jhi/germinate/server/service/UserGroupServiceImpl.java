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

package jhi.germinate.server.service;

import java.util.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link UserGroupServiceImpl} is the implementation of {@link GroupService}.
 *
 * @author Sebastian Raubach
 * @author Gordon Stephen
 */
@WebServlet(urlPatterns = {"/germinate/userpermissions"})
public class UserGroupServiceImpl extends BaseRemoteServiceServlet implements UserGroupService
{
	@Override
	public PaginatedServerResult<List<UserGroup>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException, InsufficientPermissionsException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return UserGroupManager.getAllForFilter(userAuth, filter, pagination);
	}

	@Override
	public ServerResult<Void> renameGroup(RequestProperties properties, UserGroup group) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return UserGroupManager.rename(userAuth, group);
	}

	@Override
	public ServerResult<UserGroup> createNew(RequestProperties properties, UserGroup group) throws InvalidSessionException, DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return UserGroupManager.create(userAuth, group);
	}

	@Override
	public DebugInfo delete(RequestProperties properties, List<Long> groupIds) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return UserGroupManager.delete(userAuth, groupIds);
	}
}
