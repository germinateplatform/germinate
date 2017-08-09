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

package jhi.germinate.server.database.query;

import jhi.germinate.server.database.Database.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * A {@link DefaultQuery} is a query that will return a {@link GerminateTable} as its result. This class supports "fluent" code style.
 *
 * @author Sebastian Raubach
 */
public class DefaultQuery extends GerminateQuery<DefaultQuery>
{
	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Checks the session id for validity.
	 *
	 * @param databaseType      The {@link DatabaseType}
	 * @param queryType         The {@link QueryType}
	 * @param requestProperties The {@link RequestProperties}
	 * @param servlet           The {@link BaseRemoteServiceServlet}
	 * @param query             The sql query
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the communication with the database fails
	 */
	public DefaultQuery(DatabaseType databaseType, QueryType queryType, RequestProperties requestProperties, BaseRemoteServiceServlet servlet, String query) throws InvalidSessionException,
			DatabaseException
	{
		super(databaseType, queryType, requestProperties, servlet, query);
	}

	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Checks the session id for validity.
	 *
	 * @param databaseType      The {@link DatabaseType}
	 * @param requestProperties The {@link RequestProperties}
	 * @param servlet           The {@link BaseRemoteServiceServlet}
	 * @param query             The sql query
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the communication with the database fails
	 */
	public DefaultQuery(DatabaseType databaseType, RequestProperties requestProperties, BaseRemoteServiceServlet servlet, String query) throws InvalidSessionException, DatabaseException
	{
		this(databaseType, QueryType.DATA, requestProperties, servlet, query);
	}

	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Checks the session id for validity.
	 *
	 * @param requestProperties The {@link RequestProperties}
	 * @param servlet           The {@link BaseRemoteServiceServlet}
	 * @param query             The sql query
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the communication with the database fails
	 */
	public DefaultQuery(RequestProperties requestProperties, BaseRemoteServiceServlet servlet, String query) throws InvalidSessionException, DatabaseException
	{
		this(DatabaseType.MYSQL, requestProperties, servlet, query);
	}

	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Does NOT check the session id for validity.
	 *
	 * @param databaseType The {@link DatabaseType}
	 * @param queryType    The {@link QueryType}
	 * @param query        The sql query
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public DefaultQuery(DatabaseType databaseType, QueryType queryType, String query) throws DatabaseException
	{
		super(databaseType, queryType, query);
	}

	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Does NOT check the session id for validity.
	 *
	 * @param databaseType The {@link DatabaseType}
	 * @param query        The sql query
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public DefaultQuery(DatabaseType databaseType, String query) throws DatabaseException
	{
		this(databaseType, QueryType.DATA, query);
	}

	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Does NOT check the session id for validity.
	 *
	 * @param queryType The {@link QueryType}
	 * @param query     The sql query
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public DefaultQuery(QueryType queryType, String query) throws DatabaseException
	{
		this(DatabaseType.MYSQL, queryType, query);
	}

	/**
	 * Creates a new {@link DefaultQuery} with the given query and column names to extract. Does NOT check the session id for validity.
	 *
	 * @param query The sql query
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public DefaultQuery(String query) throws DatabaseException
	{
		this(DatabaseType.MYSQL, query);
	}

	/**
	 * Returns the {@link GerminateTableStreamer} to stream {@link GerminateRow} s
	 *
	 * @return The {@link GerminateTableStreamer} to stream {@link GerminateRow} s
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public DefaultStreamer getStreamer() throws DatabaseException
	{
		return new DefaultStreamer(database, sqlDebug, stmt);
	}

	public DatabaseResult getResult() throws DatabaseException
	{
		return stmt.query();
	}
}
