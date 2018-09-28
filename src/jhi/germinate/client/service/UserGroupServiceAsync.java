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

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * Async version of {@link GroupService}.
 *
 * @author Sebastian Raubach
 */
public interface UserGroupServiceAsync
{
	/**
	 * Returns a paginated list of {@link UserGroup}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 * @return The {@link Request}
	 */
	Request getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<UserGroup>>> callback);

	/**
	 * Renames the given {@link UserGroup}
	 *
	 * @param properties The {@link RequestProperties}
	 * @param group      The {@link UserGroup} with the new name and description
	 * @param callback   The {@link AsyncCallback}
	 */
	void renameGroup(RequestProperties properties, UserGroup group, AsyncCallback<ServerResult<Void>> callback);

	/**
	 * Creates a new group of the given type
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param group          The new group
	 * @param callback       The {@link AsyncCallback}
	 */
	void createNew(RequestProperties properties, UserGroup group, AsyncCallback<ServerResult<UserGroup>> callback);

	/**
	 * Deletes the {@link UserGroup} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupIds   The {@link List} of {@link UserGroup} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void delete(RequestProperties properties, List<Long> groupIds, AsyncCallback<DebugInfo> callback);
}
