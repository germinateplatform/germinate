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

package jhi.germinate.client.management;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.login.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.structure.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * The {@link ContentHolder} contains all the methods necessary to manipulate the page content.
 *
 * @author Sebastian Raubach
 */
public class ContentHolder
{
	private static Panel     contentPanel;
	private static Panel     loginPanel;
	private static Composite currentContent;

	/**
	 * {@link Instance} is loaded on the first execution of {@link ContentHolder#getInstance()} or the first access to {@link Instance#INSTANCE}, not
	 * before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
	 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
	 *
	 * @author Sebastian Raubach
	 */
	private static final class Instance
	{
		private static final ContentHolder INSTANCE = new ContentHolder();
	}

	/**
	 * Returns the instance of {@link ContentHolder}
	 *
	 * @return The instance of {@link ContentHolder}
	 */
	public static ContentHolder getInstance()
	{
		return Instance.INSTANCE;
	}

	private ContentHolder()
	{
		contentPanel = RootPanel.get(Id.STRUCTURE_MAIN_CONTENT);
		loginPanel = RootPanel.get(Id.STRUCTURE_LOGIN);
	}

	/**
	 * Initializes the menu, the language box, the help, the search and the entries
	 */
	public void initContent()
	{
		/* Initialize various page contents */
		MainMenu.init();
		LanguageSelector.init();
		ShoppingCart.init();
		ShareWidget.init();
		AccountSettings.init();
		SearchPanel.init();
		ParallaxBanner.init();
		DebugInfoPanel.init();
	}

	private native void closeDropdown()/*-{
		$wnd.$('.dropdown.open .dropdown-toggle').dropdown('toggle');
	}-*/;

	/**
	 * Appends the new content to the dynamic content div
	 *
	 * @param page       The new {@link Page}
	 * @param parentPage Id of the menu item to highlight
	 * @param newContent The new content to append
	 */
	public void setContent(Page page, Page parentPage, final Composite newContent)
	{
		if (currentContent instanceof LoginPage)
			((LoginPage) currentContent).onUnload();

		currentContent = newContent;

		AbstractChart.removeD3();
		DebugInfoPanel.clear();
//		removeGoogleMapsLeftovers();

		closeDropdown();

		/* Make sure username and password field are cleared all the time */
		((InputElement) Document.get().getElementById(Id.LOGIN_USERNAME_INPUT)).setValue(null);
		((InputElement) Document.get().getElementById(Id.LOGIN_PASSWORD_INPUT)).setValue(null);

		if (newContent instanceof LoginPage)
		{
			Cookie.removeAll();

			contentPanel.clear();
			loginPanel.clear();
			loginPanel.add(newContent);

			GQuery.$("#" + Id.STRUCTURE_PAGE).hide();
			loginPanel.setVisible(true);
		}
		else
		{
			if (!GerminateSettingsHolder.isPageAvailable(page))
			{
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationPageUnavailable());
				History.newItem(Page.HOME.name());
				return;
			}
			/* Check if it's a public page */
			else if (page.isPublic())
			{
				navigateToPage(page, parentPage, newContent);
			}
			/* Check if the cookie is still valid */
			else if (ModuleCore.getUseAuthentication() && !Cookie.isStillAlive())
			{
				/* If not, redirect to the login page */
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInvalidSession());
				GerminateEventBus.BUS.fireEvent(new LogoutEvent());
				return;
			}
			else
			{
				Cookie.extend();
				navigateToPage(page, parentPage, newContent);
			}

			GQuery.$("#" + Id.STRUCTURE_PAGE).show();
			loginPanel.setVisible(false);
		}

		Window.scrollTo(0, 0);

		/* Set the parallax banner */
		if (newContent instanceof ParallaxBannerPage)
			ParallaxBanner.updateStyle(((ParallaxBannerPage) newContent).getParallaxStyle());
		else
			ParallaxBanner.updateStyle(null);

		Scheduler.get().scheduleDeferred(this::jsniOnPageChange);

		GerminateEventBus.BUS.fireEvent(new MainContentChangeEvent(page, newContent));
	}

	private native void jsniOnPageChange() /*-{
		if ($wnd.jsniOnPageChange)
			$wnd.jsniOnPageChange();
	}-*/;

	private void navigateToPage(Page page, Page parentPage, Composite newContent)
	{
		MainMenu.removeActiveStateMenuItems();
		boolean highlighted = MainMenu.highlightMenuItem(page);

		/* If the page itself isn't in the menu (subpage or continued page), highlight the parent (which is in the menu) */
		if (!highlighted)
			MainMenu.highlightMenuItem(parentPage);

		if (contentPanel != null)
		{
			if (newContent instanceof HasLibraries)
			{
				Library[] libraries = ((HasLibraries) newContent).getLibraries();
				Library.Queue.load(new DefaultAsyncCallback<Void>()
				{
					@Override
					protected void onSuccessImpl(Void result)
					{
						/* Remove the old content */
						contentPanel.clear();
						/* Add the new content */
						contentPanel.add(newContent);
					}
				}, libraries);
			}
			else
			{
				/* Remove the old content */
				contentPanel.clear();
				/* Add the new content */
				contentPanel.add(newContent);
			}
		}

		Window.scrollTo(0, 0);
	}

	/**
	 * Returns the current width of the content div <p/> To get continuously notified about resize events, register with the {@link ResizeRegister}.
	 *
	 * @return The current width of the content div
	 */
	public static int getContentWidth()
	{
		return contentPanel.getOffsetWidth();
	}
}
