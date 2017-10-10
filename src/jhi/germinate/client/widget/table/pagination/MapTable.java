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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class MapTable extends DatabaseObjectPaginationTable<Map>
{
	public MapTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return MapTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Map, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Map object)
				{
					return SimpleHtmlTemplate.INSTANCE.dummyAnchor(Long.toString(object.getId()));
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
			column.setDataStoreName(Map.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), sortingEnabled);
		}

		/* Add the description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Map object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Map.DESCRIPTION);
		addColumn(column, Text.LANG.mapsColumnsMapName(), sortingEnabled);

		/* Add the created on column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Map object)
			{
				return DateUtils.getLocalizedDate(object.getCreatedOn());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Map.CREATED_ON);
		addColumn(column, Text.LANG.passportColumnCreatedOn(), sortingEnabled);

		/* Add the updated on column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Map object)
			{
				return DateUtils.getLocalizedDate(object.getUpdatedOn());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Map.UPDATED_ON);
		addColumn(column, Text.LANG.passportColumnUpdatedOn(), sortingEnabled);

		/* Add the size column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Map object)
			{
				Long count = object.getSize();

				if (count == null || count == 0L)
					return "?";
				else
					return Long.toString(count);
			}

			@Override
			public Class getType()
			{
				return Long.class;
			}
		};
		addColumn(column, Text.LANG.datasetsColumnDatasetSize());
	}

	@Override
	protected void onItemSelected(NativeEvent event, Map object, int column)
	{
		/* Get the id */
		LongParameterStore.Inst.get().put(Parameter.mapId, object.getId());
	}
}
