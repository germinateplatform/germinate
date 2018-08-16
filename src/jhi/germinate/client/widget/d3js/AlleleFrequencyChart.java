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
import jhi.germinate.client.page.allelefreq.*;
import jhi.germinate.shared.*;

/**
 * The {@link AlleleFrequencyChart} visualizes the allele frequency data.
 *
 * @author Sebastian Raubach
 */
public class AlleleFrequencyChart extends AbstractChart
{
	private AlleleFreqResultsPage parent;

	private String count     = Text.LANG.generalCount();
	private String frequency = Text.LANG.allelefreqFrequency();

	public AlleleFrequencyChart()
	{
	}

	public AlleleFrequencyChart(AlleleFreqResultsPage parent, String filePath)
	{
		this.parent = parent;
		this.filePath = filePath;
	}

	public void setParent(AlleleFreqResultsPage parent)
	{
		this.parent = parent;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		if (!StringUtils.isEmpty(filePath))
			onResize(true);
	}

	@Override
	protected void updateChart(int width)
	{
		if (!StringUtils.isEmpty(filePath))
			create(parent, width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "allele-frequency";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_BAR_CHART_FAKE_X, Library.D3_DOWNLOAD};
	}

	private native void create(AlleleFreqResultsPage parent, int widthHint)/*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;
		var barStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.AlleleFrequencyChartBundle::STYLE_BAR;

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;

		var countString = this.@jhi.germinate.client.widget.d3js.AlleleFrequencyChart::count;
		var frequencyString = this.@jhi.germinate.client.widget.d3js.AlleleFrequencyChart::frequency;

		var hightlightColor = @jhi.germinate.client.util.GerminateSettingsHolder::getCategoricalColor(*)(0);

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var p = parent;

		$wnd.d3
			.tsv(filePath,
				function (data) {
					$wnd.d3.select("#" + panelId)
						.datum(data)
						.call($wnd.barChartFakeX()
							.margin(margin)
							.width(width)
							.height(height)
							.x(function (d) {
								return parseFloat(d.position);
							})
							.y(function (d) {
								return parseFloat(d.count);
							})
							.tooltip(function (d) {
								return "(" + parseFloat(d.position).toFixed(2) + ", " + d.count + ")";
							})
							.axisStyle(axisStyle)
							.tooltipStyle(tooltipStyle)
							.barStyle(barStyle)
							.color(hightlightColor)
							.minimum(0)
							.maximum(1)
							.barPositionCallback(function (a, b) {
								p.@jhi.germinate.client.page.allelefreq.AlleleFreqResultsPage::notifyChartPositionAndWidth(*)(a, b)
							})
							.onClick(function (d) {
								p.@jhi.germinate.client.page.allelefreq.AlleleFreqResultsPage::notifyChartBarClicked(*)(d.x)
							})
							.xLabel(frequencyString)
							.yLabel(countString));
				});

		$wnd.alleleFreqShowHighlight = function (start, end) {
			$wnd.barChartFakeX.showHighlight(start, end);
		};

		$wnd.alleleFreqHideHighlight = function () {
			$wnd.barChartFakeX.hideHighlight();
		};
	}-*/;
}
