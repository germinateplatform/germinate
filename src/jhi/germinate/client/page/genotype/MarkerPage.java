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
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class MarkerPage extends Composite implements HasHyperlinkButton, HasHelp
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

		PartialSearchQuery filter = null;

		if (markerId != null)
			filter = new PartialSearchQuery(new SearchCondition(Marker.ID, new Equal(), Long.toString(markerId), Long.class));
		else if (!StringUtils.isEmpty(markerName))
			filter = new PartialSearchQuery(new SearchCondition(Marker.MARKER_NAME, new Equal(), markerName, String.class));

		if (filter != null)
		{
			MarkerService.Inst.get().getMarkerForFilter(Cookie.getRequestProperties(), Pagination.getDefault(), filter, new DefaultAsyncCallback<PaginatedServerResult<List<Marker>>>()
			{
				@Override
				protected void onSuccessImpl(PaginatedServerResult<List<Marker>> result)
				{
					if (result.hasData())
					{
						marker = result.getServerResult().get(0);
						setUpDetails();
					}
					else
					{
						clearContent();
					}
				}
			});
		}
		else
		{
			clearContent();
		}
	}

	private void clearContent()
	{
		content.clear();
		content.add(new Heading(HeadingSize.H3, Text.LANG.notificationNoMapOrMarker()));
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

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.markersHelp());
	}
}