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

package jhi.germinate.client.page.admin;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.colorpicker.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.client.widget.table.basic.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class AdminConfigPage extends Composite implements HasHelp
{
	interface AdminConfigPageUiBinder extends UiBinder<HTMLPanel, AdminConfigPage>
	{
	}

	private static AdminConfigPageUiBinder ourUiBinder = GWT.create(AdminConfigPageUiBinder.class);

	@UiField
	HTMLPanel panel;

	@UiField
	FormLabel gatekeeperUrlLabel;
	@UiField
	TextBox   gatekeeperUrl;

	@UiField
	FormLabel    gatekeeperRegistrationLabel;
	@UiField
	ToggleSwitch gatekeeperRegistration;

	@UiField
	FormLabel    gatekeeperApprovalLabel;
	@UiField
	ToggleSwitch gatekeeperApproval;

	@UiField
	FormLabel    facebookLabel;
	@UiField
	ToggleSwitch facebook;

	@UiField
	FormLabel    twitterLabel;
	@UiField
	ToggleSwitch twitter;

	@UiField
	FormLabel    googleLabel;
	@UiField
	ToggleSwitch google;

	@UiField
	FormLabel    analyticsLabel;
	@UiField
	ToggleSwitch analytics;

	@UiField
	FormLabel analyticsIdLabel;
	@UiField
	TextBox   analyticsId;

	@UiField
	FormLabel    downloadTrackingLabel;
	@UiField
	ToggleSwitch downloadTracking;

	@UiField
	FormLabel templateTitleLabel;
	@UiField
	TextBox   templateTitle;

	@UiField
	FormLabel templateDatabaseNameLabel;
	@UiField
	TextBox   templateDatabaseName;

	@UiField
	FormLabel templateEmailLabel;
	@UiField
	TextBox   templateEmail;

	@UiField
	FormLabel templateMarkedAccessionUrlLabel;
	@UiField
	TextBox   templateMarkedAccessionUrl;

	@UiField
	FormLabel categoriesLabel;
	@UiField
	FlowPanel categories;

	@UiField
	FormLabel gradientLabel;
	@UiField
	FlowPanel gradients;

	@UiField
	SimplePanel templateGradientPanel;

	@UiField
	FormLabel    logoLinksLabel;
	@UiField
	ToggleSwitch logoLinks;

	@UiField
	FormLabel    parallaxLabel;
	@UiField
	ToggleSwitch parallax;

	@UiField
	FormLabel    idsLabel;
	@UiField
	ToggleSwitch ids;

	@UiField
	FormLabel    pdciLabel;
	@UiField
	ToggleSwitch pdci;

	@UiField
	FormLabel    loggingLabel;
	@UiField
	ToggleSwitch logging;

	@UiField
	FormLabel    debugLabel;
	@UiField
	ToggleSwitch debug;

	@UiField
	FormLabel    readOnlyLabel;
	@UiField
	ToggleSwitch readOnly;

	@UiField
	FormLabel    cookiesLabel;
	@UiField
	ToggleSwitch cookies;

	@UiField
	FormLabel     tempLabel;
	@UiField
	NumberTextBox temp;

	@UiField
	FormLabel     upLimitLabel;
	@UiField
	NumberTextBox upLimit;

	@UiField
	FormLabel      cookieLifespanLabel;
	@UiField
	IntegerTextBox cookieLifespan;

	@UiField
	FormLabel      imagesPPageLabel;
	@UiField
	IntegerTextBox imagesPPage;

	@UiField
	FormLabel externalFolderLabel;
	@UiField
	TextBox   externalFolder;

	@UiField
	FormLabel customMenuLabel;
	@UiField
	TextArea  customMenu;

	@UiField
	FormLabel pagesLabel;
	@UiField
	FlowPanel pagesPanel;

	@UiField
	PanelHeader   panelHeader;
	@UiField
	PanelCollapse target;

	private AdvancedTable<Page>      availablePages;
	private GerminateSettings        settings;
	private List<ColorPickerWrapper> templateCategoricalColors = new ArrayList<>();
	private List<ColorPickerWrapper> templateGradientColors    = new ArrayList<>();

	public AdminConfigPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		String id = "admin-" + RandomUtils.RANDOM.nextLong();

		target.setId(id);
		panelHeader.setDataTarget("#" + id);

		/* Get the settings from the server */
		CommonService.Inst.get().getAdminSettings(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<GerminateSettings>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				/* If the user isn't an admin, handle the exception */
				if (caught instanceof InsufficientPermissionsException)
				{
					panel.clear();
					panel.add(new Heading(HeadingSize.H3, Text.LANG.adminConfigInsufficientPermissions()));
				}
				/* Else let the parent deal with it */
				else
				{
					super.onFailureImpl(caught);
				}
			}

			@Override
			protected void onSuccessImpl(ServerResult<GerminateSettings> result)
			{
				settings = result.getServerResult();
				showConfig();
			}
		});
	}

	private void showConfig()
	{
		updateString(settings.gatekeeperUrl, gatekeeperUrl, gatekeeperUrlLabel);
		updateBoolean(settings.gatekeeperRegistrationEnabled, gatekeeperRegistration, gatekeeperRegistrationLabel);
		updateBoolean(settings.gatekeeperRegistrationNeedsApproval, gatekeeperApproval, gatekeeperApprovalLabel);

		updateBoolean(settings.socialShowFacebook, facebook, facebookLabel);
		updateBoolean(settings.socialShowTwitter, twitter, twitterLabel);
		updateBoolean(settings.socialShowGooglePlus, google, googleLabel);

		updateBoolean(settings.googleAnalyticsEnabled, analytics, analyticsLabel);
		updateString(settings.googleAnalyticsTrackingId, analyticsId, analyticsIdLabel);

		updateBoolean(settings.downloadTrackingEnabled, downloadTracking, downloadTrackingLabel);

		updateString(settings.templateTitle, templateTitle, templateTitleLabel);
		updateString(settings.templateDatabaseName, templateDatabaseName, templateDatabaseNameLabel);
		updateString(settings.templateContactEmail, templateEmail, templateEmailLabel);
		updateString(settings.templateMarkedAccessionUrl, templateMarkedAccessionUrl, templateMarkedAccessionUrlLabel);

		updateColorList(settings.templateCategoricalColors, categories, categoriesLabel, templateCategoricalColors, null, categoryDeleteCallback, null);
		updateColorList(settings.templateGradientColors, gradients, gradientLabel, templateGradientColors, gradientChangeCallback, gradientDeleteCallback, gradientAddCallback);
		updateTemplateGradient();

		updateBoolean(settings.templateLogoContainsLink, logoLinks, logoLinksLabel);
		updateBoolean(settings.templateShowParallaxBanner, parallax, parallaxLabel);

		updateBoolean(settings.hideIdColumn, ids, idsLabel);
		updateBoolean(settings.pdciEnabled, pdci, pdciLabel);
		updateBoolean(settings.serverLoggingEnabled, logging, loggingLabel);
		updateBoolean(settings.debug, debug, debugLabel);
		updateBoolean(settings.isReadOnlyMode, readOnly, readOnlyLabel);
		updateBoolean(settings.cookieNotifierEnabled, cookies, cookiesLabel);

		updateString(settings.keepTempFilesForHours, temp, tempLabel);
		updateString(settings.uploadSizeLimitMB, upLimit, upLimitLabel);

		updateString(settings.cookieLifespanMinutes, cookieLifespan, cookieLifespanLabel);
		updateString(settings.galleryImagesPerPage, imagesPPage, imagesPPageLabel);
		updateString(settings.externalDataFolder, externalFolder, externalFolderLabel);

		updateString(settings.templateCustomMenu, customMenu, customMenuLabel);
		updateTable(settings.availablePages, pagesPanel, pagesLabel);
	}

	private void updateTable(GerminateSettings.ClientProperty<Set<Page>> clientProperty, FlowPanel parent, FormLabel label)
	{
		label.setText(clientProperty.getServerProperty().getKey());

		/* Get all the valid pages from the "enum". The table should contain all of them but have only the currently available pages selected */
		Page[] pages = Page.values();

		/* Create the table */
		availablePages = new AdvancedTable<>(new ArrayList<>(Arrays.asList(pages)));

		/* Take care of selection */
		final SelectionModel<Page> selectionModel = new MultiSelectionModel<>();

		/* Checkbox cell */
		CheckboxCell cell = new CheckboxCell(true, false);

		/* First column: checkboxes */
		Column<Page, Boolean> checkboxColumn = new Column<Page, Boolean>(cell)
		{
			@Override
			public Boolean getValue(Page object)
			{
				return selectionModel.isSelected(object);
			}
		};

		/* Create a checkbox header to select all items */
		Header<Boolean> selectPageHeader = new Header<Boolean>(cell)
		{
			@Override
			public Boolean getValue()
			{
				for (Page item : availablePages.getTable().getVisibleItems())
				{
					if (!selectionModel.isSelected(item))
					{
						return false;
					}
				}
				return availablePages.getTable().getVisibleItems().size() > 0;
			}
		};

		/* Add an updater that changes the selection state of the table items */
		selectPageHeader.setUpdater(value ->
		{
			for (Page item : availablePages.getTable().getVisibleItems())
			{
				selectionModel.setSelected(item, value);
			}
		});

		/* Add the name column */
		availablePages.getTable().addColumn(checkboxColumn, selectPageHeader);
		availablePages.addStringColumn(new TextColumn<Page>()
		{
			@Override
			public String getValue(Page object)
			{
				return object.name();
			}
		}, Page.class.getSimpleName());

		availablePages.getTable().setColumnWidth(0, 1, com.google.gwt.dom.client.Style.Unit.PX);

		/* Mark the currently active pages as selected */
		for (Page page : clientProperty.getValue())
			selectionModel.setSelected(page, true);

		availablePages.getTable().setSelectionModel(selectionModel, DefaultSelectionEventManager.createCheckboxManager());

		parent.add(availablePages);
	}

	private void updateColorList(GerminateSettings.ClientProperty<List<String>> clientProperty, FlowPanel parent, FormLabel label, List<ColorPickerWrapper> wrappers, ColorPicker.ChangeCallback changeCallback, ColorPickerWrapper.DeleteCallback deleteCallback, AddCallback addCallback)
	{
		label.setText(clientProperty.getServerProperty().getKey());
		if (parent != null)
		{
			for (String color : clientProperty.getValue())
			{
				ColorPickerWrapper picker = new ColorPickerWrapper(deleteCallback);
				if (changeCallback != null)
					picker.setChangeCallback(changeCallback);
				picker.setColor(color);
				wrappers.add(picker);
				parent.add(picker);
			}

			Button add = new Button();
			add.setStyleName(Style.combine(Style.WIDGET_ICON_BUTTON, Style.MDI, Style.MDI_PLUS_BOX, Style.MDI_LG));

			add.addClickHandler(event ->
			{
				ColorPickerWrapper picker = new ColorPickerWrapper(deleteCallback);
				wrappers.add(picker);
				parent.insert(picker, parent.getWidgetCount() - 1);

				if (addCallback != null)
					addCallback.onAdd(picker);
			});
			parent.add(add);
		}
	}

	private void updateString(GerminateSettings.ClientProperty<?> clientProperty, HasText text, FormLabel label)
	{
		label.setText(clientProperty.getServerProperty().getKey());
		if (text != null && clientProperty.getValue() != null)
			text.setText(clientProperty.getValue().toString());
	}

	private void updateBoolean(GerminateSettings.ClientProperty<Boolean> clientProperty, ToggleSwitch text, FormLabel label)
	{
		label.setText(clientProperty.getServerProperty().getKey());
		if (text != null)
			text.setValue(clientProperty.getValue());
	}

	/**
	 * Recreates the gradient with the current color selection
	 */
	private void updateTemplateGradient()
	{
		Color[] colors = new Color[templateGradientColors.size()];
		for (int i = 0; i < templateGradientColors.size(); i++)
		{
			colors[i] = Color.fromHex(templateGradientColors.get(i).getColor());
		}

		templateGradientPanel.clear();
		templateGradientPanel.add(GradientUtils.createHorizontalGradientLegend(new Gradient(Gradient.createMultiGradient(colors, 100), 0, 200)));
	}

	@UiHandler("saveChanges")
	void onSaveChangesClicked(ClickEvent event)
	{
		/* When clicked, show an AlertDialog asking for confirmation */
		new AlertDialog(Text.LANG.generalConfirm(), Text.LANG.adminConfigAlertConfirmChanges())
				.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalYes(), Style.MDI_CHECK_CIRCLE, ButtonType.SUCCESS, e -> updateSettings()))
				.setNegativeButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalNo(), Style.MDI_CANCEL, ButtonType.DANGER, null))
				.open();
	}

	/**
	 * Sends the new settings to the server
	 */
	private void updateSettings()
	{
		try
		{
			settings.gatekeeperUrl.setValue(gatekeeperUrl.getValue());
			settings.gatekeeperRegistrationEnabled.setValue(gatekeeperRegistration.getValue());
			settings.gatekeeperRegistrationNeedsApproval.setValue(gatekeeperApproval.getValue());

			settings.socialShowFacebook.setValue(facebook.getValue());
			settings.socialShowTwitter.setValue(twitter.getValue());
			settings.socialShowGooglePlus.setValue(google.getValue());

			settings.googleAnalyticsEnabled.setValue(analytics.getValue());
			settings.googleAnalyticsTrackingId.setValue(analyticsId.getValue());

			settings.downloadTrackingEnabled.setValue(downloadTracking.getValue());

			settings.templateTitle.setValue(templateTitle.getValue());
			settings.templateDatabaseName.setValue(templateDatabaseName.getValue());
			settings.templateContactEmail.setValue(templateEmail.getValue());
			settings.templateMarkedAccessionUrl.setValue(templateMarkedAccessionUrl.getValue());
			settings.templateLogoContainsLink.setValue(logoLinks.getValue());
			settings.templateShowParallaxBanner.setValue(parallax.getValue());

			List<String> colors = new ArrayList<>();
			for (ColorPickerWrapper picker : templateCategoricalColors)
			{
				colors.add(picker.getColor());
			}
			settings.templateCategoricalColors.setValue(colors);

			colors = new ArrayList<>();
			for (ColorPickerWrapper picker : templateGradientColors)
			{
				colors.add(picker.getColor());
			}
			settings.templateGradientColors.setValue(colors);

			settings.hideIdColumn.setValue(ids.getValue());
			settings.pdciEnabled.setValue(pdci.getValue());
			settings.serverLoggingEnabled.setValue(logging.getValue());
			settings.debug.setValue(debug.getValue());
			settings.isReadOnlyMode.setValue(readOnly.getValue());
			settings.cookieNotifierEnabled.setValue(cookies.getValue());
			settings.keepTempFilesForHours.setValue(temp.getDoubleValue());
			settings.uploadSizeLimitMB.setValue(upLimit.getDoubleValue());
			settings.cookieLifespanMinutes.setValue(cookieLifespan.getIntegerValue());
			settings.galleryImagesPerPage.setValue(imagesPPage.getIntegerValue());
			settings.externalDataFolder.setValue(externalFolder.getValue());
			settings.templateCustomMenu.setValue(customMenu.getValue());
			settings.availablePages.setValue(((SetSelectionModel<Page>) availablePages.getTable().getSelectionModel()).getSelectedSet());

			/* Call the server with the new settings */
			CommonService.Inst.get().setAdminSettings(Cookie.getRequestProperties(), settings, new DefaultAsyncCallback<Void>()
			{
				@Override
				protected void onFailureImpl(Throwable caught)
				{
					/* Again, handle user permission exceptions */
					if (caught instanceof InsufficientPermissionsException)
					{
						Notification.notify(Notification.Type.ERROR, Text.LANG.adminConfigInsufficientPermissions());
					}
					else if (caught instanceof IOException)
					{
						Notification.notify(Notification.Type.ERROR, Text.LANG.notificationErrorWritingFile());
					}
					else
					{
						super.onFailureImpl(caught);
					}
				}

				@Override
				protected void onSuccessImpl(Void result)
				{
					Notification.notify(Notification.Type.SUCCESS, Text.LANG.notificationAdminConfigChangesApplied());
					GoogleAnalytics.trackEvent(GoogleAnalytics.Category.ADMIN, "changeSettings");
				}
			});
		}
		catch (Exception e)
		{
			/* We end up here, if any of the entered values isn't of the valid type */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationCheckEditTextValue());
		}
	}

	/* Define a callback for delete events of the categorical colors*/
	private ColorPickerWrapper.DeleteCallback categoryDeleteCallback = picker ->
	{
		if (templateCategoricalColors.size() > 1)
		{
			templateCategoricalColors.remove(picker);
			return true;
		}
		else
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationColorPickerAtLeastOne());
			return false;
		}
	};

	/* Define a callback for delete events of the gradient colors*/
	private ColorPickerWrapper.DeleteCallback gradientDeleteCallback = picker ->
	{
		if (templateGradientColors.size() > 2)
		{
			templateGradientColors.remove(picker);

			updateTemplateGradient();

			return true;
		}
		else
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationColorPickerAtLeastTwo());

			return false;
		}
	};

	/* Define a callback that updates the gradient when one of the colors changes */
	private ColorPicker.ChangeCallback gradientChangeCallback = color -> updateTemplateGradient();

	/* Define a callback that updates the gradient when a new color is added */
	private AddCallback gradientAddCallback = wrapper ->
	{
		wrapper.setChangeCallback(gradientChangeCallback);
		updateTemplateGradient();
	};

	private interface AddCallback
	{
		void onAdd(ColorPickerWrapper wrapper);
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.adminConfigHelp());
	}
}