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

import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.base.*;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.html.*;

/**
 * Represents a list item with text contents
 *
 * @author Sven Jacobs
 */
public class TextListItem extends AbstractListItem implements HasWidgets, HasText, HasClickHandlers
{
	private boolean isAttached = false;
	private Span text;

	/**
	 * Creates a default list item element
	 */
	public TextListItem()
	{
	}

	/**
	 * Creates a default list item element with the desired text
	 *
	 * @param text desired text for list item
	 */
	public TextListItem(final String text)
	{
		this();
		setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setText(final String text)
	{
		if (!isAttached)
		{
			isAttached = true;
			this.text = new Span(text);
			add(this.text, (Element) getElement());
		}
		else
		{
			this.text.setText(text);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText()
	{
		return text.getText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onAttach()
	{
		super.onAttach();

		// Adding styles to the list item depending on the parent
		if (getParent() != null)
		{
			if (getParent() instanceof MediaList)
			{
				addStyleName(Styles.MEDIA);
			}
		}
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler)
	{
		return addDomHandler(handler, ClickEvent.getType());
	}
}
