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

package jhi.germinate.util.importer.reader;

import org.apache.commons.cli.*;
import org.apache.poi.openxml4j.util.*;

import java.io.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DataImporter<T>
{
	protected Database databaseConnection;

	/**
	 * If the reader passed to the {@link #run(File, String, String, String, String, String)} method cannot be instantiated, this fallback
	 * reader will be used to import the data instead.
	 *
	 * @return
	 */
	protected abstract IDataReader getReader();

	/**
	 * This method is called when an error occurs during the data export. Deletes the items that have been inserted before the error occurred.
	 */
	protected abstract void deleteInsertedItems();

	/**
	 * Writes the given object to the database.
	 *
	 * @param object The object to write to the database
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	protected abstract void write(T object) throws DatabaseException;

	protected void flush() throws DatabaseException
	{
	}

	private String server;
	private String database;
	private String username;
	private String password;
	private String port;

	protected void run(String[] args)
	{
		ReaderOptions options = new ReaderOptions()
				.withInputFile(true)
				.withServer(true)
				.withDatabase(true)
				.withUsername(true)
				.withPassword(false)
				.withPort(false);

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			run(options.getInput(line),
					options.getServer(line),
					options.getDatabase(line),
					options.getUsername(line),
					options.getPassword(line),
					options.getPort(line));
		}
		catch (Exception e)
		{
			options.printHelp("DataImporter");

			System.exit(1);
		}
	}

	protected void prepareReader(IDataReader reader)
	{

	}

	/**
	 * Starts the import process using the given database access parameters and the name of the {@link IDataReader}.
	 *
	 * @param input    The input file containing the actual data
	 * @param server   The database server
	 * @param database The database name
	 * @param username The database username
	 * @param password The database password
	 * @param port     The port or <code>null</code>
	 */
	public void run(File input, String server, String database, String username, String password, String port)
	{
		this.server = server;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;

		BaseException.printExceptions = true;

		// We're going to assume that the files that are being imported are not Zip bombs, so we reduce the threshold.
		ZipSecureFile.setMinInflateRatio(0.001);

		// Use the #getReader() as a fallback.
		try (IDataReader reader = getReader())
		{
			try
			{
				// Connect to the database
				databaseConnection = Database.connect(Database.DatabaseType.MYSQL_DATA_IMPORT, server + (StringUtils.isEmpty(port) ? "" : (":" + port)) + "/" + database, username, password);

				// Pass the InputStream to it
				reader.init(input);
				prepareReader(reader);

				// Now check which reader type we're using
				if (reader instanceof IStreamableReader)
				{
					// Cast to streamer
					IStreamableReader<T> streamer = (IStreamableReader) reader;

					// Then stream the items
					while (streamer.hasNext())
						write(streamer.next());

					flush();
				}
				else
				{
					// Read all the entries from the file
					List<T> entries = ((IBatchReader) reader).readAll();

					// Then iterate
					for (T entry : entries)
						write(entry);

					flush();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();

				// In case there is an exception, delete all inserted items
				deleteInsertedItems();
			}
			databaseConnection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Invalid reader class specified");
		}
	}

	/**
	 * Deletes all the database items with the given ids from the given table.
	 *
	 * @param ids   The {@link Set} of ids
	 * @param table The database table name
	 */
	protected void deleteItems(Set<Long> ids, String table)
	{
		if (CollectionUtils.isEmpty(ids))
			return;

		System.out.println("Deleting inserted items for table: '" + table + "'");
		System.out.println("Deleting items: " + ids.size());

		try
		{
			// Recreate the database connection
			if (databaseConnection == null || databaseConnection.isClosed())
				databaseConnection = Database.connect(Database.DatabaseType.MYSQL_DATA_IMPORT, server + (StringUtils.isEmpty(port) ? "" : (":" + port)) + "/" + database, username, password);

			// Delete all items with the given ids
			new ValueQuery(databaseConnection, "DELETE FROM " + table + " WHERE id IN (" + StringUtils.generateSqlPlaceholderString(ids.size()) + ")")
					.setLongs(ids)
					.execute(false);

			// Get the max id
			Long id = new ValueQuery(databaseConnection, "SELECT MAX(id) AS theId FROM " + table)
					.run("theId")
					.setCloseConnection(false)
					.getLong(1L)
					.getServerResult();

			if (id == null)
				id = 1L;

			// Then set the auto increment to one above
			new ValueQuery(databaseConnection, "ALTER TABLE " + table + " AUTO_INCREMENT = ?")
					.setLong(id)
					.execute(false);
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		System.err.println("You're not supposed to be here...");
		System.err.println("Please run `java -cp germinate-importer.jar` instead...");
	}
}
