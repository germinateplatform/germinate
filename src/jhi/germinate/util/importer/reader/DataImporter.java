/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.reader;

import org.apache.commons.cli.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.util.Util;
import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DataImporter<T>
{
	protected Database databaseConnection;

	/**
	 * If the reader passed to the {@link #run(File, String, String, String, String, String, String)} method cannot be instantiated, this fallback
	 * reader will be used to import the data instead.
	 *
	 * @return
	 */
	protected abstract IDataReader getFallbackReader();

	/**
	 * This method is called when an error occurs during the data export.
	 * Deletes the items that have been inserted before the error occurred.
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
				.withPort(false)
				.withReader(false);

		try
		{
			CommandLine line = new DefaultParser().parse(options, args);

			run(options.getInput(line),
					options.getServer(line),
					options.getDatabase(line),
					options.getUsername(line),
					options.getPassword(line),
					options.getPort(line),
					options.getReader(line));
		}
		catch (Exception e)
		{
			options.printHelp("DataImporter");

			System.exit(1);
		}
	}

	/**
	 * Starts the import process using the given database access parameters and the name of the {@link IDataReader}.
	 *
	 * @param input      The input file containing the actual data
	 * @param server     The database server
	 * @param database   The database name
	 * @param username   The database username
	 * @param password   The database password
	 * @param port       The port or <code>null</code>
	 * @param readerName The fully qualified path to the {@link IDataReader}
	 */
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		this.server = server;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;

		Constructor<?> constructor = null;

		if (readerName != null)
		{
			// Try to create an instance of the specified reader class
			try
			{
				Class<?> clazz = Class.forName(readerName);
				constructor = clazz.getConstructor();
			}
			catch (ClassNotFoundException | NoSuchMethodException e)
			{
				e.printStackTrace();
				throw new RuntimeException("Invalid reader class specified");
			}
		}

		// If we've got a reflection constructor, use it. Otherwise use the #getFallbackReader() as a fallback.
		try (IDataReader reader = constructor == null ? getFallbackReader() : (IDataReader) constructor.newInstance())
		{
			// Pass the InputStream to it
			reader.init(input);

			try
			{
				// Connect to the database
				databaseConnection = Database.connect(Database.DatabaseType.MYSQL, server + (StringUtils.isEmpty(port) ? "" : (":" + port)) + "/" + database, username, password);

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

		try
		{
			// Recreate the database connection
			if (databaseConnection == null || databaseConnection.isClosed())
				databaseConnection = Database.connect(Database.DatabaseType.MYSQL, server + (StringUtils.isEmpty(port) ? "" : (":" + port)) + "/" + database, username, password);

			// Delete all items with the given ids
			new ValueQuery(databaseConnection, "DELETE FROM " + table + " WHERE id IN (" + Util.generateSqlPlaceholderString(ids.size()) + ")")
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
}