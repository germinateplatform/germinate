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

package jhi.germinate.client.page.climate;

import com.google.gwt.core.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

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
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateDataPage extends Composite implements ParallaxBannerPage, HasHyperlinkButton, HasLibraries
{
	interface ClimateDataPageUiBinder extends UiBinder<HTMLPanel, ClimateDataPage>
	{
	}

	private static ClimateDataPageUiBinder ourUiBinder = GWT.create(ClimateDataPageUiBinder.class);

	@UiField
	DatasetListWidget datasetList;

	@UiField
	FlowPanel content;

	@UiField
	ClimateListBox climateBox;
	@UiField
	GroupListBox   groupBox;

	@UiField
	FlowPanel    resultPanel;
	@UiField
	SimplePanel  chartPanel;
	@UiField
	ClimateChart climateChart;
	@UiField
	FlowPanel    mapWrapper;
	@UiField
	SimplePanel  mapPanel;
	@UiField
	SimplePanel  tablePanel;

	private LeafletUtils.ImageOverlayWrapper climateOverlays = new LeafletUtils.ImageOverlayWrapper();

	private LeafletUtils.ClusteredMarkerCreator map;
	private ClimateYearDataTable                climateDataTable;
	private List<Dataset>                       selectedDatasets;

	public ClimateDataPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		datasetList.setType(ExperimentType.climate);

		selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.climateDatasets);

		groupBox.setMultipleSelect(false);

		climateDataTable = new ClimateYearDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected boolean supportsDownload()
			{
				return true;
			}

			@Override
			protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
			{
				Climate climate = climateBox.getSelection();
				Group group = groupBox.getSelection();
				final List<Long> ids = DatabaseObject.getIds(selectedDatasets);

				if (climate == null || climate.getId() == -1)
					callback.onSuccess(new ServerResult<>(null, null));
				else
					ClimateService.Inst.get().export(Cookie.getRequestProperties(), ids, climate.getId(), group.getId(), callback);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<ClimateYearData>>> callback)
			{
				Climate climate = climateBox.getSelection();
				Group group = groupBox.getSelection();
				final ClimateYearDataTable that = this;
				final List<Long> ids = DatabaseObject.getIds(selectedDatasets);

				if (climate == null || climate.getId() == -1)
				{
					callback.onSuccess(new PaginatedServerResult<>(null, new ArrayList<>(), 0));
					return null;
				}
				else
				{
					return ClimateService.Inst.get().getGroupData(Cookie.getRequestProperties(), ids, climate.getId(), group.getId(), pagination, new DefaultAsyncCallback<PaginatedServerResult<List<ClimateYearData>>>()
					{
						@Override
						protected void onFailureImpl(Throwable caught)
						{
							tablePanel.clear();
							callback.onFailure(caught);
						}

						@Override
						protected void onSuccessImpl(PaginatedServerResult<List<ClimateYearData>> result)
						{
							that.updateGradient(result.getServerResult());
							callback.onSuccess(result);
						}
					});
				}
			}
		};
		tablePanel.add(climateDataTable);

		getClimatesAndGroups();
	}

	private void getClimatesAndGroups()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		/*
		 * Now we set up two callbacks, since we only want to do stuff when both
		 * returned successfully
		 */
		ParallelAsyncCallback<ServerResult<List<Group>>> callbackGroups = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getForType(Cookie.getRequestProperties(), GerminateDatabaseTable.locations, this);
			}
		};

		ParallelAsyncCallback<ServerResult<List<Climate>>> callbackClimate = new ParallelAsyncCallback<ServerResult<List<Climate>>>()
		{
			@Override
			protected void start()
			{
				ClimateService.Inst.get().get(Cookie.getRequestProperties(), ids, true, this);
			}
		};

		new ParallelParentAsyncCallback(callbackGroups, callbackClimate)
		{
			@Override
			public void handleSuccess()
			{
				ServerResult<List<Group>> groupData = this.getCallbackData(0);
				ServerResult<List<Climate>> climateData = this.getCallbackData(1);

				if (groupData.getServerResult().size() > 0)
				{
					groupBox.setAcceptableValues(groupData.getServerResult());
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupsNoGroupsFound(Text.LANG.collectingsiteCollsite()));
				}

				if (climateData.getServerResult().size() > 0)
				{
					Climate dummy = new Climate(-1L)
							.setName(Text.LANG.generalNone());

					climateData.getServerResult().add(0, dummy);

					climateBox.setValue(dummy, false);
					climateBox.setAcceptableValues(climateData.getServerResult());
				}
				else
				{
					content.clear();
					content.add(new Heading(HeadingSize.H3, Text.LANG.notificationNoDataFound()));
				}
			}

			@Override
			public void handleFailure(Exception reason)
			{
				callbackGroups.onFailure(reason);
			}
		};
	}

	@UiHandler("groupBox")
	void onGroupChanged(ValueChangeEvent<List<Group>> event)
	{
		onClimateChanged(null);
	}

	@UiHandler("climateBox")
	void onClimateChanged(ValueChangeEvent<List<Climate>> event)
	{
		Climate climate = climateBox.getSelection();
		Group group = groupBox.getSelection();

		if (climate == null || climate.getId() == -1)
		{
			resultPanel.setVisible(false);
//			climateChart.clear();
//			map.updateData(null);
			return;
		}

		resultPanel.setVisible(true);

		climateChart.update(climate, group);

		updateMap(climate, group);

		climateDataTable.refreshTable();
	}

	private void updateMap(Climate climate, Group group)
	{
		if (map != null)
			climateOverlays.clear(map.getMap());

		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);

		ParallelAsyncCallback<ServerResult<List<Location>>> locationData = new ParallelAsyncCallback<ServerResult<List<Location>>>()
		{
			@Override
			protected void start()
			{
				LocationService.Inst.get().getForClimateAndGroup(Cookie.getRequestProperties(), ids, climate.getId(), group.getId(), this);
			}
		};
		ParallelAsyncCallback<ServerResult<List<ClimateOverlay>>> overlayData = new ParallelAsyncCallback<ServerResult<List<ClimateOverlay>>>()
		{
			@Override
			protected void start()
			{
				ClimateService.Inst.get().getClimateOverlays(Cookie.getRequestProperties(), climate.getId(), this);
			}
		};

		new ParallelParentAsyncCallback(locationData, overlayData)
		{
			@Override
			public void handleSuccess()
			{
				/* Retrieve the result of both parallel callbacks */
				final ServerResult<List<Location>> locations = getCallbackData(0);
				final ServerResult<List<ClimateOverlay>> overlays = getCallbackData(1);

				/* If there is data to display */
				if (!CollectionUtils.isEmpty(locations.getServerResult()))
				{
					mapWrapper.setVisible(true);
					if (map == null)
						map = new LeafletUtils.ClusteredMarkerCreator(mapPanel, locations.getServerResult(), null);
					else
						map.updateData(locations.getServerResult());
				}
				else
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationNoInformationFound());
				}

				if (!CollectionUtils.isEmpty(overlays.getServerResult()))
				{
					Scheduler.get().scheduleDeferred(() -> climateOverlays = LeafletUtils.addClimateOverlays(map.getMap(), overlays.getServerResult()));
				}
			}

			@Override
			public void handleFailure(Exception caught)
			{
				mapWrapper.setVisible(false);
			}
		};
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxClimate();
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.CLIMATE_DATASETS)
				.addParam(Parameter.climateDatasetIds);
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.LEAFLET_COMPLETE};
	}
}