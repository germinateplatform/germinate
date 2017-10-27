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
import com.google.gwt.user.client.ui.*;

/**
 * {@link HTMLUtils} contains methods to create common {@link HTML} elements. Using methods from this class will prevent XSS attacks, since every
 * possibly harmful HTML/Script code is stripped from the controls before creating them.
 *
 * @author Sebastian Raubach
 */
public final class HTMLUtils
{
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
}
