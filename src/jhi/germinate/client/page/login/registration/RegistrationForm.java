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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.client.ui.form.error.*;
import org.gwtbootstrap3.client.ui.form.validator.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.handler.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class RegistrationForm extends Composite implements Validator<String>
{
	interface RegistrationFormUiBinder extends UiBinder<FlowPanel, RegistrationForm>
	{
	}

	private static RegistrationFormUiBinder ourUiBinder = GWT.create(RegistrationFormUiBinder.class);

	@UiField
	HTML               privacyPolicy;
	@UiField
	ToggleSwitch       accountToggle;
	@UiField
	TextBox            username;
	@UiField
	Input              password;
	@UiField
	FlowPanel          newAccountPanel;
	@UiField
	Input              passwordConfirm;
	@UiField
	Tooltip            tooltip;
	@UiField
	Progress           progress;
	@UiField
	ProgressBar        progressBar;
	@UiField
	TextBox            name;
	@UiField
	TextBox            email;
	@UiField
	FormGroup          institutionGroup;
	@UiField
	InstitutionListBox institution;

	private int passwordStrength;

	private ValueChangeHandler<String> passwordStrengthHandler = event -> {
		if (StringUtils.isEmpty(password.getValue()))
			passwordStrength = 0;
		else
			passwordStrength = getPasswordStrength(password.getValue());

		switch (passwordStrength)
		{
			case 0:
				progressBar.setPercent(1);
				progressBar.setType(ProgressBarType.DANGER);
				tooltip.setTitle(Text.LANG.passwordStrengthZero());
				break;
			case 1:
				progressBar.setPercent(25);
				progressBar.setType(ProgressBarType.DANGER);
				tooltip.setTitle(Text.LANG.passwordStrengthOne());
				break;
			case 2:
				progressBar.setPercent(50);
				progressBar.setType(ProgressBarType.DANGER);
				tooltip.setTitle(Text.LANG.passwordStrengthTwo());
				break;
			case 3:
				progressBar.setPercent(75);
				progressBar.setType(ProgressBarType.WARNING);
				tooltip.setTitle(Text.LANG.passwordStrengthThree());
				break;
			case 4:
				progressBar.setPercent(100);
				progressBar.setType(ProgressBarType.SUCCESS);
				tooltip.setTitle(Text.LANG.passwordStrengthFour());
				break;
		}
	};

	public RegistrationForm()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		privacyPolicy.setHTML(Text.LANG.privacyPolicyInformation());

		// Add the validators that make sure everything is set
		username.addValidator(this);
		password.addValidator(this);
		passwordConfirm.addValidator(this);
		name.addValidator(this);
		email.addValidator(this);

		updateInstitutions(null);

		password.addValueChangeHandler(passwordStrengthHandler);
		password.addKeyUpHandler(event -> passwordStrengthHandler.onValueChange(null));
	}

	public void addValueChangeHandlerString(ValueChangeHandler<String> handler)
	{
		username.addValueChangeHandler(handler);
		username.addKeyUpHandler(event -> handler.onValueChange(null));
		password.addValueChangeHandler(handler);
		password.addKeyUpHandler(event -> handler.onValueChange(null));
		passwordConfirm.addValueChangeHandler(handler);
		passwordConfirm.addKeyUpHandler(event -> handler.onValueChange(null));
		name.addValueChangeHandler(handler);
		name.addKeyUpHandler(event -> handler.onValueChange(null));
		email.addValueChangeHandler(handler);
		email.addKeyUpHandler(event -> handler.onValueChange(null));
	}

	public void addValueChangeHandlerBoolean(ValueChangeHandler<Boolean> handler)
	{
		accountToggle.addValueChangeHandler(handler);
	}

	public void addValueChangeHandlerInstitution(ValueChangeHandler<List<Institution>> handler)
	{
		institution.addValueChangeHandler(handler);
	}

	private void updateInstitutions(Institution toSelect)
	{
		/* Get the list of available institutions */
		UserService.Inst.get().getInstitutions(new AsyncCallback<List<Institution>>()
		{
			@Override
			public void onSuccess(List<Institution> result)
			{
				/* Add an empty institution */
				Institution empty = new Institution(-1L)
						.setName("");
				result.add(0, empty);

				if (toSelect != null)
				{
					for (Institution i : result)
					{
						if (StringUtils.areEqual(i.getName(), toSelect.getName())
								&& StringUtils.areEqual(i.getAcronym(), toSelect.getAcronym())
								&& StringUtils.areEqual(i.getAddress(), toSelect.getAddress()))
						{
							institution.setValue(i, false);
							break;
						}
					}
				}
				else
				{
					/* Set the values */
					institution.setValue(empty, false);
				}
				institution.setAcceptableValues(result);
			}

			@Override
			public void onFailure(Throwable caught)
			{
			}
		});
	}

	@UiHandler("accountToggle")
	void onAccountToggleValueChange(ValueChangeEvent<Boolean> e)
	{
		newAccountPanel.setVisible(!e.getValue());
		progress.setVisible(!e.getValue());

		passwordStrengthHandler.onValueChange(null);
	}

	@UiHandler("addInstitutionButton")
	void onAddInstitutionButtonClicked(ClickEvent e)
	{
		ModalBody body = new ModalBody();
		RegistrationNewInstitution newInstitution = new RegistrationNewInstitution();
		body.add(newInstitution);

		AlertDialog dialog = new AlertDialog(Text.LANG.registrationAddNewInstitution())
				.setContent(body)
				.setAutoCloseOnPositive(false);
		dialog.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalCancel(), Style.MDI_CANCEL, ev -> dialog.close()));
		dialog.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDone(), Style.MDI_CHECK, ButtonType.PRIMARY, ev ->
		{
			Institution i = newInstitution.getInstitution();
			if (i != null)
			{
				UserService.Inst.get().addInstitution(i, new AsyncCallback<Void>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						ExceptionHandler.handleException(caught);
					}

					@Override
					public void onSuccess(Void result)
					{
						updateInstitutions(i);
						dialog.close();
					}
				});
			}
		}))
			  .open();
	}

	public boolean isValid()
	{
		institutionGroup.removeStyleName(ValidationState.ERROR.getCssName());

		if (!username.validate() || !password.validate())
			return false;

		if (!accountToggle.getValue())
		{
			if (!passwordConfirm.validate() || !name.validate() || !email.validate())
				return false;
			else if (institution.getSelection() == null || institution.getSelection().getId() < 1)
			{
				institutionGroup.addStyleName(ValidationState.ERROR.getCssName());
				return false;
			}
			else if (!StringUtils.areEqual(password.getText(), passwordConfirm.getText()))
			{
				password.addStyleName(ValidationState.ERROR.getCssName());
				passwordConfirm.addStyleName(ValidationState.ERROR.getCssName());
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationPasswordsDontMatch());
				return false;
			}
			else if(passwordStrength < 2)
			{
				password.addStyleName(ValidationState.ERROR.getCssName());
				passwordConfirm.addStyleName(ValidationState.ERROR.getCssName());
				Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationPasswordsWeakPassword());
				return false;
			}
		}

		return true;
	}

	public void register(Callback<Void, Throwable> callback)
	{
		/* Create the user DTO */
		UnapprovedUser user = new UnapprovedUser();
		user.toRegister = !accountToggle.getValue();
		user.userUsername = username.getText();
		user.userPassword = password.getText();
		user.userFullName = name.getText();
		user.userEmailAddress = email.getText();

		/* Add either the institution ID or the details */
		user.institutionId = institution.getSelection().getId();

		final LoadingIndicator indicator = new LoadingIndicator(Text.LANG.notificationLongRunning());
		indicator.show();

		/* Try to register the new user */
		UserService.Inst.get().register(Cookie.getRequestProperties(), user, new AsyncCallback<Void>()
		{
			@Override
			public void onSuccess(Void result)
			{
				indicator.hide();
				Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationRegistrationSuccess());
				History.newItem(Page.HOME.name());

				callback.onSuccess(null);
			}

			@Override
			public void onFailure(Throwable caught)
			{
				indicator.hide();
				if (caught instanceof LoginRegistrationException)
				{
					switch (((LoginRegistrationException) caught).getReason())
					{
						case GATEKEEPER_EMAIL_FAILED:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationEmailFailed());
							break;
						case GATEKEEPER_UNAVAILABLE:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationGatekeeperUnavailable());
							break;
						case REGISTRATION_UNAVAILABLE:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationUnavailable());
							break;
						case USERNAME_ALREADY_EXISTS:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationUsernameExists());
							username.setFocus(true);
							username.selectAll();
							username.showErrors(Collections.singletonList(new BasicEditorError(username, username.getText(), Text.LANG.notificationRegistrationUsernameExists())));
							break;
						case USER_ALREADY_HAS_ACCESS:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationAlreadyHasAccess());
							break;
						case USER_ALREADY_REQUESTED_ACCESS:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationAlreadyRequestedAccess());
							break;
						case USERNAME_PASSWORD_WRONG:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationLoginInvalidUsernameOrPassword());
							password.setFocus(true);
							password.selectAll();
							username.showErrors(Collections.singletonList(new BasicEditorError(username, username.getText(), Text.LANG.notificationLoginInvalidUsernameOrPassword())));
							password.showErrors(Collections.singletonList(new BasicEditorError(password, password.getText(), Text.LANG.notificationLoginInvalidUsernameOrPassword())));
							break;
					}
				}
				else if (caught instanceof InvalidArgumentException)
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationRegistrationInvalidData());
				}
				else if (caught instanceof DatabaseException)
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationDatabaseError(DatabaseException.class.getName()));
				}
				else if (caught instanceof SystemInReadOnlyModeException)
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationSystemReadOnly());
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, caught.getLocalizedMessage());
				}

				callback.onFailure(caught);
			}
		});
	}

	private native int getPasswordStrength(String password)/*-{
		return $wnd.zxcvbn(password).score;
	}-*/;

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