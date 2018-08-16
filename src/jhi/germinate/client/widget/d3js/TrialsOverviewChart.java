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

import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;

/**
 * @author Sebastian Raubach
 */
public class TrialsOverviewChart extends AbstractChart
{
	private String xAxisTitleLine = Text.LANG.generalYear();
	private String yAxisTitleLine;

	public TrialsOverviewChart(String filePath, String yAxisTitleLine)
	{
		this.filePath = filePath;
		this.yAxisTitleLine = yAxisTitleLine;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		onResize(true);
	}

	@Override
	protected void updateChart(int width)
	{
		create(width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "trials-overview";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_MULTI_LINE_CHART, Library.D3_DOWNLOAD};
	}


	private native void create(int widthHint)/*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;
		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;
		var legendStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_LEGEND_ITEM;
		var lineStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ClimateLineChartBundle::STYLE_LINE;

		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.TrialsOverviewChart::xAxisTitleLine;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.TrialsOverviewChart::yAxisTitleLine;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var color = $wnd.d3.scale.ordinal().range(@jhi.germinate.client.util.JavaScript.D3::getColorPalette()());

		var formatDate = $wnd.d3.time.format("%Y");

		$wnd.d3.tsv(filePath, function (data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.multiLineChart()
					.margin(margin)
					.width(width)
					.height(height)
					.xScale($wnd.d3.time.scale.utc())
					.xTickFormat(formatDate)
					.xTicks($wnd.d3.time.years)
					.xTicksValue(1)
					.minimum(0).x(function (d) {
						return formatDate.parse(d.date);
					})
					.y(function (d) {
						return parseFloat(d);
					})
					.tooltip(function (d) {
						return d.key + "<br/>" + d.data[d.key].toFixed(2);
					})
					.color(color)
					.tooltipStyle(tooltipStyle)
					.legendItemStyle(legendStyle)
					.axisStyle(axisStyle)
					.lineStyle(lineStyle)
					.xLabel(xAxisTitle)
					.yLabel(yAxisTitle)
					.showLegend(true)
					.legendWidth(150)
					.interpolate("cardinal"));
		});
	}-*/;
}
