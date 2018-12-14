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
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.accession.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class MatrixChart<T extends DatabaseObject> extends AbstractChart
{
	private String         coloringValue;

	private FlowPanel chartPanel;
	private Button    deleteButton;
	private Button    badgeButton;

	public MatrixChart()
	{
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		this.chartPanel = chartPanel;
	}

	public void update(ExperimentType experimentType, List<Long> datasetIds, List<Long> objectIds, List<Long> groupIds, String color)
	{
		this.coloringValue = color;
		getData(experimentType, datasetIds, groupIds, objectIds, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				if (result.hasData())
				{
					filePath = new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

					MatrixChart.this.onResize(true);
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
				}
			}
		});
	}

	@Override
	protected void updateChart(int width)
	{
		create(coloringValue.equals(Text.LANG.trialsPByPColorByTreatment()), coloringValue.equals(Text.LANG.trialsPByPColorByDataset()), coloringValue.equals(Text.LANG.trialsPByPColorByYear()), width);
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

	private void getData(ExperimentType experimentType, List<Long> datasetIds, List<Long> groupIds, List<Long> objectIds, AsyncCallback<ServerResult<String>> callback)
	{
		switch (experimentType)
		{
			case trials:
				PhenotypeService.Inst.get().export(Cookie.getRequestProperties(), datasetIds, groupIds, objectIds, true, callback);
				break;

			case compound:
				CompoundService.Inst.get().getExportFile(Cookie.getRequestProperties(), datasetIds, groupIds, objectIds, true, callback);
				break;
		}
	}

	@Override
	protected String getPhotoExportFilename()
	{
		return "scattermatrix";
	}

	@Override
	protected MenuItem[] getAdditionalMenuItems()
	{
		MenuItem[] menuItems = new MenuItem[2];
		menuItems[0] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHECKBOX_MARKED, Text.LANG.cartAddSelectedToCartButton()), () -> MarkedItemList.add(MarkedItemList.ItemType.ACCESSION, getSelectedDataPoints()));
		menuItems[1] = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHECKBOX_BLANK_OUTLINE, Text.LANG.cartRemoveSelectedFromCartButton()), () -> MarkedItemList.remove(MarkedItemList.ItemType.ACCESSION, getSelectedDataPoints()));

		return menuItems;
	}

	private Set<String> getSelectedDataPoints()
	{
		List<String> ids = GQuery.$(chartPanel)
								 .find(".cell")
								 .first()
								 .find(".selected")
								 .map(new Function()
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

				Modal modal = new Modal();
				modal.setClosable(true);
				modal.setRemoveOnHide(true);
				modal.setSize(ModalSize.LARGE);

				ModalBody modalBody = new ModalBody();
				modalBody.add(new PassportPage());
				modal.add(modalBody);

				modal.show();
			}
			catch (UnsupportedDataTypeException e)
			{
			}
		}
	}

	private native void create(boolean colorByTreatment, boolean colorByDataset, boolean colorByYear, int widthHint)/*-{

		var tooltipStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_TIP_TOP;
		var dotStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ScatterMatrixChartBundle::STYLE_DOT;
		var axisStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ScatterMatrixChartBundle::STYLE_AXIS;
		var frameStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ScatterMatrixChartBundle::STYLE_FRAME;
		var hiddenStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.ScatterMatrixChartBundle::STYLE_HIDDEN;

		var legendItemStyle = @jhi.germinate.client.widget.d3js.resource.Bundles.BaseBundle::STYLE_D3_LEGEND_ITEM;

		var filePath = this.@jhi.germinate.client.widget.d3js.AbstractChart::filePath;
		var panelId = this.@jhi.germinate.client.widget.d3js.AbstractChart::panelId;

		var legendWidth = 100;

		var margin = @jhi.germinate.client.util.JavaScript.D3::getMargin()();
		var width = widthHint;

		var color = $wnd.d3.scale.ordinal().range(@jhi.germinate.client.util.JavaScript.D3::getColorPalette()());

		$wnd.d3.xhr(filePath).get(function (err, response) {
			var dirtyTsv = response.responseText;
			var firstEOL = dirtyTsv.indexOf('\n');
			var parsedTsv = $wnd.d3.tsv.parse(dirtyTsv.substring(firstEOL + 1)); // Remove the first row (Helium header)

			$wnd.d3.select("#" + panelId)
				.datum(parsedTsv)
				.call($wnd.scatterMatrix()
					.margin(margin)
					.width(width)
					.padding(15)
					.colorKey(function (d) {
						if (colorByTreatment)
							return d.treatments_description;
						else if (colorByDataset)
							return d.dataset_name;
						else if (colorByYear)
							return d.year;
						else
							return null;
					})
					.tooltip(function (d) {
						if (colorByTreatment && d.treatments_description)
							return d.name + "<br/>" + d.treatments_description + "<br/>(" + d.xValue + ", " + d.yValue + ")";
						else if (colorByDataset && d.dataset_name)
							return d.name + "<br/>" + d.dataset_name + "<br/>(" + d.xValue + ", " + d.yValue + ")";
						else if (colorByYear && d.year)
							return d.name + "<br/>" + d.year + "<br/>(" + d.xValue + ", " + d.yValue + ")";
						else
							return d.name + "<br/>(" + d.xValue + ", " + d.yValue + ")";
					})
					.onClick(function (d) {
						@jhi.germinate.client.widget.d3js.MatrixChart::onDataPointClicked(Ljava/lang/String;)(d.dbId);
					})
					.dotStyle(dotStyle)
					.radius(2)
					.axisStyle(axisStyle)
					.hiddenStyle(hiddenStyle)
					.frameStyle(frameStyle)
					.tooltipStyle(tooltipStyle)
					.legendItemStyle(legendItemStyle)
					.xTickFormat($wnd.d3.format(".2s"))
					.yTickFormat($wnd.d3.format(".2s"))
					.showLegend(true)
					.legendWidth(legendWidth)
					.idColumn("dbId")
					.ignoreColumns(["general_identifier", "dataset_name", "dataset_description", "dataset_version", "license_name", "treatments_description", "name", "location_name", "year"])
					.color(color));
		});

	}-*/;

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOOLTIP, Library.D3_LEGEND, Library.D3_SCATTER_MATRIX, Library.D3_DOWNLOAD};
	}
}
