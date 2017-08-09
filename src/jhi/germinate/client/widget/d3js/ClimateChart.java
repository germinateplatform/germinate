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

package jhi.germinate.client.widget.d3js;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * {@link ClimateChart} visualizes the climate data in a line graph.
 *
 * @author Sebastian Raubach
 */
public class ClimateChart extends AbstractChart
{
	private FlowPanel chartPanel;
	private String    yAxisTitle;

	private Climate climate;
	private Group   group;

	public void update(Climate climate, Group group)
	{
		this.climate = climate;
		this.group = group;

		if (chartPanel != null)
			getData();
	}

	private void getData()
	{
		/* Set up the callback object for the min max avg data */
		ClimateService.Inst.get().getMinAvgMaxFile(Cookie.getRequestProperties(), climate.getId(), (group == null || group.getId() == -1) ? null : group.getId(), new DefaultAsyncCallback<ServerResult<Tuple.Pair<String, String>>>(true)
		{
			@Override
			public void onSuccessImpl(ServerResult<Tuple.Pair<String, String>> result)
			{
				if (!StringUtils.isEmpty(result.getServerResult().getSecond()))
				{
					/* Build the path to the chart file */
					filePath = new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
															 .setPath(ServletConstants.SERVLET_FILES)
															 .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
															 .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
															 .setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult().getSecond())
															 .build();

					/* Set up the chart */
					yAxisTitle = climate.getName() + " [" + result.getServerResult().getFirst() + "]";
					onResize(true);
				}
				else
				{
					/* Clean up and notify the user */
					if (group == null || StringUtils.isEmpty(group.getDescription()))
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
		this.chartPanel = chartPanel;
		panel.add(chartPanel);

		if (climate != null)
			getData();
		else
			onResize(true);
	}

	@Override
	protected void updateChart(int width)
	{
		create(chartPanel.getOffsetWidth());
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
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LEGEND, Library.D3_MULTI_LINE_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint)/*-{
		var barChartFile = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var barChartYAxisTitle = this.@jhi.germinate.client.widget.d3js.ClimateChart::yAxisTitle;

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;
		var legendItemStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_LEGEND_ITEM;
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;

		var lineStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ClimateLineChartBundle::STYLE_LINE;

		var monthNamesI18n = @jhi.germinate.client.util.DateUtils::MONTHS_ABBR;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var color = $wnd.d3.scale.ordinal().range(@jhi.germinate.client.util.JavaScript.D3::getColorPalette()());

		$wnd.d3.tsv(barChartFile,
			function (data) {
				$wnd.d3.select("#" + panelId)
					.datum(data)
					.call($wnd.multiLineChart()
						.margin(margin)
						.width(width)
						.height(height)
						.x(function (d) {
							return parseInt(d.recording_date);
						})
						.y(function (d) {
							return parseFloat(d);
						})
						.tooltip(function (d) {
							if (d.key === "MAX")
								return d.key + ": " + d.data[d.key] + "<br/>" + d.data.MaxCollsite;
							else if (d.key === "MIN")
								return d.key + ": " + d.data[d.key] + "<br/>" + d.data.MinCollsite;
							else
								return d.key + ": " + d.data[d.key];
						})
						.color(color)
						.tooltipStyle(tooltipStyle)
						.axisStyle(axisStyle)
						.lineStyle(lineStyle)
						.xLabel("")
						.yLabel(barChartYAxisTitle)
						.xAxisStart(20)
						.legendItemStyle(legendItemStyle)
						.showLegend(true)
						.legendWidth(60)
						.ignoreIndices([0, 1, 2, 6])
						.interpolate("cardinal").xTickFormat(function (d, i) {
							return monthNamesI18n[i];
						}));
			});
	}-*/;
}
