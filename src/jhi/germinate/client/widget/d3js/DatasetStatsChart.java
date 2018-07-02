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
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetStatsChart extends AbstractChart
{
	private FlowPanel chartPanel;

	private String xAxisTitle = Text.LANG.datasetsColumnExperimentType();
	private String yAxisTitle = Text.LANG.datasetsColumnDatasetDataPoints();

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		panel.add(new Heading(HeadingSize.H3, Text.LANG.dataStatisticsDatasetsTitle()));
		panel.add(new Label(Text.LANG.dataStatisticsDatasetsText()));
		panel.add(chartPanel);

		DatasetService.Inst.get().getDatasetStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				panel.clear();
				DatasetStatsChart.this.removeFromParent();
			}

			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				if (!StringUtils.isEmpty(result.getServerResult()))
				{
					filePath = new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

					DatasetStatsChart.this.onResize(true);
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
				}
			}
		});
	}

	@Override
	protected void updateChart(int width)
	{
		create(chartPanel.getOffsetWidth());
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "dataset-stats";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LEGEND, Library.D3_GROUPED_BAR_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint)/*-{
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;
		var legendItemStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_LEGEND_ITEM;

		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.DatasetStatsChart::yAxisTitle;

		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var color = $wnd.d3.scale.ordinal().range(@jhi.germinate.client.util.JavaScript.D3::getColorPalette()());

		$wnd.d3.tsv(filePath, function (error, data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.groupedBarChart()
					.margin(margin)
					.width(width)
					.height(height)
					.color(color)
					.rowIdentifier("ExperimentType")
					.tooltip(function (d) {
						return d.value + " (" + d.name + ")";
					})
					.tooltipStyle(tooltipStyle)
					.axisStyle(axisStyle)
					.legendItemStyle(legendItemStyle)
					.showLegend(true)
					.legendWidth(100)
					.yLabel(yAxisTitle))
		});
	}-*/;
}
