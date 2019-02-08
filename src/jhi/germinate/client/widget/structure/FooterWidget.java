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

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.login.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class FooterWidget extends Composite
{
	interface FooterWidgetUiBinder extends UiBinder<Container, FooterWidget>
	{
	}

	private static FooterWidgetUiBinder ourUiBinder = GWT.create(FooterWidgetUiBinder.class);

	@UiField
	Container      container;
	@UiField
	ParagraphPanel copyright;
	@UiField
	FlowPanel      linkPanel;
	@UiField
	Anchor         cookie;

	private static FooterWidget INSTANCE;

	private static boolean isInitialized = false;

	public FooterWidget()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		copyright.setText(Text.LANG.copyright(DateUtils.getCurrentYear()));

		if (!GerminateSettingsHolder.isPageAvailable(Page.COOKIE))
			cookie.removeFromParent();
	}

	@UiHandler("cookie")
	void onCookieClicked(ClickEvent e)
	{
		e.preventDefault();
		CookieModal.show();
	}

	public static void init()
	{
		if (!isInitialized)
		{
			isInitialized = true;
			INSTANCE = new FooterWidget();

			RootPanel p = RootPanel.get(Id.STRUCTURE_FOOTER);
			p.add(INSTANCE);
			//			p.removeFromParent();

			GerminateEventBus.BUS.addHandler(MainContentChangeEvent.TYPE, event ->
			{
				if (INSTANCE != null)
				{
					Composite composite = event.getComposite();
					if (composite instanceof LoginPage)
						INSTANCE.container.setFluid(false);
					else
						INSTANCE.container.setFluid(true);
				}
			});
			GerminateEventBus.BUS.addHandler(LogoutEvent.TYPE, event ->
			{
				if (INSTANCE != null)
					INSTANCE.container.setFluid(false);
			});
		}
	}
}