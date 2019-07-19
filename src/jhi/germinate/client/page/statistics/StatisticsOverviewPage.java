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

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.resource.*;

/**
 * @author Sebastian Raubach
 */
public class StatisticsOverviewPage extends GerminateComposite implements ParallaxBannerPage
{
	@Override
	public Library[] getLibraries()
	{
		return null;
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
		biostat.add(new BiologicalStatusStats());
		row.add(taxonomy);
		row.add(biostat);
		panel.add(row);

		// Add the PDCI chart (if enabled)
		if (GerminateSettingsHolder.get().pdciEnabled.getValue())
		{
			panel.add(new PDCIStats());
		}

		// Add the geographic distribution map
		panel.add(new CountryStats());

		// Add the dataset statistics chart
		panel.add(new DatasetStats());
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxDataStats();
	}
}
