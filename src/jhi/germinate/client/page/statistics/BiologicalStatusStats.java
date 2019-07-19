/*
 *  Copyright 2019 Information and Computational Sciences,
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

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class BiologicalStatusStats extends Composite
{
	private FlowPanel panel = new FlowPanel();

	public BiologicalStatusStats()
	{
		initWidget(panel);
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		panel.add(new Heading(HeadingSize.H3, Text.LANG.dataStatisticsBiologicalStatusTitle()));
		panel.add(new Label(Text.LANG.dataStatisticsBiologicalStatusText()));

		CommonService.Inst.get().getBiologicalStatusStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				super.onFailureImpl(caught);
				BiologicalStatusStats.this.removeFromParent();
			}

			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				if (result.hasData())
				{
					String filePath = new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

					PlotlyBarChart.Config config = new PlotlyBarChart.Config()
							.setxAxisTitle(Text.LANG.dataStatisticsBiologicalStatusTitle())
							.setyAxisTitle(Text.LANG.generalCount())
							.setX("biologicalstatus")
							.setFilePath(filePath)
							.setDownloadFilename("biological-status")
							.setClickCallback(new Callback<String, Throwable>()
							{
								@Override
								public void onFailure(Throwable reason)
								{
								}

								@Override
								public void onSuccess(String result)
								{
									PartialSearchQuery query = new PartialSearchQuery();
									query.add(new SearchCondition(BiologicalStatus.SAMPSTAT, new Like(), result, String.class));

									/* Save it to the parameter store and change to the browse page */
									FilterMappingParameterStore.Inst.get().put(Parameter.tableFilterMapping, query);
									History.newItem(Page.ACCESSION_OVERVIEW.name());
								}
							});

					panel.add(new PlotlyBarChart(config));
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
				}
			}
		});
	}
}
