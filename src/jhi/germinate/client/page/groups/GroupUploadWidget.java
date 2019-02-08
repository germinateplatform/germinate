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

package jhi.germinate.client.page.groups;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextArea;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class GroupUploadWidget extends Composite
{
	private static final int              UPDATE_INTERVAL = 500;
	private static final int              UPDATE_START    = 0;
	private              LoadingIndicator indicator;

	interface GroupUploadWidgetUiBinder extends UiBinder<HTMLPanel, GroupUploadWidget>
	{
	}

	private static GroupUploadWidgetUiBinder ourUiBinder = GWT.create(GroupUploadWidgetUiBinder.class);

	private static final String[] COLUMNS_ACCESSION = {Accession.ID, Accession.NAME, Accession.GENERAL_IDENTIFIER, Location.SITE_NAME, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Country.COUNTRY_NAME, Taxonomy.GENUS, Taxonomy.SPECIES, Taxonomy.SUBTAXA};
	private static final String[] COLUMNS_MARKER    = {Marker.ID, Marker.MARKER_NAME, Map.ID, Map.DESCRIPTION, MapFeatureType.DESCRIPTION, MapDefinition.CHROMOSOME, MapDefinition.DEFINITION_START};
	private static final String[] COLUMNS_LOCATION  = {Location.ID, Location.SITE_NAME, Location.STATE, Location.REGION, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Country.COUNTRY_NAME, LocationType.NAME};

	@UiField
	TabListItem   uploadTab;
	@UiField
	TabListItem   copyPasteTab;
	@UiField
	FileUpload    upload;
	@UiField
	StringListBox column;
	@UiField
	TextArea      copyPaste;
	@UiField
	Label         label;
	@UiField
	FormPanel     form;

	private GerminateDatabaseTable referenceTable;
	private Group                  group;

	public GroupUploadWidget(Group group)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		this.referenceTable = group.getType().getTargetTable();
		this.group = group;

		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		GerminateDatabaseTable type;
		switch (referenceTable)
		{
			case markers:
				type = GerminateDatabaseTable.markers;
				break;
			case locations:
				type = GerminateDatabaseTable.locations;
				break;
			case germinatebase:
			default:
				type = GerminateDatabaseTable.germinatebase;
				break;
		}

		String limit = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(GerminateSettingsHolder.get().uploadSizeLimitMB.getValue());
		label.setText(Text.LANG.uploadFileSizeLimit(limit));

		String[] items = getItems(type);

		column.setValue(items[0], false);
		column.setAcceptableValues(items);
	}

	private String[] getItems(GerminateDatabaseTable type)
	{
		switch (type)
		{
			case markers:
				return COLUMNS_MARKER;
			case locations:
				return COLUMNS_LOCATION;
			case germinatebase:
			default:
				return COLUMNS_ACCESSION;
		}
	}

	public void onUploadButtonClicked()
	{
		if (uploadTab.isActive())
		{
			String url = new ServletConstants.Builder()
					.setUrl(GWT.getModuleBaseURL())
					.setPath(ServletConstants.SERVLET_UPLOAD)
					.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
					.build();

			form.setAction(url);
			form.submit();
			form.setAction("");

			indicator = new LoadingIndicator(Text.LANG.notificationLongRunning());
			indicator.show();
			/* Create the timer that will poll the upload progress */
			Timer timer = new Timer()
			{
				@Override
				public void run()
				{
					final Timer that = this;
					/* Get the progress of the upload */
					UploadProgressService.Inst.get().getProgress(Cookie.getRequestProperties(), new DefaultAsyncCallback<Float>()
					{
						@Override
						public void onFailureImpl(Throwable caught)
						{
							Notification.notify(Notification.Type.ERROR, caught.getLocalizedMessage());
							that.cancel();
						}

						@Override
						public void onSuccessImpl(Float result)
						{
							if (result == null)
							{
								Notification.notify(Notification.Type.ERROR, Text.LANG.notificationUploadNoProgressInformation());
								indicator.hide();
								that.cancel();
							}
							else if (result >= 100f)
							{
								indicator.hide();
								that.cancel();
							}
							else
							{
								indicator.setProgress(result);
							}
						}
					});
				}
			};

			/* Schedule it once after UPDATE_START milliseconds and then repeatedly every UPDATE_INTERVAL milliseconds */
			timer.schedule(UPDATE_START);
			timer.scheduleRepeating(UPDATE_INTERVAL);
		}
		else if (copyPasteTab.isActive())
		{
			GroupService.Inst.get().addItems(Cookie.getRequestProperties(), copyPaste.getValue().split("\\n"), referenceTable, column.getSelection(),
					group.getId(), new DefaultAsyncCallback<ServerResult<Tuple.Pair<Integer, Integer>>>(true)
					{
						@Override
						public void onSuccessImpl(ServerResult<Tuple.Pair<Integer, Integer>> result)
						{
							Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupItemsAddedIgnored(result.getServerResult().getFirst(), result.getServerResult().getSecond()));

							GerminateEventBus.BUS.fireEvent(new GroupMemberChangeEvent());
						}
					});
		}
	}

	@UiHandler("form")
	void onSubmitComplete(FormPanel.SubmitCompleteEvent event)
	{
		if (UploadProgressService.FILESIZE_LIMIT_EXCEEDED.equals(event.getResults()))
		{
			indicator.hide();
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationGroupsUploadFileSizeExceeded());
		}

		GroupService.Inst.get().addItems(Cookie.getRequestProperties(), event.getResults(), referenceTable, column.getSelection(), group.getId(),
				new DefaultAsyncCallback<ServerResult<Tuple.Pair<Integer, Integer>>>(true)
				{
					@Override
					public void onSuccessImpl(ServerResult<Tuple.Pair<Integer, Integer>> result)
					{
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationGroupItemsAddedIgnored(result.getServerResult().getFirst(), result.getServerResult().getSecond()));

						GerminateEventBus.BUS.fireEvent(new GroupMemberChangeEvent());
					}
				});
	}
}