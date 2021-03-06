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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class MegaEnvironmentTable extends DatabaseObjectPaginationTable<MegaEnvironment>
{
	public MegaEnvironmentTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(selectionMode, sortingEnabled);
	}

	@Override
	protected boolean supportsFiltering()
	{
		return false;
	}

	@Override
	protected boolean supportsDownload()
	{
		return false;
	}

	@Override
	protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	protected String getClassName()
	{
		return MegaEnvironmentTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<MegaEnvironment, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(MegaEnvironment object)
				{
					return Long.toString(object.getId());
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyle()
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName("id");
			addColumn(column, Text.LANG.megaEnvColumnId(), sortingEnabled);
		}

		/* Add the megaenvironment name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(MegaEnvironment object)
			{
				String value = object.getId() == null || object.getId() == -1 ? Text.LANG.megaEnvUnknown() : object.getName();
				return TableUtils.getHyperlinkValue(value, "");
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName("name");
		addColumn(column, Text.LANG.megaEnvColumnName(), sortingEnabled);

		/* Add the megaenvironment size column */
		column = new TextColumn()
		{
			@Override
			public String getValue(MegaEnvironment object)
			{
				Long count = object.getSize();

				if (count == null)
					return null;
				else
					return Long.toString(count);
			}

			@Override
			public Class getType()
			{
				return Long.class;
			}
		};
		column.setDataStoreName("count");
		addColumn(column, Text.LANG.megaEnvColumnSize(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, MegaEnvironment object, int column)
	{
	}
}
