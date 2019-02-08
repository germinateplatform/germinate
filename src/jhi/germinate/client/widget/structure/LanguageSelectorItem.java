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
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.Style;

/**
 * {@link LanguageSelectorItem} is one single entry in the {@link LanguageSelector}.
 *
 * @author Sebastian Raubach
 */
public class LanguageSelectorItem
{
	private LIElement     root    = Document.get().createLIElement();
	private AnchorElement anchor  = Document.get().createAnchorElement();
	private SpanElement   flag    = Document.get().createSpanElement();
	private SpanElement   country = Document.get().createSpanElement();

	public LanguageSelectorItem(String locale)
	{
		flag.addClassName(Style.combine(locale, Style.COUNTRY_FLAG, Style.LAYOUT_V_ALIGN_MIDDLE));

		String display = LocaleInfo.getLocaleNativeDisplayName(locale);

		country.setInnerText(display);
		country.addClassName(Style.LAYOUT_V_ALIGN_MIDDLE);

		anchor.setHref("#");
		anchor.setTitle(display);

		JavaScript.click(anchor, new ClickCallback()
		{
			@Override
			public void onSuccess(Event event)
			{
				GoogleAnalytics.trackEvent(GoogleAnalytics.Category.UI, "language", locale);
				Window.Location.replace(Window.Location.createUrlBuilder().setParameter(LocaleInfo.getLocaleQueryParam(), locale).buildString());
			}
		});

		root.appendChild(anchor);
		anchor.insertFirst(flag);
		anchor.appendChild(country);
	}

	public Element getElement()
	{
		return root;
	}
}
