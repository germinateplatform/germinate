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

package jhi.germinate.client.util;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link HasHyperlinkButton} is an interface to identify pages that supply a hyperlink button for sharing a link to the exact page the user is
 * looking at.
 *
 * @author Sebastian Raubach
 */
public interface HasHyperlinkButton
{
	/**
	 * {@link HyperlinkPopupOptions} is a helper class to store necessary settings for the creation of the hyperlink popup
	 *
	 * @author Sebastian Raubach
	 */
	final class HyperlinkPopupOptions
	{
		private       Page            page;
		private final List<Parameter> relevantParameters = new ArrayList<>();

		public HyperlinkPopupOptions()
		{
		}

		public HyperlinkPopupOptions setPage(Page page)
		{
			this.page = page;
			return this;
		}

		public HyperlinkPopupOptions addParam(Parameter param)
		{
			relevantParameters.add(param);
			return this;
		}

		public Page getPage()
		{
			return page;
		}

		public List<Parameter> getRelevantParameters()
		{
			return relevantParameters;
		}
	}

	/**
	 * Returns the {@link HyperlinkPopupOptions} used to create the URL with the page content
	 *
	 * @return The {@link HyperlinkPopupOptions}
	 */
	HyperlinkPopupOptions getHyperlinkOptions();
}
