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

package jhi.germinate.client.page.trial;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.gallery.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#compoundId
 */
public class TraitDetailsPage extends Composite
{
	private static CompoundDetailsPageUiBinder ourUiBinder = GWT.create(CompoundDetailsPageUiBinder.class);
	@UiField
	PageHeader           pageHeader;
	@UiField
	FlowPanel            resultsPanel;
	@UiField
	SynonymWidget        synonyms;
	@UiField
	SimplePanel          phenotypeDataTablePanel;
	@UiField
	AdditionalDataWidget additionalDatasetWidget;
	@UiField
	SimplePanel          datasetPanel;
	@UiField
	SimplePanel          galleryPanel;
	@UiField
	LinkWidget           linkWidget;
	private Phenotype          phenotype;
	private PhenotypeDataTable phenotypeDataTable;

	public TraitDetailsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long id = LongParameterStore.Inst.get().get(Parameter.phenotypeId);

		if (id != null)
		{
			PhenotypeService.Inst.get().getById(Cookie.getRequestProperties(), id, new DefaultAsyncCallback<ServerResult<Phenotype>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Phenotype> result)
				{
					phenotype = result.getServerResult();

					if (phenotype != null)
					{
						List<ExperimentType> types = new ArrayList<>();
						types.add(ExperimentType.trials);
						additionalDatasetWidget.setExperimentTypes(types);
						additionalDatasetWidget.setUpdateCallback(new AdditionalDataWidget.UpdateCallback()
						{
							@Override
							public void onVisibilityChanged(boolean visible)
							{
							}

							@Override
							public void onDataUpdate()
							{
								phenotypeDataTable.refreshTable();
							}
						});
						additionalDatasetWidget.update();

						resultsPanel.setVisible(true);
						pageHeader.setText(Text.LANG.traitDetailsFor(phenotype.getName()));

						synonyms.update(GerminateDatabaseTable.phenotypes, phenotype.getId());

						showPhenotypeDataTable();
						showDatasetTable();
						showImages();

						linkWidget.update(GerminateDatabaseTable.phenotypes, phenotype.getId());
					}
					else
					{
						pageHeader.setText(Text.LANG.notificationNoDataFound());
					}
				}
			});
		}
		else
		{
			pageHeader.setText(Text.LANG.notificationNoDataFound());
		}
	}

	private void showImages()
	{
		galleryPanel.add(new Gallery(false, false)
		{
			@Override
			protected void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback)
			{
				ImageService.Inst.get().getForId(Cookie.getRequestProperties(), GerminateDatabaseTable.phenotypes, phenotype.getId(), pagination, callback);
			}
		});
	}

	private void showDatasetTable()
	{
		datasetPanel.add(new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForFilterAndTrait(Cookie.getRequestProperties(), filter, ExperimentType.trials, phenotype.getId(), pagination, callback);
			}

			@Override
			protected void createColumns()
			{
				super.createColumns();

				if (phenotype.isNumeric())
				{
					SafeHtmlCell clickCell = new SafeHtmlCell()
					{
						@Override
						public Set<String> getConsumedEvents()
						{
							Set<String> events = new HashSet<>();
							events.add(BrowserEvents.CLICK);
							return events;
						}
					};

					// Add the histogram column
					addColumn(new Column<Dataset, SafeHtml>(clickCell)
					{
						@Override
						public String getCellStyleNames(Cell.Context context, Dataset row)
						{
							return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
						}

						@Override
						public SafeHtml getValue(Dataset row)
						{
							return SimpleHtmlTemplate.INSTANCE.materialIconAnchor(Style.MDI_CHART_BAR, Text.LANG.datasetHistogramTitle(), UriUtils.fromString(""), "");
						}

						@Override
						public void onBrowserEvent(Cell.Context context, Element elem, Dataset object, NativeEvent event)
						{
							if (BrowserEvents.CLICK.equals(event.getType()))
							{
								event.preventDefault();

								String file = object.getExtra("CHART_FILE");
								SimplePanel panel = new SimplePanel();
								HistogramChart chart;
								if (StringUtils.isEmpty(file))
									chart = new HistogramChart(phenotype.getId(), object.getId(), ExperimentType.trials);
								else
									chart = new HistogramChart(file);

								new AlertDialog(Text.LANG.datasetHistogramTitle(), panel)
										.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), Style.MDI_CANCEL, null))
										.setSize(ModalSize.LARGE)
										.addShownHandler(modalShownEvent -> panel.add(chart))
										.addHideHandler(modalHideEvent -> {
											String path = chart.getFilePath();
											object.setExtra("CHART_FILE", path);
										})
										.open();
							}
							else
							{
								super.onBrowserEvent(context, elem, object, event);
							}
						}
					}, "", false);
				}
			}
		});
	}

	private void showPhenotypeDataTable()
	{
		phenotypeDataTable = new PhenotypeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			{
				preventInitialDataLoad = true;
			}

			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				PhenotypeService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<PhenotypeData>>> callback)
			{
				return PhenotypeService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};
		phenotypeDataTablePanel.add(phenotypeDataTable);

		Scheduler.get().scheduleDeferred(() -> {
			PartialSearchQuery query = new PartialSearchQuery();
			query.add(new SearchCondition(Phenotype.NAME, new Equal(), phenotype.getName(), String.class));
			phenotypeDataTable.forceFilter(query, true);
		});
	}

	interface CompoundDetailsPageUiBinder extends UiBinder<HTMLPanel, TraitDetailsPage>
	{
	}
}