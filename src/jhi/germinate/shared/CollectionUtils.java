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

import jhi.germinate.shared.exception.*;

/**
 * {@link CollectionUtils} contains methods to manipulate/check {@link List}s. All methods can be used both on the client and server side.
 *
 * @author Sebastian Raubach
 */
public class CollectionUtils
{
	/**
	 * Joins the given input {@link Collection} with the given delimiter into a String
	 *
	 * @param input     The input {@link List}
	 * @param delimiter The delimiter to use
	 * @return The joined String
	 */
	public static <T> String join(Collection<T> input, String delimiter)
	{
		if (input == null || input.size() < 1)
			return "";

		StringBuilder builder = new StringBuilder();

		Iterator<T> it = input.iterator();

		builder.append(it.next());

		while (it.hasNext())
		{
			builder.append(delimiter).append(it.next());
		}

		return builder.toString();
	}

	/**
	 * Checks if AT LEAST ONE of the given {@link Collection}s is either <code>null</code> or empty.
	 *
	 * @param input The {@link Collection}s to check
	 * @return <code>true</code> if either <code>input == null</code> or <code>input.size() < 1</code> FOR AT LEAST ONE OF THE COLLECTIONS
	 */
	@SafeVarargs
	public static <T> boolean isEmpty(Collection<T>... input)
	{
		boolean result = false;
		for (Collection<T> coll : input)
		{
			result |= coll == null || coll.size() < 1;

			if (result)
				break;
		}

		return result;
	}

	public static <T> boolean isEmptyOrNull(Collection<T> input)
	{
		if (input == null || input.size() < 1)
			return true;
		else
		{
			for (T i : input)
			{
				if (i != null)
					return false;
			}

			return true;
		}
	}

	/**
	 * Creates a {@link List} from the given input {@link String} by first splitting it on the given splitter
	 *
	 * @param input    The input {@link String}
	 * @param splitter The splitter
	 * @return The parsed {@link List}
	 */
	public static List<String> parseStringList(String input, String splitter)
	{
		if (StringUtils.isEmpty(input))
			return new ArrayList<>();

		String[] parts = input.split(splitter);

		for (int i = 0; i < parts.length; i++)
			parts[i] = parts[i].trim();

		return new ArrayList<>(Arrays.asList(parts));
	}

	/**
	 * Creates a {@link List} from the given input {@link String} by first splitting it on the given splitter
	 *
	 * @param input    The input {@link String}
	 * @param splitter The splitter
	 * @return The parsed {@link List}
	 */
	public static List<Long> parseLongList(String input, String splitter) throws UnsupportedDataTypeException
	{
		if (StringUtils.isEmpty(input))
			return new ArrayList<>();

		String[] parts = input.split(splitter);
		List<Long> result = new ArrayList<>();

		for (String part : parts)
		{
			try
			{
				result.add(Long.parseLong(part));
			}
			catch (NumberFormatException e)
			{
				throw new UnsupportedDataTypeException();
			}
		}

		return result;
	}

	/**
	 * Converts a List full of longs represented as strings to a list of longs
	 *
	 * @param strings The list full of longs represented as strings
	 * @return A list of longs
	 */
	public static List<Long> convertToLong(List<String> strings)
	{
		List<Long> longs = new ArrayList<>();

		for (String string : strings)
		{
			try
			{
				longs.add(Long.parseLong(string));
			}
			catch (NumberFormatException e)
			{

			}
		}

		return longs;
	}

	public static Map<String, String> parseStringMap(String value, String innerSeparator, String outerSeparator)
	{
		List<String> pairs = parseStringList(value, outerSeparator);
		Map<String, String> result = new HashMap<>();

		for (String pair : pairs)
		{
			String[] parts = pair.split(innerSeparator, -1);

			if (ArrayUtils.isEmpty(parts) || parts.length != 2)
				continue;

			result.put(parts[0], parts[1]);
		}

		return result;
	}

	public static String joinMap(Map<String, String> map, String innerSeparator, String outerSeparator)
	{
		List<String> pairs = map.entrySet()
								.stream()
								.map(entry -> entry.getKey() + innerSeparator + entry.getValue())
								.collect(Collectors.toList());

		return join(pairs, outerSeparator);
	}

	public static <T> Set<T> combineSet(Collection<T> first, Collection<T> second)
	{
		Set<T> result = new HashSet<>();

		if (!isEmpty(first))
			result.addAll(first);
		if (!isEmpty(second))
			result.addAll(second);

		return result;
	}

	public static <T> List<T> combineList(Collection<T> first, Collection<T> second)
	{
		List<T> result = new ArrayList<>();

		if (!isEmpty(first))
			result.addAll(first);
		if (!isEmpty(second))
			result.addAll(second);

		return result;
	}
}
