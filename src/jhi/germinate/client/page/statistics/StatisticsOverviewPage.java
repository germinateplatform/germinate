/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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
import java.util.Map;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class StatisticsOverviewPage extends GerminateComposite
{
	private GeoChart geoChart;

	@Override
	public Library[] getLibraryList()
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
		panel.add(new TaxonomyPieChart());

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
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Country.COUNTRY_NAME, country.getName());
						StringStringMapParameterStore.Inst.get().put(Parameter.tableFilterMapping, mapping);
						History.newItem(Page.BROWSE_ACCESSIONS.name());
					}

					@Override
					public void onSelectionCleared()
					{
					}
				});
				geoChart.setGeoChartType(GeoChart.GeoChartType.COUNT);

				geoChartPanel.add(new Heading(HeadingSize.H3, Text.LANG.dataStatisticsAccessionsPerCountryTitle()));
				geoChartPanel.add(new Label(Text.LANG.dataStatisticsAccessionsPerCountryText()));
				geoChartPanel.add(geoChart);
			}
		});

		panel.add(new DatasetStatsChart());
	}
}
