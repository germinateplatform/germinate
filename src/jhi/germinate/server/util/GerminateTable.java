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

package jhi.germinate.server.util;

import java.util.*;

/**
 * {@link GerminateTable} is wrapper class for an {@link ArrayList} of {@link GerminateRow}s
 *
 * @author Sebastian Raubach
 */
public class GerminateTable extends ArrayList<GerminateRow>
{
	private static final long serialVersionUID = -165109347873549327L;

	public GerminateTable()
	{
	}

	/**
	 * Replaces the first GerminateRow in the GerminateTable for which the following holds true:
	 * <p/>
	 * <code>toReplace.get(key).equals(replaceWith.get(key))</code>
	 *
	 * @param newRow The GerminateRow to replace the old row with
	 * @param key    The key that will be used to determine equality
	 * @return true if the replacement process was successful, false otherwise
	 */
	public boolean replace(GerminateRow newRow, String key)
	{
		/* Check the condition for each contained row */
		for (GerminateRow row : this)
		{
			if (row.get(key).equals(newRow.get(key)))
			{
				/* Remember the first match position and stop searching */
				int index = indexOf(row);
				remove(index);
				add(index, newRow);
				return true;
			}
		}

        /* Nothing found */
		return false;
	}

	/**
	 * Returns the subset (rows) of the table starting with and ending with the given indices.
	 *
	 * @param startIndex The start position of the sub-table (inclusive)
	 * @param endIndex   The end position of the sub-table (exclusive)
	 * @return The sub-table
	 */
	public GerminateTable getSubTable(int startIndex, int endIndex)
	{
		GerminateTable result = new GerminateTable();

		for (int i = startIndex; i < Math.min(this.size(), endIndex); i++)
			result.add(this.get(i));

		return result;
	}

	/**
	 * Splits this {@link GerminateTable} based on the values of the given column.
	 *
	 * @param column The column to split based on
	 * @return A {@link Map} containing the distinct vales of the given column as the keys and a separate {@link GerminateTable} with distinct {@link
	 * GerminateRow}s for each value.
	 */
	public Map<String, GerminateTable> splitOn(String column)
	{
		Map<String, GerminateTable> result = new HashMap<>();

		for (GerminateRow row : this)
		{
			GerminateTable table = result.get(row.get(column));

			if (table == null)
				table = new GerminateTable();

			table.add(row);

			result.put(row.get(column), table);
		}

		return result;
	}

	public String[] getColumnNames()
	{
		Set<String> columnNames = new LinkedHashSet<>();

		for (GerminateRow row : this)
			columnNames.addAll(row.keySet());

		return columnNames.toArray(new String[columnNames.size()]);
	}
}
