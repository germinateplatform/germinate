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

package jhi.germinate.client.page.compound;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.*;
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
import jhi.germinate.shared.Style;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#compoundId
 */
public class CompoundDetailsPage extends Composite
{

	@UiField
	PageHeader pageHeader;

	interface CompoundDetailsPageUiBinder extends UiBinder<HTMLPanel, CompoundDetailsPage>
	{
	}

	private static CompoundDetailsPageUiBinder ourUiBinder = GWT.create(CompoundDetailsPageUiBinder.class);

	private Compound compound;
	@UiField
	FlowPanel            resultsPanel;
	@UiField
	SynonymWidget        synonyms;
	@UiField
	SimplePanel          compoundDataTablePanel;
	@UiField
	AdditionalDataWidget additionalDatasetWidget;
	@UiField
	SimplePanel          datasetPanel;
	@UiField
	SimplePanel          galleryPanel;
	@UiField
	LinkWidget           linkWidget;
	private CompoundDataTable compoundDataTable;

	public CompoundDetailsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long id = LongParameterStore.Inst.get().get(Parameter.compoundId);

		if (id != null)
		{
			CompoundService.Inst.get().getById(Cookie.getRequestProperties(), id, new DefaultAsyncCallback<ServerResult<Compound>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Compound> result)
				{
					compound = result.getServerResult();

					if (compound != null)
					{
						List<ExperimentType> types = new ArrayList<>();
						types.add(ExperimentType.compound);
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
								compoundDataTable.refreshTable();
							}
						});
						additionalDatasetWidget.update();

						resultsPanel.setVisible(true);
						pageHeader.setText(Text.LANG.compoundDetailsFor(compound.getName()));

						synonyms.update(GerminateDatabaseTable.compounds, compound.getId());

						showCompoundDataTable();
						showDatasetTable();
						showImages();

						linkWidget.update(GerminateDatabaseTable.compounds, compound.getId());
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

	private void showDatasetTable()
	{
		datasetPanel.add(new DatasetTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForFilterAndTrait(Cookie.getRequestProperties(), filter, ExperimentType.compound, compound.getId(), pagination, callback);
			}

			@Override
			protected void createColumns()
			{
				super.createColumns();

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

							String oldPath = object.getExtra("CHART_FILE");

							if (StringUtils.isEmpty(oldPath))
							{
								CompoundService.Inst.get().getHistogramData(Cookie.getRequestProperties(), compound.getId(), object.getId(), new DefaultAsyncCallback<ServerResult<String>>()
								{
									@Override
									protected void onSuccessImpl(ServerResult<String> result)
									{
										String filePath = new ServletConstants.Builder()
												.setUrl(GWT.getModuleBaseURL())
												.setPath(ServletConstants.SERVLET_FILES)
												.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
												.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
												.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

										object.setExtra("CHART_FILE", filePath);
										showChart(filePath);
									}
								});
							}
							else
							{
								showChart(oldPath);
							}
						}
						else
						{
							super.onBrowserEvent(context, elem, object, event);
						}
					}

					private void showChart(String filePath)
					{
						SimplePanel panel = new SimplePanel();

						PlotlyHistogramChart chart = new PlotlyHistogramChart()
								.setxAxisTitle(compound.getName())
								.setyAxisTitle(Text.LANG.generalCount());
						panel.add(chart);

						new AlertDialog(Text.LANG.datasetHistogramTitle(), panel)
								.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalClose(), Style.MDI_CANCEL, null))
								.setSize(ModalSize.LARGE)
								.addShownHandler(modalShownEvent -> chart.setFilePath(filePath))
								.open();
					}
				}, "", false);
			}
		});
	}

	private void showImages()
	{
		galleryPanel.add(new Gallery(false, false)
		{
			@Override
			protected void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback)
			{
				ImageService.Inst.get().getForId(Cookie.getRequestProperties(), GerminateDatabaseTable.compounds, compound.getId(), pagination, callback);
			}
		});
	}

	private void showCompoundDataTable()
	{
		compoundDataTable = new CompoundDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				CompoundService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<CompoundData>>> callback)
			{
				if (filter == null)
					filter = new PartialSearchQuery();

				if (filter.getColumnNames().size() > 0)
					filter.addLogicalOperator(new And());

				filter.add(new SearchCondition(Compound.NAME, new Equal(), compound.getName(), String.class));

				return CompoundService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};
		compoundDataTablePanel.add(compoundDataTable);
	}
}