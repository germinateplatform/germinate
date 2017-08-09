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

package jhi.germinate.client.util.tour;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class Tour extends JavaScriptObject
{
	protected Tour()
	{
	}

	public static Tour newInstance()
	{
		Tour tour = createJso().cast();

		tour.onAfterChange(targetElement ->
		{
			if (StringUtils.areEqual(targetElement.getClassName(), "introjsFloatingElement"))
				Window.scrollTo(0, 0);
		});

		return tour;
	}

	private static native Tour createJso()/*-{
		var inst = @jhi.germinate.client.i18n.Text::LANG;
		var next = inst.@jhi.germinate.client.i18n.Text::generalNext()();
		var back = inst.@jhi.germinate.client.i18n.Text::generalBack()();
		var skip = inst.@jhi.germinate.client.i18n.Text::generalSkip()();
		var done = inst.@jhi.germinate.client.i18n.Text::generalDone()();

		var tour = $wnd.introJs();
		tour.setOptions({
			nextLabel: next,
			backLabel: back,
			skipLabel: skip,
			doneLabel: done,
			exitOnEsc: true,
			exitOnOverlayClick: true,
			showStepNumbers: false,
			showProgress: true,
			scrollToElement: true
		});

		return tour;
	}-*/;

	public native final void addStep(TourStep step)/*-{
		this.addStep(step);
	}-*/;

	public native final void onAfterChange(ChangeHandler h)/*-{
		this.onafterchange(function (targetElement) {
			h.@jhi.germinate.client.util.tour.Tour.ChangeHandler::onAfterChange(*)(targetElement);
		});
	}-*/;

	public final void start()
	{
		Window.scrollTo(0, 0);
		startJs();
	}

	private native void startJs()/*-{
		this.start();
	}-*/;

	public interface ChangeHandler
	{
		void onAfterChange(com.google.gwt.dom.client.Element targetElement);
	}
}
