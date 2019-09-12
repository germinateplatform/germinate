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

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.rpc.*;

import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class CommentTable extends DatabaseObjectPaginationTable<Comment>
{
	public CommentTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return CommentTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Comment, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Comment object)
				{
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
			column.setDataStoreName(Comment.ID);
			addColumn(column, Text.LANG.commentColumnId(), sortingEnabled);
		}

		/* Add the group description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Comment object)
			{
				return SimpleHtmlTemplate.INSTANCE.text(object.getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Comment.DESCRIPTION);
		addColumn(column, Text.LANG.commentColumnDescription(), sortingEnabled);

		/* Add the comment type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Comment object)
			{
				return TableUtils.getCellValueAsString(object.getType().getDescription());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(CommentType.DESCRIPTION);
		addColumn(column, Text.LANG.commentColumnType(), sortingEnabled);

		/* Add the user column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Comment object)
			{
				if (object.getUser() != null)
					return TableUtils.getCellValueAsString(object.getUser());
				else
					return "";
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		//		column.setDataStoreName(CommentType.NAME);
		addColumn(column, Text.LANG.commentColumnUser(), false);

		/* Add the created on */
		column = new TextColumn()
		{
			@Override
			public String getValue(Comment object)
			{
				return DateUtils.getLocalizedDate(object.getCreatedOn());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Comment.CREATED_ON);
		addColumn(column, Text.LANG.commentColumnCreatedOn(), sortingEnabled);

		if (ModuleCore.getUseAuthentication())
		{
			ButtonCell buttonCell = new ButtonCell(ButtonType.DANGER, Style.MDI_DELETE)
			{
				@Override
				public void render(Context context, SafeHtml data, SafeHtmlBuilder sb)
				{
					Comment comment = table.getVisibleItem(context.getIndex());
					if (comment.getCreatedBy() != null && comment.getCreatedBy().longValue() == ModuleCore.getUserAuth().getId().longValue())
						super.render(context, data, sb);
				}
			};

			Column<Comment, String> deleteColumn = new Column<Comment, String>(buttonCell)
			{
				@Override
				public String getValue(Comment object)
				{
					return null;
				}
			};
			addColumn(deleteColumn, Text.LANG.generalDelete());

			deleteColumn.setFieldUpdater((index, object, value) ->
					AlertDialog.createYesNoDialog(Text.LANG.generalConfirm(), Text.LANG.annotationsDeleteConfirm(1), false, e -> {
						CommentService.Inst.get().disable(Cookie.getRequestProperties(), object, new DefaultAsyncCallback<Void>()
						{
							@Override
							protected void onFailureImpl(Throwable caught)
							{
								if (caught instanceof InsufficientPermissionsException)
								{
									Notification.notify(Notification.Type.ERROR, Text.LANG.notificationActionInsufficientPermissions());
								}
								else
								{
									super.onFailureImpl(caught);
								}
							}

							@Override
							public void onSuccessImpl(Void result)
							{
								GoogleAnalytics.trackEvent(GoogleAnalytics.Category.ANNOTATIONS, "delete", Long.toString(object.getId()));

								refreshTable();
							}
						});
					}, null));

			fixItemAlignment();
		}
	}

	private void fixItemAlignment()
	{
		/* Dirty hack to get stuff vertically aligned and break words */
		table.addLoadingStateChangeHandler(event ->
		{
			if (event.getLoadingState() == LoadingStateChangeEvent.LoadingState.LOADED)
			{
				Scheduler.get().scheduleDeferred(() ->
				{
					for (int i = 0; i < table.getRowCount(); i++)
					{
						align(table.getRowElement(i));
					}
				});
			}
		});
	}

	private native void align(Element element) /*-{
		$wnd.$(element)
			.find("td")
			.css("vertical-align", "middle");
	}-*/;

	@Override
	protected void onItemSelected(NativeEvent event, Comment object, int column)
	{
	}
}
