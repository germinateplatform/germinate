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
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class PedigreeChart extends AbstractChart
{
	private Long accessionId;

	public PedigreeChart(Long accessionId)
	{
		this.accessionId = accessionId;
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		getData();
	}

	private void getData()
	{
		List<Long> ids = new ArrayList<>();
		ids.add(accessionId);
		PedigreeService.Inst.get().exportToHelium(Cookie.getRequestProperties(), ids, Pedigree.PedigreeQuery.UP_DOWN_GRANDPARENTS, new DefaultAsyncCallback<ServerResult<String>>()
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

					PedigreeChart.this.onResize(true, false);
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
		create(width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "pedigreeplot";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	public void onNodeClicked(String accessionName)
	{
		StringParameterStore.Inst.get().put(Parameter.accessionName, accessionName);

		if (History.getToken().equals(Page.PASSPORT.name()))
			History.fireCurrentHistoryState();
		else
			History.newItem(Page.PASSPORT.name());
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_DAGRE, Library.D3_PEDIGREE_CHART, Library.D3_DOWNLOAD};
	}

	private native void create(int widthHint) /*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var nodeStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.PedigreeChartBundle::STYLE_NODE;
		var edgeStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.PedigreeChartBundle::STYLE_EDGE_PATH;
		var maleStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.PedigreeChartBundle::STYLE_MALE;
		var femaleStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.PedigreeChartBundle::STYLE_FEMALE;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;

		var that = this;

		$wnd.d3.xhr(filePath).get(function (err, response) {
			var dirtyTsv = response.responseText;
			var firstEOL = dirtyTsv.indexOf('\n');
			var parsedTsv = $wnd.d3.tsv.parse(dirtyTsv.substring(firstEOL + 1)); // Remove the first row (Helium header)

			var nodes = {};
			var connections = [];

			// First, add the parents (important for layout)
			parsedTsv.forEach(function (d) {
				nodes[d.Parent] = null;
			});

			// Then the children and the edges
			parsedTsv.forEach(function (d) {
				nodes[d.LineName] = null;

				var style = "";

				if (d.ParentType === 'F')
					style = femaleStyle;
				else if (d.ParentType === 'M')
					style = maleStyle;

				connections.push({
					from: d.Parent,
					to: d.LineName,
					style: style
				});
			});

			var data = [];

			for (var node in nodes) {
				if (nodes.hasOwnProperty(node)) {
					data.push({
						label: node
					});
				}
			}

			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.pedigreeChart()
					.margin(margin)
					.width(width)
					.height(height)
					.nodeStyle(nodeStyle)
					.edgeStyle(edgeStyle)
					.connections(connections)
					.nodeShape("circle")
					.onClick(function (d) {
						that.@jhi.germinate.client.widget.d3js.PedigreeChart::onNodeClicked(*)(d);
					})
					.interpolate("bundle"));
		});
	}-*/;
}
