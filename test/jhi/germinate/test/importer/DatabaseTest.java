/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.test.importer;

import org.junit.jupiter.api.*;

import java.io.*;
import java.io.IOException;
import java.nio.charset.*;
import java.sql.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.*;

/**
 * @author Sebastian Raubach
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseTest
{
	protected Database db;
	protected String   server   = "localhost";
	protected String   database = "germinate_template_test";
	protected String   username = "root";
	protected String   password = "";

	@BeforeAll
	public void initDatabase() throws DatabaseException, IOException, SQLException
	{
		db = Database.connect(Database.DatabaseType.MYSQL, server, username, password);

		// Drop old version of db (if exists)
		new ValueQuery("DROP DATABASE IF EXISTS `" + database + "`")
				.setDatabase(db)
				.execute(false);
		// Create the db
		new ValueQuery("CREATE DATABASE `" + database + "`")
				.setDatabase(db)
				.execute(false);

		// Change connection to the specific db
		db.close();
		db = Database.connect(Database.DatabaseType.MYSQL, server + "/" + database, username, password);

		// Import the db creation script
		File databaseScript = new File("database/germinate_template.sql");
		assert databaseScript.exists();

		ScriptRunner runner = new ScriptRunner(db.getConnection(), false, true);
		runner.setLogWriter(new PrintWriter(System.out));
		runner.setErrorLogWriter(new PrintWriter(System.err));
		runner.runScript(new BufferedReader(new InputStreamReader(new FileInputStream(databaseScript), StandardCharsets.UTF_8)));
	}

	@AfterAll
	public void closeDatabase() throws DatabaseException
	{
		// Close current connection, then connect to parent
		db.close();
		db = Database.connect(Database.DatabaseType.MYSQL, server, username, password);

		// Drop table
		new ValueQuery("DROP DATABASE IF EXISTS `" + database + "`")
				.setDatabase(db)
				.execute(false);

		// Disconnect
		db.close();
	}
}
