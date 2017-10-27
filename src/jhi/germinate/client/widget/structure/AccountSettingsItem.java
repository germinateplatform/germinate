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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;

import jhi.germinate.shared.Style;

/**
 * {@link AccountSettingsItem} is one single entry in the {@link LanguageSelector}.
 *
 * @author Sebastian Raubach
 */
public class AccountSettingsItem
{
	private LIElement     root   = Document.get().createLIElement();
	private AnchorElement anchor = Document.get().createAnchorElement();
	private SpanElement   icon   = Document.get().createSpanElement();
	private SpanElement   name   = Document.get().createSpanElement();

	public AccountSettingsItem(String text, String iconStyle, ClickHandler handler)
	{
		icon.addClassName(Style.combine(iconStyle, Style.MDI, Style.FA_FIXED_WIDTH, Style.FA_LG, Style.LAYOUT_V_ALIGN_MIDDLE));

		name.setInnerText(text);
		name.getStyle().setTextTransform(com.google.gwt.dom.client.Style.TextTransform.CAPITALIZE);

		anchor.setTitle(text);
		anchor.setHref("#");

		root.appendChild(anchor);
		anchor.insertFirst(icon);
		anchor.appendChild(name);

		GQuery.$(anchor).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				handler.onClick(null);
				return false;
			}
		});
	}

	public Element getElement()
	{
		return root;
	}
}
