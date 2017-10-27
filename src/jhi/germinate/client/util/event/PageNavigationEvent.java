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

import jhi.germinate.client.util.event.PageNavigationEvent.*;
import jhi.germinate.shared.datastructure.*;

/**
 * A {@link PageNavigationEvent} indicates that the user navigated to a new page.
 *
 * @author Sebastian Raubach
 */
public class PageNavigationEvent extends GwtEvent<PageNavigationEventHandler>
{
	/**
	 * {@link jhi.germinate.client.util.event.LogoutEvent.LogoutEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface PageNavigationEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onPageNavigation(PageNavigationEvent event);
	}

	public static final Type<PageNavigationEventHandler> TYPE = new Type<>();

	private Page page;

	private Throwable ex;

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public PageNavigationEvent(Page page)
	{
		this.page = page;
	}

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 *
	 * @param ex The {@link Throwable} that caused the {@link LogoutEvent}
	 */
	public PageNavigationEvent(Throwable ex)
	{
		this.ex = ex;
	}

	public Page getPage()
	{
		return page;
	}

	/**
	 * Returns the {@link Throwable} that caused the {@link LogoutEvent}
	 *
	 * @return The {@link Throwable} that caused the {@link LogoutEvent}
	 */
	public Throwable getThrowable()
	{
		return ex;
	}

	@Override
	public Type<PageNavigationEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(PageNavigationEventHandler handler)
	{
		handler.onPageNavigation(this);
	}
}
