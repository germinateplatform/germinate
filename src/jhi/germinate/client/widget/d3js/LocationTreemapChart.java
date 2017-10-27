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

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link LocationTreemapChart} is a page that shows the locations in a treemap. Will redirect to {@link Page#ACCESSIONS_FOR_COLLSITE} when user
 * clicks on a collsite.
 *
 * @author Sebastian Raubach
 */
public class LocationTreemapChart extends AbstractChart
{
	//	private final String items     = Text.LANG.collsiteTreemapItems();
	private final String locations = Text.LANG.collsiteTreemapLocation();
	private FlowPanel chartPanel;

	private LocationType type = null;

	/**
	 * Redirects the user to the {@link Page#ACCESSIONS_FOR_COLLSITE} page
	 *
	 * @param id The id of the selected collectingsite
	 */
	public void redirectToAccessionsForCollsitePage(String id)
	{
		if (type == LocationType.collectingsites)
		{
			try
			{
				Long longId = Long.parseLong(id);

				LongParameterStore.Inst.get().put(Parameter.collectingsiteId, longId);
				History.newItem(Page.ACCESSIONS_FOR_COLLSITE.name());
			}
			catch (NumberFormatException e)
			{

			}
		}
	}

	public void setLocationType(LocationType type)
	{
		this.type = type;
	}

	@Override
	protected void createContent(final FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		panel.add(chartPanel);
	}

	@Override
	protected void updateChart(int width)
	{
		d3(chartPanel.getOffsetWidth());
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "treemap";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	private native void d3(int widthHint)/*-{
		var width = widthHint;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT * 1.2;

		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;

		var grandparentStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.TreemapBundle::STYLE_GRANDPARENT;
		var parentStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.TreemapBundle::STYLE_PARENT;
		var childrenStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.TreemapBundle::STYLE_CHILDREN;
		var childStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.TreemapBundle::STYLE_CHILD;
		var rectStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.TreemapBundle::STYLE_RECT;
		var textStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.TreemapBundle::STYLE_TEXT;
		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;

		var collsiteString = this.@jhi.germinate.client.widget.d3js.LocationTreemapChart::locations;

		var color = $wnd.d3.scale
			.ordinal()
			.range(@jhi.germinate.client.util.JavaScript.D3::getColorPalette()());

		var that = this;

		$wnd.d3.json(filePath, function (data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.treemap()
					.width(width)
					.height(height)
					.color(color)
					.grandparentStyle(grandparentStyle)
					.parentStyle(parentStyle)
					.childrenStyle(childrenStyle)
					.childStyle(childStyle)
					.rectStyle(rectStyle)
					.textStyle(textStyle)
					.tooltipStyle(tooltipStyle)
					.overallHeaderName(collsiteString)
					.onClick(function (d) {
						that.@jhi.germinate.client.widget.d3js.LocationTreemapChart::redirectToAccessionsForCollsitePage(*)(d._children[0].id);
					})
					.tooltip(function (d) {
						return d.key + " (" + d.value + ")";
					}));
		});
	}-*/;

	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_TREEMAP, Library.D3_DOWNLOAD};
	}
}
