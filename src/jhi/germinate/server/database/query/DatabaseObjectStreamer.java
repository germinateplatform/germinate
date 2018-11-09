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
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link DatabaseObjectStreamer} is a utility class allowing to "stream" {@link DatabaseObject}s, i.e., this class will return one {@link DatabaseObject}
 * at a time.
 *
 * @author Sebastian Raubach
 */
public final class DatabaseObjectStreamer<T extends DatabaseObject>
{
	private final Database                database;
	private       DatabaseResult          res;
	private       DebugInfo               info;
	private       DatabaseObjectParser<T> parser;
	private       UserAuth                user;
	private       boolean                 preventClose;
	private       boolean                 foreignKeysFromResult;

	DatabaseObjectStreamer(Database database, DebugInfo info, DatabaseStatement stmt, DatabaseObjectParser<T> parser, UserAuth user, boolean preventClose, boolean foreignKeysFromResult) throws DatabaseException
	{
		this.database = database;
		this.info = info;
		this.parser = parser;
		this.user = user;
		this.preventClose = preventClose;
		this.foreignKeysFromResult = foreignKeysFromResult;

		/* Run the query */
		info.add(stmt.getStringRepresentation());
		stmt.setFetchSize(Integer.MIN_VALUE);
		res = stmt.query();

		parser.clearCache();
	}

	/**
	 * Returns the next {@link DatabaseObject} or <code>null</code> if there is no next item
	 *
	 * @return The next {@link DatabaseObject} or <code>null</code> if there is no next item
	 * @throws DatabaseException Thrown if either {@link DatabaseResult#next()} or {@link DatabaseResult#getString(String)} fails
	 */
	public T next() throws DatabaseException
	{
		if (!database.isClosed())
		{
			if (res.next())
			{
				return parser.parse(res, user, foreignKeysFromResult);
			}
			else
			{
				parser.clearCache();
				/* Else close the database connection */
				if (!preventClose)
					database.close();
			}
		}
		else
		{
			parser.clearCache();
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
