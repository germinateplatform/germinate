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

import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.base.*;

/**
 * @author Sebastian Raubach
 */
public class CheckboxListItem extends AbstractAnchorListItem implements HasText
{
	private CheckBox box = new CheckBox()
	{
		@Override
		protected void onLoad()
		{
			super.onLoad();

			getElement().getFirstChildElement().getStyle().setWidth(100, Style.Unit.PCT);
		}
	};

	public CheckboxListItem(boolean value)
	{
		super();
		box.setValue(value);
		anchor.add(box);
		box.getElement().getStyle().setMargin(0, Style.Unit.PX);
	}

	public CheckboxListItem(final boolean value, final String text)
	{
		this(value);
		setText(text);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return box.addClickHandler(handler);
	}

	@Override
	protected void delegateEvent(Widget target, GwtEvent<?> event)
	{
	}

	public void setValue(boolean value)
	{
		box.setValue(value);
	}

	public Boolean getValue()
	{
		return box.getValue();
	}

	@Override
	public void setText(final String text)
	{
		box.setText(text);
	}

	@Override
	public String getText()
	{
		return box.getText();
	}
}