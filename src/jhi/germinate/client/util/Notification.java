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

package jhi.germinate.client.util;

import org.gwtbootstrap3.client.ui.constants.*;
import org.gwtbootstrap3.extras.notify.client.constants.*;
import org.gwtbootstrap3.extras.notify.client.ui.*;

import java.util.*;

import jhi.germinate.shared.datastructure.Tuple.*;

/**
 * {@link Notification} is a wrapper for <a href="http://toastrjs.com/">Toastr - Simple javascript toast notifications</a>.
 * <p/>
 * Notification will strip all html tags from the content before showing them to prevent execution of, e.g., javascript.
 *
 * @author Sebastian Raubach
 */
public class Notification
{
	private static final long IGNORE_LAST_MESSAGE_IF_LESS_THAN = 1000;

	private static final Map<Type, Pair<String, Long>> lastMessages = new HashMap<>();

	/**
	 * The available types of {@link Notification}s.
	 *
	 * @author Sebastian Raubach
	 */
	public enum Type
	{
		WARNING,
		INFO,
		ERROR,
		SUCCESS
	}

	/**
	 * Shows a notification of the given {@link Type} with the given message
	 *
	 * @param message The message to display
	 */
	public static void notify(Type type, boolean message)
	{
		notify(type, Boolean.toString(message));
	}

	/**
	 * Shows a notification of the given {@link Type} with the given message
	 *
	 * @param message The message to display
	 */
	public static void notify(Type type, double message)
	{
		notify(type, Double.toString(message));
	}

	/**
	 * Shows a notification of the given {@link Type} with the given message
	 *
	 * @param message The message to display
	 */
	public static void notify(Type type, float message)
	{
		notify(type, Float.toString(message));
	}

	/**
	 * Shows a notification of the given {@link Type} with the given message
	 *
	 * @param message The message to display
	 */
	public static void notify(Type type, int message)
	{
		notify(type, Integer.toString(message));
	}

	/**
	 * Shows a notification of the given {@link Type} with the given message
	 *
	 * @param type    The {@link Type} of notification
	 * @param message The message to display
	 */
	public static void notify(Type type, Object message)
	{
		if (message == null)
			notify(type, "null");
		else
		{
			if (message instanceof Exception)
				notify(type, ((Exception) message).getLocalizedMessage());
			else
				notify(type, message.toString());
		}
	}

	/**
	 * Shows a notification of the given {@link Type} with the given message
	 *
	 * @param type    The {@link Type} of notification
	 * @param message The message
	 */
	public static void notify(Type type, String message)
	{
		/* Strip potentially malicious content */
		message = HTMLUtils.stripHtmlTags(message);

        /*
		 * Check if the same notification has already been issued within a
         * certain amount of time. If so, ignore this one, if not, show it.
         */
		long now = System.currentTimeMillis();

		Pair<String, Long> lastMessage = lastMessages.get(type);

		if (lastMessage != null)
		{
			/* If new equals old and the interval requirement is met, return */
			if (now - lastMessage.getSecond() < IGNORE_LAST_MESSAGE_IF_LESS_THAN && message.equals(lastMessage.getFirst()))
			{
				return;
			}
		}

        /* Save the new last message */
		lastMessages.put(type, new Pair<>(message, now));

		NotifySettings settings = NotifySettings.newSettings();
//		settings.setShowProgressbar(true);
		settings.setPauseOnMouseOver(true);
		settings.setOffset(0, 60);
		settings.setAllowDismiss(false);
		settings.setZIndex(9999);

		switch (type)
		{
			case WARNING:
				settings.setType(NotifyType.WARNING);
				Notify.notify(null, message, IconType.WARNING, settings);
				break;
			case INFO:
				settings.setType(NotifyType.INFO);
				Notify.notify(null, message, IconType.INFO, settings);
				break;
			case ERROR:
				settings.setType(NotifyType.DANGER);
				Notify.notify(null, message, IconType.BAN, settings);
				break;
			case SUCCESS:
				settings.setType(NotifyType.SUCCESS);
				Notify.notify(null, message, IconType.CHECK, settings);
				break;
		}
	}
}
