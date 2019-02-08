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
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class HelpWidget
{
	private static boolean isInitialized = false;

	private static Widget helpContent = null;

	public static void init()
	{
		if (!isInitialized)
		{
			isInitialized = true;

			Anchor helpAnchor = Anchor.wrap(Document.get().getElementById(Id.STRUCTURE_HELP_A));
			helpAnchor.setVisible(true);
			helpAnchor.setTitle(Text.LANG.helpTitle());

			try
			{
				Anchor contactAnchor = Anchor.wrap(Document.get().getElementById(Id.STRUCTURE_CONTACT_A));
				contactAnchor.setTitle(Text.LANG.contact());
			}
			catch (Exception | Error e)
			{
			}

			JavaScript.click(helpAnchor, new ClickCallback()
			{
				@Override
				public void onSuccess(Event event)
				{
					if (!helpAnchor.getElement().getParentElement().hasClassName(Styles.DISABLED))
						show();
				}
			});

			GerminateEventBus.BUS.addHandler(MainContentChangeEvent.TYPE, event ->
			{
				Composite newContent = event.getComposite();

				if (newContent instanceof HasHelp)
					helpContent = ((HasHelp) newContent).getHelpContent();
				else
					helpContent = null;

				if (helpContent == null)
					helpAnchor.getElement().getParentElement().addClassName(Styles.DISABLED);
				else
					helpAnchor.getElement().getParentElement().removeClassName(Styles.DISABLED);
			});
		}
	}

	public static void show(Composite page)
	{
		if (page instanceof HasHelp)
			helpContent = ((HasHelp) page).getHelpContent();
		else
			helpContent = null;

		show();
	}

	public static void show()
	{
		if (helpContent != null)
		{
			HelpModal.show(helpContent);
		}
		else
		{
			GoogleAnalytics.trackEvent(GoogleAnalytics.Category.HELP, History.getToken());
			Notification.notify(Notification.Type.INFO, Text.LANG.notificationHelpNotAvailable());
		}
	}
}
