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

package jhi.germinate.client.util.handler;

import com.google.gwt.user.client.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.management.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.structure.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class LoginHandler implements LoginEvent.LoginEventHandler
{
	@Override
	public void onLogin(LoginEvent event)
	{
		ModuleCore.setLoggedIn(true);
		ModuleCore.setUseAuthentication(true);
		ModuleCore.setUserAuth(event.getUserAuthentication());
		HelpWidget.init();

		/* Set up the cookie */
		Cookie.setUp(event.getUserAuthentication());

		ContentHolder.getInstance().initContent();

		if (!event.isAutomaticLogin())
		{
			/* Show notification */
			Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationLoginSuccessful());
		}

		/* Track information using Google Analytics */
		JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.LOGIN, "login", event.getUserAuthentication().getUsername());

		/* Fire a page update */
		String historyToken = History.getToken();

		boolean hasReadParameters = UrlParameterReader.readUrlParameters();

		/*
		 * If there are URL parameters and there is a valid page to
		 * navigate to, then do so
		 * Also do this, if this is a page refresh event
		 */
		if ((hasReadParameters && !StringUtils.isEmpty(historyToken) && !Page.LOGOUT.name().equals(historyToken)) || event.isAutomaticLogin())
		{
			History.fireCurrentHistoryState();
		}
		/* Else navigate to the home page */
		else
		{
			if (Page.HOME.name().equals(historyToken))
				History.fireCurrentHistoryState();
			else
				History.newItem(Page.HOME.name());
		}
	}
}
