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

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.enums.*;

public abstract class DataExportWizard extends Composite
{
	private static final DataExportWizardUiBinder uiBinder = GWT.create(DataExportWizardUiBinder.class);

	interface DataExportWizardUiBinder extends UiBinder<Widget, DataExportWizard>
	{
	}

	@UiField
	FlowPanel    overallPanel;
	@UiField
	HTML         accessionText;
	@UiField
	GroupListBox accessionGroupsList;
	@UiField
	HTML         markerText;
	@UiField
	GroupListBox markerGroupsList;
	@UiField
	MapListBox   mapsList;
	@UiField
	Button       continueButton;

	@UiField
	LIElement    genotypeMessage;
	@UiField
	LIElement    alleleMessage;
	@UiField
	ToggleSwitch mdfToggle;

	private List<Long> selectedDatasets;

	public DataExportWizard(final ExportType type)
	{
		initWidget(uiBinder.createAndBindUi(this));

		switch (type)
		{
			case allelefreq:
				selectedDatasets = LongListParameterStore.Inst.get().get(Parameter.allelefreqDatasetIds);
				break;
			case genotype:
				selectedDatasets = LongListParameterStore.Inst.get().get(Parameter.genotypeDatasetIds);
				break;
		}

		if (selectedDatasets == null || selectedDatasets.size() < 1)
		{
			showError(Text.LANG.notificationExportNoDataset());
			return;
		}

		accessionText.setHTML(Text.LANG.genotypeExportSubtitleAccessionGroups());
		markerText.setHTML(Text.LANG.genotypeExportSubtitleMarkerGroups());

		accessionGroupsList.setGroupCreationInterface(callback -> GroupService.Inst.get().getAccessionGroups(Cookie.getRequestProperties(), selectedDatasets, type.type, callback));
		markerGroupsList.setGroupCreationInterface(callback -> GroupService.Inst.get().getMarkerGroups(Cookie.getRequestProperties(), selectedDatasets, type.type, callback));

		ParallelAsyncCallback<ServerResult<List<Group>>> accessionGroupsCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getAccessionGroups(Cookie.getRequestProperties(), selectedDatasets, type.type, this);
			}
		};
		ParallelAsyncCallback<ServerResult<List<Group>>> markerGroupsCallback = new ParallelAsyncCallback<ServerResult<List<Group>>>()
		{
			@Override
			protected void start()
			{
				GroupService.Inst.get().getMarkerGroups(Cookie.getRequestProperties(), selectedDatasets, type.type, this);
			}
		};
		ParallelAsyncCallback<PaginatedServerResult<List<Map>>> mapsCallback = new ParallelAsyncCallback<PaginatedServerResult<List<Map>>>()
		{
			@Override
			protected void start()
			{
				MapService.Inst.get().get(Cookie.getRequestProperties(), type.type, Pagination.getDefault(), this);
			}
		};

		new ParallelParentAsyncCallback(true, accessionGroupsCallback, markerGroupsCallback, mapsCallback)
		{
			@Override
			public void handleSuccess()
			{
				ServerResult<List<Group>> accessionGroups = getCallbackData(0);
				ServerResult<List<Group>> markerGroups = getCallbackData(1);
				PaginatedServerResult<List<Map>> mapsData = getCallbackData(2);

				if (CollectionUtils.isEmpty(accessionGroups.getServerResult()))
				{
					handleFailure(new RuntimeException(Text.LANG.notificationGenotypeExportNoAccessionGroupsForDataset()));
					return;
				}
				if (CollectionUtils.isEmpty(markerGroups.getServerResult()))
				{
					handleFailure(new RuntimeException(Text.LANG.notificationGenotypeExportNoMarkerGroupsForDataset()));
					return;
				}
				if (CollectionUtils.isEmpty(mapsData.getServerResult()))
				{
					handleFailure(new RuntimeException(Text.LANG.notificationGenotypeExportNoMap()));
					return;
				}

				accessionGroupsList.setValue(accessionGroups.getServerResult().get(0), false);
				accessionGroupsList.setAcceptableValues(accessionGroups.getServerResult());

				markerGroupsList.setValue(markerGroups.getServerResult().get(0), false);
				markerGroupsList.setAcceptableValues(markerGroups.getServerResult());

				mapsList.setValue(mapsData.getServerResult().get(0), false);
				mapsList.setAcceptableValues(mapsData.getServerResult());
			}

			@Override
			public void handleFailure(Exception reason)
			{
				if (reason instanceof RuntimeException)
					showError(reason.getLocalizedMessage());
			}
		};

		if (type == ExportType.allelefreq)
		{
			if (type == ExportType.genotype)
				alleleMessage.removeFromParent();
			else
				genotypeMessage.removeFromParent();
		}

		mdfToggle.setOnText(Text.LANG.generalYes());
		mdfToggle.setOffText(Text.LANG.generalNo());
		mdfToggle.setValue(true);
	}

	@UiHandler("continueButton")
	void onContinuePressed(ClickEvent event)
	{
		/* Get all the user selection */
		List<Long> accessionGroups, markerGroups, maps;

		accessionGroups = DatabaseObject.getIds(accessionGroupsList.getSelections());
		markerGroups = DatabaseObject.getIds(markerGroupsList.getSelections());
		maps = DatabaseObject.getIds(mapsList.getSelections());

		if (CollectionUtils.isEmpty(accessionGroups, markerGroups, maps))
			Notification.notify(Notification.Type.WARNING, Text.LANG.notificationGenotypeExportSelectAtLeastOne());
		else
			onContinuePressed(selectedDatasets, accessionGroups, markerGroups, maps, mdfToggle.getValue(), false);
	}

	/**
	 * Shows an error with the given message
	 *
	 * @param message The error message
	 */
	private void showError(String message)
	{
		overallPanel.clear();
		overallPanel.add(new Heading(HeadingSize.H3, message));
	}

	protected abstract void onContinuePressed(List<Long> datasets, List<Long> accessionGroups, List<Long> markerGroups, List<Long> maps, boolean missingOn, boolean heterozygousOn);

	public enum ExportType
	{
		allelefreq(ExperimentType.allelefreq),
		genotype(ExperimentType.genotype);

		private ExperimentType type;

		ExportType(ExperimentType type)
		{
			this.type = type;
		}
	}
}
