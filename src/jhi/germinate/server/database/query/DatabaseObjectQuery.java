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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class DatabaseObjectQuery<T extends DatabaseObject> extends GerminateQuery<DatabaseObjectQuery<T>>
{
	private static final String CALC_ROWS = "SELECT SQL_CALC_FOUND_ROWS ";

	private UserAuth auth          = null;
	private Integer  previousCount = -1;

	public DatabaseObjectQuery(RequestProperties requestProperties, BaseRemoteServiceServlet servlet, String query) throws InvalidSessionException, DatabaseException
	{
		super(Database.DatabaseType.MYSQL, Database.QueryType.DATA, requestProperties, servlet, query);

		auth = UserAuth.getFromSession(servlet, requestProperties);
	}

	public DatabaseObjectQuery(String query, UserAuth userAuth) throws DatabaseException
	{
		super(Database.DatabaseType.MYSQL, Database.QueryType.DATA, query);
		this.auth = userAuth;
	}

	public DatabaseObjectQuery(Database.QueryType type, String query, UserAuth userAuth) throws DatabaseException
	{
		super(Database.DatabaseType.MYSQL, type, query);
		this.auth = userAuth;
	}

	/**
	 * Runs the current query on the database. The result can be requested by calling any of the <code>getDatatype</code> methods.
	 *
	 * @return this
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public ExecutedDatabaseObjectQuery run() throws DatabaseException
	{
		return new ExecutedDatabaseObjectQuery(database, stmt, previousCount);
	}

	public DatabaseObjectStreamer getStreamer(DatabaseObjectParser<T> parser, UserAuth user, boolean foreignKeysFromResult) throws DatabaseException
	{
		return new DatabaseObjectStreamer<>(database, sqlDebug, stmt, parser, user, foreignKeysFromResult);
	}

	public DatabaseObjectQuery<T> setFetchesCount(Integer previousCount) throws DatabaseException
	{
		this.previousCount = previousCount;

		if (previousCount == null && !query.contains(CALC_ROWS))
		{
			query = query.replaceFirst("SELECT ", CALC_ROWS);

			this.stmt = database.prepareStatement(query);
		}

		return this;
	}

	public class ExecutedDatabaseObjectQuery
	{
		private final Database          database;
		private final DatabaseStatement stmt;
		private final DatabaseResult    rs;
		private Integer previousCount = null;

		public ExecutedDatabaseObjectQuery(Database database, DatabaseStatement stmt, Integer previousCount) throws DatabaseException
		{
			this.database = database;
			this.stmt = stmt;
			this.previousCount = previousCount;
			this.rs = stmt.query();
		}

		public ServerResult<T> getObject(DatabaseObjectParser<T> parser) throws DatabaseException
		{
			return getObject(parser, false);
		}

		public ServerResult<T> getObject(DatabaseObjectParser<T> parser, boolean foreignsFromResultSet) throws DatabaseException
		{
			sqlDebug.add(stmt.getStringRepresentation());
			checkResultSet();
			if (rs.next())
			{
				T obj = parser.parse(rs, auth, foreignsFromResultSet);
				database.close();
				return new ServerResult<>(sqlDebug, obj);
			}
			else
			{
				database.close();
				return new ServerResult<>(sqlDebug, null);
			}
		}

		public ServerResult<List<T>> getObjects(DatabaseObjectParser<T> parser) throws DatabaseException
		{
			return getObjects(parser, false);
		}

		public PaginatedServerResult<List<T>> getObjectsPaginated(DatabaseObjectParser<T> parser) throws DatabaseException
		{
			return getObjectsPaginated(parser, false);
		}

		public ServerResult<List<T>> getObjects(DatabaseObjectParser<T> parser, boolean foreignsFromResultSet) throws DatabaseException
		{
			sqlDebug.add(stmt.getStringRepresentation());
			checkResultSet();
			List<T> result = null;

			parser.clearCache();

			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				T object = parser.parse(rs, auth, foreignsFromResultSet);

				if (object != null)
					result.add(object);
			}

			parser.clearCache();

			database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		public PaginatedServerResult<List<T>> getObjectsPaginated(DatabaseObjectParser<T> parser, boolean foreignsFromResultSet) throws DatabaseException
		{
			sqlDebug.add(stmt.getStringRepresentation());
			checkResultSet();
			List<T> result = null;

			parser.clearCache();

			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				T object = parser.parse(rs, auth, foreignsFromResultSet);

				if (object != null)
					result.add(object);
			}

			parser.clearCache();

			Integer count = previousCount == null ? stmt.getCount() : previousCount;

			database.close();
			return new PaginatedServerResult<>(sqlDebug, result, count);
		}

		/**
		 * Checks if the {@link java.sql.ResultSet} is not <code>null</code>.
		 *
		 * @throws DatabaseException Thrown if the {@link java.sql.ResultSet} is <code>null</code>.
		 */
		private void checkResultSet() throws DatabaseException
		{
			if (rs == null)
			{
				database.close();
				throw new DatabaseException("You need to run the query before requesting result values!");
			}
		}

		/**
		 * Returns true if there is a result (Note that you cannot get the result after this is called. It's simply a method that you call when you
		 * want to know IF there is a result, not if you want to know WHAT the result is.
		 *
		 * @return True if there is a result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method
		 */
		public boolean hasNext() throws DatabaseException
		{
			checkResultSet();
			return rs.next();
		}
	}
}
