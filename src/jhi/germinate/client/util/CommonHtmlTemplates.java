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

import com.google.gwt.core.client.*;
import com.google.gwt.safehtml.client.*;
import com.google.gwt.safehtml.shared.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public interface CommonHtmlTemplates extends SafeHtmlTemplates
{
	CommonHtmlTemplates INSTANCE = GWT.create(CommonHtmlTemplates.class);

	/**
	 * Creates a &lt;pre&gt;&lt;code&gt;CODE&lt;/code&gt;&lt;/pre&gt; block with pretty print styling.
	 *
	 * @param message The code
	 * @return An anchor with a highlighted substring
	 */
	@Template("<pre class='" + Style.PRETTY_PRINT + "'><code class='{0}'>{1}</code></span>")
	SafeHtml preCode(String codeClass, String message);
}
