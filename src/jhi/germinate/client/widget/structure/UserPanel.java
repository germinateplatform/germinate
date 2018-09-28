/*
 *  Copyright 2018 Information and Computational Sciences,
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
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;

import jhi.germinate.client.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class UserPanel
{
	private static UserPanelUiBinder ourUiBinder   = GWT.create(UserPanelUiBinder.class);
	private static boolean           isInitialized = false;
	private static UserPanel         INSTANCE;
	@UiField
	UListElement     root;
	@UiField
	ParagraphElement username;
	//	@UiField
	//	ParagraphElement email;
	@UiField
	AnchorElement    gatekeeperLink;
	@UiField
	ButtonElement    logoutLink;
	@UiField
	LIElement        adminContainer;
	@UiField
	AnchorElement    adminLink;
	@UiField
	AnchorElement    userPermissionsLink;

	public UserPanel()
	{
		ourUiBinder.createAndBindUi(this);
	}

	public static void init()
	{
		UserAuth auth = ModuleCore.getUserAuth();

		if (!isInitialized)
		{
			isInitialized = true;
			INSTANCE = new UserPanel();

			if (!ModuleCore.getUseAuthentication())
			{
				GQuery selection = GQuery.$("#" + Id.STRUCTURE_ACCOUNT_SETTINGS_UL);

				if (selection != null && !selection.isEmpty())
					selection.remove();

				return;
			}

			if (auth != null)
			{
				GQuery.$("#" + Id.STRUCTURE_ACCOUNT_SETTINGS_UL)
					  .css("display", "inline-block")
					  .append(INSTANCE.root);

				INSTANCE.update(auth);
			}
		}
		else
		{
			INSTANCE.update(auth);
		}
	}

	private void update(UserAuth auth)
	{
		boolean show = false;
		if (auth.isAdmin() && GerminateSettingsHolder.isPageAvailable(Page.ADMIN_CONFIG))
		{
			show = true;
			adminLink.setHref("#" + Page.ADMIN_CONFIG.name());
		}
		else
		{
			adminLink.setHref("#");
		}
		if (auth.isAdmin() && GerminateSettingsHolder.isPageAvailable(Page.USER_PERMISSIONS))
		{
			show = true;
			userPermissionsLink.setHref("#" + Page.USER_PERMISSIONS.name());
		}
		else
		{
			userPermissionsLink.setHref("#");
		}

		if (show)
			GQuery.$(adminContainer).show();
		else
			GQuery.$(adminContainer).hide();

		username.setInnerText(auth.getUsername());
		gatekeeperLink.setHref(GerminateSettingsHolder.get().gatekeeperUrl.getValue());
		GQuery.$(logoutLink).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				/* Track information using Google Analytics */
				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.LOGOUT, "logout");

				/* Clear the parameter store and show the login page */
				GerminateEventBus.BUS.fireEvent(new LogoutEvent());

				return false;
			}
		});
	}

	interface UserPanelUiBinder extends UiBinder<UListElement, UserPanel>
	{
	}
}