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
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
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
	protected String getClassName()
	{
		return GroupTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Group, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Group object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.GROUPS);
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
			column.setDataStoreName(Group.ID);
			addColumn(column, Text.LANG.groupsColumnId(), sortingEnabled);
		}

		/* Add the group name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Group object)
			{
				if (GroupsPage.canEdit(object))
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
						return SimpleHtmlTemplate.INSTANCE.materialIconAnchorWithText(Style.MDI_RENAME_BOX, object.getName(), Text.LANG.generalRename(), UriUtils.fromString("#" + Page.GROUPS), "");
					else
						return SimpleHtmlTemplate.INSTANCE.materialIconWithText(Style.MDI_RENAME_BOX, object.getName(), Text.LANG.generalRename());
				}
				else
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
						return TableUtils.getHyperlinkValue(object.getName(), "#" + Page.GROUPS);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getName());
				}
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, Group object, NativeEvent event)
			{
				Element element = Element.as(event.getEventTarget());

				if (BrowserEvents.CLICK.equals(event.getType()) && element.hasClassName(Style.MDI))
				{
					event.preventDefault();

					AddGroupDialog content = new AddGroupDialog(Collections.singletonList(object.getType()), object.getType());
					content.setName(object.getName());
					content.setDescription(object.getDescription());

					new AlertDialog(Text.LANG.generalRename())
							.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalRename(), Style.MDI_PENCIL_BOX_OUTLINE, ButtonType.PRIMARY, e ->
							{
								object.setName(content.getName());
								object.setDescription(content.getDescription());
								GroupService.Inst.get().renameGroup(Cookie.getRequestProperties(), object, new DefaultAsyncCallback<ServerResult<Void>>()
								{
									@Override
									protected void onSuccessImpl(ServerResult<Void> result)
									{
										refreshTable();
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
		column.setDataStoreName(Group.NAME);
		addColumn(column, Text.LANG.groupsColumnName(), sortingEnabled);

		/* Add the group description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Group object)
			{
				return TableUtils.getCellValueAsString(object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Group.DESCRIPTION);
		addColumn(column, Text.LANG.groupsColumnDescription(), sortingEnabled);

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
		addColumn(column, Text.LANG.groupsColumnType(), sortingEnabled);

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
		addColumn(column, Text.LANG.commentColumnUser(), sortingEnabled, false);

		/* Add the size column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return Long.class;
			}

			@Override
			public String getValue(Group object)
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
		addColumn(column, Text.LANG.groupsColumnCreatedOn(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Group object, int column)
	{
		/* Get the id */
		if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
		{
			GerminateDatabaseTableParameterStore.Inst.get().put(Parameter.groupType, GerminateDatabaseTable.germinatebase);
			LongParameterStore.Inst.get().put(Parameter.groupId, object.getId());
		}
	}
}
