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

import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * Async version of {@link NewsService}.
 *
 * @author Sebastian Raubach
 */
public interface NewsServiceAsync
{
	/**
	 * Returns a list of {@link News}.
	 *
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	void get(Pagination pagination, AsyncCallback<PaginatedServerResult<List<News>>> callback);

	/**
	 * Returns the position of the given {@link News} item.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param newsId     The news id
	 * @param callback   The {@link AsyncCallback}
	 */
	void getPosition(RequestProperties properties, Long newsId, AsyncCallback<ServerResult<Integer>> callback);

	/**
	 * Returns the latest project {@link News}.
	 *
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getProjects(Pagination pagination, AsyncCallback<ServerResult<List<News>>> callback);
}
