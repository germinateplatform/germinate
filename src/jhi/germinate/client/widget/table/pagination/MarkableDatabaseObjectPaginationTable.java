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
import com.google.gwt.event.shared.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.markeditemlist.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.cell.*;
import jhi.germinate.client.widget.table.pagination.resource.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class MarkableDatabaseObjectPaginationTable<T extends DatabaseObject> extends DatabaseObjectPaginationTable<T>
{
	private final MarkedItemList.ItemType itemType;
	private       HandlerRegistration     markedItemListRegistration;
	private       HandlerRegistration     groupRegistration;

	public MarkableDatabaseObjectPaginationTable(MarkedItemList.ItemType itemType, SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(selectionMode, sortingEnabled);

		this.itemType = itemType;
	}

	public final void getIds(AsyncCallback<ServerResult<List<String>>> callback)
	{
		getIds(getSearchFilter(false), callback);
	}

	public abstract void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback);

	protected boolean preventAllItemMarking()
	{
		return false;
	}

	@Override
	protected void onPostLoad()
	{
		super.onPostLoad();


		makeTableMarkable();

		addMarkedItemListButton();

		/* Add a handler that updates the table when the shopping cart changes (these will be changes from other sources than the current table) */
		markedItemListRegistration = GerminateEventBus.BUS.addHandler(MarkedItemListEvent.TYPE, event ->
		{
			if (itemType == event.getType())
			{
				Collection<String> ids = event.getIds();
				List<T> visibleItems = getVisibleItems();

				if (!CollectionUtils.isEmpty(ids))
				{
					for (int i = 0; i < visibleItems.size(); i++)
					{
						Long id = DatabaseObject.getGroupSpecificId(visibleItems.get(i));
						if (id != null && ids.contains(Long.toString(id)))
						{
							redrawRow(i);
						}
					}
				}
				else if (ids == null)
				{
					redraw();
				}
			}
		});

		groupRegistration = GerminateEventBus.BUS.addHandler(GroupCreationEvent.TYPE, event -> MarkedItemList.clear(itemType));
	}

	/**
	 * Adds two things to the {@link DatabaseObjectPaginationTable}: <ul> <li>An additional column (at the given index) that indicates the "marked
	 * status" of the row and allows marking by just clicking the icon</li> <li>A context menu that allows marking unmarking items</li> </ul>
	 */
	public void makeTableMarkable()
	{
		if (GerminateSettingsHolder.isPageAvailable(Page.MARKED_ITEMS))
		{
			DatabaseObjectPaginationTable.ContextMenuHandler<T> handler = (row, x, y, anchorClicked) ->
			{
				if (anchorClicked)
				{
					/* Ignore if the user right-clicks on a link */
					return false;
				}
				else
				{
					/* Otherwise show a popup menu */
					return showPopupMenu(x, y, row, new MarkedItemListCallback()
					{
						private Long getId(DatabaseObject object)
						{
							if (object == null)
								return null;

							return DatabaseObject.getGroupSpecificId(object);
						}

						@Override
						public List<String> getIds(boolean toBeMarked)
						{
							List<String> result = new ArrayList<>();

							for (T row : getVisibleItems())
							{
								Long id = getId(row);
								if (id != null)
									result.add(Long.toString(id));
							}

							return result;
						}

						@Override
						public String getId(boolean toBeMarked)
						{
							Long id = getId(row);

							if (id == null)
								return null;
							else
								return Long.toString(id);
						}

						@Override
						public void updateTable(Collection<String> ids)
						{
							if (CollectionUtils.isEmpty(ids))
								return;

							List<T> rows = getVisibleItems();
							for (int i = 0; i < rows.size(); i++)
							{
								Long id = getId(rows.get(i));
								if (id != null && ids.contains(Long.toString(id)))
									redrawRow(i);
							}
						}

						@Override
						public void updateTable(String newId)
						{
							List<T> rows = getVisibleItems();
							for (int i = 0; i < rows.size(); i++)
							{
								Long id = getId(rows.get(i));
								if (id != null && newId.equals(Long.toString(id)))
									redrawRow(i);
							}
						}
					});
				}
			};

			/* Add the new column */
			addMarkedStatusColumn(handler);

			/* Handle the context menu */
			setContextMenuHandler(handler);
		}
	}

	/**
	 * Shows a {@link PopupPanel} as a context menu at the given location. Uses {@link Accession#ID} to retrieve the id from the {@link
	 * DatabaseObject}
	 *
	 * @param x        The x position of the {@link PopupPanel}
	 * @param y        The y position of the {@link PopupPanel}
	 * @param callback The {@link MarkedItemListCallback}
	 * @return <code>true</code> if the panel will be shown
	 */
	private boolean showPopupMenu(final int x, final int y, T row, final MarkedItemListCallback callback)
	{
		if (callback == null || (row == null && preventAllItemMarking()))
			return false;

		final PopupPanel popupPanel = new PopupPanel(true);

		final MenuBar menuBar = new MenuBar(true);
		MenuItem menuItem;
		int itemCount = 0;

		// We only show the "mark all" items if it's the header that's clicked and if the child class doesn't request to prevent it
		if (row == null && !preventAllItemMarking())
		{
			menuItem = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHECKBOX_MULTIPLE_MARKED, Text.LANG.cartAddAllToCartButton()), () -> getIds(new DefaultAsyncCallback<ServerResult<List<String>>>(true)
			{
				@Override
				protected void onSuccessImpl(ServerResult<List<String>> result)
				{
					if (result.hasData())
					{
						MarkedItemList.add(itemType, result.getServerResult());
						popupPanel.hide();
						callback.updateTable(result.getServerResult());
					}
				}
			}));
			menuItem.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Style.LAYOUT_WHITE_SPACE_NO_WRAP, Emphasis.PRIMARY.getCssName()));
			menuBar.addItem(menuItem);
			itemCount++;

			menuItem = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_CHECKBOX_MULTIPLE_BLANK_OUTLINE, Text.LANG.cartRemoveAllFromCartButton()), () -> getIds(new DefaultAsyncCallback<ServerResult<List<String>>>(true)
			{
				@Override
				protected void onSuccessImpl(ServerResult<List<String>> result)
				{
					if (result.hasData())
					{
						MarkedItemList.remove(itemType, result.getServerResult());
						popupPanel.hide();
						callback.updateTable(result.getServerResult());
					}
				}
			}));
			menuItem.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Style.LAYOUT_WHITE_SPACE_NO_WRAP, Emphasis.PRIMARY.getCssName()));
			menuBar.addItem(menuItem);
			itemCount++;
		}

		// Add the "Create group from selection" button for the header
		if (row == null && ModuleCore.getUseAuthentication() && !GerminateSettingsHolder.get().isReadOnlyMode.getValue())
		{
			menuItem = new MenuItem(SimpleHtmlTemplate.INSTANCE.contextMenuItemMaterialIcon(Style.MDI_GROUP, Text.LANG.buttonCreateGroupFromSelection()), (Command) () ->
			{
				Set<String> ids = MarkedItemList.get(itemType);

				if (CollectionUtils.isEmpty(ids))
				{
					Notification.notify(Notification.Type.WARNING, Text.LANG.notificationNoItemsMarked());
				}
				else
				{
					AbstractCartView.askForGroupNameAndCreate(new ArrayList<>(ids), itemType, null);
				}
			});
			menuItem.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Style.LAYOUT_WHITE_SPACE_NO_WRAP, Emphasis.PRIMARY.getCssName()));
			menuBar.addItem(menuItem);
			itemCount++;
		}

		MenuItem[] additionalItems = getAdditionalItems(row, popupPanel, callback);
		// If there are other items to add
		if (!ArrayUtils.isEmpty(additionalItems))
		{
			if(itemCount > 0)
			{
				// Add a separator
				menuBar.addSeparator();
			}

			// For each item
			for (MenuItem m : additionalItems)
			{
				final Scheduler.ScheduledCommand old = m.getScheduledCommand();
				m.setScheduledCommand(() ->
				{
					old.execute();
					popupPanel.hide();
				});
				m.setStyleName(Style.combine(TooltipPanelResource.INSTANCE.css().link(), Style.LAYOUT_WHITE_SPACE_NO_WRAP, Emphasis.PRIMARY.getCssName()));
				menuBar.addItem(m);
				itemCount++;
			}
		}

		menuBar.setVisible(true);
		menuBar.setStyleName(TooltipPanelResource.INSTANCE.css().panel());
		menuBar.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);

		popupPanel.add(menuBar);
		popupPanel.setPopupPositionAndShow((offsetWidth, offsetHeight) ->
		{
			Scheduler.get().scheduleDeferred(() -> {
				int popupX = x + Window.getScrollLeft();
				if (popupX + offsetWidth > Window.getClientWidth() + Window.getScrollLeft())
					popupX = Window.getClientWidth() + Window.getScrollLeft() - offsetWidth;

				popupPanel.setPopupPosition(popupX, y + Window.getScrollTop());
			});
		});
		return true;
	}

	/**
	 * Adds an additional column
	 */
	private void addMarkedStatusColumn(final DatabaseObjectPaginationTable.ContextMenuHandler<T> handler)
	{
		Column<T, SafeHtml> column = new Column<T, SafeHtml>(new SafeHtmlCell()
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
		})
		{
			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, T row, NativeEvent event)
			{
				/* On right-click events */
				if (BrowserEvents.CONTEXTMENU.equals(event.getType()))
				{
					boolean preventDefault = handler.handleContextMenuEvent(row, event.getClientX(), event.getClientY(), false);

					if (preventDefault)
						event.preventDefault();
				}
				/* On click events */
				else if (BrowserEvents.CLICK.equals(event.getType()))
				{
					Long id = DatabaseObject.getGroupSpecificId(row);

					if (id == null)
						return;

					String idString = Long.toString(id);

					/* Change the marked state of the item */
					MarkedItemList.toggle(itemType, idString);

					/* Update the row */
					List<T> rows = getVisibleItems();
					for (int i = 0; i < rows.size(); i++)
					{
						Long itemId = DatabaseObject.getGroupSpecificId(rows.get(i));
						if (itemId != null && id.longValue() == itemId.longValue())
							redrawRow(i);
					}
				}
				else
				{
					super.onBrowserEvent(context, elem, row, event);
				}
			}

			@Override
			public String getCellStyleNames(Cell.Context context, T row)
			{
				return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
			}

			@Override
			public SafeHtml getValue(T row)
			{
				Long id = DatabaseObject.getGroupSpecificId(row);

				if (id == null)
					return SimpleHtmlTemplate.INSTANCE.empty();

				String idString = Long.toString(id);

				boolean isMarked = MarkedItemList.contains(itemType, idString);

				String fa;
				String title;

				if (isMarked)
				{
					fa = Style.combine(Style.MDI_CHECKBOX_MARKED, Emphasis.PRIMARY.getCssName());
					title = Text.LANG.cartTooltipClickToUnmarkItem();
				}
				else
				{
					fa = Style.MDI_CHECKBOX_BLANK_OUTLINE;
					title = Text.LANG.cartTooltipClickToMarkItem();
				}

				return SimpleHtmlTemplate.INSTANCE.materialIconFixedWidth(fa, title);
			}
		};

		/* Adds a column header for the new checkbox column that will toggle the checkbox state of the rows */
		Header<Boolean> header = new Header<Boolean>(new FACheckboxCell<T>(false, true, handler)
		{
			@Override
			public Set<String> getConsumedEvents()
			{
				HashSet<String> events = new HashSet<>();
				events.add(BrowserEvents.CLICK);
				events.add(BrowserEvents.CONTEXTMENU);
				return events;
			}
		})
		{
			@Override
			public void onBrowserEvent(Cell.Context context, Element elem, NativeEvent event)
			{
				/* On click events */
				if (BrowserEvents.CLICK.equals(event.getType()))
				{
					showPopupMenu(event.getClientX(), event.getClientY(), null, new MarkedItemListCallback()
					{
						private Long getId(DatabaseObject object)
						{
							if (object == null)
								return null;

							return DatabaseObject.getGroupSpecificId(object);
						}

						@Override
						public List<String> getIds(boolean toBeMarked)
						{
							List<String> result = new ArrayList<>();

							for (T row : getVisibleItems())
							{
								Long id = getId(row);
								if (id != null)
									result.add(Long.toString(id));
							}

							return result;
						}

						@Override
						public String getId(boolean toBeMarked)
						{
							return null;
						}

						@Override
						public void updateTable(Collection<String> ids)
						{
							if (CollectionUtils.isEmpty(ids))
								return;

							List<T> rows = getVisibleItems();
							for (int i = 0; i < rows.size(); i++)
							{
								Long id = getId(rows.get(i));
								if (id != null && ids.contains(Long.toString(id)))
									redrawRow(i);
							}
						}

						@Override
						public void updateTable(String newId)
						{
							List<T> rows = getVisibleItems();
							for (int i = 0; i < rows.size(); i++)
							{
								Long id = getId(rows.get(i));
								if (id != null && newId.equals(Long.toString(id)))
									redrawRow(i);
							}
						}
					});
				}
				else
				{
					super.onBrowserEvent(context, elem, event);
				}
			}

			@Override
			public Boolean getValue()
			{
				for (T item : getVisibleItems())
				{
					if (!MarkedItemList.contains(itemType, Long.toString(DatabaseObject.getGroupSpecificId(item))))
					{
						return false;
					}
				}
				return getVisibleItems().size() > 0;
			}

			@Override
			public String getHeaderStyleNames()
			{
				return Style.combine(Style.TEXT_CENTER_ALIGN, Style.CURSOR_DEFAULT);
			}
		};
		header.setUpdater(value ->
		{
			List<T> rows = getVisibleItems();
			for (int i = 0; i < rows.size(); i++)
			{
				String id = Long.toString(DatabaseObject.getGroupSpecificId(rows.get(i)));

				MarkedItemList.toggle(itemType, id);
				redrawRow(i);
			}
		});

		addColumn(column, header);
	}

	@Override
	public void onUnload()
	{
		/* Remember to remove the handler */
		if (markedItemListRegistration != null)
			markedItemListRegistration.removeHandler();
		if (groupRegistration != null)
			groupRegistration.removeHandler();

		super.onUnload();
	}

	/**
	 * Adds the marked item indicator the the top right of the table
	 */
	private void addMarkedItemListButton()
	{
		ButtonGroup group = new ButtonGroup();
		group.addStyleName(Style.LAYOUT_FLOAT_INITIAL);
		// Add the button
		Button deleteButton = new Button("", e -> AlertDialog.createYesNoDialog(Text.LANG.generalClear(), Text.LANG.markedItemListClearConfirm(), false, ev -> MarkedItemList.clear(itemType), null));
		deleteButton.addStyleName(Style.mdiLg(Style.MDI_DELETE));
		deleteButton.setTitle(Text.LANG.generalClear());

		Button badgeButton = new Button("", e -> {
			ItemTypeParameterStore.Inst.get().put(Parameter.markedItemType, itemType);
			History.newItem(Page.MARKED_ITEMS.name());
		});
		// Add the actual badge that shows the number
		Badge badge = new Badge(NumberUtils.INTEGER_FORMAT.format(MarkedItemList.get(itemType).size()));
		group.add(deleteButton);
		group.add(badgeButton);
		badgeButton.add(badge);

		// Listen to shopping cart changes
		GerminateEventBus.BUS.addHandler(MarkedItemListEvent.TYPE, event -> badge.setText(NumberUtils.INTEGER_FORMAT.format(MarkedItemList.get(itemType).size())));

		// Add it to the top pager
		topPager.add(group);
	}

	protected MenuItem[] getAdditionalItems(T row, PopupPanel popupPanel, MarkedItemListCallback callback)
	{
		return null;
	}

	protected interface MarkedItemListCallback
	{
		String getId(boolean toBeMarked);

		List<String> getIds(boolean toBeMarked);

		void updateTable(Collection<String> ids);

		void updateTable(String id);
	}
}
