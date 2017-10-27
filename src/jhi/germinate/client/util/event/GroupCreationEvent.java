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

package jhi.germinate.client.util.event;

import com.google.gwt.event.shared.*;

import jhi.germinate.client.util.*;

/**
 * A {@link GroupCreationEvent} indicates that the user navigated to a new page.
 *
 * @author Sebastian Raubach
 */
public class GroupCreationEvent extends GwtEvent<GroupCreationEvent.GroupCreationEventHandler>
{
	/**
	 * {@link GroupCreationEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface GroupCreationEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onGroupCreated(GroupCreationEvent event);
	}

	public static final Type<GroupCreationEventHandler> TYPE = new Type<>();

	private MarkedItemList.ItemType type;
	private Long                    id;

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public GroupCreationEvent(MarkedItemList.ItemType type, Long id)
	{
		this.type = type;
		this.id = id;
	}

	public MarkedItemList.ItemType getType()
	{
		return type;
	}

	public Long getId()
	{
		return id;
	}

	@Override
	public Type<GroupCreationEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(GroupCreationEventHandler handler)
	{
		handler.onGroupCreated(this);
	}
}
