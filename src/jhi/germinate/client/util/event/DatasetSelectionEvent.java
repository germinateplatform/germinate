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

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;

/**
 * A {@link DatasetSelectionEvent} indicates that the user selected some datasets.
 *
 * @author Sebastian Raubach
 */
public class DatasetSelectionEvent extends GwtEvent<DatasetSelectionEvent.DatasetSelectionEventHandler>
{
	public static final Type<DatasetSelectionEventHandler> TYPE = new Type<>();
	private             List<Dataset>                      datasets;

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public DatasetSelectionEvent(List<Dataset> datasets)
	{
		this.datasets = datasets;
	}

	public List<Dataset> getDatasets()
	{
		return datasets;
	}

	@Override
	public Type<DatasetSelectionEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(DatasetSelectionEventHandler handler)
	{
		handler.onDatasetSelected(this);
	}

	/**
	 * {@link DatasetSelectionEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface DatasetSelectionEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onDatasetSelected(DatasetSelectionEvent event);
	}
}
