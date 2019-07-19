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

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.page.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class PlotlyBarChart extends AbstractChart implements PlotlyChart
{
	private String xAxisTitle;
	private String yAxisTitle;
	private String x;

	private boolean needsRedraw = true;

	private Config config;

	public PlotlyBarChart(Config config)
	{
		super();
		this.config = config;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.xAxisTitle = config != null ? config.xAxisTitle : "";
		this.yAxisTitle = config != null ? config.yAxisTitle : "";
		this.x = config != null ? config.x : "";
		this.filePath = config != null ? config.filePath : null;

		panel.add(chartPanel);

		if (!StringUtils.isEmpty(filePath))
			onResize(true);
	}

	@Override
	public void clear()
	{
		jsniClear(panelId);
		super.clear();
	}

	private native void jsniClear(String id) /*-{
		$wnd.Plotly.purge($wnd.document.getElementById(id));
	}-*/;

	@Override
	public int[] getDownloadSize()
	{
		return new int[]{1280, 500};
	}

	@Override
	public void onResize(boolean containerResize)
	{
		if (needsRedraw)
		{
			needsRedraw = false;
			super.onResize(containerResize);
		}
	}

	public void onBarClicked(String text)
	{
		if (config != null && config.clickCallback != null)
			config.clickCallback.onSuccess(text);
	}

	@Override
	protected void updateChart(int width)
	{
		create(width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return (config != null && config.downloadFilename != null) ? config.downloadFilename : "bar-chart";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_BAR, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint) /*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyBarChart::xAxisTitle;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyBarChart::yAxisTitle;
		var x = this.@jhi.germinate.client.widget.d3js.PlotlyBarChart::x;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;
		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		var that = this;

		$wnd.Plotly.d3.tsv(filePath, function (error, rows) {
			$wnd.Plotly.d3.select('#' + panelId)
				.datum(rows)
				.call($wnd.plotlyBarChart()
					.height(height)
					.colors(colors)
					.x(x)
					.xCategory(xAxisTitle)
					.yCategory(yAxisTitle)
					.onPointClicked(function (data) {
						that.@jhi.germinate.client.widget.d3js.PlotlyBarChart::onBarClicked(*)(data);
					}));
		});
	}-*/;

	public static class Config
	{
		private String xAxisTitle       = null;
		private String yAxisTitle       = null;
		private String downloadFilename = null;
		private String x                = null;
		private String filePath         = null;

		private Callback<String, Throwable> clickCallback;

		public Config()
		{
		}

		public Config(String xAxisTitle, String yAxisTitle, String downloadFilename, String x, String filePath, Callback<String, Throwable> clickCallback)
		{
			this.xAxisTitle = xAxisTitle;
			this.yAxisTitle = yAxisTitle;
			this.downloadFilename = downloadFilename;
			this.x = x;
			this.filePath = filePath;
			this.clickCallback = clickCallback;
		}

		public Config setxAxisTitle(String xAxisTitle)
		{
			this.xAxisTitle = xAxisTitle;
			return this;
		}

		public Config setyAxisTitle(String yAxisTitle)
		{
			this.yAxisTitle = yAxisTitle;
			return this;
		}

		public Config setFilePath(String filePath)
		{
			this.filePath = filePath;
			return this;
		}

		public Config setDownloadFilename(String downloadFilename)
		{
			this.downloadFilename = downloadFilename;
			return this;
		}

		public Config setX(String x)
		{
			this.x = x;
			return this;
		}

		public Config setClickCallback(Callback<String, Throwable> clickCallback)
		{
			this.clickCallback = clickCallback;
			return this;
		}
	}
}
