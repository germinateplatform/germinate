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

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class MapChartCanvas extends AbstractChart
{
	private FlowPanel chartPanel;
	private Long      mapId;

	public MapChartCanvas(Long mapId)
	{
		this.mapId = mapId;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		panel.add(chartPanel);

		getData();
	}

	private void getData()
	{
		MapService.Inst.get().getInFormat(Cookie.getRequestProperties(), mapId, MapFormat.flapjack, null, new DefaultAsyncCallback<ServerResult<String>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				super.onFailureImpl(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				if (result.hasData())
				{
					filePath = new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

					MapChartCanvas.this.onResize(true);
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
		chartPanel.clear();
		removeD3(panelId);
		create(width);
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
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_MAP_CANVAS};
	}

	private native void create(int widthHint) /*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;

		var color = @jhi.germinate.client.util.GerminateSettingsHolder::getCategoricalColor(*)(0);

		$wnd.d3.xhr(filePath).get(function (err, response) {
			var dirtyTsv = response.responseText;
			var firstEOL = dirtyTsv.indexOf('\n');
			var tsv = "markerName\tchromosome\tposition\n" + dirtyTsv.substring(firstEOL + 1);
			var data = $wnd.d3.tsv.parse(tsv); // Remove the first row (Flapjack header)

			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.mapCanvas()
					.tooltipStyle(tooltipStyle)
					.preferredWidth(width)
					.margin(margin)
					.color(color)
					.excludePercentage(1)
					.individualHeight(40));
		});
	}-*/;
}
