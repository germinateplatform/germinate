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

package jhi.germinate.client.widget.element;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class BootstrapPager extends AbstractPager implements HasWidgets
{
	public static final  int   DEFAULT_PAGE_SIZE = 25;
	private static final int[] SIZES             = {10, 25, 50, 100, 250, 500};

	private NumberFormat formatter;

	private final FlowPanel         panel;
	private final UnorderedList     ul;
	private       MdiAnchorListItem firstPage     = new MdiAnchorListItem();
	private       MdiAnchorListItem prevPage      = new MdiAnchorListItem();
	private       MdiAnchorListItem totalCount    = new MdiAnchorListItem();
	private       MdiAnchorListItem currentPage   = new MdiAnchorListItem();
	private       MdiAnchorListItem nextPage      = new MdiAnchorListItem();
	private       MdiAnchorListItem lastPage      = new MdiAnchorListItem();
	private       ButtonGroup       pageSizeGroup = new ButtonGroup();
	private       Button            toggle        = new Button();

	public BootstrapPager()
	{
		panel = new FlowPanel();
		initWidget(panel);

		formatter = NumberFormat.getFormat(Text.LANG.pagerNumberFormat());

		ul = new UnorderedList();
		panel.add(ul);

		int currentPageSize = IntegerParameterStore.Inst.get().get(Parameter.paginationPageSize, DatabaseObjectPaginationTable.DEFAULT_NR_OF_ITEMS_PER_PAGE);

		toggle.setText(Integer.toString(currentPageSize));
		toggle.setTitle(Text.LANG.pagerItemsPerPage());
		toggle.setDataToggle(Toggle.DROPDOWN);
		toggle.setToggleCaret(true);

		toggle.getElement().getStyle().setProperty("borderRight", "0");

		pageSizeGroup.add(toggle);
		DropDownMenu menu = new DropDownMenu();
		pageSizeGroup.add(menu);

		panel.insert(pageSizeGroup, 0);

		ul.add(firstPage);
		ul.add(prevPage);
		ul.add(totalCount);
		ul.add(currentPage);
		ul.add(nextPage);
		ul.add(lastPage);

		firstPage.setMdi(Style.MDI_CHEVRON_DOUBLE_LEFT);
		prevPage.setMdi(Style.MDI_CHEVRON_LEFT);
//		totalCount.setMdi(Style.MDI_POUND);
		currentPage.setMdi(Style.MDI_BOOK_OPEN_PAGE_VARIANT);
		nextPage.setMdi(Style.MDI_CHEVRON_RIGHT);
		lastPage.setMdi(Style.MDI_CHEVRON_DOUBLE_RIGHT);

		totalCount.setHiddenOn(DeviceSize.XS);
		currentPage.setHiddenOn(DeviceSize.XS);

		ul.addStyleName(Style.combine(Style.LAYOUT_NO_PADDING, Style.LAYOUT_NO_MARGIN, Style.LAYOUT_V_ALIGN_MIDDLE, Styles.PAGINATION));

		addClickListeners();

		for (int i : SIZES)
		{
			AnchorListItem item = new AnchorListItem(Integer.toString(i));
			item.addClickHandler(e ->
			{
				Integer value = Integer.parseInt(item.getText());
				toggle.setText(item.getText());

				IntegerParameterStore.Inst.get().put(Parameter.paginationPageSize, value);

				GoogleAnalytics.trackEvent(GoogleAnalytics.Category.UI, "set", "itemsPerPage", value);

				GerminateEventBus.BUS.fireEvent(new TableRowCountChangeEvent());
			});
			menu.add(item);
		}

		GerminateEventBus.BUS.addHandler(TableRowCountChangeEvent.TYPE, e ->
		{
			int value = IntegerParameterStore.Inst.get().get(Parameter.paginationPageSize, DEFAULT_PAGE_SIZE);
			toggle.setText(Integer.toString(value));
		});
	}

	@Override
	public void setVisible(boolean visible)
	{
		ul.setVisible(visible);
		pageSizeGroup.setVisible(visible);
	}

	@Override
	public void add(Widget w)
	{
		panel.add(w);
	}

	@Override
	public boolean remove(Widget w)
	{
		return panel.remove(w);
	}

	@Override
	public Iterator<Widget> iterator()
	{
		return panel.iterator();
	}

	@Override
	public void clear()
	{
		panel.clear();
	}

	private void addClickListeners()
	{
		firstPage.addClickHandler(event -> firstPage());
		prevPage.addClickHandler(event -> previousPage());
		currentPage.addClickHandler(event -> showPageSelection());
		nextPage.addClickHandler(event -> nextPage());
		lastPage.addClickHandler(event -> lastPage());
	}

	private void showPageSelection()
	{
		FormGroup group = new FormGroup();
		RangedIntegerTextBox box = new RangedIntegerTextBox(1, getPageCount());
		box.setText(Integer.toString(getPage() + 1));
		FormLabel label = new FormLabel();
		label.setText(Text.LANG.pagerPageNumberInput(formatter.format(getPageCount())));
		group.add(label);
		group.add(box);

		AlertDialog dialog = new AlertDialog(Text.LANG.pagerJumpToPageTitle(), group);
		dialog.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDone(), Style.MDI_CHECK, e ->
		{
			if (box.validate(true))
			{
				setPage(box.getIntegerValue() - 1);
				GoogleAnalytics.trackEvent(GoogleAnalytics.Category.UI, "jump", "toPage", box.getIntegerValue() - 1);
				dialog.close();
			}
		}))
			  .setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalCancel(), Style.MDI_CANCEL, null))
			  .setAutoCloseOnPositive(false)
			  .addShownHandler(e ->
			  {
				  box.selectAll();
				  box.setFocus(true);

			  })
			  .open();

		box.addKeyPressHandler(event ->
		{
			if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
			{
				dialog.positiveClick();
			}
		});
	}

	@Override
	protected void onRangeOrRowCountChanged()
	{
		HasRows display = getDisplay();

		// Update the prev and first buttons.
		setPrevPageButtonsDisabled(!hasPreviousPage());

		// Update the next and last buttons.
		if (isRangeLimited() || !display.isRowCountExact())
		{
			setNextPageButtonsDisabled(!hasNextPage());
		}

		int dataSize = display.getRowCount();
		boolean exact = display.isRowCountExact();

		String itemText = formatter.format(dataSize);
		String pageText = formatter.format(getPage() + 1) + (exact ? Text.LANG.pagerOf() : Text.LANG.pagerOfOver()) + formatter.format(getPageCount());

		currentPage.setText(pageText);
		currentPage.setVisible(!StringUtils.isEmpty(pageText));
		totalCount.setText(itemText);
		totalCount.setVisible(!StringUtils.isEmpty(itemText));
	}

	@Override
	public void setPageStart(int index)
	{
		if (getDisplay() != null)
		{
			Range range = getDisplay().getVisibleRange();
			int pageSize = range.getLength();

			/* Removing this fixes an issue with the last page. Example: If
			 * there are 850 items and each size is set to show 100, the last
			 * page (if reached by stepping through and not by jumping to the
			 * end) would go from 751 to 850 instead of 801 - 850. */
			// if (isRangeLimited && display.isRowCountExact())
			// {
			// index = Math.min(index, display.getRowCount() - pageSize);
			// }

			index = Math.max(0, index);
			if (index != range.getStart())
			{
				getDisplay().setVisibleRange(index, pageSize);
			}
		}
	}

	/**
	 * Enable or disable the next page buttons.
	 *
	 * @param disabled true to disable, false to enable
	 */
	private void setNextPageButtonsDisabled(boolean disabled)
	{
		nextPage.setEnabled(!disabled);
		lastPage.setEnabled(!disabled);

		if (disabled)
		{
			nextPage.addStyleName(Styles.DISABLED);
			lastPage.addStyleName(Styles.DISABLED);
		}
		else
		{
			nextPage.removeStyleName(Styles.DISABLED);
			lastPage.removeStyleName(Styles.DISABLED);
		}
	}

	/**
	 * Enable or disable the previous page buttons.
	 *
	 * @param disabled true to disable, false to enable
	 */
	private void setPrevPageButtonsDisabled(boolean disabled)
	{
		firstPage.setEnabled(!disabled);
		prevPage.setEnabled(!disabled);

		if (disabled)
		{
			firstPage.addStyleName(Styles.DISABLED);
			prevPage.addStyleName(Styles.DISABLED);
		}
		else
		{
			firstPage.removeStyleName(Styles.DISABLED);
			prevPage.removeStyleName(Styles.DISABLED);
		}
	}
}
