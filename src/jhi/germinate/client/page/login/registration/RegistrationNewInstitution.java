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

package jhi.germinate.client.page.login.registration;

import com.google.gwt.core.client.*;
import com.google.gwt.editor.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.form.error.*;
import org.gwtbootstrap3.client.ui.form.validator.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class RegistrationNewInstitution extends Composite implements Validator<String>
{
	interface RegistrationNewInstitutionUiBinder extends UiBinder<FlowPanel, RegistrationNewInstitution>
	{
	}

	private static RegistrationNewInstitutionUiBinder ourUiBinder = GWT.create(RegistrationNewInstitutionUiBinder.class);

	@UiField
	TextBox  name;
	@UiField
	TextBox  acronym;
	@UiField
	TextArea address;

	public RegistrationNewInstitution()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		name.addValidator(this);
		acronym.addValidator(this);
		address.addValidator(this);
	}

	public String getName()
	{
		return name.getText();
	}

	public String getAcronym()
	{
		return acronym.getText();
	}

	public String getAddress()
	{
		return address.getText();
	}

	public Institution getInstitution()
	{
		if (!name.validate() || !acronym.validate() || !address.validate())
			return null;
		else
			return new Institution()
					.setName(getName())
					.setAcronym(getAcronym())
					.setAddress(getAddress());
	}

	@Override
	public int getPriority()
	{
		return 0;
	}

	@Override
	public List<EditorError> validate(Editor<String> editor, String value)
	{
		if (StringUtils.isEmpty(value))
			return Collections.singletonList(new BasicEditorError(editor, value, Text.LANG.notificationRegistrationFillFields()));
		else
			return null;
	}
}