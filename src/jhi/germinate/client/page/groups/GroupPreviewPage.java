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

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.markeditemlist.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class GroupPreviewPage extends Composite
{
	interface GroupPreviewPageUiBinder extends UiBinder<HTMLPanel, GroupPreviewPage>
	{
	}

	private static GroupPreviewPageUiBinder ourUiBinder = GWT.create(GroupPreviewPageUiBinder.class);

	@UiField
	HTMLPanel   root;
	@UiField
	HTMLPanel   resultPanel;
	@UiField
	SimplePanel tablePanel;
	@UiField
	Button      createGroupButton;

	private String filename;

	public GroupPreviewPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		filename = StringParameterStore.Inst.get().get(Parameter.groupPreviewFile);

		if (StringUtils.isEmpty(filename))
		{
			root.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoDataFound()));
		}
		else
		{
			initTable();

			if (GerminateSettingsHolder.get().isReadOnlyMode.getValue())
				createGroupButton.removeFromParent();
		}
	}

	/**
	 * Creates all the important controls and lays them out
	 */
	private void initTable()
	{
		/* Create the appropriate table for the given group type */
		final AccessionTable table = new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, false)
		{
			@Override
			protected boolean supportsFiltering()
			{
				return false;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
			{
				return AccessionService.Inst.get().getForGroupPreview(Cookie.getRequestProperties(), pagination, filename, callback);
			}

			@Override
			protected boolean preventAllItemMarking()
			{
				return true;
			}
		};

		resultPanel.setVisible(true);
		tablePanel.add(table);

		/* Add a delete button below the table that starts a server call to delete the selected items from the group */
		ButtonGroup buttonGroup = new ButtonGroup();
		Button deleteButton = new Button(Text.LANG.generalRemove(), e ->
		{
			Set<? extends DatabaseObject> selectedItems = table.getSelection();

			List<Long> ids = DatabaseObject.getGroupSpecificIds(selectedItems);

			if (!CollectionUtils.isEmpty(ids))
			{
				AccessionService.Inst.get().removeFromGroupPreview(Cookie.getRequestProperties(), ids, filename, new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onSuccessImpl(Void result)
					{
						table.refreshTable();
					}
				});
			}
		});
		deleteButton.addStyleName(Style.mdiLg(Style.MDI_DELETE));
		buttonGroup.add(deleteButton);

		table.addExtraContent(buttonGroup);
	}

	@UiHandler("createGroupButton")
	void onCreateGroupButtonClicked(ClickEvent e)
	{
		AbstractCartView.askForGroupNameAndCreate(null, MarkedItemList.ItemType.ACCESSION, new DefaultAsyncCallback<ServerResult<Group>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<Group> result)
			{
				addGroupMembers(result.getServerResult().getId());
			}
		});
	}

	private void addGroupMembers(final Long groupId)
	{
		GroupService.Inst.get().addItemsFromPreview(Cookie.getRequestProperties(), groupId, filename, new DefaultAsyncCallback<ServerResult<Integer>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				if (caught instanceof InvalidArgumentException)
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupPreviewInvalidFilename());
				}
				else
				{
					super.onFailureImpl(caught);
				}
			}

			@Override
			protected void onSuccessImpl(ServerResult<Integer> result)
			{
				AccessionService.Inst.get().clearGroupPreview(Cookie.getRequestProperties(), filename, new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onFailureImpl(Throwable caught)
					{
						StringParameterStore.Inst.get().remove(Parameter.groupPreviewFile);
					}

					@Override
					protected void onSuccessImpl(Void result)
					{
						StringParameterStore.Inst.get().remove(Parameter.groupPreviewFile);
					}
				});

				Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationGroupItemsAddedIgnored(result.getServerResult(), 0));

				LongParameterStore.Inst.get().put(Parameter.groupId, groupId);

				History.newItem(Page.GROUPS.name());
			}
		});
	}
}