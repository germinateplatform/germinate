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

package jhi.germinate.client.widget.table.pagination.cell;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.safecss.shared.*;
import com.google.gwt.safehtml.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class FilterCell extends AbstractCell<String> implements ClearableCell
{
	interface Templates extends SafeHtmlTemplates
	{
		@SafeHtmlTemplates.Template("<div class='" + Style.LAYOUT_VERTICAL_INPUT_GROUP + "'><input class='" + Styles.FORM_CONTROL + "' type='text' placeholder='{1}' style='{3} width: 100%; display: block; padding: 3px;' value='{0}' title='{2}'></input></div>")
		SafeHtml filter(String value, String placeholder, String title, SafeStyles borderTopStyle);

		@SafeHtmlTemplates.Template("<div class='" + Style.LAYOUT_VERTICAL_INPUT_GROUP + "'><input class='" + Styles.FORM_CONTROL + "' type='text' placeholder='{1}' style='{3} width: 100%; display: none; padding: 3px;' value='{0}' title='{2}'></input></div>")
		SafeHtml filterInvisible(String value, String placeholder, String title, SafeStyles borderTopStyle);

		@SafeHtmlTemplates.Template("<div class='" + Style.LAYOUT_VERTICAL_INPUT_GROUP + "'><input class='" + Styles.FORM_CONTROL + "' type='text' placeholder='{1}' style='{3} width: 100%; display: block; padding: 3px; cursor: pointer;' value='{0}' readonly  title='{2}'></input></div>")
		SafeHtml filterDate(String value, String placeholder, String title, SafeStyles borderTopStyle);

		@SafeHtmlTemplates.Template("<div class='" + Style.LAYOUT_VERTICAL_INPUT_GROUP + "'><input class='" + Styles.FORM_CONTROL + "' type='text' placeholder='{1}' style='{3} width: 100%; display: none; padding: 3px; cursor: pointer;' value='{0}' readonly  title='{2}'></input></div>")
		SafeHtml filterDateInvisible(String value, String placeholder, String title, SafeStyles borderTopStyle);
	}

	public static final Templates TEMPLATES = GWT.create(Templates.class);

	private FilterCallback     filterCallback;
	private VisibilityCallback visibilityCallback;
	private String          value = "";
	private FilterCellState state = FilterCellState.SINGLE;

	public FilterCell(Widget parent, FilterCellState state, VisibilityCallback visibilityCallback, FilterCallback filterCallback)
	{
		super(BrowserEvents.FOCUS, BrowserEvents.MOUSEOVER, BrowserEvents.MOUSEOUT);

		this.state = state;
		this.visibilityCallback = visibilityCallback;
		this.filterCallback = filterCallback;
	}

	@Override
	public void render(Context context, String value, SafeHtmlBuilder sb)
	{
//		if (StringUtils.isEmpty(value))
//		{
//			return;
//		}

		String placeholder;
		SafeStyles borderTopStyle = SafeStylesUtils.fromTrustedString("");

		switch (state)
		{
			case TOP:
				placeholder = Text.LANG.generalRangeFrom();
				break;
			case BOTTOM:
				placeholder = Text.LANG.generalRangeTo();
				borderTopStyle = SafeStylesUtils.fromTrustedNameAndValue("border-top", 0, com.google.gwt.dom.client.Style.Unit.PX);
				break;
			case SINGLE:
			default:
				placeholder = "";

		}

		if (visibilityCallback != null && !visibilityCallback.isFilterVisible())
		{
			sb.append(TEMPLATES.filterInvisible(this.value, placeholder, Text.LANG.tableHeaderFilterCellTooltip(), borderTopStyle));
		}
		else
		{
			sb.append(TEMPLATES.filter(this.value, placeholder, Text.LANG.tableHeaderFilterCellTooltip(), borderTopStyle));
		}
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater)
	{
		if (BrowserEvents.KEYDOWN.equals(event.getType()) && event.getKeyCode() == KeyCodes.KEY_ENTER)
		{
			event.preventDefault();
		}

		final InputElement element = getInputElement(parent);

		DOM.sinkEvents(element, Event.ONKEYUP | Event.ONFOCUS | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
		DOM.setEventListener(element, event1 ->
		{
			if (InputElement.is(event1.getEventTarget()))
			{
				InputElement element1 = event1.getEventTarget().cast();

				FilterCell.this.value = element1.getValue();

				if (filterCallback != null)
				{
					/* If the focus is lost */
					if (BrowserEvents.BLUR.equals(event1.getType()))
					{
						/* Let the callback know */
						try
						{
							filterCallback.onFilterEvent(true, FilterCell.this.state != FilterCellState.BOTTOM, FilterCell.this.value);
							element1.getParentElement().removeClassName(ValidationState.ERROR.getCssName());
						}
						catch (InvalidArgumentException e)
						{
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationCheckEditTextValue());
							element1.getParentElement().addClassName(ValidationState.ERROR.getCssName());
						}
					}
					/* If enter is pressed */
					else if ((BrowserEvents.KEYUP.equals(event1.getType()) && event1.getKeyCode() == KeyCodes.KEY_ENTER))
					{
						/* Let the callback know */
						try
						{
							filterCallback.onFilterEvent(true, FilterCell.this.state != FilterCellState.BOTTOM, FilterCell.this.value);
							element1.getParentElement().removeClassName(ValidationState.ERROR.getCssName());

							/* Then invoke the enter pressed method */
							filterCallback.onEnterPressed();
						}
						catch (InvalidArgumentException e)
						{
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationCheckEditTextValue());
							element1.getParentElement().addClassName(ValidationState.ERROR.getCssName());
						}
					}
				}
			}
		});
	}

	/**
	 * Get the input element.
	 *
	 * @param parent the cell parent element
	 * @return the input element
	 */
	protected InputElement getInputElement(Element parent)
	{
		DivElement div = parent.getChild(0).cast();
		return div.getFirstChildElement().cast();
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public void clear()
	{
		this.value = "";
	}

	public enum FilterCellState
	{
		SINGLE,
		TOP,
		BOTTOM
	}

	public interface VisibilityCallback
	{
		boolean isFilterVisible();
	}

	public interface FilterCallback
	{
		void onFilterEvent(boolean isUserInput, boolean isStart, String value) throws InvalidArgumentException;

		void onEnterPressed();

		class Range
		{
			private String  start;
			private String  end;
			private boolean isUserInput;

			public Range()
			{
			}

			public Range(String start, String end)
			{
				this.start = start;
				this.end = end;
			}

			public boolean isUserInput()
			{
				return isUserInput;
			}

			public Range setUserInput(boolean userInput)
			{
				isUserInput = userInput;
				return this;
			}

			public String getStart()
			{
				return start;
			}

			public Range setStart(String start)
			{
				this.start = start;
				return this;
			}

			public String getEnd()
			{
				return end;
			}

			public Range setEnd(String end)
			{
				this.end = end;
				return this;
			}

			public List<String> getValues()
			{
				Set<String> result = new TreeSet<>();

				if (!StringUtils.isEmpty(start))
					result.add(start);
				if (!StringUtils.isEmpty(end))
					result.add(end);

				return new ArrayList<>(result);
			}

			public boolean isEmpty()
			{
				return CollectionUtils.isEmpty(getValues());
			}
		}
	}
}
