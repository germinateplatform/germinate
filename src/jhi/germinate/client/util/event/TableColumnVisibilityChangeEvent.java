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

/**
 * A {@link TableColumnVisibilityChangeEvent} indicates that an {@link Exception} has been received from the server
 *
 * @author Sebastian Raubach
 */
public class TableColumnVisibilityChangeEvent extends GwtEvent<TableColumnVisibilityChangeEvent.TableColumnVisibilityChangeEventHandler>
{
	/**
	 * {@link TableColumnVisibilityChangeEventHandler} is the {@link EventHandler} of {@link TableColumnVisibilityChangeEvent}
	 */
	public interface TableColumnVisibilityChangeEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link TableColumnVisibilityChangeEvent} has been fired
		 *
		 * @param event The {@link TableColumnVisibilityChangeEvent}
		 */
		void onStyleChange(TableColumnVisibilityChangeEvent event);
	}

	public static final Type<TableColumnVisibilityChangeEventHandler> TYPE = new Type<>();

	private String sourceId;
	private String columnStyle;

	/**
	 * Creates a new instance of {@link TableColumnVisibilityChangeEvent}
	 *
	 * @param columnStyle The Style of the column that caused the {@link TableColumnVisibilityChangeEvent}
	 */
	public TableColumnVisibilityChangeEvent(String sourceId, String columnStyle)
	{
		this.sourceId = sourceId;
		this.columnStyle = columnStyle;
	}

	public String getColumnStyle()
	{
		return columnStyle;
	}

	public String getSourceId()
	{
		return sourceId;
	}

	@Override
	public Type<TableColumnVisibilityChangeEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(TableColumnVisibilityChangeEventHandler handler)
	{
		handler.onStyleChange(this);
	}
}
