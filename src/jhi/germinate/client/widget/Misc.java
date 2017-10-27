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

package jhi.germinate.client.widget;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * {@link Misc} is a utility class containing miscellaneous methods.
 *
 * @author Sebastian Raubach
 */
public class Misc
{
	/**
	 * Checks if the pressed key is a letter or digit or a backspace character
	 *
	 * @param charPressed The pressed key code
	 * @return True of the key code is either a letter, a digit or the backspace key
	 */
	public static boolean checkKeyEvent(char charPressed)
	{
		return Character.isLetterOrDigit(charPressed) || charPressed == KeyCodes.KEY_BACKSPACE;
	}

	/**
	 * Makes the given {@link Element}s use the same width based on the widest one. Their width is determined by calling {@link
	 * Element#getOffsetWidth()}. Afterwards the new width is set using {@link com.google.gwt.dom.client.Style#setWidth(double, Unit)}.
	 *
	 * @param elements The {@link Element} to make of equal width
	 */
	public static void makeEqualWidth(final Widget... elements)
	{
		Scheduler.get().scheduleDeferred(() ->
		{
			int maxWidth = 0;

			for (Widget element : elements)
			{
				if (element == null)
					continue;

				maxWidth = Math.max(maxWidth, element.getOffsetWidth());
			}

			for (Widget element : elements)
			{
				if (element == null)
					continue;

				element.getElement().getStyle().setWidth(maxWidth, Unit.PX);
			}
		});
	}

	/**
	 * Makes the given {@link Element}s use the same width based on the widest one. Their width is determined by calling {@link
	 * Element#getOffsetWidth()}. Afterwards the new width is set using {@link com.google.gwt.dom.client.Style#setWidth(double, Unit)}.
	 *
	 * @param elements The {@link Element} to make of equal width
	 */
	public static void makeEqualWidth(final Element... elements)
	{
		Scheduler.get().scheduleDeferred(() ->
		{
			int maxWidth = 0;

			for (Element element : elements)
			{
				if (element == null)
					continue;

				maxWidth = Math.max(maxWidth, element.getOffsetWidth());
			}

			for (Element element : elements)
			{
				if (element == null)
					continue;

				element.getStyle().setWidth(maxWidth, Unit.PX);
			}
		});
	}

	/**
	 * Checks if the current browser is IE.
	 *
	 * @return <code>true</code> if the user-agent contains either "msie" or "trident". This is the expected behaviour for IE <= 11. If newer versions
	 * of IE use a different user-agent, this method will return <code>false</code>.
	 */
	public static boolean isIE()
	{
		String userAgent = Window.Navigator.getUserAgent().toLowerCase();

		return userAgent.contains("msie") || userAgent.contains("trident");
	}
}
