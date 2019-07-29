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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.widget.input.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class SplitPointBinningWidget extends Composite
{
	interface EqualWidthBinningWidgetUiBinder extends UiBinder<FlowPanel, SplitPointBinningWidget>
	{
	}

	private static EqualWidthBinningWidgetUiBinder ourUiBinder = GWT.create(EqualWidthBinningWidgetUiBinder.class);

	@UiField
	RangedIntegerTextBox   nrOfBinsLeft;
	@UiField
	RangedNumberTextBox    splitPoint;
	@UiField
	RangedIntegerTextBox   nrOfBinsRight;

	private Color low  = Color.fromHex("#ff7878");
	private Color high = Color.fromHex("#78fd78");

	private JsArrayNumber widths;

	private Callback<JsArrayNumber, Throwable> callback = null;

	public SplitPointBinningWidget()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	public SplitPointBinningWidget setCallback(Callback<JsArrayNumber, Throwable> callback)
	{
		this.callback = callback;
		return this;
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		onRefreshButtonClicked(null);
	}

	public void refresh()
	{
		onRefreshButtonClicked(null);
	}

	public void updateSplitPosition(double splitPoint)
	{
		this.splitPoint.setValue(splitPoint);
		onRefreshButtonClicked(null);
	}

	@UiHandler({"nrOfBinsLeft", "splitPoint", "nrOfBinsRight"})
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
		params.nrOfBins = nrOfBinsLeft.getIntegerValue();
		params.nrOfBinsRight = nrOfBinsRight.getIntegerValue();
		params.splitPoint = splitPoint.getDoubleValue();
		params.method = HistogramMethod.SPLIT;

		return params;
	}

	@UiHandler({"refreshButtonOne", "refreshButtonTwo", "refreshButtonThree"})
	void onRefreshButtonClicked(ClickEvent event)
	{
		if (nrOfBinsLeft.validate(true) && splitPoint.validate(true) && nrOfBinsRight.validate(true))
		{
			int binsLeft = nrOfBinsLeft.getIntegerValue();
			int binsRight = nrOfBinsRight.getIntegerValue();
			double split = splitPoint.getDoubleValue();
			int bins = binsLeft + binsRight;

			widths = JsArrayNumber.createArray().cast();

			for (int i = 0; i < binsLeft; i++)
				widths.push(split / binsLeft * 100);
			for (int i = binsLeft; i < bins; i++)
				widths.push((1 - split) / binsRight * 100);

			if (callback != null)
				callback.onSuccess(widths);
		}
	}
}