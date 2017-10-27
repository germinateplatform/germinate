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
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.html.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class HeaderAlert extends Composite
{
	interface HeaderAlertUiBinder extends UiBinder<Alert, HeaderAlert>
	{
	}

	private static HeaderAlertUiBinder ourUiBinder = GWT.create(HeaderAlertUiBinder.class);

	@UiField
	Strong title;
	@UiField
	HTML   message;

	public HeaderAlert(String title, String message)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		if (!StringUtils.isEmpty(title))
			this.title.setText(title);
		else
			this.title.setVisible(false);

		if (!StringUtils.isEmpty(message))
			this.message.setText(message);
		else
			this.message.setVisible(false);
	}

	public HeaderAlert(String title, SafeHtml message)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		if (!StringUtils.isEmpty(title))
			this.title.setText(title);
		else
			this.title.setVisible(false);

		if (!StringUtils.isEmpty(message.asString()))
			this.message.setHTML(message);
		else
			this.message.setVisible(false);
	}
}