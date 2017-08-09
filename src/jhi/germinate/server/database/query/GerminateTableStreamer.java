/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link GerminateTableStreamer} is a utility class allowing to "stream" {@link GerminateRow}s, i.e., this class will return one {@link GerminateRow}
 * at a time.
 *
 * @author Sebastian Raubach
 */
public final class GerminateTableStreamer implements AutoCloseable
{
	private final Database       database;
	private       String[]       columnNames;
	private       DatabaseResult res;
	private       DebugInfo      info;

	GerminateTableStreamer(Database database, DebugInfo info, DatabaseStatement stmt, String[] columnNames) throws DatabaseException
	{
		this.database = database;
		this.info = info;

        /* Run the query */
		info.add(stmt.getStringRepresentation());
		stmt.setFetchSize(Integer.MIN_VALUE);
		res = stmt.query();

		if (columnNames == null)
			columnNames = res.getColumnNames();

		this.columnNames = columnNames;
	}

	/**
	 * Returns the column names. Either obtained from the constructor call or from {@link DatabaseResult#getColumnNames(String...)}.
	 *
	 * @return The column names.
	 */
	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**
	 * Returns the next {@link GerminateRow} or <code>null</code> if there is no next item
	 *
	 * @return The next {@link GerminateRow} or <code>null</code> if there is no next item
	 * @throws DatabaseException Thrown if either {@link DatabaseResult#next()} or {@link DatabaseResult#getString(String)} fails
	 */
	public GerminateRow next() throws DatabaseException
	{
		GerminateRow result = null;

		if (!database.isClosed())
		{
			if (res.next())
			{
				/* Get the next item */
				result = new GerminateRow();
				for (String column : columnNames)
				{
					result.put(column, res.getString(column));
				}
			}
		}

		if (result == null)
			close();

		return result;
	}

	/**
	 * Closes the database
	 */
	@Override
	public void close()
	{
		if (!database.isClosed())
			database.close();
	}

	public boolean isClosed()
	{
		return database.isClosed();
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
