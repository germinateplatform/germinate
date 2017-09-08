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

package jhi.germinate.client;

import com.google.gwt.core.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.logging.*;

import jhi.germinate.client.management.*;
import jhi.germinate.client.page.login.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.handler.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link ModuleCore} is the {@link EntryPoint} of Germinate3. All the page navigation is handled here.
 *
 * @author Sebastian Raubach
 */
public class ModuleCore implements EntryPoint
{
	private static boolean  isLoggedIn        = false;
	private static boolean  useAuthentication = true;
	private static UserAuth userAuth          = null;

	private final Logger logger = Logger.getLogger("Germinate");

	@Override
	public final void onModuleLoad()
	{
		/* First, take care of any exceptions that aren't explicitly caught */
		if (GWT.isProdMode() && GWT.isClient())
		{
			GWT.setUncaughtExceptionHandler(e ->
			{
				if (GerminateSettingsHolder.get() != null && GerminateSettingsHolder.get().debug.getValue())
				{
					unwrap(e).printStackTrace();
					logger.log(Level.SEVERE, "Exception caught!", e);
				}
			});
		}

        /* Second, move the other code to a separate method, since the
		 * UncaughtExceptionHandler only works if onModuleLoad() returns
         * successfully. By doing this, we ensure that all exceptions are caught */
		Scheduler.get().scheduleDeferred(this::onModuleLoadForReal);
	}

	/**
	 * If the given {@link Throwable} is an {@link UmbrellaException}, this method will "unwrap" it, i.e., it will return the actual {@link
	 * Throwable}
	 *
	 * @param e The given {@link Throwable}
	 * @return The unwrapped {@link Throwable}
	 */
	private Throwable unwrap(Throwable e)
	{
		if (e instanceof UmbrellaException)
		{
			UmbrellaException ue = (UmbrellaException) e;
			if (ue.getCauses().size() == 1)
			{
				return unwrap(ue.getCauses().iterator().next());
			}
		}
		return e;
	}

	/**
	 * This method initializes the notification system and exception handling.
	 */
	protected void onModuleLoadForReal()
	{
		/* Set up the exception handling system */
		ExceptionHandler.init();

		initContent();
	}

	private void initContent()
	{
		// Take care of page navigation changes
		History.addValueChangeHandler(new HistoryChangeHandler());

		CommonService.Inst.get().getSettings(new AsyncCallback<GerminateSettings>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				if (SystemUnderMaintenanceException.isInstance(caught))
				{
					ContentHolder.getInstance().initContent();
					MaintenanceWidget.show();
				}
				else
				{
					Notification.notify(Notification.Type.ERROR, "Failed to receive settings from the server."); // TODO: i18n
				}
			}

			@Override
			public void onSuccess(GerminateSettings settings)
			{
				GerminateSettingsHolder.set(settings);
				FooterWidget.init();

				if (settings.isReadOnlyMode.getValue())
					ReadOnlyBanner.show();

				Cookie.loadCookieNotification();

				attemptSessionLogin();
			}
		});
	}

	public static void onPageNavigation(PageNavigationEvent event)
	{
		NavigationHandler.onPageNavigation(event);
	}

	protected void attemptSessionLogin()
	{
		Window.addResizeHandler(new ResizeHandler()
		{
			private static final int DELAY = 250;
			Timer timer = new Timer()
			{
				@Override
				public void run()
				{
					ResizeRegister.triggerResize();
				}
			};

			@Override
			public void onResize(ResizeEvent event)
			{
				if (timer.isRunning())
					timer.cancel();

				timer.schedule(DELAY);
			}
		});

		/* Listen for PageNavigationEvents */
		GerminateEventBus.BUS.addHandler(PageNavigationEvent.TYPE, new PageNavigationHandler());

        /* Listen for LogoutEvents */
		GerminateEventBus.BUS.addHandler(LogoutEvent.TYPE, new LogoutHandler());

        /* Listen for LoginEvents */
		GerminateEventBus.BUS.addHandler(LoginEvent.TYPE, new LoginHandler());

        /* This is the first login attempt when the page is loaded for the first
		 * time or when refresh is pressed. Set up the callback object for first
         * authentication. Try to log in without user credentials. This will
         * succeed if the session is valid and fail if it's not */
		UserService.Inst.get().login(Cookie.getRequestProperties(), new UserCredentials("", ""), new AsyncCallback<UserAuth>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				if (caught instanceof LoginRegistrationException)
				{
					LoginRegistrationException e = (LoginRegistrationException) caught;

					switch (e.getReason())
					{
						case LOGIN_UNAVAILABLE:
							/* Authentication is not necessary, just show the page */
							isLoggedIn = false;
							useAuthentication = false;

                    		/* Remove all cookies, because there might still be cookies from the time the page was not public and then the user might see things that s/he shouldn't */
							Cookie.removeAll();

							UserAuth auth = e.getUserAuth();
							Cookie.setUp(auth);

							ContentHolder.getInstance().initContent();
							MarkedItemList.init();

                    		/* Read the URL parameters and save them in the parameter store */
							UrlParameterReader.readUrlParameters();

							updatePage();
							break;
						case USERNAME_PASSWORD_WRONG:
						case USER_SUSPENDED:
						case INSUFFICIENT_PERMISSIONS:
						default:
							/* User has to log in */
							useAuthentication = true;
							TypedParameterStore.clearAll();

//							ContentHolder.getInstance().initContent();

							try
							{
								Page page = Page.parse(History.getToken());
								if (page.isPublic())
									GerminateEventBus.BUS.fireEvent(new PageNavigationEvent(page));
								else
									ContentHolder.getInstance().setContent(Page.HOME, null, new LoginPage());
							}
							catch (InvalidPageException ex)
							{
								GerminateEventBus.BUS.fireEvent(new ExceptionEvent(ex));
							}
							break;
					}
				}
				else if (caught instanceof DatabaseException)
				{
					Notification.notify(Notification.Type.ERROR, caught.getLocalizedMessage());
				}
				else if (SystemUnderMaintenanceException.isInstance(caught))
				{
					GerminateEventBus.BUS.fireEvent(new LoginEvent(null, true));
				}
			}

			@Override
			public void onSuccess(UserAuth result)
			{
				GerminateEventBus.BUS.fireEvent(new LoginEvent(result, true));
			}
		});
	}

	/**
	 * Updates the page based on the login status and previous selections (history tokens)
	 */
	private void updatePage()
	{
		/* If there is no history */
		if (StringUtils.isEmpty(History.getToken()))
		{
			/* Check if the user is logged in */
			if (!isLoggedIn && useAuthentication)
			{
				/* Show login page */
				ContentHolder.getInstance().setContent(Page.HOME, null, new LoginPage());
//				History.newItem(Page.LOGIN.name());
			}
			else
			{
				/* Select home as default */
				History.newItem(Page.HOME.name());
			}
		}
		else
		{
			/* Otherwise reselect the current page (mostly caused by page
			 * refresh) */
			History.fireCurrentHistoryState();
		}
	}

	/**
	 * Returns <code>true</code> if the user is logged in or if authentication is disabled
	 *
	 * @return <code>true</code> if the user is logged in or if authentication is disabled
	 */
	public static boolean isLoggedIn()
	{
		return isLoggedIn;
	}

	/**
	 * Set the logging status of the user
	 *
	 * @param isLoggedIn <code>true</code> of the user is logged in, <code>false</code> if not
	 */
	public static void setLoggedIn(boolean isLoggedIn)
	{
		ModuleCore.isLoggedIn = isLoggedIn;
	}

	/**
	 * Returns <code>true</code> of the authentication process is enabled
	 *
	 * @return <code>true</code> of the authentication process is enabled
	 */
	public static boolean getUseAuthentication()
	{
		return useAuthentication;
	}

	public static void setUseAuthentication(boolean useAuthentication)
	{
		ModuleCore.useAuthentication = useAuthentication;
	}

	public static void setUserAuth(UserAuth userAuth)
	{
		ModuleCore.userAuth = userAuth;
	}

	public static UserAuth getUserAuth()
	{
		return userAuth;
	}
}
