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

package jhi.germinate.client.page.geography;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.search.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.LocationType;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;
import jhi.gwt.leaflet.client.basic.*;
import jhi.gwt.leaflet.client.control.*;
import jhi.gwt.leaflet.client.layer.other.*;

/**
 * @author Sebastian Raubach
 */
public class GeographicSearchPage extends Composite implements HasHyperlinkButton, HasLibraries
{
	interface GeographicSearchPageUiBinder extends UiBinder<HTMLPanel, GeographicSearchPage>
	{
	}

	private static GeographicSearchPageUiBinder ourUiBinder = GWT.create(GeographicSearchPageUiBinder.class);

	// POINT TAB CONTENT
	@UiField
	TabListItem   pointTab;
	@UiField
	TabPane       pointTabContent;
	@UiField
	NumberTextBox latitudeBox;
	@UiField
	NumberTextBox longitudeBox;
	@UiField
	SimplePanel   pointMapPanel;

	// POLYOGON TAB CONTENT
	@UiField
	TabListItem polygonTab;
	@UiField
	TabPane     polygonTabContent;
	@UiField
	HTML        polygonHtml;
	@UiField
	SimplePanel polygonMapPanel;

	// CONTINUE
	@UiField
	Button continueButton;

	// RESULTS
	@UiField
	FlowPanel     resultPanel;
	@UiField
	SearchSection accessionSection;
	@UiField
	SearchSection locationSection;

	// FIELDS
	private LeafletDraw                          draw;
	private LeafletUtils.IndividualMarkerCreator pointMap;
	private LeafletUtils.ClusteredMarkerCreator  polygonMap;
	private Location                             queryLocation;
	private LeafletGeodesic                      geodesic;

	public GeographicSearchPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		polygonHtml.setHTML(Text.LANG.geographicSearchPolygonText());

		pointMap = new LeafletUtils.IndividualMarkerCreator(pointMapPanel, null, (mapPanel, map) ->
		{
			mapPanel.getElement().appendChild(new CenteredCrosshairWidget().getElement());

			map.on("move", () ->
			{
				LeafletLatLng latLng = map.getCenter();
				latitudeBox.setValue(latLng.getLatitude());
				longitudeBox.setValue(latLng.getLongitude());

				FloatParameterStore.Inst.get().put(Parameter.latitude, (float) latLng.getLatitude());
				FloatParameterStore.Inst.get().put(Parameter.longitude, (float) latLng.getLongitude());
			});
		});

		polygonMap = new LeafletUtils.ClusteredMarkerCreator(polygonMapPanel, null, (mapPanel, map) ->
		{
			draw = LeafletDraw.newInstance(map, LeafletControlPosition.TOP_RIGHT);
			GQuery.$(".leaflet-draw-draw-polygon").click();
		});

		Float lat = FloatParameterStore.Inst.get().get(Parameter.latitude);
		Float lng = FloatParameterStore.Inst.get().get(Parameter.longitude);

		if (lat != null && lng != null)
		{
			latitudeBox.setValue(Double.valueOf(lat));
			longitudeBox.setValue(Double.valueOf(lng));

			Scheduler.get().scheduleDeferred(() -> continueButton.click());
		}
	}

	@UiHandler("polygonTab")
	void onPolygonTabClicked(ClickEvent event)
	{
		Scheduler.get().scheduleDeferred(() -> polygonMap.getMap().invalidateSize(false));
	}

	@UiHandler("pointTab")
	void onPointTabClicked(ClickEvent event)
	{
		Scheduler.get().scheduleDeferred(() -> pointMap.getMap().invalidateSize(false));
	}

	@UiHandler("continueButton")
	void onContinueClicked(ClickEvent event)
	{
		accessionSection.clear();
		locationSection.clear();
		resultPanel.setVisible(true);

		if (geodesic != null)
			pointMap.getMap().removeLayer(geodesic);

		if (pointTab.isActive())
		{
			Double latitude = latitudeBox.getDoubleValue();
			Double longitude = longitudeBox.getDoubleValue();

			if (latitude == null || longitude == null)
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNotANumber());
				return;
			}

			FloatParameterStore.Inst.get().put(Parameter.latitude, latitude.floatValue());
			FloatParameterStore.Inst.get().put(Parameter.longitude, longitude.floatValue());

			queryLocation = new Location()
					.setLatitude(latitude)
					.setLongitude(longitude)
					.setName(Text.LANG.geographicSearchQuery());

			pointMap.updateData(Collections.singletonList(queryLocation));

			LocationTable locationTable = new LocationTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				@Override
				protected void createColumns()
				{
					super.createColumns();

					/* Add the elevation column */
					TextColumn column = new TextColumn()
					{
						@Override
						public String getValue(Location object)
						{
							return TableUtils.getCellValueAsString(object.getExtra(LocationService.DISTANCE));
						}

						@Override
						public Class getType()
						{
							return Double.class;
						}
					};
					column.setDataStoreName(LocationService.DISTANCE);
					addColumn(column, Text.LANG.collectingsiteDistance(), true);
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					try
					{
						if (filter == null)
							filter = new PartialSearchQuery();
						SearchCondition condition = new SearchCondition();
						condition.setColumnName(jhi.germinate.shared.datastructure.database.LocationType.NAME);
						condition.setComp(new Equal());
						condition.addConditionValue(jhi.germinate.shared.enums.LocationType.collectingsites.name());
						condition.setType(String.class.getSimpleName());
						filter.add(condition);

						if (filter.getAll().size() > 1)
						{
							if (filterOperatorButton.getValue())
								filter.addLogicalOperator(new And());
							else
								filter.addLogicalOperator(new Or());
						}
					}
					catch (InvalidArgumentException | InvalidSearchQueryException e)
					{
						e.printStackTrace();
					}

					LocationService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Location>>> callback)
				{
					if (latitude != null && longitude != null)
						return LocationService.Inst.get().getByDistance(Cookie.getRequestProperties(), latitude, longitude, pagination, new SearchCallback<>(locationSection, callback));
					else
						return null;
				}
			};

			locationSection.add(locationTable);

			locationTable.addMouseHoverHandler(new TableMouseHoverHandler<Location>()
			{
				@Override
				public void onMouseOverRow(Location row)
				{
					if (geodesic != null)
						pointMap.getMap().removeLayer(geodesic);

					LeafletLatLng start = LeafletLatLng.newInstance(queryLocation.getLatitude(), queryLocation.getLongitude());
					LeafletLatLng end = LeafletLatLng.newInstance(row.getLatitude(), row.getLongitude());

					pointMap.updateData(Arrays.asList(queryLocation, row));

					geodesic = LeafletGeodesic.newInstance(start, end)
											  .addTo(pointMap.getMap());
				}

				@Override
				public void onMouseOutRow(Location row)
				{
				}

				@Override
				public void onMouseOverTable()
				{

				}

				@Override
				public void onMouseOutTable()
				{

				}
			});

			accessionSection.add(new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					try
					{
						if (filter == null)
							filter = new PartialSearchQuery();
						SearchCondition condition = new SearchCondition();
						condition.setColumnName(LocationType.NAME);
						condition.setComp(new Equal());
						condition.addConditionValue(jhi.germinate.shared.enums.LocationType.collectingsites.name());
						condition.setType(Long.class.getSimpleName());
						filter.add(condition);

						if (filter.getAll().size() > 1)
						{
							if (filterOperatorButton.getValue())
								filter.addLogicalOperator(new And());
							else
								filter.addLogicalOperator(new Or());
						}
					}
					catch (InvalidArgumentException | InvalidSearchQueryException e)
					{
						e.printStackTrace();
					}

					AccessionService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return false;
				}

				@Override
				protected void createColumns()
				{
					super.createColumns();

					/* Add the elevation column */
					TextColumn column = new TextColumn()
					{
						@Override
						public String getValue(Accession object)
						{
							return TableUtils.getCellValueAsString(object.getExtra(LocationService.DISTANCE));
						}

						@Override
						public Class getType()
						{
							return Double.class;
						}
					};
					column.setDataStoreName(LocationService.DISTANCE);
					addColumn(column, Text.LANG.collectingsiteDistance(), true);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
				{
					if (latitude != null && longitude != null)
						return AccessionService.Inst.get().getByDistance(Cookie.getRequestProperties(), latitude, longitude, pagination, new SearchCallback<>(accessionSection, callback));
					else
						return null;
				}
			});
		}
		else if (polygonTab.isActive())
		{
			if (draw != null)
			{
				List<LatLngPoint> bounds = new ArrayList<>();
				JsArray<LeafletLatLng> latLng = draw.getPolygon();

				for (int i = 0; i < latLng.length(); i++)
					bounds.add(new LatLngPoint(latLng.get(i).getLatitude(), latLng.get(i).getLongitude()));

				if (bounds.size() > 0)
				{
					LocationService.Inst.get().getInPolygon(Cookie.getRequestProperties(), Pagination.getDefault(), bounds, new DefaultAsyncCallback<PaginatedServerResult<List<Location>>>(true)
					{
						@Override
						protected void onSuccessImpl(PaginatedServerResult<List<Location>> result)
						{
							polygonMap.updateData(result.getServerResult());
						}
					});

					locationSection.add(new LocationTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
					{
						@Override
						public boolean supportsFullIdMarking()
						{
							return true;
						}

						@Override
						public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
						{
							LocationService.Inst.get().getIdsInPolygon(Cookie.getRequestProperties(), bounds, callback);
						}

						@Override
						protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Location>>> callback)
						{
							return LocationService.Inst.get().getInPolygon(Cookie.getRequestProperties(), pagination, bounds, new SearchCallback<>(locationSection, callback));
						}
					});

					accessionSection.add(new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
					{
						@Override
						public boolean supportsFullIdMarking()
						{
							return true;
						}

						@Override
						public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
						{
							AccessionService.Inst.get().getIdsInPolygon(Cookie.getRequestProperties(), bounds, callback);
						}

						@Override
						protected boolean supportsFiltering()
						{
							return false;
						}

						@Override
						protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
						{
							return AccessionService.Inst.get().getInPolygon(Cookie.getRequestProperties(), pagination, bounds, new SearchCallback<>(accessionSection, callback));
						}
					});
				}
			}
		}
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.GEOGRAPHIC_SEARCH)
				.addParam(Parameter.latitude)
				.addParam(Parameter.longitude);
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.LEAFLET, Library.LEAFLET_MINIMAP, Library.LEAFLET_DRAW, Library.LEAFLET_GEODESIC};
	}
}