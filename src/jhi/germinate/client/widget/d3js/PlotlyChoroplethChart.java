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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class PlotlyChoroplethChart extends AbstractChart implements PlotlyChart
{
	private boolean                      needsRedraw = true;
	private Callback<Country, Throwable> callback;

	public PlotlyChoroplethChart(String filePath, Callback<Country, Throwable> callback)
	{
		super();
		this.filePath = filePath;
		this.callback = callback;
	}

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

	@Override
	public int[] getDownloadSize()
	{
		return new int[]{1280, 600};
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

	private native void jsniClear(String id) /*-{
		$wnd.Plotly.purge($wnd.document.getElementById(id));
	}-*/;

	@Override
	protected void updateChart(int width)
	{
		create(width);
	}

	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "choropleth";
	}

	/**
	 * Handles selection of data points. Will redirect to {@link Page#PASSPORT}
	 *
	 * @param idString The country's id
	 * @param country  The country
	 */
	private void onCountryClicked(String idString, String country)
	{
		if (callback != null)
		{
			if (StringUtils.isEmpty(country))
			{
				callback.onSuccess(null);
			}
			else
			{
				long id;
				try
				{
					id = Long.parseLong(idString);
				}
				catch (Exception e)
				{
					id = -1;
				}
				callback.onSuccess(new Country(id).setName(country));
			}
		}
	}

	private native void create(int widthHint)/*-{
		var that = this;
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		$wnd.Plotly.d3.tsv(filePath, function (error, rows) {
			function unpack(rows, key) {
				return rows.map(function (row) {
					return row[key];
				});
			}

			function hexToRgbA(hex, a) {
				var c;
				if (/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)) {
					c = hex.substring(1).split('');
					if (c.length === 3) {
						c = [c[0], c[0], c[1], c[1], c[2], c[2]];
					}
					c = '0x' + c.join('');
					return 'rgba(' + [(c >> 16) & 255, (c >> 8) & 255, c & 255].join(',') + ',' + a + ')';
				}
				return hex;
			}

			var data = [{
				type: 'choropleth',
				locations: unpack(rows, 'code'),
				z: unpack(rows, 'count'),
				text: unpack(rows, 'country'),
				colorscale: [[0, hexToRgbA(colors[0], 0.1)], [1, hexToRgbA(colors[0], 1)]],
				autocolorscale: false,
				reversescale: false,
				marker: {
					line: {
						color: 'rgb(180,180,180)',
						width: 0.1
					}
				},
				tick0: 0,
				zmin: 0,
				dtick: 1000,
				colorbar: {
					autotic: false,
					tickprefix: '',
					thickness: 6
				}
			}];

			var layout = {
				autosize: true,
				height: 800,
				geo: {
					showframe: true,
					showcountries: true,
					showcoastlines: false,
					projection: {
						type: 'natural earth'
					}
				}
			};

			var config = {
				modeBarButtonsToRemove: ['toImage'],
				displayModeBar: true,
				responsive: true,
				displaylogo: false,
				scrollZoom: false
			};

			var chart = $wnd.document.getElementById(panelId);

			$wnd.Plotly.plot(chart, data, layout, config);

			chart.on('plotly_selected', function (data) {
				console.log('selected', data);
			});

			chart.on('plotly_click', function (data) {
				if (data && data.points && data.points.length > 0) {
					that.@jhi.germinate.client.widget.d3js.PlotlyChoroplethChart::onCountryClicked(*)(data.points[0].id, data.points[0].text);
				}
			});
		});
	}-*/;

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.D3_DOWNLOAD};
	}
}
