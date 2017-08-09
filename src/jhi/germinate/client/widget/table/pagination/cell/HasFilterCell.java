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

package jhi.germinate.client.widget.table.pagination.cell;

import com.google.gwt.cell.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * @author Sebastian Raubach
 */
public class HasFilterCell implements HasCell<String, String>
{
	private final FilterCell cell;

	public HasFilterCell(Widget parent, FilterCell.FilterCellState state, FilterCell.VisibilityCallback visibilityCallback, FilterCell.FilterCallback filterCallback)
	{
		cell = new FilterCell(parent, state, visibilityCallback, filterCallback);
	}

	@Override
	public Cell<String> getCell()
	{
		return cell;
	}

	@Override
	public FieldUpdater<String, String> getFieldUpdater()
	{
		return null;
	}

	@Override
	public String getValue(String object)
	{
		return "";
	}
}
