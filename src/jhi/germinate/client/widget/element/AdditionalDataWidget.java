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
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;

import java.util.*;
import java.util.stream.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class AdditionalDataWidget extends Composite
{

	private static AdditionalDataWidgetUiBinder ourUiBinder = GWT.create(AdditionalDataWidgetUiBinder.class);
	@UiField
	HTML        additionalDataText;
	@UiField
	SimplePanel additionalDataTablePanel;
	private DatasetTable additionalDataTable;
	private UpdateCallback       updateCallback;
	private List<ExperimentType> experimentTypes = new ArrayList<>();

	public AdditionalDataWidget()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		additionalDataText.setHTML(Text.LANG.searchAdditionalDatasetsText());

		addAdditionalDatasetsTable();
	}

	public AdditionalDataWidget setExperimentTypes(List<ExperimentType> experimentTypes)
	{
		this.experimentTypes = experimentTypes;
		return this;
	}

	public AdditionalDataWidget setUpdateCallback(UpdateCallback updateCallback)
	{
		this.updateCallback = updateCallback;
		return this;
	}

	private void addAdditionalDatasetsTable()
	{
		additionalDataTable = new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true, false, null)
		{
			{
				preventInitialDataLoad = true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getWithUnacceptedLicense(Cookie.getRequestProperties(), experimentTypes, pagination, new AsyncCallback<PaginatedServerResult<List<Dataset>>>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						AdditionalDataWidget.this.setVisible(false);
						if (updateCallback != null)
							updateCallback.onVisibilityChanged(false);

						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(PaginatedServerResult<List<Dataset>> result)
					{
						AdditionalDataWidget.this.setVisible(!CollectionUtils.isEmpty(result.getServerResult()));
						if (updateCallback != null)
							updateCallback.onVisibilityChanged(!CollectionUtils.isEmpty(result.getServerResult()));

						callback.onSuccess(result);
					}
				});
			}
		};

		additionalDataTablePanel.add(additionalDataTable);

		Button updateButton = new Button(Text.LANG.generalUpdate());
		updateButton.addStyleName(Style.combine(Style.MDI, Style.MDI_LG, Style.MDI_REFRESH));
		updateButton.addClickHandler(event -> {
			/* Get the selected items */
			Set<Dataset> selectedItems = additionalDataTable.getSelection();

			Set<License> licensesToAgreeTo = selectedItems.stream()
														  .filter(d -> d.getLicense() != null)
														  .map(d -> {
															  License license = d.getLicense();
															  license.setExtra(Dataset.ID, d.getId());
															  return license;
														  })
														  .collect(Collectors.toCollection(HashSet::new));

			if (!CollectionUtils.isEmpty(licensesToAgreeTo))
			{
				DatasetWidget.showLicenseAcceptWizard(additionalDataTable, licensesToAgreeTo, new DefaultAsyncCallback<List<Dataset>>()
				{
					@Override
					protected void onSuccessImpl(List<Dataset> result)
					{
						// Refresh the table
						if (updateCallback != null)
							updateCallback.onDataUpdate();
						additionalDataTable.refreshTable();

						JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DATASET, "additionalDatasets", "show", selectedItems.size());
					}
				});
			}
		});
		additionalDataTable.addExtraContent(updateButton);
	}

	public void update()
	{
		additionalDataTable.refreshTable();
	}

	interface AdditionalDataWidgetUiBinder extends UiBinder<FlowPanel, AdditionalDataWidget>
	{
	}

	public interface UpdateCallback
	{
		void onVisibilityChanged(boolean visible);

		void onDataUpdate();
	}
}
