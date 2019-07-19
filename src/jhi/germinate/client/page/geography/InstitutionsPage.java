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
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

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

	private PlotlyChoroplethChart chart;
	private InstitutionTable      institutionsTable;

	public InstitutionsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		final Long institutionId = LongParameterStore.Inst.get().get(Parameter.institutionId);

		institutionsTable = new InstitutionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			{
				preventInitialDataLoad = true;
			}

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

		/* Apply any filtering that another page requested before redirecting here */
		PartialSearchQuery query = new PartialSearchQuery();

		if (institutionId != null)
			query.add(new SearchCondition(Institution.ID, new Equal(), Long.toString(institutionId), String.class));

		final PartialSearchQuery m = query;
		Scheduler.get().scheduleDeferred(() -> institutionsTable.forceFilter(m, true));

		LocationService.Inst.get().getInstitutionsByCountry(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<String>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				String filePath = new ServletConstants.Builder()
						.setUrl(GWT.getModuleBaseURL())
						.setPath(ServletConstants.SERVLET_FILES)
						.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
						.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
						.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult())
						.build();
				chart = new PlotlyChoroplethChart(filePath, new Callback<Country, Throwable>()
				{
					@Override
					public void onFailure(Throwable reason)
					{
					}

					@Override
					public void onSuccess(Country country)
					{
						PartialSearchQuery query = new PartialSearchQuery();
						query.add(new SearchCondition(Country.COUNTRY_NAME, new Equal(), country.getName(), String.class));
						institutionsTable.forceFilter(query, true);

						// TODO: Clear
						/* If something is de-selected in the chart, clear the table filtering (if available) */
						//						if (institutionsTable.isFiltered())
						//							institutionsTable.clearFilter();
					}
				});

				map.add(chart);
			}
		});
	}
}