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
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.widget.element.*;

/**
 * {@link ResizeRegister} is a central place to register a {@link GerminateComposite} for {@link ResizeEvent}s. As a consequence of the register
 * process, the given {@link GerminateComposite} will get notified once the {@link jhi.germinate.client.management.ContentHolder} fires a {@link
 * ResizeEvent} by calling its {@link GerminateComposite#onResize(boolean)} method.
 *
 * @author Sebastian Raubach
 */
public class ResizeRegister
{
	private static final Set<GerminateComposite> toResize = new HashSet<>();

	/**
	 * Adds the given {@link GerminateComposite} to the {@link ResizeRegister} if it is not already present.
	 *
	 * @param comp Element to be added to this {@link Set}
	 * @return <code>true</code> if this set did not already contain the specified element
	 */
	public static boolean register(final GerminateComposite comp)
	{
		return toResize.add(comp);
	}

	/**
	 * Removes the given {@link ResizeHandler} from the {@link ResizeRegister} if it is present.
	 *
	 * @param comp object to be removed from this set, if present
	 * @return <code>true</code> if this set contained the specified element
	 */
	public static boolean deregister(GerminateComposite comp)
	{
		return toResize.remove(comp);
	}

	/**
	 * Triggers a {@link ResizeEvent} no all contained {@link ResizeHandler}s by calling their {@link GerminateComposite#onResize(boolean)}
	 * method.
	 */
	public static void triggerResize()
	{
		Iterator<GerminateComposite> it = toResize.iterator();

		while (it.hasNext())
		{
			GerminateComposite comp = it.next();
			Widget c = comp.getPanel();

			if (c == null || !c.isAttached() || c.getParent() == null)
				it.remove();
			else
			{
				int tempWidth = c.getOffsetWidth();

				final boolean containerResize;
				if (comp.getWidth() != tempWidth)
				{
					comp.setWidth(tempWidth);
					containerResize = true;
				}
				else
				{
					containerResize = false;
				}

				// Wait for the browser loop to return before issuing the resize to the child
				Scheduler.get().scheduleDeferred(() -> comp.onResize(containerResize));
			}
		}
	}
}
