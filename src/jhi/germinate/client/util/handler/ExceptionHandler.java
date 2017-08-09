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

package jhi.germinate.client.util.handler;

import com.google.gwt.user.client.rpc.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.InvalidSessionException.*;

/**
 * {@link ExceptionHandler} is the centralized instance that handles exceptions.
 *
 * @author Sebastian Raubach
 */
public class ExceptionHandler
{
	private static boolean initialized = false;

	public static void init()
	{
		if (!initialized)
		{
			GerminateEventBus.BUS.addHandler(ExceptionEvent.TYPE, event -> handleException(event.getThrowable()));

			initialized = true;
		}
	}

	public static void handleException(Throwable caught)
	{
		if (caught instanceof LoginRegistrationException)
		{
			switch (((LoginRegistrationException) caught).getReason())
			{
				case USER_SUSPENDED:
					/* Show notification and set logged-in status to false -> login page will be shown */
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInvalidSession());
					GerminateEventBus.BUS.fireEvent(new LogoutEvent(caught));
					break;
				case USERNAME_PASSWORD_WRONG:
					/* Show notification and set logged-in status to false -> login page will be shown */
					Notification.notify(Notification.Type.ERROR, Text.LANG.notificationLoginUnsuccessful());
					GerminateEventBus.BUS.fireEvent(new LogoutEvent(caught));
			}
		}
		else if (caught instanceof InvalidSessionException)
		{
			/*
			 * Show notification and set logged-in status to false -> login page
             * will be shown
             */
			String notificationMessage;

			InvalidProperty property = ((InvalidSessionException) caught).getInvalidProperty();

			switch (property)
			{
				case INVALID_COOKIE:
					notificationMessage = Text.LANG.notificationInvalidCookie();
					break;
				case INVALID_PAYLOAD:
					notificationMessage = Text.LANG.notificationInvalidPayload();
					break;
				case INVALID_SESSION:
				default:
					notificationMessage = Text.LANG.notificationInvalidSession();
			}

			Notification.notify(Notification.Type.ERROR, notificationMessage);
			GerminateEventBus.BUS.fireEvent(new LogoutEvent(caught));
		}
		else if (caught instanceof InsufficientPermissionsException)
		{
			/*
			 * Show notification and set logged-in status to false -> login page
             * will be shown
             */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInsufficientPermissions());
			GerminateEventBus.BUS.fireEvent(new LogoutEvent(caught));
		}
		else if (caught instanceof SystemUnderMaintenanceException)
		{
			/* Do nothing here. It's handled somewhere else */
			MaintenanceWidget.show();
		}
		else if (caught instanceof InvalidDatabaseTypeException)
		{
			/* InvalidDatabaseTypeException don't require a logout */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationDatabaseError(InvalidDatabaseTypeException.class.getName()));
		}
		else if (caught instanceof DatabaseException)
		{
			/* Just show a notification, no need to log the user out */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationDatabaseError(""));
		}
		else if (caught instanceof IOException)
		{
			/* IOExceptions don't require a logout */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationIOError());
		}
		else if (caught instanceof MissingPropertyException)
		{
			/* MissingPropertyException don't require a logout */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationUnspecifiedServerError());
		}
		else if (caught instanceof InvalidPageException)
		{
			/* We redirect the user to the login page */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationPageUnavailable());
//			ContentHolder.getInstance().setContent(Page.LOGIN, null, new HomeText());
		}
		else if (caught instanceof InvalidOptionsException)
		{
			Notification.notify(Notification.Type.ERROR, caught.getLocalizedMessage());
		}
		else if (caught instanceof SystemInReadOnlyModeException)
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationSystemReadOnly());
		}
		else if (caught instanceof FlapjackException)
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationFlapjackException());
		}
		else if (caught instanceof StatusCodeException)
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationUnspecifiedServerError());
		}
		else
		{
			/*
			 * Something went really wrong... Just show a notification (we don't
             * really know what happened, so logging the user out might be too
             * much)
             */
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationUnknownError(caught.getClass().getName()));
		}
	}
}
