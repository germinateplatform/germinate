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

package jhi.germinate.client.util;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.*;

import org.gwtbootstrap3.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;

/**
 * {@link ShareUtils} contains methods to interact with social networks or other means of sharing links/URLs
 *
 * @author Sebastian Raubach
 */
public class ShareUtils
{
	/**
	 * We need to use a placeholder and then use {@link String#replace(CharSequence, CharSequence)}, since {@link String#format(String, Object...)}
	 * isn't supported in GWT.
	 */
	private static final String PLACEHOLDER = "*={URL}=*";

	/**
	 * Supported set of share types
	 *
	 * @author Sebastian Raubach
	 */
	public enum ShareType
	{
		FACEBOOK("https://www.facebook.com/sharer/sharer.php?u=" + PLACEHOLDER, Style.MDI_FACEBOOK_BOX, Text.LANG.socialFacebook()),
		TWITTER("https://twitter.com/share?url=" + PLACEHOLDER + "&hashtags=germinate", Style.MDI_TWITTER_BOX, Text.LANG.socialTwitter()),
		SHARE_LINK(PLACEHOLDER, Style.MDI_LINK_VARIANT, Text.LANG.generalGetLinkToPage());

		private final String urlPrefix;
		private final String icon;
		private final String title;

		ShareType(String urlPrefix, String icon, String title)
		{
			this.urlPrefix = urlPrefix;
			this.icon = icon;
			this.title = title;
		}

		public boolean isAvailable()
		{
			if (GerminateSettingsHolder.get() != null)
			{
				switch (this)
				{
					case FACEBOOK:
						return GerminateSettingsHolder.get().socialShowFacebook.getValue();
					case TWITTER:
						return GerminateSettingsHolder.get().socialShowTwitter.getValue();
					case SHARE_LINK:
						return true;
					default:
						return false;
				}
			}
			else
			{
				return false;
			}
		}

		public String getTitle()
		{
			return title;
		}

		public String getIcon()
		{
			return icon;
		}
	}

	/**
	 * Opens the given url using the address of the {@link ShareType}
	 *
	 * @param type The given {@link ShareType}
	 * @param url  The url to open
	 */
	public static void openUrl(ShareType type, String url)
	{
		String finalUrl;
		if (url != null)
			finalUrl = type.urlPrefix.replace(PLACEHOLDER, getUrl(type, url));
		else if (!StringUtils.isEmpty(History.getToken()))
			finalUrl = type.urlPrefix.replace(PLACEHOLDER, getUrl(type, GWT.getHostPageBaseURL() + "#" + History.getToken()));
		else
			finalUrl = type.urlPrefix.replace(PLACEHOLDER, getUrl(type, GWT.getHostPageBaseURL()));

		if (type == ShareType.SHARE_LINK)
		{
			TextBox box = new TextBox();
			box.setText(finalUrl);

			new AlertDialog(Text.LANG.generalGetLinkToPageTitle(), box)
					.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDone(), Style.MDI_CHECK, null))
					.addShownHandler(e ->
							{
								box.setFocus(true);
								box.selectAll();
							}
					)
					.open();
		}
		else
		{
			Window.open(finalUrl, "_blank", "");
		}
	}

	private static String getUrl(ShareType type, String url)
	{
		if (type == ShareType.SHARE_LINK)
			return url;
		else
			return URL.encodePathSegment(url);
	}
}
