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

package jhi.germinate.server.util;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class LocaleUtils
{
	public static Locale parseLocale(String locale)
	{
		String[] parts = locale.split("_");
		switch (parts.length)
		{
			case 3:
				return new Locale(parts[0], parts[1], parts[2]);
			case 2:
				return new Locale(parts[0], parts[1]);
			case 1:
				return new Locale(parts[0]);
			default:
				throw new IllegalArgumentException("Invalid locale: " + locale);
		}
	}

	public static boolean isValid(Locale locale)
	{
		try
		{
			return locale.getISO3Language() != null && locale.getISO3Country() != null;
		}
		catch (MissingResourceException e)
		{
			return false;
		}
	}
}
