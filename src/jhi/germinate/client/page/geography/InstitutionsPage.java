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

package jhi.germinate.client.page.geography;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;
import java.util.Map;

import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class InstitutionsPage extends Composite
{
	interface InstitutionsPageUiBinder extends UiBinder<HTMLPanel, InstitutionsPage>
	{
	}

	private static InstitutionsPageUiBinder ourUiBinder = GWT.create(InstitutionsPageUiBinder.class);

	@UiField
	SimplePanel table;
	@UiField
	SimplePanel map;

	private GeoChart         chart;
	private InstitutionTable institutionsTable;

	public InstitutionsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		institutionsTable = new InstitutionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Institution>>> callback)
			{
				return LocationService.Inst.get().getInstitutionsForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};
		table.add(institutionsTable);

		LocationService.Inst.get().getInstitutionsByCountry(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<List<Country>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<Country>> result)
			{
				/* When a country is selected by the user, force filter the table with the country name *//* If something is de-selected in the chart, clear the table filtering (if available) */
				chart = new GeoChart(result.getServerResult(), new GeoChart.CountrySelectionHandler()
				{
					@Override
					public void onCountySelected(Country country)
					{
						/* When a country is selected by the user, force filter the table with the country name */
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Country.COUNTRY_NAME, country.getName());
						try
						{
							institutionsTable.forceFilter(mapping, true);
						}
						catch (InvalidArgumentException e)
						{
						}
					}

					@Override
					public void onSelectionCleared()
					{
						/* If something is de-selected in the chart, clear the table filtering (if available) */
						if (institutionsTable.isFilterVisible())
							institutionsTable.toggleFilter();
					}
				});
				map.add(chart);
			}
		});
	}
}