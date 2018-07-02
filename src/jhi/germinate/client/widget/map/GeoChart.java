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

package jhi.germinate.client.widget.map;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gwt.charts.client.*;
import com.googlecode.gwt.charts.client.event.*;
import com.googlecode.gwt.charts.client.format.*;
import com.googlecode.gwt.charts.client.geochart.*;
import com.googlecode.gwt.charts.client.options.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.Color;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class GeoChart extends GerminateComposite
{
	public enum GeoChartType
	{
		COUNT,
		AVERAGE
	}

	private GeoChartType type = GeoChartType.COUNT;

	private SimplePanel chartPanel = new SimplePanel();
	private List<Country>           chartData;
	private CountrySelectionHandler handler;

	public GeoChart(List<Country> chartData)
	{
		this.chartData = chartData;
	}

	public GeoChart(List<Country> chartData, CountrySelectionHandler handler)
	{
		this.chartData = chartData;
		this.handler = handler;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.GOOGLE_CHARTS};
	}

	@Override
	protected void setUpContent()
	{
		panel.add(chartPanel);

		if (chartData != null)
			update(chartData);
	}

	@Override
	public void onResize(boolean containerResize)
	{
		if (containerResize)
		{
			update(chartData);
		}
	}

	public void setGeoChartType(GeoChartType type)
	{
		this.type = type;
	}

	/**
	 * Draws the Google GeoChart with the data from the server
	 */
	public void update(final List<Country> chartData)
	{
		this.chartData = chartData;

		chartPanel.clear();

		/* If there is no data, return */
		if (chartData == null)
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
			chartPanel.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoDataFound()));
			return;
		}

        /* Set the axis colors */
		GeoChartColorAxis axis = GeoChartColorAxis.create();
		String lower = Color.fromHex(GerminateSettingsHolder.getCategoricalColor(0))
							.toTransparency(0.2f)
							.toHexValue();
		axis.setColors(lower, GerminateSettingsHolder.getCategoricalColor(0));

        /* Set the GeoChart options */
		GeoChartLegend legend = GeoChartLegend.createObject().cast();
		legend.setNumberFormat(".##");

		GeoChartOptions options = GeoChartOptions.create();
		options.setDisplayMode(DisplayMode.REGIONS);
		options.setColorAxis(axis);
		options.setLegend(legend);

        /* Set up the DataTable */
		final DataTable data = DataTable.create();

		data.addColumn(ColumnType.STRING, "Country");
		if (type == GeoChartType.COUNT)
			data.addColumn(ColumnType.NUMBER, Text.LANG.generalCount());
		else
			data.addColumn(ColumnType.NUMBER, Text.LANG.generalAverage());
		data.addColumn(ColumnType.STRING, "Display");

		data.addRows(chartData.size());

		int i = 0;
		for (Country row : chartData)
		{
			data.setValue(i, 0, row.getCountryCode2());
			if (type == GeoChartType.COUNT)
				data.setValue(i, 1, row.getExtra(Country.COUNT));
			else
				data.setValue(i, 1, row.getExtra(Country.AVERAGE));
			data.setValue(i, 2, row.getName());

			i++;
		}

		/* Format the average values to two decimal places */
		if (type == GeoChartType.AVERAGE)
		{
			NumberFormatOptions numberFormatOptions = NumberFormatOptions.create();
			numberFormatOptions.setFractionDigits(2);
			NumberFormat numberFormat = NumberFormat.create(numberFormatOptions);

			numberFormat.format(data, 1);
		}

        /* We want to show the "Display" column in the tooltip rather than
		 * "Country" */
		PatternFormat format = PatternFormat.create("{1}");
		format.format(data, 0, 2);

		JsArrayInteger columns = JsArrayInteger.createArray().cast();
		columns.push(0);
		columns.push(1);

		DataView view = DataView.create(data);
		view.setColumns(columns);

        /* Create the chart from the view */
		final com.googlecode.gwt.charts.client.geochart.GeoChart chart = new com.googlecode.gwt.charts.client.geochart.GeoChart();
		chartPanel.add(chart);
		chart.draw(view, options);

		if (handler != null)
		{
			/* Listen for selection events */
			chart.addSelectHandler(new SelectHandler()
			{
				@Override
				public void onSelect(SelectEvent event)
				{
					/* Get the user selection */
					JsArray<Selection> selection = chart.getSelection();

					if (selection.length() > 0)
					{
						/* If there is a selection, get the first and notify the handler */
						Selection s = selection.get(0);
						handler.onCountySelected(chartData.get(s.getRow()));
					}
					else
					{
						/* If the selection is empty, notify the handler */
						handler.onSelectionCleared();
					}
				}
			});
		}
	}

	public boolean hasData()
	{
		return !CollectionUtils.isEmpty(chartData);
	}

	/**
	 * {@link CountrySelectionHandler} is an interface that can be added to a {@link GeoChart} to get notified when the user selects/de-selects a
	 * {@link Country}.
	 */
	public interface CountrySelectionHandler
	{
		/**
		 * Called when the user selects a {@link Country}
		 *
		 * @param country The selected {@link Country}
		 */
		void onCountySelected(Country country);

		/**
		 * Called when the user de-selects a country
		 */
		void onSelectionCleared();
	}
}
