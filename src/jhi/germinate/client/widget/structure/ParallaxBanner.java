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

package jhi.germinate.client.widget.structure;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class ParallaxBanner
{
	interface ParallaxBannerUiBinder extends UiBinder<DivElement, ParallaxBanner>
	{
	}

	private static ParallaxBannerUiBinder ourUiBinder = GWT.create(ParallaxBannerUiBinder.class);

	private static boolean isInitialized = false;
	private static ParallaxBanner INSTANCE;

	@UiField
	DivElement wrapper;
	@UiField
	DivElement parallax;

	private String currentStyle = "";

	public ParallaxBanner()
	{
		ourUiBinder.createAndBindUi(this);
	}

	public static void updateStyle(String style)
	{
		if (GerminateSettingsHolder.get() != null && !GerminateSettingsHolder.get().templateShowParallaxBanner.getValue())
		{
			if (INSTANCE != null)
				INSTANCE.parallax.removeFromParent();

			INSTANCE = null;
		}

		if (INSTANCE == null)
			return;

		if (!StringUtils.isEmpty(INSTANCE.currentStyle))
			INSTANCE.parallax.removeClassName(INSTANCE.currentStyle);
		if (!StringUtils.isEmpty(style))
			INSTANCE.parallax.addClassName(style);
		INSTANCE.currentStyle = style;

		GQuery el = GQuery.$("#" + Id.STRUCTURE_PARALLAX);

		if (style == null)
			el.hide();
		else
			el.show();
	}

	public static void init()
	{
		if (!isInitialized)
		{
			isInitialized = true;

			INSTANCE = new ParallaxBanner();

			RootPanel p = RootPanel.get(Id.STRUCTURE_PARALLAX);
			p.getElement().appendChild(INSTANCE.parallax);
			p.removeFromParent();
		}
	}
}