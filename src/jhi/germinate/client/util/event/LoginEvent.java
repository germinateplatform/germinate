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

import jhi.germinate.client.util.event.LoginEvent.*;
import jhi.germinate.shared.datastructure.*;

/**
 * A {@link LogoutEvent} indicates that the user has been logged in.
 *
 * @author Sebastian Raubach
 */
public class LoginEvent extends GwtEvent<LoginEventHandler>
{
	/**
	 * {@link LoginEventHandler} is the {@link EventHandler} of {@link LoginEvent}
	 */
	public interface LoginEventHandler extends EventHandler
	{
		/**
		 * Called when a {@link LoginEvent} has been fired
		 *
		 * @param event The {@link LoginEvent}
		 */
		void onLogin(LoginEvent event);
	}

	public static final Type<LoginEventHandler> TYPE = new Type<>();

	private final UserAuth auth;
	private final boolean  isAutomaticLogin;

	/**
	 * Creates a new instance of {@link LoginEvent}
	 *
	 * @param auth             The {@link UserAuth} received from the server
	 * @param isAutomaticLogin Set to <code>true</code> if this {@link LoginEvent} is created by an automatic login event
	 */
	public LoginEvent(UserAuth auth, boolean isAutomaticLogin)
	{
		this.auth = auth;
		this.isAutomaticLogin = isAutomaticLogin;
	}

	/**
	 * Returns the {@link UserAuth} received from the server
	 *
	 * @return The {@link UserAuth} received from the server
	 */
	public UserAuth getUserAuthentication()
	{
		return auth;
	}

	/**
	 * Returns <code>true</code> of this {@link LoginEvent} has been created by an automatic login event
	 *
	 * @return <code>true</code> of this {@link LoginEvent} has been created by an automatic login event
	 */
	public boolean isAutomaticLogin()
	{
		return isAutomaticLogin;
	}

	@Override
	public Type<LoginEventHandler> getAssociatedType()
	{
		return TYPE;
	}

	@Override
	protected void dispatch(LoginEventHandler handler)
	{
		handler.onLogin(this);
	}
}
