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

package jhi.germinate.client.widget.table.pagination.cell;

import com.google.gwt.user.cellview.client.*;

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class FilterCellCallback<T extends DatabaseObject> implements FilterCell.FilterCallback
{
	private DatabaseObjectPaginationTable<T> table;
	private Column<T, ?>                     column;
	private Range                            range;

	public FilterCellCallback(DatabaseObjectPaginationTable<T> table, Column<T, ?> column)
	{
		this.table = table;
		this.column = column;
	}

	@Override
	public void onFilterEvent(boolean isUserInput, boolean isStart, String value) throws InvalidArgumentException
	{
		if (column instanceof DatabaseObjectFilterColumn)
		{
			Class clazz = ((DatabaseObjectFilterColumn) column).getType();

			if (StringUtils.isEmpty(value))
			{
				setRange(range, isStart);
				if (range != null)
					range.setUserInput(isUserInput);
			}
			else if (!isFilterValid(value, clazz))
			{
				setRange(range, isStart);
				if (range != null)
					range.setUserInput(isUserInput);

				throw new InvalidArgumentException();
			}
			else
			{
				if (range == null)
					range = new Range();

				range.setUserInput(isUserInput);

				if (isStart)
					range.setStart(value);
				else
					range.setEnd(value);
			}
		}
	}

	public void setRange(Range range)
	{
		this.range = range;
	}

	private void setRange(Range range, boolean isStart)
	{
		if (range != null)
		{
			if (isStart)
				range.setStart(null);
			else
				range.setEnd(null);

			if (range.isEmpty())
				this.range = null;
		}
	}

	public Range getRange()
	{
		return range;
	}

	@Override
	public void onEnterPressed()
	{
		table.refreshTable();
	}

	private boolean isFilterValid(String value, Class clazz)
	{
		try
		{
			if (Double.class.equals(clazz))
			{
				Double.parseDouble(value);
			}
			else if (Boolean.class.equals(clazz))
			{
				Boolean.parseBoolean(value);
			}
			else if (Long.class.equals(clazz))
			{
				Long.parseLong(value);
			}
			else if (Date.class.equals(clazz))
			{
				try
				{
					/* Try to parse it as a full date first */
					DateUtils.getDatabaseDate(value);
				}
				catch (Exception e)
				{
					/* Then try to parse just the year */
					DateUtils.getYear(value);
				}
			}

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
