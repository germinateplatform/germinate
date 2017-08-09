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
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class CompoundBarChart extends AbstractChart
{
	private static final int LIMIT = 500;

	private FlowPanel chartPanel;
	private Element   sortValues;

	private String yAxisTitle = Text.LANG.compoundBarChartYAxisTitle();

	private Long compoundId;
	private Long datasetId;

	private Label dataLimitWarning = new Label(Text.LANG.compoundBarChartDataLimitWarning(LIMIT));

	public CompoundBarChart(Long compoundId, Long datasetId)
	{
		this.compoundId = compoundId;
		this.datasetId = datasetId;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		createCheckBox(chartPanel);

		dataLimitWarning.setVisible(false);
		dataLimitWarning.setStyleName(Emphasis.DANGER.getCssName());

		panel.add(dataLimitWarning);
		panel.add(chartPanel);

		CompoundService.Inst.get().getBarChartData(Cookie.getRequestProperties(), compoundId, datasetId, new DefaultAsyncCallback<ServerResult<String>>(false)
		{
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

					CompoundBarChart.this.onResize(true);
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
				}
			}
		});
	}

	private void createCheckBox(FlowPanel parent)
	{
		sortValues = Document.get().createCheckInputElement();
		LabelElement labelElem = Document.get().createLabelElement();

		parent.getElement().appendChild(sortValues);
		parent.getElement().appendChild(labelElem);

		String uid = DOM.createUniqueId();
		sortValues.setPropertyString("id", uid);
		labelElem.setHtmlFor(uid);

		labelElem.setInnerText("Sort by value");
	}

	private void dataLimitReached(boolean reached)
	{
		dataLimitWarning.setVisible(reached);
	}

	@Override
	protected void updateChart(int width)
	{
		create(chartPanel.getOffsetWidth());
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "compound-details";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_BAR_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint)/*-{
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;
		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;

		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.CompoundBarChart::yAxisTitle;

		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var color = @jhi.germinate.client.util.GerminateSettingsHolder::getCategoricalColor(*)(0);

		var checkbox = this.@jhi.germinate.client.widget.d3js.CompoundBarChart::sortValues;

		var limit = @jhi.germinate.client.widget.d3js.CompoundBarChart::LIMIT;

		var that = this;

		$wnd.d3.tsv(filePath, function (error, data) {

			if (data.length > limit) {
				data = data.slice(0, limit);

				that.@jhi.germinate.client.widget.d3js.CompoundBarChart::dataLimitReached(*)(true);
			}
			else {
				that.@jhi.germinate.client.widget.d3js.CompoundBarChart::dataLimitReached(*)(false);
			}

			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.barChart()
					.margin(margin)
					.width(width)
					.height(height)
					.x(function (d) {
						return d.name;
					})
					.y(function (d) {
						return parseFloat(d.value);
					})
					.tooltip(function (d) {
						return d.name + ": " + parseFloat(d.value);
					})
					.tooltipStyle(tooltipStyle)
					.axisStyle(axisStyle)
					.color(color)
					.outerPadding(0)
					.bindSort(checkbox)
					.removeXAxisTicks(true)
					.yLabel(yAxisTitle))
		});
	}-*/;
}
