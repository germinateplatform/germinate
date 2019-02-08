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
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class NewsPanelItem extends Composite
{
	interface NewsPanelItemUiBinder extends UiBinder<HTMLPanel, NewsPanelItem>
	{
	}

	private static NewsPanelItemUiBinder ourUiBinder = GWT.create(NewsPanelItemUiBinder.class);

	@UiField
	SpanElement icon;

	@UiField
	Element title;

	@UiField
	SpanElement date;

	@UiField
	HTML content;

	@UiField
	AnchorElement anchor;

	NewsPanelItem(News news, boolean trim)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		title.setInnerText(news.getTitle());
		date.setInnerText(DateUtils.getLocalizedDate(news.getCreatedOn()));

		String text = news.getContent();

		String c = trim ? StringUtils.getWordsUntil(text.replaceAll("\\<.*?>", " "), 100) : text;

		if (trim)
			content.setText(c);
		else
			content.setHTML(c);

		switch (news.getType())
		{
			case data:
				icon.addClassName(Style.MDI_DATABASE);
				break;
			case updates:
				icon.addClassName(Style.MDI_REFRESH);
				break;
			case general:
			default:
				icon.addClassName(Style.MDI_NEWSPAPER);
		}

		anchor.setHref(news.getHyperlink());

		JavaScript.click(anchor, false, new ClickCallback()
		{
			@Override
			public void onSuccess(Event event)
			{
				boolean preventDefault = false;
				String link = news.getHyperlink();
				if (link.startsWith("#"))
				{
					try
					{
						Page p = Page.valueOf(link.substring(1));
						preventDefault = true;

						LongParameterStore.Inst.get().put(Parameter.newsId, news.getId());

						if (p.name().equals(History.getToken()))
							History.fireCurrentHistoryState();
						else
							History.newItem(p.name());
					}
					catch (Exception ex)
					{
					}
				}

				if (preventDefault)
				{
					event.stopPropagation();
					event.preventDefault();
				}
			}
		});
	}
}