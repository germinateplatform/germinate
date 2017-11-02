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

package jhi.germinate.client.widget.gallery;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class GalleryItem extends Composite
{
	interface GalleryItemUiBinder extends UiBinder<Column, GalleryItem>
	{
	}

	private static GalleryItemUiBinder ourUiBinder = GWT.create(GalleryItemUiBinder.class);

	@UiField
	Column panel;

	@UiField
	AnchorWithContent anchor;

	@UiField
	SimplePanel image;

	@UiField
	Heading heading;

	@UiField
	ParagraphPanel paragraph;

	@UiField
	Button button;

	private Image img;

	public GalleryItem(Image img)
	{
		this(img, false);
	}

	public GalleryItem(Image img, boolean showButton)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		this.img = img;

		String urlSmall = getUrl(img, ImageService.SIZE_SMALL);
		String urlLarge = getUrl(img, ImageService.SIZE_LARGE);

		if (!StringUtils.isEmpty(img.getPath()))
			image.getElement().getStyle().setBackgroundImage("url(" + UriUtils.fromTrustedString(urlSmall).asString() + ")");
		else
			image.setVisible(false);

		heading.setText(img.getPath());
		heading.setTitle(img.getPath());
		paragraph.setText(img.getDescription());
		paragraph.setTitle(img.getDescription());

		anchor.setHref(UriUtils.fromTrustedString(urlLarge).asString());
		anchor.setTitle(img.getDescription());

		if (img.getType() != null && showButton && isPageAvailable())
		{
			button.setVisible(true);
			button.getElement().getStyle().setBackgroundColor(GerminateSettingsHolder.getCategoricalColor(img.getType().getId().intValue() - 1));
			button.setColor("white");

			String text = img.getType().getDescription();

			if (!StringUtils.isEmpty(img.getExtra(ImageService.IMAGE_REFERENCE_NAME)))
				text += ": " + img.getExtra(ImageService.IMAGE_REFERENCE_NAME);

			button.setText(text);
		}
	}

	public void setGalleryId(String id)
	{
		anchor.getElement().setAttribute("data-gallery", id);
	}

	private boolean isPageAvailable()
	{
		if (img.getType() == null || img.getType().getReferenceTable() == null)
			return false;
		else
		{
			switch (img.getType().getReferenceTable())
			{
				case germinatebase:
					return GerminateSettingsHolder.isPageAvailable(Page.PASSPORT);
				case compounds:
					return GerminateSettingsHolder.isPageAvailable(Page.COMPOUNDS);
				default:
					return false;
			}
		}
	}

	private String getUrl(Image img, String size)
	{
		String imagePath = img.getPath();

		if (!imagePath.startsWith("http://") && !imagePath.startsWith("https://"))
		{
			imagePath = new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
													  .setPath(ServletConstants.SERVLET_IMAGES)
													  .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
													  .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
													  .setParam(ServletConstants.PARAM_SIZE, size)
													  .setParam(ServletConstants.PARAM_IMAGE_PATH, imagePath)
													  .build();
		}

		return imagePath;
	}

	public void setSize(ColumnSize first, ColumnSize... sizes)
	{
		panel.setSize(first, sizes);
	}

	@UiHandler("button")
	void onButtonClicked(ClickEvent event)
	{
		switch (img.getType().getReferenceTable())
		{
			case germinatebase:
				LongParameterStore.Inst.get().put(Parameter.accessionId, img.getForeignId());
				History.newItem(Page.PASSPORT.name());
				break;
			case compounds:
				LongParameterStore.Inst.get().put(Parameter.compoundId, img.getForeignId());
				History.newItem(Page.COMPOUNDS.name());
				break;
		}
	}
}