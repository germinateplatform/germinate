/*
 *  Copyright 2018 Information and Computational Sciences,
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

import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.base.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class MarkedItemPageHeader extends Composite implements HasText, HasSubText
{
	private Heading                 checkbox;
	private Heading                 header;
	private MarkedItemList.ItemType type = MarkedItemList.ItemType.ACCESSION;
	private String                  id;
	private HandlerRegistration     register;

	public MarkedItemPageHeader()
	{
		header = new Heading(HeadingSize.H2);
		checkbox = new Heading(HeadingSize.H2);
		checkbox.addStyleName(Style.combine(Style.LAYOUT_DISPLAY_INLINE_BLOCK, Emphasis.PRIMARY.getCssName(), Style.CURSER_POINTER, Style.MDI, Style.FA_FIXED_WIDTH, Style.MDI_CHECKBOX_BLANK_OUTLINE));
		header.addStyleName(Style.LAYOUT_DISPLAY_INLINE_BLOCK);

		FlowPanel panel = new FlowPanel();
		panel.add(header);
		panel.add(checkbox);

		initWidget(panel);
	}

	private void update()
	{
		if (MarkedItemList.get(type).contains(id))
		{
			checkbox.removeStyleName(Style.MDI_CHECKBOX_BLANK_OUTLINE);
			checkbox.addStyleName(Style.MDI_CHECKBOX_MARKED);
		}
		else
		{
			checkbox.addStyleName(Style.MDI_CHECKBOX_BLANK_OUTLINE);
			checkbox.removeStyleName(Style.MDI_CHECKBOX_MARKED);
		}
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		JavaScript.click(checkbox, new ClickCallback()
		{
			@Override
			public void onSuccess(Event event)
			{
				if (MarkedItemList.contains(type, id))
					MarkedItemList.remove(type, id);
				else
					MarkedItemList.add(type, id);
			}
		});

		register = GerminateEventBus.BUS.addHandler(MarkedItemListEvent.TYPE, e -> {
			if (e.getType() == type)
				update();
		});

		update();
	}

	@Override
	protected void onUnload()
	{
		if (register != null)
			register.removeHandler();

		super.onUnload();
	}

	public void setId(String id)
	{
		this.id = id;
		update();
	}

	public MarkedItemList.ItemType getType()
	{
		return type;
	}

	public void setType(MarkedItemList.ItemType type)
	{
		this.type = type;
		update();
	}

	@Override
	public String getText()
	{
		return header.getText();
	}

	@Override
	public void setText(String s)
	{
		header.setText(s);
	}

	@Override
	public String getSubText()
	{
		return header.getSubText();
	}

	@Override
	public void setSubText(String s)
	{
		header.setSubText(s);
	}
}
