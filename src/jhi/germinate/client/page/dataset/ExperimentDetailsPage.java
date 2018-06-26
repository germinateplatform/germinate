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
import jhi.germinate.shared.exception.*;
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

		DatasetTable datasetTable = new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true, true)
		{
			private PartialSearchQuery addToFilter(PartialSearchQuery filter)
			{
				if (filter == null)
					filter = new PartialSearchQuery();
				filter.add(new SearchCondition(Experiment.ID, new Like(), experimentId, Long.class));

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
				datasetTable.setShowDownload(true, new SimpleCallback<Dataset>()
				{
					@Override
					public void onSuccess(Dataset result)
					{
						/* Get the id of the selected dataset */
						List<Long> ids = new ArrayList<>();
						ids.add(result.getId());

						/* Start the export process */
						PhenotypeService.Inst.get().export(Cookie.getRequestProperties(), ids, null, null, false, new DefaultAsyncCallback<ServerResult<String>>(true)
						{
							@Override
							protected void onSuccessImpl(ServerResult<String> result)
							{
								clickDownloadLink(result);
							}
						});
					}
				});
				break;
			case genotype:
				datasetTable.setShowDownload(true, new SimpleCallback<Dataset>()
				{
					@Override
					public void onSuccess(Dataset result)
					{
						if (!StringUtils.isEmpty(result.getSourceFile()))
						{
							/* If we're dealing with an hdf5 file, convert it to tab-delimited */
							if (result.getSourceFile().endsWith(".hdf5"))
							{
								/* Start the export process */
								GenotypeService.Inst.get().convertHdf5ToText(Cookie.getRequestProperties(), result.getId(), new DefaultAsyncCallback<ServerResult<String>>(true)
								{
									@Override
									protected void onFailureImpl(Throwable caught)
									{
										if (caught instanceof InvalidArgumentException)
											Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInsufficientPermissions());
										else
											super.onFailureImpl(caught);
									}

									@Override
									protected void onSuccessImpl(ServerResult<String> result)
									{
										ExperimentDetailsPage.clickDownloadLink(result);
									}
								});
							}
							else
							{
								/* Else just download the file */
								String href = new ServletConstants.Builder()
										.setUrl(GWT.getModuleBaseURL())
										.setPath(ServletConstants.SERVLET_FILES)
										.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
										.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
										.setParam(ServletConstants.PARAM_FILE_LOCATION, FileLocation.data.name())
										.setParam(ServletConstants.PARAM_FILE_PATH, (ReferenceFolder.genotype.name() + "/") + result.getSourceFile())
										.build();

								JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), result.getSourceFile());

								/* Click it */
								JavaScript.invokeDownload(href);
							}
						}
					}
				});
				break;
		}

		datasetTablePanel.add(datasetTable);
	}
}