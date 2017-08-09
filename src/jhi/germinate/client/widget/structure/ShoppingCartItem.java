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

package jhi.germinate.client.widget.structure;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link ShoppingCartItem} is one single entry in the {@link LanguageSelector}.
 *
 * @author Sebastian Raubach
 */
public class ShoppingCartItem
{
	private LIElement     root         = Document.get().createLIElement();
	private AnchorElement anchor       = Document.get().createAnchorElement();
	private SpanElement   icon         = Document.get().createSpanElement();
	private SpanElement   name         = Document.get().createSpanElement();
	private SpanElement   countWrapper = Document.get().createSpanElement();
	private SpanElement   count        = Document.get().createSpanElement();

	private ShoppingCart.ItemType type;

	public ShoppingCartItem(ShoppingCart.ItemType type)
	{
		this.type = type;

		icon.addClassName(Style.combine(type.getIcon(), Style.MDI, Style.FA_FIXED_WIDTH, Style.FA_LG, Style.LAYOUT_V_ALIGN_MIDDLE));

		String display = type.getDisplayName();

		name.setInnerText(display);

		anchor.setTitle(display);
		anchor.setHref("#");
		GQuery.$(anchor).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				ItemTypeParameterStore.Inst.get().put(Parameter.shoppingCartItemType, type);
				History.newItem(Page.SHOPPING_CART.name());
				return false;
			}
		});

		countWrapper.addClassName(Styles.PULL_RIGHT);
		countWrapper.getStyle().setLineHeight(28, com.google.gwt.dom.client.Style.Unit.PX);
		count.addClassName(Styles.BADGE);

		root.appendChild(anchor);
		anchor.insertFirst(icon);
		anchor.appendChild(name);
		anchor.appendChild(countWrapper);
		countWrapper.appendChild(count);

		// Listen to shopping cart changes
		GerminateEventBus.BUS.addHandler(ShoppingCartEvent.TYPE, event -> updateCount());

		updateCount();
	}

	public Element getElement()
	{
		return root;
	}

	private void updateCount()
	{
		count.setInnerText(Integer.toString(ShoppingCart.get(type).size()));
	}
}
