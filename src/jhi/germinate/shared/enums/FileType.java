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
	kmz("application/vnd.google-earth.kmz", "earth", Style.FA_GLOBE),
	txt("application/txt", "txt", Style.FA_FILE_TEXT_O),
	tsv("application/txt", "txt", Style.FA_FILE_TEXT_O),
	pdf("application/pdf", "pdf", Style.FA_FILE_PDF_O),
	flapjack("application/flapjack", "flapjack", Style.FA_FILE_O),
	helium("application/helium", "helium", Style.FA_FILE_O),
	strudel("application/strudel", "strudel", Style.FA_FILE_O),
	mct("application/txt", "mct", Style.FA_FILE_O),
	map("application/flapjack", "flapjack", Style.FA_FILE_O),
	png("image/png", "png", Style.FA_FILE_IMAGE_O),
	xlsx("application/ms-excel", "xlsx", Style.FA_FILE_EXCEL_O),
	unknown("application/txt", "", "");

	private String contentType;
	private String iconStyle;
	private String faStyle;

	FileType(String contentType, String iconStyle, String faStyle)
	{
		this.contentType = contentType;
		this.iconStyle = iconStyle;
		this.faStyle = faStyle;
	}

	public String getContentType()
	{
		return contentType;
	}

	public String getIconStyle()
	{
		return iconStyle;
	}

	public String getFaStyle()
	{
		return faStyle;
	}
}
