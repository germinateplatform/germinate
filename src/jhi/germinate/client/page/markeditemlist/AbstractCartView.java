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

package jhi.germinate.client.page.markeditemlist;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.groups.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public abstract class AbstractCartView<T extends DatabaseObject> extends GerminateComposite
{
	private DatabaseObjectPaginationTable<T> table;
	private HandlerRegistration              groupRegistration;

	protected static List<GroupType> types;

	protected abstract MarkedItemList.ItemType getItemType();

	protected abstract DatabaseObjectPaginationTable<T> getTable(List<String> markedIds);

	protected abstract void writeToFile(List<String> markedIds, AsyncCallback<ServerResult<String>> callback);

	@Override
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	public void onUnload()
	{
		super.onUnload();

		if (groupRegistration != null)
			groupRegistration.removeHandler();
	}

	public static void askForGroupNameAndCreate(List<String> newGroupMembers, MarkedItemList.ItemType itemType, AsyncCallback<ServerResult<Group>> callback)
	{
		GroupService.Inst.get().getTypes(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<List<GroupType>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<GroupType>> result)
			{
				types = result.getServerResult();

				GroupType type = null;

				for (GroupType t : types)
				{
					if (itemType.getTarget() == t.getTargetTable())
					{
						type = t;
						break;
					}
				}

				AddGroupDialog content = new AddGroupDialog(types, type);

				final AlertDialog dialog = new AlertDialog(Text.LANG.groupsSubtitleNewGroup());
				dialog.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalAdd(), Style.MDI_PLUS_BOX, ButtonType.SUCCESS, ev ->
				{
					if (addNewGroup(content.getName(), content.getDescription(), newGroupMembers, itemType, callback))
						dialog.close();
				}))
					  .setAutoCloseOnPositive(false)
					  .setContent(content);

				dialog.open();
			}
		});
	}

	/**
	 * Adds a new group to the database
	 *
	 * @param newGroup The new group name
	 */
	private static boolean addNewGroup(String newGroup, String description, final List<String> newGroupMembers, MarkedItemList.ItemType type, AsyncCallback<ServerResult<Group>> callback)
	{
		if (StringUtils.isEmpty(newGroup))
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupsCannotBeEmpty());
			return false;
		}

		final String strippedString = HTMLUtils.stripHtmlTags(newGroup);

		Group g = new Group().setName(strippedString).setDescription(description);

		GroupService.Inst.get().createNew(Cookie.getRequestProperties(), g, type.getTarget(), new DefaultAsyncCallback<ServerResult<Group>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				if (callback != null)
					callback.onFailure(caught);
				else
					super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(ServerResult<Group> result)
			{
				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.GROUPS, "create", strippedString);

				if (!CollectionUtils.isEmpty(newGroupMembers))
					addGroupMembers(newGroupMembers, result.getServerResult().getId(), type);

				if (callback != null)
					callback.onSuccess(result);
			}
		});

		return true;
	}

	@Override
	protected void setUpContent()
	{
		final List<String> markedIds = getItemType().getMarkedIds();

		final FlowPanel content = new FlowPanel();
		panel.add(content);
		content.add(new Heading(HeadingSize.H3, Text.LANG.cartTitle()));

		if (markedIds == null || markedIds.size() < 1)
		{
			content.add(new Paragraph(Text.LANG.cartEmpty()));
		}
		else
		{
			content.add(new Paragraph(Text.LANG.cartText()));

			ButtonGroup buttonBar = new ButtonGroup();
			Button clear = new Button(Text.LANG.generalClear(), r ->
			{
				MarkedItemList.clear(getItemType());
				History.fireCurrentHistoryState();
			});
			clear.addStyleName(Style.mdiLg(Style.MDI_DELETE));
			buttonBar.add(clear);

			/* If logged in and groups page is available, add a create group
			 * button */
			if (ModuleCore.getUseAuthentication() && !GerminateSettingsHolder.get().isReadOnlyMode.getValue() && GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
			{
				GroupService.Inst.get().getTypes(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<List<GroupType>>>()
				{
					@Override
					protected void onSuccessImpl(ServerResult<List<GroupType>> result)
					{
						types = result.getServerResult();

						groupRegistration = GerminateEventBus.BUS.addHandler(GroupCreationEvent.TYPE, e -> History.fireCurrentHistoryState());

						Button createGroup = new Button(Text.LANG.cartCreateGroup(), e -> askForGroupNameAndCreate(markedIds, getItemType(), null));
						createGroup.addStyleName(Style.mdiLg(Style.MDI_GROUP));
						buttonBar.add(createGroup);
					}
				});
			}

			content.add(buttonBar);

			final FlowPanel tablePanel = new FlowPanel();
			content.add(tablePanel);

			table = getTable(markedIds);
			tablePanel.add(table);

			DownloadWidget widget = new DownloadWidget(Text.LANG.downloadHeading())
			{
				@Override
				protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
				{
					writeToFile(new ArrayList<>(MarkedItemList.get(getItemType())), callback);
				}
			};
			widget.add(new DownloadWidget.FileConfig(Text.LANG.downloadFileAsTxt()).setType(FileType.txt).setLongRunning(true));

			tablePanel.add(widget);
		}
	}

	private static void addGroupMembers(List<String> newGroupMembers, Long groupId, MarkedItemList.ItemType type)
	{
		List<Long> ids = CollectionUtils.convertToLong(newGroupMembers);

		GroupService.Inst.get().addItems(Cookie.getRequestProperties(), groupId, ids, new DefaultAsyncCallback<ServerResult<Set<Long>>>(true)
		{
			@Override
			public void onSuccessImpl(ServerResult<Set<Long>> result)
			{
				Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationGroupItemsAdded());

				MarkedItemList.clear(type);

				GerminateEventBus.BUS.fireEvent(new GroupCreationEvent(type, groupId));
			}
		});
	}
}
