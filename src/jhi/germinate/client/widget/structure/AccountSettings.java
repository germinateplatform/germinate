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


import com.google.gwt.dom.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class AccountSettings
{
	private static UListElement list;

	public static void init()
	{
		UserAuth auth = ModuleCore.getUserAuth();

		if (list == null)
		{
			if (!ModuleCore.getUseAuthentication())
			{
				GQuery selection = GQuery.$("#" + Id.STRUCTURE_ACCOUNT_SETTINGS_UL);

				if (selection != null && !selection.isEmpty())
					selection.remove();

				return;
			}

			if (auth != null)
			{
				list = Document.get().createULElement();

				list.addClassName(Style.combine(Styles.DROPDOWN_MENU, Style.BOOTSTRAP_DROPDOWN_ALERT));

				RootPanel p = RootPanel.get(Id.STRUCTURE_ACCOUNT_SETTINGS_UL);
				p.setVisible(true);
				p.getElement().appendChild(list);
				p.removeFromParent();

				update(auth);
			}
		}
		else
		{
			update(auth);
		}
	}

	private static void update(UserAuth auth)
	{
		list.removeAllChildren();

		if (auth.isAdmin() && GerminateSettingsHolder.isPageAvailable(Page.ADMIN_CONFIG))
		{
			AccountSettingsItem adminItem = new AccountSettingsItem(Text.LANG.adminConfigMenuItem(), Style.MDI_SETTINGS, event -> History.newItem(Page.ADMIN_CONFIG.name()));
			list.appendChild(adminItem.getElement());
			LIElement divider = Document.get().createLIElement();
			divider.setClassName(Styles.DIVIDER);
			list.appendChild(divider);
		}

		list.appendChild(new AccountSettingsItem(Text.LANG.menuLogout(), Style.MDI_LOGOUT_VARIANT, event ->
		{
			/* Track information using Google Analytics */
			JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.LOGOUT, "logout");

			/* Clear the parameter store and show the login page */
			GerminateEventBus.BUS.fireEvent(new LogoutEvent());
		}).getElement());
	}
}
