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
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.*;
import com.google.gwt.maps.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.enums.*;

/**
 * {@link JavaScript} is a utility class containing methods executing javascript code.
 *
 * @author Sebastian Raubach
 */
public class JavaScript
{
	/**
	 * Invokes a resize event on the given map
	 *
	 * @param map The map
	 */
	public static native void triggerResize(MapImpl map)/*-{
		$wnd.google.maps.event.trigger(map, 'resize');
	}-*/;

	public static native void smoothScrollTo(Element element)/*-{
		$wnd.$('html, body').animate({
			scrollTop: $wnd.$(element).offset().top
		});
	}-*/;

	public static native void smoothScrollTo(int y)/*-{
		$wnd.$('html, body').animate({
			scrollTop: y
		});
	}-*/;

	public static native void scrollToBottom(Element element)/*-{
		element.scrollTop = element.scrollHeight;
	}-*/;

	public static native void toggle(String selector)/*-{
		$wnd.$(selector).toggle();
	}-*/;

	public static native String getOctetStreamBase64Data(String html) /*-{
		return "data:application/octet-stream;base64," + $wnd.btoa(unescape(encodeURIComponent(html)));
	}-*/;

	public static native String getJsonData(String json) /*-{
		return "data:text/json;charset=utf-8," + $wnd.encodeURIComponent(json);
	}-*/;

	public static native void setVisible(String selector, boolean value)/*-{
		if (value)
			$wnd.$(selector).show();
		else
			$wnd.$(selector).hide();
	}-*/;

	public static native String getLinkColor()/*-{
		var a = $wnd.document.createElement("a");
		$wnd.document.body.appendChild(a);
		var fillColor = $(a).css("color");
		$wnd.document.body.removeChild(a);
		return fillColor;
	}-*/;

	public static void printObject(String elementId)
	{
		printString(GQuery.$("#" + elementId).html());
	}

	public static void printHtml(HTML html)
	{
		printString(html.getHTML());
	}

	public static void printString(String string)
	{
		DivElement element = Document.get().createDivElement();
		element.setInnerHTML(string);
		Document.get().getBody().appendChild(element);
		element.addClassName(Style.PRINT_SECTION);

		Window.print();

		Scheduler.get().scheduleDeferred(element::removeFromParent);
	}

	/**
	 * {@link D3} is a utility class containing constants and utility methods for D3
	 *
	 * @author Sebastian Raubach
	 */
	public static final class D3
	{
		public static final class Margin extends JavaScriptObject
		{
			protected Margin()
			{
			}

			public final native void setLeft(int value)/*-{
				this.left = value;
			}-*/;

			public final native void setRight(int value)/*-{
				this.right = value;
			}-*/;

			public final native void setTop(int value)/*-{
				this.top = value;
			}-*/;

			public final native void setBottom(int value)/*-{
				this.bottom = value;
			}-*/;
		}

		/** The default left margin of charts */
		public static final int MARGIN_LEFT   = 50;
		/** The default right margin of charts */
		public static final int MARGIN_RIGHT  = 50;
		/** The default top margin of charts */
		public static final int MARGIN_TOP    = 30;
		/** The default bottom margin of charts */
		public static final int MARGIN_BOTTOM = 30;
		/** The default height of charts */
		public static final int HEIGHT        = 500;

		public static JsArrayString getColorPalette()
		{
			return toJsArray(GerminateSettingsHolder.get().templateCategoricalColors.getValue());
		}

		public static Margin getMargin()
		{
			Margin margin = Margin.createObject().cast();
			margin.setTop(MARGIN_TOP);
			margin.setRight(MARGIN_RIGHT);
			margin.setBottom(MARGIN_BOTTOM);
			margin.setLeft(MARGIN_LEFT);

			return margin;
		}
	}

	public static void invokeDownload(String uri, String filename)
	{
		Anchor anchor;

		if (!StringUtils.isEmpty(filename))
		{
			anchor = new Anchor("Download", uri, "_blank");
			anchor.getElement().setAttribute("download", filename);
		}
		else
		{
			anchor = new Anchor("Download", uri);
		}

		anchor.setVisible(false);

		RootPanel.get().add(anchor);

		/* Click it */
		clickElement(anchor.getElement());

		/* And remove it */
		anchor.removeFromParent();
	}

	/**
	 * Invokes a click event on the given element
	 *
	 * @param elem The element
	 */
	public static native void clickElement(Element elem) /*-{
		elem.click();
	}-*/;

	public static void invokeDownload(String uri)
	{
		invokeDownload(uri, null);
	}

	public static void invokeGerminateDownload(String uri)
	{
		/* Create a new invisible dummy link on the page */
		String path = new ServletConstants.Builder()
				.setUrl(GWT.getModuleBaseURL())
				.setPath(ServletConstants.SERVLET_FILES)
				.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
				.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
				.setParam(ServletConstants.PARAM_FILE_PATH, uri)
				.build();

		JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), uri);

		invokeDownload(path);
	}

	/**
	 * Creates a JavaScript array based on the given Java {@link Collection}
	 *
	 * @param input The Java {@link Collection}
	 * @return The JavaScript Array
	 */
	public static JsArrayString toJsArray(Collection<String> input)
	{
		JsArrayString jsArrayString = JsArrayString.createArray().cast();
		for (String i : input)
			jsArrayString.push(i);
		//		input.forEach(jsArrayString::push);
		return jsArrayString;
	}

	/**
	 * Prints the given message to the browser console
	 *
	 * @param logMessage The log message to print
	 */
	public static native void consoleLog(String logMessage) /*-{
		console.log(logMessage);
	}-*/;

	/**
	 * Prints the given element to the browser console
	 *
	 * @param element The element
	 */
	public static native void consoleLog(Element element) /*-{
		console.log(element);
	}-*/;

	/**
	 * Highlights the given {@link Widget}'s {@link Element} using <code>$(element).delay(250).effect("highlight")</code>
	 *
	 * @param widget   The {@link Widget} to highlight
	 * @param scrollTo Scroll to the highlighted element?
	 */
	public static void highlight(Widget widget, boolean scrollTo)
	{
		if (widget != null)
		{
			if (widget.isVisible())
				highlight(widget.getElement(), scrollTo);
		}
	}

	/**
	 * Highlights the given {@link Element} using <code>$(element).delay(250).effect("highlight")</code>
	 *
	 * @param element  The element to highlight
	 * @param scrollTo Scroll to the highlighted element?
	 */
	public static void highlight(final Element element, final boolean scrollTo)
	{
		/* The highlight effect changes the display cs property, so make sure it's not hidden */
		if (element != null && GQuery.$(element).isVisible())
		{
			Scheduler.get().scheduleDeferred(() ->
			{
				if (scrollTo)
					element.scrollIntoView();

				highlightNative(element);
			});
		}
	}

	private static native void highlightNative(Element element)/*-{
		var highlightColor = @jhi.germinate.client.util.GerminateSettingsHolder::getCategoricalColor(*)(0);
		$wnd.$(element)
			.stop(false, true)
			.effect("highlight", {
				color: highlightColor
			}, 750);
	}-*/;

	/**
	 * Creates a JavaScript array based on the given Java array
	 *
	 * @param input The Java array
	 * @return The JavaScript Array
	 */
	public static JsArrayString toJsStringArray(String[] input)
	{
		JsArrayString jsArrayString = JsArrayString.createArray().cast();
		for (String s : input)
		{
			jsArrayString.push(s);
		}
		return jsArrayString;
	}

	public static JsArrayNumber toJsNumbersArray(double[] input)
	{
		JsArrayNumber jsArrayNumbers = JsArrayNumber.createArray().cast();
		for (double s : input)
		{
			jsArrayNumbers.push(s);
		}
		return jsArrayNumbers;
	}

	/**
	 * {@link GoogleAnalytics} provides functionality to track user events and page views to GoogleAnalytics.
	 *
	 * @author Sebastian Raubach
	 */
	public static final class GoogleAnalytics
	{
		public enum Category
		{
			GET_HYPERLINK("get-hyperlink"),
			LOGIN("login"),
			LOGOUT("logout"),
			HELP("help"),
			SHARE("share"),
			GROUPS("groups"),
			USER_GROUPS("user-groups"),
			ANNOTATIONS("annotations"),
			SEARCH("search"),
			DOWNLOAD("download");

			private final String category;

			Category(String category)
			{
				this.category = category;
			}
		}

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
	}

	/**
	 * Toggles the height of the element.
	 *
	 * @param element The element
	 */
	public static native void toggleHeight(Element element)/*-{
		$wnd.$(element)
			.stop(true, true)
			.animate({
				height: 'toggle'
			}, 200);
	}-*/;

	public static native Element elementFromPoint(int x, int y)/*-{
		return $wnd.document.elementFromPoint(x, y);
	}-*/;

	public static native boolean hasChildElement(Element element)/*-{
		return element.children.length > 0;
	}-*/;
}
