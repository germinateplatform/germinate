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
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Panel;

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class NewsPanel extends Composite
{
	interface NewsItemUiBinder extends UiBinder<Panel, NewsPanel>
	{
	}

	private static NewsItemUiBinder ourUiBinder = GWT.create(NewsItemUiBinder.class);

	@UiField
	Anchor  link;
	@UiField
	ULPanel list;

	public NewsPanel(List<News> news, boolean trim)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		if (GerminateSettingsHolder.isPageAvailable(Page.NEWS))
			link.setHref("#" + Page.NEWS.name());
		else
			link.setHref("");

		if (news != null)
		{
			for (News n : news)
				list.add(new NewsPanelItem(n, trim), false);
		}
	}
}