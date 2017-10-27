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

package jhi.germinate.client.widget.news;

import com.google.gwt.core.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class ProjectItem extends Composite
{
	interface NewsItemUiBinder extends UiBinder<ThumbnailPanel, ProjectItem>
	{
	}

	private static NewsItemUiBinder ourUiBinder = GWT.create(NewsItemUiBinder.class);

	@UiField
	SimplePanel image;

	@UiField
	Heading heading;

	@UiField
	ParagraphPanel paragraph;

	@UiField
	Anchor anchor;

	public ProjectItem(News news)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		if (!StringUtils.isEmpty(news.getImage()))
			image.getElement().getStyle().setBackgroundImage("url(" + UriUtils.fromTrustedString(news.getImage()).asString() + ")");
		else
			image.setVisible(false);

		heading.setText(news.getTitle());

		paragraph.setText(news.getContent());

		anchor.addStyleName(Style.combine(Styles.BTN, ButtonType.PRIMARY.getCssName()));
		anchor.setHref(news.getHyperlink());
		anchor.setTarget("_blank");
	}
}