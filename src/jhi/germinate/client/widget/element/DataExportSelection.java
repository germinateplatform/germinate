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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class DataExportSelection<T extends DatabaseObject> extends Composite
{
	interface DataExportSelectionUiBinder extends UiBinder<HTMLPanel, DataExportSelection>
	{
	}

	private static DataExportSelectionUiBinder ourUiBinder = GWT.create(DataExportSelectionUiBinder.class);

	@UiField
	Heading               heading;
	@UiField
	HTML                  phenotypeHtml;
	@UiField(provided = true)
	GerminateValueListBox objectBox;

	@UiField
	HTML         groupHtml;
	@UiField
	GroupListBox groupBox;
	@UiField
	Button       downloadButton;

	private List<Long>     datasetIds;
	private ExperimentType type;

	public DataExportSelection(ExperimentType type)
	{
		this.type = type;

		switch (type)
		{
			case compound:
				objectBox = new CompoundListBox();
				break;
			case trials:
			default:
				objectBox = new PhenotypeListBox();
				break;
		}

		initWidget(ourUiBinder.createAndBindUi(this));
	}

	public void update(List<Long> datasetIds, List<T> objects, List<Group> groups)
	{
		this.datasetIds = datasetIds;

		switch (type)
		{
			case compound:
				heading.setText(Text.LANG.compoundExportHeadingCompounds());
				break;
			case trials:
			default:
				heading.setText(Text.LANG.phenotypeExportHeadingPhenotypes());
				break;
		}

		phenotypeHtml.setHTML(Text.LANG.phenotypeExportSubtitlePhenotypes());
		if (objects != null)
		{
			objectBox.setValue(objects.get(0), true);
			objectBox.setAcceptableValues(objects);
		}

		groupHtml.setHTML(Text.LANG.phenotypeExportSubtitleAccessionGroups());
		groupBox.setAcceptableValues(groups);

		downloadButton.setEnabled(true);
	}

	@UiHandler("downloadButton")
	void onDownloadButtonClicked(ClickEvent e)
	{
		/* Get the user selections */
		final List<Long> groupIds = DatabaseObject.getIds(groupBox.getSelections());
		final List<Long> objectIds = DatabaseObject.getIds(objectBox.getSelections());

		if (groupIds == null || objectIds == null)
		{
			Notification.notify(Notification.Type.WARNING, Text.LANG.notificationGenotypeExportSelectAtLeastOne());
			return;
		}

		PlotlyMatrixChart.getData(type, datasetIds, groupIds, objectIds, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			public void onSuccessImpl(ServerResult<String> result)
			{
				// Add a download link if the result file creation was successful
				if (result.hasData())
				{
					// Click it
					JavaScript.invokeGerminateDownload(result.getServerResult());
				}
				else
				{
					Notification.notify(Notification.Type.INFO, Text.LANG.notificationPhenotypeExportNoData());
				}
			}
		});
	}
}