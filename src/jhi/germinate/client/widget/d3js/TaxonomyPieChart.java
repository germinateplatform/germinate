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

import java.util.*;
import java.util.Map;

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
public class TaxonomyPieChart extends AbstractChart
{
	private FlowPanel chartPanel;

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		panel.add(new Heading(HeadingSize.H3, Text.LANG.dataStatisticsTaxonomyTitle()));
		panel.add(new Label(Text.LANG.dataStatisticsTaxonomyText()));
		panel.add(chartPanel);

		CommonService.Inst.get().getTaxonomyStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<String>>(true)
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
		create(chartPanel.getOffsetWidth());
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
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_PIE, Library.D3_DOWNLOAD};
	}

	private void onClickSegment(String genusSpecies)
	{
		try
		{
			/* Split on the first space */
			int index = genusSpecies.indexOf(" ");
			String genus = genusSpecies.substring(0, index);
			String species = genusSpecies.substring(index + 1);

			/* Create the mapping */
			Map<String, String> mapping = new HashMap<>();
			mapping.put(Taxonomy.GENUS, genus);
			mapping.put(Taxonomy.SPECIES, species);

			/* Save it to the parameter store and change to the browse page */
			StringStringMapParameterStore.Inst.get().put(Parameter.tableFilterMapping, mapping);
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
				newData.push({
					label: d.taxonomy,
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
						console.log(a);
						that.@jhi.germinate.client.widget.d3js.TaxonomyPieChart::onClickSegment(*)(a.data.label);
					}
				}
			});
		});
	}-*/;
}
