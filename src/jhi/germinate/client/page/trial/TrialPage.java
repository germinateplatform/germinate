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

package jhi.germinate.client.page.trial;

import com.google.gwt.core.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
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
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class TrialPage extends Composite implements HasHyperlinkButton, HasLibraries
{
	interface TrialPageUiBinder extends UiBinder<HTMLPanel, TrialPage>
	{
	}

	private static TrialPageUiBinder ourUiBinder = GWT.create(TrialPageUiBinder.class);

	@UiField
	HTMLPanel     panel;
	@UiField
	Row           content;
	@UiField
	CategoryPanel overviewTab;
	@UiField
	CategoryPanel scatterTab;
	@UiField
	CategoryPanel matrixTab;
	@UiField
	CategoryPanel downloadTab;

	@UiField
	FlowPanel                      overviewPanel;
	@UiField(provided = true)
	ScatterChart<Phenotype>        phenotypeByPhenotypeChart;
	@UiField(provided = true)
	MatrixChart<Phenotype>         matrixChart;
	@UiField(provided = true)
	DataExportSelection<Phenotype> exportSelection;

	@UiField
	DeckPanel deck;

	private List<Phenotype> phenotypes;
	private List<Group>     groups;
	private List<Long>      selectedDatasets;

	public TrialPage()
	{
		matrixChart = new MatrixChart<>();
		phenotypeByPhenotypeChart = new ScatterChart<>();
		exportSelection = new DataExportSelection<>(ExperimentType.trials);

		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		/* See if there are selected datasets in the parameter store */
		selectedDatasets = LongListParameterStore.Inst.get().get(Parameter.trialsDatasetIds);

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
			scatterTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
			matrixTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));
			downloadTab.setColor(GerminateSettingsHolder.getCategoricalColor(i++));

			GQuery.$(overviewTab.getAnchor()).click(new Function()
			{
				@Override
				public boolean f(Event e)
				{
					deck.showWidget(0);
					return false;
				}
			});
			GQuery.$(scatterTab.getAnchor()).click(new Function()
			{
				@Override
				public boolean f(Event e)
				{
					deck.showWidget(1);
					return false;
				}
			});
			GQuery.$(matrixTab.getAnchor()).click(new Function()
			{
				@Override
				public boolean f(Event e)
				{
					deck.showWidget(2);
					return false;
				}
			});
			GQuery.$(downloadTab.getAnchor()).click(new Function()
			{
				@Override
				public boolean f(Event e)
				{
					deck.showWidget(3);
					return false;
				}
			});

			getPhenotypes();
		}
	}

	private void getPhenotypes()
	{
		final ParallelAsyncCallback<ServerResult<List<Phenotype>>> phenotypeCallback = new ParallelAsyncCallback<ServerResult<List<Phenotype>>>()
		{
			@Override
			protected void start()
			{
				PhenotypeService.Inst.get().get(Cookie.getRequestProperties(), selectedDatasets, ExperimentType.trials, false, this);
			}
		};
		final ParallelAsyncCallback<ServerResult<List<Group>>> groupCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getAccessionGroups(Cookie.getRequestProperties(), selectedDatasets, ExperimentType.trials, this);
			}
		};

		new ParallelParentAsyncCallback(true, phenotypeCallback, groupCallback)
		{
			@Override
			public void handleSuccess()
			{
				ServerResult<List<Phenotype>> phenotypeData = getCallbackData(0);
				ServerResult<List<Group>> groupData = getCallbackData(1);

				phenotypes = phenotypeData.getServerResult();
				groups = groupData.getServerResult();

				getOverviewStats();

				phenotypeByPhenotypeChart.update(ExperimentType.trials, getNumericalPhenotypes(), groups, Text.LANG.trialsPByPText());
				matrixChart.update(ExperimentType.trials, getNumericalPhenotypes(), groups);
				exportSelection.update(selectedDatasets, phenotypes, groups);
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

	private void getOverviewStats()
	{
		overviewPanel.add(new TrialOverviewWidget(phenotypes));
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.TRIALS)
				.addParam(Parameter.trialsDatasetIds);
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LASSO, Library.D3_LEGEND, Library.D3_MULTI_LINE_CHART, Library.D3_SCATTER_MATRIX, Library.D3_SCATTER_PLOT, Library.D3_DOWNLOAD};
	}
}