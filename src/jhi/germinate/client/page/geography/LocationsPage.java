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

package jhi.germinate.client.page.geography;

import com.google.gwt.core.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;
import jhi.gwt.leaflet.client.map.*;

/**
 * @author Sebastian Raubach
 */
public class LocationsPage extends Composite implements HasLibraries, ParallaxBannerPage
{
	interface LocationsPageUiBinder extends UiBinder<HTMLPanel, LocationsPage>
	{
	}

	private static LocationsPageUiBinder ourUiBinder = GWT.create(LocationsPageUiBinder.class);

	private static final Gradient GRADIENT = new Gradient(Gradient.getTemplateGradient(), 0, 100);

	@UiField
	HTML                clusteredText;
	@UiField
	HTML                heatmapText;
	@UiField
	LocationTypeListBox locationTypeBox;
	@UiField
	FormGroup           climateSection;
	@UiField
	ClimateListBox      climateBox;
	@UiField
	ToggleSwitch        synchonizeToggle;
	@UiField
	SimplePanel         clusteredPanel;
	@UiField
	SimplePanel         heatmapPanel;
	@UiField
	SimplePanel         gradientPanel;

	@UiField
	HTML                html;
	@UiField
	LocationTypeListBox locationTypeBoxTwo;
	@UiField
	SimplePanel         chartPanel;

	private LeafletUtils.ClusteredMarkerCreator clusteredMap;
	private LeafletUtils.HeatmapCreator         heatmapMap;
	private LocationTreemapChart                chart;

	private LeafletUtils.ImageOverlayWrapper clusteredClimateOverlays = new LeafletUtils.ImageOverlayWrapper();
	private LeafletUtils.ImageOverlayWrapper heatmapClimateOverlays   = new LeafletUtils.ImageOverlayWrapper();

	public LocationsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		clusteredText.setHTML(Text.LANG.geographyCollsiteTextClustered());
		heatmapText.setHTML(Text.LANG.geographyCollsiteTextHeatmap());

		synchonizeToggle.setOnText(Text.LANG.generalYes());
		synchonizeToggle.setOffText(Text.LANG.generalNo());

		chart = new LocationTreemapChart();
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		locationTypeBox.reselectValue();

		getClimates();

		/* Wait for the page to finish loading and then add the legend */
		Scheduler.get().scheduleDeferred(() ->
		{
			Widget gradient = GradientUtils.createHorizontalGradientLegend(GRADIENT, GradientUtils.HorizontalLegendPosition.BOTTOM);
			gradientPanel.add(gradient);
		});

		html.setHTML(Text.LANG.collsiteTreemapText());

		chartPanel.add(chart);

		List<LocationType> list = new ArrayList<>(Arrays.asList(LocationType.values()));
		list.remove(LocationType.all);

		locationTypeBoxTwo.setValue(LocationType.collectingsites, true);
		locationTypeBoxTwo.setAcceptableValues(list);
	}

	private void getClimates()
	{
		ClimateService.Inst.get().getWithGroundOverlays(Cookie.getRequestProperties(), new AsyncCallback<ServerResult<List<Climate>>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				/* If anything goes wrong, just hide the climate box */
				climateSection.setVisible(false);
			}

			@Override
			public void onSuccess(ServerResult<List<Climate>> result)
			{
				/* Fill the climate box if there are climates or hide it if
				 * there aren't */
				if (!CollectionUtils.isEmpty(result.getServerResult()))
				{
					Climate dummy = new Climate(-1L)
							.setName(Text.LANG.generalNone());

					/* Fill the climate combo box */
					result.getServerResult().add(0, dummy);
					climateBox.setValue(dummy, false);
					climateBox.setAcceptableValues(result.getServerResult());
					climateSection.setVisible(true);
				}
				else
				{
					climateSection.setVisible(false);
				}
			}
		});
	}

	private void updateMaps(List<Location> result)
	{
		climateBox.selectItem(0, true);

		if (result == null || result.size() < 1)
		{
			Notification.notify(Notification.Type.INFO, Text.LANG.notificationNoDataFound());

			clusteredMap.updateData(null);
			heatmapMap.updateData(null);
		}
		//		else if
		else
		{
			if (clusteredMap == null)
				clusteredMap = new LeafletUtils.ClusteredMarkerCreator(clusteredPanel, result, (id, name) -> {
					if (!StringUtils.isEmpty(id))
					{
						try
						{
							LongParameterStore.Inst.get().putAsString(Parameter.collectingsiteId, id);
							History.newItem(Page.ACCESSIONS_FOR_COLLSITE.name());
						}
						catch (UnsupportedDataTypeException e)
						{
						}
					}
				}, null);
			else
				clusteredMap.updateData(result);

			if (heatmapMap == null)
				heatmapMap = new LeafletUtils.HeatmapCreator(heatmapPanel, result, null);
			else
				heatmapMap.updateData(result);
		}
	}

	@UiHandler("climateBox")
	void onClimateChange(ValueChangeEvent<List<Climate>> event)
	{
		Climate climate = climateBox.getSelection();

		clusteredClimateOverlays.clear(clusteredMap.getMap());
		heatmapClimateOverlays.clear(heatmapMap.getMap());

		if (climate != null && climate.getId() != -1)
		{
			ClimateService.Inst.get().getClimateOverlays(Cookie.getRequestProperties(), climate.getId(), new DefaultAsyncCallback<ServerResult<List<ClimateOverlay>>>()
			{
				@Override
				public void onSuccessImpl(ServerResult<List<ClimateOverlay>> result)
				{
					if (result.getServerResult().size() > 0)
					{
						clusteredClimateOverlays = LeafletUtils.addClimateOverlays(clusteredMap.getMap(), result.getServerResult());
						heatmapClimateOverlays = LeafletUtils.addClimateOverlays(heatmapMap.getMap(), result.getServerResult());
					}
					else
					{
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationNoInformationFound());
					}
				}
			});
		}
	}

	@UiHandler("synchonizeToggle")
	void onSynchronizeStateChange(ValueChangeEvent<Boolean> event)
	{
		if (event.getValue())
			LeafletMap.sync(clusteredMap.getMap(), heatmapMap.getMap());
		else
			LeafletMap.unsync(clusteredMap.getMap(), heatmapMap.getMap());
	}

	@UiHandler("locationTypeBox")
	void onLocationTypeChange(ValueChangeEvent<List<LocationType>> event)
	{
		final LocationType type = locationTypeBox.getSelection();

		/* Set our search query */
		PartialSearchQuery filter = null;
		if (type != LocationType.all)
		{
			filter = new PartialSearchQuery();
			SearchCondition condition = new SearchCondition(LocationType.NAME, new Equal(), type.name(), String.class);
			filter.add(condition);
		}

		LocationService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, Pagination.getDefault(), new DefaultAsyncCallback<PaginatedServerResult<List<Location>>>()
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				heatmapPanel.clear();
				clusteredPanel.clear();

				super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(PaginatedServerResult<List<Location>> result)
			{
				updateMaps(result.getServerResult());
			}
		});
	}

	@UiHandler("locationTypeBoxTwo")
	void onLocationTypeChanged(ValueChangeEvent<List<LocationType>> event)
	{
		LocationType type = locationTypeBoxTwo.getSelection();

		LocationService.Inst.get().getJsonForType(Cookie.getRequestProperties(), type, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				chart.clear();
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
			}

			@Override
			public void onSuccessImpl(ServerResult<String> result)
			{
				if (!StringUtils.isEmpty(result.getServerResult()))
				{
					/* Construct the path to the json file */
					chart.setLocationType(type);
					chart.setFilePath(new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult())
							.build());
				}
				else
				{
					onFailureImpl(null);
				}
			}
		});
	}

	@Override
	public Library[] getLibraries()
	{
		Library[] l = chart.getLibraries();
		return ArrayUtils.add(l, Library.LEAFLET_COMPLETE, new Library[l.length + 1]);
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxGeography();
	}
}