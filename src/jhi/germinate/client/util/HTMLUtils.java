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

package jhi.germinate.client.util;

import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.shared.datastructure.*;

/**
 * {@link HTMLUtils} contains methods to create common {@link HTML} elements. Using methods from this class will prevent XSS attacks, since every
 * possibly harmful HTML/Script code is stripped from the controls before creating them.
 *
 * @author Sebastian Raubach
 */
public final class HTMLUtils
{
	public static SafeHtml sanitize(String input)
	{
		return SimpleHtmlSanitizer.sanitizeHtml(input);
	}

//	/**
//	 * Creates an {@link HTML} element representing a <code>&lt;hX&gt;</code> element of type X
//	 *
//	 * @param headerType The type of heading, from 1 to 6 inclusive
//	 * @param message    The actual content of the heading
//	 * @return The created {@link HTML} element
//	 * @see Document#createHElement(int)
//	 */
//	public static HTML createHeading(int headerType, String message)
//	{
//		HeadingElement el = Document.get().createHElement(headerType);
//		el.setInnerText(stripHtmlTags(message));
//
//		return getElement(el);
//	}
//
//	/**
//	 * Creates an {@link HTML} element representing a <code>&lt;hX&gt;</code> element of type X
//	 *
//	 * @param headerType The type of heading, from 1 to 6 inclusive
//	 * @param message    The actual content of the heading
//	 * @param button     The {@link SimpleRadioButton}
//	 * @return The created {@link HTML} element
//	 * @see Document#createHElement(int)
//	 */
//	public static HTML createHeading(int headerType, String message, SimpleRadioButton button)
//	{
//		button.getElement().getStyle().setFloat(Float.LEFT);
//		button.getElement().getStyle().setMarginTop(16, Unit.PX);
//
//		HeadingElement el = Document.get().createHElement(headerType);
//		el.setInnerText(stripHtmlTags(message));
//		el.getStyle().setCursor(Cursor.DEFAULT);
//		el.getStyle().setClear(Clear.NONE);
//
//		return getElement(el);
//	}
//
//	/**
//	 * Creates an {@link HTML} element representing a <code>&lt;hX&gt;</code> element of type X
//	 *
//	 * @param headerType The type of heading, from 1 to 6 inclusive
//	 * @param message    The actual content of the heading
//	 * @return The created {@link HTML} element
//	 * @see Document#createHElement(int)
//	 */
//	public static HTML createHeading(int headerType, SafeHtml message)
//	{
//		HeadingElement el = Document.get().createHElement(headerType);
//		el.setInnerText(stripHtmlTags(message.asString()));
//
//		return getElement(el);
//	}

	/**
	 * Creates an {@link HTML} element representing a <code>&lt;br/&gt;</code> element
	 *
	 * @return The created {@link HTML} element
	 * @see Document#createBRElement()
	 */
	public static HTML createBR()
	{
		return getElement(Document.get().createBRElement());
	}

	/**
	 * Creates an {@link HTML} element representing a <code>&lt;div style='clear:both;'&gt;&lt;div&gt;</code> element
	 *
	 * @return The created {@link HTML} element
	 * @see Document#createBRElement()
	 */
	public static HTML createClearBoth()
	{
		DivElement div = Document.get().createDivElement();
		div.getStyle().setClear(Clear.BOTH);
		return getElement(div);
	}

	/**
	 * Creates an {@link HTML} element representing a <code>&lt;b&gt;MESSAGE&lt;/b&gt;</code> element
	 *
	 * @param message The actual content of the bold tag
	 * @return The created {@link HTML} element
	 * @see Document#createElement(String)
	 */
	public static Widget createBold(String message)
	{
		Element el = Document.get().createElement("b");
		el.setInnerText(stripHtmlTags(message));

		return getElement(el);
	}

	/**
	 * Creates an {@link HTML} element from the given {@link Element}
	 *
	 * @param element The {@link Element}
	 * @return The created {@link HTML} element
	 */
	private static HTML getElement(Element element)
	{
		return new HTML(element.getString());
	}

	/**
	 * Removes all the html tags from the given input
	 *
	 * @param input The unstripped string
	 * @return The stripped string
	 */
	public static synchronized String stripHtmlTags(String input)
	{
		return new HTML(input).getText();
	}

	/**
	 * Checks if the hyperlink at the given index of the array is valid.
	 * <p/>
	 * Will call {@link #isValidHyperlink(String)} if the given entry exists.
	 *
	 * @param hyperlinks An array potentially containing a valid link
	 * @param index      The index of the potential link
	 * @return <code>true</code> If none of the checks fails
	 */
	public static boolean isValidHyperlink(String[] hyperlinks, int index)
	{
		/* If there are no links or the given index is invalid */
		if (hyperlinks == null || index < 0 || index >= hyperlinks.length)
			return false;
		else
			return isValidHyperlink(hyperlinks[index]);
	}

	/**
	 * Checks if the given hyperlink is valid. This basically involves two calls. <ul> <li>A <code>null</code> check</li> <li>And one checking if the
	 * link represents a {@link Page}. If so it will also check if the given page is available in this instance of Germinate.</li> </ul>
	 *
	 * @param link The hyperlink to check
	 * @return <code>true</code> if none of the checks fails
	 */
	public static boolean isValidHyperlink(String link)
	{
		/* If there is nothing, then it's not valid */
		if (link == null)
			return false;
		/* If it's an empty link, then that is valid */
		else if (link.equals(""))
			return true;
		/* If it represents an internal page */
		else if (link.startsWith("#") && link.length() > 1)
		{
			try
			{
				/* Try to see if it's a valid page */
				Page page = Page.valueOf(link.substring(1));

				return GerminateSettingsHolder.isPageAvailable(page);
			}
			catch (IllegalArgumentException e)
			{
				/* If it's not a valid internal page, return false */
				return false;
			}
		}
		/* If it's not null, not empty and not an internal page */
		else
		{
			try
			{
				/* Still try to see if it might be an internal page */
				Page page = Page.valueOf(link);

                /* If so, check if it is available */
				return GerminateSettingsHolder.isPageAvailable(page);
			}
			catch (IllegalArgumentException e)
			{
				/* If it's not a valid internal page, return true (might be
				 * anything) */
				return true;
			}
		}
	}
}
