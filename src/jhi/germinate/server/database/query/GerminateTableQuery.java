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

import jhi.germinate.server.database.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * A {@link GerminateTableQuery} is a query that will return a {@link GerminateTable} as its result. This class supports "fluent" code style.
 *
 * @author Sebastian Raubach
 */
public class GerminateTableQuery extends GerminateQuery<GerminateTableQuery>
{
	private String[] columnNames;

	public GerminateTableQuery(String query, UserAuth userAuth, String[] columnNames)
	{
		super(query, userAuth);
		this.columnNames = columnNames;
	}

	/**
	 * Creates a new {@link GerminateTableQuery} with the given query and column names to extract. Does NOT check the session id for validity.
	 *
	 * @param query       The sql query
	 * @param columnNames The column names to extract
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public GerminateTableQuery(String query, String[] columnNames)
	{
		super(query);
		this.columnNames = columnNames;
	}

	/**
	 * Returns the {@link GerminateTable} retrieved from the database
	 *
	 * @return The {@link GerminateTable} retrieved from the database
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public ServerResult<GerminateTable> run() throws DatabaseException
	{
		init();
		sqlDebug.add(stmt.getStringRepresentation());

		GerminateTable result = stmt.runQuery(columnNames);

		database.close();

		return new ServerResult<>(sqlDebug, result);
	}

	/**
	 * Returns the {@link GerminateTableStreamer} to stream {@link GerminateRow} s
	 *
	 * @return The {@link GerminateTableStreamer} to stream {@link GerminateRow} s
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public GerminateTableStreamer getStreamer() throws DatabaseException
	{
		init();
		return new GerminateTableStreamer(database, sqlDebug, stmt, columnNames);
	}

	public DatabaseResult getResult() throws DatabaseException
	{
		init();
		return stmt.query();
	}
}
