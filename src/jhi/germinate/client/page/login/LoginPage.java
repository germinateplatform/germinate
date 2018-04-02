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

package jhi.germinate.client.page.login;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class LoginPage extends GerminateComposite implements HasHelp
{
	private LoginForm form;
	private Request   currentRequest;

	/**
	 * Creates a new instance of the login page
	 */
	public LoginPage()
	{
		TypedParameterStore.clearAll();
	}

	/**
	 * Attempts a login. Will "refresh" the page
	 */
	private void doLogin()
	{
		// Wait for the previous one to finish
		if(currentRequest != null && currentRequest.isPending())
			return;

		final String username = form.getUsername();
		String password = form.getPassword();

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
		{
			Notification.notify(Notification.Type.INFO, Text.LANG.notificationLoginFillFields());
			return;
		}

		/* Set up the callback object for login */
		currentRequest = UserService.Inst.get().login(Cookie.getRequestProperties(), new UserCredentials(username, password), new AsyncCallback<UserAuth>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				boolean highlightUsername = false;
				boolean highlightPassword = false;

				/* Handle the exception */
				if (caught instanceof LoginRegistrationException)
				{
					switch (((LoginRegistrationException) caught).getReason())
					{
						case USER_SUSPENDED:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationLoginSuspendedUser());
							highlightUsername = true;
							break;
						default:
						case USERNAME_PASSWORD_WRONG:
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationLoginInvalidUsernameOrPassword());
							highlightPassword = true;
							break;
					}
				}
				else if (caught instanceof InsufficientPermissionsException)
				{
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInsufficientPermissions());
					highlightUsername = true;
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, caught.getLocalizedMessage());
					highlightPassword = true;
				}

				if (highlightUsername)
					form.highlightUsername();
				if (highlightPassword)
					form.highlightPassword();

				currentRequest = null;
			}

			@Override
			public void onSuccess(UserAuth result)
			{
				GerminateEventBus.BUS.fireEvent(new LoginEvent(result, false));
				currentRequest = null;
			}
		});
	}

	@Override
	public void onResize(boolean containerResize)
	{
		jsniOnLoginShown(Window.getClientWidth(), Window.getClientHeight());
	}

	@Override
	protected void setUpContent()
	{
		form = new LoginForm(this, event -> doLogin());
		panel.getElement().appendChild(form.getElement());
		panel.addStyleName(Style.NO_POINTER_EVENTS);

		/* Set the focus to the username box */
		Scheduler.get().scheduleDeferred(() -> form.forceFocus());
		Scheduler.get().scheduleDeferred(() -> jsniOnLoginShown(Window.getClientWidth(), Window.getClientHeight()));
	}

	@Override
	public void onUnload()
	{
		form.clear();

		super.onUnload();

		Scheduler.get().scheduleDeferred(this::jsniOnLoginHidden);
	}

	public static native void jsniOnLoginShown(int width, int height)/*-{
		if ($wnd.jsniOnLoginShown)
			$wnd.jsniOnLoginShown(width, height);
	}-*/;

	private native void jsniOnLoginHidden()/*-{
		if ($wnd.jsniOnLoginHidden)
			$wnd.jsniOnLoginHidden();
	}-*/;

	@Override
	public Library[] getLibraryList()
	{
		return new Library[]{Library.D3_V3, Library.D3_TOPOJSON};
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.loginHelp(GerminateSettingsHolder.get().gatekeeperUrl.getValue()));
	}
}
