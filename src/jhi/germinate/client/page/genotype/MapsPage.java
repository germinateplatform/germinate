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
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class MapsPage extends Composite implements HasHyperlinkButton, HasHelp
{
	interface MapsPageUiBinder extends UiBinder<HTMLPanel, MapsPage>
	{
	}

	private static MapsPageUiBinder ourUiBinder = GWT.create(MapsPageUiBinder.class);

	@UiField
	HTML        text;
	@UiField
	FlowPanel   mapPanel;
	@UiField
	FlowPanel   mapDetailsWrapper;
	@UiField
	Heading     mapDetailsHeading;
	@UiField
	HTML        mapDetailsParagraph;
	@UiField
	SimplePanel markerTablePanel;

	@UiField
	FlowPanel             mapHeatmapPanel;
	@UiField
	SimplePanel           chartPanel;
	@UiField
	HTML                  mapHeatmapParagraph;
	@UiField
	MapExportOptionsPanel exportOptionsPanel;

	private Map                map;
	private MapDefinitionTable mapDefinitionTable;

	public MapsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		text.setHTML(Text.LANG.mapsParagraph());
		mapDetailsParagraph.setHTML(Text.LANG.mapsMarkersParagraph());
		mapHeatmapParagraph.setHTML(Text.LANG.mapsHeatmapText());

		mapPanel.add(new MapTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Map>>> callback)
			{
				return MapService.Inst.get().get(Cookie.getRequestProperties(), pagination, callback);
			}

			@Override
			protected void onItemSelected(NativeEvent event, Map object, int column)
			{
				super.onItemSelected(event, object, column);
				map = object;
				updateMapDetails();
				exportOptionsPanel.update(map);
			}
		});

		Long mapId = LongParameterStore.Inst.get().get(Parameter.mapId);
		if (mapId != null)
		{
			MapService.Inst.get().getById(Cookie.getRequestProperties(), mapId, new DefaultAsyncCallback<ServerResult<Map>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Map> result)
				{
					if (result.hasData())
					{
						map = result.getServerResult();
						updateMapDetails();
						exportOptionsPanel.update(result.getServerResult());
					}
					else
					{
						LongParameterStore.Inst.get().remove(Parameter.mapId);
						map = null;
						updateMapDetails();
					}
				}
			});
		}
	}

	private void updateMapDetails()
	{
		mapDetailsHeading.setText(Text.LANG.mapsHeadingMarkers(HTMLUtils.stripHtmlTags(map.getName())));
		mapDetailsWrapper.setVisible(true);

		if (mapDefinitionTable == null)
		{
			mapDefinitionTable = new MapDefinitionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					filter = addToFilter(filter);

					MarkerService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback)
				{
					filter = addToFilter(filter);
					return MarkerService.Inst.get().getMapDefinitionForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
				}

				private PartialSearchQuery addToFilter(PartialSearchQuery filter)
				{
					if (filter == null)
						filter = new PartialSearchQuery();
					SearchCondition condition = new SearchCondition(Map.ID, new Equal(), Long.toString(map.getId()), Long.class);
					filter.add(condition);

					if (filter.getAll().size() > 1)
						filter.addLogicalOperator(new And());

					return filter;
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}
			};
			markerTablePanel.add(mapDefinitionTable);
		}
		else
		{
			mapDefinitionTable.refreshTable();
		}

		chartPanel.clear();
		if (map.getSize() < 100000)
		{
			mapHeatmapPanel.setVisible(true);
			chartPanel.add(new MapChartCanvas(map.getId()));
		}
		else
		{
			mapHeatmapPanel.setVisible(false);
		}
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.MAP_DETAILS)
				.addParam(Parameter.mapId);
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.mapsHelp());
	}
}