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

package jhi.germinate.client.page.dataset;

import com.google.gwt.i18n.client.*;

import java.util.*;
import java.util.stream.*;

import jhi.germinate.client.*;
import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class LicenseWizard extends ModalWizard
{
	private List<License>         licenses = new ArrayList<>();
	private List<ModalWizardPage> pages    = new ArrayList<>();
	private NavigationStatus      status   = new NavigationStatus(false, true, null);

	public LicenseWizard(Set<License> l)
	{
		setTitle(Text.LANG.licenseWizardTitle());
		ModalWizardPage page;

		if (!ModuleCore.getUseAuthentication() && GerminateSettingsHolder.get().downloadTrackingEnabled.getValue())
		{
			page = new UserIdentificationWizardPage(decision -> updateControls());
			pages.add(page);
			add(page);
		}

		for (License license : l)
		{
			LicenseData data = license.getLicenseData(LocaleInfo.getCurrentLocale().getLocaleName());

			if (data != null)
			{
				page = new LicenseWizardPage(license, data, decision -> updateControls());
				licenses.add(license);
				pages.add(page);
				add(page);
			}
		}
	}

	@Override
	protected NavigationStatus getNavigationStatus()
	{
		int position = getCurrentPage();

		status.setCanGoForward(pages.get(position).getDecision() != ModalWizardPage.Decision.UNKNOWN);

		return status;
	}

	@Override
	protected boolean onFinished()
	{
		return true;
	}

	public List<License> getAcceptedLicenses()
	{
		return licenses.stream()
					   .filter(l -> l.getLicenseLog() != null)
					   .collect(Collectors.toList());
	}

	public List<LicenseLog> getLicenseLogs()
	{
		return licenses.stream()
					   .filter(l -> l.getLicenseLog() != null)
					   .map(License::getLicenseLog)
					   .collect(Collectors.toList());
	}
}
