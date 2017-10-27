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
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
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
import jhi.germinate.client.widget.table.basic.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#compoundDatasetIds
 */
public class CompoundDataPage extends Composite implements HasLibraries
{
	private List<Compound> compounds;
	private List<Group>    groups;

	interface CompoundDataPageUiBinder extends UiBinder<HTMLPanel, CompoundDataPage>
	{
	}

	private static CompoundDataPageUiBinder ourUiBinder = GWT.create(CompoundDataPageUiBinder.class);

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
	FlowPanel                     overviewPanel;
	@UiField(provided = true)
	ScatterChart<Compound>        compoundByCompoundChart;
	@UiField(provided = true)
	MatrixChart<Compound>         compoundMatrixChart;
	@UiField(provided = true)
	DataExportSelection<Compound> exportSelection;

	@UiField
	DeckPanel deck;

	private List<Long> selectedDatasets;

	public CompoundDataPage()
	{
		compoundMatrixChart = new MatrixChart<>();
		compoundByCompoundChart = new ScatterChart<>();
		exportSelection = new DataExportSelection<>(ExperimentType.compound);

		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		/* See if there are selected datasets in the parameter store */
		selectedDatasets = LongListParameterStore.Inst.get().get(Parameter.compoundDatasetIds);

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

			getCompounds();
		}
	}

	private void getCompounds()
	{
		final ParallelAsyncCallback<ServerResult<List<Compound>>> compoundCallback = new ParallelAsyncCallback<ServerResult<List<Compound>>>()
		{
			@Override
			protected void start()
			{
				CompoundService.Inst.get().getForDatasetIds(Cookie.getRequestProperties(), selectedDatasets, this);
			}
		};
		final ParallelAsyncCallback<ServerResult<List<Group>>> groupCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getAccessionGroups(Cookie.getRequestProperties(), selectedDatasets, ExperimentType.compound, this);
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

				compoundByCompoundChart.update(ExperimentType.compound, compounds, groups, null);
				compoundMatrixChart.update(ExperimentType.compound, compounds, groups);
				exportSelection.update(selectedDatasets, compounds, groups);
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
		CompoundService.Inst.get().getDataStatsForDatasets(Cookie.getRequestProperties(), selectedDatasets, new DefaultAsyncCallback<ServerResult<List<DataStats>>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				super.onFailureImpl(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<List<DataStats>> result)
			{
				if (result.getServerResult() != null && result.getServerResult().size() > 0)
				{
					overviewPanel.add(new CompoundDataOverviewTable(result.getServerResult()));
				}
			}
		});
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LASSO, Library.D3_LEGEND, Library.D3_SCATTER_MATRIX, Library.D3_SCATTER_PLOT, Library.D3_DOWNLOAD};
	}
}