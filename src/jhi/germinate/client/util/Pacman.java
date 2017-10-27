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
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;

import jhi.germinate.shared.*;

public class Pacman
{
	private static final String URI_OPENED = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAeCAYAAAA7MK6iAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAN1wAADdcBQiibeAAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAKcSURBVEiJtddLaJxVFMDx35mOUQrajWZVQSsRpFi1ediH1IpaQaIgYrrSja6KLty3CxdiuikWtD4qgohdiCI+NtZFLVipTSaaaizFFtFYtUMLRagh0ZnjIkya2mRmkpn5L+893/0fvu9+95wbmiTHdZsxiPuEHtyClZjGtDQhfCccdo3PY62ZeutFQ+GYLap24n4UmszzPA4oGI71fl+SOL91k6r90gNNyhbiIvZY5cXoMd1QnCXbpTewqgXpfL5R9UQMmFxUnKN24JXFkmqBsrA1ep24Qpwjnhb2d0BaY1LV5hgwObdZsmSd8OpypH+co+cxVj/MyV/qht6o4KM8pFiAnNAlHcDVS5XC8Z84NcmZMke/bxje6zrPF8HfnhTWLkcKD97N8HNMTbN9W8PwKak78n0rrPEjbl2uuEkuSPt02Rt3KBetsbHD0rL0mqKX4y4XaoNFPNQh4c/Ya8abscnU/yeL0pY2/zxjwm6nfRhDKosFFYXVbRIeEXZb77MI2Si4iO4WZFV8LA1Hv2NLebBA4+zqELhKLP1jFVBuUTyIoznqqyx5pHlxOtOCeD6bpU+y5EiWDGbWfwsF4XCbxLOkTdKnSsZzxFN5SHEx8RdtFV/iduEd1zqRo57JCV3zJyNTQckPuK1DCdQ4K72uYk9s8FcwV4ff6rC4xjkMz9bjld5F44LWHq5HzO28LFknHbPMmtw0YcRpG+c6kOh1XNqhtQOlEb+qeDyGVC7rk6Pf23i2Q/Kz0rZap3lFgx599klDXKqdLRO+9q++6HeyNrTgzSD6faDgThxsUXlRekHaGhv8dnkuDchR92CX2SvMiiaF5/GeqpdiwJ8LBTRdVXLMDSoeVXCv1IObmTuNpjCBcXyJg9Hnn3rr/QdYF8YnDejUIQAAAABJRU5ErkJggg==";
	private static final String URI_CLOSED = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB8AAAAfCAYAAAAfrhY5AAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAA3XAAAN1wFCKJt4AAAAB3RJTUUH4AwODRQrdXWWVgAAAiVJREFUSMfN179rllcYxvHPfVLSkkEnM6VgW1IwKUWaRAvaNGKrUIogRbPo1C3o5tgODmIG7aSCBLqUZij9A/wxtKFQKprUJEbxBwGN6Y+g4BIl0bzHIYmkGpI3r8n79BrPc57nez/XOZxzX6FM5UH1pn2JHUIj3kMdpjAlGxGuCn3ecj6aTS/3zVgWOqBdyTfYiVRmrQ/RK+mOj/y1Ynj+00YlPbLPVK5JfGe9Y9Foqix47tcpO4v1VkeXlOyLLcaWhOcrunCqnCVZoSaEjmhxY1F4vuxroWcNwPMaU7Jt3oG0wOoPhdOVgP9+QONeGr7g5t0lp74tOZd/8cYLeB5RK+vFm5X8ztBt7owxPsEfw8tOb7LOEcxW4LGDQnOlXn6+le7DPJmic1cZL5R0oDvyT2q86zreVz3dMqop8hXb8Zvq65OE3YrR7iRrLwSdtSehoRB4aEioL8j2+oRcEDwnTBQEn0iy8YI23HgS+gracH1JuFgQ/GLkLOl3DZuqiL6hxQcpQkl2ssrrPRyhNHuf1/kBw1W0fODFfR7NpoUDc23wWoMvG3XiP51MtBiSda3xoXPPjK9ivxkv9+HR5nscWqMC/pXtWtjBvhICotUZ2X48WkWrf/dMa7S5uXB40QQSbX6WbMaF18ROyo7KOuJj91cel2Y7nW/n4lLNCuLSj0qOxxb/VJzVFmS2DWbskXwqa8Q7qJ17/AQjGMSvuBCtnvo/6zlTaplYSsiJswAAAABJRU5ErkJggg==";

	private static final int DIV_WIDTH  = 31;
	private static final int DIV_HEIGHT = 31;

	private static final String STYLE        = ".pacman-point-counter { padding: 10px; z-index: 99999999; position: absolute; height: 20px; width: 100%; bottom: 0px; left: 0px; background-color: black; opacity: 0.75; color: white; font-size: 20px; text-align: center; } .pacman { z-index: 99999999; position: absolute; background: url(" + URI_OPENED + ") no-repeat left top; width: " + DIV_WIDTH + "px; height: " + DIV_HEIGHT + "px; }";
	private static final String STYLE_CLOSED = ".pacman-closed { background: url(" + URI_CLOSED + ") no-repeat left top !important; }";

	private static boolean cssInjected  = false;
	private static boolean alreadyAdded = false;

	private int height;
	private int width;
	private int x;
	private int y;
	private int mouseX;
	private int mouseY;

	private Element pacman;
	private Element pointCounter;

	private int points = 0;

	public Pacman()
	{
		if (alreadyAdded)
			return;

		// Initially scroll to the top left
		Window.scrollTo(0, 0);

		// Then disable page scrolling
		StyleInjector.injectAtEnd("body { overflow: hidden; }", true);

		alreadyAdded = true;

		if (!cssInjected)
		{
			StyleInjector.injectAtStart(STYLE, true);
			StyleInjector.injectAtStart(STYLE_CLOSED, true);
			cssInjected = true;
		}

		// Create the new element
		pacman = Document.get().createDivElement();
		pacman.setClassName("pacman");

		pointCounter = Document.get().createDivElement();
		pointCounter.setClassName("pacman-point-counter");

		// Style it
		final Style style = pacman.getStyle();
		Element body = Document.get().getElementsByTagName("body").getItem(0);
		body.appendChild(pacman);
		body.appendChild(pointCounter);

		// Get bounds
		height = Window.getClientHeight();
		width = Window.getClientWidth();

		// Generate random start position
		x = RandomUtils.RANDOM.nextInt(width);
		y = RandomUtils.RANDOM.nextInt(height);

		// Initially set the mouse position to the start position
		mouseX = x;
		mouseY = y;

		// Position it there
		style.setTop(y - (DIV_HEIGHT / 2), Style.Unit.PX);
		style.setLeft(x - (DIV_WIDTH / 2), Style.Unit.PX);

		// Listen for mouse events to update the mouse position
		Event.addNativePreviewHandler(event ->
		{
			final int eventType = event.getTypeInt();
			switch (eventType)
			{
				case Event.ONMOUSEMOVE:
					mouseX = event.getNativeEvent().getClientX();
					mouseY = event.getNativeEvent().getClientY();
					break;
				default:
			}
		});

		new Timer()
		{
			private int countdown = 0;

			@Override
			public void run()
			{
				// Calculate the new position
				int deltaX = mouseX - x;
				int deltaY = mouseY - y;

				if (Math.abs(deltaX) > Math.abs(deltaY) * 10)
					deltaY = 0;
				if (Math.abs(deltaY) > Math.abs(deltaX) * 10)
					deltaX = 0;

				int signumX = (int) Math.signum(deltaX);
				int signumY = (int) Math.signum(deltaY);

				x += signumX;
				y += signumY;

				// Restrict everything to be on screen
				x = Math.min(width, Math.max(0, x));
				y = Math.min(height, Math.max(0, y));

				style.setTop(y - (DIV_HEIGHT / 2), Style.Unit.PX);
				style.setLeft(x - (DIV_WIDTH / 2), Style.Unit.PX);

				style.setDisplay(Style.Display.NONE);
				Element element = JavaScript.elementFromPoint(x, y);
				style.setDisplay(Style.Display.INITIAL);

				Rotation r = Rotation.getFromCoordinates(signumX, signumY);
				r.applyTo(pacman);

				// Close pacman's mouth if he just ate an element
				if (element != null && !element.equals(pacman) && !element.equals(pointCounter) && !JavaScript.hasChildElement(element))
				{
					countdown = 15;
					pacman.addClassName("pacman-closed");
					element.removeFromParent();
					pointCounter.setInnerText("Points: " + points++);
				}
				// Then open it again after a while
				else if (countdown == 0)
				{
					pacman.removeClassName("pacman-closed");
				}

				countdown--;

				this.schedule(10);
			}
		}.schedule(10);
	}

	public interface Css extends Messages
	{
		Css INST = GWT.create(Css.class);

		@DefaultMessage(".rotate{0} '{' -webkit-transform: rotate({0}deg); -moz-transform: rotate({0}deg); -o-transform: rotate({0}deg); -ms-transform: rotate({0}deg); transform: rotate({0}deg); '}'")
		String style(int value);

		@DefaultMessage("rotate{0}")
		String className(int value);
	}

	private enum Rotation
	{
		TOP(270),
		TOP_RIGHT(315),
		RIGHT(0, true),
		BOTTOM_RIGHT(45),
		BOTTOM(90),
		BOTTOM_LEFT(135),
		LEFT(180),
		TOP_LEFT(225);

		private int    degree;
		private String style;
		private boolean injected = false;

		Rotation(int degree)
		{
			this.degree = degree;
		}

		Rotation(int degree, boolean injected)
		{
			this.degree = degree;
			this.injected = injected;
		}

		public void applyTo(Element element)
		{
			if (!injected)
			{
				style = Css.INST.style(degree);
				JavaScript.consoleLog(style);
				StyleInjector.injectAtStart(style, true);
				injected = true;
			}

			for (Rotation r : values())
			{
				if (r == this)
					element.addClassName(Css.INST.className(degree));
				else
					element.removeClassName(Css.INST.className(r.degree));
			}
		}

		public static Rotation getFromCoordinates(int xDelta, int yDelta)
		{
			if (xDelta == 0 && yDelta == 0)
				return RIGHT;
			else if (xDelta == 0)
			{
				if (yDelta > 0)
					return BOTTOM;
				else
					return TOP;
			}
			else if (yDelta == 0)
			{
				if (xDelta > 0)
					return RIGHT;
				else
					return LEFT;
			}
			else if (xDelta > 0 && yDelta > 0)
				return BOTTOM_RIGHT;
			else if (xDelta > 0 && yDelta < 0)
				return TOP_RIGHT;
			else if (xDelta < 0 && yDelta > 0)
				return BOTTOM_LEFT;
			else if (xDelta < 0 && yDelta < 0)
				return TOP_LEFT;

			return RIGHT;
		}
	}
}