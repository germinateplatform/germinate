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
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

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
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class TaxonomyPieChart extends AbstractChart implements PlotlyChart
{
	private boolean needsRedraw = true;

	@Override
	public void onResize(boolean containerResize, boolean force)
	{
		if (force || (needsRedraw && !StringUtils.isEmpty(filePath)))
		{
			needsRedraw = false;
			super.onResize(containerResize, force);
		}
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(new Heading(HeadingSize.H3, Text.LANG.dataStatisticsTaxonomyTitle()));
		panel.add(new Label(Text.LANG.dataStatisticsTaxonomyText()));
		panel.add(chartPanel);

		CommonService.Inst.get().getTaxonomyStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<String>>(true)
		{
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

					TaxonomyPieChart.this.onResize(true, false);
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
				}
			}
		});
	}

	@Override
	public int[] getDownloadSize()
	{
		return new int[]{1280, 800};
	}

	@Override
	protected void updateChart(int width)
	{
		create(width);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "taxonomy-stats";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		return null;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_PIE_CHART, Library.D3_DOWNLOAD};
	}

	private void onClickSegment(String genus, String species, String subtaxa)
	{
		try
		{
			PartialSearchQuery query = new PartialSearchQuery();
			query.add(new SearchCondition(Taxonomy.GENUS, new Equal(), genus, String.class));
			if (!StringUtils.isEmpty(species))
				query.add(new SearchCondition(Taxonomy.SPECIES, new Equal(), species, String.class));
			if (!StringUtils.isEmpty(subtaxa))
				query.add(new SearchCondition(Taxonomy.SUBTAXA, new Equal(), subtaxa, String.class));

			/* Save it to the parameter store and change to the browse page */
			FilterMappingParameterStore.Inst.get().put(Parameter.tableFilterMapping, query);
			History.newItem(Page.ACCESSION_OVERVIEW.name());
		}
		catch (Exception e)
		{
		}
	}

	private native void create(int widthHint)/*-{
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;
		var height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;
		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		var that = this;

		function unpackAndJoin(rows, keys) {
			return rows.map(function (row) {
				var result = row[keys[0]];
				for(var i = 1; i < keys.length; i++) {
					result += ' ' + row[keys[i]];
				}
				return result;
			});
		}

		$wnd.Plotly.d3.tsv(filePath, function (error, rows) {
			$wnd.Plotly.d3.select("#" + panelId)
				.datum(rows)
				.call($wnd.plotlyPieChart()
					.labels(function (rows) {
						return unpackAndJoin(rows, ["genus", "species", "subtaxa"]);
					})
					.custom(function (rows) {
						return rows.map(function (r) {
							return {
								genus: r.genus,
								species: r.species,
								subtaxa: r.subtaxa
							};
						})
					})
					.onSliceClicked(function (selection) {
						var index = selection.points[0].i;
						var custom = selection.points[0].data.custom[index];

						that.@jhi.germinate.client.widget.d3js.TaxonomyPieChart::onClickSegment(*)(custom.genus, custom.species, custom.subtaxa);
					})
					.height(height)
					.colors(colors)
				);
		});
	}-*/;
}
