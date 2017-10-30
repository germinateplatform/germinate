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

package jhi.germinate.client.page.genotype;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class MapsPage extends Composite implements HasHyperlinkButton
{
	interface MapsPageUiBinder extends UiBinder<HTMLPanel, MapsPage>
	{
	}

	private static MapsPageUiBinder ourUiBinder = GWT.create(MapsPageUiBinder.class);

	@UiField
	HTML        text;
	@UiField
	FlowPanel   mapPanel;
	@UiField
	FlowPanel   mapDetailsWrapper;
	@UiField
	Heading     mapDetailsHeading;
	@UiField
	HTML        mapDetailsParagraph;
	@UiField
	SimplePanel markerTablePanel;
	@UiField
	SimplePanel markerDownloadPanel;

	@UiField
	Panel         exportOptionsPanel;
	@UiField
	PanelCollapse collapsePanel;
	@UiField
	HTML          chromosomeHtml;
	@UiField
	ListBox       chromosomeBox;
	@UiField
	HTML          regionHtml;
	@UiField
	TextBox       intervalFirstMarker;
	@UiField
	TextBox       intervalSecondMarker;
	@UiField
	HTML          intervalHtml;
	@UiField
	HTML          radiusHtml;
	@UiField
	HTML          panelHtml;
	@UiField
	Button        closeOptions;

	@UiField
	TabListItem chromosomeTab;
	@UiField
	TabListItem regionTab;
	@UiField
	TabListItem intervalTab;
	@UiField
	TabListItem radiusTab;

	@UiField
	TextBox           radiusMarker;
	@UiField
	RangedLongTextBox radiusLeft;
	@UiField
	RangedLongTextBox radiusRight;

	@UiField
	SimplePanel regionTable;
	/* Range controls */
	private CellTable<MappingEntry>        table;
	private ListDataProvider<MappingEntry> dataProvider;
	private List<String>                   chromosomes;

	private Map                map;
	private MapDefinitionTable mapDefinitionTable;

	public MapsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		text.setHTML(Text.LANG.mapsParagraph());
		mapDetailsParagraph.setHTML(Text.LANG.mapsMarkersParagraph());

		mapPanel.add(new MapTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Map>>> callback)
			{
				return MapService.Inst.get().get(Cookie.getRequestProperties(), null, pagination, callback);
			}

			@Override
			protected void onItemSelected(NativeEvent event, Map object, int column)
			{
				super.onItemSelected(event, object, column);

				map = object;

				updateMapDetails();
			}
		});

		Long mapId = LongParameterStore.Inst.get().get(Parameter.mapId);
		if (mapId != null)
		{
			MapService.Inst.get().getById(Cookie.getRequestProperties(), mapId, new DefaultAsyncCallback<ServerResult<Map>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Map> result)
				{
					if (result.getServerResult() != null)
					{
						map = result.getServerResult();
						updateMapDetails();
					}
				}
			});
		}

		String[] files = new String[]{Text.LANG.downloadInFlapjackFormat(), Text.LANG.downloadInMapChartFormat(), Text.LANG.downloadInStrudelFormat()};
		FileType[] fileTypes = new FileType[]{FileType.map, FileType.mct, FileType.strudel};

		FileDownloadWidget widget = new OnDemandFileDownloadWidget((index, callback) ->
		{
			MapFormat format;

			switch (index)
			{
				case 0:
					format = MapFormat.flapjack;
					break;
				case 1:
					format = MapFormat.mapchart;
					break;
				case 2:
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
		}, true)
				.setHeading(null)
				.setFiles(Arrays.asList(files))
				.setTypes(Arrays.asList(fileTypes));

		markerDownloadPanel.add(widget);

		panelHtml.setHTML(Text.LANG.markersExportOptionsText());
		chromosomeHtml.setHTML(Text.LANG.markersExportOptionsChromosomesText());
		regionHtml.setHTML(Text.LANG.markersExportOptionsRegionText());
		intervalHtml.setHTML(Text.LANG.markersExportOptionsIntervalText());
		radiusHtml.setHTML(Text.LANG.markersExportOptionsRadiusText());

		MapService.Inst.get().getChromosomesForMap(Cookie.getRequestProperties(), LongParameterStore.Inst.get().get(Parameter.mapId),
				new DefaultAsyncCallback<ServerResult<List<String>>>()
				{
					@Override
					public void onSuccessImpl(ServerResult<List<String>> result)
					{
						if (result.getServerResult() != null)
						{
							chromosomes = result.getServerResult();
							exportOptionsPanel.setVisible(true);
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
		table = new CellTable<>(DatabaseObjectPaginationTable.DEFAULT_NR_OF_ITEMS_PER_PAGE);
		table.setBordered(true);
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

	private void updateMapDetails()
	{
		mapDetailsHeading.setText(Text.LANG.mapsHeadingMarkers(HTMLUtils.stripHtmlTags(map.getDescription())));
		mapDetailsWrapper.setVisible(true);

		if (mapDefinitionTable == null)
		{
			mapDefinitionTable = new MapDefinitionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
			{
				@Override
				public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
				{
					filter = addToFilter(filter);

					MarkerService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
				}

				@Override
				public boolean supportsFullIdMarking()
				{
					return true;
				}

				@Override
				protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<MapDefinition>>> callback)
				{
					filter = addToFilter(filter);
					return MarkerService.Inst.get().getMapDefinitionForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
				}

				private PartialSearchQuery addToFilter(PartialSearchQuery filter)
				{
					try
					{
						if (filter == null)
							filter = new PartialSearchQuery();
						SearchCondition condition = new SearchCondition();
						condition.setColumnName(Map.ID);
						condition.setComp(new Equal());
						condition.addConditionValue(Long.toString(map.getId()));
						condition.setType(Long.class.getSimpleName());
						filter.add(condition);

						if (filter.getAll().size() > 1)
							filter.addLogicalOperator(new And());
					}
					catch (InvalidArgumentException | InvalidSearchQueryException e)
					{
						e.printStackTrace();
					}

					return filter;
				}

				@Override
				protected boolean supportsFiltering()
				{
					return true;
				}
			};
			markerTablePanel.add(mapDefinitionTable);
		}
		else
		{
			mapDefinitionTable.refreshTable();
		}
	}

	/**
	 * Returns the selected {@link MapExportSettings} or <code>null</code> if no {@link com.google.gwt.user.client.ui.TabBar.Tab} is selected or if
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

	private native void collapse(Element e)/*-{
		var target = this;
		var $collapse = $wnd.jQuery(e);

		$collapse.collapse('toggle');
	}-*/;

	/**
	 * The available export options: {@link #REGIONS}, {@link #MARKER_INTERVAL}, {@link #MARKER_RADIUS}.
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

	private class MappingEntry
	{
		private String chromosome;
		private Long start = null;
		private Long end   = null;

		MappingEntry(String chromosome, Long start, Long end)
		{
			this.chromosome = chromosome;
			this.start = start;
			this.end = end;
		}
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.MAP_DETAILS)
				.addParam(Parameter.mapId);
	}
}