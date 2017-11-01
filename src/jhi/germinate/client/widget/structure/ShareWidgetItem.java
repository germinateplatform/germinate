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
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
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
	private AnchorElement anchor = Document.get().createAnchorElement();
	private SpanElement   icon   = Document.get().createSpanElement();
	private SpanElement   name   = Document.get().createSpanElement();

	private HasHyperlinkButton.HyperlinkPopupOptions hyperlinkPopupOptions;

	public ShareWidgetItem(ShareUtils.ShareType type)
	{
		icon.addClassName(Style.combine(type.getIcon(), Style.MDI, Style.FA_FIXED_WIDTH, Style.MDI_LG, Style.LAYOUT_V_ALIGN_MIDDLE));

		String display = type.getTitle();

		name.setInnerText(display);

		anchor.setTitle(display);
		anchor.setHref("#");
		GQuery.$(anchor).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				/* Track that the user clicked help */
				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.SHARE, "#" + History.getToken(), type.name());

				ShareUtils.openUrl(type, getHyperlinkURL());
				return false;
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
				String value = TypedParameterStore.getUntyped(param);

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
