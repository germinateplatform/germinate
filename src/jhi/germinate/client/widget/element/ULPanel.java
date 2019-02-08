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

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.enums.*;

/**
 * {@link ULPanel} is a basic implementation of an unordered list (ul)
 *
 * @author Sebastian Raubach
 */
public class ULPanel extends ComplexPanel
{
	private final UListElement list;

	/**
	 * Creates a new unordered list instance
	 */
	public ULPanel()
	{
		list = Document.get().createULElement();
		setElement(list);
	}

	public void add(Widget child, boolean asLi)
	{
		if (asLi)
			add(child);
		else
		{
			Element d = Document.get().createDivElement().cast();
			list.appendChild(d);
			super.add(child, d);
		}
	}

	/**
	 * Adds the given widget as a new li to the ul
	 */
	@Override
	public void add(Widget child)
	{
		Element li = Document.get().createLIElement().cast();
		list.appendChild(li);
		super.add(child, li);
	}

	/**
	 * Adds the given widget as a new li to the ul with the given style class
	 *
	 * @param child The widget to add
	 * @param style The IconStyle
	 * @param type  The FileType
	 */
	public void add(Widget child, FileType.IconStyle style, FileType type, boolean longRunning)
	{
		LIElement li = Document.get().createLIElement();

		if (style == FileType.IconStyle.IMAGE)
		{
			li.setClassName(type.getStyle(style));
		}
		else
		{
			Element i = Document.get().createElement("i");
			i.setClassName(Style.combine(Style.MDI, Style.MDI_LG, Style.FA_FIXED_WIDTH, Style.LAYOUT_V_ALIGN_MIDDLE, type.getStyle(style)));
			i.getStyle().setPaddingRight(7, com.google.gwt.dom.client.Style.Unit.PX);

			li.appendChild(i);
			child.addStyleName(Style.LAYOUT_V_ALIGN_MIDDLE);
		}

		list.appendChild(li);
		super.add(child, li);

		if (longRunning)
		{
			Element i = Document.get().createElement("i");
			i.setClassName(Style.combine(Style.MDI, Style.MDI_LG, Style.LAYOUT_V_ALIGN_MIDDLE, Style.MDI_TIMER_SAND));
			i.getStyle().setPaddingLeft(7, com.google.gwt.dom.client.Style.Unit.PX);
			i.setTitle(Text.LANG.notificationLongRunning());
			i.setAttribute("data-toggle", "tooltip");
			i.setAttribute("data-placement", "top");
			li.appendChild(i);

			Scheduler.get().scheduleDeferred(() -> tooltip(i));
		}
	}

	private native void tooltip(final Element e) /*-{
		$wnd.jQuery(e).tooltip();
	}-*/;

	/**
	 * Adds the given widget as a new li to the ul with the given Material Design Icon style
	 *
	 * @param child The widget to add
	 * @param mdi   The Material Design Icon style
	 */
	public void add(Widget child, String mdi)
	{
		LIElement li = Document.get().createLIElement();

		Element i = Document.get().createElement("i");
		i.setClassName(Style.combine(Style.MDI, Style.MDI_LG, Style.FA_FIXED_WIDTH, Style.LAYOUT_V_ALIGN_MIDDLE, mdi));
		i.getStyle().setPaddingRight(7, com.google.gwt.dom.client.Style.Unit.PX);

		li.appendChild(i);
		//		li.addClassName(Style.FA_ICONLIST);
		child.addStyleName(Style.LAYOUT_V_ALIGN_MIDDLE);

		list.appendChild(li);
		super.add(child, li);
	}
}
