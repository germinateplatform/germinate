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

package jhi.germinate.client.util.event;

import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.shared.datastructure.*;

/**
 * A {@link MainContentChangeEvent} indicates that the user navigated to a new page.
 *
 * @author Sebastian Raubach
 */
public class MainContentChangeEvent extends GwtEvent<MainContentChangeEvent.MainContentChangeEventHandler>
{
	/**
	 * {@link LogoutEvent.LogoutEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface MainContentChangeEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onMainContentChanged(MainContentChangeEvent event);
	}

	public static final Type<MainContentChangeEventHandler> TYPE = new Type<>();

	private Page      page;
	private Composite composite;

	/**
	 * Creates a new instance of {@link MainContentChangeEvent}
	 */
	public MainContentChangeEvent(Page page, Composite composite)
	{
		this.page = page;
		this.composite = composite;
	}

	public Page getPage()
	{
		return page;
	}

	public Composite getComposite()
	{
		return composite;
	}

	@Override
	public Type<MainContentChangeEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(MainContentChangeEventHandler handler)
	{
		handler.onMainContentChanged(this);
	}
}
