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

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.util.*;

/**
 * @author Sebastian Raubach
 */
public class CategoryPanel extends Composite
{
	interface CategoryPanelUiBinder extends UiBinder<HTMLPanel, CategoryPanel>
	{
	}

	private static CategoryPanelUiBinder ourUiBinder = GWT.create(CategoryPanelUiBinder.class);

	@UiField
	DivElement panel;

	@UiField
	DivElement heading;

	@UiField
	Element icon;

	@UiField
	AnchorElement anchor;

	@UiField
	SpanElement detailsLabel;

	private String prevIcon;

	public CategoryPanel()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		String color = GerminateSettingsHolder.getCategoricalColor(0);

		setColor(color);
	}

	public void setColor(String color)
	{
		panel.getStyle().setBorderColor(color);
		heading.getStyle().setBackgroundColor(color);
		heading.getStyle().setBorderColor(color);
		heading.getStyle().setColor("white");
		anchor.getStyle().setColor(color);
	}

	public void setText(String details)
	{
		this.detailsLabel.setInnerText(details);
	}

	public void setIcon(String icon)
	{
		if (prevIcon != null)
			this.icon.removeClassName(prevIcon);
		prevIcon = icon;

		this.icon.addClassName(icon);
	}

	public void setAnchor(String url)
	{
		anchor.setHref(url);
	}

	public AnchorElement getAnchor()
	{
		return anchor;
	}
}