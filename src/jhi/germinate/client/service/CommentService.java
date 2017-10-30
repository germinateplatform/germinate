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
 * {@link CommentService} is a {@link RemoteService} providing methods to retrieve annotations data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("comment")
public interface CommentService extends RemoteService
{
	String[] COLUMNS_SORTABLE = {Comment.ID, Comment.DESCRIPTION, CommentType.DESCRIPTION, "comments." + Comment.CREATED_ON, Comment.REFERENCE_ID, CommentType.REFERENCE_TABLE};

	final class Inst
	{
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE}, not
		 * before.
		 * <p/>
		 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder idiom</a>) is
		 * thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final CommentServiceAsync INSTANCE = GWT.create(CommentService.class);
		}

		public static CommentServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Adds a new {@link Comment} to the database.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param type        The {@link CommentType}
	 * @param referenceId The reference id (the id within the reference table)
	 * @param description The actual annotation text to add
	 * @return The {@link DebugInfo}
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete this operation
	 * @throws SystemInReadOnlyModeException    Thrown if the system is in "readAll-only" mode
	 */
	DebugInfo add(RequestProperties properties, CommentType type, Long referenceId, String description) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	/**
	 * Returns a list of {@link CommentType}s for the given {@link GerminateDatabaseTable}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param reference  The {@link GerminateDatabaseTable} specifying the reference table
	 * @return A list of {@link CommentType}s for the given {@link GerminateDatabaseTable}
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<CommentType>> getTypes(RequestProperties properties, GerminateDatabaseTable reference) throws InvalidSessionException, DatabaseException;

	/**
	 * Disables the given {@link Comment} in the database so that it'll no longer be shown.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param comment    The {@link Comment} to disable
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to complete this operation
	 * @throws SystemInReadOnlyModeException    Thrown if the system is in "readAll-only" mode
	 */
	void disable(RequestProperties properties, Comment comment) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException;

	PaginatedServerResult<List<Comment>> getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination) throws InvalidSessionException, InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException;
}
