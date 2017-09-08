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

package jhi.germinate.client.widget.element;

import com.google.gwt.canvas.client.*;
import com.google.gwt.canvas.dom.client.*;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.safecss.shared.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.management.*;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * {@link GradientUtils} contains methods to create {@link Widget}s from {@link Gradient}s
 *
 * @author Sebastian Raubach
 */
public class GradientUtils
{
	public enum HorizontalLegendPosition
	{
		TOP,
		BOTTOM
	}

	private static final int DEFAULT_SIZE = 25;

	public static Widget createHorizontalGradientLegend(Gradient gradient, HorizontalLegendPosition position)
	{
		return createHorizontalGradientLegend(gradient, position, false);
	}

	public static Widget createHorizontalGradientLegend(Gradient gradient, HorizontalLegendPosition position, boolean showActualValues)
	{
		FlowPanel overall = new FlowPanel();
		FlowPanel gradientPanel = new FlowPanel();

		if (Canvas.isSupported())
		{
			Canvas canvas = Canvas.createIfSupported();

			int overallWidth = ContentHolder.getContentWidth();

			canvas.setWidth("100%");
			canvas.setHeight(DEFAULT_SIZE + "px");
			gradientPanel.getElement().getStyle().setHeight(DEFAULT_SIZE, Unit.PX);

			canvas.setCoordinateSpaceWidth(overallWidth);
			canvas.setCoordinateSpaceHeight(DEFAULT_SIZE);

			Context2d context = canvas.getContext2d();

			CanvasGradient cGradient = context.createLinearGradient(5, 0, overallWidth - 5, 0);

            /* Add an intermediate step for each color */
			int size = gradient.getColors().length;
			for (int i = 0; i < size; i++)
			{
				cGradient.addColorStop((1.0f * i) / (size - 1), gradient.getColors()[i].toHexValue());
			}

			context.setFillStyle(cGradient);
			context.fillRect(0, 0, overallWidth, DEFAULT_SIZE);

			gradientPanel.add(canvas);
		}
		else
		{
			float perc = (100f / gradient.getColors().length);
			/* Add a div for each of the colors */
			for (Color color : gradient.getColors())
			{
				SafeStyles style = SafeStylesUtils.forTrustedBackgroundColor(color.toRGBValue());
				HTML html = new HTML(SimpleHtmlTemplate.INSTANCE.gradientLegendHorizontal(style));
				html.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
				html.getElement().getStyle().setWidth(perc, Unit.PCT);
				gradientPanel.add(html);
			}
		}

		FlowPanel legend = new FlowPanel();

        /* Add the text legend */
		String lowValue = Text.LANG.generalLow();

		if (showActualValues)
			lowValue += " (" + gradient.getMin() + ")";

		Label low = new Label(lowValue);
		low.addStyleName(Styles.PULL_LEFT);
		legend.add(low);

		String highValue = Text.LANG.generalHigh();

		if (showActualValues)
			highValue += " (" + gradient.getMax() + ")";

		Label high = new Label(highValue);
		high.addStyleName(Styles.PULL_RIGHT);
		legend.add(high);

		if (position == HorizontalLegendPosition.TOP)
		{
			overall.add(legend);
			overall.add(HTMLUtils.createClearBoth());
			overall.add(gradientPanel);
		}
		else
		{
			overall.add(gradientPanel);
			overall.add(legend);
			overall.add(HTMLUtils.createClearBoth());
		}

		overall.addStyleName(Style.LAYOUT_CLEAR_BOTH);

		return overall;
	}

	/**
	 * Creates a horizontal gradient legend (running from min to max)
	 *
	 * @param gradient The {@link Gradient} to use
	 * @return The created gradient legend
	 */
	public static Widget createHorizontalGradientLegend(Gradient gradient)
	{
		return createHorizontalGradientLegend(gradient, HorizontalLegendPosition.BOTTOM);
	}
}
