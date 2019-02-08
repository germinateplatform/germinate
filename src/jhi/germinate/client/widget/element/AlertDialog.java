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

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.Label;

import org.gwtbootstrap3.client.shared.event.*;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;

/**
 * {@link AlertDialog} is a class used to easily create {@link Modal}s that are used similarly to the Android AlertDialog. They basically contain a
 * title, a message and two buttons. One of the buttons is a positive button and one a negative button.
 *
 * @author Sebastian Raubach
 */
public class AlertDialog
{
	protected ButtonGroup  buttonGroup = new ButtonGroup();
	private   ModalBody    content;
	private   String       heading;
	private   ButtonConfig positiveConfig;
	private   ButtonConfig negativeConfig;
	private   Modal        dialog      = new Modal();
	private   ModalFooter  footer      = new ModalFooter();

	private boolean preventClose        = false;
	private boolean autoCloseOnPositive = true;
	private boolean autoCloseOnNegative = true;
	private boolean removeOnHide        = true;
	private Button  negativeButton;
	private Button  positiveButton;
	private boolean isSetUp             = false;

	private String printableId;
	private String downloadFileName;

	public AlertDialog(String heading)
	{
		this.heading = heading;
	}

	/**
	 * Creates a new instance of {@link AlertDialog} with the given heading and message
	 *
	 * @param heading The heading
	 * @param content The content
	 */
	public AlertDialog(String heading, ModalBody content)
	{
		this(heading);
		this.content = content;
	}

	public AlertDialog(String heading, Widget widget)
	{
		this(heading);
		this.content = new ModalBody();
		this.content.add(widget);
	}

	public AlertDialog(String heading, String content)
	{
		this(heading);

		this.content = new ModalBody();
		this.content.add(new Label(content));
	}

	public AlertDialog setPrintable(String printableId, String downloadFileName)
	{
		this.printableId = printableId;
		this.downloadFileName = downloadFileName;
		return this;
	}

	public boolean isVisible()
	{
		return dialog.isVisible();
	}

	/**
	 * Sets the {@link ButtonConfig} of the positive button
	 *
	 * @param positiveConfig The {@link ButtonConfig} of the positive button
	 * @return <code>this</code>
	 */
	public AlertDialog setPositiveButtonConfig(ButtonConfig positiveConfig)
	{
		this.positiveConfig = positiveConfig;
		return this;
	}

	/**
	 * Sets the {@link ButtonConfig} of the negative button
	 *
	 * @param negativeConfig The {@link ButtonConfig} of the negative button
	 * @return <code>this</code>
	 */
	public AlertDialog setNegativeButtonConfig(ButtonConfig negativeConfig)
	{
		this.negativeConfig = negativeConfig;
		return this;
	}

	/**
	 * Sets the content of the {@link AlertDialog}.
	 *
	 * @param content The {@link FlowPanel} to show as the content of this {@link AlertDialog}.
	 * @return <code>this</code>
	 */
	public AlertDialog setContent(ModalBody content)
	{
		this.content = content;
		return this;
	}

	public AlertDialog setContent(Widget widget)
	{
		this.content = new ModalBody();
		this.content.add(widget);
		return this;
	}

	public AlertDialog addShowHandler(ModalShowHandler handler)
	{
		dialog.addShowHandler(handler);
		return this;
	}

	public AlertDialog addShownHandler(ModalShownHandler handler)
	{
		dialog.addShownHandler(handler);
		return this;
	}

	/**
	 * Closes the {@link AlertDialog}
	 */
	public void close()
	{
		if (dialog != null)
			dialog.hide();
	}

	public AlertDialog addHideHandler(ModalHideHandler handler)
	{
		dialog.addHideHandler(handler);
		return this;
	}

	/**
	 * Set to true if you don't want the {@link AlertDialog} to automatically close after the user pressed one of the buttons
	 *
	 * @param preventClose Setting to <code>true</code> will prevent automatic close
	 */
	public AlertDialog setPreventClose(boolean preventClose)
	{
		this.preventClose = preventClose;
		return this;
	}

	public AlertDialog setAutoCloseOnPositive(boolean autoCloseOnPositive)
	{
		this.autoCloseOnPositive = autoCloseOnPositive;
		return this;
	}

	public AlertDialog setAutoCloseOnNegative(boolean autoCloseOnNegative)
	{
		this.autoCloseOnNegative = autoCloseOnNegative;
		return this;
	}

	public AlertDialog setRemoveOnHide(boolean removeOnHide)
	{
		this.removeOnHide = removeOnHide;
		return this;
	}

	public void remove()
	{
		dialog.removeFromParent();
	}

	public static void createYesNoDialog(String title, String message, boolean danger, ClickHandler positive, ClickHandler negative)
	{
		ModalBody body = new ModalBody();
		body.add(new Label(message));
		new AlertDialog(title, body)
				.setPositiveButtonConfig(new ButtonConfig(Text.LANG.generalYes(), Style.MDI_CHECK, danger ? ButtonType.DANGER : ButtonType.PRIMARY, positive))
				.setNegativeButtonConfig(new ButtonConfig(Text.LANG.generalNo(), Style.MDI_CANCEL, ButtonType.DEFAULT, negative))
				.focusPositiveButton()
				.open();
	}

	/**
	 * Opens the {@link AlertDialog}.
	 */
	public void open()
	{
		if (!isSetUp)
			setUp();

		dialog.show();
	}

	protected void setUp()
	{
		isSetUp = true;

		dialog.setRemoveOnHide(removeOnHide);
		dialog.setFade(true);
		dialog.setTitle(heading);
		if (preventClose)
		{
			dialog.setClosable(false);
			dialog.setDataBackdrop(ModalBackdrop.STATIC);
		}

		dialog.add(content);

		if (!StringUtils.isEmpty(printableId))
		{
			ButtonGroup group = new ButtonGroup();

			Button toggle = new Button(Text.LANG.generalSave());
			toggle.addStyleName(Style.mdiLg(Style.MDI_DOWNLOAD));
			toggle.setDataToggle(Toggle.DROPDOWN);
			toggle.setToggleCaret(true);
			DropDownMenu menu = new DropDownMenu();
			MdiAnchorListItem print = new MdiAnchorListItem();
			print.setText(Text.LANG.generalPrint());
			print.setMdi(Style.MDI_PRINTER);

			print.addClickHandler(event -> {
				JavaScript.printObject(printableId);
			});

			MdiAnchorListItem html = new MdiAnchorListItem();
			html.setText(Text.LANG.downloadFileAsHtml());
			html.setMdi(Style.MDI_FILE_XML);
			html.addClickHandler(event -> {
				String url = JavaScript.getOctetStreamBase64Data(JavaScript.html("#" + printableId));
				String filename = StringUtils.isEmpty(downloadFileName) ? "download.txt" : downloadFileName;
				JavaScript.invokeDownload(url, filename);
			});

			menu.add(print);
			menu.add(html);
			group.add(toggle);
			group.add(menu);
			buttonGroup.add(group);
		}

		if (negativeConfig != null)
		{
			negativeButton = new Button(negativeConfig.text);
			negativeButton.setType(negativeConfig.buttonType);
			if (!StringUtils.isEmpty(negativeConfig.mdiIcon))
				negativeButton.addStyleName(Style.mdiLg(negativeConfig.mdiIcon));

			if (!StringUtils.isEmpty(negativeConfig.id))
				negativeButton.getElement().setId(negativeConfig.id);

			negativeButton.addClickHandler(event ->
			{
				if (negativeConfig.callback != null)
					negativeConfig.callback.onClick(event);

				if (autoCloseOnNegative)
					dialog.hide();
			});

			buttonGroup.add(negativeButton);
		}

		if (positiveConfig != null)
		{
			positiveButton = new Button(positiveConfig.text);
			positiveButton.setType(positiveConfig.buttonType);
			if (!StringUtils.isEmpty(positiveConfig.mdiIcon))
				positiveButton.addStyleName(Style.mdiLg(positiveConfig.mdiIcon));

			if (!StringUtils.isEmpty(positiveConfig.id))
				positiveButton.getElement().setId(positiveConfig.id);

			positiveButton.addClickHandler(event ->
			{
				if (positiveConfig.callback != null)
					positiveConfig.callback.onClick(event);

				if (autoCloseOnPositive)
					dialog.hide();
			});

			buttonGroup.add(positiveButton);
		}

		footer.add(buttonGroup);
		dialog.add(footer);
	}

	public AlertDialog setSize(ModalSize size)
	{
		dialog.setSize(size);
		return this;
	}

	public void negativeClick()
	{
		if (negativeButton != null)
			negativeButton.click();
	}

	public void positiveClick()
	{
		if (positiveButton != null)
			positiveButton.click();
	}

	public AlertDialog focusPositiveButton()
	{
		dialog.addShownHandler(e -> {
			if (positiveButton != null)
				positiveButton.setFocus(true);
		});

		return this;
	}

	/**
	 * {@link ButtonConfig} is a helper class used in combination with {@link AlertDialog}. It is used to configure how the buttons look.
	 *
	 * @author Sebastian Raubach
	 */
	public static final class ButtonConfig
	{
		private String       text;
		private String       mdiIcon;
		private ButtonType   buttonType;
		private String       id;
		private ClickHandler callback;

		/**
		 * Creates a new instance of {@link ButtonConfig} using the given text, {@link Icon} and {@link Size}
		 *
		 * @param text     The text to display on the button
		 * @param icon     The MDI style to show on the left
		 * @param type     The {@link ButtonType}
		 * @param callback The {@link ClickHandler}
		 */
		public ButtonConfig(String text, String icon, ButtonType type, ClickHandler callback)
		{
			this.text = text;
			this.mdiIcon = icon;
			this.buttonType = type;
			this.callback = callback;
		}

		/**
		 * Creates a new instance of {@link ButtonConfig} using the given text, {@link Icon} and {@link Size}
		 *
		 * @param text     The text to display on the button
		 * @param icon     The MDI style to show on the left
		 * @param callback The {@link ClickHandler}
		 */
		public ButtonConfig(String text, String icon, ClickHandler callback)
		{
			this(text, icon, ButtonType.DEFAULT, callback);
		}
	}
}
