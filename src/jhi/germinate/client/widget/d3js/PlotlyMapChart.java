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
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.genotype.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class PlotlyMapChart extends AbstractChart implements PlotlyChart
{
	private boolean needsRedraw = true;

	private OnSelectionCallback selectionCallback;
	private Long                mapId;

	public PlotlyMapChart(Long mapId, OnSelectionCallback callback)
	{
		this.mapId = mapId;
		this.selectionCallback = callback;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		if (!StringUtils.isEmpty(filePath))
		{
			onResize(true, false);
		}
		else
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

						PlotlyMapChart.this.onResize(true, false);
					}
					else
					{
						Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
					}
				}
			});
		}
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
		return new int[]{1280, 1280};
	}

	@Override
	public void onResize(boolean containerResize, boolean force)
	{
		if ((needsRedraw && !StringUtils.isEmpty(filePath)) || force)
		{
			needsRedraw = false;
			super.onResize(containerResize, force);
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
		return "map-chart";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_MAP_CHART, Library.D3_DOWNLOAD};
	}

	private void onPointsSelected(int chromosome, double start, double end)
	{
		if (selectionCallback != null)
			selectionCallback.onSelection(new MapExportOptionsPanel.MappingEntry(Integer.toString(chromosome), (long) Math.floor(start), (long) Math.ceil(end)));
	}

	private native void create(int widthHint) /*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var colors = this.@jhi.germinate.client.widget.d3js.PlotlyBarChart::colors;

		var that = this;

		$wnd.Plotly.d3.xhr(filePath).get(function (error, response) {
			var dirtyTsv = response.responseText;
			var firstEOL = dirtyTsv.indexOf('\n');
			var tsv = "markerName\tchromosome\tposition\n" + dirtyTsv.substring(firstEOL + 1);
			var data = $wnd.Plotly.d3.tsv.parse(tsv); // Remove the first row (Flapjack header)

			$wnd.Plotly.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.plotlyMapChart()
					.colors(colors)
					.onPointsSelected(function (chromosome, start, end) {
						that.@jhi.germinate.client.widget.d3js.PlotlyMapChart::onPointsSelected(*)(chromosome, start, end);
					}));
		});
	}-*/;

	public interface OnSelectionCallback
	{
		void onSelection(MapExportOptionsPanel.MappingEntry selection);
	}
}
