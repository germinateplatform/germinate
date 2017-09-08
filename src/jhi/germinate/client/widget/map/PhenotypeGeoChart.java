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

package jhi.germinate.client.widget.map;

import com.google.gwt.core.client.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class PhenotypeGeoChart extends GerminateComposite
{
	private List<Long>     datasetIds;
	private ExperimentType experimentType;

	private GerminateValueListBox<Phenotype> phenotypeBox;
	private GeoChart                         geoChart;

	public PhenotypeGeoChart(ExperimentType experimentType)
	{
		this.experimentType = experimentType;
	}

	@Override
	public void onResize(boolean containerResize)
	{
		if (geoChart != null)
			geoChart.onResize(containerResize);
	}

	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.GOOGLE_CHARTS};
	}

	@Override
	protected void setUpContent()
	{
		switch (experimentType)
		{
			case trials:
				datasetIds = LongListParameterStore.Inst.get().get(Parameter.trialsDatasetIds);
				break;
		}

		phenotypeBox = new PhenotypeListBox(false);
		phenotypeBox.addValueChangeHandler(event -> updateGeoChart(event.getValue().get(0).getId()));

		geoChart = new GeoChart(null);

		panel.add(phenotypeBox);

		Scheduler.get().scheduleDeferred(() -> panel.add(geoChart));

		queryForPhenotypes();
	}

	private void queryForPhenotypes()
	{
		PhenotypeService.Inst.get().get(Cookie.getRequestProperties(), datasetIds, experimentType, true, new DefaultAsyncCallback<ServerResult<List<Phenotype>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<Phenotype>> result)
			{
				if (!CollectionUtils.isEmpty(result.getServerResult()))
				{
					Phenotype dummy = new Phenotype(null)
							.setName(Text.LANG.phenotypeExportGeoChartAllPhenotypes());

					result.getServerResult().add(0, dummy);

					phenotypeBox.setValue(dummy, false);
					phenotypeBox.setAcceptableValues(result.getServerResult());

					if (!geoChart.hasData() && geoChart.isVisible())
					{
						updateGeoChart(null);
					}
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
					panel.clear();
				}
			}
		});
	}

	private void updateGeoChart(final Long phenotypeId)
	{
		LocationService.Inst.get().getCountryValues(Cookie.getRequestProperties(), datasetIds, experimentType, phenotypeId, new DefaultAsyncCallback<ServerResult<List<Country>>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				geoChart.update(null);
				super.onFailureImpl(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<List<Country>> result)
			{
				if (phenotypeId == null)
					geoChart.setGeoChartType(GeoChart.GeoChartType.COUNT);
				else
					geoChart.setGeoChartType(GeoChart.GeoChartType.AVERAGE);

				geoChart.update(result.getServerResult());
			}
		});
	}

	public void update()
	{
		if (geoChart != null && !geoChart.hasData())
		{
			updateGeoChart(null);
		}
	}
}
