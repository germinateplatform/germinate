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
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;

/**
 * @author Sebastian Raubach
 */
public class LanguageSelector
{
	private static boolean isInitialized = false;
	public static  boolean hasLanguages  = false;

	public static void init()
	{
		if (!isInitialized)
		{
			isInitialized = true;

			RootPanel p = RootPanel.get(Id.STRUCTURE_LANGUAGE_SELECTOR_UL);
			p.setVisible(true);
			addTo(p.getElement());
			p.removeFromParent();
		}
	}

	public static void addTo(Element element)
	{
		UListElement list = Document.get().createULElement();

		list.addClassName(Style.combine(Styles.DROPDOWN_MENU, Style.BOOTSTRAP_DROPDOWN_ALERT));
		/* Get all the supported locales */
		final List<String> locales = new ArrayList<>(Arrays.asList(LocaleInfo.getAvailableLocaleNames()));

		/* Remove the default locale, since it's equal to en_GB */
		locales.remove("default");

		/* Sort the remaining locales */
		Collections.sort(locales);

		if (locales.size() > 1)
		{
			hasLanguages = true;

			for (final String locale : locales)
			{
				LanguageSelectorItem item = new LanguageSelectorItem(locale);

				list.appendChild(item.getElement());
			}

			element.appendChild(list);
		}
		else
		{
			element.removeFromParent();
		}
	}
}
