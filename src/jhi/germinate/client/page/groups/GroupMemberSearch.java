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

package jhi.germinate.client.page.groups;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class GroupMemberSearch
{
	private AlertDialog                  dialog;
	private ComparisonRow.ComparisonType type;
	private List<ToggleSwitch>  switches = new ArrayList<>();
	private List<ComparisonRow> rows     = new ArrayList<>();
	private final FlowPanel content;
	private final FlowPanel body;

	private Group                                                                  group;
	private AsyncCallback<DatabaseObjectPaginationTable<? extends DatabaseObject>> callback;

	public GroupMemberSearch(Group group, AsyncCallback<DatabaseObjectPaginationTable<? extends DatabaseObject>> callback)
	{
		this.group = group;
		this.callback = callback;

		switch (group.getType().getTargetTable())
		{
			case markers:
				type = ComparisonRow.ComparisonType.marker;
				break;
			case locations:
				type = ComparisonRow.ComparisonType.location;
				break;
			case germinatebase:
				type = ComparisonRow.ComparisonType.accession;
				break;
			default:
				type = null;
		}

		body = new FlowPanel();
		content = new FlowPanel();
		Button addButton = new Button(Text.LANG.generalAdd(), IconType.PLUS_SQUARE_O, e -> addRow());

		body.add(content);
		body.add(addButton);

		addRow();
	}

	public void open()
	{
		if (dialog == null)
		{
			dialog = new AlertDialog(Text.LANG.groupsCriteriaTitle(), body)
					.setRemoveOnHide(false)
					.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalCancel(), IconType.BAN, null))
					.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalSearch(), IconType.SEARCH, ButtonType.PRIMARY, e -> search()));
		}

		dialog.open();
	}

	public void onUnload()
	{
		dialog.remove();
	}

	private void addRow()
	{
		ComparisonRow row = new ComparisonRow(rows.size() > 0);
		row.setComparisonType(type);
		row.addAttachHandler(event ->
		{
			if (!event.isAttached())
			{
				int index = rows.indexOf(row);
				if (index - 1 >= 0)
				{
					rows.remove(row);
					ToggleSwitch toggleSwitch = switches.get(index - 1);
					switches.remove(toggleSwitch);
					toggleSwitch.removeFromParent();
				}
			}
		});

		if (rows.size() > 0)
		{
			ToggleSwitch toggleSwitch = new ToggleSwitch();
			toggleSwitch.setOnText(Text.LANG.operatorsAnd());
			toggleSwitch.setOffText(Text.LANG.operatorsOr());
			toggleSwitch.setOnColor(ColorType.PRIMARY);
			toggleSwitch.setOffColor(ColorType.PRIMARY);
			toggleSwitch.setValue(true);

			content.add(toggleSwitch);
			switches.add(toggleSwitch);
		}

		content.add(row);
		rows.add(row);

		Scheduler.get().scheduleDeferred(() -> JavaScript.scrollToBottom(content.getElement()));
	}

	private void search()
	{
		/* Assemble the query */
		PartialSearchQuery query = new PartialSearchQuery();

        /* For each of the rows */
		for (int i = 0; i < rows.size(); i++)
		{
			ComparisonRow row = rows.get(i);

            /* Otherwise add it and possibly the operator in between */
			if (i > 0)
			{
				boolean value = switches.get(i - 1).getValue();
				query.addLogicalOperator(value ? new And() : new Or());
			}

			try
			{
				query.add(row.getSearchCondition());
			}
			catch (InvalidArgumentException | InvalidSearchQueryException e)
			{
				Notification.notify(Notification.Type.ERROR, e.getLocalizedMessage());
				return;
			}
		}

		returnTable(query);
	}

	private void returnTable(PartialSearchQuery query)
	{
		DatabaseObjectPaginationTable<? extends DatabaseObject> result = null;

		switch (type)
		{
			case accession:
				result = new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
					{
						return AccessionService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, query, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						AccessionService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), query, callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}

					@Override
					protected boolean supportsFiltering()
					{
						return false;
					}
				};
				break;
			case marker:
				result = new MapDefinitionTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback)
					{
						return MarkerService.Inst.get().getMapDefinitionForFilter(Cookie.getRequestProperties(), pagination, query, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						MarkerService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), query, callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}
				};
				break;
			case location:
				result = new LocationTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Location>>> callback)
					{
						return LocationService.Inst.get().getForFilter(Cookie.getRequestProperties(), query, pagination, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						LocationService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), query, callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}
				};
				break;
		}

		final DatabaseObjectPaginationTable<? extends DatabaseObject> t = result;

		ButtonGroup buttonGroup = new ButtonGroup();
		Button addGroupMember = new Button(Text.LANG.generalAdd(), IconType.PLUS_SQUARE, e ->
		{
			Set<? extends DatabaseObject> selectedItems = t.getSelection();

			if (selectedItems.size() < 1)
			{
				Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
				return;
			}

			List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

			GroupService.Inst.get().addItems(Cookie.getRequestProperties(), group.getId(), ids, new DefaultAsyncCallback<ServerResult<Set<Long>>>(true)
			{
				@Override
				protected void onSuccessImpl(ServerResult<Set<Long>> result)
				{
					GerminateEventBus.BUS.fireEvent(new GroupMemberChangeEvent());
				}
			});
		});

		buttonGroup.add(addGroupMember);
		result.addExtraContent(buttonGroup);

		callback.onSuccess(result);
	}
}