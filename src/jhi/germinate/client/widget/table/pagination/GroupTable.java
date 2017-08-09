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

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class GroupTable extends DatabaseObjectPaginationTable<Group>
{
	public GroupTable()
	{
	}

	public GroupTable(SelectionMode selectionMode, boolean sortingEnabled)
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
	protected void createColumns()
	{
		Column<Group, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Group object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.ACCESSIONS_FOR_COLLSITE))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.ACCESSIONS_FOR_COLLSITE);
					else
						return SimpleHtmlTemplate.INSTANCE.text(Long.toString(object.getId()));
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyleNames(Cell.Context context, Group object)
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Group.ID);
			addColumn(column, Text.LANG.groupsColumnId(), true);
		}

		/* Add the group description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Group object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
					return TableUtils.getHyperlinkValue(object.getDescription(), "#" + Page.GROUPS);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Group.DESCRIPTION);
		addColumn(column, Text.LANG.groupsColumnDescription(), true);

		/* Add the group type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Group object)
			{
				return TableUtils.getCellValueAsString(object.getType().getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(GroupType.DESCRIPTION);
		addColumn(column, Text.LANG.groupsColumnDescription(), true);

		/* Add the user column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Group object)
			{
				return TableUtils.getCellValueAsString(object.getUser());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Group.CREATED_BY);
		addColumn(column, Text.LANG.commentColumnUser(), true);

		/* Add the created on */
		column = new TextColumn()
		{
			@Override
			public String getValue(Group object)
			{
				return DateUtils.getLocalizedDate(object.getCreatedOn());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Group.CREATED_ON);
		addColumn(column, Text.LANG.groupsColumnCreatedOn(), true);
	}

	@Override
	protected void onSelectionChanged(NativeEvent event, Group object, int column)
	{
		/* Get the id */
		if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
		{
			GerminateDatabaseTableParameterStore.Inst.get().put(Parameter.groupType, GerminateDatabaseTable.germinatebase);
			LongParameterStore.Inst.get().put(Parameter.groupId, object.getId());
		}
	}
}
