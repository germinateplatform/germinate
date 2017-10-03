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

package jhi.germinate.client.page.search;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.Map;
import java.util.stream.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.management.*;
import jhi.germinate.client.page.accession.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class SearchPage extends Composite implements HasHyperlinkButton, HasHelp
{
	interface SearchPageUiBinder extends UiBinder<FlowPanel, SearchPage>
	{
	}

	private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

	@UiField
	TextBox           searchBox;
	@UiField
	Button            searchButton;
	@UiField
	SearchTypeListBox typeBox;
	@UiField
	SearchSection     accessionSection;
	@UiField
	SearchSection     accessionAttributeSection;
	@UiField
	SearchSection     phenotypeSection;
	@UiField
	SearchSection     compoundSection;
	@UiField
	SearchSection     mapDefinitionSection;
	@UiField
	SearchSection     datasetSection;
	@UiField
	SearchSection     datasetAttributeSection;
	@UiField
	SearchSection     pedigreeSection;
	@UiField
	SearchSection     locationSection;
	@UiField
	Heading           resultHeading;
	@UiField
	FlowPanel         resultPanel;

	@UiField
	FlowPanel   additionalDataPanel;
	@UiField
	HTML        additionalDataTextShort;
	@UiField
	HTML        additionalDataText;
	@UiField
	SimplePanel additionalDataTablePanel;

	private AccessionTable              accessionDataTable;
	private AccessionAttributeDataTable accessionAttributeDataTable;
	private PhenotypeDataTable          phenotypeDataTable;
	private CompoundDataTable           compoundDataTable;
	private MapDefinitionTable          mapDefinitionTable;
	private DatasetTable                datasetTable;
	private DatasetAttributeDataTable   datasetAttributeDataTable;
	private PedigreeTable               pedigreeTable;
	private LocationTable               locationTable;

	private List<ExperimentType> experimentTypes = new ArrayList<>();
	private DatasetTable additionalDataTable;

	public SearchPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		String searchString = StringParameterStore.Inst.get().get(Parameter.searchString);
//		StringParameterStore.Inst.get().remove(Parameter.searchString);

		if (!StringUtils.isEmpty(searchString))
		{
			Scheduler.get().scheduleDeferred(() ->
			{
				searchBox.setText(searchString);
				searchBox.setFocus(true);
				searchBox.selectAll();
			});
		}

		try
		{
			long id = OsterPassportPage.isOsterEi(searchString);

			LongParameterStore.Inst.get().put(Parameter.accessionId, id);
			StringParameterStore.Inst.get().remove(Parameter.searchString);

			ContentHolder.getInstance().setContent(Page.PASSPORT, Page.ACCESSION_OVERVIEW, new OsterPassportPage());
		}
		catch (IllegalArgumentException e)
		{
		}

		if (ModuleCore.getUseAuthentication())
			addAdditionalDatasetsTable();

		additionalDataText.setHTML(Text.LANG.searchAdditionalDatasetsText());
		additionalDataTextShort.setHTML(Text.LANG.searchAdditionalDatasetsTextShort());
		additionalDataTextShort.addStyleName(Emphasis.INFO.getCssName());
	}

	private void doSearch(String searchString)
	{
		resultHeading.setSubText("\"" + searchString + "\"");

		final SearchType section = typeBox.getSelection();

		resultPanel.setVisible(true);

		accessionSection.clear();
		accessionAttributeSection.clear();
		phenotypeSection.clear();
		compoundSection.clear();
		mapDefinitionSection.clear();
		datasetSection.clear();
		datasetAttributeSection.clear();
		pedigreeSection.clear();
		locationSection.clear();

		accessionDataTable = null;
		accessionAttributeDataTable = null;
		phenotypeDataTable = null;
		compoundDataTable = null;
		mapDefinitionTable = null;
		datasetTable = null;
		datasetAttributeDataTable = null;
		pedigreeTable = null;
		locationTable = null;

		accessionSection.setVisible(false);
		accessionAttributeSection.setVisible(false);
		phenotypeSection.setVisible(false);
		compoundSection.setVisible(false);
		mapDefinitionSection.setVisible(false);
		datasetSection.setVisible(false);
		datasetAttributeSection.setVisible(false);
		pedigreeSection.setVisible(false);
		locationSection.setVisible(false);

		if (section == SearchType.ACCESSION_DATA || section == SearchType.ALL)
		{
			accessionDataTable = new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					AccessionService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					AccessionService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
				{
					return AccessionService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, new SearchCallback<>(accessionSection, callback));
				}
			};
			accessionSection.add(accessionDataTable);
		}

		if (section == SearchType.ACCESSION_ATTRIBUTE_DATA || section == SearchType.ALL)
		{
			accessionAttributeDataTable = new AccessionAttributeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					AttributeService.Inst.get().export(Cookie.getRequestProperties(), GerminateDatabaseTable.germinatebase, filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					AttributeService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), GerminateDatabaseTable.germinatebase, filter, callback);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<AttributeData>>> callback)
				{
					return AttributeService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, GerminateDatabaseTable.germinatebase, filter, new SearchCallback<>(accessionAttributeSection, callback));
				}
			};
			accessionAttributeSection.add(accessionAttributeDataTable);
		}

		if (section == SearchType.PHENOTYPE_DATA || section == SearchType.ALL)
		{
			phenotypeDataTable = new PhenotypeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					PhenotypeService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					PhenotypeService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<PhenotypeData>>> callback)
				{
					return PhenotypeService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), null, pagination, filter, new SearchCallback<>(phenotypeSection, callback));
				}
			};
			phenotypeSection.add(phenotypeDataTable);
		}

		if (section == SearchType.COMPOUND_DATA || section == SearchType.ALL)
		{
			compoundDataTable = new CompoundDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					CompoundService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					CompoundService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<CompoundData>>> callback)
				{
					return CompoundService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, new SearchCallback<>(compoundSection, callback));
				}
			};
			compoundSection.add(compoundDataTable);
		}

		if (section == SearchType.MAPDEFINITION_DATA || section == SearchType.ALL)
		{
			mapDefinitionTable = new MapDefinitionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					MarkerService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					MarkerService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback)
				{
					return MarkerService.Inst.get().getMapDefinitionForFilter(Cookie.getRequestProperties(), pagination, filter, new SearchCallback<>(mapDefinitionSection, callback));
				}
			};
			mapDefinitionSection.add(mapDefinitionTable);
		}

		if (section == SearchType.DATASETS || section == SearchType.ALL)
		{
			datasetTable = new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					DatasetService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
				{
					return DatasetService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, null, true, pagination, new SearchCallback<>(datasetSection, callback));
				}
			};
			datasetSection.add(datasetTable);
		}

		if (section == SearchType.DATASET_ATTRIBUTE_DATA || section == SearchType.ALL)
		{
			datasetAttributeDataTable = new DatasetAttributeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					AttributeService.Inst.get().export(Cookie.getRequestProperties(), GerminateDatabaseTable.datasets, filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<AttributeData>>> callback)
				{
					return AttributeService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, GerminateDatabaseTable.datasets, filter, new SearchCallback<>(datasetAttributeSection, callback));
				}
			};
			datasetAttributeSection.add(datasetAttributeDataTable);
		}

		if (section == SearchType.PEDIGREE_DATA || section == SearchType.ALL)
		{
			pedigreeTable = new PedigreeTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					PedigreeService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Pedigree>>> callback)
				{
					return PedigreeService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, pagination, new SearchCallback<>(pedigreeSection, callback));
				}
			};
			pedigreeSection.add(pedigreeTable);
		}

		if (section == SearchType.LOCATION_DATA || section == SearchType.ALL)
		{
			locationTable = new LocationTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				{
					preventInitialDataLoad = true;
				}

				@Override
				protected boolean supportsDownload()
				{
					return true;
				}

				@Override
				protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
				{
					LocationService.Inst.get().export(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					LocationService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Location>>> callback)
				{
					return LocationService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, pagination, new SearchCallback<>(locationSection, callback));
				}
			};
			locationSection.add(locationTable);
		}

		if (!StringUtils.isEmpty(searchString))
		{
			Scheduler.get().scheduleDeferred(() ->
			{
				experimentTypes.clear();
				experimentTypes = new ArrayList<>();
				if (section == SearchType.ACCESSION_DATA || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Accession.GENERAL_IDENTIFIER, searchString);
						mapping.put(Accession.NAME, searchString);
						mapping.put(Accession.NUMBER, searchString);
						mapping.put(Accession.COLLNUMB, searchString);
						mapping.put(Taxonomy.GENUS, searchString);
						mapping.put(Taxonomy.SPECIES, searchString);
						mapping.put(Country.COUNTRY_NAME, searchString);
						accessionDataTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.ACCESSION_ATTRIBUTE_DATA || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Accession.GENERAL_IDENTIFIER, searchString);
						mapping.put(Accession.NAME, searchString);
						mapping.put(Attribute.NAME, searchString);
						mapping.put(Attribute.DESCRIPTION, searchString);
						mapping.put(AttributeData.VALUE, searchString);
						accessionAttributeDataTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.PHENOTYPE_DATA || section == SearchType.ALL)
				{
					experimentTypes.add(ExperimentType.trials);
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Accession.GENERAL_IDENTIFIER, searchString);
						mapping.put(Accession.NAME, searchString);
						mapping.put(Dataset.DESCRIPTION, searchString);
						mapping.put(Phenotype.NAME, searchString);
						mapping.put(Phenotype.SHORT_NAME, searchString);
						phenotypeDataTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.COMPOUND_DATA || section == SearchType.ALL)
				{
					experimentTypes.add(ExperimentType.compound);
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Accession.GENERAL_IDENTIFIER, searchString);
						mapping.put(Accession.NAME, searchString);
						mapping.put(Compound.NAME, searchString);
						compoundDataTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.MAPDEFINITION_DATA || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Marker.MARKER_NAME, searchString);
						mapping.put(MapFeatureType.DESCRIPTION, searchString);
						mapping.put(jhi.germinate.shared.datastructure.database.Map.DESCRIPTION, searchString);
						mapping.put(MapDefinition.CHROMOSOME, searchString);
						mapDefinitionTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.DATASETS || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Dataset.DESCRIPTION, searchString);
						mapping.put(ExperimentType.DESCRIPTION, searchString);
						mapping.put(Experiment.EXPERIMENT_NAME, searchString);
						mapping.put(Dataset.CONTACT, searchString);
						datasetTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.DATASET_ATTRIBUTE_DATA || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Dataset.DESCRIPTION, searchString);
						mapping.put(Attribute.NAME, searchString);
						mapping.put(Attribute.DESCRIPTION, searchString);
						mapping.put(AttributeData.VALUE, searchString);
						datasetAttributeDataTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.PEDIGREE_DATA || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(PedigreeService.CHILD_GID, searchString);
						mapping.put(PedigreeService.CHILD_NAME, searchString);
						mapping.put(PedigreeService.PARENT_GID, searchString);
						mapping.put(PedigreeService.PARENT_NAME, searchString);
						pedigreeTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}
				if (section == SearchType.LOCATION_DATA || section == SearchType.ALL)
				{
					try
					{
						Map<String, String> mapping = new HashMap<>();
						mapping.put(Location.SITE_NAME, searchString);
						mapping.put(Location.REGION, searchString);
						mapping.put(Location.STATE, searchString);
						mapping.put(Country.COUNTRY_NAME, searchString);
						locationTable.forceFilter(mapping, false);
					}
					catch (InvalidArgumentException e)
					{
					}
				}

				additionalDataTable.refreshTable();
			});
		}

//		additionalDataTable.refreshTable();
	}

	private void updateTables()
	{
		if (accessionDataTable != null)
			accessionDataTable.refreshTable();
		if (accessionAttributeDataTable != null)
			accessionAttributeDataTable.refreshTable();
		if (phenotypeDataTable != null)
			phenotypeDataTable.refreshTable();
		if (compoundDataTable != null)
			compoundDataTable.refreshTable();
		if (mapDefinitionTable != null)
			mapDefinitionTable.refreshTable();
		if (datasetTable != null)
			datasetTable.refreshTable();
		if (pedigreeTable != null)
			pedigreeTable.refreshTable();
		if (locationTable != null)
			locationTable.refreshTable();
	}

	private void addAdditionalDatasetsTable()
	{
		additionalDataTable = new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.MULTI, true, false)
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
						additionalDataPanel.setVisible(false);
						additionalDataTextShort.setVisible(false);

						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(PaginatedServerResult<List<Dataset>> result)
					{
						additionalDataPanel.setVisible(!CollectionUtils.isEmpty(result.getServerResult()));
						additionalDataTextShort.setVisible(!CollectionUtils.isEmpty(result.getServerResult()));

						callback.onSuccess(result);
					}
				});
			}
		};

		additionalDataTablePanel.add(additionalDataTable);
	}

	@UiHandler("updateButton")
	void onUpdateButtonClicked(ClickEvent e)
	{
		/* Get the selected items */
		Set<Dataset> selectedItems = additionalDataTable.getSelection();

		Set<License> licensesToAgreeTo = selectedItems.stream()
													  .filter(d -> d.getLicense() != null)
													  .map(Dataset::getLicense)
													  .collect(Collectors.toCollection(HashSet::new));

		if (!CollectionUtils.isEmpty(licensesToAgreeTo))
		{
			DatasetWidget.showLicenseAcceptWizard(additionalDataTable, licensesToAgreeTo, new DefaultAsyncCallback<Set<Dataset>>()
			{
				@Override
				protected void onSuccessImpl(Set<Dataset> result)
				{
					// Refresh the table
					updateTables();
					additionalDataTable.refreshTable();
				}
			});
		}
	}

	@UiHandler("searchBox")
	void onSearchKeyPress(KeyPressEvent e)
	{
		if (e.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
			doSearch(searchBox.getText());
	}

	@UiHandler("searchBox")
	void onSearchBoxFocus(FocusEvent e)
	{
		searchBox.selectAll();
	}

	@UiHandler("searchButton")
	void onSearchButtonClicked(ClickEvent e)
	{
		doSearch(searchBox.getText());
	}

	public enum SearchType
	{
		ALL(Text.LANG.searchSectionAll()),
		ACCESSION_DATA(Text.LANG.searchSectionAccessionData()),
		ACCESSION_ATTRIBUTE_DATA(Text.LANG.searchSectionAccessionAttributeData()),
		PHENOTYPE_DATA(Text.LANG.searchSectionPhenotypeData()),
		COMPOUND_DATA(Text.LANG.searchSectionCompoundData()),
		MAPDEFINITION_DATA(Text.LANG.searchSectionMapDefinitionData()),
		DATASETS(Text.LANG.searchSectionDatasets()),
		DATASET_ATTRIBUTE_DATA(Text.LANG.searchSectionDatasetAttributeData()),
		PEDIGREE_DATA(Text.LANG.searchSectionPedigreeData()),
		LOCATION_DATA(Text.LANG.searchSectionLocationData());

		String title;

		SearchType(String title)
		{
			this.title = title;
		}

		public String getTitle()
		{
			return title;
		}
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.SEARCH)
				.addParam(Parameter.searchString);
	}

	@Override
	public Widget getHelpContent()
	{
		return new Label(Text.LANG.searchHelp());
	}
}