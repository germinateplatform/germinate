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

package jhi.germinate.client.widget.d3js;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link PlotlyClimateChart} visualizes the climate data in a line graph.
 *
 * @author Sebastian Raubach
 */
public class PlotlyClimateChart extends AbstractChart implements PlotlyChart
{
	private boolean needsRedraw = true;

	private FlowPanel chartPanel = null;
	private String    yAxisTitle;

	private Climate climate;
	private Group   group;

	private List<Dataset> selectedDatasets;

	public PlotlyClimateChart(Climate climate, Group group)
	{
		super();
		update(climate, group);
	}

	public void update(Climate climate, Group group)
	{
		this.climate = climate;
		this.group = group;
		this.needsRedraw = true;

		if (chartPanel != null)
			getData();
	}

	@Override
	public int[] getDownloadSize()
	{
		return new int[]{1280, 640};
	}

	@Override
	public void onResize(boolean containerResize, boolean force)
	{
		if (needsRedraw || force)
		{
			needsRedraw = false;
			super.onResize(containerResize, force);
		}
	}

	private void getData()
	{
		final List<Long> ids = DatabaseObject.getIds(selectedDatasets);
		/* Set up the callback object for the min max avg data */
		ClimateService.Inst.get().getMinAvgMaxFile(Cookie.getRequestProperties(), ids, climate.getId(), (group == null || group.getId() == -1) ? null : group.getId(), new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			public void onSuccessImpl(ServerResult<String> result)
			{
				if (result.hasData())
				{
					/* Build the path to the chart file */
					filePath = new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
															 .setPath(ServletConstants.SERVLET_FILES)
															 .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
															 .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
															 .setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult())
															 .build();

					/* Set up the chart */
					if (climate.getUnit() != null)
						yAxisTitle = climate.getName() + " [" + climate.getUnit().getName() + "]";
					else
						yAxisTitle = climate.getName();
					PlotlyClimateChart.this.onResize(true, false);
				}
				else
				{
					/* Clean up and notify the user */
					if (group == null || StringUtils.isEmpty(group.getName()))
					{
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationClimateNoInformationClimate());
					}
					else
					{
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationClimateNoInformationClimateGroup());
					}
				}
			}
		});
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		selectedDatasets = DatasetListParameterStore.Inst.get().get(Parameter.climateDatasets);

		this.chartPanel = chartPanel;
		panel.add(chartPanel);

		if (climate != null)
			getData();
		else
			onResize(true, false);
	}

	@Override
	protected void updateChart(int width)
	{
		create(width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "climate-chart";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_LINE_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint) /*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var yaxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyClimateChart::yAxisTitle;

		var monthNamesI18n = @jhi.germinate.client.util.DateUtils::MONTHS_ABBR;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		function unpack(rows, key) {
			return rows.map(function (row) {
				return row[key];
			});
		}

		$wnd.Plotly.d3.tsv(filePath, function (error, rows) {
			$wnd.Plotly.d3.select("#" + panelId)
				.datum(rows)
				.call($wnd.plotlyLineChart()
					.height(height)
					.colors(colors)
					.x('recording_date')
					.yaxisTitle(yaxisTitle)
					.yaxisRangeMode('tozero')
					.getText(function (rows, dim) {
						if (dim === 'MIN') {
							return unpack(rows, 'MinCollsite');
						} else if (dim === 'MAX') {
							return unpack(rows, 'MaxCollsite');
						} else {
							return ['', '', '', '', '', '', '', '', '', '', '', ''];
						}
					})
					.columnsToIgnore(['MinCollsite', 'MaxCollsite', 'recording_date', 'unit_abbreviation'])
					.xaxisTickVals([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12])
					.xaxisTickText(monthNamesI18n)
				);
		});
	}-*/;
}
