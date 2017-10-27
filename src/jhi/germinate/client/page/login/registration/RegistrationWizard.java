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
import com.google.gwt.safehtml.shared.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class RegistrationWizard extends ModalWizard
{
	private RegistrationLicense license;
	private RegistrationForm    form;
	private NavigationStatus status = new NavigationStatus(false, true, null);

	public RegistrationWizard()
	{
		setTitle(Text.LANG.registrationTitle());

		SafeHtml disclaimerShort = Text.LANG.registrationDisclaimerShortHtml();

		// If there is no registration text
		if (disclaimerShort != null && !StringUtils.isEmpty(disclaimerShort.asString()))
		{
			license = new RegistrationLicense();
			license.addValueChangeHandler(event -> updateControls());
			add(license);
		}

		form = new RegistrationForm();
		form.addValueChangeHandlerString(event -> updateControls());
		form.addValueChangeHandlerInstitution(event -> updateControls());
		form.addValueChangeHandlerBoolean(event -> updateControls());
		add(form);
	}

	@Override
	protected boolean onFinished()
	{
		form.register(new Callback<Void, Throwable>()
		{
			@Override
			public void onFailure(Throwable reason)
			{
			}

			@Override
			public void onSuccess(Void result)
			{
				RegistrationWizard.this.close();
			}
		});

		return false;
	}

	@Override
	protected NavigationStatus getNavigationStatus()
	{
		switch (getCurrentPage())
		{
			case 0:
				if (license != null)
				{
					status.setCanGoForward(license.hasAccepted());
					break;
				}
				// Don't break here, just continue to next case
			case 1:
			default:
				status.setCanGoForward(form.isValid());
				break;
		}

		return status;
	}
}
