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

package jhi.germinate.client.util.event;

import com.google.gwt.event.shared.*;

import java.util.*;

import jhi.germinate.client.util.*;

/**
 * A {@link MarkedItemListEvent} indicates that the user navigated to a new page.
 *
 * @author Sebastian Raubach
 */
public class MarkedItemListEvent extends GwtEvent<MarkedItemListEvent.MarkedItemListEventHandler>
{
	/**
	 * {@link MarkedItemListEvent.MarkedItemListEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface MarkedItemListEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onMarkedItemListChanged(MarkedItemListEvent event);
	}

	public static final Type<MarkedItemListEventHandler> TYPE = new Type<>();

	private MarkedItemList.ItemType type;
	private Collection<String>      ids;

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public MarkedItemListEvent(MarkedItemList.ItemType type, Collection<String> ids)
	{
		this.type = type;
		this.ids = ids;
	}

	public MarkedItemList.ItemType getType()
	{
		return type;
	}

	public Collection<String> getIds()
	{
		return ids;
	}

	@Override
	public Type<MarkedItemListEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(MarkedItemListEventHandler handler)
	{
		handler.onMarkedItemListChanged(this);
	}
}
