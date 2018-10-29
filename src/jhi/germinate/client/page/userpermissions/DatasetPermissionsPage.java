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

package jhi.germinate.client.page.userpermissions;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetPermissionsPage extends Composite
{
	private static GroupsPageUiBinder ourUiBinder = GWT.create(GroupsPageUiBinder.class);
	@UiField
	HTML        datasetPermissionsHtml;
	@UiField
	SimplePanel datasetTablePanel;
	@UiField
	Heading     datasetName;
	@UiField
	FlowPanel   datasetPermissionsWrapper;
	@UiField
	SimplePanel datasetUserPermissionsPanel;
	@UiField
	SimplePanel newDatasetUserPermissionsTable;
	@UiField
	SimplePanel datasetGroupPermissionsPanel;
	@UiField
	SimplePanel newDatasetGroupPermissionsTable;
	private Dataset dataset;


	public DatasetPermissionsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		datasetPermissionsHtml.setHTML(Text.LANG.datasetPermissionsText());

		DatasetTable datasetTable = new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true, true, null)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, null, pagination, callback);
			}

			@Override
			protected void onItemSelected(NativeEvent event, Dataset object, int column)
			{
				event.preventDefault();
				dataset = object;
				updateDatasetUserPermissions();
				updateDatasetGroupPermissions();
			}
		};
		datasetTablePanel.add(datasetTable);
	}

	private void updateDatasetGroupPermissions()
	{
		datasetGroupPermissionsPanel.clear();
		newDatasetGroupPermissionsTable.clear();

		UserGroupTable userGroupTable = new UserGroupTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<UserGroup>>> callback)
			{
				if (filter == null)
					filter = new PartialSearchQuery();

				filter.add(new SearchCondition("datasetpermissions.dataset_id", new Equal(), dataset.getId(), Long.class));

				if (filter.getAll().size() > 1)
					filter.addLogicalOperator(new And());

				return UserGroupService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};

		datasetGroupPermissionsPanel.add(userGroupTable);

		if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
		{
			ButtonGroup group = new ButtonGroup();

			Button deleteUser = new Button(Text.LANG.generalRemove(), e ->
			{
				Set<? extends DatabaseObject> selectedItems = userGroupTable.getSelection();

				if (selectedItems.size() < 1)
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
					return;
				}

				List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

				UserService.Inst.get().removeFromDataset(Cookie.getRequestProperties(), dataset.getId(), ids, GerminateDatabaseTable.usergroups, new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onSuccessImpl(Void result)
					{
						super.onSuccessImpl(result);

						JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DATASET_PERMISSIONS, "removeItemsGroup", Long.toString(dataset.getId()), ids.size());

						userGroupTable.refreshTable();
					}
				});
			});
			deleteUser.addStyleName(Style.mdiLg(Style.MDI_DELETE));

			group.add(deleteUser);
			userGroupTable.addExtraContent(group);
		}

		UserGroupTable newUserGroupTable = new UserGroupTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<UserGroup>>> callback)
			{
				return UserGroupService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};
		newDatasetGroupPermissionsTable.add(newUserGroupTable);

		if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
		{
			ButtonGroup group = new ButtonGroup();

			Button addGroup = new Button(Text.LANG.userGroupsAddGroup(), e ->
			{
				Set<? extends DatabaseObject> selectedItems = newUserGroupTable.getSelection();

				if (selectedItems.size() < 1)
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
					return;
				}

				List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

				UserService.Inst.get().addToDataset(Cookie.getRequestProperties(), dataset.getId(), ids, GerminateDatabaseTable.usergroups, new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onSuccessImpl(Void result)
					{
						super.onSuccessImpl(result);

						JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DATASET_PERMISSIONS, "addItemsGroup", Long.toString(dataset.getId()), ids.size());

						userGroupTable.refreshTable();
						newUserGroupTable.setSelection(null);
					}
				});
			});
			addGroup.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));

			group.add(addGroup);
			newUserGroupTable.addExtraContent(group);
		}

		GerminateEventBus.BUS.addHandler(UserGroupChangeEvent.TYPE, e -> {
			Scheduler.get().scheduleDeferred(() -> {
				newUserGroupTable.refreshTable();
				userGroupTable.refreshTable();
			});
		});
	}

	private void updateDatasetUserPermissions()
	{
		datasetUserPermissionsPanel.clear();
		datasetName.setSubText(dataset.getName());
		datasetPermissionsWrapper.setVisible(dataset != null);
		newDatasetUserPermissionsTable.clear();

		UserTable userTable = new UserTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<GatekeeperUser>>> callback)
			{
				return UserService.Inst.get().getUsersForFilter(Cookie.getRequestProperties(), pagination, filter, dataset.getId(), GerminateDatabaseTable.datasets, callback);
			}
		};

		datasetUserPermissionsPanel.add(userTable);

		if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
		{
			ButtonGroup group = new ButtonGroup();

			Button deleteUser = new Button(Text.LANG.generalRemove(), e ->
			{
				Set<? extends DatabaseObject> selectedItems = userTable.getSelection();

				if (selectedItems.size() < 1)
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
					return;
				}

				List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

				UserService.Inst.get().removeFromDataset(Cookie.getRequestProperties(), dataset.getId(), ids, null, new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onSuccessImpl(Void result)
					{
						super.onSuccessImpl(result);

						JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DATASET_PERMISSIONS, "removeItemsUser", Long.toString(dataset.getId()), ids.size());

						userTable.refreshTable();
					}
				});
			});
			deleteUser.addStyleName(Style.mdiLg(Style.MDI_DELETE));

			group.add(deleteUser);
			userTable.addExtraContent(group);
		}

		UserTable newUserTable = new UserTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<GatekeeperUser>>> callback)
			{
				return UserService.Inst.get().getUsersForFilter(Cookie.getRequestProperties(), pagination, filter, null, GerminateDatabaseTable.datasets, callback);
			}
		};
		newDatasetUserPermissionsTable.add(newUserTable);

		if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
		{
			ButtonGroup group = new ButtonGroup();

			Button addUser = new Button(Text.LANG.userGroupsAddUser(), e ->
			{
				Set<? extends DatabaseObject> selectedItems = newUserTable.getSelection();

				if (selectedItems.size() < 1)
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
					return;
				}

				List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

				UserService.Inst.get().addToDataset(Cookie.getRequestProperties(), dataset.getId(), ids, null, new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onSuccessImpl(Void result)
					{
						super.onSuccessImpl(result);

						JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DATASET_PERMISSIONS, "addItemsUser", Long.toString(dataset.getId()), ids.size());

						userTable.refreshTable();
						newUserTable.setSelection(null);
					}
				});
			});
			addUser.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));

			group.add(addUser);
			newUserTable.addExtraContent(group);
		}
	}

	interface GroupsPageUiBinder extends UiBinder<FlowPanel, DatasetPermissionsPage>
	{
	}
}