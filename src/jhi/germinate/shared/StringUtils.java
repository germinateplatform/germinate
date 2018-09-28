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

package jhi.germinate.shared;

import java.util.*;
import java.util.stream.*;

/**
 * {@link StringUtils} contains methods to manipulate/check {@link String}s.
 *
 * @author Sebastian Raubach
 */
public class StringUtils
{
	/**
	 * Checks if the given {@link String} is either <code>null</code> or empty after calling {@link String#trim()}.
	 *
	 * @param input The {@link String} to check
	 * @return <code>true</code> if the given {@link String} is <code>null</code> or empty after calling {@link String#trim()}.
	 */
	public static boolean isEmpty(String input)
	{
		return input == null || input.trim().isEmpty();
	}

	/**
	 * Checks if the given {@link String}s are either <code>null</code> or empty after calling {@link String#trim()}.
	 *
	 * @param input The {@link String}s to check
	 * @return <code>true</code> if any of the given {@link String}s is <code>null</code> or empty after calling {@link String#trim()}.
	 */
	public static boolean isEmpty(String... input)
	{
		if (input == null)
			return true;
		for (String text : input)
		{
			if (StringUtils.isEmpty(text))
				return true;
		}

		return false;
	}

	/**
	 * Joins the given parts to a String separated by the delimiter. {@link #isEmpty(String)} will be called on each part and the part will only be
	 * added if the result is <code>true</code>.
	 *
	 * @param delimiter The delimiter to use
	 * @param parts     The parts to join
	 * @return The joined parts or an empty {@link String} if either <code>parts.length == 0</code> or there is no part that returns
	 * <code>false</code> for {@link #isEmpty(String)}
	 */
	public static String join(String delimiter, String... parts)
	{
		if (parts.length == 0)
			return "";

		StringBuilder builder = new StringBuilder();

		boolean atLeastOne = false;
		for (String part : parts)
		{
			if (!isEmpty(part))
			{
				atLeastOne = true;
				builder.append(part).append(delimiter);
			}
		}

		/*
		 * Remove the last delimiter. We have to do it this way, because we
		 * don't know how many (of any) parts pass the check
		 */
		if (atLeastOne)
		{
			int startIndex = builder.lastIndexOf(delimiter);

			if (startIndex != -1)
				builder.delete(startIndex, startIndex + delimiter.length());
		}

		return builder.toString();
	}

	/**
	 * Checks if the given parts are pairwise distinct, i.e. calls {@link String#equals(Object)} on each adjacent pair.
	 *
	 * @param parts The parts to compare
	 * @return <code>true</code> if all parts are distinct, <code>false</code> otherwise
	 */
	public static boolean areDistinct(String... parts)
	{
		Set<String> distinctValues = new HashSet<>();

		for (String part : parts)
		{
			if (distinctValues.contains(part))
				return false;

			distinctValues.add(part);
		}

		return true;
	}

	/**
	 * Checks if the given parts are pairwise equal, i.e. calls {@link String#equals(Object)} on each adjacent pair.
	 *
	 * @param parts The parts to compare
	 * @return <code>true</code> if all parts are equal, <code>false</code> otherwise
	 */
	public static boolean areEqual(String... parts)
	{
		for (int i = 0; i < parts.length - 1; i++)
		{
			if (parts[i] == null)
				return false;

			if (!Objects.equals(parts[i], parts[i + 1]))
				return false;
		}

		return true;
	}

	/**
	 * Checks if the given {@link String}s are either <code>null</code> or empty after calling {@link String#trim()}.
	 *
	 * @param input The {@link String}s to check
	 * @return <code>true</code> if ALL of the given {@link String}s is <code>null</code> or empty after calling {@link String#trim()}.
	 */
	public static boolean areEmpty(String... input)
	{
		if (input == null)
			return true;
		for (String text : input)
		{
			if (!StringUtils.isEmpty(text))
				return false;
		}

		return true;
	}

	/**
	 * Checks if the given parts are pairwise equal, i.e. calls {@link String#equals(Object)} on each adjacent pair.
	 *
	 * @param parts The parts to compare
	 * @return <code>true</code> if all parts are equal, <code>false</code> otherwise
	 */
	public static boolean areEqualIgnoreCase(String... parts)
	{
		for (int i = 0; i < parts.length - 1; i++)
		{
			if (parts[i] == null)
				return false;

			if (!parts[i].equalsIgnoreCase(parts[i + 1]))
				return false;
		}

		return true;
	}

	public static int compareTo(String first, String second)
	{
		if (first == null && second == null)
			return 0;
		else if (first == null)
			return 1;
		else if (second == null)
			return -1;
		else
			return first.compareTo(second);
	}

	public static int length(String name)
	{
		if (StringUtils.isEmpty(name))
			return 0;
		else
			return name.length();
	}

	public static String toFileValue(String value)
	{
		return value == null ? "" : value;
	}

	public static String toFileValue(Long value)
	{
		return value == null ? "" : Long.toString(value);
	}

	//	@GwtIncompatible
	//	public static String dateToFileValue(Long value)
	//	{
	//		return value == null ? "" : Util.SDF_HUMAN_READABLE.format(new Date(value));
	//	}

	public static String toFileValue(Integer value)
	{
		return value == null ? "" : Integer.toString(value);
	}

	public static String trim(String input)
	{
		if (input == null)
			return null;
		else
			return input.trim();
	}

	public static void trim(String[] parts)
	{
		if (parts != null)
		{
			for (int i = 0; i < parts.length; i++)
				parts[i] = parts[i].trim();
		}
	}

	public static String getWordsUntil(String input, int limit)
	{
		if (input.length() < limit)
			return input;
		else
		{
			int index = input.indexOf(" ", limit - 3);
			if (index != -1)
				return input.substring(0, index) + " ...";
			else
				return input;
		}
	}

	public static String[] splitAndTrim(String input, String delimiter)
	{
		if (isEmpty(input, delimiter))
			return null;
		else
		{
			String[] split = input.split(delimiter);
			for (int i = 0; i < split.length; i++)
				split[i] = split[i].trim();

			return split;
		}
	}

	public static String toEmptyIfNull(String input)
	{
		if (isEmpty(input))
			return "";
		else
			return input;
	}

	/**
	 * Generates a SQL placeholder String of the form: "?,?,?,?" for the given size.
	 *
	 * @param size The number of placeholders to generate
	 * @return The generated String
	 */
	public static String generateSqlPlaceholderString(int size)
	{
		if (size < 1)
			return "";

		return IntStream.range(0, size)
						.mapToObj(value -> "?")
						.collect(Collectors.joining(","));
	}

	public static boolean isLink(String text)
	{
		if (isEmpty(text))
			return false;
		else
			return text.contains("http://") || text.contains("https://") || text.contains("ftp://");
	}
}
