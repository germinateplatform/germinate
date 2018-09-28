/*
 *  Copyright 2018 Information and Computational Sciences,
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

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.groups.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class UserGroupTable extends DatabaseObjectPaginationTable<UserGroup>
{
	public UserGroupTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return UserGroupTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<UserGroup, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(UserGroup object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.USER_PERMISSIONS);
					else
						return SimpleHtmlTemplate.INSTANCE.text(Long.toString(object.getId()));
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
			column.setDataStoreName(UserGroup.ID);
			addColumn(column, Text.LANG.groupsColumnId(), sortingEnabled);
		}

		/* Add the group name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(UserGroup object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.USER_PERMISSIONS))
					return SimpleHtmlTemplate.INSTANCE.materialIconAnchorWithText(Style.MDI_RENAME_BOX, object.getName(), Text.LANG.generalRename(), UriUtils.fromString("#" + Page.USER_PERMISSIONS), "");
				else
					return SimpleHtmlTemplate.INSTANCE.materialIconWithText(Style.MDI_RENAME_BOX, object.getName(), Text.LANG.generalRename());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, UserGroup object, NativeEvent event)
			{
				Element element = Element.as(event.getEventTarget());

				if (BrowserEvents.CLICK.equals(event.getType()) && element.hasClassName(Style.MDI))
				{
					event.preventDefault();

					AddGroupDialog content = new AddGroupDialog(null, null);
					content.setName(object.getName());
					content.setDescription(object.getDescription());

					new AlertDialog(Text.LANG.generalRename())
							.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalRename(), Style.MDI_PENCIL_BOX_OUTLINE, ButtonType.PRIMARY, e ->
							{
								object.setName(content.getName());
								object.setDescription(content.getDescription());
								UserGroupService.Inst.get().renameGroup(Cookie.getRequestProperties(), object, new DefaultAsyncCallback<ServerResult<Void>>()
								{
									@Override
									protected void onSuccessImpl(ServerResult<Void> result)
									{
										refreshTable();
										GerminateEventBus.BUS.fireEvent(new UserGroupChangeEvent());
									}
								});
							}))
							.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalCancel(), Style.MDI_CANCEL, null))
							.setContent(content)
							.open();
				}
				else
				{
					super.onBrowserEvent(context, elem, object, event);
				}
			}
		};
		column.setDataStoreName(UserGroup.NAME);
		addColumn(column, Text.LANG.groupsColumnName(), sortingEnabled);

		/* Add the group description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(UserGroup object)
			{
				return TableUtils.getCellValueAsString(object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(UserGroup.DESCRIPTION);
		addColumn(column, Text.LANG.groupsColumnDescription(), sortingEnabled);

		/* Add the size column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return Long.class;
			}

			@Override
			public String getValue(UserGroup object)
			{
				return TableUtils.getCellValueAsString(object.getSize());
			}
		};
		column.setDataStoreName(DatabaseObject.COUNT);
		addColumn(column, Text.LANG.groupsColumnSize(), sortingEnabled);

		/* Add the created on */
		column = new TextColumn()
		{
			@Override
			public String getValue(UserGroup object)
			{
				return DateUtils.getLocalizedDate(object.getCreatedOn());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(UserGroup.CREATED_ON);
		addColumn(column, Text.LANG.groupsColumnCreatedOn(), sortingEnabled);

		GerminateEventBus.BUS.addHandler(UserGroupChangeEvent.TYPE, event -> refreshTable());
	}

	@Override
	protected void onItemSelected(NativeEvent event, UserGroup object, int column)
	{
	}
}
