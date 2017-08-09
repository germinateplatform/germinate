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

package jhi.germinate.client.widget.element;

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

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * The {@link DatasetWidget} contains a list of available datasets for the current user and the given {@link ExperimentType}.
 *
 * @author Sebastian Raubach
 */
public class DatasetWidget extends GerminateComposite implements HasHelp
{
	private DatasetCallback datasetCallback = null;
	private ExperimentType  experimentType  = null;
	private boolean         singleSelection = false;

	private FlowPanel   tablePanel;
	private SimplePanel mapPanel;
	private FlowPanel   buttonPanel;

	private DatasetTable table;

	private SafeHtml headerText = null;

	private String titleText; // = Text.LANG.datasetsTitle();

	private boolean internal = true;

	private boolean showLoadingIndicator = false;

	private boolean linkToExportPage = false;

	private boolean showDownload = false;

	private boolean showMap = false;

	private ReferenceFolder         referenceFolder  = null;
	private SimpleCallback<Dataset> downloadCallback = null;

	private DatasetTable.SelectionMode selectionMode = null;

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

		if (!StringUtils.isEmpty(titleText))
			tablePanel.add(new Heading(HeadingSize.H3, titleText));

		if (showMap)
		{
			DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), null, experimentType, internal, Pagination.getDefault(), new DefaultAsyncCallback<PaginatedServerResult<List<Dataset>>>(showLoadingIndicator)
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
		final Map<Location, Dataset> mapping = new HashMap<>();

		for (Dataset dataset : datasets)
		{
			Location location = dataset.getLocation();

			if (location != null)
				mapping.put(location, dataset);
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

		table = new DatasetTable(selectionMode, true, linkToExportPage)
		{
			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, experimentType, internal, pagination, callback);
			}
		};

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
			Button button = new Button(Text.LANG.generalContinue(), IconType.ARROW_CIRCLE_RIGHT, event ->
			{
				/* Get the selected items */
				Set<Dataset> selectedItems = table.getSelection();

				/* Get their ids */
				List<Long> ids = selectedItems.stream()
											  .map(DatabaseObject::getId)
											  .collect(Collectors.toList());

				if (ids.size() > 0)
				{
					/* Save the ids to the parameter store */
					switch (experimentType)
					{
						case allelefreq:
							LongListParameterStore.Inst.get().put(Parameter.allelefreqDatasetIds, ids);
							break;
						case climate:
							LongListParameterStore.Inst.get().put(Parameter.climateDatasetIds, ids);
							break;
						case compound:
							LongListParameterStore.Inst.get().put(Parameter.compoundDatasetIds, ids);
							break;
						case genotype:
							LongListParameterStore.Inst.get().put(Parameter.genotypeDatasetIds, ids);
							break;
						case phenotype:
							LongListParameterStore.Inst.get().put(Parameter.phenotypeDatasetIds, ids);
							break;
						case trials:
							LongListParameterStore.Inst.get().put(Parameter.trialsDatasetIds, ids);
							break;
					}

					/* Notify the callback */
					datasetCallback.onContinuePressed();
				}
				else
				{
					Notification.notify(Notification.Type.WARNING, Text.LANG.notificationDatasetsSelectAtLeastOne());
				}
			});
			button.setType(ButtonType.PRIMARY);
			button.addStyleName(Style.LAYOUT_BUTTON_MARGIN);

			buttonPanel.add(button);
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
		this.titleText = title;
	}

	/**
	 * Sets the text above the table
	 *
	 * @param headerText The text to display above the dataset table)
	 */
	public void setHeaderText(SafeHtml headerText)
	{
		this.headerText = headerText;
	}

	public void setHeaderText(String headerText)
	{
		this.setHeaderText(SafeHtmlUtils.fromTrustedString(new Heading(HeadingSize.H3, headerText).toString()));
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

	public void setShowDownload(boolean showDownload, ReferenceFolder refrenceFolder)
	{
		this.showDownload = showDownload;
		this.referenceFolder = refrenceFolder;

		if (table != null)
			table.setShowDownload(showDownload, referenceFolder);
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
	public Library[] getLibraryList()
	{
		return null;
	}


	@Override
	protected void setUpContent()
	{
		if (headerText != null)
		{
			panel.add(new HTML(headerText));
		}
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
}
