/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.client.page.trial;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class TrialSitesPage extends Composite
{
	private static CompoundDetailsPageUiBinder ourUiBinder = GWT.create(CompoundDetailsPageUiBinder.class);
	@UiField
	PageHeader header;
	@UiField
	Heading    noDataHeader;
	@UiField
	FlowPanel  detailsPanel;
	private Location location;

	public TrialSitesPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long id = LongParameterStore.Inst.get().get(Parameter.trialsiteId);

		if (id != null)
		{
			List<String> ids = new ArrayList<>();
			ids.add(Long.toString(id));
			LocationService.Inst.get().getByIds(Cookie.getRequestProperties(), Pagination.getDefault(), ids, new DefaultAsyncCallback<ServerResult<List<Location>>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<List<Location>> result)
				{
					if (result.hasData())
					{
						location = result.getServerResult().get(0);

						update();
					}
					else
					{
						noDataHeader.setVisible(true);
					}
				}
			});
		}
		else
		{
			noDataHeader.setVisible(true);
		}
	}

	private void update()
	{
		header.setSubText(location.getName());
		detailsPanel.setVisible(true);
		detailsPanel.clear();

		PhenotypeDataTable table = new PhenotypeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			{
				preventInitialDataLoad = true;
			}

			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				PhenotypeService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<PhenotypeData>>> callback)
			{
				return PhenotypeService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};

		detailsPanel.add(table);

		Scheduler.get().scheduleDeferred(() -> {
			PartialSearchQuery filter = new PartialSearchQuery();
			filter.add(new SearchCondition(Location.SITE_NAME, new Equal(), location.getName(), String.class));
			table.forceFilter(filter, true);
		});
	}

	interface CompoundDetailsPageUiBinder extends UiBinder<HTMLPanel, TrialSitesPage>
	{
	}
}