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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.Notification.*;
import jhi.germinate.shared.*;

/**
 * {@link GerminateComposite} is a {@link Composite} that allows its children to specify which {@link Library}s should be loaded before the content is
 * displayed. Once that is done, {@link #setUpContent()} is called.
 *
 * @author Sebastian Raubach
 */
public abstract class GerminateComposite extends Composite
{
	protected final FlowPanel panel;
	private         int       width = 0;

	/**
	 * Creates a new instance of {@link GerminateComposite}. Child classes mustn't call {@link #initWidget(Widget)}!
	 */
	public GerminateComposite()
	{
		panel = new FlowPanel();
		initWidget(panel);
		ResizeRegister.register(this);
	}

	/**
	 * Inserts a widget before the specified index
	 *
	 * @param widget      The widget to be inserted
	 * @param beforeIndex The index before which it will be inserted
	 */
	public void insert(Widget widget, int beforeIndex)
	{
		panel.insert(widget, beforeIndex);
	}

	public void onResize(boolean containerResize)
	{
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	/**
	 * We don't allow child classes to override this method.
	 */
	@Override
	protected final void initWidget(Widget widget)
	{
		if (!panel.equals(widget))
			throw new IllegalStateException("You aren't supposed to call initWidget(Widget)...");
		else
			super.initWidget(widget);
	}

	/**
	 * Asks for a list of {@link Library}s to load before laying out the content. Returning <code>null</code> indicates that no libraries should be
	 * loaded.
	 * <p/>
	 * <b>IMPORTANT</b>: The get of items matters, since they are loaded in this get. Loading {@link Library#D3_TOOLTIP} before {@link Library#D3_V3}
	 * will consequently fail, since the tooltip depends on d3 in general.
	 *
	 * @return The list of {@link Library}s to load
	 */
	public abstract Library[] getLibraries();

	@Override
	public void onLoad()
	{
		Library[] librariesToLoad = getLibraries();

		if (!ArrayUtils.isEmpty(librariesToLoad))
		{
			for (Library lib : librariesToLoad)
				Library.Queue.push(lib);
		}

		loadLibraries();
		onPostLoad();

		width = panel.getOffsetWidth();
	}

	@Override
	protected void onUnload()
	{
		super.onUnload();

		ResizeRegister.deregister(this);
	}

	protected void onPostLoad()
	{

	}

	private void loadLibraries()
	{
		if (Library.Queue.isEmpty())
		{
			setUpContent();
		}
		else
		{
			Library.Queue.callCallbacks(new Callback<Void, Exception>()
			{
				@Override
				public void onFailure(Exception reason)
				{
					Notification.notify(Type.ERROR, reason.getLocalizedMessage());

					if (GerminateSettingsHolder.get().loadPageOnLibraryError.getValue())
						setUpContent();
				}

				@Override
				public void onSuccess(Void result)
				{
					/* Once they all successfully loaded, set up the child content */
					setUpContent();
				}
			});
		}
	}

	/**
	 * Called when all the libraries are successfully loaded.
	 * <p/>
	 * Child classes should add their content to {@link #panel} here.
	 */
	protected abstract void setUpContent();

	public FlowPanel getPanel()
	{
		return panel;
	}
}
