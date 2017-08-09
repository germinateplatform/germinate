/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class RegistrationLicense extends Composite
{
	interface RegistrationLicenseUiBinder extends UiBinder<FlowPanel, RegistrationLicense>
	{
	}

	private static RegistrationLicenseUiBinder ourUiBinder = GWT.create(RegistrationLicenseUiBinder.class);

	@UiField
	HTML         license;
	@UiField
	Button       fullLicenseButton;
	@UiField
	ToggleSwitch acceptToggle;

	private final String disclaimerShort;
	private final String disclaimerLong;

	public RegistrationLicense()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		disclaimerShort = RegistrationPageResources.INSTANCE.getShortAgreement().getText();
		disclaimerLong = RegistrationPageResources.INSTANCE.getLongAgreement().getText();

		license.setHTML(disclaimerShort);

		if (!StringUtils.isEmpty(disclaimerLong))
		{
			fullLicenseButton.setVisible(true);
		}
	}

	@UiHandler("fullLicenseButton")
	void onFullLicenseButtonClicked(ClickEvent e)
	{
		if (fullLicenseButton.isVisible())
		{
			ModalBody content = new ModalBody();
			HTML html = new HTML(disclaimerLong);
			content.add(html);

			new AlertDialog("")
					.setContent(content)
					.open();
		}
	}

	public boolean hasAccepted()
	{
		return acceptToggle.getValue();
	}

	public void addValueChangeHandler(ValueChangeHandler<Boolean> handler)
	{
		acceptToggle.addValueChangeHandler(handler);
	}

	public interface RegistrationPageResources extends ClientBundle
	{
		RegistrationPageResources INSTANCE = GWT.create(RegistrationPageResources.class);

		@Source("registration-agreement-long.html")
		TextResource getLongAgreement();

		@Source("registration-agreement-short.html")
		TextResource getShortAgreement();
	}
}