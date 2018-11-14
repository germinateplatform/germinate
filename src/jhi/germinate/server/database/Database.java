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

import java.sql.*;

import jhi.germinate.server.util.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
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
	private static DatabaseType type = DatabaseType.MYSQL;
	private static String       server;
	private static String       database;
	private static String       port;
	private static String       username;
	private static String       password;
	private        Connection   connection;

	/* Initialize the stored procedures and views */
	public static void initialize()
	{
		new DatabaseUpdater().initialize();
		new StoredProcedureInitializer().initialize();
		new ViewInitializer().initialize();
	}

	private Database()
	{
	}

	public Connection getConnection()
	{
		return connection;
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
			String url = type.getUrl(dbPath);
			database.connection = DriverManager.getConnection(url, username, password);
		}
		catch (IllegalAccessException | InstantiationException | ClassNotFoundException e)
		{
			throw new DatabaseException("Unable to instantiate MySQL connection: " + e.getLocalizedMessage());
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 1044 && e.getSQLState().equals("42000"))
				throw new DatabaseException(e.getLocalizedMessage().replace(PropertyWatcher.get(ServerProperty.DATABASE_USERNAME), "<USERNAME>"));
			else
				throw new DatabaseException(e);
		}

		return database;
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
		return connectAndCheckSession(type, requestProperties, servlet);
	}

	/**
	 * The set of database types supported by Germinate
	 *
	 * @author Sebastian Raubach
	 */
	public enum DatabaseType
	{
		MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://", "?useSSL=false&allowPublicKeyRetrieval=true"),
		MYSQL_DATA_IMPORT("com.mysql.cj.jdbc.Driver", "jdbc:mysql://", "?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8");
		//		MYSQL_BATCH_ENABLED("com.mysql.cj.jdbc.Driver", "jdbc:mysql://", "?rewriteBatchedStatements=true");

		private final String classForName;
		private final String connectionString;
		private final String optionalParameters;

		DatabaseType(String classForName, String connectionString, String optionalParameters)
		{
			this.classForName = classForName;
			this.connectionString = connectionString;
			this.optionalParameters = optionalParameters;
		}

		public String getUrl(String dbPath)
		{
			return connectionString + dbPath + (optionalParameters != null ? optionalParameters : "");
		}
	}

	/**
	 * Connects to the Germinate database without checking the session
	 *
	 * @return The new database object
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static Database connect() throws DatabaseException
	{
		return connect(type);
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
			case MYSQL_DATA_IMPORT:
				return connect(type, getServerString(type), username, password);
			default:
				throw new InvalidDatabaseTypeException();
		}
	}

	public static void setDefaults(DatabaseType type, String server, String database, String port, String username, String password)
	{
		Database.type = type;
		Database.server = server;
		Database.database = database;
		Database.port = port;
		Database.username = username;
		Database.password = password;
	}

	/**
	 * Returns a String of the form &lt;SERVER&gt;:&lt;PORT&gt;/&lt;DATABASE&gt;
	 *
	 * @param type The {@link DatabaseType} of the server
	 * @return A String of the form &lt;SERVER&gt;:&lt;PORT&gt;/&lt;DATABASE&gt;
	 * @throws InvalidDatabaseTypeException Thrown if the requested {@link DatabaseType} is invalid
	 */
	public static String getServerString(DatabaseType type) throws InvalidDatabaseTypeException
	{
		switch (type)
		{
			case MYSQL:
			case MYSQL_DATA_IMPORT:
				if (!StringUtils.isEmpty(port))
				{
					return server + ":" + port + "/" + database;
				}
				else
				{
					return server + "/" + database;
				}
			default:
				throw new InvalidDatabaseTypeException("Invalid database type: " + type);
		}
	}
}
