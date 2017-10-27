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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * A {@link ValueQuery} can either be used to get a single value from the database or to execute a query (that is, a query without a result). This
 * class supports "fluent" code style.
 *
 * @author Sebastian Raubach
 */
public class ValueQuery extends GerminateQuery<ValueQuery>
{
	/**
	 * Creates a new {@link ValueQuery} for the given query, but using the given {@link Database} connection
	 *
	 * @param database The {@link Database} object
	 * @param query    The sql query
	 */
	public ValueQuery(Database database, String query)
	{
		super(database, query);
	}

	/**
	 * Creates a new {@link ValueQuery} for the given query
	 *
	 * @param query The sql query
	 */
	public ValueQuery(String query)
	{
		super(query);
	}

	/**
	 * Creates a new {@link ValueQuery} for the given query
	 *
	 * @param query    The sql query
	 * @param userAuth The {@link UserAuth} object containing the user information
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public ValueQuery(String query, UserAuth userAuth)
	{
		super(query, userAuth);
	}

	/**
	 * Executes the current query on the database. The returned result contains the generated ids (if any). Use {@link ValueQuery#run(String)} to run
	 * the query and get the result.
	 *
	 * @return The {@link DebugInfo} of this instance and the generated ids (if any)
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public ServerResult<List<Long>> execute() throws DatabaseException
	{
		return execute(true);
	}

	/**
	 * Executes the current query on the database. The returned result contains the generated ids (if any). Use {@link ValueQuery#run(String)} to run
	 * the query and get the result.
	 *
	 * @return The {@link DebugInfo} of this instance and the generated ids (if any)
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public ServerResult<List<Long>> execute(boolean closeConnection) throws DatabaseException
	{
		init();
		sqlDebug.add(stmt.getStringRepresentation());
		List<Long> ids = stmt.execute();

		if (closeConnection)
			database.close();

		return new ServerResult<>(sqlDebug, ids);
	}

	/**
	 * Runs the current query on the database. The result can be requested by calling any of the <code>getDatatype</code> methods.
	 *
	 * @param column The column to extract
	 * @return this
	 * @throws DatabaseException Thrown if the communication with the database fails.
	 */
	public ExecutedValueQuery run(String column) throws DatabaseException
	{
		init();
		sqlDebug.add(stmt.getStringRepresentation());

		return new ExecutedValueQuery(column, database, stmt.query(), sqlDebug);
	}

	/**
	 * {@link ExecutedValueQuery} is a {@link ValueQuery} that has been executed (duh). It can only be used to retrieve information.
	 *
	 * @author Sebastian Raubach
	 */
	public static class ExecutedValueQuery
	{
		private final String         column;
		private final Database       database;
		private final DatabaseResult rs;
		private final DebugInfo      sqlDebug;
		private boolean closeConnection = true;

		private ExecutedValueQuery(String column, Database database, DatabaseResult rs, DebugInfo sqlDebug)
		{
			this.column = column;
			this.database = database;
			this.rs = rs;
			this.sqlDebug = sqlDebug;
		}

		public ExecutedValueQuery setCloseConnection(boolean closeConnection)
		{
			this.closeConnection = closeConnection;
			return this;
		}

		/**
		 * Requests the String result value from the query
		 *
		 * @return The {@link DebugInfo} and the String result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<String> getString() throws DatabaseException
		{
			checkResultSet();
			if (rs.next())
			{
				String value = rs.getString(column);
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, value);
			}
			else
			{
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, null);
			}
		}

		/**
		 * Requests the String result values from the query
		 *
		 * @return The {@link DebugInfo} and the String results
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<List<String>> getStrings() throws DatabaseException
		{
			checkResultSet();
			List<String> result = null;
			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				result.add(rs.getString(column));
			}

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the Long result value from the query
		 *
		 * @return The {@link DebugInfo} and the Long result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<Long> getLong() throws DatabaseException
		{
			checkResultSet();
			if (rs.next())
			{
				Long value = rs.getLong(column);
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, value);
			}
			else
			{
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, null);
			}
		}

		/**
		 * Requests the int result value from the query
		 *
		 * @param fallback This value will be returned if there is no result
		 * @return The {@link DebugInfo} and the int result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method
		 */
		public ServerResult<Long> getLong(Long fallback) throws DatabaseException
		{
			checkResultSet();
			Long result = fallback;

			if (rs.next())
				result = rs.getLong(column);

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);

		}

		/**
		 * Requests the String result values from the query
		 *
		 * @return The {@link DebugInfo} and the String results
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<List<Long>> getLongs() throws DatabaseException
		{
			checkResultSet();
			List<Long> result = null;
			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				result.add(rs.getLong(column));
			}

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the String result value from the query
		 *
		 * @param fallback This value will be returned if there is no result
		 * @return The {@link DebugInfo} and the String result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method
		 */
		public ServerResult<String> getString(String fallback) throws DatabaseException
		{
			checkResultSet();
			String result = rs.next() ? rs.getString(column) : fallback;
			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the int result value from the query
		 *
		 * @return The {@link DebugInfo} and the int result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<Integer> getInt() throws DatabaseException
		{
			checkResultSet();
			if (rs.next())
			{
				Integer value = rs.getInt(column);
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, value);
			}
			else
			{
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, null);
			}
		}

		/**
		 * Requests the int result values from the query
		 *
		 * @return The {@link DebugInfo} and the int results
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<List<Integer>> getInts() throws DatabaseException
		{
			checkResultSet();
			List<Integer> result = null;
			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				result.add(rs.getInt(column));
			}

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the int result value from the query
		 *
		 * @param fallback This value will be returned if there is no result
		 * @return The {@link DebugInfo} and the int result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method
		 */
		public ServerResult<Integer> getInt(int fallback) throws DatabaseException
		{
			checkResultSet();
			Integer result = fallback;

			if (rs.next())
				result = rs.getInt(column);

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);

		}

		/**
		 * Requests the double result value from the query
		 *
		 * @return The {@link DebugInfo} and the double result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<Double> getDouble() throws DatabaseException
		{
			checkResultSet();
			if (rs.next())
			{
				Double value = rs.getDouble(column);
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, value);
			}
			else
			{
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, null);
			}
		}

		/**
		 * Requests the double result values from the query
		 *
		 * @return The {@link DebugInfo} and the double results
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<List<Double>> getDoubles() throws DatabaseException
		{
			checkResultSet();
			List<Double> result = null;
			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				result.add(rs.getDouble(column));
			}

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the double result value from the query
		 *
		 * @param fallback This value will be returned if there is no result
		 * @return The {@link DebugInfo} and the double result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method
		 */
		public ServerResult<Double> getDouble(double fallback) throws DatabaseException
		{
			checkResultSet();
			Double result = fallback;

			if (rs.next())
				result = rs.getDouble(column);

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the boolean result value from the query
		 *
		 * @return The {@link DebugInfo} and the boolean result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<Boolean> getBoolean() throws DatabaseException
		{
			checkResultSet();
			if (rs.next())
			{
				boolean value = rs.getBoolean(column);
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, value);
			}
			else
			{
				if (closeConnection) database.close();
				return new ServerResult<>(sqlDebug, null);
			}
		}

		/**
		 * Requests the boolean result values from the query
		 *
		 * @return The {@link DebugInfo} and the boolean results
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method or if there is no result
		 */
		public ServerResult<List<Boolean>> getBooleans() throws DatabaseException
		{
			checkResultSet();
			List<Boolean> result = null;
			while (rs.next())
			{
				if (result == null)
					result = new ArrayList<>();

				result.add(rs.getBoolean(column));
			}

			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Requests the boolean result value from the query
		 *
		 * @param fallback This value will be returned if there is no result
		 * @return The {@link DebugInfo} and the boolean result
		 * @throws DatabaseException Thrown if {@link ValueQuery#run(String)} wasn't called before calling this method
		 */
		public ServerResult<Boolean> getBoolean(boolean fallback) throws DatabaseException
		{
			checkResultSet();
			boolean result = rs.next() ? rs.getBoolean(column) : fallback;
			if (closeConnection) database.close();
			return new ServerResult<>(sqlDebug, result);
		}

		/**
		 * Returns an array of all the column names in the {@link java.sql.ResultSet}
		 *
		 * @return An array of all the column names in the {@link java.sql.ResultSet}
		 * @throws DatabaseException if a database access error occurs
		 */
		public ServerResult<List<String>> getColumnNames() throws DatabaseException
		{
			return new ServerResult<>(sqlDebug, Arrays.asList(rs.getColumnNames()));
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
				if (closeConnection) database.close();
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

		public void close()
		{
			database.close();
		}
	}
}
