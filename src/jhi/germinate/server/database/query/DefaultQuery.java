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

package jhi.germinate.server.database.query;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * A {@link DefaultQuery} is a query that will return a {@link DatabaseResult} as its result.
 *
 * @author Sebastian Raubach
 */
public class DefaultQuery extends GerminateQuery<DefaultQuery>
{
	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Checks the session id for validity.
	 *
	 * @param query    The sql query
	 * @param userAuth The {@link UserAuth} object containing the user information
	 */
	public DefaultQuery(String query, UserAuth userAuth)
	{
		super(query, userAuth);
	}

	/**
	 * Returns the {@link DefaultStreamer} to stream {@link DatabaseResult}s
	 *
	 * @return The {@link DefaultStreamer} to stream {@link DatabaseResult}s
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public DefaultStreamer getStreamer() throws DatabaseException
	{
		init();
		return new DefaultStreamer(database, sqlDebug, stmt);
	}

	public DatabaseResult getResult() throws DatabaseException
	{
		init();
		return stmt.query();
	}
}
