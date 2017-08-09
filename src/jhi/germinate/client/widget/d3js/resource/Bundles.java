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

package jhi.germinate.client.widget.d3js.resource;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;

/**
 * @author Sebastian Raubach
 */
public class Bundles
{
	/**
	 * The {@link BaseBundle} is used for styling d3 charts with d3Tip tooltips
	 *
	 * @author Sebastian Raubach
	 */
	public interface BaseBundle extends ClientBundle
	{
		BaseBundle INSTANCE    = GWT.create(BaseBundle.class);
		Boolean    IS_INJECTED = INSTANCE.css().ensureInjected();

		String STYLE_AXIS           = INSTANCE.css().axis();
		String STYLE_D3_TIP_TOP     = INSTANCE.css().d3TipTop();
		String STYLE_D3_TIP_RIGHT   = INSTANCE.css().d3TipRight();
		String STYLE_D3_LEGEND_ITEM = INSTANCE.css().legendItem();

		@Source("base.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			String axis();

			String d3TipTop();

			String d3TipRight();

			String legendItem();
		}
	}

	/**
	 * The {@link ClientBundle} used for styling the d3 histogram and bar chart
	 *
	 * @author Sebastian Raubach
	 */
	public interface ScatterChartBundle extends ClientBundle
	{
		ScatterChartBundle INSTANCE = GWT.create(ScatterChartBundle.class);

		String  STYLE_DOT   = INSTANCE.css().dot();
		/* Make sure the resource is injected */
		Boolean IS_INJECTED = INSTANCE.css().ensureInjected();

		@Source("scatter-chart.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			String dot();
		}
	}

	/**
	 * The {@link ClientBundle} used for styling the d3 histogram and bar chart
	 *
	 * @author Sebastian Raubach
	 */
	public interface LassoBundle extends ClientBundle
	{
		LassoBundle INSTANCE           = GWT.create(LassoBundle.class);
		/* Make sure the resource is injected */
		Boolean     IS_INJECTED        = INSTANCE.css().ensureInjected();
		String      STYLE_SELECTED     = INSTANCE.css().selected();
		String      STYLE_POSSIBLE     = INSTANCE.css().possible();
		String      STYLE_NOT_POSSIBLE = INSTANCE.css().notPossible();

		@Source("lasso.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			@NotStrict
			String lasso();

			@NotStrict
			String drawn();

			@NotStrict
			String origin();

			@NotStrict
			@ClassName("loop_close")
			String loopClose();

			@NotStrict
			String possible();

			@NotStrict
			@ClassName("not_possible")
			String notPossible();

			@NotStrict
			String selected();
		}
	}

	/**
	 * The {@link ClientBundle} used for styling the d3 histogram
	 *
	 * @author Sebastian Raubach
	 */
	public interface ClimateLineChartBundle extends ClientBundle
	{
		ClimateLineChartBundle INSTANCE = GWT.create(ClimateLineChartBundle.class);

		/* Make sure the resource is injected */
		Boolean IS_INJECTED = INSTANCE.css().ensureInjected();

		String STYLE_LINE = INSTANCE.css().line();

		@Source("climate-line-chart.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			String line();
		}
	}

	/**
	 * The {@link ClientBundle} used for styling the d3 histogram and bar chart
	 *
	 * @author Sebastian Raubach
	 */
	public interface ScatterMatrixChartBundle extends ClientBundle
	{
		ScatterMatrixChartBundle INSTANCE     = GWT.create(ScatterMatrixChartBundle.class);
		String                   STYLE_AXIS   = INSTANCE.css().axis();
		String                   STYLE_FRAME  = INSTANCE.css().frame();
		String                   STYLE_HIDDEN = INSTANCE.css().hidden();
		String                   STYLE_DOT    = INSTANCE.css().dot();
		/* Make sure the resource is injected */
		Boolean                  IS_INJECTED  = INSTANCE.css().ensureInjected();

		@Source("scatter-matrix-chart.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			String axis();

			String frame();

			String hidden();

			@NotStrict
			String extent();

			String dot();
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
	}

	/**
	 * The {@link ClientBundle} used for styling the d3 histogram
	 *
	 * @author Sebastian Raubach
	 */
	public interface AlleleFrequencyChartBundle extends ClientBundle
	{
		AlleleFrequencyChartBundle INSTANCE    = GWT.create(AlleleFrequencyChartBundle.class);
		String                     STYLE_BAR   = INSTANCE.css().bar();
		/* Make sure the resource is injected */
		Boolean                    IS_INJECTED = INSTANCE.css().ensureInjected();

		@Source("allelefrequency.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			String bar();
		}
	}

	/**
	 * The {@link ClientBundle} used for styling the d3 histogram
	 *
	 * @author Sebastian Raubach
	 */
	public interface FlapjackBundle extends ClientBundle
	{
		FlapjackBundle INSTANCE          = GWT.create(FlapjackBundle.class);
		String         STYLE_BINNING_DIV = INSTANCE.css().flapjackBinning();
		String         STYLE_SEPARATOR   = INSTANCE.css().flapjackSeparator();
		String         STYLE_AREA        = INSTANCE.css().flapjackArea();
		String         STYLE_TOOLTIP     = INSTANCE.css().flapjackTooltip();
		/* Make sure the resource is injected */
		Boolean        IS_INJECTED       = INSTANCE.css().ensureInjected();

		@Source("allelefreq-flapjack-binning.css")
		MyResources css();

		interface MyResources extends CssResource
		{
			String flapjackArea();

			String flapjackSeparator();

			String flapjackBinning();

			String flapjackTooltip();
		}
	}
}
