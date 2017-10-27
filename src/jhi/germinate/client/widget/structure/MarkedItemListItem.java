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

package jhi.germinate.client.widget.structure;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link MarkedItemListItem} is one single entry in the {@link MarkedItemList}.
 *
 * @author Sebastian Raubach
 */
public class MarkedItemListItem
{
	private LIElement     root         = Document.get().createLIElement();
	private AnchorElement anchor       = Document.get().createAnchorElement();
	private SpanElement   icon         = Document.get().createSpanElement();
	private SpanElement   name         = Document.get().createSpanElement();
	private SpanElement   countWrapper = Document.get().createSpanElement();
	private SpanElement   trash        = Document.get().createSpanElement();
	private SpanElement   count        = Document.get().createSpanElement();

	private MarkedItemList.ItemType type;

	public MarkedItemListItem(MarkedItemList.ItemType type)
	{
		this.type = type;

		icon.addClassName(Style.combine(type.getIcon(), Style.MDI, Style.FA_FIXED_WIDTH, Style.FA_LG, Style.LAYOUT_V_ALIGN_MIDDLE));
		trash.addClassName(Style.combine(Style.MDI, Style.FA_FIXED_WIDTH, Style.FA_LG, Style.MDI_DELETE));

		String display = type.getDisplayName();

		name.setInnerText(display);

		anchor.setTitle(display);
		anchor.setHref("#");
		GQuery.$(anchor).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				ItemTypeParameterStore.Inst.get().put(Parameter.markedItemType, type);
				History.newItem(Page.MARKED_ITEMS.name());
				return false;
			}
		});

		GQuery.$(trash).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				MarkedItemList.clear(type);
				return false;
			}
		});

		countWrapper.getStyle().setMarginLeft(10, com.google.gwt.dom.client.Style.Unit.PX);
		countWrapper.getStyle().setLineHeight(28, com.google.gwt.dom.client.Style.Unit.PX);
		count.addClassName(Styles.BADGE);

		root.appendChild(anchor);
		anchor.insertFirst(icon);
		anchor.appendChild(name);
		anchor.appendChild(countWrapper);
		countWrapper.appendChild(count);
		countWrapper.appendChild(trash);
		trash.setTitle(Text.LANG.generalClear());
		trash.getStyle().setMarginLeft(5, com.google.gwt.dom.client.Style.Unit.PX);

		// Listen to shopping cart changes
		GerminateEventBus.BUS.addHandler(MarkedItemListEvent.TYPE, event -> updateCount());

		updateCount();
	}

	public Element getElement()
	{
		return root;
	}

	private void updateCount()
	{
		count.setInnerText(NumberUtils.INTEGER_FORMAT.format(MarkedItemList.get(type).size()));
	}
}
