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

package jhi.germinate.client.widget.table.resource;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public interface BorderStyleResource extends ClientBundle
{
	class Instance
	{
		private static final BorderStyleResource RES = GWT.create(BorderStyleResource.class);

		static
		{
			RES.css().ensureInjected();
		}

		public static String getStyle(BorderStyle... borders)
		{
			StringBuilder style = new StringBuilder();

			for (BorderStyle b : borders)
			{
				style.append(" ")
					 .append(b.style);
			}

			return style.toString();
		}
	}

	enum BorderStyle
	{
		FULL(Instance.RES.css().borderLeft(), Instance.RES.css().borderRight(), Instance.RES.css().borderBottom(), Instance.RES.css().borderTop()),
		TOP(Instance.RES.css().borderTop()),
		RIGHT(Instance.RES.css().borderRight()),
		BOTTOM(Instance.RES.css().borderBottom()),
		LEFT(Instance.RES.css().borderLeft()),
		NO_TOP(Instance.RES.css().borderLeft(), Instance.RES.css().borderRight(), Instance.RES.css().borderBottom()),
		NO_BOTTOM(Instance.RES.css().borderLeft(), Instance.RES.css().borderRight(), Instance.RES.css().borderTop());

		private String style = "";

		BorderStyle(String... styles)
		{
			this.style = StringUtils.join(" ", styles);
		}

		public String getStyle()
		{
			return style;
		}
	}


	/**
	 * The styles used in this widget.
	 */
	@Source("style-border.css")
	Style css();

	interface Style extends CssResource
	{
		String borderLeft();

		String borderRight();

		String borderTop();

		String borderBottom();
	}
}
