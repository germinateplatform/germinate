/*
 *  Copyright 2019 Information and Computational Sciences,
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

import jhi.germinate.client.page.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class PlotlyHistogramChart extends AbstractChart implements PlotlyChart
{
	private boolean needsRedraw = true;
	private String xAxisTitle = "";
	private String yAxisTitle = "";

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		if (!StringUtils.isEmpty(filePath))
			onResize(true, false);
	}

	@Override
	public int[] getDownloadSize()
	{
		return new int[]{1280, 600};
	}

	@Override
	public void onResize(boolean containerResize, boolean force)
	{
		if (force || (needsRedraw && !StringUtils.isEmpty(filePath)))
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
		return "histogram";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_HISTOGRAM_CHART, Library.D3_DOWNLOAD};
	}

	public String getxAxisTitle()
	{
		return xAxisTitle;
	}

	public PlotlyHistogramChart setxAxisTitle(String xAxisTitle)
	{
		this.xAxisTitle = xAxisTitle;
		return this;
	}

	public String getyAxisTitle()
	{
		return yAxisTitle;
	}

	public PlotlyHistogramChart setyAxisTitle(String yAxisTitle)
	{
		this.yAxisTitle = yAxisTitle;
		return this;
	}

	private native void create(int widthHint)/*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var height = Math.round(@jhi.germinate.client.util.JavaScript.D3::HEIGHT * 0.8);
		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();
		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyHistogramChart::xAxisTitle;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyHistogramChart::yAxisTitle;

		$wnd.d3.tsv(filePath, function (data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.plotlyHistogramChart()
					.xAxisTitle(xAxisTitle)
					.yAxisTitle(yAxisTitle)
					.height(height)
					.colors(colors));
		});
	}-*/;
}
