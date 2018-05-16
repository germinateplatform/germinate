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
 * Async version of {@link CommentService}
 *
 * @author Sebastian Raubach
 */
public interface CommentServiceAsync
{
	/**
	 * Adds a new {@link Comment} to the database.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param type        The {@link CommentType}
	 * @param referenceId The reference id (the id within the reference table)
	 * @param description The actual annotation text to add
	 * @param callback    The {@link AsyncCallback}
	 */
	void add(RequestProperties properties, CommentType type, Long referenceId, String description, AsyncCallback<DebugInfo> callback);

	/**
	 * Returns a list of {@link CommentType}s for the given {@link GerminateDatabaseTable}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param reference  The {@link GerminateDatabaseTable} specifying the reference table
	 * @param callback   The {@link AsyncCallback}
	 */
	void getTypes(RequestProperties properties, GerminateDatabaseTable reference, AsyncCallback<ServerResult<List<CommentType>>> callback);

	/**
	 * Disables the given {@link Comment} in the database so that it'll no longer be shown.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param comment    The {@link Comment} to disable
	 * @param callback   The {@link AsyncCallback}
	 */
	void disable(RequestProperties properties, Comment comment, AsyncCallback<Void> callback);

	/**
	 * Returns a paginated list of {@link Comment}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 * @return The {@link Request}
	 */
	Request getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Comment>>> callback);
}
