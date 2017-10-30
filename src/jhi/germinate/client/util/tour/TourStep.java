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

package jhi.germinate.client.util.tour;

import com.google.gwt.core.client.*;
import com.google.gwt.query.client.*;

/**
 * @author Sebastian Raubach
 */
public class TourStep extends JavaScriptObject
{
	public enum Position
	{
		TOP("top"),
		RIGHT("right"),
		BOTTOM("bottom"),
		LEFT("left"),
		AUTO("auto");

		private String position;

		Position(String position)
		{
			this.position = position;
		}
	}

	protected TourStep()
	{
	}

	public static TourStep newInstance(String element, String intro, Position position)
	{
		if (!GQuery.$(element).isEmpty())
			return createJson(element, intro, position.position).cast();
		else
			return createJson(intro, position.position).cast();
	}

	public static TourStep newInstance(String intro)
	{
		return newInstance(null, intro);
	}

	public static TourStep newInstance(String element, String intro)
	{
		if (element == null || !GQuery.$(element).isEmpty())
			return newInstance(element, intro, Position.AUTO);
		else
			return null;
	}

	private static native TourStep createJson(String element, String intro, String position)/*-{
		return {element: element, intro: intro, position: position};
	}-*/;

	private static native TourStep createJson(String intro, String position)/*-{
		return {intro: intro, position: position};
	}-*/;
}