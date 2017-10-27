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

package jhi.germinate.client.widget.colorpicker;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.resources.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.shared.*;

/**
 * ColorPicker is a widget that wraps the JavaScript library <a href="https://bgrins.github.io/spectrum/">Spectrum</a>. It's a color picker widget.
 *
 * @author Sebastian Raubach
 */
public class ColorPicker extends Widget
{
	private static final String CONFIRM = Text.LANG.generalConfirm();
	private static final String CANCEL  = Text.LANG.generalCancel();

	private String color      = "#ffffff";
	private String innerStyle = "";
	private final InputElement   element;
	private       ChangeCallback callback;

	public interface ColorPickerResources extends ClientBundle
	{
		@Source("spectrum.css")
		TextResource css();

		@Source("germinate-theme.css")
		TextResource theme();

		@Source("spectrum.js")
		TextResource js();
	}

	static
	{
		load();
	}

	/**
	 * Check if the script is loaded
	 *
	 * @return <code>true</code> if loaded, <code>false</code> otherwise
	 */
	public static native boolean isLoaded() /*-{
		return $wnd.jQuery.spectrum !== undefined;
	}-*/;

	/**
	 * Load the javasccript and the stylesheet by using the ScriptInjector
	 */
	public static void load()
	{
		if (!isLoaded())
		{
			/* Inject all the resources */
			ColorPickerResources resources = GWT.create(ColorPickerResources.class);
			StyleInjector.inject(resources.css().getText());
			StyleInjector.inject(resources.theme().getText());
			ScriptInjector.fromString(resources.js().getText()).setWindow(ScriptInjector.TOP_WINDOW).inject();
		}
	}

	public ColorPicker()
	{
		element = Document.get().createTextInputElement();
		setElement(element);

		element.setId("spectrum-" + RandomUtils.RANDOM.nextLong());
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		init(element);

		GQuery button = GQuery.$(".sp-choose");

		button.removeClass("sp-choose")
			  .addClass(Styles.BTN)
			  .addClass(ButtonType.DEFAULT.getCssName());

		button.css("padding", "4px 5px");
	}

	@Override
	protected void onUnload()
	{
		super.onUnload();

		/* Remember to destroy the DOM elements the JS library created */
		destroy(element);
	}

	private native void destroy(Element element) /*-{
		$wnd.jQuery(element).spectrum('destroy');
	}-*/;

	private native void init(Element element) /*-{
		// Remember the scope
		var that = this;

		$wnd.jQuery(element).spectrum({
			preferredFormat: "hex", // Only accept hex values
			color: this.@jhi.germinate.client.widget.colorpicker.ColorPicker::getColor()(), // Set the current color
			showInput: true, // Allow direct input of hex values
			theme: 'sp-germinate', // The theme
			replacerClassName: this.@jhi.germinate.client.widget.colorpicker.ColorPicker::getInnerStyle()(), // Force an additional style
			chooseText: @jhi.germinate.client.widget.colorpicker.ColorPicker::CONFIRM, // Set the confirm text
			cancelText: @jhi.germinate.client.widget.colorpicker.ColorPicker::CANCEL, // Set the cancel text
			change: function (color) {
				that.@jhi.germinate.client.widget.colorpicker.ColorPicker::onChange(*)(color.toHexString());
			} // Define a change handler
		});
	}-*/;

	private void onChange(String color)
	{
		setColor(color);
		if (callback != null)
			callback.onChange(color);
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;

		if (super.isAttached())
			setColorJs(element, color);
	}

	private native void setColorJs(Element element, String color)/*-{
		$wnd.jQuery(element).spectrum("set", color);
	}-*/;

	public void setInnerStyle(String innerStyle)
	{
		this.innerStyle = innerStyle;
	}

	public void setCallback(ChangeCallback callback)
	{
		this.callback = callback;
	}

	public String getInnerStyle()
	{
		return innerStyle;
	}

	public interface ChangeCallback
	{
		void onChange(String color);
	}
}
