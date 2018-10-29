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

package jhi.germinate.client.page.groups;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.resource.*;
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
public class GroupsPage extends Composite implements ParallaxBannerPage, HasHyperlinkButton, HasHelp
{
	interface GroupsPageUiBinder extends UiBinder<FlowPanel, GroupsPage>
	{
	}

	private static GroupsPageUiBinder ourUiBinder = GWT.create(GroupsPageUiBinder.class);

	@UiField
	SimplePanel    tablePanel;
	@UiField
	FlowPanel      groupMembersWrapper;
	@UiField
	SimplePanel    groupMembersPanel;
	@UiField
	FlowPanel      newGroupMembersPanel;
	@UiField
	SimplePanel    newGroupMembersTable;
	@UiField
	Button         download;
	@UiField
	ToggleSwitch   isPublic;
	@UiField
	Heading        groupName;
	@UiField
	FlowPanel      descriptionPanel;
	@UiField
	ParagraphPanel description;

	private final GroupTable                       groupTable;
	private       Button                           addGroup;
	private       Button                           uploadGroupMember;
	private       Button                           deleteGroupMember;
	private       DatabaseObjectPaginationTable<?> table;
	private       AlertDialog                      uploadAlertDialog;

	private Group           group;
	private List<GroupType> groupTypes;

	public GroupsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long groupId = LongParameterStore.Inst.get().get(Parameter.groupId);

		GroupService.Inst.get().getTypes(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<List<GroupType>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<GroupType>> result)
			{
				groupTypes = result.getServerResult();
				updateContent();
			}
		});

		groupTable = new GroupTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			boolean initialLoad = true;

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Group>>> callback)
			{
				return GroupService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, new AsyncCallback<PaginatedServerResult<List<Group>>>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(PaginatedServerResult<List<Group>> result)
					{
						callback.onSuccess(result);

						if (groupId != null && initialLoad && !CollectionUtils.isEmpty(result.getServerResult()))
						{
							for (Group g : result.getServerResult())
							{
								if (Objects.equals(g.getId(), groupId))
								{
									group = g;
									break;
								}
							}

							if (group != null)
								updateGroupMembers(false);
						}

						initialLoad = false;
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
				addColumn(new Column<Group, SafeHtml>(clickCell)
				{
					@Override
					public String getCellStyleNames(Cell.Context context, Group row)
					{
						return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
					}

					@Override
					public SafeHtml getValue(Group row)
					{
						if (canEdit(row))
							return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(Style.combine(Style.MDI_DELETE, Emphasis.DANGER.getCssName()), Text.LANG.generalDelete(), UriUtils.fromString(""), "");
						else
							return SimpleHtmlTemplate.INSTANCE.empty();
					}

					@Override
					public void onBrowserEvent(Cell.Context context, Element elem, Group object, NativeEvent event)
					{
						if (BrowserEvents.CLICK.equals(event.getType()) && canEdit(object))
						{
							event.preventDefault();

							AlertDialog.createYesNoDialog(Text.LANG.groupsDeleteTitle(), Text.LANG.groupsDeleteText(), true, e ->
							{
								List<Long> ids = Collections.singletonList(object.getId());

								GroupService.Inst.get().delete(Cookie.getRequestProperties(), ids, new DefaultAsyncCallback<DebugInfo>()
								{
									@Override
									protected void onSuccessImpl(DebugInfo result)
									{
										Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationGroupDeleted());
										groupTable.refreshTable();

										JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "delete", Long.toString(group.getId()));

										if (group != null && Objects.equals(object.getId(), group.getId()))
										{
											group = null;
											updateGroupMembers(false);
										}
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
			protected void onItemSelected(NativeEvent event, Group object, int column)
			{
				super.onItemSelected(event, object, column);
				group = object;
				updateGroupMembers(true);
			}
		};
		groupTable.setHideEmptyTable(false);
		tablePanel.add(groupTable);

		if (!GerminateSettingsHolder.get().isReadOnlyMode.getValue() && ModuleCore.getUseAuthentication())
		{
			ButtonGroup group = new ButtonGroup();

			addGroup = new Button(Text.LANG.groupsButtonAddGroup(), e ->
			{
				AddGroupDialog content = new AddGroupDialog(groupTypes, null);

				new AlertDialog(Text.LANG.groupsSubtitleNewGroup())
						.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalAdd(), Style.MDI_PLUS_BOX, ButtonType.SUCCESS, ev -> addNewGroup(content.getName(), content.getDescription(), content.getType())))
						.setContent(content)
						.open();
			});
			addGroup.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));
			addGroup.setEnabled(false);

			group.add(addGroup);
			groupTable.addExtraContent(group);
		}

		GerminateEventBus.BUS.addHandler(GroupMemberChangeEvent.TYPE, e -> updateGroupMembers(false));
	}

	private void updateIsPublic()
	{
		isPublic.setEnabled(true);
		isPublic.setValue(group != null && group.getVisibility());
		isPublic.setEnabled(canEdit(group));
	}

	private void updateGroupMembers(boolean scrollTo)
	{
		if (group == null)
		{
			groupMembersWrapper.setVisible(false);
			return;
		}

		groupName.setText(group.getName());
		groupName.setSubText(group.getType().getDescription());
		descriptionPanel.setVisible(!StringUtils.isEmpty(group.getDescription()));
		description.setText(StringUtils.toEmptyIfNull(group.getDescription()));

		if (uploadAlertDialog != null)
			uploadAlertDialog.close();

		groupMembersWrapper.setVisible(true);

		DatabaseObjectPaginationTable.SelectionMode mode = DatabaseObjectPaginationTable.SelectionMode.NONE;

		boolean canEdit = canEdit(group);
		if (canEdit)
			mode = DatabaseObjectPaginationTable.SelectionMode.MULTI;

		updateIsPublic();
		groupMembersPanel.clear();
		newGroupMembersPanel.setVisible(false);
		newGroupMembersTable.clear();

		switch (group.getType().getTargetTable())
		{
			case germinatebase:
				table = new AccessionTable(mode, true)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
					{
						return GroupService.Inst.get().getAccessionItems(Cookie.getRequestProperties(), group.getId(), pagination, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						GroupService.Inst.get().getAccessionItemIds(Cookie.getRequestProperties(), group.getId(), callback);
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
			case locations:
				table = new LocationTable(mode, true)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Location>>> callback)
					{
						return GroupService.Inst.get().getLocationItems(Cookie.getRequestProperties(), group.getId(), pagination, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						GroupService.Inst.get().getLocationItemIds(Cookie.getRequestProperties(), group.getId(), callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}
				};
				break;
			case markers:
				table = new MarkerTable(mode, true)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Marker>>> callback)
					{
						return GroupService.Inst.get().getMarkerItems(Cookie.getRequestProperties(), group.getId(), pagination, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						GroupService.Inst.get().getMarkerItemIds(Cookie.getRequestProperties(), group.getId(), callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}
				};
				break;
		}

		table.setHideEmptyTable(false);
		if (scrollTo)
		{
			table.getPanel().addAttachHandler(event ->
			{
				if (event.isAttached())
					JavaScript.smoothScrollTo(groupMembersWrapper.getElement());
			});
		}
		groupMembersPanel.add(table);

		if (canEdit)
		{
			ButtonGroup buttonGroup = new ButtonGroup();
			deleteGroupMember = new Button(Text.LANG.groupsButtonDeleteMembers(), e ->
			{
				Set<? extends DatabaseObject> selectedItems = table.getSelection();

				if (selectedItems.size() < 1)
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupsSelectAtLeastOne());
					return;
				}

				List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

				AlertDialog.createYesNoDialog(Text.LANG.groupMembersDeleteTitle(), Text.LANG.groupMembersDeleteText(), true, event ->
				{
					GroupService.Inst.get().removeItems(Cookie.getRequestProperties(), group.getId(), ids, new DefaultAsyncCallback<DebugInfo>()
					{
						@Override
						public void onFailureImpl(Throwable caught)
						{
							/* If the user doesn't have the permissions to delete the group */
							if (caught instanceof InsufficientPermissionsException)
							{
								Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupsInsufficientPermissions());
							}
							else
							{
								super.onFailureImpl(caught);
							}
						}

						@Override
						public void onSuccessImpl(DebugInfo result)
						{
							JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "deleteItems", Long.toString(group.getId()), ids.size());

							Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationGroupItemsDeleted());

							table.refreshTable();
						}
					});
				}, null);
			});
			deleteGroupMember.addStyleName(Style.mdiLg(Style.MDI_DELETE));

			updateNewGroupMembersPanel();

			uploadGroupMember = new Button(Text.LANG.groupsButtonUploadMembers(), e ->
			{
				if (uploadAlertDialog == null)
				{
					GroupUploadWidget w = new GroupUploadWidget(group);
					uploadAlertDialog = new AlertDialog(Text.LANG.groupsButtonUploadMembers(), w)
							.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalUpload(), Style.MDI_UPLOAD, ButtonType.PRIMARY, ev -> w.onUploadButtonClicked()))
							.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalCancel(), Style.MDI_CANCEL, null))
							.setAutoCloseOnPositive(false)
							.setRemoveOnHide(false);
				}
				uploadAlertDialog.open();
			});
			uploadGroupMember.addStyleName(Style.mdiLg(Style.MDI_UPLOAD));

			buttonGroup.add(deleteGroupMember);
			buttonGroup.add(uploadGroupMember);
			table.addExtraContent(buttonGroup);
		}
	}

	private void updateNewGroupMembersPanel()
	{
		newGroupMembersPanel.setVisible(true);
		newGroupMembersTable.clear();

		DatabaseObjectPaginationTable<? extends DatabaseObject> result = null;

		switch (group.getType().getTargetTable())
		{
			case germinatebase:
				result = new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
					{
						return AccessionService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						AccessionService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}

					@Override
					protected boolean supportsFiltering()
					{
						return true;
					}
				};
				break;
			case markers:
				result = new MapDefinitionTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback)
					{
						return MarkerService.Inst.get().getMapDefinitionForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						MarkerService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}

					@Override
					protected boolean supportsFiltering()
					{
						return true;
					}
				};
				break;
			case locations:
				result = new LocationTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
				{
					@Override
					protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Location>>> callback)
					{
						return LocationService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, pagination, callback);
					}

					@Override
					public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
					{
						LocationService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
					}

					@Override
					public boolean supportsFullIdMarking()
					{
						return true;
					}

					@Override
					protected boolean supportsFiltering()
					{
						return true;
					}
				};
				break;
		}

		final DatabaseObjectPaginationTable<? extends DatabaseObject> t = result;

		ButtonGroup buttonGroup = new ButtonGroup();
		Button addGroupMember = new Button(Text.LANG.generalAdd(), e ->
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

					JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "addItems", Long.toString(group.getId()), result.getServerResult().size());
				}
			});
		});
		addGroupMember.addStyleName(Style.mdiLg(Style.MDI_PLUS_BOX));

		buttonGroup.add(addGroupMember);
		result.addExtraContent(buttonGroup);

		newGroupMembersTable.add(result);
	}

	@Override
	protected void onUnload()
	{
		super.onUnload();

		if (uploadAlertDialog != null)
			uploadAlertDialog.remove();
	}

	/**
	 * Checks if the group editing functionality should be enabled
	 *
	 * @param group The group to check
	 * @return <code>true</code> if the group editing functionality should be enabled
	 */
	public static boolean canEdit(Group group)
	{
		/* If we're in read-only mode or authentication is disabled, don't allow editing */
		if (GerminateSettingsHolder.get().isReadOnlyMode.getValue() || !ModuleCore.getUseAuthentication())
			return false;

		UserAuth auth = ModuleCore.getUserAuth();

		/* Admins can change ecerything */
		if (auth != null && auth.isAdmin())
			return true;

		/* Check if the user created the group */
		if (auth != null && group != null && group.getCreatedBy() != null)
			return Objects.equals(group.getCreatedBy(), ModuleCore.getUserAuth().getId());

		return false;
	}

	private void updateContent()
	{
		addGroup.setEnabled(!CollectionUtils.isEmpty(groupTypes));
	}

	/**
	 * Adds a new group to the database
	 *
	 * @param newGroup The new group name
	 */
	private void addNewGroup(String newGroup, String newGroupDescription, GroupType type)
	{
		if (StringUtils.isEmpty(newGroup))
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupsCannotBeEmpty());
			return;
		}

		final String strippedName = HTMLUtils.stripHtmlTags(newGroup);
		final String strippedDescription = HTMLUtils.stripHtmlTags(newGroupDescription);

		Group g = new Group().setName(strippedName).setDescription(strippedDescription);

		GroupService.Inst.get().createNew(Cookie.getRequestProperties(), g, type.getTargetTable(), new DefaultAsyncCallback<ServerResult<Group>>()
		{
			@Override
			public void onSuccessImpl(ServerResult<Group> result)
			{
				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "create", Long.toString(result.getServerResult().getId()));
				group = result.getServerResult();
				groupTable.refreshTable();
				updateGroupMembers(true);
			}
		});
	}

	@UiHandler("isPublic")
	void onPublicChanged(ValueChangeEvent<Boolean> event)
	{
		if (canEdit(group))
		{
			GroupService.Inst.get().setVisibility(Cookie.getRequestProperties(), group.getId(), event.getValue(), new DefaultAsyncCallback<DebugInfo>()
			{
				@Override
				protected void onFailureImpl(Throwable caught)
				{
					group.setVisibility(!group.getVisibility());
					JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "changeVisibility", Long.toString(group.getId()), group.getVisibility() ? 1 : 0);

					super.onFailureImpl(caught);
				}
			});
		}
	}

	@UiHandler("download")
	void onDownloadClicked(ClickEvent event)
	{
		GroupService.Inst.get().exportForGroupId(Cookie.getRequestProperties(), group.getId(), group.getType().getTargetTable(), new FileDownloadCallback(true));
		JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "download", Long.toString(group.getId()));
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxGroup();
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.GROUPS)
				.addParam(Parameter.groupId);
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.groupsHelp());
	}
}