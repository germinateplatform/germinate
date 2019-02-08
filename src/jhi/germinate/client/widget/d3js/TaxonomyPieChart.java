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
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

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
public class TaxonomyPieChart extends AbstractChart
{
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

					TaxonomyPieChart.this.onResize(true);
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
		return new Library[]{Library.D3_V3, Library.D3_PIE, Library.D3_DOWNLOAD};
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
		var width = widthHint, height = @jhi.germinate.client.util.JavaScript.D3::HEIGHT;
		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		var that = this;

		$wnd.d3.tsv(filePath, function (error, data) {
			if (error) throw error;

			var newData = [];

			data.forEach(function (d) {
				var label = d.genus;

				if (d.species) {
					label += " " + d.species;
				}
				if (d.subtaxa) {
					label += " " + d.subtaxa;
				}

				newData.push({
					datum: d,
					label: label,
					value: parseFloat(d.count)
				});
			});

			new $wnd.d3pie(panelId, {
				size: {
					pieOuterRadius: "90%",
					canvasHeight: height,
					canvasWidth: width
				},
				data: {
					content: newData
				},
				labels: {
					outer: {
						hideWhenLessThanPercentage: 2
					},
					inner: {
						hideWhenLessThanPercentage: 5
					}
				},
				tooltips: {
					enabled: true,
					type: "placeholder",
					string: "{label}: {value}"
				},
				misc: {
					colors: {
						segments: colors
					}
				},
				callbacks: {
					onClickSegment: function (a) {
						that.@jhi.germinate.client.widget.d3js.TaxonomyPieChart::onClickSegment(*)(a.data.datum.genus, a.data.datum.species, a.data.datum.subtaxa);
					}
				}
			});
		});
	}-*/;
}
