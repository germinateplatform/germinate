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

package jhi.germinate.client.widget.d3js.resource;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;

/**
 * @author Sebastian Raubach
 */
public class Bundles
{
	public interface PedigreeChartBundle extends ClientBundle
	{
		PedigreeChartBundle INSTANCE    = GWT.create(PedigreeChartBundle.class);
		Boolean             IS_INJECTED = INSTANCE.css().ensureInjected();

		String STYLE_NODE      = INSTANCE.css().node();
		String STYLE_EDGE_PATH = INSTANCE.css().edgePath();
		String STYLE_MALE      = INSTANCE.css().male();
		String STYLE_FEMALE    = INSTANCE.css().female();

		@Source("pedigree-chart.css")
		MyResource css();

		interface MyResource extends CssResource
		{
			String node();

			String edgePath();

			String male();

			String female();
		}
	}

	public interface TreemapBundle extends ClientBundle
	{
		TreemapBundle INSTANCE = GWT.create(TreemapBundle.class);

		String  STYLE_GRANDPARENT = INSTANCE.css().grandparent();
		String  STYLE_PARENT      = INSTANCE.css().parent();
		String  STYLE_CHILDREN    = INSTANCE.css().children();
		String  STYLE_CHILD       = INSTANCE.css().child();
		String  STYLE_RECT        = INSTANCE.css().rect();
		String  STYLE_TEXT        = INSTANCE.css().text();
		String  STYLE_D3_TIP_TOP  = INSTANCE.css().d3TipTop();
		/* Make sure the resource is injected */
		Boolean IS_INJECTED       = INSTANCE.css().ensureInjected();

		@Source("treemap.css")
		MyResources css();
	}

	interface MyResources extends CssResource
	{
		String grandparent();

		String parent();

		String children();

		String child();

		String rect();

		String text();

		String d3TipTop();
	}
}
