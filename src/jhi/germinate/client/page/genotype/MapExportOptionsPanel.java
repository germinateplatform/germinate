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

package jhi.germinate.client.page.genotype;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class MapExportOptionsPanel extends Composite
{

	private static MapsPageUiBinder ourUiBinder = GWT.create(MapsPageUiBinder.class);
	@UiField
	FlowPanel         panel;
	@UiField
	Panel             exportOptionsPanel;
	@UiField
	PanelCollapse     collapsePanel;
	@UiField
	HTML              chromosomeHtml;
	@UiField
	ListBox           chromosomeBox;
	@UiField
	HTML              regionHtml;
	@UiField
	TextBox           intervalFirstMarker;
	@UiField
	TextBox           intervalSecondMarker;
	@UiField
	HTML              intervalHtml;
	@UiField
	HTML              radiusHtml;
	@UiField
	HTML              panelHtml;
	@UiField
	Button            closeOptions;
	@UiField
	NavTabs           tabs;
	@UiField
	TabListItem       chromosomeTab;
	@UiField
	TabListItem       regionTab;
	@UiField
	TabListItem       intervalTab;
	@UiField
	TabListItem       radiusTab;
	@UiField
	TextBox           radiusMarker;
	@UiField
	RangedLongTextBox radiusLeft;
	@UiField
	RangedLongTextBox radiusRight;
	@UiField
	SimplePanel       regionTable;
	@UiField
	SimplePanel       markerDownloadPanel;
	private DownloadWidget                 downloadWidget;
	/* Range controls */
	private CellTable<MappingEntry>        table;
	private ListDataProvider<MappingEntry> dataProvider;
	private List<String>                   chromosomes;
	private Map                            map;

	public MapExportOptionsPanel()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	public void update(Map map)
	{
		this.map = map;

		panel.setVisible(map != null);

		setUpMapDownloadPanel();
	}

	private void setUpMapDownloadPanel()
	{
		if (downloadWidget == null)
		{
			List<DownloadWidget.FileConfig> files = new ArrayList<>();
			files.add(new DownloadWidget.FileConfig(Text.LANG.downloadInFlapjackFormat(), FileType.flapjack).setLongRunning(true).setStyle(FileType.IconStyle.IMAGE));
			files.add(new DownloadWidget.FileConfig(Text.LANG.downloadInMapChartFormat(), FileType.mct).setLongRunning(true).setStyle(FileType.IconStyle.IMAGE));
			files.add(new DownloadWidget.FileConfig(Text.LANG.downloadInStrudelFormat(), FileType.strudel).setLongRunning(true).setStyle(FileType.IconStyle.IMAGE));

			downloadWidget = new DownloadWidget()
			{
				@Override
				protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
				{
					MapFormat format;

					switch (config.getType())
					{
						case flapjack:
							format = MapFormat.flapjack;
							break;
						case mct:
							format = MapFormat.mapchart;
							break;
						case strudel:
							format = MapFormat.strudel;
							break;
						default:
							format = MapFormat.flapjack;
					}

					try
					{
						MapExportOptions settings = getOptions();

						MapService.Inst.get().getInFormat(Cookie.getRequestProperties(), map.getId(), format, settings, callback);
					}
					catch (InvalidOptionsException e)
					{
						callback.onFailure(e);
					}
				}
			};
			downloadWidget.addAll(files);
			markerDownloadPanel.add(downloadWidget);

			panelHtml.setHTML(Text.LANG.markersExportOptionsText());
			chromosomeHtml.setHTML(Text.LANG.markersExportOptionsChromosomesText());
			regionHtml.setHTML(Text.LANG.markersExportOptionsRegionText());
			intervalHtml.setHTML(Text.LANG.markersExportOptionsIntervalText());
			radiusHtml.setHTML(Text.LANG.markersExportOptionsRadiusText());
		}

		MapService.Inst.get().getChromosomesForMap(Cookie.getRequestProperties(), map.getId(), new DefaultAsyncCallback<ServerResult<List<String>>>()
		{
			@Override
			public void onSuccessImpl(ServerResult<List<String>> result)
			{
				if (result.hasData())
				{
					chromosomes = result.getServerResult();
					exportOptionsPanel.setVisible(true);
					chromosomeBox.clear();
					chromosomes.forEach(chromosomeBox::addItem);

					setUpTable();
				}
				else
				{
					exportOptionsPanel.setVisible(false);
				}
			}
		});
	}

	private void setUpTable()
	{
		regionTable.clear();
		table = new CellTable<>(DatabaseObjectPaginationTable.DEFAULT_NR_OF_ITEMS_PER_PAGE);
		regionTable.add(table);
		final SelectionModel<MappingEntry> selectionModel = new MultiSelectionModel<>();
		table.setSelectionModel(selectionModel, DefaultSelectionEventManager.createCheckboxManager());
		dataProvider = new ListDataProvider<>();
		dataProvider.addDataDisplay(table);

		List<MappingEntry> list = new ArrayList<>();
		list.add(new MappingEntry(chromosomes.get(0), null, null));
		dataProvider.setList(list);

		/* First column: checkboxes */
		Column<MappingEntry, Boolean> checkboxColumn = new Column<MappingEntry, Boolean>(new CheckboxCell(true, false))
		{
			@Override
			public Boolean getValue(MappingEntry object)
			{
				return selectionModel.isSelected(object);
			}
		};

		table.addColumn(checkboxColumn);

		/* Then the combo column */
		Column<MappingEntry, String> comboColumn = new Column<MappingEntry, String>(new SelectionCell(chromosomes))
		{
			@Override
			public String getValue(MappingEntry object)
			{
				return object.chromosome;
			}
		};
		comboColumn.setFieldUpdater((index, object, value) -> object.chromosome = value);

		table.addColumn(comboColumn, Text.LANG.markersColumnChromosome());

		/* The start column */
		final EditTextCell cell = new EditTextCell();
		Column<MappingEntry, String> startColumn = new Column<MappingEntry, String>(cell)
		{
			@Override
			public String getValue(MappingEntry row)
			{
				String value = row.start == null ? null : Long.toString(row.start);
				if (StringUtils.isEmpty(value))
					return SimpleHtmlTemplate.INSTANCE.text(Text.LANG.generalClickToEdit()).asString();
				else
					return SimpleHtmlTemplate.INSTANCE.text(value).asString();
			}
		};

		startColumn.setFieldUpdater((index, object, value) ->
		{
			try
			{
				object.start = Long.parseLong(value);
			}
			catch (NumberFormatException e)
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNotANumber());
				cell.clearViewData(object);
				table.redrawRow(index);
			}
		});

		table.addColumn(startColumn, Text.LANG.markersRegionStartPosition());

		/* The end column */
		Column<MappingEntry, String> endColumn = new Column<MappingEntry, String>(cell)
		{
			@Override
			public String getValue(MappingEntry row)
			{
				String value = row.end == null ? null : Long.toString(row.end);
				if (StringUtils.isEmpty(value))
					return SimpleHtmlTemplate.INSTANCE.text(Text.LANG.generalClickToEdit()).asString();
				else
					return SimpleHtmlTemplate.INSTANCE.text(value).asString();
			}
		};

		endColumn.setFieldUpdater((index, object, value) ->
		{
			try
			{
				object.end = Long.parseLong(value);
			}
			catch (NumberFormatException e)
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNotANumber());
				cell.clearViewData(object);
			}
		});

		table.addColumn(endColumn, Text.LANG.markersRegionEndPosition());
		table.addStyleName(Style.LAYOUT_NO_MARGIN);
	}

	/**
	 * Returns the selected {@link MapExportSettings} or <code>null</code> if no {@link TabBar.Tab} is selected or if
	 * the {@link DisclosurePanel} is not open at the point in time of calling this method.
	 *
	 * @return The selected {@link MapExportSettings}.
	 * @see DisclosurePanel#isOpen()
	 */
	public MapExportSettings getSelectedOption()
	{
		if (!collapsePanel.isIn())
			return null;

		if (chromosomeTab.isActive())
			return MapExportSettings.CHROMOSOME;
		else if (regionTab.isActive())
			return MapExportSettings.REGIONS;
		else if (intervalTab.isActive())
			return MapExportSettings.MARKER_INTERVAL;
		else if (radiusTab.isActive())
			return MapExportSettings.MARKER_RADIUS;
		else
			return null;
	}

	private MapExportOptions getOptions() throws InvalidOptionsException
	{
		if (!collapsePanel.isIn())
			return null;
		else
		{
			MapExportOptions options = new MapExportOptions();

			switch (getSelectedOption())
			{
				case CHROMOSOME:
					options.setChromosomes(getChromosomes());
					break;
				case REGIONS:
					options.setRegions(getRegions());
					break;
				case MARKER_INTERVAL:
					options.setInterval(getInterval());
					break;
				case MARKER_RADIUS:
					options.setRadius(getRadius());
					break;
			}

			return options;
		}
	}

	private List<String> getChromosomes() throws InvalidOptionsException
	{
		List<String> result = ListBoxUtils.getItemTexts(chromosomeBox);

		if (result == null || result.size() < 1)
			throw new InvalidOptionsException(Text.LANG.notificationMarkersExportOptionsFillInOrClose());

		return result;
	}

	/**
	 * Returns the user selected marker and {@link Region}
	 *
	 * @return The user selected marker and {@link Region}
	 * @throws InvalidOptionsException Thrown if the selected options are invalid
	 */
	public Tuple.Pair<String, Region> getRadius() throws InvalidOptionsException
	{
		String marker = radiusMarker.getText().trim();
		long left = radiusLeft.getLongValue();
		long right = radiusRight.getLongValue();

		if (StringUtils.isEmpty(marker))
			throw new InvalidOptionsException(Text.LANG.notificationMarkersExportOptionsFillInOrClose());

		return new Tuple.Pair<>(marker, new Region(left, right));
	}

	/**
	 * Returns the user entered values for the marker interval.
	 *
	 * @return The user entered values for the marker interval
	 * @throws InvalidOptionsException Thrown if the selected options are invalid
	 */
	public Tuple.Pair<String, String> getInterval() throws InvalidOptionsException
	{
		String first = intervalFirstMarker.getText().trim();
		String second = intervalSecondMarker.getText().trim();

		if (StringUtils.isEmpty(first) || StringUtils.isEmpty(second))
			throw new InvalidOptionsException(Text.LANG.notificationMarkersExportOptionsFillInOrClose());

		return new Tuple.Pair<>(first, second);
	}

	/**
	 * Returns the regions defined by the user
	 *
	 * @return The regions defined by the user
	 * @throws InvalidOptionsException Thrown if the selected options are invalid
	 */
	public java.util.Map<String, List<Region>> getRegions() throws InvalidOptionsException
	{
		String chromosome;
		Region region;

		java.util.Map<String, List<Region>> result = null;

		for (MappingEntry entry : dataProvider.getList())
		{
			if (entry == null || entry.start == null || entry.end == null)
				continue;

			chromosome = entry.chromosome;
			region = new Region();
			region.start = entry.start;
			region.end = entry.end;

			if (result == null)
				result = new HashMap<>();

			List<Region> regions = result.get(chromosome);
			if (regions == null)
				regions = new ArrayList<>();

			regions.add(region);
			result.put(chromosome, regions);
		}

		if (result == null || result.size() < 1)
			throw new InvalidOptionsException(Text.LANG.notificationMarkersExportOptionsFillInOrClose());

		return result;
	}

	@UiHandler("closeOptions")
	void onCloseClicked(ClickEvent event)
	{
		collapse(collapsePanel.getElement());
	}

	public void open()
	{
		if (!collapsePanel.isIn())
			collapsePanel.setIn(true);
	}

	@UiHandler("deleteRegion")
	void onDeleteRegionClicked(ClickEvent event)
	{
		/* Get the selected items */
		Set<MappingEntry> selectedItems = ((SetSelectionModel<MappingEntry>) table.getSelectionModel()).getSelectedSet();

		dataProvider.getList().removeAll(selectedItems);
		table.redraw();
	}

	@UiHandler("addRegion")
	void onAddRegionClicked(ClickEvent event)
	{
		dataProvider.getList().add(new MappingEntry(chromosomes.get(0), null, null));
		table.redraw();
	}

	public void addTableSelection(MappingEntry selection)
	{
		// Remove the dummy entry if it's there
		if (dataProvider.getList().size() == 1) {
			MappingEntry entry = dataProvider.getList().get(0);
			if (entry.start == null || entry.end == null)
				dataProvider.getList().clear();
		}

		// Add the new item, then redraw
		dataProvider.getList().add(selection);
		table.redraw();

		// Open the export options panel if it isn't already
		open();

		// Activate the regions tab if it isn't already
		if(!regionTab.isActive())
			regionTab.showTab(false);
	}

	private native void collapse(Element e)/*-{
		$wnd.jQuery(e).collapse('toggle');
	}-*/;

	/**
	 * The available export options: {@link #CHROMOSOME} {@link #REGIONS}, {@link #MARKER_INTERVAL}, {@link #MARKER_RADIUS}.
	 *
	 * @author Sebastian Raubach
	 */
	public enum MapExportSettings
	{
		CHROMOSOME,
		REGIONS,
		MARKER_INTERVAL,
		MARKER_RADIUS
	}

	interface MapsPageUiBinder extends UiBinder<FlowPanel, MapExportOptionsPanel>
	{
	}

	public static class MappingEntry
	{
		private String chromosome;
		private Long   start = null;
		private Long   end   = null;

		public MappingEntry(String chromosome, Long start, Long end)
		{
			this.chromosome = chromosome;
			this.start = start;
			this.end = end;
		}
	}
}