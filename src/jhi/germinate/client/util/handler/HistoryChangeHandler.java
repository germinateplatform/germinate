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

import com.google.gwt.event.logical.shared.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class HistoryChangeHandler implements ValueChangeHandler<String>
{
	@Override
	public void onValueChange(ValueChangeEvent<String> event)
	{
		String historyToken = event.getValue();

		if (StringUtils.isEmpty(historyToken))
			historyToken = Page.HOME.name();

		try
		{
			/* Redirect to the requested page */
			Page page = Page.parse(historyToken);

			if (page.isPublic())
			{
				GerminateEventBus.BUS.fireEvent(new PageNavigationEvent(page));
			}
			/* Check if the user is logged in */
			else if (ModuleCore.getUseAuthentication() && !ModuleCore.isLoggedIn())
			{
				/* If not, show the login dialog */
//				ContentHolder.getInstance().setContent(Page.LOGIN, null, new LoginPage());
				Notification.notify(Notification.Type.INFO, Text.LANG.notificationLoginPrompt());
			}
			else
			{
//				GwtTour.endTour(true);
				/* Redirect to the requested page */
				GerminateEventBus.BUS.fireEvent(new PageNavigationEvent(page));
			}
		}
		catch (InvalidPageException e)
		{
			GerminateEventBus.BUS.fireEvent(new ExceptionEvent(e));
		}
	}
}
