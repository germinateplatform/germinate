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

package jhi.germinate.client.widget.table.pagination.cell;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safecss.shared.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;

/**
 * @author Sebastian Raubach
 */
public class CustomDatePickerCell extends DatePickerCell implements ClearableCell
{
	private InputElement                  inputElement;
	private FilterCell.FilterCellState    state;
	private FilterCell.VisibilityCallback visibilityCallback;

	private String value = "";

	public CustomDatePickerCell(FilterCell.FilterCellState state, FilterCell.VisibilityCallback visibilityCallback)
	{
		super();
		this.state = state;
		this.visibilityCallback = visibilityCallback;

		/* Configure the date picker */
		final DatePicker picker = getDatePicker();
		picker.setYearAndMonthDropdownVisible(true);
		picker.setYearArrowsVisible(true);
		picker.setVisibleYearCount(200);

		/* Fix the positioning of the DatePicker, i.e. make sure it's always on-screen */
		picker.addAttachHandler(event -> Scheduler.get().scheduleDeferred(() ->
		{
			if (inputElement != null)
			{
				Widget parent = picker.getParent();

				if (parent instanceof PopupPanel)
				{
					/* Get the picker dimensions */
					int pickerWidth = picker.getOffsetWidth();
					int pickerHeight = picker.getOffsetHeight();

					/* Get the input element position/dimensions */
					int inputX = inputElement.getAbsoluteLeft();
					int inputY = inputElement.getAbsoluteTop();
					int inputHeight = inputElement.getOffsetHeight();

					/* Calculate the final positioning of the picker */
					int finalX;
					int finalY;

					/* If it sticks out to the right, move it to the left */
					if (inputX + pickerWidth > Window.getClientWidth())
					{
						finalX = Window.getClientWidth() - pickerWidth;
					}
					else
					{
						finalX = inputX;
					}

					/* If it sticks out the bottom, move it up */
					if (inputY + inputHeight + pickerHeight > Window.getScrollTop() + Window.getClientHeight())
					{
						finalY = inputY - pickerHeight;
					}
					else
					{
						finalY = inputY + inputHeight;
					}

					/* Set the final position */
					((PopupPanel) parent).setPopupPosition(finalX, finalY);
				}
			}
		}));
	}

	@Override
	public Set<String> getConsumedEvents()
	{
		Set<String> consumedEvents = new HashSet<>();
		consumedEvents.add(BrowserEvents.FOCUS);

		return consumedEvents;
	}

	@Override
	public void render(Cell.Context context, Date value, SafeHtmlBuilder sb)
	{
		String placeholder;
		SafeStyles borderTopStyle = SafeStylesUtils.fromTrustedString("");

		switch (state)
		{
			case TOP:
				placeholder = Text.LANG.generalRangeFrom();
				break;
			case BOTTOM:
				placeholder = Text.LANG.generalRangeTo();
				borderTopStyle = SafeStylesUtils.fromTrustedNameAndValue("border-top", 0, Style.Unit.PX);
				break;
			case SINGLE:
			default:
				placeholder = "";

		}

		/* Get the view data */
		Object key = context.getKey();
		Date viewData = getViewData(key);
		if (viewData != null && viewData.equals(value))
		{
			clearViewData(key);
			viewData = null;
		}

		if (viewData != null && visibilityCallback != null && visibilityCallback.isFilterVisible())
		{
			this.value = DateUtils.getLocalizedDate(viewData);
		}
		else
		{
			setViewData(key, null);
			this.value = "";
		}

		SafeHtml s;

		if (visibilityCallback != null && !visibilityCallback.isFilterVisible())
		{
			if (this.value != null)
			{
				s = FilterCell.TEMPLATES.filterDateInvisible(this.value, placeholder, Text.LANG.tableHeaderFilterCellTooltip(), borderTopStyle);
			}
			else
			{
				s = FilterCell.TEMPLATES.filterDateInvisible("", placeholder, Text.LANG.tableHeaderFilterCellTooltip(), borderTopStyle);
			}
		}
		else
		{
			if (this.value != null)
			{
				s = FilterCell.TEMPLATES.filterDate(this.value, placeholder, Text.LANG.tableHeaderFilterCellTooltip(), borderTopStyle);
			}
			else
			{
				s = FilterCell.TEMPLATES.filterDate("", placeholder, Text.LANG.tableHeaderFilterCellTooltip(), borderTopStyle);
			}
		}

		sb.append(s);
	}

	@Override
	public void onBrowserEvent(final Context context, final Element parent, final Date value, final NativeEvent event, final ValueUpdater<Date> valueUpdater)
	{
		DivElement div = parent.getChild(0).cast();
		inputElement = div.getFirstChildElement().cast();

		DOM.sinkEvents(inputElement, Event.ONCLICK);
		DOM.setEventListener(inputElement, event1 ->
		{
			if (InputElement.is(event1.getEventTarget()))
			{
				if (BrowserEvents.BLUR.equals(event1.getType()))
				{
					return;
				}

				Date date = value;

				if (date == null)
					date = new Date();

				/* "Convert" the event into a click event */
				NativeEvent clickEvent = Document.get().createClickEvent(Event.ONCLICK, event1.getScreenX(), event1.getScreenY(), event1.getClientX(), event1.getClientY(), event1.getCtrlKey(), event1.getAltKey(), event1.getShiftKey(), event1.getMetaKey());
				CustomDatePickerCell.super.onBrowserEvent(context, parent, date, clickEvent, valueUpdater);
			}
		});
	}

	@Override
	public void clear()
	{
		this.value = "";
	}

	public void updateInnerValue(Date value)
	{
		this.value = DateUtils.getLocalizedDate(value);
	}
}
