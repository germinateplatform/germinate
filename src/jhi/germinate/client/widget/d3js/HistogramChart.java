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
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class HistogramChart extends AbstractChart
{
	private Long           id;
	private Long           datasetId;
	private ExperimentType type;

	private AsyncCallback<ServerResult<String>> callback = new DefaultAsyncCallback<ServerResult<String>>()
	{
		@Override
		public void onSuccessImpl(ServerResult<String> result)
		{
			filePath = new ServletConstants.Builder()
					.setUrl(GWT.getModuleBaseURL())
					.setPath(ServletConstants.SERVLET_FILES)
					.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
					.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
					.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

			onResize(true);
		}
	};

	public HistogramChart(String filePath)
	{
		super();
		this.filePath = filePath;
	}

	public HistogramChart(Long id, Long datasetId, ExperimentType type)
	{
		super();
		this.id = id;
		this.datasetId = datasetId;
		this.type = type;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		Scheduler.get().scheduleDeferred(() -> {
			if(StringUtils.isEmpty(filePath))
				getData();
			else
				onResize(true);
		});
	}

	private void getData()
	{
		switch (type)
		{
			case trials:
				PhenotypeService.Inst.get().getHistogramData(Cookie.getRequestProperties(), id, datasetId, callback);
				break;
			case compound:
				CompoundService.Inst.get().getHistogramData(Cookie.getRequestProperties(), id, datasetId, callback);
				break;
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
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_HISTOGRAM, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint)/*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = (@jhi.germinate.client.util.JavaScript.D3::HEIGHT) / 2;
		var hightlightColor = @jhi.germinate.client.util.GerminateSettingsHolder::getCategoricalColor(*)(0);

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;
		var barStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.HistogramChartBundle::STYLE_BAR;

		console.log(width);

		$wnd.d3.tsv(filePath, function (data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.histogram()
					.width(width)
					.margin(margin)
					.y(function (d) {
						return parseFloat(d.value);
					})
					.height(height)
					.tooltip(function (d) {
						return d.length + "";
					})
					.nrOfBars(30)
					.barStyle(barStyle)
					.axisStyle(axisStyle)
					.tooltipStyle(tooltipStyle)
					.yLabel("Count"));
		});
	}-*/;
}
