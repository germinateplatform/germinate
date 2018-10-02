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

package jhi.germinate.client.page.allelefreq.binning;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class EqualWidthBinningWidget extends Composite
{
	interface EqualWidthBinningWidgetUiBinder extends UiBinder<FlowPanel, EqualWidthBinningWidget>
	{
	}

	private static EqualWidthBinningWidgetUiBinder ourUiBinder = GWT.create(EqualWidthBinningWidgetUiBinder.class);

	@UiField
	RangedIntegerTextBox   nrOfBins;
	@UiField
	AlleleFreqBinningChart chart;

	private Color low  = Color.fromHex("#ff7878");
	private Color high = Color.fromHex("#78fd78");

	private JsArrayString colors;
	private JsArrayNumber widths;

	public EqualWidthBinningWidget()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		onRefreshButtonClicked(null);
	}

	/**
	 * Updates the position of the visualization widget. This is required as it has to align with the allele frequency histogram chart.
	 *
	 * @param left  The left edge of the histograms x-axis
	 * @param width The width of the histograms x-axis
	 */
	public void updatePosition(int left, int width)
	{
		chart.getElement().getStyle().setWidth(width, Style.Unit.PX);
		chart.getElement().getStyle().setMarginLeft(left, Style.Unit.PX);

		chart.update(colors, widths);
	}

	@UiHandler("nrOfBins")
	void onNrOfBinsKeyPress(KeyPressEvent event)
	{
		if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
		{
			onRefreshButtonClicked(null);
		}
	}

	public AlleleFrequencyService.HistogramParams getParams()
	{
		AlleleFrequencyService.HistogramParams params = new AlleleFrequencyService.HistogramParams();
		params.nrOfBins = nrOfBins.getIntegerValue();
		params.method = HistogramMethod.STANDARD;

		return params;
	}

	@UiHandler("refreshButton")
	void onRefreshButtonClicked(ClickEvent event)
	{
		if (nrOfBins.validate(true))
		{
			int bins = nrOfBins.getIntegerValue();
			Color[] gradient = Gradient.createGradient(low, high, bins);

			widths = JsArrayNumber.createArray().cast();
			colors = JsArrayString.createArray().cast();

			for (int i = 0; i < bins; i++)
			{
				widths.push(100f / bins);
				colors.push(gradient[i].toHexValue());
			}

			chart.update(colors, widths);
		}
	}
}