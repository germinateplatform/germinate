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

import java.util.*;

/**
 * This {@link Enum} represents the supported image types of Germinate. {@link #getContentType()} can be used to get the associated content type.
 *
 * @author Sebastian Raubach
 */
public enum ImageMimeType
{
	bmp("image/bmp"),
	jpeg("image/jpeg"),
	jpg("image/jpeg"),
	png("image/png"),
	gif("image/gif");

	private String contentType;

	ImageMimeType(String contentType)
	{
		this.contentType = contentType;
	}

	public String getContentType()
	{
		return contentType;
	}

	public static String[] stringValues()
	{
		return Arrays.stream(ImageMimeType.values())
					 .map(ImageMimeType::name)
					 .toArray(size -> new String[size]);
	}
}
