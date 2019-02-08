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

package jhi.germinate.client.page.accession;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * The AccessionOverviewPage is the main accession view. It shows a table with all accessions and also contains options for exporting of the accession
 * data.
 *
 * @author Sebastian Raubach
 */
public class AccessionOverviewPage extends GerminateComposite implements ParallaxBannerPage, HasHelp
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
		header.setText(Text.LANG.browseAccessionsTitle());
		panel.add(header);
		panel.add(new HTML(Text.LANG.browseAccessionsText()));

		/* Add the main accession table */
		final AccessionTable table = new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			{
				preventInitialDataLoad = true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
			{
				return AccessionService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}

			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				AccessionService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}
		};
		panel.add(table);

		/* Apply any filtering that another page requested before redirecting here */
		PartialSearchQuery query = FilterMappingParameterStore.Inst.get().get(Parameter.tableFilterMapping);

		if (query == null)
			query = new PartialSearchQuery();

		/* By default, filter down to accessions only */
		query.add(new SearchCondition(EntityType.NAME, new Equal(), EntityType.ACCESSION.getName(), String.class));
		FilterMappingParameterStore.Inst.get().remove(Parameter.tableFilterMapping);

		final PartialSearchQuery m = query;
		Scheduler.get().scheduleDeferred(() -> table.forceFilter(m, true));

		/* Set up callbacks for column names and groups */
		ParallelAsyncCallback<ServerResult<List<String>>> columnCallback = new ParallelAsyncCallback<ServerResult<List<String>>>()
		{
			@Override
			protected void start()
			{
				CommonService.Inst.get().getColumnsOfTable(Cookie.getRequestProperties(), GerminateDatabaseTable.germinatebase, this);
			}
		};
		ParallelAsyncCallback<ServerResult<List<Group>>> groupCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getForType(Cookie.getRequestProperties(), GerminateDatabaseTable.germinatebase, this);
			}
		};
		ParallelAsyncCallback<ServerResult<Boolean>> hasPedigreeCallback = new ParallelAsyncCallback<ServerResult<Boolean>>()
		{
			@Override
			protected void start()
			{
				PedigreeService.Inst.get().exists(Cookie.getRequestProperties(), null, this);
			}
		};

		/* Start them all in parallel */
		new ParallelParentAsyncCallback(columnCallback, groupCallback, hasPedigreeCallback)
		{
			@Override
			public void handleSuccess()
			{
				/* Get the result */
				int i = 0;
				ServerResult<List<String>> columns = getCallbackData(i++);
				ServerResult<List<Group>> groups = getCallbackData(i++);
				ServerResult<Boolean> hasPedigree = getCallbackData(i++);

				panel.add(new AccessionDataDownloadWidget(columns.getServerResult(), groups.getServerResult(), hasPedigree.getServerResult()));
			}

			@Override
			public void handleFailure(Exception reason)
			{
				GerminateEventBus.BUS.fireEvent(new ExceptionEvent(reason));
			}
		};
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxAccession();
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.accessionHelp());
	}
}
