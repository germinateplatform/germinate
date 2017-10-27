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

package jhi.germinate.shared.enums;

import jhi.germinate.shared.*;

/**
 * This {@link Enum} represents the file types that are used within Germinate. {@link #getContentType()} can be used to get the associated content
 * type and {@link #getIconStyle()} can be used to get the associated icon style (the mime icon).
 *
 * @author Sebastian Raubach
 */
public enum FileType
{
	kmz("application/vnd.google-earth.kmz", "earth", Style.MDI_GOOGLE_EARTH),
	txt("application/txt", "txt", Style.MDI_FILE_DOCUMENT),
	tsv("application/txt", "txt", Style.MDI_FILE_DOCUMENT),
	pdf("application/pdf", "pdf", Style.MDI_FILE_PDF),
	flapjack("application/flapjack", "flapjack", Style.MDI_FILE_DOCUMENT),
	helium("application/helium", "helium", Style.MDI_FILE_DOCUMENT),
	strudel("application/strudel", "strudel", Style.MDI_FILE_DOCUMENT),
	mct("application/txt", "mct", Style.MDI_FILE_DOCUMENT),
	map("application/flapjack", "flapjack", Style.MDI_FILE_DOCUMENT),
	png("image/png", "png", Style.MDI_FILE_IMAGE),
	xlsx("application/ms-excel", "xlsx", Style.MDI_FILE_EXCEL),
	unknown("application/txt", "", "");

	private String contentType;
	private String iconStyle;
	private String mdiStyle;

	FileType(String contentType, String iconStyle, String mdiStyle)
	{
		this.contentType = contentType;
		this.iconStyle = iconStyle;
		this.mdiStyle = mdiStyle;
	}

	public String getContentType()
	{
		return contentType;
	}

	public String getIconStyle()
	{
		return iconStyle;
	}

	public String getMdiStyle()
	{
		return mdiStyle;
	}
}
