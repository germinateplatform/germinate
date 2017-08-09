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

package jhi.germinate.client.page.genotype;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class MarkerPage extends Composite implements HasHyperlinkButton
{
	interface MarkerPageUiBinder extends UiBinder<HTMLPanel, MarkerPage>
	{
	}

	private static MarkerPageUiBinder ourUiBinder = GWT.create(MarkerPageUiBinder.class);

	@UiField
	HTMLPanel     content;
	@UiField
	Heading       markerName;
	@UiField
	SimplePanel   datasetPanel;
	@UiField
	SimplePanel   mapsPanel;
	@UiField
	SynonymWidget synonymWidget;

	private Marker marker;

	public MarkerPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long markerId = LongParameterStore.Inst.get().get(Parameter.markerId);
		String markerName = StringParameterStore.Inst.get().get(Parameter.markerName);

		if (markerId == null)
		{
			// If we don't have an ID, but we've got the name, then use it to get the marker
			if (markerName != null)
				requestMarkerDetailsFromName(markerName);
			else
				clearContent();
		}
		else
		{
			// If we have an id, then use it to get the details
			requestMarkerDetailsFromId(markerId);
		}
	}

	private void clearContent()
	{
		content.clear();
		content.add(new Heading(HeadingSize.H3, Text.LANG.notificationNoMapOrMarker()));
	}

	/**
	 * Request the complete marker details for the selected marker. On success, the rest of the page will be created.
	 */
	private void requestMarkerDetailsFromId(Long markerId)
	{
		MarkerService.Inst.get().getById(Cookie.getRequestProperties(), markerId, new DefaultAsyncCallback<ServerResult<Marker>>()
		{
			@Override
			public void onSuccessImpl(ServerResult<Marker> result)
			{
				if (result != null)
				{
					marker = result.getServerResult();
					setUpDetails();
				}
				else
				{
					clearContent();
				}
			}
		});
	}

	/**
	 * Request the complete marker details for the selected marker. On success, the rest of the page will be created.
	 */
	private void requestMarkerDetailsFromName(String markerName)
	{
		MarkerService.Inst.get().getByName(Cookie.getRequestProperties(), markerName, new DefaultAsyncCallback<ServerResult<Marker>>()
		{
			@Override
			public void onSuccessImpl(ServerResult<Marker> result)
			{
				if (result != null)
				{
					marker = result.getServerResult();
					setUpDetails();
				}
				else
				{
					clearContent();
				}
			}
		});
	}

	private void setUpDetails()
	{
		markerName.setText(Text.LANG.markersSubtitleDatasets(HTMLUtils.stripHtmlTags(marker.getName())));
		synonymWidget.update(GerminateDatabaseTable.markers, marker.getId());
		requestMarkerDatasets();
		requestMarkerMapDetails();
	}

	/**
	 * Retrieves the maps this marker is on along with the positions
	 */
	private void requestMarkerMapDetails()
	{
		mapsPanel.add(new MapDefinitionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, false)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback)
			{
				return MapService.Inst.get().getDataForMarker(Cookie.getRequestProperties(), marker.getId(), pagination, callback);
			}
		});
	}

	/**
	 * Retrieves the datasets the marker is contained in from the server and displays them in a table. A click handler will be added that shows
	 * further information for the marker and the selected dataset.
	 */
	private void requestMarkerDatasets()
	{
		datasetPanel.add(new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, false, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForMarker(Cookie.getRequestProperties(), pagination, marker.getId(), callback);
			}
		});
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.MARKER_DETAILS)
				.addParam(Parameter.markerId);
	}
}