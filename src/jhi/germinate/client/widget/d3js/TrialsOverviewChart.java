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
public class TrialsOverviewChart extends AbstractChart implements PlotlyChart
{
	private String xAxisTitleLine = Text.LANG.generalYear();
	private String yAxisTitleLine;

	private boolean needsRedraw = true;

	public TrialsOverviewChart(String filePath, String yAxisTitleLine)
	{
		this.filePath = filePath;
		this.yAxisTitleLine = yAxisTitleLine;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		onResize(true, false);
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
		return new Library[]{Library.PLOTLY, Library.PLOTLY_LINE_CHART, Library.D3_DOWNLOAD};
	}


	private native void create(int widthHint)/*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.TrialsOverviewChart::xAxisTitleLine;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.TrialsOverviewChart::yAxisTitleLine;

		var height = Math.round(@jhi.germinate.client.util.JavaScript.D3::HEIGHT * 1.5);

		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		function unpack(rows, key) {
			return rows.map(function (row) {
				return row[key];
			});
		}

		$wnd.d3.tsv(filePath, function (data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.plotlyLineChart()
					.x("date")
					.legendOrientation("v")
					.hovermode("closest")
					.hovertemplate('%{y}')
					.columnsToIgnore(["date"])
					.getText(function (rows, dim) {
						return unpack(rows, dim);
					})
					.height(height)
					.colors(colors)
					.xaxisTitle(xAxisTitle)
					.yaxisTitle(yAxisTitle));
		});
	}-*/;

	@Override
	public int[] getDownloadSize()
	{
		return new int[]{1280, 800};
	}
}
