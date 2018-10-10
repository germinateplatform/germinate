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
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.Map;
import java.util.stream.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.dataset.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * The {@link DatasetWidget} contains a list of available datasets for the current user and the given {@link ExperimentType}.
 *
 * @author Sebastian Raubach
 */
public class DatasetWidget extends GerminateComposite implements HasHelp, ParallaxBannerPage
{
	private DatasetCallback datasetCallback = null;
	private ExperimentType  experimentType  = null;
	private boolean         singleSelection = false;

	private FlowPanel    tablePanel;
	private SimplePanel  mapPanel;
	private FlowPanel    buttonPanel;
	private DatasetTable table;
	private String       headerText;
	private SafeHtml     text                   = null;
	private List<Long>   urlParameterDatasetIds = null;

	private boolean internal             = true;
	private boolean showLoadingIndicator = false;
	private boolean linkToExportPage     = false;
	private boolean showDownload         = false;
	private boolean showMap              = false;

	private ReferenceFolder         referenceFolder  = null;
	private SimpleCallback<Dataset> downloadCallback = null;

	private DatasetTable.SelectionMode selectionMode                 = null;
	private Button                     continueButton;
	private boolean                    alreadyAskedUserAboutLicenses = false;

	/**
	 * Creates a new dataset table. This table will either show the internal or external datasets based on the selection ({@link
	 * #setInternal(boolean)}).
	 */
	public DatasetWidget()
	{
	}

	/**
	 * Creates a new dataset table for the given {@link ExperimentType}
	 *
	 * @param callback        The callback that is called when the user presses the "Continue" button
	 * @param experimentType  The experiment type
	 * @param singleSelection Should the user only be able to only select single datasets?
	 */
	public DatasetWidget(DatasetCallback callback, ExperimentType experimentType, boolean singleSelection)
	{
		this.datasetCallback = callback;
		this.experimentType = experimentType;
		this.singleSelection = singleSelection;
	}

	/**
	 * Initializes the table containing the datasets defined in the database
	 */
	private void requestData()
	{
		tablePanel = new FlowPanel();
		mapPanel = new SimplePanel();
		mapPanel.addStyleName(Style.LAYOUT_BUTTON_MARGIN);
		buttonPanel = new FlowPanel();

		panel.add(tablePanel);
		panel.add(buttonPanel);
		panel.add(mapPanel);

		if (!StringUtils.isEmpty(headerText))
		{
			PageHeader header = new PageHeader();
			header.setText(headerText);
			tablePanel.add(header);
		}

		if (text != null)
		{
			tablePanel.add(new HTML(text));
		}

		if (showMap)
		{
			PartialSearchQuery q = new PartialSearchQuery(new SearchCondition(Dataset.IS_EXTERNAL, new Equal(), 0, Integer.class));
			DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), q, experimentType, Pagination.getDefault(), new DefaultAsyncCallback<PaginatedServerResult<List<Dataset>>>(showLoadingIndicator)
			{
				@Override
				protected void onFailureImpl(Throwable caught)
				{
					super.onFailureImpl(caught);
				}

				@Override
				protected void onSuccessImpl(PaginatedServerResult<List<Dataset>> result)
				{
					if (!CollectionUtils.isEmpty(result.getServerResult()))
					{
						createMap(result.getServerResult());
					}
				}
			});
		}

		createTable();
		addContinueButton();
	}

	/**
	 * Creates the map overview
	 *
	 * @param datasets The {@link List} of {@link Dataset}s
	 */
	private void createMap(List<Dataset> datasets)
	{
		final Map<Location, List<Dataset>> mapping = new HashMap<>();

		for (Dataset dataset : datasets)
		{
			Location location = dataset.getLocation();

			if (location != null)
			{
				List<Dataset> mappedDatasets = mapping.get(location);

				if (mappedDatasets == null)
					mappedDatasets = new ArrayList<>();

				mappedDatasets.add(dataset);

				mapping.put(location, mappedDatasets);
			}
		}

		if (mapping.size() < 1)
			return;

		new IterativeParentCallback(Library.LEAFLET_COMPLETE.getCallback())
		{
			@Override
			public void handleSuccess()
			{
				new LeafletUtils.DatasetMarkerCreator(mapPanel, mapping, null);
			}

			@Override
			public void handleFailure(Exception reason)
			{
			}
		};
	}

	public static void showLicenseAcceptWizard(DatasetTable table, Set<License> licenses, DefaultAsyncCallback<List<Dataset>> callback)
	{
		new LicenseWizard(licenses)
		{
			@Override
			protected boolean onFinished()
			{
				List<LicenseLog> logs = getLicenseLogs();
				List<License> licenses = getAcceptedLicenses();

				// Get all the currently selected datasets
				Set<Dataset> datasets = table.getSelection();
				// Which ones should still be selected?
				List<Dataset> toSelect = new ArrayList<>();

				// For each of them, check if its license has been accepted
				for (Dataset d : datasets)
				{
					// If the dataset has a license
					if (d.getLicense() != null)
					{
						// License was already accepted before (there is a log with a valid id)
						if (d.getLicense().getLicenseLog() != null && d.getLicense().getLicenseLog().getId() > 0)
							toSelect.add(d);
							// The user just accepted this license
						else if (licenses.contains(d.getLicense()))
							toSelect.add(d);
					}
					else
					{
						// Else, just add it. It's a freebie!
						toSelect.add(d);
					}
				}

				// Then set the selection of the table
				table.setSelection(toSelect);
				table.redraw();

				// Finally, if there are new log entries (new licenses that the user accepted), then submit them
				if (!CollectionUtils.isEmpty(logs))
				{
					DatasetService.Inst.get().updateLicenseLogs(Cookie.getRequestProperties(), logs, new AsyncCallback<ServerResult<Boolean>>()
					{
						@Override
						public void onFailure(Throwable caught)
						{
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(ServerResult<Boolean> result)
						{
							callback.onSuccess(toSelect);
						}
					});
				}

				return true;
			}
		}.open();
	}

	/**
	 * Creates the actual table from the {@link List} of {@link Dataset}s
	 */
	private void createTable()
	{
		/* Set an initial value */
		selectionMode = DatabaseObjectPaginationTable.SelectionMode.NONE;

		if (datasetCallback != null)
		{
			/* If the continue button is available -> multi */
			if (datasetCallback.isContinueButtonAvailable())
				selectionMode = DatasetTable.SelectionMode.MULTI;
				/* If not -> none */
			else
				selectionMode = DatasetTable.SelectionMode.NONE;
		}

		/* If it wasn't none before, we can restrict it to single if required */
		if (selectionMode != DatabaseObjectPaginationTable.SelectionMode.NONE && singleSelection)
			selectionMode = DatasetTable.SelectionMode.SINGLE;


		if (experimentType != null)
		{
			switch (experimentType)
			{
				case allelefreq:
					urlParameterDatasetIds = LongListParameterStore.Inst.get().get(Parameter.allelefreqDatasetIds);
					LongListParameterStore.Inst.get().remove(Parameter.allelefreqDatasetIds);
					break;
				case climate:
					urlParameterDatasetIds = LongListParameterStore.Inst.get().get(Parameter.climateDatasetIds);
					LongListParameterStore.Inst.get().remove(Parameter.climateDatasetIds);
					break;
				case compound:
					urlParameterDatasetIds = LongListParameterStore.Inst.get().get(Parameter.compoundDatasetIds);
					LongListParameterStore.Inst.get().remove(Parameter.compoundDatasetIds);
					break;
				case genotype:
					urlParameterDatasetIds = LongListParameterStore.Inst.get().get(Parameter.genotypeDatasetIds);
					LongListParameterStore.Inst.get().remove(Parameter.genotypeDatasetIds);
					break;
				case trials:
					urlParameterDatasetIds = LongListParameterStore.Inst.get().get(Parameter.trialsDatasetIds);
					LongListParameterStore.Inst.get().remove(Parameter.trialsDatasetIds);
					break;
			}
		}

		table = new DatasetTable(selectionMode, true, linkToExportPage, experimentType)
		{
			{
				preventInitialDataLoad = !CollectionUtils.isEmpty(urlParameterDatasetIds);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			private PartialSearchQuery addToFilter(PartialSearchQuery filter)
			{
				if (filter == null)
					filter = new PartialSearchQuery();
				filter.add(new SearchCondition(Dataset.IS_EXTERNAL, new Equal(), internal ? 0 : 1, Integer.class));

				if (filter.getAll().size() > 1)
					filter.addLogicalOperator(new And());

				return filter;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				filter = addToFilter(filter);

				return DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, experimentType, pagination, callback);
			}
		};

		// Pre-filter the table with the dataset ids that have been passed to Germinate in the URL
		if (!CollectionUtils.isEmpty(urlParameterDatasetIds))
		{
			PartialSearchQuery query = new PartialSearchQuery();
			for (Long id : urlParameterDatasetIds)
				query.add(new SearchCondition(Dataset.ID, new Equal(), Long.toString(id), String.class));

			Scheduler.get().scheduleDeferred(() -> table.forceFilter(query, false));
		}

		if (showDownload)
		{
			if (downloadCallback != null)
				table.setShowDownload(showDownload, downloadCallback);
			else if (referenceFolder != null)
				table.setShowDownload(showDownload, referenceFolder);
		}

		tablePanel.add(table);
	}

	private void addContinueButton()
	{
		/* If we have checkboxes, add the "continue" button */
		if (selectionMode != DatasetTable.SelectionMode.NONE && datasetCallback != null && datasetCallback.isContinueButtonAvailable())
		{
			/* Handle "continue" click events */
			/* Get the selected items *//* Get their ids *//* Save the ids to the parameter store *//* Notify the callback */
			continueButton = new Button(Text.LANG.generalContinue(), event ->
			{
				/* Get the selected items */
				Set<Dataset> selectedItems = table.getSelection();

				Set<License> licensesToAgreeTo = selectedItems.stream()
															  .filter(d -> d.getLicense() != null && (!ModuleCore.getUseAuthentication() || !d.hasLicenseBeenAccepted(ModuleCore.getUserAuth())))
															  .map(d -> {
																  License license = d.getLicense();
																  license.setExtra(Dataset.ID, d.getId());
																  return license;
															  })
															  .collect(Collectors.toCollection(HashSet::new));

				if (!CollectionUtils.isEmpty(licensesToAgreeTo) && !alreadyAskedUserAboutLicenses)
				{
					showLicenseAcceptWizard(table, licensesToAgreeTo, new DefaultAsyncCallback<List<Dataset>>()
					{
						@Override
						protected void onSuccessImpl(List<Dataset> result)
						{
							// Refresh the table
							table.refreshTable();
							// Then "hit" continue
							continueWithDatasets(result);
						}
					});
				}
				else
				{
					if (!CollectionUtils.isEmpty(selectedItems))
						continueWithDatasets(new ArrayList<>(selectedItems));
					else
						Notification.notify(Notification.Type.INFO, Text.LANG.notificationDatasetsSelectAtLeastOne());
				}
			});
			continueButton.addStyleName(Style.mdiLg(Style.MDI_ARROW_RIGHT_BOLD));
			continueButton.setType(ButtonType.PRIMARY);
			continueButton.addStyleName(Style.LAYOUT_BUTTON_MARGIN);

			buttonPanel.add(continueButton);
		}
	}

	private void continueWithDatasets(List<Dataset> selectedItems)
	{
		if (!CollectionUtils.isEmpty(selectedItems))
		{
			/* Save the ids to the parameter store */
			switch (experimentType)
			{
				case allelefreq:
					DatasetListParameterStore.Inst.get().put(Parameter.allelefreqDatasets, selectedItems);
					break;
				case climate:
					DatasetListParameterStore.Inst.get().put(Parameter.climateDatasets, selectedItems);
					break;
				case compound:
					DatasetListParameterStore.Inst.get().put(Parameter.compoundDatasets, selectedItems);
					break;
				case genotype:
					DatasetListParameterStore.Inst.get().put(Parameter.genotypeDatasets, selectedItems);
					break;
				case trials:
					DatasetListParameterStore.Inst.get().put(Parameter.trialsDatasets, selectedItems);
					break;
			}

			GerminateEventBus.BUS.fireEvent(new DatasetSelectionEvent(selectedItems));

			/* Notify the callback */
			datasetCallback.onContinuePressed();
		}
		else
		{
			Notification.notify(Notification.Type.WARNING, Text.LANG.notificationDatasetsSelectAtLeastOne());
		}
	}

	/**
	 * Gets the html cell value
	 *
	 * @param dataset The {@link Dataset}
	 * @param text    The text to display
	 * @return The generated {@link SafeHtml}
	 */
	public static SafeHtml getValue(Dataset dataset, String text)
	{
		if (StringUtils.isEmpty(text))
		{
			return SimpleHtmlTemplate.INSTANCE.text("");
		}
		else if (StringUtils.isEmpty(dataset.getHyperlink()))
		{
			return SimpleHtmlTemplate.INSTANCE.text(text);
		}
		else
		{
			SafeUri href = UriUtils.fromString(dataset.getHyperlink());
			return SimpleHtmlTemplate.INSTANCE.anchorNewTab(href, text);
		}
	}

	public void setTitle(String title)
	{
		this.headerText = title;
	}

	/**
	 * Sets the text above the table
	 *
	 * @param headerText The text to display above the dataset table)
	 */
	public void setText(SafeHtml headerText)
	{
		this.text = headerText;
	}

	public void setText(String headerText)
	{
		this.setText(SafeHtmlUtils.fromTrustedString(new Heading(HeadingSize.H3, headerText).toString()));
	}

	public boolean isInternal()
	{
		return internal;
	}

	/**
	 * Should the table show internal or external datasets?
	 *
	 * @param internal Set to <code>true</code> to show internal datasets, to <code>false</code> to show external datasets
	 */
	public void setInternal(boolean internal)
	{
		this.internal = internal;
	}

	public void setLinkToExportPage(boolean linkToExportPage)
	{
		this.linkToExportPage = linkToExportPage;
	}

	public void setShowLoadingIndicator(boolean showLoadingIndicator)
	{
		this.showLoadingIndicator = showLoadingIndicator;
	}

	public void setShowDownload(boolean showDownload, SimpleCallback<Dataset> downloadCallback)
	{
		this.showDownload = showDownload;
		this.downloadCallback = downloadCallback;

		if (table != null)
			table.setShowDownload(showDownload, downloadCallback);
	}

	public void setShowMap(boolean showMap)
	{
		this.showMap = showMap;
	}

	@Override
	public Library[] getLibraries()
	{
		return null;
	}


	@Override
	protected void setUpContent()
	{
		requestData();
	}

	/**
	 * A simple callback used for the dataset page
	 */
	public interface DatasetCallback
	{
		/**
		 * Checks if the continue button should be available at all
		 *
		 * @return <code>true</code> if it's available, <code>false</code> otherwise
		 */
		boolean isContinueButtonAvailable();

		/**
		 * This method is called when the user clicks on the "Continue" button on the dataset page
		 */
		void onContinuePressed();
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.datasetsHelp());
	}

	@Override
	public String getParallaxStyle()
	{
		return null;
	}
}
