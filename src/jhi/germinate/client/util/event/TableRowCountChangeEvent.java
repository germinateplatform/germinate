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

/**
 * A {@link TableRowCountChangeEvent} indicates that the user changed the number of rows to display in a table.
 *
 * @author Sebastian Raubach
 */
public class TableRowCountChangeEvent extends GwtEvent<TableRowCountChangeEvent.TableRowCountChangeEventHandler>
{
	/**
	 * {@link LogoutEvent.LogoutEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface TableRowCountChangeEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onPageNavigation(TableRowCountChangeEvent event);
	}

	public static final Type<TableRowCountChangeEventHandler> TYPE = new Type<>();

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public TableRowCountChangeEvent()
	{
	}

	@Override
	public Type<TableRowCountChangeEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(TableRowCountChangeEventHandler handler)
	{
		handler.onPageNavigation(this);
	}
}
