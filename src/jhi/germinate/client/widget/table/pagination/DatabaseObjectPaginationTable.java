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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.query.client.*;
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
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;
import java.util.Map;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.CompositeCell;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.client.widget.table.pagination.cell.*;
import jhi.germinate.client.widget.table.pagination.resource.*;
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
public abstract class DatabaseObjectPaginationTable<T extends DatabaseObject> extends Composite implements FilterCell.VisibilityCallback
{
	interface DatabaseObjectPaginationTableUiBinder extends UiBinder<HTMLPanel, DatabaseObjectPaginationTable>
	{
	}

	private static DatabaseObjectPaginationTableUiBinder ourUiBinder = GWT.create(DatabaseObjectPaginationTableUiBinder.class);

	public static final int DEFAULT_NR_OF_ITEMS_PER_PAGE = 25;

	@UiField
	HTMLPanel panel;

	@UiField
	HTML filterInfo;

	@UiField
	FlowPanel      topPanel;
	@UiField
	FlowPanel      filterPlaceholder;
	@UiField
	Button         filterButton;
	@UiField
	protected
	ToggleSwitch   filterOperatorButton;
	@UiField
	BootstrapPager topPager;

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
	private boolean                                                      filterVisible   = false;
	private Map<DatabaseObjectFilterColumn<T, ?>, FilterCellCallback<T>> filterCallbacks = new HashMap<>();
	private RefreshableAsyncDataProvider<T> dataProvider;
	private ContextMenuHandler<T>           contextMenuHandler;
	private SelectionModel<T>               selectionModel;
	private MultiPageBooleanHeader          selectPageHeader;
	private int rangeStart = 0;

	// SERVER COMMUNICATION
	private Request currentRequest;
	private Pagination pagination = new Pagination(0, Integer.MAX_VALUE);

	// OTHER THINGS
	private PopupPanel tooltipPanel;

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

		table = new CellTable<T>(nrOfItemsPerPage)
		{
			@Override
			protected void onBrowserEvent2(Event event)
			{
				/* If this table is filterable and the event happened on an inputelement, just return */
				if (supportsFiltering() && InputElement.is(event.getEventTarget()) && (TableUtils.isEvent(event.getType(), BrowserEvents.CLICK, BrowserEvents.MOUSEDOWN, BrowserEvents.MOUSEUP, BrowserEvents.KEYDOWN)))
				{
					InputElement input = InputElement.as(event.getEventTarget()).cast();
					if (Objects.equals(input.getType(), "checkbox"))
						super.onBrowserEvent2(event);
				}
				else
				{
					super.onBrowserEvent2(event);
				}
			}
		};

		initWidget(ourUiBinder.createAndBindUi(this));
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
			filterObject = getSearchFilter();
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

		super.onUnload();
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		if (!supportsFiltering())
			filterInfo.setVisible(false);

		String id = "table-" + String.valueOf(Math.abs(RandomUtils.RANDOM.nextLong()));
		table.getElement().setId(id);

		table.setLoadingIndicator(new LoadingSpinner());
		table.getLoadingIndicator().getParent().getElement().getStyle().setProperty("minHeight", "110px");

		topPager.setDisplay(this);
		bottomPager.setDisplay(this);

		initTable();

		applyHeaderStyles();

		if (!supportsDownload())
			downloadButton.removeFromParent();

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

		for (int col = 0; col < table.getColumnCount(); col++)
		{
			Column<T, ?> column = table.getColumn(col);

			if (column instanceof DatabaseObjectFilterColumn)
			{
				Class type = ((DatabaseObjectFilterColumn) column).getType();

				if (ClassUtils.isNumeric(type))
				{
					column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
					table.getHeader(col).setHeaderStyleNames(Style.TEXT_RIGHT_ALIGN);
				}
			}
		}

		if (sortingEnabled)
		{
			// Add a ColumnSortEvent.AsyncHandler to connect sorting to the AsyncDataProvider
			ColumnSortEvent.AsyncHandler columnSortHandler = new ColumnSortEvent.AsyncHandler(table);
			table.addColumnSortHandler(columnSortHandler);
		}

		if (supportsFiltering())
		{
			filterPlaceholder.setVisible(true);
			filterOperatorButton.setVisible(false);
			filterOperatorButton.addValueChangeHandler(event ->
			{
				for (FilterCellCallback<T> callback : filterCallbacks.values())
				{
					if (callback.getRange() != null)
					{
						refreshTable();
						break;
					}
				}
			});
		}

		fetchTableData();
	}

	public void toggleFilter()
	{
		GQuery inputBoxes = GQuery.$("#" + getId() + " th").find("input[type='text']");

		for (DatabaseObjectFilterColumn<T, ?> column : filterCallbacks.keySet())
		{
			Header header = table.getHeader(table.getColumnIndex(column));
			Cell parent = header.getCell();

			if (parent instanceof ClearableCell)
			{
				((ClearableCell) parent).clear();
			}
			else if (parent instanceof CompositeCell)
			{
				List<Cell<?>> cells = ((CompositeCell<?>) parent).getCells();
				for (Cell<?> cell : cells)
				{
					if (cell instanceof ClearableCell)
						((ClearableCell) cell).clear();
				}
			}
		}

		for (FilterCellCallback<T> callback : filterCallbacks.values())
			callback.setRange(null);

		/* Get the content of all boxes */
		boolean allEmpty = true;
		for (int i = 0; i < inputBoxes.size(); i++)
		{
			InputElement e = inputBoxes.get(i).cast();
			if (!StringUtils.isEmpty(e.getValue()))
			{
				allEmpty = false;
				break;
			}
		}

		/* Toggle the visibility state of all inputs */
		inputBoxes.removeAttr("value")
				  .toggle();

		filterVisible = !filterVisible;

		if (!allEmpty)
			refreshTable();

		filterOperatorButton.setVisible(filterVisible);
		filterOperatorButton.setValue(true);
	}

	public boolean forceFilter(Map<String, String> columnToValue, boolean isAnd) throws InvalidArgumentException
	{
		FilterCellCallback<T> theCallbackToCallInTheEnd = null;

		for (int i = 0; i < table.getColumnCount(); i++)
		{
			Column<T, ?> c = table.getColumn(i);
			if (supportsFiltering() && c instanceof DatabaseObjectFilterColumn && columnToValue.containsKey(c.getDataStoreName()))
			{
				DatabaseObjectFilterColumn col = (DatabaseObjectFilterColumn) c;

				FilterCellCallback<T> callback = filterCallbacks.get(col);

				if (callback != null)
				{
					/* Make sure the filter is opened */
					if (!filterVisible)
					{
						filterButton.click();
					}

					/* Set the text to the cell */
					Header header = table.getHeader(table.getColumnIndex(c));
					Cell parent = header.getCell();

					if (parent instanceof FilterCell)
					{
						((FilterCell) parent).setValue(columnToValue.get(c.getDataStoreName()));
					}
					else if (parent instanceof CompositeCell)
					{
						List<Cell<?>> cells = ((CompositeCell<?>) parent).getCells();
						for (Cell<?> cell : cells)
						{
							if (cell instanceof FilterCell)
							{
								((FilterCell) cell).setValue(columnToValue.get(c.getDataStoreName()));
								break;
							}
						}
					}

					/* Cancel any currently running request */
					if (currentRequest != null && currentRequest.isPending())
						currentRequest.cancel();

					/* Initiate the filtering */
					callback.onFilterEvent(false, true, columnToValue.get(c.getDataStoreName()));
					theCallbackToCallInTheEnd = callback;
				}
			}
		}

		if (theCallbackToCallInTheEnd != null)
		{
			filterOperatorButton.setValue(isAnd);

			theCallbackToCallInTheEnd.onEnterPressed();
		}

		return theCallbackToCallInTheEnd != null;
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
			public Boolean getValue(T object)
			{
				return selectionModel.isSelected(object);
			}
		};

		if (selectionMode == SelectionMode.SINGLE)
		{
			addColumn(checkboxColumn, "", false, false);
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
					filterObject = getSearchFilter();
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

	private void updatePagersInitial(boolean filterApplied, PaginatedServerResult<List<T>> result)
	{
		if (pagination.getResultSize() != null)
		{
			if (!Objects.equals(pagination.getResultSize(), result.getResultSize()))
				Notification.notify(Notification.Type.WARNING, jhi.germinate.client.i18n.Text.LANG.notificationIncinsistancyCountResult());

			return;
		}
		else
		{
			pagination.setResultSize(result.getResultSize());
		}

		Integer nrOfItems = pagination.getResultSize();

		table.setPageSize(nrOfItemsPerPage);
		pagination.setResultSize(result.getResultSize());

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
							parent.getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.RELATIVE);
						}
					}

					PartialSearchQuery filterObject = null;

					if (supportsFiltering())
						filterObject = getSearchFilter();

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

								noDataHeading.setVisible(true);
								table.setRowCount(0, true);
								updateRowData(rangeStart, new ArrayList<>());
								topPager.setVisible(false);
								bottomPager.setVisible(false);

								if (!filterApplied)
								{
									table.setVisible(false);
									topPanel.setVisible(false);
									bottomPanel.setVisible(false);
								}
							}
							else
							{
								/* Tell the header how many items there are on this page */
								if (selectPageHeader != null)
									selectPageHeader.setValue(result.getResultSize());

								updatePagersInitial(filterApplied, result);

								table.setRowCount(pagination.getResultSize(), true);
								/* Show debug information */

                        		/* Update table */
								updateRowData(rangeStart, data);
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
	 * Adds a tooltip to the table. The tooltip will get its position and content from the {@link TableTooltipHandler}
	 *
	 * @param handler The {@link TableTooltipHandler} supplying the content of the tooltip and the {@link Alignment}
	 */
	public void addTooltipHandler(final TableTooltipHandler<T> handler)
	{
		tooltipPanel = new PopupPanel();
		tooltipPanel.setStyleName(TooltipPanelResource.INSTANCE.css().wrapper());

		table.addCellPreviewHandler(event ->
		{
			if (BrowserEvents.MOUSEOVER.equals(event.getNativeEvent().getType().toLowerCase()))
			{
				T row = event.getValue();

				/* Get the necessary information from the handler */
				IsWidget actualContent = handler.getTooltipContent(row, event.getColumn());
				TableTooltipHandler.Alignment alignment = handler.getAlignment();

				/* If there is nothing to display for this cell, return */
				if (actualContent == null)
					return;

				/* Get the visible range of the table and the start of the current page */
				int start = table.getVisibleRange().getStart();

				/* Determine the row index on this page */
				int rowIndex = event.getIndex() - start;

				/* Determine which table item the mouse is hovering over */
				TableCellElement reference = table.getRowElement(rowIndex).getCells().getItem(event.getColumn());

				/* Create the tooltip content */
				SimplePanel content = new SimplePanel();
				content.add(actualContent);
				content.setStyleName(TooltipPanelResource.INSTANCE.css().panel());

				/* Make sure an alignment is selected */
				if (alignment == null)
					alignment = TableTooltipHandler.Alignment.BELOW_ALIGN_LEFT;

				/* Determine left and top position based on the alignment */
				int left = alignment.getLeft(reference.getAbsoluteLeft(), reference.getAbsoluteRight(), content.getOffsetWidth());
				int top = alignment.getTop(reference.getAbsoluteTop(), reference.getAbsoluteBottom(), content.getOffsetHeight());

				/* Add the content to the tooltip and show it */
				tooltipPanel.setWidget(content);
				tooltipPanel.show();
				/* Set the position of the tooltip */
				tooltipPanel.setPopupPosition(left, top);
			}
			else if (BrowserEvents.MOUSEOUT.equals(event.getNativeEvent().getType().toLowerCase()))
			{
				tooltipPanel.hide();
			}
		});
	}

	/**
	 * Adds the column style names based on the database column that is associated with it.
	 *
	 * @param column The {@link Column} to add the style to
	 * @param header The {@link Header} to add the style to
	 */
	private void setColumnStyleName(Column<T, ?> column, Header<?> header)
	{
		if (!StringUtils.isEmpty(column.getDataStoreName()))
		{
			String style = "gm8-col-" + column.getDataStoreName().replace(".", "-");
			column.setCellStyleNames(style);

			if (header != null)
				header.setHeaderStyleNames(style);
		}
	}

	/**
	 * Adds the given column to the table
	 *
	 * @param column       The column
	 * @param headerString The column header text
	 * @param sortable     Should this column be sortable?
	 */
	public void addColumn(final Column<T, ?> column, final String headerString, final boolean sortable)
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
	public void addColumn(final Column<T, ?> column, final String headerString, final boolean sortable, final boolean filterable)
	{
		if (supportsFiltering() && filterable)
		{
			Class<?> type = String.class;

			if (column instanceof DatabaseObjectFilterColumn)
				type = ((DatabaseObjectFilterColumn) column).getType();

			List<HasCell<String, ?>> cells = new ArrayList<>();

			boolean supportsRange = ClassUtils.isNumeric(type) || ClassUtils.isAnyType(type) || Objects.equals(Date.class, type);

			FilterCellCallback<T> callback;

			/* Add the first input */
			if (Objects.equals(type, Date.class))
			{
				callback = new FilterCellCallback<>(this, column);
				cells.add(new HasDatePickerCell(supportsRange ? FilterCell.FilterCellState.TOP : FilterCell.FilterCellState.SINGLE, this, callback));
			}
			else
			{
				callback = new FilterCellCallback<>(this, column);
				cells.add(new HasFilterCell(table, supportsRange ? FilterCell.FilterCellState.TOP : FilterCell.FilterCellState.SINGLE, this, callback));
			}

			/* Add the second input if this column supports range queries */
			if (supportsRange)
			{
				if (Objects.equals(type, Date.class))
				{
					cells.add(new HasDatePickerCell(FilterCell.FilterCellState.BOTTOM, this, callback));
				}
				else
				{
					cells.add(new HasFilterCell(table, FilterCell.FilterCellState.BOTTOM, this, callback));
				}
			}
			else
			{
				cells.add(new HasDummyInputCell(this));
			}

			if (column instanceof DatabaseObjectFilterColumn)
			{
				filterCallbacks.put((DatabaseObjectFilterColumn) column, callback);
			}

			/* Add the colum header (the actual column name) */
			cells.add(0, new HasTextCell(headerString));

			table.addColumn(column, new Header<String>(new CompositeCell<>(cells))
			{
				@Override
				public String getValue()
				{
					return headerString;
				}
			});

			if (sortable)
				addSortBits(column);
		}
		else
		{
			table.addColumn(column, headerString);

			if (sortable)
				addSortBits(column);
		}

		setColumnStyleName(column, table.getHeader(table.getColumnCount() - 1));
	}

	/**
	 * Adds the given column to the table
	 *
	 * @param column       The column
	 * @param headerString The column header text
	 */
	public void addColumn(Column<T, ?> column, String headerString)
	{
		addColumn(column, headerString, false);
	}

	/**
	 * Adds the given column to the table
	 *
	 * @param column The column
	 */
	public void addColumn(Column<T, ?> column)
	{
		addColumn(column, "", false);
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

	protected PartialSearchQuery getSearchFilter()
	{
		PartialSearchQuery q = new PartialSearchQuery();

		boolean atLeastOne = false;
		for (DatabaseObjectFilterColumn<T, ?> column : filterCallbacks.keySet())
		{
			String databaseColumnName = column.getDataStoreName();
			FilterCell.FilterCallback.Range range = filterCallbacks.get(column).getRange();

			if (StringUtils.isEmpty(databaseColumnName) || range == null)
				continue;

			try
			{
				List<String> rangeValues = range.getValues();

				if (rangeValues.size() == 2)
				{
					String first = rangeValues.get(0);
					String second = rangeValues.get(1);

					SearchCondition condition = new SearchCondition();
					condition.setColumnName(databaseColumnName);
					condition.setType(column.getType().getSimpleName());
					condition.setComp(new Between());
					condition.addConditionValue(first);
					condition.addConditionValue(second);
					q.add(condition);
					if (filterOperatorButton.getValue())
						q.addLogicalOperator(new And());
					else
						q.addLogicalOperator(new Or());
				}
				else if (rangeValues.size() == 1)
				{
					SearchCondition condition = new SearchCondition();
					condition.setColumnName(databaseColumnName);
					condition.setType(column.getType().getSimpleName());
					condition.setComp(new Equal());
					condition.addConditionValue(rangeValues.get(0));

					q.add(condition);
					if (filterOperatorButton.getValue())
						q.addLogicalOperator(new And());
					else
						q.addLogicalOperator(new Or());
				}
				else
				{
					continue;
				}

				atLeastOne = true;
			}
			catch (InvalidSearchQueryException | InvalidArgumentException e)
			{
			}
		}

		if (atLeastOne)
			q.removeLogicalOperator(q.getLogicalOperators().size() - 1);
		else
			q = null;

		return q;
	}

	private void addSortBits(Column<T, ?> column)
	{
		/* Set up sorting */
		if (sortingEnabled)
		{
			column.setSortable(true);
		}
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

	@Override
	public boolean isFilterVisible()
	{
		return filterVisible;
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
	protected abstract void onSelectionChanged(NativeEvent event, T object, int column);

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
		public void onBrowserEvent(Cell.Context context, Element elem, T object, NativeEvent event)
		{
			/* We want to handle click events */
			if (BrowserEvents.CLICK.equals(event.getType()))
			{
				onSelectionChanged(event, object, context.getColumn());

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
					onSelectionChanged(event, object, context.getColumn());
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