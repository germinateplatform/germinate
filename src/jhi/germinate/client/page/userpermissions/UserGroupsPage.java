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

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.groups.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class UserGroupsPage extends Composite
{
	private static GroupsPageUiBinder ourUiBinder = GWT.create(GroupsPageUiBinder.class);
	private final  UserGroupTable     groupTable;
	@UiField
	HTML        userGroupsHtml;
	@UiField
	Heading     groupName;
	@UiField
	SimplePanel groupTablePanel;
	@UiField
	FlowPanel   groupMembersWrapper;
	@UiField
	SimplePanel groupMembersPanel;
	@UiField
	SimplePanel newGroupMembersTable;


	private UserGroup group;

	public UserGroupsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		userGroupsHtml.setHTML(Text.LANG.userGroupsText());

		groupTable = new UserGroupTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<UserGroup>>> callback)
			{
				return UserGroupService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, new AsyncCallback<PaginatedServerResult<List<UserGroup>>>()
				{
					@Override
					public void onFailure(Throwable throwable)
					{
						if (throwable instanceof InsufficientPermissionsException)
						{
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationActionInsufficientPermissions());
						}
						else
						{
							callback.onFailure(throwable);
						}
					}

					@Override
					public void onSuccess(PaginatedServerResult<List<UserGroup>> result)
					{
						callback.onSuccess(result);
					}
				});
			}

			@Override
			protected void createColumns()
			{
				super.createColumns();

				SafeHtmlCell clickCell = new SafeHtmlCell()
				{
					@Override
					public Set<String> getConsumedEvents()
					{
						Set<String> events = new HashSet<>();
						events.add(BrowserEvents.CLICK);
						return events;
					}
				};

				/* Add the delete column */
				addColumn(new Column<UserGroup, SafeHtml>(clickCell)
				{
					@Override
					public String getCellStyleNames(Cell.Context context, UserGroup row)
					{
						return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
					}

					@Override
					public SafeHtml getValue(UserGroup row)
					{
						return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(Style.combine(Style.MDI_DELETE, Emphasis.DANGER.getCssName()), Text.LANG.generalDelete(), UriUtils.fromString(""), "");
					}

					@Override
					public void onBrowserEvent(Cell.Context context, Element elem, UserGroup object, NativeEvent event)
					{
						if (BrowserEvents.CLICK.equals(event.getType()))
						{
							event.preventDefault();

							AlertDialog.createYesNoDialog(Text.LANG.groupsDeleteTitle(), Text.LANG.groupsDeleteText(), true, e ->
							{
								List<Long> ids = Collections.singletonList(object.getId());

								UserGroupService.Inst.get().delete(Cookie.getRequestProperties(), ids, new DefaultAsyncCallback<DebugInfo>()
								{
									@Override
									protected void onSuccessImpl(DebugInfo result)
									{
										Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationGroupDeleted());
										//										groupTable.refreshTable();

										if (group != null && Objects.equals(object.getId(), group.getId()))
										{
											group = null;
											updateGroupMembers();
										}

										GoogleAnalytics.trackEvent(GoogleAnalytics.Category.USER_GROUPS, "delete", Long.toString(object.getId()));

										GerminateEventBus.BUS.fireEvent(new UserGroupChangeEvent());
									}
								});
							}, null);
						}
						else
						{
							super.onBrowserEvent(context, elem, object, event);
						}
					}
				}, "", false);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			@Override
			protected void onItemSelected(NativeEvent event, UserGroup object, int column)
			{
				event.preventDefault();
				group = object;
				updateGroupMembers();
			}
		};
		groupTable.setHideEmptyTable(false);
		groupTablePanel.add(groupTable);

		if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
		{
			ButtonGroup group = new ButtonGroup();

			Button addGroup = new Button(Text.LANG.groupsButtonAddGroup(), e ->
			{
				AddGroupDialog content = new AddGroupDialog(null, null);

				new AlertDialog(Text.LANG.groupsSubtitleNewGroup())
						.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalAdd(), Style.MDI_PLUS_BOX, ButtonType.SUCCESS, ev -> addNewGroup(content.getName(), content.getDescription())))
						.setContent(content)
						.open();
			});
			addGroup.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));

			group.add(addGroup);
			groupTable.addExtraContent(group);
		}
	}

	private void updateGroupMembers()
	{
		groupMembersPanel.clear();
		newGroupMembersTable.clear();
		groupMembersWrapper.setVisible(group != null);

		if (group != null)
		{
			groupName.setSubText(group.getName());
			UserTable userTable = new UserTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true)
			{
				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<GatekeeperUser>>> callback)
				{
					return UserService.Inst.get().getUsersForFilter(Cookie.getRequestProperties(), pagination, filter, group.getId(), GerminateDatabaseTable.usergroups, callback);
				}
			};

			groupMembersPanel.add(userTable);

			if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
			{
				ButtonGroup g = new ButtonGroup();

				Button deleteUser = new Button(Text.LANG.generalRemove(), e ->
				{
					Set<? extends DatabaseObject> selectedItems = userTable.getSelection();

					if (selectedItems.size() < 1)
					{
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
						return;
					}

					List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

					UserService.Inst.get().removeFromGroup(Cookie.getRequestProperties(), group.getId(), ids, new DefaultAsyncCallback<Void>()
					{
						@Override
						protected void onSuccessImpl(Void result)
						{
							super.onSuccessImpl(result);

							userTable.refreshTable();
							//							groupTable.refreshTable();

							GoogleAnalytics.trackEvent(GoogleAnalytics.Category.USER_GROUPS, "removeItems", Long.toString(group.getId()), ids.size());

							GerminateEventBus.BUS.fireEvent(new UserGroupChangeEvent());
						}
					});
				});
				deleteUser.addStyleName(Style.mdiLg(Style.MDI_DELETE));

				g.add(deleteUser);
				userTable.addExtraContent(g);
			}

			UserTable newUserTable = new UserTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true)
			{
				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<GatekeeperUser>>> callback)
				{
					return UserService.Inst.get().getUsersForFilter(Cookie.getRequestProperties(), pagination, filter, null, GerminateDatabaseTable.usergroups, callback);
				}
			};
			newGroupMembersTable.add(newUserTable);

			if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
			{
				ButtonGroup g = new ButtonGroup();

				Button addUser = new Button(Text.LANG.userGroupsAddUser(), e ->
				{
					Set<? extends DatabaseObject> selectedItems = newUserTable.getSelection();

					if (selectedItems.size() < 1)
					{
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
						return;
					}

					List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

					UserService.Inst.get().addToGroup(Cookie.getRequestProperties(), group.getId(), ids, new DefaultAsyncCallback<Void>()
					{
						@Override
						protected void onSuccessImpl(Void result)
						{
							super.onSuccessImpl(result);

							userTable.refreshTable();
							//							groupTable.refreshTable();
							newUserTable.setSelection(null);

							GoogleAnalytics.trackEvent(GoogleAnalytics.Category.USER_GROUPS, "addItems", Long.toString(group.getId()), ids.size());

							GerminateEventBus.BUS.fireEvent(new UserGroupChangeEvent());
						}
					});
				});
				addUser.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));

				g.add(addUser);
				newUserTable.addExtraContent(g);
			}
		}
	}

	/**
	 * Adds a new group to the database
	 *
	 * @param newGroup The new group name
	 */
	private void addNewGroup(String newGroup, String newGroupDescription)
	{
		if (StringUtils.isEmpty(newGroup))
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupsCannotBeEmpty());
			return;
		}

		final String strippedName = HTMLUtils.stripHtmlTags(newGroup);
		final String strippedDescription = HTMLUtils.stripHtmlTags(newGroupDescription);

		UserGroup g = new UserGroup().setName(strippedName)
									 .setDescription(strippedDescription);

		UserGroupService.Inst.get().createNew(Cookie.getRequestProperties(), g, new DefaultAsyncCallback<ServerResult<UserGroup>>()
		{
			@Override
			public void onSuccessImpl(ServerResult<UserGroup> result)
			{
				GoogleAnalytics.trackEvent(GoogleAnalytics.Category.USER_GROUPS, "create", Long.toString(result.getServerResult().getId()));
				group = result.getServerResult();
				//				groupTable.refreshTable();

				GerminateEventBus.BUS.fireEvent(new UserGroupChangeEvent());
			}
		});
	}

	interface GroupsPageUiBinder extends UiBinder<FlowPanel, UserGroupsPage>
	{
	}
}