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
import jhi.germinate.shared.exception.*;

/**
 * {@link NewsService} is a {@link RemoteService} providing methods to retrieve latest news.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("news")
public interface NewsService extends RemoteService
{
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
			private static final NewsServiceAsync INSTANCE = GWT.create(NewsService.class);
		}

		public static NewsServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a list of {@link News}.
	 *
	 * @param pagination The {@link Pagination}
	 * @return A list of {@link News}.
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	PaginatedServerResult<List<News>> get(Pagination pagination) throws DatabaseException;

	/**
	 * Returns the position of the given {@link News} item.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param newsId     The news id
	 * @return The position of the given {@link News} item.
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	ServerResult<Integer> getPosition(RequestProperties properties, Long newsId) throws DatabaseException;

	/**
	 * Returns the latest project {@link News}.
	 *
	 * @param pagination The {@link Pagination}
	 * @return The latest project {@link News}.
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	ServerResult<List<News>> getProjects(Pagination pagination) throws DatabaseException;
}
