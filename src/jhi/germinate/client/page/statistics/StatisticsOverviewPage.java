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

package jhi.germinate.client.page.statistics;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class StatisticsOverviewPage extends GerminateComposite implements ParallaxBannerPage
{
	private GeoChart geoChart;

	@Override
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	public void onResize(boolean containerResize)
	{
		if (containerResize)
		{
			if (geoChart != null)
				geoChart.onResize(containerResize);
		}
	}

	@Override
	protected void setUpContent()
	{
		PageHeader header = new PageHeader();
		header.setText(Text.LANG.dataStatisticsTitle());
		panel.add(header);

		// Add the taxonomy pie chart
		Row row = new Row();
		Column taxonomy = new Column(ColumnSize.XS_12, ColumnSize.LG_6);
		Column biostat = new Column(ColumnSize.XS_12, ColumnSize.LG_6);
		taxonomy.add(new TaxonomyPieChart());
		biostat.add(new BiologicalStatusPieChart());
		row.add(taxonomy);
		row.add(biostat);
		panel.add(row);

		// Add the PDCI chart (if enabled)
		if (GerminateSettingsHolder.get().pdciEnabled.getValue())
			panel.add(new PDCIStatsChart());

		// Add the geographic distribution map
		final FlowPanel geoChartPanel = new FlowPanel();
		panel.add(geoChartPanel);
		CommonService.Inst.get().getCountryStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<List<Country>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<Country>> result)
			{
				geoChart = new GeoChart(result.getServerResult(), new GeoChart.CountrySelectionHandler()
				{
					@Override
					public void onCountySelected(Country country)
					{
						PartialSearchQuery query = new PartialSearchQuery();
						query.add(new SearchCondition(Country.COUNTRY_NAME, new Equal(), country.getName(), String.class));
						FilterMappingParameterStore.Inst.get().put(Parameter.tableFilterMapping, query);
						History.newItem(Page.ACCESSION_OVERVIEW.name());
					}

					@Override
					public void onSelectionCleared()
					{
					}
				});

				geoChartPanel.add(new Heading(HeadingSize.H3, Text.LANG.dataStatisticsAccessionsPerCountryTitle()));
				geoChartPanel.add(new Label(Text.LANG.dataStatisticsAccessionsPerCountryText()));
				geoChartPanel.add(geoChart);
			}
		});

		// Add the dataset statistics chart
		panel.add(new DatasetStatsChart());
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxDataStats();
	}
}
