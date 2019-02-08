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
import com.google.gwt.resources.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.shared.*;

/**
 * ColorPickerWrapper is a simple wrapper widget for a {@link ColorPicker}. It basically just adds a border around it and adds a delete button that
 * will delete this widget from the parent.
 *
 * @author Sebastian Raubach
 */
public class ColorPickerWrapper extends Composite
{
	/**
	 * The {@link ClientBundle} used for styling the annotations
	 *
	 * @author Sebastian Raubach
	 */
	public interface Bundle extends ClientBundle
	{
		Bundle INSTANCE = GWT.create(Bundle.class);

		/* Make sure the resource is injected */
		Boolean IS_INJECTED = INSTANCE.css().ensureInjected();

		@Source("wrapper.css")
		Bundle.Res css();

		interface Res extends CssResource
		{
			String spectrumWrapper();

			String spectrumWrapperColorPicker();
		}
	}

	private ColorPicker                colorPicker;
	private FlowPanel                  content;
	private DeleteCallback             callback;
	private String                     color = "white";
	private ColorPicker.ChangeCallback changeCallback;

	public ColorPickerWrapper(final DeleteCallback callback)
	{
		this();
		this.callback = callback;
	}

	public ColorPickerWrapper()
	{
		content = new FlowPanel();
		initWidget(content);
	}

	public void setDeleteCallback(DeleteCallback callback)
	{
		this.callback = callback;
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		Bundle.INSTANCE.css().ensureInjected();

		Button deleteButton = new Button();
		deleteButton.setStyleName(Style.combine(Style.WIDGET_ICON_BUTTON, Style.MDI, Style.MDI_DELETE, Style.MDI_LG));
		deleteButton.addClickHandler(event ->
		{
			/* Notify the caller */
			if (callback != null && callback.onDelete(ColorPickerWrapper.this))
			{
				/* And remove myself */
				ColorPickerWrapper.this.removeFromParent();
			}
		});

		colorPicker = new ColorPicker();
		colorPicker.setColor(color);
		if (changeCallback != null)
			colorPicker.setCallback(changeCallback);
		colorPicker.setInnerStyle(Bundle.INSTANCE.css().spectrumWrapperColorPicker());
		content.add(colorPicker);
		content.add(deleteButton);
		content.addStyleName(Bundle.INSTANCE.css().spectrumWrapper());
	}

	public void setColor(String color)
	{
		this.color = color;

		if (colorPicker != null)
			colorPicker.setColor(color);
	}

	public String getColor()
	{
		return colorPicker.getColor();
	}

	public void setChangeCallback(ColorPicker.ChangeCallback callback)
	{
		this.changeCallback = callback;
		if (colorPicker != null)
			colorPicker.setCallback(callback);
	}

	/**
	 * This callback is used to notify the caller that this {@link ColorPickerWrapper} has been removed by the user.
	 */
	public interface DeleteCallback
	{
		/**
		 * Called when the user clicks on the delete button.
		 *
		 * @param picker The {@link ColorPickerWrapper} in question.
		 */
		boolean onDelete(ColorPickerWrapper picker);
	}
}
