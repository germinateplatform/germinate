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

import com.google.gwt.cell.client.*;

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class HasDatePickerCell implements HasCell<String, Date>
{
	private final CustomDatePickerCell       cell;
	private final FilterCell.FilterCallback  filterCallback;
	private final FilterCell.FilterCellState state;

	public HasDatePickerCell(FilterCell.FilterCellState state, FilterCell.VisibilityCallback visibilityCallback, FilterCell.FilterCallback filterCallback)
	{
		cell = new CustomDatePickerCell(state, visibilityCallback);
		this.filterCallback = filterCallback;
		this.state = state;
	}

	@Override
	public Cell<Date> getCell()
	{
		return cell;
	}

	@Override
	public FieldUpdater<String, Date> getFieldUpdater()
	{
		return (index, object, value) ->
		{
			cell.updateInnerValue(value);

			if (filterCallback != null)
			{
				try
				{
					filterCallback.onFilterEvent(true, HasDatePickerCell.this.state != FilterCell.FilterCellState.BOTTOM, DateUtils.getDatabaseDate(value.getTime()));
					filterCallback.onEnterPressed();
				}
				catch (InvalidArgumentException e)
				{
					/* Do nothing here */
				}
			}
		};
	}

	@Override
	public Date getValue(String object)
	{
		return null;
	}
}
