/*
 *  Copyright 2019 Information and Computational Sciences,
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

/**
 * {@link GoogleAnalytics} provides functionality to track user events and page views to GoogleAnalytics.
 *
 * @author Sebastian Raubach
 */
public final class GoogleAnalytics
{
	/**
	 * Sends information about pageviews to Google Analytics
	 *
	 * @param page The page that has been viewed
	 */
	public static void trackPageview(String page)
	{
		if (isGoogleAnalyticsLoaded() && GerminateSettingsHolder.get().googleAnalyticsEnabled.getValue())
		{
			trackPageviewNative(page);
		}
	}

	private static native void trackPageviewNative(String page) /*-{
		$wnd.ga('send', 'pageview', '/#' + page);
	}-*/;

	/**
	 * Sends information about tracked events to Google Analytics
	 *
	 * @param category The category of the event
	 * @param action   The action
	 */
	public static void trackEvent(Category category, String action)
	{
		if (isGoogleAnalyticsLoaded() && GerminateSettingsHolder.get().googleAnalyticsEnabled.getValue())
		{
			trackEventNative(category.category, action);
		}
	}

	private static native void trackEventNative(String category, String action) /*-{
		$wnd.ga('send', 'event', category, action);
	}-*/;

	/**
	 * Sends information about tracked events to Google Analytics
	 *
	 * @param category The category of the event
	 * @param action   The action
	 * @param label    The label
	 */
	public static void trackEvent(Category category, String action, String label)
	{
		if (isGoogleAnalyticsLoaded() && GerminateSettingsHolder.get().googleAnalyticsEnabled.getValue())
		{
			trackEventNative(category.category, action, label);
		}
	}

	private static native void trackEventNative(String category, String action, String label) /*-{
		$wnd.ga('send', 'event', category, action, label);
	}-*/;

	/**
	 * Sends information about tracked events to Google Analytics
	 *
	 * @param category The category of the event
	 * @param action   The action
	 * @param label    The label
	 * @param value    The value
	 */
	public static void trackEvent(Category category, String action, String label, int value)
	{
		if (isGoogleAnalyticsLoaded() && GerminateSettingsHolder.get().googleAnalyticsEnabled.getValue())
		{
			trackEventNative(category.category, action, label, value);
		}
	}

	private static native void trackEventNative(String category, String action, String label, int value) /*-{
		$wnd.ga('send', 'event', category, action, label, value);
	}-*/;

	/**
	 * Checks if the GoogleAnalytics API was loaded and initialized successfully by trying to access it in JavaScript
	 *
	 * @return <code>true</code> if the GoogleAnalytics API was loaded and initialized successfully
	 */
	public static native boolean isGoogleAnalyticsLoaded()/*-{
		return $wnd['ga'] !== undefined;
	}-*/;

	public enum Category
	{
		GET_HYPERLINK("get-hyperlink"),
		LOGIN("login"),
		LOGOUT("logout"),
		HELP("help"),
		UI("ui"),
		ADMIN("admin"),
		SHARE("share"),
		GROUPS("groups"),
		USER_GROUPS("user-groups"),
		DATASET("datasets"),
		LICENSE("license"),
		DATASET_PERMISSIONS("dataset-permissions"),
		ANNOTATIONS("annotations"),
		SEARCH("search"),
		DOWNLOAD("download"),
		MARKED_ITEMS("marked-items");

		private final String category;

		Category(String category)
		{
			this.category = category;
		}
	}
}