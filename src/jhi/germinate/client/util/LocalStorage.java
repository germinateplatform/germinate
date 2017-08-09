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

import com.google.gwt.storage.client.*;

/**
 * @author Sebastian Raubach
 */
public class LocalStorage
{
	private static final Storage localStorage = Storage.getLocalStorageIfSupported();

	private static String prefix = "";

	/**
	 * Sets a new cookie
	 *
	 * @param name  The name of the cookie
	 * @param value The value of the cookie
	 */
	public static void set(String name, String value)
	{
		set(name, value, true);
	}

	/**
	 * Sets a new cookie
	 *
	 * @param name    The name of the cookie
	 * @param value   The value of the cookie
	 * @param expires Set to <code>true</code> if the cookie should expire
	 */
	public static void set(String name, String value, boolean expires)
	{
		if (localStorage != null)
		{
			localStorage.setItem(prefix + name, value);
		}
		else
		{
			Cookie.setCookie(name, value, expires);
		}
	}

	/**
	 * Returns the cookie with the given name
	 *
	 * @param name The name/key
	 * @return The cookie value
	 */
	public static String get(String name)
	{
		if (localStorage != null)
			return localStorage.getItem(prefix + name);
		else
			return Cookie.getCookie(name);
	}

	/**
	 * Removes the cookie with the given name
	 *
	 * @param name The name of the cookie to remove
	 */
	public static void remove(String name)
	{
		if (localStorage != null)
			localStorage.removeItem(prefix + name);
		else
			Cookie.removeCookie(name);
	}

	public static void setPrefix(String prefix)
	{
		if (prefix == null)
			prefix = "";
		else
		{
			if (!prefix.endsWith("."))
				prefix += ".";
			if (prefix.startsWith("/"))
				prefix = prefix.substring(1);
		}

		LocalStorage.prefix = prefix;
	}
}
