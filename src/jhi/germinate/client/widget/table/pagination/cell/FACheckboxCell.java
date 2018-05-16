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
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.database.*;

/**
 * FACheckboxCell extends AbstractEditableCell. The idea is that it's used as a table header that shows a checkbox representing the state of the table
 * items. It can be clicked to change the table item state. It will use a Material Design Icon style icon to represent the state.
 *
 * @author Sebastian Raubach
 */
public class FACheckboxCell<T extends DatabaseObject> extends AbstractEditableCell<Boolean, Boolean>
{

	/**
	 * An html string representation of a checked input box.
	 */
	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<span class='" + Style.combine(Style.MDI, Style.MDI_LG, Style.FA_FIXED_WIDTH, Style.MDI_CHECKBOX_MARKED, Emphasis.PRIMARY.getCssName()) + "'/>");

	/**
	 * An html string representation of an unchecked input box.
	 */
	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<span class='" + Style.combine(Style.MDI, Style.MDI_LG, Style.FA_FIXED_WIDTH, Style.MDI_CHECKBOX_BLANK_OUTLINE) + "'/>");

	private final boolean                                             dependsOnSelection;
	private final boolean                                             handlesSelection;
	private final DatabaseObjectPaginationTable.ContextMenuHandler<T> handler;

	/**
	 * Construct a new {@link FACheckboxCell} that optionally controls selection.
	 *
	 * @param dependsOnSelection true if the cell depends on the selection state
	 * @param handlesSelection   true if the cell modifies the selection state
	 */
	public FACheckboxCell(boolean dependsOnSelection, boolean handlesSelection, DatabaseObjectPaginationTable.ContextMenuHandler<T> handler)
	{
		super(BrowserEvents.CLICK);
		this.dependsOnSelection = dependsOnSelection;
		this.handlesSelection = handlesSelection;
		this.handler = handler;
	}

	@Override
	public boolean dependsOnSelection()
	{
		return dependsOnSelection;
	}

	@Override
	public boolean handlesSelection()
	{
		return handlesSelection;
	}

	@Override
	public boolean isEditing(Context context, Element parent, Boolean value)
	{
		// A checkbox is never in "edit mode". There is no intermediate state
		// between checked and unchecked.
		return false;
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater)
	{
		String type = event.getType();

		if (BrowserEvents.CLICK.equals(type))
		{
			int bufferArea = 5;
			SpanElement span = parent.getChild(0).cast();
			int x = span.getAbsoluteLeft();
			int y = span.getAbsoluteTop();
			int width = span.getOffsetWidth();
			int height = span.getOffsetHeight();
			int mouseX = event.getClientX() + Window.getScrollLeft();
			int mouseY = event.getClientY() + Window.getScrollTop();

			if (mouseX >= x - bufferArea && mouseX <= x + width + bufferArea && mouseY >= y - bufferArea && mouseY <= y + height + bufferArea)
			{
				/* On click, open the context menu of the handler and let it take care of everything else */
				handler.handleContextMenuEvent(null, event.getClientX(), event.getClientY(), false);
			}
		}
	}

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb)
	{
		// Get the view data.
		Boolean viewData = getViewData(context.getKey());
		if (viewData != null && viewData.equals(value))
		{
			clearViewData(context.getKey());
			viewData = null;
		}

		if (value != null && ((viewData != null) ? viewData : value))
		{
			sb.append(INPUT_CHECKED);
		}
		else
		{
			sb.append(INPUT_UNCHECKED);
		}
	}
}
