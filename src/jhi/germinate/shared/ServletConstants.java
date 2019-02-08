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

package jhi.germinate.shared;

import com.google.gwt.http.client.*;

import java.util.*;
import java.util.Map.*;

/**
 * @author Sebastian Raubach
 */
public class ServletConstants
{
	/** The relative URL of the file servlet */
	public static final String SERVLET_FILES  = "file";
	/** The relative URL of the image servlet */
	public static final String SERVLET_IMAGES = "image";
	/** The relative URL of the group upload servlet */
	public static final String SERVLET_UPLOAD = "upload";

	/** The session id URL parameter */
	public static final String PARAM_SID = "sid";

	/** The file location URL parameter */
	public static final String PARAM_FILE_LOCATION    = "file-location";
	/** The file path URL parameter */
	public static final String PARAM_FILE_PATH        = "file-path";
	/** The file locale URL parameter */
	public static final String PARAM_FILE_LOCALE      = "file-locale";
	/** The image path URL parameter */
	public static final String PARAM_IMAGE_PATH       = "image-path";
	/** The reference folder URL parameter */
	public static final String PARAM_REFERENCE_FOLDER = "reference-folder";
	/** The size URL parameter */
	public static final String PARAM_SIZE             = "size";

	/** The username URL parameter */
	public static final String PARAM_USERNAME        = "username";
	/** The password URL parameter */
	public static final String PARAM_PASSWORD        = "password";
	/** The statistics view URL parameter */
	public static final String PARAM_STATISTICS_VIEW = "statistics-view";

	public enum FragmentPosition
	{
		START,
		END
	}

	public static class Builder
	{
		private String              url              = "";
		private String              path             = "";
		private Map<String, String> params;
		private FragmentPosition    fragmentPosition = FragmentPosition.END;
		private String              fragment         = null;

		public Builder()
		{
			params = new HashMap<>();
		}

		public Builder setUrl(String url)
		{
			this.url = url;
			return this;
		}

		public Builder setPath(String path)
		{
			this.path = path;
			return this;
		}

		public Builder setParam(String key, String value)
		{
			params.put(key, value);
			return this;
		}

		public Builder setFragment(String fragment)
		{
			if (fragment != null)
				fragment = fragment.replace("#", "");

			this.fragment = fragment;
			return this;
		}

		public Builder setFragment(FragmentPosition fragmentPosition, String fragment)
		{
			setFragment(fragment);
			this.fragmentPosition = fragmentPosition;
			return this;
		}

		public String build()
		{
			StringBuilder builder = new StringBuilder();

			if (!url.endsWith("/"))
				url += "/";

			builder.append(url);
			builder.append(path);

			if (fragmentPosition == FragmentPosition.START && !StringUtils.isEmpty(fragment))
				builder.append("#").append(fragment);

			boolean isFirst = true;
			for (Entry<String, String> stringStringEntry : params.entrySet())
			{
				if (isFirst)
				{
					isFirst = false;
					builder.append("?");
				}
				else
				{
					builder.append("&");
				}

				String value = stringStringEntry.getValue();

				value = URL.encodePathSegment(value);

				builder.append(stringStringEntry.getKey()).append("=").append(value);
			}

			if (fragmentPosition == FragmentPosition.END && !StringUtils.isEmpty(fragment))
				builder.append("#").append(fragment);

			return builder.toString();
		}

		@Override
		public String toString()
		{
			return build();
		}
	}
}
