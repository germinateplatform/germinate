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

package jhi.germinate.client.util.handler;

import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;

import jhi.germinate.client.*;
import jhi.germinate.client.management.*;
import jhi.germinate.client.page.login.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class LogoutHandler implements LogoutEvent.LogoutEventHandler
{
	@Override
	public void onLogout(LogoutEvent event)
	{
		UserService.Inst.get().logout(Cookie.getRequestProperties(), new AsyncCallback<Void>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				navigate();
			}

			@Override
			public void onSuccess(Void result)
			{
				navigate();
			}
		});
	}

	private void navigate()
	{
		ModuleCore.setLoggedIn(false);
		Cookie.removeAll();
		TypedParameterStore.clearAllStores();

		if (ModuleCore.getUseAuthentication())
		{
			//			History.newItem("");
			ContentHolder.getInstance().setContent(Page.HOME, null, new LoginPage());
		}
		else
		{
			History.newItem(Page.HOME.name());
		}
	}
}
