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
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.accession.*;
import jhi.germinate.client.service.*;
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
 * @author Sebastian Raubach
 */
public class PlotlyMatrixChart<T extends DatabaseObject> extends AbstractChart implements PlotlyChart
{
	private String coloringValue;

	private FlowPanel chartPanel;
	private Button    deleteButton;
	private Button    badgeButton;

	private Set<String> selectedIds = new HashSet<>();

	public PlotlyMatrixChart()
	{
	}

	@Override
	protected void createContent(FlowPanel chartPanel)
	{
		panel.add(chartPanel);

		this.chartPanel = chartPanel;
	}

	@Override
	protected boolean canDownloadSvg()
	{
		return false;
	}

	@Override
	public int[] getDownloadSize()
	{
		return new int[] {1280, 1280};
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

					PlotlyMatrixChart.this.onResize(true, false);
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

	public static void getData(ExperimentType experimentType, List<Long> datasetIds, List<Long> groupIds, List<Long> objectIds, AsyncCallback<ServerResult<String>> callback)
	{
		Set<String> markedIds = new HashSet<>();

		if (groupIds.contains(Group.ID_MARKED_ITEMS))
			markedIds.addAll(MarkedItemList.get(MarkedItemList.ItemType.ACCESSION));

		switch (experimentType)
		{
			case trials:
				PhenotypeService.Inst.get().export(Cookie.getRequestProperties(), datasetIds, groupIds, markedIds, objectIds, callback);
				break;

			case compound:
				CompoundService.Inst.get().getExportFile(Cookie.getRequestProperties(), datasetIds, groupIds, markedIds, objectIds, callback);
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
	 * Returns the ids of the selected data points (by lasso selection)
	 *
	 * @return The ids of the selected data points (by lasso selection)
	 */
	private Set<String> getSelectedDataPoints()
	{
		return selectedIds;
	}

	private native void create(boolean colorByTreatment, boolean colorByDataset, boolean colorByYear, int widthHint)/*-{

		var that = this;
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

		$wnd.Plotly.d3.xhr(filePath).get(function (err, response) {
			var dirtyTsv = response.responseText;
			var firstEOL = dirtyTsv.indexOf('\n');
			var parsedTsv = $wnd.d3.tsv.parse(dirtyTsv.substring(firstEOL + 1)); // Remove the first row

			$wnd.Plotly.d3.select("#" + panelId)
				.datum(parsedTsv)
				.call($wnd.plotlyScatterMatrix()
					.colors(colors)
					.colorBy(colorBy)
					.width(widthHint)
					.height(widthHint)
					.onPointClicked(function (point) {
						@jhi.germinate.client.widget.d3js.PlotlyMatrixChart::onDataPointClicked(Ljava/lang/String;)(point.id.split("-")[0]);
					})
					.onPointsSelected(function (points) {
						that.@jhi.germinate.client.widget.d3js.PlotlyMatrixChart::onDataPointsSelected(*)(points);
					}));
		});
	}-*/;

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.PLOTLY, Library.PLOTLY_SCATTER_MATRIX};
	}
}
