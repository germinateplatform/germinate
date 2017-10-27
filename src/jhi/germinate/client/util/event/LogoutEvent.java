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

import jhi.germinate.client.util.event.LogoutEvent.*;

/**
 * A {@link LogoutEvent} indicates that the user should be logged out.
 *
 * @author Sebastian Raubach
 */
public class LogoutEvent extends GwtEvent<LogoutEventHandler>
{
	/**
	 * {@link LogoutEventHandler} is the {@link EventHandler} of {@link LogoutEvent}
	 */
	public interface LogoutEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LogoutEvent} has been fired
		 *
		 * @param event The {@link LogoutEvent}
		 */
		void onLogout(LogoutEvent event);
	}

	public static final Type<LogoutEventHandler> TYPE = new Type<>();

	private Throwable ex;

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 */
	public LogoutEvent()
	{
	}

	/**
	 * Creates a new instance of {@link LogoutEvent}
	 *
	 * @param ex The {@link Throwable} that caused the {@link LogoutEvent}
	 */
	public LogoutEvent(Throwable ex)
	{
		this.ex = ex;
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
	public Type<LogoutEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(LogoutEventHandler handler)
	{
		handler.onLogout(this);
	}
}
