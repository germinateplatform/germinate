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

package jhi.germinate.client.util.event;

import com.google.gwt.event.shared.*;

/**
 * A {@link UserGroupChangeEvent} indicates that the user navigated to a new page.
 *
 * @author Sebastian Raubach
 */
public class UserGroupChangeEvent extends GwtEvent<UserGroupChangeEvent.GroupCreationEventHandler>
{
	public static final Type<GroupCreationEventHandler> TYPE = new Type<>();

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public UserGroupChangeEvent()
	{
	}

	@Override
	public Type<GroupCreationEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(GroupCreationEventHandler handler)
	{
		handler.onUserGroupsChanged(this);
	}

	/**
	 * {@link GroupCreationEventHandler} is the {@link EventHandler} of {@link UserGroupChangeEvent}
	 */
	public interface GroupCreationEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link UserGroupChangeEvent} has been fired
		 *
		 * @param event The {@link UserGroupChangeEvent}
		 */
		void onUserGroupsChanged(UserGroupChangeEvent event);
	}
}
