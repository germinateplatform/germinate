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

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.Database.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link GerminateQuery} is an abstract class containing all the base methods to build a query. <p/> <b>IMPORTANT</b>: When subclassing this class,
 * make sure to do it in this fashion: <p/> <code> class YourClass extends GerminateQuery&lt;YourClass&gt; </code> <p/> Otherwise you WILL end up with
 * a {@link ClassCastException}. <p/> Thanks to <a href="http://stackoverflow.com/a/14105328/1740724">this</a> answer on StackOverflow for the idea.
 *
 * @param <T> The type of the extending subclass
 * @author Sebastian Raubach
 */
public abstract class GerminateQuery<T extends GerminateQuery<?>>
{
	protected String            query;
	protected DatabaseStatement stmt;
	protected DebugInfo         sqlDebug;
	protected QueryType queryType = QueryType.DATA;
	protected int       i         = 1;
	protected Database  database  = null;
	protected UserAuth userAuth;

	GerminateQuery(Database database, String query)
	{
		this.database = database;
		this.query = query;
	}

	GerminateQuery(String query, UserAuth userAuth)
	{
		this.userAuth = userAuth;
		this.query = query;
	}

	GerminateQuery(String query)
	{
		this.query = query;
	}

	protected void init() throws DatabaseException
	{
		if (database == null)
		{
			/* Connect to the database WITHOUT checking the session id */
			switch (queryType)
			{
				case AUTHENTICATION:
					database = Database.connect(DatabaseType.MYSQL, PropertyReader.getServerStringForAuthentication(), PropertyReader.get(ServerProperty.DATABASE_USERNAME),
							PropertyReader.get(ServerProperty.DATABASE_PASSWORD));
					break;

				case DATA:
				default:
					database = Database.connect(DatabaseType.MYSQL);
					break;
			}

			this.stmt = database.prepareStatement(query);

        	/* Check if debugging is activated */
			sqlDebug = DebugInfo.create(userAuth);
		}
	}

	public T setQueryType(QueryType queryType)
	{
		this.queryType = queryType;
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The int value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setInt(Integer value) throws DatabaseException
	{
		init();
		stmt.setInt(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholders with the given values
	 *
	 * @param values The int values to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setInts(Collection<Integer> values) throws DatabaseException
	{
		init();
		for (Integer value : values)
			setInt(value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The long value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setLong(Long value) throws DatabaseException
	{
		init();
		stmt.setLong(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholders with the given values
	 *
	 * @param values The int values to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setLongs(Collection<Long> values) throws DatabaseException
	{
		init();
		for (Long value : values)
			setLong(value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The String value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setString(String value) throws DatabaseException
	{
		init();
		stmt.setString(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholders with the given values
	 *
	 * @param values The String values to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setStrings(Collection<String> values) throws DatabaseException
	{
		init();
		for (String value : values)
			setString(value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The double value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setDouble(Double value) throws DatabaseException
	{
		init();
		stmt.setDouble(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The date value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setDate(Date value) throws DatabaseException
	{
		init();
		stmt.setDate(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The date value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setTimestamp(Date value) throws DatabaseException
	{
		init();
		stmt.setTimestamp(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholders with the given values
	 *
	 * @param values The Double values to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setDoubles(Collection<Double> values) throws DatabaseException
	{
		init();
		for (Double value : values)
			setDouble(value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholder with the given value
	 *
	 * @param value The boolean value to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setBoolean(boolean value) throws DatabaseException
	{
		init();
		stmt.setBoolean(i++, value);
		return (T) this;
	}

	/**
	 * Replaces the next placeholders with the given values
	 *
	 * @param values The Boolean values to set
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setBooleans(Collection<Boolean> values) throws DatabaseException
	{
		init();
		for (Boolean value : values)
			setBoolean(value);
		return (T) this;
	}

	/**
	 * Sets the next placeholder to null of the given {@link Types} instance.
	 *
	 * @param type The {@link Types} instance
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setNull(int type) throws DatabaseException
	{
		init();
		stmt.setNull(i++, type);
		return (T) this;
	}

	/**
	 * Sets the next placeholders to null of the given {@link Types} instances.
	 *
	 * @param types The {@link Types} instances
	 * @return this
	 * @throws DatabaseException Thrown if the query fails with a {@link SQLException}
	 */
	public T setNull(Collection<Integer> types) throws DatabaseException
	{
		init();
		for (Integer type : types)
			setNull(type);
		return (T) this;
	}

	/**
	 * Adds the given {@link DebugInfo} to the internal one.
	 *
	 * @param info Previous {@link DebugInfo} to add to this instance
	 * @return this
	 */
	public T addDebugInfo(DebugInfo info)
	{
		sqlDebug.addAll(info);
		return (T) this;
	}

	public T printTo(PrintStream out)
	{
		out.println(getStringRepresentation());
		return (T) this;
	}

	public T printTo(Logger logger)
	{
		logger.log(Level.INFO, getStringRepresentation());
		return (T) this;
	}

	/**
	 * Returns the {@link String} representation of the {@link DatabaseStatement}. Depending on the database, this will either return the actual built
	 * query or just some other string representation. <p/> MySQL returns the actual query.
	 *
	 * @return The string representation of the contained {@link DatabaseStatement}
	 */
	public String getStringRepresentation()
	{
		return stmt.getStringRepresentation();
	}

	public boolean isClosed()
	{
		return database.isClosed();
	}

	public void close()
	{
		if (!database.isClosed())
			database.close();
	}
}
