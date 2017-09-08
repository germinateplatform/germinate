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

package jhi.germinate.client.widget.listbox;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.markeditemlist.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * GroupListBox extends {@link GerminateValueListBox} and displays {@link Group}s with their size.
 *
 * @author Sebastian Raubach
 */
public class GroupListBox extends GerminateValueListBox<Group>
{
	private MarkedItemList.ItemType type;
	private GroupCreationInterface  groupCreationInterface;
	private HandlerRegistration     groupRegistration;

	private boolean hasCustomFirstValue = false;

	public GroupListBox()
	{
		super(new GerminateUnitRenderer<Group>()
		{
			@Override
			protected String getText(Group object)
			{
				return object.getDescription();
			}

			@Override
			protected String getUnit(Group object)
			{
				return (object.getSize() != null && object.getSize() != 0L) ? Long.toString(object.getSize()) : null;
			}

			@Override
			protected BracketType getBracketType()
			{
				return BracketType.ROUND;
			}
		});

		setMultipleSelect(true);
	}

	public GroupListBox(MarkedItemList.ItemType type)
	{
		this();
		this.type = type;
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		/* If the type is set, the interface is set and the shopping cart contains elements for this type */
		if (groupCreationInterface != null && type != null && !CollectionUtils.isEmpty(MarkedItemList.get(type)) && ModuleCore.getUseAuthentication() && !GerminateSettingsHolder.get().isReadOnlyMode.getValue())
		{
			// Create a new button that users can use to create a new group from this page
			Button createGroup = new Button(Text.LANG.buttonCreateGroupFromCart(), IconType.PLUS_SQUARE, event ->
			{
				// Create the group
				AbstractCartView.askForGroupNameAndCreate(new ArrayList<>(MarkedItemList.get(type)), type, null);
			});
			createGroup.setType(ButtonType.DEFAULT);

			createGroup.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			additionalButtonPanel.add(createGroup);
			additionalButtonPanel.setVisible(true);

			groupRegistration = GerminateEventBus.BUS.addHandler(GroupCreationEvent.TYPE, event ->
			{
				/* Get the updated group data */
				groupCreationInterface.updateData(new DefaultAsyncCallback<ServerResult<List<Group>>>()
												  {
													  @Override
													  protected void onSuccessImpl(ServerResult<List<Group>> result)
													  {
														  // Update the list and select the new group
														  GroupListBox.this.clear();

														  Group toSelect = result.getServerResult().get(0);

														  for (Group group : result.getServerResult())
														  {
															  if (group.getId().equals(event.getId()))
															  {
																  toSelect = group;
																  break;
															  }
														  }

														  GroupListBox.this.setValue(toSelect, false);
														  GroupListBox.this.setAcceptableValues(result.getServerResult());
													  }
												  }
				);
			});
		}
	}

	@Override
	public void setAcceptableValues(Collection<Group> newValues)
	{
		Group allItems = new Group(-1L);
		switch (type)
		{
			case MARKER:
				allItems.setDescription(Text.LANG.groupsAllMarkers());
				break;
			case ACCESSION:
				allItems.setDescription(Text.LANG.groupsAllAccessions());
				break;
			case LOCATION:
				allItems.setDescription(Text.LANG.groupsAllLocations());
				break;
		}

		List<Group> all = new ArrayList<>();
		all.add(allItems);

		if (!CollectionUtils.isEmpty(newValues))
			all.addAll(newValues);

		if (!hasCustomFirstValue)
			setValue(allItems, false);
		super.setAcceptableValues(all);
	}

	public void setAcceptableValues(boolean hasCustomFirstValue, List<Group> groups)
	{
		this.hasCustomFirstValue = hasCustomFirstValue;
		setAcceptableValues(groups);
	}

	@Override
	public void setAcceptableValues(Group[] newValues)
	{
		setAcceptableValues(Arrays.asList(newValues));
	}

	@Override
	protected void onUnload()
	{
		super.onUnload();

		if (groupRegistration != null)
			groupRegistration.removeHandler();
	}

	public MarkedItemList.ItemType getType()
	{
		return type;
	}

	public void setType(MarkedItemList.ItemType type)
	{
		this.type = type;
	}

	public void setGroupCreationInterface(GroupCreationInterface groupCreationInterface)
	{
		this.groupCreationInterface = groupCreationInterface;
	}

	public interface GroupCreationInterface
	{
		void updateData(AsyncCallback<ServerResult<List<Group>>> callback);
	}
}
