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
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.page.*;

/**
 * The {@link AlleleFreqSplitBinningChart} visualizes the allele frequency data.
 *
 * @author Sebastian Raubach
 */
public class AlleleFreqSplitBinningChart extends AbstractChart
{
	private FlowPanel chartPanel;

	private JsArrayString colors;
	private JsArrayNumber widths;
	private double splitPoint = 0.5;

	public AlleleFreqSplitBinningChart()
	{
	}

	public void update(JsArrayString colors, JsArrayNumber widths, double splitPoint)
	{
		this.colors = colors;
		this.widths = widths;
		this.splitPoint = splitPoint;

		onResize(true);
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		panel.add(chartPanel);

		onResize(true);
	}

	@Override
	protected void updateChart(int width)
	{
		if (chartPanel != null && colors != null && widths != null)
		{
			updateBinningDiv(0, 1, colors, widths, splitPoint);
		}
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return null;
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_FLAPJACK_BINNING};
	}

	private native void updateBinningDiv(int min, int max, JsArrayString colors, JsArrayNumber widths, double splitPoint)/*-{
		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.FlapjackBundle::STYLE_TOOLTIP;
		var rectStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.FlapjackBundle::STYLE_AREA;
		var separatorStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.FlapjackBundle::STYLE_SEPARATOR;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		$wnd.d3BinningDiv()
			.id(panelId)
			.min(min)
			.max(max)
			.colors(colors)
			.widths(widths)
			.splitPoint(splitPoint)
			.tooltipStyle(tooltipStyle)
			.rectStyle(rectStyle)
			.separatorStyle(separatorStyle)
			.hoverCallback(function (isHover, start, end) {
				if (isHover)
					$wnd.alleleFreqShowHighlight(start, end);
				else
					$wnd.alleleFreqHideHighlight();
			}).call();
	}-*/;
}
