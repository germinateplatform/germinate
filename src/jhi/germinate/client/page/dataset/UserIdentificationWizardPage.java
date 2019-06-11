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

package jhi.germinate.client.page.dataset;

import com.google.gwt.core.client.*;
import com.google.gwt.editor.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.form.error.*;
import org.gwtbootstrap3.client.ui.form.validator.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class UserIdentificationWizardPage extends ModalWizardPage implements Validator<String>
{
	private static LicenseWizardPageUiBinder ourUiBinder = GWT.create(LicenseWizardPageUiBinder.class);

	@UiField
	HTML              heading;
	@UiField
	HTML              privacyPolicy;
	@UiField
	Form              form;
	@UiField
	TextBox           name;
	@UiField
	TextBox           email;
	@UiField
	TextBox           institution;
	@UiField
	DatasetUseListBox selection;
	@UiField
	TextArea          explanation;

	private OnDecisionChangeHandler    handler;
	private ValueChangeHandler<String> changeHandler = e -> handler.onDecisionChanged(getDecision());

	public UserIdentificationWizardPage(OnDecisionChangeHandler handler)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		this.handler = handler;

		heading.setHTML(Text.LANG.userTrackingHeading());

		privacyPolicy.setHTML(Text.LANG.privacyPolicyInformation());

		name.getElement().setPropertyBoolean("required", true);
		email.getElement().setPropertyBoolean("required", true);
		institution.getElement().setPropertyBoolean("required", true);

		name.getElement().setAttribute("autocomplete", "name");
		email.getElement().setAttribute("autocomplete", "email");
		institution.getElement().setAttribute("autocomplete", "street-address");
		email.getElement().setAttribute("type", "email");

		name.addValidator(this);
		email.addValidator(this);
		institution.addValidator(this);

		name.addValueChangeHandler(changeHandler);
		name.addKeyUpHandler(event -> changeHandler.onValueChange(null));
		email.addValueChangeHandler(changeHandler);
		email.addKeyUpHandler(event -> changeHandler.onValueChange(null));
		institution.addValueChangeHandler(changeHandler);
		institution.addKeyUpHandler(event -> changeHandler.onValueChange(null));
		explanation.addValueChangeHandler(changeHandler);
		explanation.addKeyUpHandler(event -> changeHandler.onValueChange(null));
		selection.addValueChangeHandler(event -> {
			String s = selection.getSelection();

			boolean isOther = Objects.equals(s, Text.LANG.userTrackingExplanationOptionOther());
			explanation.setVisible(isOther);
			explanation.setFocus(isOther);

			changeHandler.onValueChange(null);
		});

		// Try and restore previous input
		UnapprovedUser user = UnapprovedUserParameterStore.Inst.get().get(Parameter.user);
		if (user != null)
		{
			name.setText(user.userFullName);
			email.setText(user.userEmailAddress);
			institution.setText(user.institutionName);

			boolean selected = selection.selectItem(user.extra, false);

			if (!selected)
			{
				explanation.setText(user.extra);
				explanation.setVisible(true);
				selection.selectItem(Text.LANG.userTrackingExplanationOptionOther(), false);
			}
		}
	}

	public Decision getDecision()
	{
		Decision decision;
		if (name.validate() && email.validate() && institution.validate())
		{
			decision = Decision.ACCEPTED;
			// Remember the user input for next time
			UnapprovedUser user = new UnapprovedUser();
			user.userFullName = name.getText();
			user.userEmailAddress = email.getText();
			user.institutionName = institution.getText();

			String s = selection.getSelection();
			boolean isOther = Objects.equals(s, Text.LANG.userTrackingExplanationOptionOther());

			if (isOther)
				user.extra = explanation.getValue();
			else
				user.extra = s;

			UnapprovedUserParameterStore.Inst.get().put(Parameter.user, user);

			// "Submit" the form so that the browser can re-use the information with auto-fill next time.
			form.onFormSubmit();
		}
		else
		{
			decision = Decision.UNKNOWN;
		}

		return decision;
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

	interface LicenseWizardPageUiBinder extends UiBinder<HTMLPanel, UserIdentificationWizardPage>
	{
	}
}