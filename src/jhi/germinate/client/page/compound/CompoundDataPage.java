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

package jhi.germinate.client.page.compound;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.stream.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.basic.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#compoundDatasetIds
 */
public class CompoundDataPage extends Composite implements HasLibraries, HasHyperlinkButton
{
	private List<Compound> compounds;
	private List<Group>    groups;

	interface CompoundDataPageUiBinder extends UiBinder<HTMLPanel, CompoundDataPage>
	{
	}

	private static CompoundDataPageUiBinder ourUiBinder = GWT.create(CompoundDataPageUiBinder.class);

	@UiField
	DatasetListWidget datasetList;
	@UiField
	HTMLPanel         panel;
	@UiField
	Row               content;
	@UiField
	CategoryPanel     overviewTab;
	@UiField
	CategoryPanel     matrixTab;
	@UiField
	CategoryPanel     dataTab;
	@UiField
	CategoryPanel     downloadTab;

	@UiField
	FlowPanel                     overviewPanel;
	@UiField(provided = true)
	MatrixScatterPanel<Compound>  compoundMatrixChart;
	@UiField(provided = true)
	CompoundDataTable             compoundDataTable;
	@UiField(provided = true)
	DataExportSelection<Compound> exportSelection;

	@UiField(provided = true)
	DatasetMetadataDownload metadataDownload;

	@UiField
	DeckPanel deck;

	private boolean compoundDataTableShown = false;

	private List<Dataset> selectedDatasets;

	public CompoundDataPage()
	{
		/* See if there are selected datasets in the parameter store */
		selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.compoundDatasets);

		compoundMatrixChart = new MatrixScatterPanel<>();
		exportSelection = new DataExportSelection<>(ExperimentType.compound);

		metadataDownload = new DatasetMetadataDownload(selectedDatasets);

		compoundDataTable = new CompoundDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			{
				preventInitialDataLoad = true;
			}

			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			private PartialSearchQuery addToFilter(PartialSearchQuery filter)
			{
				if (filter == null)
					filter = new PartialSearchQuery();

				String search = selectedDatasets.stream()
												.map(Dataset::getName)
												.collect(Collectors.joining(", "));
				filter.add(new SearchCondition(Dataset.NAME, new InSet(), search, String.class));

				if (filter.getAll().size() > 1)
					filter.addLogicalOperator(new And());

				return filter;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<CompoundData>>> callback)
			{
				filter = addToFilter(filter);

				return CompoundService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};

		initWidget(ourUiBinder.createAndBindUi(this));

		datasetList.setType(ExperimentType.compound);
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		if (selectedDatasets == null || selectedDatasets.size() < 1)
		{
			/* If not, show an error message */
			content.clear();
			panel.add(new Heading(HeadingSize.H3, Text.LANG.notificationExportNoDataset()));
		}
		else
		{
			content.setVisible(true);

			int i = 0;
			overviewTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
			matrixTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
			dataTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
			downloadTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));

			JavaScript.click(overviewTab.getAnchor(), new ClickCallback()
			{
				@Override
				public void onSuccess(Event event)
				{
					deck.showWidget(0);
				}
			});
			JavaScript.click(matrixTab.getAnchor(), new ClickCallback()
			{
				@Override
				public void onSuccess(Event event)
				{
					deck.showWidget(1);
				}
			});
			JavaScript.click(dataTab.getAnchor(), new ClickCallback()
			{
				@Override
				public void onSuccess(Event event)
				{
					deck.showWidget(2);

					if (!compoundDataTableShown)
					{
						compoundDataTableShown = true;
						compoundDataTable.refreshTable();
					}
				}
			});
			JavaScript.click(downloadTab.getAnchor(), new ClickCallback()
			{
				@Override
				public void onSuccess(Event event)
				{
					deck.showWidget(3);
				}
			});

			getCompounds();
		}
	}

	private void getCompounds()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		final ParallelAsyncCallback<ServerResult<List<Compound>>> compoundCallback = new ParallelAsyncCallback<ServerResult<List<Compound>>>()
		{
			@Override
			protected void start()
			{
				CompoundService.Inst.get().getForDatasetIds(Cookie.getRequestProperties(), ids, this);
			}
		};
		final ParallelAsyncCallback<ServerResult<List<Group>>> groupCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getAccessionGroups(Cookie.getRequestProperties(), ids, ExperimentType.compound, this);
			}
		};

		new ParallelParentAsyncCallback(true, compoundCallback, groupCallback)
		{
			@Override
			public void handleSuccess()
			{
				ServerResult<List<Compound>> phenotypeData = getCallbackData(0);
				ServerResult<List<Group>> groupData = getCallbackData(1);

				compounds = phenotypeData.getServerResult();
				groups = groupData.getServerResult();

				getOverviewStats();

				compoundMatrixChart.update(ExperimentType.compound, compounds, groups);
				exportSelection.update(ids, compounds, groups);
			}

			@Override
			public void handleFailure(Exception reason)
			{
				compoundCallback.onFailure(reason);
			}
		};
	}

	private void getOverviewStats()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		CompoundService.Inst.get().getDataStatsForDatasets(Cookie.getRequestProperties(), ids, new DefaultAsyncCallback<ServerResult<List<DataStats>>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				super.onFailureImpl(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<List<DataStats>> result)
			{
				if (result.hasData())
				{
					overviewPanel.add(new DataStatsTable(result.getServerResult()));
				}
			}
		});
	}

	@Override
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.COMPOUND_DATASETS)
				.addParam(Parameter.compoundDatasetIds);
	}
}