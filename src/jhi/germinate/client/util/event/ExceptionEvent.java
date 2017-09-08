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

package jhi.germinate.client.util.event;

import com.google.gwt.event.shared.*;

import jhi.germinate.client.util.event.ExceptionEvent.*;

/**
 * A {@link ExceptionEvent} indicates that an {@link Exception} has been received from the server
 *
 * @author Sebastian Raubach
 */
public class ExceptionEvent extends GwtEvent<ExceptionEventHandler>
{
	/**
	 * {@link ExceptionEventHandler} is the {@link EventHandler} of {@link ExceptionEvent}
	 */
	public interface ExceptionEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link ExceptionEvent} has been fired
		 *
		 * @param event The {@link ExceptionEvent}
		 */
		void onException(ExceptionEvent event);
	}

	public static final Type<ExceptionEventHandler> TYPE = new Type<>();

	private Throwable ex;

	/**
	 * Creates a new instance of {@link ExceptionEvent}
	 *
	 * @param ex The {@link Throwable} that caused the {@link ExceptionEvent}
	 */
	public ExceptionEvent(Throwable ex)
	{
		this.ex = ex;
	}

	/**
	 * Returns the {@link Throwable} that caused the {@link ExceptionEvent}
	 *
	 * @return The {@link Throwable} that caused the {@link ExceptionEvent}
	 */
	public Throwable getThrowable()
	{
		return ex;
	}

	@Override
	public Type<ExceptionEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(ExceptionEventHandler handler)
	{
		handler.onException(this);
	}
}
