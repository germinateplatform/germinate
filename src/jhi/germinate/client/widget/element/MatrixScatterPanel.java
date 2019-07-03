/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.client.widget.element;

import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class MatrixScatterPanel<T extends DatabaseObject> extends Composite
{
	private FlowPanel panel      = new FlowPanel();
	private FlowPanel chartPanel = new FlowPanel();

	private ExperimentType experimentType;
	private List<T>        objects;
	private List<Group>    groups;

	private MatrixChartSelection<T> parameterSelection;
	private ScatterChart<T>         scatterChart;
	private MatrixChart<T>          matrixChart;
	private List<Dataset>           selectedDatasets;

	public MatrixScatterPanel()
	{
		initWidget(panel);
	}

	private void initPlotButton()
	{
		if (parameterSelection != null)
			return;

		switch (experimentType)
		{
			case trials:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.trialsDatasets);
				break;
			case compound:
				selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.compoundDatasets);
				break;
		}

		Button plot = new Button(Text.LANG.trialsPlot(), e ->
		{
			List<Long> selectedDatasetIds = DatabaseObject.getIds(selectedDatasets);
			List<Long> groupIds = DatabaseObject.getIds(parameterSelection.getGroups());
			List<Long> objectIds = DatabaseObject.getIds(parameterSelection.getObjects());

			if (CollectionUtils.isEmpty(groupIds, objectIds))
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.phenotypeMatrixSelectGroupAndPhenotype());
				return;
			}

			if (objectIds.size() > MatrixChartSelection.MAX_NR_OF_OBJECTS)
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.phenotypeMatrixAtMost(MatrixChartSelection.MAX_NR_OF_OBJECTS));
				return;
			}

			chartPanel.clear();

			List<T> items = new ArrayList<>(parameterSelection.getObjects());
			if (objectIds.size() == 1)
			{
				objectIds.add(objectIds.get(0));
				items.add(items.get(0));
			}

			if (objectIds.size() == 2)
			{
				scatterChart = new ScatterChart<>();
				chartPanel.add(scatterChart);
				scatterChart.update(experimentType, selectedDatasetIds, items, groupIds, parameterSelection.getColor());
			}
			else
			{
				matrixChart = new MatrixChart<>();
				chartPanel.add(matrixChart);
				matrixChart.update(experimentType, selectedDatasetIds, objectIds, groupIds, parameterSelection.getColor());
			}
		});
		plot.addStyleName(Style.mdiLg(Style.MDI_ARROW_RIGHT_BOLD));
		plot.setType(ButtonType.PRIMARY);

		if (CollectionUtils.isEmpty(objects))
		{
			panel.clear();
			panel.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoDataFound()));
			return;
		}

		parameterSelection = new MatrixChartSelection<>(experimentType, selectedDatasets, objects, groups);

		panel.add(parameterSelection);
		panel.add(plot);
		panel.add(chartPanel);
	}

	public void update(ExperimentType experimentType, List<T> objects, List<Group> groups)
	{
		this.experimentType = experimentType;
		if (objects != null)
			this.objects = new ArrayList<>(objects);
		if (groups != null)
			this.groups = new ArrayList<>(groups);

		initPlotButton();
	}
}
