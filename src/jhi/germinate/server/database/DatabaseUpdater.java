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

import org.flywaydb.core.*;
import org.flywaydb.core.api.*;

import java.util.logging.*;

import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link DatabaseUpdater} takes care of updating the database to the latest schema
 *
 * @author Sebastian Raubach
 */
public class DatabaseUpdater
{
	public void initialize()
	{
		/* Automatically update the database if this is enabled */
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_AUTO_UPDATE_DATABASE))
		{
			String database = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
			try
			{
				Logger.getLogger("").log(Level.INFO, "RUNNING FLYWAY on: " + database);
				Flyway flyway = new Flyway();
				flyway.setTable("schema_version");
				flyway.setValidateOnMigrate(false);
				flyway.setDataSource(Database.DatabaseType.MYSQL.getUrl(PropertyWatcher.getServerString(Database.DatabaseType.MYSQL)), PropertyWatcher.get(ServerProperty.DATABASE_USERNAME), PropertyWatcher.get(ServerProperty.DATABASE_PASSWORD));
				flyway.setLocations("classpath:jhi.germinate.server.database.migration");
				flyway.setBaselineOnMigrate(true);
				flyway.migrate();
				flyway.repair();
			}
			catch (InvalidDatabaseTypeException | FlywayException e)
			{
				e.printStackTrace();
			}
		}
	}
}
