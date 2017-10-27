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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;

/**
 * @author Sebastian Raubach
 */
public class CookieModal extends Composite
{
	public static void show()
	{
		new AlertDialog(Text.LANG.cookieTitle(), new CookieModal())
				.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDone(), IconType.CHECK, null))
				.open();
	}

	interface CookieWidgetUiBinder extends UiBinder<HTMLPanel, CookieModal>
	{
	}

	private static CookieWidgetUiBinder ourUiBinder = GWT.create(CookieWidgetUiBinder.class);

	@UiField
	HTML      internal;
	@UiField
	FlowPanel externalPanel;
	@UiField
	HTML      external;

	public CookieModal()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		internal.setHTML(Text.LANG.cookieTextInternal());

		if (GerminateSettingsHolder.get().googleAnalyticsEnabled.getValue())
		{
			externalPanel.setVisible(true);
			external.setHTML(Text.LANG.cookieTextGoogleAnalytics());
		}
	}
}