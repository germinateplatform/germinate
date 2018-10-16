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

package jhi.germinate.client.page.login;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.SubmitButton;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.login.registration.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.structure.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class LoginForm extends Composite
{
	interface LoginFormNewUiBinder extends UiBinder<FlowPanel, LoginForm>
	{
	}

	private static LoginFormNewUiBinder ourUiBinder = GWT.create(LoginFormNewUiBinder.class);

	@UiField
	Heading pageTitle;

	@UiField
	Container container;

	@UiField
	HTML homeText;

	@UiField
	FormPanel form;

	@UiField
	FlowPanel usernameDiv;

	@UiField
	FlowPanel passwordDiv;

	@UiField
	SubmitButton button;

	@UiField
	Anchor registerAnchor;

	@UiField
	Anchor forgotPasswordAnchor;

	@UiField
	LIElement languageSelector;

	@UiField
	LIElement emailAnchorParent;

	@UiField
	AnchorElement emailAnchor;

	@UiField
	AnchorElement helpAnchor;

	private final Element      originalForm;
	private       InputElement usernameBox;
	private       InputElement passwordBox;

	public LoginForm(LoginPage page, final ClickHandler handler)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		pageTitle.setText(GerminateSettingsHolder.get().templateTitle.getValue());

		homeText.setHTML(Text.LANG.loginText());

		originalForm = Document.get().getElementById(Id.LOGIN_FORM);

		usernameBox = (InputElement) Document.get().getElementById(Id.LOGIN_USERNAME_INPUT);
		passwordBox = (InputElement) Document.get().getElementById(Id.LOGIN_PASSWORD_INPUT);

		usernameBox.setPropertyString("placeholder", Text.LANG.loginUsername());
		passwordBox.setPropertyString("placeholder", Text.LANG.loginPassword());

		usernameDiv.getElement().appendChild(usernameBox);
		passwordDiv.getElement().appendChild(passwordBox);

		button.addClickHandler(event ->
		{
			event.preventDefault();
			form.submit();
		});

		form.addSubmitHandler(event ->
		{
			event.cancel();
			handler.onClick(null);
		});

		EventListener enterKeyListener = event ->
		{
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
			{
				form.submit();
			}
		};

		DOM.sinkEvents(button.getElement(), Event.ONCLICK);
		DOM.setEventListener(button.getElement(), event ->
		{
			event.preventDefault();
			form.submit();
		});

		DOM.sinkEvents(usernameBox, Event.KEYEVENTS);
		DOM.setEventListener(usernameBox, enterKeyListener);

		DOM.sinkEvents(passwordBox, Event.KEYEVENTS);
		DOM.setEventListener(passwordBox, enterKeyListener);

		if (GerminateSettingsHolder.get().gatekeeperRegistrationEnabled.getValue())
		{
			registerAnchor.setVisible(true);
			GQuery.$(registerAnchor).click(new Function()
			{
				@Override
				public boolean f(Event e)
				{
					new RegistrationWizard().open();

					return false;
				}
			});
		}

		GQuery.$(forgotPasswordAnchor).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				HelpWidget.show(page);

				return false;
			}
		});

		String email = GerminateSettingsHolder.get().templateContactEmail.getValue();
		if (!StringUtils.isEmpty(email))
			emailAnchor.setHref("mailto:" + email);
		else
			emailAnchorParent.removeFromParent();

		GQuery.$(helpAnchor).click(new Function()
		{
			@Override
			public boolean f(Event e)
			{
				HelpWidget.show(page);
				return false;
			}
		});

		LanguageSelector.addTo(languageSelector);
	}

	public void forceFocus()
	{
		usernameBox.focus();
	}

	public void highlightUsername()
	{
		usernameDiv.addStyleName(ValidationState.ERROR.getCssName());
		usernameBox.focus();
		usernameBox.select();
	}

	public void highlightPassword()
	{
		passwordDiv.addStyleName(ValidationState.ERROR.getCssName());
		passwordBox.focus();
		passwordBox.select();
	}

	public String getUsername()
	{
		return usernameBox.getValue();
	}

	public String getPassword()
	{
		return passwordBox.getValue();
	}

	public void clear()
	{
		originalForm.removeAllChildren();

		usernameBox.setInnerText(null);
		passwordBox.setInnerText(null);

		usernameBox.removeFromParent();
		passwordBox.removeFromParent();

		usernameBox.setInnerText(null);
		passwordBox.setInnerText(null);

		originalForm.appendChild(usernameBox);
		originalForm.appendChild(passwordBox);

		/* Unfortunately, we have to use this approach, as the browser will always try to fill the text fields again */
		Scheduler.get().scheduleFixedPeriod(new Scheduler.RepeatingCommand()
		{
			int counter = 1;

			@Override
			public boolean execute()
			{
				((InputElement) Document.get().getElementById(Id.LOGIN_USERNAME_INPUT)).setValue(null);
				((InputElement) Document.get().getElementById(Id.LOGIN_PASSWORD_INPUT)).setValue(null);

				return counter++ < 10;
			}
		}, 100);
	}
}