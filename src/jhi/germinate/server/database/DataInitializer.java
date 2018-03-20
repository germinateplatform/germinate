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

import jhi.germinate.server.config.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link DataInitializer} contains methods to drop and create views.
 *
 * @author Sebastian Raubach
 */
public class DataInitializer
{
	private static final String INSERT_IGNORE_EXPERIMENTTYPE   = "INSERT IGNORE INTO `experimenttypes` SET `id` = ?, `description` = ?";
	private static final String RENAME_TABLE_PEDIGREENOTATIONS = "RENAME TABLE `pedigreenotation` TO `pedigreenotations`;"; //Fixes a naming issue

	private static final String INSERT_IGNORE_NEWS_TYPES = "INSERT IGNORE INTO `newstypes` SET `id` = ?, `name` = ?, `description` = ?";

	public void initialize()
	{
		for (ExperimentType type : ExperimentType.values())
		{
			try
			{
				new ValueQuery(INSERT_IGNORE_EXPERIMENTTYPE)
						.setLong(type.getId())
						.setString(type.name())
						.execute();
			}
			catch (Exception e)
			{
				/* Do nothing here */
				e.printStackTrace();
			}
		}

		for (NewsType type : NewsType.values())
		{
			try
			{
				new ValueQuery(INSERT_IGNORE_NEWS_TYPES)
						.setLong(type.getId())
						.setString(type.getName())
						.setString(type.getDescription())
						.execute();
			}
			catch (Exception e)
			{
				/* Do nothing here */
				e.printStackTrace();
			}
		}

		try
		{
			new ValueQuery(RENAME_TABLE_PEDIGREENOTATIONS)
					.execute();
		}
		catch (Exception e)
		{
			/* Do nothing here */
		}

		/* Automatically update the database if this is enabled */
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_AUTO_UPDATE_DATABASE))
		{
			try
			{
				Logger.getLogger("").log(Level.INFO, "RUNNING FLYWAY");
				Flyway flyway = new Flyway();
				flyway.setTable("schema_version");
				flyway.setDataSource(Database.DatabaseType.MYSQL.getConnectionString() + PropertyReader.getServerString(Database.DatabaseType.MYSQL), PropertyReader.get(ServerProperty.DATABASE_USERNAME), PropertyReader.get(ServerProperty.DATABASE_PASSWORD));
				flyway.setLocations("classpath:jhi.germinate.server.database.migration");
				flyway.setBaselineOnMigrate(true);
				flyway.migrate();
			}
			catch (InvalidDatabaseTypeException | FlywayException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static class DatasetMetaJob implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				new ValueQuery("call " + StoredProcedureInitializer.DATASET_META + "()")
						.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
