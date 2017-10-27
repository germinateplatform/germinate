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

import jhi.germinate.server.database.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link DefaultStreamer} is a utility class allowing to "stream" {@link DatabaseResult}s, i.e., this class will return one {@link DatabaseResult} at
 * a time.
 *
 * @author Sebastian Raubach
 */
public final class DefaultStreamer
{
	private final Database       database;
	private       DatabaseResult res;
	private       DebugInfo      info;

	DefaultStreamer(Database database, DebugInfo info, DatabaseStatement stmt) throws DatabaseException
	{
		this.database = database;
		this.info = info;

        /* Run the query */
		info.add(stmt.getStringRepresentation());
		stmt.setFetchSize(Integer.MIN_VALUE);
		res = stmt.query();
	}

	/**
	 * Returns the next {@link DatabaseResult} or <code>null</code> if there is no next item
	 *
	 * @return The next {@link DatabaseResult} or <code>null</code> if there is no next item
	 * @throws DatabaseException Thrown if either {@link DatabaseResult#next()} or {@link DatabaseResult#getString(String)} fails
	 */
	public DatabaseResult next() throws DatabaseException
	{
		if (!database.isClosed())
		{
			if (res.next())
			{
				return res;
			}
			else
			{
				/* Else close the database connection */
				database.close();
			}
		}

		return null;
	}

	/**
	 * Closes the database
	 */
	public void close()
	{
		if (!database.isClosed())
			database.close();
	}

	/**
	 * Returns the {@link DebugInfo}
	 *
	 * @return The {@link DebugInfo}
	 */
	public DebugInfo getDebugInfo()
	{
		return info;
	}

}
