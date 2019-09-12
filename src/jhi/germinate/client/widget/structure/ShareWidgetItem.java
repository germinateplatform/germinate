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

package jhi.germinate.client.widget.structure;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link ShareWidgetItem} is one single entry in the {@link LanguageSelector}.
 *
 * @author Sebastian Raubach
 */
public class ShareWidgetItem
{
	private LIElement     root   = Document.get().createLIElement();

	private HasHyperlinkButton.HyperlinkPopupOptions hyperlinkPopupOptions;

	public ShareWidgetItem(ShareUtils.ShareType type)
	{
		SpanElement icon = Document.get().createSpanElement();
		icon.addClassName(Style.combine(type.getIcon(), Style.MDI, Style.FA_FIXED_WIDTH, Style.MDI_LG, Style.LAYOUT_V_ALIGN_MIDDLE));

		String display = type.getTitle();

		SpanElement name = Document.get().createSpanElement();
		name.setInnerText(display);

		AnchorElement anchor = Document.get().createAnchorElement();
		anchor.setTitle(display);
		anchor.setHref("#");
		JavaScript.click(anchor, new ClickCallback()
		{
			@Override
			public void onSuccess(Event event)
			{
				/* Track that the user clicked help */
				GoogleAnalytics.trackEvent(GoogleAnalytics.Category.SHARE, "#" + History.getToken(), type.name());

				ShareUtils.openUrl(type, getHyperlinkURL());
			}
		});

		if (type == ShareUtils.ShareType.SHARE_LINK)
		{
			GerminateEventBus.BUS.addHandler(MainContentChangeEvent.TYPE, event ->
			{
				Composite composite = event.getComposite();
				if (composite instanceof HasHyperlinkButton)
					hyperlinkPopupOptions = ((HasHyperlinkButton) composite).getHyperlinkOptions();
				else
					hyperlinkPopupOptions = new HasHyperlinkButton.HyperlinkPopupOptions().setPage(event.getPage());
			});
		}

		root.appendChild(anchor);
		anchor.insertFirst(icon);
		anchor.appendChild(name);
	}

	public Element getElement()
	{
		return root;
	}

	private String getHyperlinkURL()
	{
		if (hyperlinkPopupOptions == null)
			return null;

		/* Build the URL */
		ServletConstants.Builder builder = new ServletConstants.Builder().setUrl(GWT.getHostPageBaseURL());

		for (Parameter param : hyperlinkPopupOptions.getRelevantParameters())
		{
			try
			{
				String value;

				if (param.isDatasetParam())
				{
					List<Dataset> datasets = DatasetListParameterStore.Inst.get().get(Parameter.getDatasetParam(param));
					List<Long> ids = DatabaseObject.getIds(datasets);
					value = CollectionUtils.join(ids, ",");
				}
				else
				{
					value = TypedParameterStore.getUntyped(param);
				}

				if (StringUtils.isEmpty(value))
					continue;

				builder.setParam(param.name(), value);
			}
			catch (UnsupportedDataTypeException e)
			{
				// Do nothing here
			}
		}
		/* Append the current page */
		builder.setFragment(ServletConstants.FragmentPosition.END, hyperlinkPopupOptions.getPage().name());

		return builder.toString();
	}
}
