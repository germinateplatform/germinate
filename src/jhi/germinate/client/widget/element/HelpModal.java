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

/**
 * @author Sebastian Raubach
 */
public class HelpModal extends Composite
{
	public static void show(Widget content)
	{
		new AlertDialog(Text.LANG.helpTitle(), new HelpModal(content))
				.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDone(), IconType.CHECK, null))
				.open();
	}

	interface HelpModalUiBinder extends UiBinder<HTMLPanel, HelpModal>
	{
	}

	private static HelpModalUiBinder ourUiBinder = GWT.create(HelpModalUiBinder.class);

	@UiField
	HTMLPanel content;

	public HelpModal(Widget widget)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		content.add(widget);
	}
}