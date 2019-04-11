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

package jhi.germinate.client.page.trial;

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
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.client.widget.table.basic.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class TrialPage extends Composite implements HasHyperlinkButton, HasLibraries, ParallaxBannerPage
{
	interface TrialPageUiBinder extends UiBinder<HTMLPanel, TrialPage>
	{
	}

	private static TrialPageUiBinder ourUiBinder = GWT.create(TrialPageUiBinder.class);

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
	FlowPanel                      overviewPanel;
	@UiField(provided = true)
	MatrixScatterPanel<Phenotype>  matrixChart;
	@UiField(provided = true)
	PhenotypeDataTable             phenotypeDataTable;
	@UiField(provided = true)
	DataExportSelection<Phenotype> exportSelection;

	@UiField(provided = true)
	DatasetMetadataDownload metadataDownload;

	@UiField
	DeckPanel deck;

	private List<Phenotype> phenotypes;
	private List<Group>     groups;
	private List<Dataset>   selectedDatasets;

	private boolean phenotypeDataTableShown = false;

	public TrialPage()
	{
		/* See if there are selected datasets in the parameter store */
		selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.trialsDatasets);

		exportSelection = new DataExportSelection<>(ExperimentType.trials);
		metadataDownload = new DatasetMetadataDownload(selectedDatasets);

		matrixChart = new MatrixScatterPanel<>();

		phenotypeDataTable = new PhenotypeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			{
				preventInitialDataLoad = true;
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
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<PhenotypeData>>> callback)
			{
				filter = addToFilter(filter);

				return PhenotypeService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};

		initWidget(ourUiBinder.createAndBindUi(this));

		datasetList.setType(ExperimentType.trials);
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

					if (!phenotypeDataTableShown)
					{
						phenotypeDataTableShown = true;
						phenotypeDataTable.refreshTable();
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

			getPhenotypes();
		}
	}

	private void getPhenotypes()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		final ParallelAsyncCallback<ServerResult<List<Phenotype>>> phenotypeCallback = new ParallelAsyncCallback<ServerResult<List<Phenotype>>>()
		{
			@Override
			protected void start()
			{
				PhenotypeService.Inst.get().get(Cookie.getRequestProperties(), ids, ExperimentType.trials, false, this);
			}
		};
		final ParallelAsyncCallback<ServerResult<List<Group>>> groupCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getAccessionGroups(Cookie.getRequestProperties(), ids, ExperimentType.trials, this);
			}
		};

		new ParallelParentAsyncCallback(true, phenotypeCallback, groupCallback)
		{
			@Override
			public void handleSuccess()
			{
				ServerResult<List<Phenotype>> phenotypeData = getCallbackData(0);
				ServerResult<List<Group>> groupData = getCallbackData(1);
				final List<Long> ids = DatabaseObject.getIds(selectedDatasets);

				phenotypes = phenotypeData.getServerResult();
				groups = groupData.getServerResult();

				getYearOverviewStats();

				matrixChart.update(ExperimentType.trials, getNumericalPhenotypes(), groups);
				exportSelection.update(ids, phenotypes, groups);
			}

			@Override
			public void handleFailure(Exception reason)
			{
				phenotypeCallback.onFailure(reason);
			}
		};
	}

	private List<Phenotype> getNumericalPhenotypes()
	{
		return phenotypes.stream()
						 .filter(Phenotype::isNumeric)
						 .collect(Collectors.toList());
	}

	private void getYearOverviewStats()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		TrialService.Inst.get().getTrialYears(Cookie.getRequestProperties(), ids, new DefaultAsyncCallback<ServerResult<List<Integer>>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				getBasicOverviewStats();
			}

			@Override
			protected void onSuccessImpl(ServerResult<List<Integer>> result)
			{
				// Otherwise show the development over time
				if (result.hasData())
					overviewPanel.add(new TrialOverviewWidget(phenotypes, result.getServerResult()));
				else
					// If there are no years or if there's just one, then just plot the basics
					getBasicOverviewStats();
			}
		});
	}

	private void getBasicOverviewStats()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		PhenotypeService.Inst.get().getOverviewStats(Cookie.getRequestProperties(), ids, new DefaultAsyncCallback<ServerResult<List<DataStats>>>()
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
					overviewPanel.add(new DataStatsTable(result.getServerResult()));
			}
		});
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.TRIALS_DATASETS)
				.addParam(Parameter.trialsDatasetIds);
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LASSO, Library.D3_LEGEND, Library.D3_MULTI_LINE_CHART, Library.D3_SCATTER_MATRIX, Library.D3_SCATTER_PLOT, Library.D3_DOWNLOAD};
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxTrial();
	}
}