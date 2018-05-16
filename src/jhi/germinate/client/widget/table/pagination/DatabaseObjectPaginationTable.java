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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import java.util.*;
import java.util.Map;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.client.widget.table.pagination.filter.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DatabaseObjectPaginationTable<T extends DatabaseObject> extends Composite
{
	interface DatabaseObjectPaginationTableUiBinder extends UiBinder<HTMLPanel, DatabaseObjectPaginationTable>
	{
	}

	private static DatabaseObjectPaginationTableUiBinder ourUiBinder = GWT.create(DatabaseObjectPaginationTableUiBinder.class);

	public static final int DEFAULT_NR_OF_ITEMS_PER_PAGE = 25;

	@UiField
	HTMLPanel panel;

	@UiField
	FlowPanel filterDisplay;

	@UiField
	FlowPanel                topPanel;
	@UiField
	ButtonGroup              filterPlaceholder;
	@UiField
	Button                   filterButton;
	@UiField
	Button                   clearFilterButton;
	@UiField
	BootstrapPager           topPager;
	@UiField
	ButtonGroup              columnSelectorButton;
	@UiField
	NonAutoCloseDropDownMenu columnSelectorMenu;

	@UiField(provided = true)
	CellTable<T> table;

	@UiField
	FlowPanel      bottomPanel;
	@UiField
	ButtonGroup    extrasPlaceholder;
	@UiField
	Button         downloadButton;
	@UiField
	BootstrapPager bottomPager;

	@UiField
	Heading noDataHeading;

	// CONFIGURATION
	private SelectionMode selectionMode = SelectionMode.NONE;
	boolean sortingEnabled = true;
	private   boolean hideEmptyTable         = true;
	protected boolean preventInitialDataLoad = false;
	private   int     nrOfItemsPerPage       = IntegerParameterStore.Inst.get().get(Parameter.paginationPageSize, DEFAULT_NR_OF_ITEMS_PER_PAGE);

	// CURRENT STATE
	//	private Map<DatabaseObjectFilterColumn<T, ?>, FilterCellCallback<T>> filterCallbacks = new HashMap<>();
	private List<FilterRow.Column>          columns                  = new ArrayList<>();
	private RefreshableAsyncDataProvider<T> dataProvider;
	private Map<String, ClickHandler>       columnVisibilityHandlers = new HashMap<>();
	private ContextMenuHandler<T>           contextMenuHandler;
	private SelectionModel<T>               selectionModel;
	private MultiPageBooleanHeader          selectPageHeader;
	private HandlerRegistration             tableColumnVisibilityHandler;
	private HandlerRegistration             tableRowCountChangeHandler;
	private int                             rangeStart               = 0;

	// SERVER COMMUNICATION
	private Request    currentRequest;
	private Pagination pagination = new Pagination(0, Integer.MAX_VALUE);

	// OTHER THINGS
	private FilterPanel filterPanel = new FilterPanel();
	private PopupPanel  tooltipPanel;

	public DatabaseObjectPaginationTable()
	{
		this(SelectionMode.NONE, true);
	}

	/**
	 * Creates a new instance of {@link DatabaseObjectPaginationTable}
	 */
	public DatabaseObjectPaginationTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		this.selectionMode = selectionMode;
		this.sortingEnabled = sortingEnabled;

		table = new CellTable<T>(nrOfItemsPerPage);

		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@UiHandler("clearFilterButton")
	void onClearFilterButtonClicked(ClickEvent event)
	{
		filterPanel.clear();
		filterButton.setType(ButtonType.DEFAULT);
		clearFilterButton.setVisible(false);
		refreshTable();
	}

	@UiHandler("filterButton")
	void onFilterButtonClicked(ClickEvent event)
	{
		toggleFilter();
	}

	@UiHandler("downloadButton")
	void onDownloadButtonClicked(ClickEvent event)
	{
		PartialSearchQuery filterObject = null;

		if (supportsFiltering())
		{
			filterObject = getSearchFilter(false);
		}

		download(filterObject, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				if (!StringUtils.isEmpty(result.getServerResult()))
				{
					String path = new ServletConstants.Builder()
							.setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

					JavaScript.invokeDownload(path);
				}
			}
		});
	}

	@Override
	protected void onUnload()
	{
		// Make sure the popup is hidden when the table is unloaded (removed from its parent)
		if (tooltipPanel != null)
			tooltipPanel.hide();

		tooltipPanel = null;

		if (tableColumnVisibilityHandler != null)
		{
			tableColumnVisibilityHandler.removeHandler();
			tableColumnVisibilityHandler = null;
		}

		if (tableRowCountChangeHandler != null)
		{
			tableRowCountChangeHandler.removeHandler();
			tableRowCountChangeHandler = null;
		}

		if (filterPanel != null)
			filterPanel.onUnload();

		super.onUnload();
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		String id = "table-" + String.valueOf(Math.abs(RandomUtils.RANDOM.nextLong()));
		table.getElement().setId(id);

		table.setLoadingIndicator(new LoadingSpinner());
		table.getLoadingIndicator().getParent().getElement().getStyle().setProperty("minHeight", "300px");
		table.getLoadingIndicator().getParent().getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.RELATIVE);

		topPager.setDisplay(table);
		bottomPager.setDisplay(table);

		initTable();

		applyHeaderStyles();

		if (!supportsDownload())
			downloadButton.removeFromParent();

		if (columnVisibilityHandlers.size() < 1)
			columnSelectorButton.setVisible(false);

		tableColumnVisibilityHandler = GerminateEventBus.BUS.addHandler(TableColumnVisibilityChangeEvent.TYPE, e ->
		{
			if (!StringUtils.areEqual(e.getSourceId(), getId()))
			{
				ClickHandler handler = columnVisibilityHandlers.get(e.getColumnStyle());
				if (handler != null)
					handler.onClick(null);
			}
		});

		tableRowCountChangeHandler = GerminateEventBus.BUS.addHandler(TableRowCountChangeEvent.TYPE, e ->
		{
			int value = IntegerParameterStore.Inst.get().get(Parameter.paginationPageSize, BootstrapPager.DEFAULT_PAGE_SIZE);
			setPageSize(value);
			refreshTable();
		});

		onPostLoad();
	}

	protected void onPostLoad()
	{
	}

	/**
	 * Applies the style to each individual column header
	 */
	private void applyHeaderStyles()
	{
		for (int i = 0; i < table.getColumnCount(); i++)
		{
			Column<T, ?> column = table.getColumn(i);

			String style = null;

			if (column instanceof DatabaseObjectFilterColumn)
			{
				style = ((DatabaseObjectFilterColumn) column).getHeaderStyle();
			}

			if (!StringUtils.isEmpty(style))
				table.getHeader(i).setHeaderStyleNames(style);
		}
	}

	/**
	 * Updates the table with the new information
	 */
	private void initTable()
	{
		if (selectionMode != SelectionMode.NONE)
		{
			addCheckboxColumn();
		}

		createColumns();
		filterPanel.update(columns);
		filterPanel.addFilterPanelHandler(new FilterPanel.FilterPanelHandler()
		{
			@Override
			public void onRowAdded()
			{

			}

			@Override
			public void onRowDeleted()
			{
				if (filterPanel.getSize() < 1)
					filterButton.click();
			}

			@Override
			public void onSearchClicked()
			{
				filterButton.setType(ButtonType.SUCCESS);
				refreshTable();
			}

			@Override
			public void onClearClicked()
			{
				clearFilter();
			}
		});

		if (sortingEnabled)
		{
			// Add a ColumnSortEvent.AsyncHandler to connect sorting to the AsyncDataProvider
			ColumnSortEvent.AsyncHandler columnSortHandler = new ColumnSortEvent.AsyncHandler(table);
			table.addColumnSortHandler(columnSortHandler);
		}

		if (supportsFiltering())
			filterPlaceholder.setVisible(true);

		fetchTableData();
	}

	public void clearFilter()
	{
		filterPanel.clear();
		filterButton.setType(ButtonType.DEFAULT);
		clearFilterButton.setVisible(false);
		refreshTable();
	}

	public void toggleFilter()
	{
//		filterVisible = !filterVisible;

		filterPanel.setVisible(true);

//		if (!filterVisible)
//			refreshTable();
	}

	public boolean isFiltered()
	{
		return filterPanel.getSize() > 0;
	}

	public boolean forceFilter(FilterPanel.FilterMapping columnToValue, boolean isAnd) throws InvalidArgumentException
	{
		return forceFilter(columnToValue, isAnd, new Equal());
	}

	public boolean forceFilter(FilterPanel.FilterMapping columnToValue, boolean isAnd, ComparisonOperator operator) throws InvalidArgumentException
	{
		/* Cancel any currently running request */
		if (currentRequest != null && currentRequest.isPending())
			currentRequest.cancel();

		filterPanel.setVisible(false);
		filterPanel.add(columnToValue, isAnd, operator);
		filterButton.setType(ButtonType.SUCCESS);
		refreshTable();

		return true;
	}

	public void refreshTable()
	{
		if (selectionModel != null)
		{
			if (selectionModel instanceof SingleSelectionModel)
				((SingleSelectionModel) selectionModel).clear();
			else if (selectionModel instanceof MultiSelectionModel)
			{
				((MultiSelectionModel) selectionModel).clear();
				selectPageHeader.setValue(0);
			}
		}

		/* We need to fetch the result size again */
		pagination.setResultSize(null);

		/* Then let the table know, we're starting on page 1 (cause filter may have changed and the number of pages as well), then fire event */
		table.setVisibleRangeAndClearData(new Range(0, nrOfItemsPerPage), true);
	}

	/**
	 * Adds a checkbox column as the first column of the table. This column represents the {@link #selectionModel}.
	 */
	private void addCheckboxColumn()
	{
		ProvidesKey<T> keyProvider = DatabaseObject::getId;

		/* Take care of selection */
		if (selectionMode == SelectionMode.SINGLE)
			selectionModel = new SingleSelectionModel<>(keyProvider);
		else
			selectionModel = new MultiSelectionModel<>(keyProvider);

		setSelectionModel(selectionModel, DefaultSelectionEventManager.createCheckboxManager());

		/* Checkbox cell */
		CheckboxCell cell = new CheckboxCell(true, false);

		Column<T, Boolean> checkboxColumn = new Column<T, Boolean>(cell)
		{
			@Override
			public String getCellStyleNames(Cell.Context context, T object)
			{
				return Style.combine(Style.TEXT_CENTER_ALIGN, super.getCellStyleNames(context, object));
			}

			@Override
			public Boolean getValue(T object)
			{
				return selectionModel.isSelected(object);
			}
		};

		if (selectionMode == SelectionMode.SINGLE)
		{
			addColumn(checkboxColumn, "", false);
		}
		else
		{
			/* Create a checkbox header to select all items */
			selectPageHeader = new MultiPageBooleanHeader(cell)
			{
				@Override
				public Boolean getValue()
				{
					int selected = ((MultiSelectionModel<T>) selectionModel).getSelectedSet().size();
					int size = getSize();
					return selected != 0 ? size == selected : false;
				}

				@Override
				public String getHeaderStyleNames()
				{
					return Style.combine(Style.TEXT_CENTER_ALIGN, super.getHeaderStyleNames());
				}
			};

			/*
			 * Add an updater that changes the selection state of the table
			 * items
			 */
			selectPageHeader.setUpdater(value ->
			{
				/*
				 * Determine the name of the database column to sort by and the
				 * direction
				 */
				ColumnSortList sortList = table.getColumnSortList();
				String sortColumnName = (sortList != null && sortList.size() > 0) ? sortList.get(0).getColumn().getDataStoreName() : null;
				boolean ascending = (sortList != null && sortList.size() > 0) ? sortList.get(0).isAscending() : true;

				PartialSearchQuery filterObject = null;

				if (supportsFiltering())
				{
					filterObject = getSearchFilter(false);
				}

				/* Now, we need to get ALL the database items that are in this table, no matter on which page to be able to select them. */
				getData(new Pagination(0, Integer.MAX_VALUE, sortColumnName, ascending), filterObject, new DefaultAsyncCallback<PaginatedServerResult<List<T>>>(true)
				{
					@Override
					protected void onSuccessImpl(PaginatedServerResult<List<T>> result)
					{
						selectPageHeader.setValue(result.getResultSize());

						for (T item : result.getServerResult())
						{
							selectionModel.setSelected(item, value);
						}
					}
				});
			});

			/* Add the new column */
			addColumn(checkboxColumn, selectPageHeader);
		}
	}

	private void updatePanels(boolean filterApplied, Integer resultSize)
	{
		if (pagination.getResultSize() != null)
		{
			if (!Objects.equals(pagination.getResultSize(), resultSize))
				Notification.notify(Notification.Type.WARNING, Text.LANG.notificationIncinsistancyCountResult());

			return;
		}
		else
		{
			pagination.setResultSize(resultSize);
		}

		Integer nrOfItems = pagination.getResultSize();

		table.setPageSize(nrOfItemsPerPage);
		pagination.setResultSize(resultSize);

		topPager.setVisible(nrOfItems > nrOfItemsPerPage);
		bottomPager.setVisible(nrOfItems > nrOfItemsPerPage);

		table.setVisible(true);
		topPanel.setVisible(true);
		bottomPanel.setVisible(true);
		extrasPlaceholder.setVisible(true);

		if (nrOfItems == 0)
		{
			table.setRowCount(nrOfItems, true);
			if (filterApplied)
			{
				table.setVisible(true);
				topPanel.setVisible(true);
				bottomPanel.setVisible(true);
				dataProvider.updateRowData(0, new ArrayList<>());
			}
			else if (hideEmptyTable)
			{
				table.setVisible(false);
				topPanel.setVisible(false);
				bottomPanel.setVisible(false);
				extrasPlaceholder.setVisible(false);
				noDataHeading.setVisible(true);
			}
		}
		else if (nrOfItems == -1)
		{
			table.setRowCount(Integer.MAX_VALUE, false);
			table.setPageSize(Integer.MAX_VALUE);
			fetchTableData();
		}
		else if (nrOfItems < rangeStart)
		{
			topPager.setPageStart(0);
		}
	}

	/**
	 * Initializes the async data provider that fills the table with data from the server
	 */
	private void fetchTableData()
	{
		if (dataProvider == null)
		{
			/* Set up a async data provider */
			dataProvider = new RefreshableAsyncDataProvider<T>()
			{
				/* Get the ColumnSortInfo from the table */
				private ColumnSortList sortList = table.getColumnSortList();

				@Override
				protected void onRangeChanged(HasData<T> display)
				{
					if (preventInitialDataLoad)
					{
						preventInitialDataLoad = false;
						return;
					}

					/* Get the start and length of the current page */
					final Range range = display.getVisibleRange();
					rangeStart = range.getStart();
					final int length = display.getVisibleRange().getLength();

					/* Determine the name of the database column to sort by and the direction */
					String sortColumnName = (sortList != null && sortList.size() > 0) ? sortList.get(0).getColumn().getDataStoreName() : null;
					boolean ascending = (sortList != null && sortList.size() > 0) ? sortList.get(0).isAscending() : true;

					if (table.getLoadingIndicator() != null)
					{
						/* Prevent "jumping" of the page by explicitly setting the height of the loading widget to the size of the current page */
						int height = table.getBodyHeight() - 20;
						if (height > 0)
						{
							Widget loadingIndicator = table.getLoadingIndicator();
							Widget parent = loadingIndicator.getParent();

							parent.setHeight(height + "px");
						}
					}

					PartialSearchQuery filterObject = null;

					if (supportsFiltering())
						filterObject = getSearchFilter(true);

					final boolean filterApplied = filterObject != null;

					table.setVisibleRangeAndClearData(table.getVisibleRange(), false);

					/* Set up the callback object */
					currentRequest = getData(pagination.update(rangeStart, length, sortColumnName, ascending), filterObject, new DefaultAsyncCallback<PaginatedServerResult<List<T>>>()
					{
						@Override
						public void onFailureImpl(Throwable caught)
						{
							updateRowData(0, new ArrayList<>());

							super.onFailureImpl(caught);
						}

						@Override
						public void onSuccessImpl(PaginatedServerResult<List<T>> result)
						{
							List<T> data = result.getServerResult();

							noDataHeading.setVisible(false);

							if (data == null)
							{
								if (filterApplied)
									table.redrawHeaders();

								table.setRowCount(0, true);
								updateRowData(rangeStart, new ArrayList<>());
								topPager.setVisible(false);
								bottomPager.setVisible(false);
								topPanel.setVisible(true);
								bottomPanel.setVisible(true);

								updatePanels(filterApplied, result.getResultSize());

								if (!filterApplied && hideEmptyTable)
								{
									table.setVisible(false);
									topPanel.setVisible(false);
									bottomPanel.setVisible(false);
									noDataHeading.setVisible(true);
								}
							}
							else
							{
								/* Tell the header how many items there are on this page */
								if (selectPageHeader != null)
									selectPageHeader.setValue(result.getResultSize());

								updatePanels(filterApplied, result.getResultSize());

								table.setRowCount(pagination.getResultSize(), true);
								/* Show debug information */

								/* Update table */
								updateRowData(rangeStart, data);

								onDataChanged();
							}
						}
					});
				}
			};

			/* Connect the table to the data provider */
			dataProvider.addDataDisplay(table);

		}
		else
		{
			dataProvider.refresh(table);
		}
	}

	protected void onDataChanged()
	{

	}

	private void displayFilter(FlowPanel filterObject)
	{
		filterDisplay.clear();
		filterDisplay.add(filterObject);
		clearFilterButton.setVisible(filterObject != null && filterObject.getWidgetCount() > 0);
	}

	/**
	 * Adds a {@link com.google.gwt.view.client.CellPreviewEvent.Handler} to the table that calls the parameter {@link TableMouseHoverHandler}
	 *
	 * @param handler The {@link TableMouseHoverHandler} that is called upon 'mouseover' and 'mouseout'.
	 */
	public void addMouseHoverHandler(final TableMouseHoverHandler<T> handler)
	{
		table.sinkEvents(Event.ONMOUSEOUT);
		table.sinkEvents(Event.ONMOUSEOVER);
		table.addHandler(event -> handler.onMouseOutTable(), MouseOutEvent.getType());
		table.addHandler(event -> handler.onMouseOverTable(), MouseOverEvent.getType());

		table.addCellPreviewHandler(event ->
		{
			T row = event.getValue();
			if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType().toLowerCase()))
			{
				handler.onMouseOverRow(row);
			}
			else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType().toLowerCase()))
			{
				handler.onMouseOutRow(row);
			}
		});
	}

	/**
	 * Adds the column style names based on the database column that is associated with it.
	 *
	 * @param column The {@link Column} to add the style to
	 * @param header The {@link Header} to add the style to
	 */
	private void setColumnStyleName(String headerString, DatabaseObjectFilterColumn<T, ?> column, Header<?> header)
	{
		Class type = column.getType();
		String headerStyle = ClassUtils.isNumeric(type) ? Style.TEXT_RIGHT_ALIGN : "";

		if (ClassUtils.isNumeric(type))
		{
			header.setHeaderStyleNames(Style.combine(header.getHeaderStyleNames(), headerStyle));
			column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		}

		column.setCellStyleNames(column.getCellStyle());

		if (!StringUtils.isEmpty(column.getDataStoreName()))
		{
			String cellStyle = "gm8-col-" + getClassName() + "-" + column.getDataStoreName().replace(".", "-").replace("_", "-");

			boolean hidden = StringListParameterStore.Inst.get().contains(Parameter.invisibleTableColumns, cellStyle);
			CheckboxListItem item = new CheckboxListItem(true, headerString);
			columnSelectorMenu.add(item);
			ClickHandler h = event ->
			{
				if (event == null)
					item.setValue(!item.getValue());

				if (item.getValue())
				{
					StringListParameterStore.Inst.get().remove(Parameter.invisibleTableColumns, cellStyle);
					column.setCellStyleNames(column.getCellStyle());
					header.setHeaderStyleNames(Style.combine(headerStyle, column.getHeaderStyle()));
				}
				else
				{
					StringListParameterStore.Inst.get().add(Parameter.invisibleTableColumns, cellStyle);
					column.setCellStyleNames(Style.combine(Style.LAYOUT_DISPLAY_NONE, column.getCellStyle()));
					header.setHeaderStyleNames(Style.combine(headerStyle, Style.LAYOUT_DISPLAY_NONE, column.getHeaderStyle()));
				}

				table.redraw();

				if (event != null)
					GerminateEventBus.BUS.fireEvent(new TableColumnVisibilityChangeEvent(getId(), cellStyle));
			};
			item.addClickHandler(h);

			if (hidden)
				Scheduler.get().scheduleDeferred(() -> h.onClick(null));

			columnVisibilityHandlers.put(cellStyle, h);
		}
	}

	/**
	 * Adds the given column to the table
	 *
	 * @param column       The column
	 * @param headerString The column header text
	 * @param sortable     Should this column be sortable?
	 */
	public void addColumn(final DatabaseObjectFilterColumn<T, ?> column, final String headerString, final boolean sortable)
	{
		addColumn(column, headerString, sortable, true);
	}

	/**
	 * Adds the given column to the table
	 *
	 * @param column       The column
	 * @param headerString The column header text
	 * @param sortable     Should this column be sortable?
	 */
	@SuppressWarnings("unchecked")
	public void addColumn(final DatabaseObjectFilterColumn<T, ?> column, final String headerString, final boolean sortable, final boolean filterable)
	{
		if (supportsFiltering() && filterable)
			columns.add(new FilterRow.Column(column.getDataStoreName(), headerString, column.getType()));

		table.addColumn(column, headerString);

		if (sortable)
			addSortBits(column);

		setColumnStyleName(headerString, column, table.getHeader(table.getColumnCount() - 1));
	}

	protected void addColumn(Column<T, ?> column, String headerString)
	{
		table.addColumn(column, headerString);
	}

	protected void addColumn(Column<T, ?> column, String headerString, boolean sortable)
	{
		addColumn(column, headerString);

		if (sortable)
			addSortBits(column);
	}

	/**
	 * Adds the given column to the table. The Header parameter is used as the column header.
	 *
	 * @param column The column
	 * @param header The column header
	 */

	public void addColumn(Column<T, ?> column, Header<?> header)
	{
		table.addColumn(column, header);
	}

	protected PartialSearchQuery getSearchFilter(boolean forDisplay)
	{
		if (!supportsFiltering())
			return null;

		if (forDisplay)
			displayFilter(filterPanel.getQueryHtml());


		return filterPanel.getQuery();
	}

	private void addSortBits(Column<T, ?> column)
	{
		/* Set up sorting */
		if (sortingEnabled)
			column.setSortable(true);
	}

	/**
	 * Returns an unmodifiable list of the visible table items
	 *
	 * @return An unmodifiable list of the visible table items
	 */
	public List<T> getVisibleItems()
	{
		return Collections.unmodifiableList(table.getVisibleItems());
	}

	protected void setSelectionModel(SelectionModel<T> selectionModel, DefaultSelectionEventManager<T> checkboxManager)
	{
		table.setSelectionModel(selectionModel, checkboxManager);
	}

	public void setContextMenuHandler(ContextMenuHandler<T> contextMenuHandler)
	{
		this.contextMenuHandler = contextMenuHandler;
	}

	public Set<T> getSelection()
	{
		return ((SetSelectionModel<T>) selectionModel).getSelectedSet();
	}

	public void setSelection(Collection<T> items)
	{
		SetSelectionModel<T> model = (SetSelectionModel<T>) selectionModel;
		model.clear();

		for (T item : items)
			model.setSelected(item, true);
	}

	public void addExtraContent(Widget widget)
	{
		Scheduler.get().scheduleDeferred(() -> extrasPlaceholder.add(widget));
	}

	public void setHideEmptyTable(boolean hideEmptyTable)
	{
		this.hideEmptyTable = hideEmptyTable;
	}

	public CellTable<T> getTable()
	{
		return table;
	}

	/**
	 * Sets the number of items per page to the given value.
	 *
	 * @param value The number of items per page
	 */
	public void setPageSize(Integer value)
	{
		nrOfItemsPerPage = value;
		table.setPageSize(value);
	}

	public HTMLPanel getPanel()
	{
		return panel;
	}

	/**
	 * Redraws the table
	 */
	public void redraw()
	{
		table.redraw();
	}

	/**
	 * Redraws a given row identified by the relative index.
	 *
	 * @param relativeIndex The relative index of the row in the table
	 */
	public void redrawRow(int relativeIndex)
	{
		table.redrawRow(relativeIndex + table.getPageStart());
	}

	/**
	 * Returns the id of the table
	 *
	 * @return The id of the table
	 */
	public String getId()
	{
		if (table == null || !isAttached())
		{
			Notification.notify(Notification.Type.ERROR, "Table has to be added to a parent before calling getId()");
			throw new RuntimeException("Table has to be added to a parent before calling getId()");
		}
		return table.getElement().getId();
	}

	protected abstract String getClassName();

	/**
	 * Creates the columns and adds them to the table
	 */
	protected abstract void createColumns();

	/**
	 * Returns <code>true</code> if filtering is supported
	 *
	 * @return <code>true</code> if filtering is supported
	 */
	protected abstract boolean supportsFiltering();

	/**
	 * Returns <code>true</code> if downloading is supported
	 *
	 * @return <code>true</code> if downloading is supported
	 */
	protected abstract boolean supportsDownload();

	/**
	 * Queries the database for an actual chunk of data
	 *
	 * @param pagination The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} reflecting the user filters
	 * @param callback   The {@link AsyncCallback} that is called with the result
	 */
	protected abstract Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<T>>> callback);

	/**
	 * Initiates the data download with the given {@link PartialSearchQuery}
	 *
	 * @param filter   The {@link PartialSearchQuery} reflecting the user filters
	 * @param callback The {@link AsyncCallback} that is called with the result
	 */
	protected abstract void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Called when the table selection changes.
	 *
	 * @param event  The {@link NativeEvent} of the selection
	 * @param object The selected object
	 * @param column The column index of the selection
	 */
	protected abstract void onItemSelected(NativeEvent event, T object, int column);

	protected class ClickableTableCell extends SafeHtmlCell
	{
		/**
		 * Tell the cell which events to handle
		 */
		@Override
		public Set<String> getConsumedEvents()
		{
			HashSet<String> events = new HashSet<>();
			events.add(BrowserEvents.CLICK);
			events.add(BrowserEvents.CONTEXTMENU);
			return events;
		}
	}

	protected class ClickableTextCell extends TextCell
	{
		@Override
		public Set<String> getConsumedEvents()
		{
			HashSet<String> events = new HashSet<>();
			events.add(BrowserEvents.CONTEXTMENU);
			return events;
		}
	}

	public abstract class SafeHtmlColumn extends DatabaseObjectFilterColumn<T, SafeHtml>
	{
		/**
		 * Construct a new Column with a {@link ClickableTableCell}.
		 */
		public SafeHtmlColumn()
		{
			super(new ClickableTableCell());
		}

		@Override
		public String getHeaderStyle()
		{
			return null;
		}

		@Override
		public String getCellStyle()
		{
			return null;
		}

		@Override
		public void onBrowserEvent(Cell.Context context, Element elem, T object, NativeEvent event)
		{
			/* Handle context menu events */
			if (BrowserEvents.CONTEXTMENU.equals(event.getType()))
			{
				/* Let the child classes decide what to do now */
				boolean preventDefault = false;

				if (contextMenuHandler != null)
					preventDefault = contextMenuHandler.handleContextMenuEvent(object, event.getClientX(), event.getClientY(), false);

				if (preventDefault)
					event.preventDefault();
			}
			else
			{
				super.onBrowserEvent(context, elem, object, event);
			}
		}
	}

	public abstract class TextColumn extends DatabaseObjectFilterColumn<T, String>
	{
		/**
		 * Construct a new Column with a {@link ClickableTableCell}.
		 */
		public TextColumn()
		{
			super(new ClickableTextCell());
		}

		@Override
		public String getHeaderStyle()
		{
			return null;
		}

		@Override
		public String getCellStyle()
		{
			return null;
		}

		@Override
		public void onBrowserEvent(Cell.Context context, Element elem, T object, NativeEvent event)
		{
			/* Handle context menu events */
			if (BrowserEvents.CONTEXTMENU.equals(event.getType()))
			{
				/* Let the child classes decide what to do now */
				boolean preventDefault = false;

				if (contextMenuHandler != null)
					preventDefault = contextMenuHandler.handleContextMenuEvent(object, event.getClientX(), event.getClientY(), false);

				if (preventDefault)
					event.preventDefault();
			}
			else
			{
				super.onBrowserEvent(context, elem, object, event);
			}
		}
	}

	protected abstract class ClickableSafeHtmlColumn extends DatabaseObjectFilterColumn<T, SafeHtml>
	{
		/**
		 * Construct a new Column with a {@link ClickableTableCell}.
		 */
		public ClickableSafeHtmlColumn()
		{
			super(new ClickableTableCell());
		}

		@Override
		public String getHeaderStyle()
		{
			return null;
		}

		@Override
		public String getCellStyle()
		{
			return null;
		}

		@Override
		public void onBrowserEvent(Cell.Context context, Element elem, T object, NativeEvent event)
		{
			/* We want to handle click events */
			if (BrowserEvents.CLICK.equals(event.getType()))
			{
				onItemSelected(event, object, context.getColumn());

				if (!event.getCtrlKey() && !event.getShiftKey() && !event.getMetaKey())
					super.onBrowserEvent(context, elem, object, event);
			}
			/* Handle context menu events */
			else if (BrowserEvents.CONTEXTMENU.equals(event.getType()))
			{
				NodeList<Element> children = elem.getElementsByTagName("a");

				/* Determine if the click happened on an anchor element */
				boolean anchorClicked = false;

				for (int i = 0; i < children.getLength(); i++)
				{
					Element child = children.getItem(i);

					anchorClicked |= child.getAbsoluteLeft() <= event.getClientX() && child.getAbsoluteRight() > event.getClientX();
				}

				/* Let the child classes decide what to do now */
				boolean preventDefault = false;

				if (contextMenuHandler != null)
					preventDefault = contextMenuHandler.handleContextMenuEvent(object, event.getClientX(), event.getClientY(), anchorClicked);

				if (preventDefault)
					event.preventDefault();
				else
					onItemSelected(event, object, context.getColumn());
			}

			super.onBrowserEvent(context, elem, object, event);
		}
	}

	public interface ContextMenuHandler<T extends DatabaseObject>
	{
		/**
		 * This method will handle context menu events. Return <code>true</code> to prevent the browser default.
		 *
		 * @param row           The row element the mouse hovers over. Can be <code>null</code>; in that case the menu options for individuals won't
		 *                      be shown.
		 * @param x             The x position of the event
		 * @param y             The y position of the event
		 * @param anchorClicked Did the click happen on the anchor element?
		 * @return <code>true</code> to prevent the browser default
		 */
		boolean handleContextMenuEvent(T row, int x, int y, boolean anchorClicked);
	}

	public enum SelectionMode
	{
		NONE,
		SINGLE,
		MULTI
	}
}