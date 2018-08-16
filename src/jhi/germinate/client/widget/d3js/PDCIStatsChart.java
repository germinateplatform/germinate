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

package jhi.germinate.client.widget.d3js;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

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
public class PDCIStatsChart extends AbstractChart
{
	private String xAxisTitle = Text.LANG.passportColumnPDCI();
	private String yAxisTitle = Text.LANG.generalCount();

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(new Heading(HeadingSize.H3, Text.LANG.passportPDCITitle()));
		panel.add(new HTML(Text.LANG.passportPDCIExplanation()));
		panel.add(chartPanel);

		AccessionService.Inst.get().getPDCIStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				panel.clear();
				PDCIStatsChart.this.removeFromParent();
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

					PDCIStatsChart.this.onResize(true);
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
		create(width);
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
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_GROUPED_BAR_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint)/*-{
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;

		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.PDCIStatsChart::xAxisTitle;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.PDCIStatsChart::yAxisTitle;

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
					.rowIdentifier("bin")
					.tooltip(function (d) {
						return d.value;
					})
					.tooltipStyle(tooltipStyle)
					.axisStyle(axisStyle)
					.showLegend(false)
					.yLabel(yAxisTitle)
					.xLabel(xAxisTitle))
		});
	}-*/;
}
