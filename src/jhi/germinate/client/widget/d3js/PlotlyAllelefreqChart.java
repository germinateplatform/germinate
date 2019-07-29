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

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class PlotlyAllelefreqChart extends AbstractChart implements PlotlyChart
{
	private String        xAxisTitle = Text.LANG.allelefreqFrequency();
	private String        yAxisTitle = Text.LANG.generalCount();
	private JsArrayNumber widths;
	private JsArrayString colors;

	private boolean needsRedraw = true;

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
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
		if (needsRedraw && !StringUtils.isEmpty(filePath))
		{
			needsRedraw = false;
			super.onResize(containerResize);
		}
	}

	@Override
	public void forceRedraw()
	{
		this.needsRedraw = true;
		super.forceRedraw();
	}

	@Override
	protected void updateChart(int width)
	{
		create(width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "allele-freq";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_ALLELE_FREQ_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint) /*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyAllelefreqChart::xAxisTitle;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyAllelefreqChart::yAxisTitle;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;
		var widths = this.@jhi.germinate.client.widget.d3js.PlotlyAllelefreqChart::widths;
		var colors = this.@jhi.germinate.client.widget.d3js.PlotlyAllelefreqChart::colors;

		var that = this;

		$wnd.Plotly.d3.tsv(filePath, function (error, rows) {
			$wnd.Plotly.d3.select('#' + panelId)
				.datum(rows)
				.call($wnd.plotlyAlleleFreqChart()
					.height(height)
					.widths(widths)
					.colors(colors)
					.x('position')
					.y('count')
					.xCategory(xAxisTitle)
					.yCategory(yAxisTitle)
					.onPointClicked(function (data) {
						// TODO
//						that.@jhi.germinate.client.widget.d3js.PlotlyAllelefreqChart::onBarClicked(*)(data);
					}));
		});
	}-*/;

	public void update(JsArrayNumber widths, JsArrayString colors)
	{
		this.widths = widths;
		this.colors = colors;
	}
}
