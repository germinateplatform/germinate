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

package jhi.germinate.client.widget.table.basic;

import com.google.gwt.core.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.gwt.*;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class AdvancedTable<T> extends Composite
{
	/** The actual table */
	private CellTable<T>                   table;
	/** Sort handler for the data */
	private ColumnSortEvent.ListHandler<T> columnSortHandler;

	private boolean freezeHeader = true;
	private String  id           = String.valueOf(RandomUtils.RANDOM.nextLong());

	public AdvancedTable(List<T> data)
	{
		if (data == null)
		{
			Notification.notify(Notification.Type.ERROR, "Table not properly set up. Exiting.");
			throw new RuntimeException("Table not properly set up. 'data' is not set.");
		}

		table = new CellTable<>(data.size());
		table.setWidth("100%");

		table.setAutoFooterRefreshDisabled(false);
		table.setAutoHeaderRefreshDisabled(false);
		table.getElement().setId(id);

		FlowPanel scrollPanel = new FlowPanel();
		scrollPanel.add(table);

        /* Take care of sorting */
		/* Data provider based on a list */
		ListDataProvider<T> dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(table);

		List<T> list = dataProvider.getList();
		list.addAll(data);

		columnSortHandler = new ColumnSortEvent.ListHandler<>(list);
		table.addColumnSortHandler(columnSortHandler);

		initWidget(scrollPanel);
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		if (freezeHeader && JavaScript.isFreezeHeaderLoaded())
		{
			Scheduler.get().scheduleDeferred(() -> JavaScript.makeHeaderSticky(id));
		}
	}

	public void addStringColumn(Column<T, String> column, String headerString)
	{
		table.addColumn(column, headerString);
		if (column.isSortable())
			columnSortHandler.setComparator(column, new BaseStringComparator(column));
	}

	public void addSafeHtmlColumn(Column<T, SafeHtml> column, String headerString)
	{
		table.addColumn(column, headerString);
		if (column.isSortable())
			columnSortHandler.setComparator(column, new BaseSafeHtmlComparator(column));
	}

	public void setFreezeHeader(boolean freezeHeader)
	{
		this.freezeHeader = freezeHeader;
	}

	public CellTable<T> getTable()
	{
		return table;
	}

	private static int compareTo(String first, String second)
	{
		/* Handle null values */
		if (first == null && second == null)
			return 0;
		/* Sort null values to the end */
		else if (first == null)
			return 1;
		else if (second == null)
			return -1;

        /* Handle question marks */
		if (first.equals("?") && second.equals("?"))
			return 0;
		/* Sort question marks to the end */
		else if (first.equals("?"))
			return -1;
		else if (second.equals("?"))
			return 1;

        /* Try to parse it as a number */
		boolean numbers = true;
		boolean dates = true;
		double firstNumber = 0;
		double secondNumber = 0;
		Date firstDate = null;
		Date secondDate = null;

		try
		{
			firstNumber = NumberUtils.toDouble(first);
			secondNumber = NumberUtils.toDouble(second);
		}
		catch (Exception e)
		{
			numbers = false;
		}

		try
		{
			firstDate = DateUtils.getLocalizedDate(first);
			secondDate = DateUtils.getLocalizedDate(second);
		}
		catch (Exception e)
		{
			dates = false;
		}

		int result;

        /* If it's a number, compare numbers */
		if (numbers)
		{
			result = (int) Math.signum(firstNumber - secondNumber);
		}
		else if (dates)
		{
			result = DateUtils.compare(firstDate, secondDate);
		}
		/* Else compare as String */
		else
		{
			result = first.compareToIgnoreCase(second);
		}

		return result;
	}

	private class BaseStringComparator implements Comparator<T>
	{
		private Column<T, String> column;

		public BaseStringComparator(Column<T, String> column)
		{
			this.column = column;
		}

		@Override
		public int compare(T o1, T o2)
		{
			String one = column.getValue(o1);
			String two = column.getValue(o2);

			return compareTo(one, two);
		}
	}

	private class BaseSafeHtmlComparator implements Comparator<T>
	{
		private Column<T, SafeHtml> column;

		public BaseSafeHtmlComparator(Column<T, SafeHtml> column)
		{
			this.column = column;
		}

		@Override
		public int compare(T o1, T o2)
		{
			String one = column.getValue(o1).asString();
			String two = column.getValue(o2).asString();

			return compareTo(one, two);
		}
	}

}
