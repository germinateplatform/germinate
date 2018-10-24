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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetMetadataDownload<T extends DatabaseObject> extends Composite
{
	private static DataExportSelectionUiBinder ourUiBinder = GWT.create(DataExportSelectionUiBinder.class);
	@UiField
	FlowPanel        panel;
	@UiField
	AttributeListBox attributesListBox;
	private List<Long> selectedDatasets = new ArrayList<>();

	public DatasetMetadataDownload(List<Dataset> selectedDatasets)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		update(selectedDatasets);
	}

	public void update(List<Dataset> selectedDatasets)
	{
		if (!CollectionUtils.isEmpty(selectedDatasets))
		{
			this.selectedDatasets = DatabaseObject.getIds(selectedDatasets);

			PartialSearchQuery filter = new PartialSearchQuery();
			filter.add(new SearchCondition(Attribute.TARGET_TABLE, new Equal(), GerminateDatabaseTable.datasets, String.class));
			filter.addLogicalOperator(new And());
			filter.add(new SearchCondition(AttributeData.FOREIGN_ID, new InSet(), this.selectedDatasets, String.class));
			AttributeService.Inst.get().getForFilter(Cookie.getRequestProperties(), Pagination.getDefault(), filter, new DefaultAsyncCallback<PaginatedServerResult<List<Attribute>>>()
			{
				@Override
				protected void onSuccessImpl(PaginatedServerResult<List<Attribute>> result)
				{
					if (result != null && !CollectionUtils.isEmpty(result.getServerResult()))
						attributesListBox.setAcceptableValues(result.getServerResult());
					else
						panel.setVisible(false);
				}
			});
		}
	}

	@UiHandler("download")
	void onDownloadClicked(ClickEvent event)
	{
		List<Attribute> selectedAttributes = attributesListBox.getSelections();

		if (CollectionUtils.isEmpty(selectedAttributes))
		{
			Notification.notify(Notification.Type.WARNING, Text.LANG.notificationAttributeSelectAtLeastOne());
		}
		else
		{
			List<Long> attributeIds = DatabaseObject.getIds(selectedAttributes);

			DatasetService.Inst.get().exportAttributes(Cookie.getRequestProperties(), selectedDatasets, attributeIds, new DefaultAsyncCallback<ServerResult<String>>(true)
			{
				@Override
				protected void onSuccessImpl(ServerResult<String> result)
				{
					if (!StringUtils.isEmpty(result.getServerResult()))
					{
						JavaScript.invokeGerminateDownload(result.getServerResult());
					}
				}
			});
		}
	}

	interface DataExportSelectionUiBinder extends UiBinder<FlowPanel, DatasetMetadataDownload>
	{
	}
}