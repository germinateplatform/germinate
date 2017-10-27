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

package jhi.germinate.client.widget.d3js;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.resource.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
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
public class ScatterChart<T extends DatabaseObject> extends AbstractChart
{
	private String         xAxisTitle = "";
	private String         yAxisTitle = "";
	private JsArrayInteger size       = null;
	private ExperimentType experimentType;

	private List<Long>  datasetIds;
	private List<T>     objects;
	private List<Group> groups;

	private FlowPanel                chartPanel;
	private ScatterChartSelection<T> parameterSelection;
	private Button deleteButton;
	private Button badgeButton;

	public ScatterChart()
	{
	}

	public ScatterChart(List<T> objects, List<Group> groups, ExperimentType experimentType, String title, SafeHtml message)
	{
		super(title, message);
		this.experimentType = experimentType;
		if (objects != null)
			this.objects = new ArrayList<>(objects);
		if (groups != null)
			this.groups = new ArrayList<>(groups);
	}

	public void update(ExperimentType expertimentType, List<T> objects, List<Group> groups, SafeHtml message)
	{
		setMessage(message);

		this.experimentType = expertimentType;
		if (objects != null)
			this.objects = new ArrayList<>(objects);
		if (groups != null)
			this.groups = new ArrayList<>(groups);

		initComboBoxes();
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		this.chartPanel = chartPanel;
		initComboBoxes();
	}

	@Override
	protected void updateChart(int width)
	{
		computeChartSize(width);
		String coloringValue = parameterSelection.getColor();
		create(coloringValue.equals(Text.LANG.trialsPByPColorByTreatment()), coloringValue.equals(Text.LANG.trialsPByPColorByDataset()), coloringValue.equals(Text.LANG.trialsPByPColorByYear()), GerminateSettingsHolder.getCategoricalColor(0));
	}

	protected Button[] getAdditionalButtons()
	{
		// Add the button
		if(deleteButton == null && badgeButton == null)
		{
			ButtonGroup group = new ButtonGroup();
			group.addStyleName(Style.LAYOUT_FLOAT_INITIAL);
			// Add the button
			deleteButton = new Button("", e -> {
				AlertDialog.createYesNoDialog(Text.LANG.generalClear(), Text.LANG.markedItemListClearConfirm(), false, ev -> MarkedItemList.clear(MarkedItemList.ItemType.ACCESSION), null);
			});
			deleteButton.addStyleName(Style.combine(Style.MDI, Style.MDI_DELETE));
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

		return new Button[] {deleteButton, badgeButton};
	}

	private void initPlotButton()
	{
		Button plot = new Button(Text.LANG.trialsPlot(), IconType.ARROW_CIRCLE_RIGHT, e ->
		{
			// JavaScript.D3.removeD3();
			T first = parameterSelection.getFirstObject().get(0);
			T second = parameterSelection.getSecondObject().get(0);
			Long groupId = parameterSelection.getGroup().getId();
			Long firstId = first.getId();
			Long secondId = second.getId();

			setNames(first, second);


			LongParameterStore.Inst.get().put(Parameter.trialsPhenotypeOne, firstId);
			LongParameterStore.Inst.get().put(Parameter.trialsPhenotypeTwo, secondId);

			getData(groupId, firstId, secondId, new DefaultAsyncCallback<ServerResult<String>>(true)
			{
				@Override
				protected void onSuccessImpl(ServerResult<String> result)
				{
					chartPanel.clear();
					chartPanel.getElement().removeAllChildren();

					if (!StringUtils.isEmpty(result.getServerResult()))
					{
						filePath = new ServletConstants.Builder()
								.setUrl(GWT.getModuleBaseURL())
								.setPath(ServletConstants.SERVLET_FILES)
								.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
								.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
								.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

						ScatterChart.this.onResize(true);
					}
					else
					{
						Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
					}
				}
			});
		});
		plot.setType(ButtonType.PRIMARY);

		panel.add(plot);
		panel.add(chartPanel);
	}

	private void setNames(T f, T s)
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

	private void getData(Long groupId, Long firstId, Long secondId, AsyncCallback<ServerResult<String>> callback)
	{
		switch (experimentType)
		{
			case trials:
				TrialService.Inst.get().exportPhenotypeScatter(Cookie.getRequestProperties(), datasetIds, firstId, secondId, groupId, callback);
				break;
			case compound:
				CompoundService.Inst.get().getCompoundByCompoundFile(Cookie.getRequestProperties(), datasetIds, firstId, secondId, groupId, callback);
				break;
		}

	}

	private void initComboBoxes()
	{
		if (objects != null && parameterSelection == null)
		{
			switch (experimentType)
			{
				case trials:
					datasetIds = LongListParameterStore.Inst.get().get(Parameter.trialsDatasetIds);
					break;
				case compound:
					datasetIds = LongListParameterStore.Inst.get().get(Parameter.compoundDatasetIds);
					break;
			}

			parameterSelection = new ScatterChartSelection<>(experimentType, objects, groups);

			panel.add(parameterSelection);

			initPlotButton();
		}
	}

	/**
	 * Tries to compute the "optimal" size for the chart. It's a scatter plot, so we'd want it to be square.
	 *
	 * @param pageWidth The available width
	 */
	private void computeChartSize(int pageWidth)
	{
		pageWidth = Math.min(Math.round(pageWidth * 0.8f), Math.round(Window.getClientHeight() * 0.8f));

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

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;
		var legendItemStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_LEGEND_ITEM;
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_AXIS;

		var possibleStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.LassoBundle::STYLE_POSSIBLE;
		var notPossibleStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.LassoBundle::STYLE_NOT_POSSIBLE;
		var selectedStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.LassoBundle::STYLE_SELECTED;

		var dotStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ScatterChartBundle::STYLE_DOT;

		var xAxisTitle = this.@jhi.germinate.client.widget.d3js.ScatterChart::xAxisTitle;
		var yAxisTitle = this.@jhi.germinate.client.widget.d3js.ScatterChart::yAxisTitle;

		var size = this.@jhi.germinate.client.widget.d3js.ScatterChart::size;
		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var legendWidth = 100;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		margin[1] = margin[2] = margin[3] = margin[0];
		var width = size[0];
		var height = size[1] - legendWidth;

		margin.bottom += 5;
		margin.right += 5;

		var color = $wnd.d3.scale.ordinal().range(@jhi.germinate.client.util.JavaScript.D3::getColorPalette()());

		$wnd.d3.tsv(filePath, function (data) {
			$wnd.d3.select("#" + panelId)
				.datum(data)
				.call($wnd.scatterPlot()
					.margin(margin)
					.width(width)
					.height(height)
					.x(function (d) {
						return parseFloat(d[xAxisTitle]);
					})
					.y(function (d) {
						return parseFloat(d[yAxisTitle]);
					})
					.colorKey(function (d) {
						if (colorByTreatment)
							return d.treatment;
						else if (colorByDataset)
							return d.dataset;
						else if (colorByYear)
							return d.recording_date;
						else
							return null;
					})
					.itemName(function (d) {
						return d.name;
					})
					.highlightColor(highlightColor)
					.tooltip(function (d) {
						if (colorByTreatment && d.treatment)
							return d.name + "<br/>" + d.treatment + "<br/>(" + d[xAxisTitle] + ", " + d[yAxisTitle] + ")";
						else if (colorByDataset && d.dataset)
							return d.name + "<br/>" + d.dataset + "<br/>(" + d[xAxisTitle] + ", " + d[yAxisTitle] + ")";
						else if (colorByYear && d.recording_date)
							return d.name + "<br/>" + d.recording_date + "<br/>(" + d[xAxisTitle] + ", " + d[yAxisTitle] + ")";
						else
							return d.name + "<br/>(" + d[xAxisTitle] + ", " + d[yAxisTitle] + ")";
					})
					.onClick(function (d) {
						@jhi.germinate.client.widget.d3js.ScatterChart::onDataPointClicked(Ljava/lang/String;)(d.id);
					})
					.color(color)
					.tooltipStyle(tooltipStyle)
					.legendItemStyle(legendItemStyle)
					.showLegend(true)
					.legendWidth(legendWidth)
					.axisStyle(axisStyle)
					.dotStyle(dotStyle)
					.showDistribution(true)
					.lassoConfig({
						"possibleStyle": possibleStyle,
						"notPossibleStyle": notPossibleStyle,
						"selectedStyle": selectedStyle
					})
					.xLabel(xAxisTitle)
					.yLabel(yAxisTitle));
		});

	}-*/;

	/**
	 * Returns the ids of the selected data points (by lasso selection)
	 *
	 * @return The ids of the selected data points (by lasso selection)
	 */
	private Set<String> getSelectedDataPoints()
	{
		List<String> ids = GQuery.$(chartPanel).find("." + Bundles.LassoBundle.STYLE_SELECTED).map(new Function()
		{
			@Override
			public String f(Element e, int i)
			{
				return e.getId().replace("item-", "");
			}
		});

		return new HashSet<>(ids);
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
				History.newItem(Page.PASSPORT.name());
			}
			catch (UnsupportedDataTypeException e)
			{
			}
		}
	}

	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LASSO, Library.D3_LEGEND, Library.D3_SCATTER_PLOT, Library.D3_DOWNLOAD};
	}
}
