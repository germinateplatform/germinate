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
import com.google.gwt.user.client.ui.*;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class AnchorWithContent extends Panel
{
	private List<Widget> children = new ArrayList<>();

	public AnchorWithContent()
	{
		this(Document.get().createAnchorElement());
	}

	protected AnchorWithContent(Element elem)
	{
		setElement(elem);
	}

	/**
	 * Adds a widget to this panel.
	 *
	 * @param w the child widget to be added
	 */
	@Override
	public void add(Widget w)
	{
		// Detach new child.
		if (w != null)
		{
			w.removeFromParent();

			children.add(w);
			getContainerElement().appendChild(w.getElement());
			// DOM.appendChild(getContainerElement(), w.getElement());

			adopt(w);
		}
	}

	@Override
	public Iterator<Widget> iterator()
	{
		return new Iterator<Widget>()
		{
			private Iterator<Widget> it = children.iterator();
			private Widget current = null;

			public boolean hasNext()
			{
				return it.hasNext();
			}

			public Widget next()
			{
				current = it.next();
				return current;
			}

			public void remove()
			{
				if (current != null)
					AnchorWithContent.this.remove(current);

				it.remove();
			}
		};

	}

	@Override
	public boolean remove(Widget w)
	{
		// Validate.
		if (!children.contains(w))
		{
			return false;
		}

		// Orphan.
		orphan(w);

		// Physical detach.
		getContainerElement().removeChild(w.getElement());

		// Logical detach.
		children.remove(w);
		return true;
	}

	/**
	 * Override this method to specify that an element other than the root element be the container for the panel's child widget. This can be useful
	 * when you want to create a simple panel that decorates its contents.
	 * <p/>
	 * Note that this method continues to return the {@link com.google.gwt.dom.client.Element} class defined in the <code>User</code> module to
	 * maintain backwards compatibility.
	 *
	 * @return the element to be used as the panel's container
	 */
	protected Element getContainerElement()
	{
		return getElement();
	}

	public void setHref(String href)
	{
		getElement().setAttribute("href", href);
	}

	public String getHref()
	{
		return getElement().getAttribute("href");
	}

	public void setTarget(String frameName)
	{
		getElement().setAttribute("target", frameName);
	}
}