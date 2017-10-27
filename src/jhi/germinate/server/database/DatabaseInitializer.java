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

import jhi.germinate.shared.exception.*;

/**
 * {@link DatabaseInitializer} is an abstract class that takes care of initializing needed features of the database.
 * <p/>
 * Classes extending this class have to supply names and queries along with create and drop statements.
 *
 * @author Sebastian Raubach
 */
public abstract class DatabaseInitializer
{
	/**
	 * Initializes the feature of the database.
	 */
	public void initialize()
	{
		Database database = null;
		try
		{
			database = Database.connect();

			String dropStatement = getDropStatement();
			String createStatement = getCreateStatement();
			String[] names = getNames();
			String[] queries = getQueries();

            /* Drop and recreate the views */
			for (int i = 0; i < Math.min(names.length, queries.length); i++)
			{
				try
				{
					database.prepareStatement(String.format(dropStatement, names[i])).execute();
					database.prepareStatement(String.format(createStatement, names[i], queries[i])).execute();
				}
				catch (DatabaseException e)
				{
					e.printStackTrace();
				}
			}

			database.close();
		}
		catch (DatabaseException e)
		{
			if (database != null)
				database.close();

			e.printStackTrace();
		}
	}

	protected abstract String[] getNames();

	protected abstract String[] getQueries();

	protected abstract String getDropStatement();

	protected abstract String getCreateStatement();
}
