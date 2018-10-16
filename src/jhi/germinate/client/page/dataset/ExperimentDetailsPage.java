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

package jhi.germinate.client.page.dataset;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * {@link ExperimentDetailsPage} shows information about a specific {@link Experiment} and the {@link Dataset}s it contains.
 *
 * @author Sebastian Raubach
 * @see Parameter#experimentId
 */
public class ExperimentDetailsPage extends Composite
{
	interface ExperimentDetailsPageUiBinder extends UiBinder<HTMLPanel, ExperimentDetailsPage>
	{
	}

	private static ExperimentDetailsPageUiBinder ourUiBinder = GWT.create(ExperimentDetailsPageUiBinder.class);

	@UiField
	PageHeader  header;
	@UiField
	Heading     noDataHeading;
	@UiField
	FlowPanel   resultPanel;
	@UiField
	HTML        html;
	@UiField
	SimplePanel datasetTablePanel;

	private Long       experimentId;
	private Experiment experiment;

	public ExperimentDetailsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		experimentId = LongParameterStore.Inst.get().get(Parameter.experimentId);

		if (experimentId == null)
		{
			resultPanel.removeFromParent();
		}
		else
		{
			DatasetService.Inst.get().getExperiment(Cookie.getRequestProperties(), experimentId, new DefaultAsyncCallback<ServerResult<Experiment>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Experiment> result)
				{
					experiment = result.getServerResult();
					header.setSubText(experiment.getName());
					setUpPage();
				}
			});
		}
	}

	public static void clickDownloadLink(ServerResult<String> result)
	{
		/* If there is a result */
		if (result != null && result.getServerResult() != null)
		{
			/* Get the filename from the result */
			String filename = result.getServerResult();

			/* Create a new invisible dummy link on the page */
			String path = new ServletConstants.Builder()
					.setUrl(GWT.getModuleBaseURL())
					.setPath(ServletConstants.SERVLET_FILES)
					.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
					.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
					.setParam(ServletConstants.PARAM_FILE_PATH, filename).build();

			JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), filename);

			/* Click it */
			JavaScript.invokeDownload(path);
		}
		else
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
		}
	}

	private void setUpPage()
	{
		html.setHTML(Text.LANG.experimentDetailsText());
		noDataHeading.removeFromParent();

		DatasetTable datasetTable = new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true, true, experiment.getType())
		{
			private PartialSearchQuery addToFilter(PartialSearchQuery filter)
			{
				if (filter == null)
					filter = new PartialSearchQuery();
				filter.add(new SearchCondition(Experiment.ID, new Equal(), experimentId, Long.class));

				if (filter.getAll().size() > 1)
					filter.addLogicalOperator(new And());

				return filter;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				filter = addToFilter(filter);

				return DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, null, pagination, callback);
			}
		};

		switch (experiment.getType())
		{
			case trials:
			case genotype:
				datasetTable.setShowDownload(true);
				break;
		}

		datasetTablePanel.add(datasetTable);
	}
}