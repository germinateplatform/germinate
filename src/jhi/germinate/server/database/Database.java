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

package jhi.germinate.server.database;

import com.microsoft.sqlserver.jdbc.*;
import com.mysql.jdbc.exceptions.jdbc4.*;

import java.sql.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link Database} is a basic implementation of a database connection.
 *
 * @author Sebastian Raubach
 */
public final class Database
{
	private Connection connection;

	/* Initialize the stored procedures and views */
	public static void initialize()
	{
		new DatabaseUpdater().initialize();
		new StoredProcedureInitializer().initialize();
		new ViewInitializer().initialize();
	}

	public void setAutoCommit(boolean autoCommit) throws DatabaseException
	{
		try
		{
			connection.setAutoCommit(autoCommit);
		}
		catch (SQLException e)
		{
			throw new DatabaseException(e);
		}
	}

	public boolean getAutoCommit() throws DatabaseException
	{
		try
		{
			return connection.getAutoCommit();
		}
		catch (SQLException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * The set of query types supported by Germinate. <p/> {@link #AUTHENTICATION} should be used for queries to Gatekeeper. <p/> {@link #DATA} should
	 * be used for all other queries.
	 *
	 * @author Sebastian Raubach
	 */
	public enum QueryType
	{
		AUTHENTICATION,
		DATA
	}

	/**
	 * The set of database types supported by Germinate
	 *
	 * @author Sebastian Raubach
	 */
	public enum DatabaseType
	{
		MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://", "?useSSL=false");
//		MYSQL_BATCH_ENABLED("com.mysql.jdbc.Driver", "jdbc:mysql://", "?rewriteBatchedStatements=true");

		private final String classForName;
		private final String connectionString;
		private final String optionalParameters;

		DatabaseType(String classForName, String connectionString, String optionalParameters)
		{
			this.classForName = classForName;
			this.connectionString = connectionString;
			this.optionalParameters = optionalParameters;
		}

		public String getClassForName()
		{
			return classForName;
		}

		public String getConnectionString()
		{
			return connectionString;
		}

		public String getOptionalParameters()
		{
			return optionalParameters;
		}
	}

	private Database()
	{
	}

	/**
	 * Creates and returns a prepared DatabaseStatement
	 *
	 * @param sql The String representation of the sql query
	 * @return The Prepared Statement
	 * @throws DatabaseException Thrown if any kind of {@link Exception} is thrown while trying to connect. The {@link DatabaseException} will contain
	 *                           the message of the original {@link Exception}.
	 */
	public final DatabaseStatement prepareStatement(String sql) throws DatabaseException
	{
		try
		{
			PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			// stmt.setQueryTimeout(120);
			return new DatabaseStatement(stmt, this);
		}
		catch (SQLException e)
		{
			throw new DatabaseException(e);
		}
	}

	/**
	 * Connects to the given MySQL database with specified credentials
	 *
	 * @param type     The {@link DatabaseType}
	 * @param dbPath   The location of the database
	 * @param username The username
	 * @param password The password
	 * @return The {@link Database} instance
	 * @throws DatabaseException Thrown if any kind of {@link Exception} is thrown while trying to connect. The {@link DatabaseException} will contain
	 *                           the message of the original {@link Exception}.
	 */
	public static Database connect(DatabaseType type, String dbPath, String username, String password) throws DatabaseException
	{
		Database database = new Database();

        /* Connect to the database */
		try
		{
			Class.forName(type.classForName).newInstance();
			String url = type.connectionString + dbPath + (type.optionalParameters != null ? type.optionalParameters : "");
			database.connection = DriverManager.getConnection(url, username, password);
		}
		catch (CommunicationsException e)
		{
			throw new DatabaseException("Unable to connect to the database: " + e.getLocalizedMessage());
		}
		catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
		{
			throw new DatabaseException("Unable to instantiate MySQL connection: " + e.getLocalizedMessage());
		}
		catch (SQLServerException e)
		{
			throw new DatabaseException(e);
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 1044 && e.getSQLState().equals("42000"))
				throw new DatabaseException(e.getLocalizedMessage().replace(PropertyReader.get(ServerProperty.DATABASE_USERNAME), "<USERNAME>"));
			else
				throw new DatabaseException(e);
		}

		return database;
	}

	/**
	 * Closes the connection to the database
	 */
	public final void close()
	{
		try
		{
			if (connection != null)
				connection.close();
			connection = null;
		}
		catch (SQLException e)
		{
			/* Do nothing here! */
		}
	}

	/**
	 * Checks if the {@link Database} is closed
	 *
	 * @return <code>true</code> if the {@link Database} has already been closed.
	 */
	public final boolean isClosed()
	{
		try
		{
			return connection == null || connection.isClosed();
		}
		catch (SQLException e)
		{
			return true;
		}
	}

	/**
	 * Checks the current session and connects to the database if the session is still valid
	 *
	 * @param requestProperties The {@link RequestProperties}
	 * @param servlet           The {@link BaseRemoteServiceServlet}
	 * @return The new database object
	 * @throws InvalidSessionException Thrown if the current session is not valid
	 * @throws DatabaseException       Thrown if the interaction with the database fails
	 */
	public static Database connectAndCheckSession(RequestProperties requestProperties, BaseRemoteServiceServlet servlet) throws InvalidSessionException, DatabaseException
	{
		return connectAndCheckSession(DatabaseType.MYSQL, requestProperties, servlet);
	}

	/**
	 * Checks the current session and connects to the database if the session is still valid
	 *
	 * @param type              The {@link DatabaseType}
	 * @param requestProperties The {@link RequestProperties}
	 * @param servlet           The {@link BaseRemoteServiceServlet}
	 * @return The new database object
	 * @throws InvalidSessionException Thrown if the current session is not valid
	 * @throws DatabaseException       Thrown if the interaction with the database fails
	 */
	public static Database connectAndCheckSession(DatabaseType type, RequestProperties requestProperties, BaseRemoteServiceServlet servlet) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(requestProperties, servlet);

        /* Connect to the database */
		return connect(type);
	}

	/**
	 * Connects to the Germinate database without checking the session
	 *
	 * @return The new database object
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static Database connect() throws DatabaseException
	{
		return connect(DatabaseType.MYSQL);
	}

	/**
	 * Connects to the Germinate database without checking the session
	 *
	 * @param type The {@link DatabaseType}
	 * @return The new database object
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static Database connect(DatabaseType type) throws DatabaseException
	{
		switch (type)
		{
			case MYSQL:
				return connect(type, PropertyReader.getServerString(type), PropertyReader.get(ServerProperty.DATABASE_USERNAME), PropertyReader.get(ServerProperty.DATABASE_PASSWORD));
			default:
				throw new InvalidDatabaseTypeException();
		}
	}
}
