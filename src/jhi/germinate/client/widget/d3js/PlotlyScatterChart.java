/*
 *  Copyright 2019 Information and Computational Sciences,
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

package jhi.germinate.client.widget.d3js;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.accession.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * PhenotypeByPhenotypeChart is a d3.js chart that plots data of two objects against each other in a scatter plot. Along each side of the scatter
 * plot, there is a density visualization. The user will also be able to draw an arbitrary polygon on the chart to select accessions.
 *
 * @author Sebastian Raubach
 */
public class PlotlyScatterChart<T extends DatabaseObject> extends AbstractChart implements PlotlyChart
{
	private String         xAxisTitle = "";
	private String         yAxisTitle = "";
	private JsArrayInteger size       = null;
	private String         coloringValue;

	private FlowPanel   chartPanel;
	private Button      deleteButton;
	private Button      badgeButton;
	private Set<String> selectedIds = new HashSet<>();

	public PlotlyScatterChart()
	{
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		this.chartPanel = chartPanel;
		chartPanel.addStyleName(Alignment.CENTER.getCssName());
	}

	@Override
	protected void updateChart(int width)
	{
		computeChartSize(width);
		create(coloringValue.equals(Text.LANG.trialsPByPColorByTreatment()), coloringValue.equals(Text.LANG.trialsPByPColorByDataset()), coloringValue.equals(Text.LANG.trialsPByPColorByYear()), GerminateSettingsHolder.getCategoricalColor(0));
	}

	@Override
	public int[] getDownloadSize()
	{
		return new int[] {1280, 1280};
	}

	@Override
	protected boolean canDownloadSvg()
	{
		return false;
	}

	protected Button[] getAdditionalButtons()
	{
		// Add the button
		if (deleteButton == null && badgeButton == null)
		{
			ButtonGroup group = new ButtonGroup();
			group.addStyleName(Style.LAYOUT_FLOAT_INITIAL);
			// Add the button
			deleteButton = new Button("", e -> AlertDialog.createYesNoDialog(Text.LANG.generalClear(), Text.LANG.markedItemListClearConfirm(), false, ev -> MarkedItemList.clear(MarkedItemList.ItemType.ACCESSION), null));
			deleteButton.addStyleName(Style.mdiLg(Style.MDI_DELETE));
			deleteButton.setTitle(Text.LANG.generalClear());

			badgeButton = new Button("", e -> {
				ItemTypeParameterStore.Inst.get().put(Parameter.markedItemType, MarkedItemList.ItemType.ACCESSION);
				History.newItem(Page.MARKED_ITEMS.name());
			});
			// Add the actual badge that shows the number
			Badge badge = new Badge(NumberUtils.INTEGER_FORMAT.format(MarkedItemList.get(MarkedItemList.ItemType.ACCESSION).size()));
			group.add(deleteButton);
			group.add(badgeButton);
			badgeButton.add(badge);

			// Listen to shopping cart changes
			GerminateEventBus.BUS.addHandler(MarkedItemListEvent.TYPE, event -> badge.setText(NumberUtils.INTEGER_FORMAT.format(MarkedItemList.get(MarkedItemList.ItemType.ACCESSION).size())));
		}

		return new Button[]{deleteButton, badgeButton};
	}

	private void setNames(ExperimentType experimentType, T f, T s)
	{
		switch (experimentType)
		{
			case compound:
				Compound firstCompound = (Compound) f;
				Compound secondCompound = (Compound) s;
				xAxisTitle = firstCompound.getName();
				if (firstCompound.getUnit() != null && !StringUtils.isEmpty(firstCompound.getUnit().getAbbreviation()))
					xAxisTitle += " [" + firstCompound.getUnit().getAbbreviation() + "]";

				yAxisTitle = secondCompound.getName();
				if (secondCompound.getUnit() != null && !StringUtils.isEmpty(secondCompound.getUnit().getAbbreviation()))
					yAxisTitle += " [" + secondCompound.getUnit().getAbbreviation() + "]";
				break;
			case trials:
			default:
				Phenotype firstPhenotype = (Phenotype) f;
				Phenotype secondPhenotype = (Phenotype) s;
				xAxisTitle = firstPhenotype.getName();
				if (firstPhenotype.getUnit() != null && !StringUtils.isEmpty(firstPhenotype.getUnit().getAbbreviation()))
					xAxisTitle += " [" + firstPhenotype.getUnit().getAbbreviation() + "]";

				yAxisTitle = secondPhenotype.getName();
				if (secondPhenotype.getUnit() != null && !StringUtils.isEmpty(secondPhenotype.getUnit().getAbbreviation()))
					yAxisTitle += " [" + secondPhenotype.getUnit().getAbbreviation() + "]";

				break;

		}

	}

	/**
	 * Tries to compute the "optimal" size for the chart. It's a scatter plot, so we'd want it to be square.
	 *
	 * @param pageWidth The available width
	 */
	private void computeChartSize(int pageWidth)
	{
		pageWidth = Math.min(Math.round(pageWidth), Math.round(Window.getClientHeight()));

		size = JsArrayInteger.createArray().cast();
		size.push(pageWidth);
		size.push(pageWidth);
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "scatterplot";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		MenuItem[] menuItems = new MenuItem[2];
		menuItems[0] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHECKBOX_MARKED, Text.LANG.cartAddSelectedToCartButton()), () -> MarkedItemList.add(MarkedItemList.ItemType.ACCESSION, getSelectedDataPoints()));
		menuItems[1] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHECKBOX_BLANK_OUTLINE, Text.LANG.cartRemoveSelectedFromCartButton()), () -> MarkedItemList.remove(MarkedItemList.ItemType.ACCESSION, getSelectedDataPoints()));

		return menuItems;
	}

	private native void create(boolean colorByTreatment, boolean colorByDataset, boolean colorByYear, String highlightColor)/*-{
		var that = this;
		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyScatterChart::xAxisTitle;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.PlotlyScatterChart::yAxisTitle;

		var size = this.@jhi.germinate.client.widget.d3js.PlotlyScatterChart::size;
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var colors = @jhi.germinate.client.util.JavaScript.D3::getColorPalette()();

		var colorBy;

		if (colorByTreatment)
			colorBy = 'treatments_description';
		else if (colorByDataset)
			colorBy = 'dataset_name';
		else if (colorByYear)
			colorBy = 'year';
		else
			colorBy = '';

		var width = size[0];
		var height = size[1];

		if (colorBy.length > 0)
			height += 50;

		$wnd.Plotly.d3.xhr(filePath).get(function (err, response) {
			var dirtyTsv = response.responseText;
			var firstEOL = dirtyTsv.indexOf('\n');
			var parsedTsv = $wnd.d3.tsv.parse(dirtyTsv.substring(firstEOL + 1)); // Remove the first row

			$wnd.Plotly.d3.select("#" + panelId)
				.datum(parsedTsv)
				.call($wnd.plotlyScatterPlot()
					.colors(colors)
					.colorBy(colorBy)
					.width(width)
					.height(height)
					.xCategory(xAxisTitle)
					.yCategory(yAxisTitle)
					.onPointClicked(function (point) {
						@jhi.germinate.client.widget.d3js.PlotlyScatterChart::onDataPointClicked(Ljava/lang/String;)(point.id.split("-")[0]);
					})
					.onPointsSelected(function (points) {
						that.@jhi.germinate.client.widget.d3js.PlotlyScatterChart::onDataPointsSelected(*)(points);
					}));
		});
	}-*/;

	public void onDataPointsSelected(JsArrayString ids)
	{
		selectedIds.clear();

		if (ids != null)
		{
			for (int i = 0; i < ids.length(); i++)
				selectedIds.add(ids.get(i));
		}
	}

	/**
	 * Handles selection of data points. Will redirect to {@link Page#PASSPORT}
	 *
	 * @param id The accession id
	 */
	private static void onDataPointClicked(String id)
	{
		if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
		{
			try
			{
				LongParameterStore.Inst.get().putAsString(Parameter.accessionId, id);

				Modal modal = new Modal();
				modal.setClosable(true);
				modal.setRemoveOnHide(true);
				modal.setSize(ModalSize.LARGE);

				ModalBody modalBody = new ModalBody();
				modalBody.add(new PassportPage(false));
				modal.add(modalBody);

				modal.show();
			}
			catch (UnsupportedDataTypeException e)
			{
			}
		}
	}

	/**
	 * Returns the ids of the selected data points (by lasso selection)
	 *
	 * @return The ids of the selected data points (by lasso selection)
	 */
	private Set<String> getSelectedDataPoints()
	{
		return selectedIds;
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_SCATTER_PLOT, Library.D3_V3, Library.D3_DOWNLOAD};
	}

	public void update(ExperimentType experimentType, List<Long> selectedDatasetIds, List<T> objects, List<Long> groupIds, String color)
	{
		this.coloringValue = color;

		// JavaScript.D3.removeD3();
		T first = objects.get(0);
		T second = objects.get(1);
		Long firstId = first.getId();
		Long secondId = second.getId();

		setNames(experimentType, first, second);

		PlotlyMatrixChart.getData(experimentType, selectedDatasetIds, groupIds, Arrays.asList(firstId, secondId), new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				chartPanel.clear();
				chartPanel.getElement().removeAllChildren();

				if (result.hasData())
				{
					filePath = new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

					PlotlyScatterChart.this.onResize(true, false);
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
				}
			}
		});
	}
}
