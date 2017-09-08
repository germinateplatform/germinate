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

package jhi.germinate.client.page.geography;

import com.google.gwt.core.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class LocationTreemapPage extends Composite implements HasLibraries
{
	interface LocationTreemapPageUiBinder extends UiBinder<HTMLPanel, LocationTreemapPage>
	{
	}

	private static LocationTreemapPageUiBinder ourUiBinder = GWT.create(LocationTreemapPageUiBinder.class);

	@UiField
	HTML                html;
	@UiField
	LocationTypeListBox locationTypeBox;
	@UiField
	SimplePanel         chartPanel;

	private LocationTreemapChart chart;

	public LocationTreemapPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		chart = new LocationTreemapChart();
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		html.setHTML(Text.LANG.collsiteTreemapText());

		chartPanel.add(chart);

		List<LocationType> list = new ArrayList<>(Arrays.asList(LocationType.values()));
		list.remove(LocationType.all);

		locationTypeBox.setValue(LocationType.collectingsites, true);
		locationTypeBox.setAcceptableValues(list);
	}

	@UiHandler("locationTypeBox")
	void onLocationTypeChanged(ValueChangeEvent<List<LocationType>> event)
	{
		LocationType type = locationTypeBox.getSelection();

		LocationService.Inst.get().getJsonForType(Cookie.getRequestProperties(), type, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				chart.clear();
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
			}

			@Override
			public void onSuccessImpl(ServerResult<String> result)
			{
				if (!StringUtils.isEmpty(result.getServerResult()))
				{
					/* Construct the path to the json file */
					chart.setLocationType(type);
					chart.setFilePath(new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult())
							.build());
				}
				else
				{
					onFailureImpl(null);
				}
			}
		});
	}

	@Override
	public Library[] getLibraries()
	{
		return chart.getLibraryList();
	}
}