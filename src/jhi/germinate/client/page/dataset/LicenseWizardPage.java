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

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class LicenseWizardPage extends ModalWizardPage
{
	interface LicenseWizardPageUiBinder extends UiBinder<HTMLPanel, LicenseWizardPage>
	{
	}

	private static LicenseWizardPageUiBinder ourUiBinder = GWT.create(LicenseWizardPageUiBinder.class);

	@UiField
	HTML              content;
	@UiField
	FlowPanel         acceptPart;
	@UiField
	Button            accept;
	@UiField
	Button            decline;
	@UiField
	MdiAnchorListItem html;
	@UiField
	MdiAnchorListItem print;

	private Decision                decision = Decision.UNKNOWN;
	private String                  datasetId;
	private License                 license;
	private LicenseData             data;
	private OnDecisionChangeHandler handler;

	public LicenseWizardPage(License license, OnDecisionChangeHandler handler)
	{
		this.license = license;
		this.data = license.getLicenseData(LocaleInfo.getCurrentLocale().getLocaleName());
		this.datasetId = license.getExtra(Dataset.ID);
		this.handler = handler;

		initWidget(ourUiBinder.createAndBindUi(this));

		acceptPart.setVisible(handler != null);
		content.setHTML(data.getContent());

		content.getElement().setId(RandomUtils.generateRandomId());
	}

	public String getId()
	{
		return content.getElement().getId();
	}

	@UiHandler("accept")
	void onAcceptButtonClicked(ClickEvent e)
	{
		decision = Decision.ACCEPTED;

		accept.setType(ButtonType.SUCCESS);
		decline.setType(ButtonType.DEFAULT);

		license.setLicenseLog(new LicenseLog(-1L)
				.setLicense(license.getId())
				.setUser(ModuleCore.getUseAuthentication() ? ModuleCore.getUserAuth().getId() : null)
				.setAcceptedOn(System.currentTimeMillis()));

		if (handler != null)
			handler.onDecisionChanged(decision);
	}

	@UiHandler("decline")
	void onDeclineButtonClicked(ClickEvent e)
	{
		decision = Decision.DECLINED;

		accept.setType(ButtonType.DEFAULT);
		decline.setType(ButtonType.DANGER);

		license.setLicenseLog(null);

		if (handler != null)
			handler.onDecisionChanged(decision);
	}

	@UiHandler("print")
	void onPrintButtonClicked(ClickEvent e)
	{
		GoogleAnalytics.trackEvent(GoogleAnalytics.Category.LICENSE, "print", Long.toString(data.getLicense()));
		JavaScript.printString(data.getContent());
	}

	@UiHandler("html")
	void onHtmlButtonClicked(ClickEvent e)
	{
		String url = JavaScript.getOctetStreamBase64Data(data.getContent());
		String downloadFileName = datasetId + "-" + license.getName() + ".html";
		downloadFileName = downloadFileName.replace(' ', '-');
		GoogleAnalytics.trackEvent(GoogleAnalytics.Category.LICENSE, "saveHtml", Long.toString(data.getLicense()));
		JavaScript.invokeDownload(url, downloadFileName);
	}

	public Decision getDecision()
	{
		return decision;
	}
}