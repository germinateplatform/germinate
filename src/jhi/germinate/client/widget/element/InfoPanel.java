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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class InfoPanel
{
	interface InfoPanelUiBinder extends UiBinder<AnchorElement, InfoPanel>
	{
	}

	private static InfoPanelUiBinder ourUiBinder = GWT.create(InfoPanelUiBinder.class);

	@UiField
	DivElement panel;

	@UiField
	DivElement heading;

	@UiField
	Element icon;

	@UiField
	DivElement label;

	@UiField
	DivElement category;

	@UiField
	AnchorElement anchor;

	@UiField
	SpanElement detailsLabel;

	public InfoPanel(String value, String category, String icon, String details, String color, Page link)
	{
		if (GerminateSettingsHolder.isPageAvailable(link))
			create(value, category, icon, details, color, "#" + link.name());
		else
			create(value, category, icon, details, color, null);
	}

	public InfoPanel(String value, String category, String icon, String details, String color, String link)
	{
		create(value, category, icon, details, color, link);
	}

	private void create(String value, String category, String icon, String details, String color, String link)
	{
		ourUiBinder.createAndBindUi(this);

		this.label.setInnerText(value);
		this.category.setInnerText(category);
		this.icon.addClassName(icon);
		this.detailsLabel.setInnerText(details);

		panel.getStyle().setBorderColor(color);
		heading.getStyle().setBackgroundColor(color);
		heading.getStyle().setBorderColor(color);
		heading.getStyle().setColor("white");
		anchor.getStyle().setColor(color);

		if (StringUtils.isEmpty(link))
			anchor.removeFromParent();
		else
			anchor.setHref(UriUtils.fromString(link));
	}

	public Element getElement()
	{
		return anchor;
	}
}