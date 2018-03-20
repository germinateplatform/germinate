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


/**
 * {@link ArrayUtils} contains methods to manipulate/check arrays. All methods can be used both on the client and server side.
 *
 * @author Sebastian Raubach
 */
public class ArrayUtils
{
	/**
	 * Checks if the given array is either <code>null</code> or its size is smaller than 1.
	 *
	 * @param input The array to check
	 * @return <code>true</code> if the given array is either <code>null</code> or its size is smaller than 1
	 */
	public static <T> boolean isEmpty(T[] input)
	{
		return input == null || input.length < 1;
	}

	/**
	 * Checks if the given array is either <code>null</code> or its size is smaller than 1 or if all Strings inside it fulfill {@link
	 * StringUtils#isEmpty(String)}.
	 *
	 * @param input The array to check
	 * @return <code>true</code> if the given array is either <code>null</code> or its size is smaller than 1 or if all Strings inside it fulfill
	 * {@link StringUtils#isEmpty(String)}.
	 */
	public static boolean isEmpty(String[] input)
	{
		if (input == null || input.length < 1)
			return true;
		else
		{
			for (String part : input)
			{
				if (!StringUtils.isEmpty(part))
					return false;
			}
		}

		return true;
	}

	/**
	 * Adds the new element into an array
	 *
	 * @param input                The original array
	 * @param theNewGuyNobodyLikes The new element
	 * @param output               The output array
	 * @return The output array with the data of input and theNewGuyNobodyLikes
	 */
	public static <T> T[] add(T[] input, T theNewGuyNobodyLikes, T[] output)
	{
		return insert(input, theNewGuyNobodyLikes, output, input.length);
	}

	/**
	 * Inserts the new element into an array
	 *
	 * @param input                The original array
	 * @param theNewGuyNobodyLikes The new element
	 * @param output               The output array
	 * @param index                The index
	 * @return The output array with the data of input and theNewGuyNobodyLikes
	 */
	public static <T> T[] insert(T[] input, T theNewGuyNobodyLikes, T[] output, int index)
	{
		int j = 0;
		for (int i = 0; i < input.length; i++)
		{
			if (i == index)
			{
				j++;
			}

			output[j] = input[i];

			j++;
		}

		output[index] = theNewGuyNobodyLikes;

		return output;
	}

	/**
	 * Joins the given input array with the given delimiter into a String. Uses {@link Object#toString()} to convert {@link Object}s to {@link
	 * String}s.
	 *
	 * @param input     The input array
	 * @param delimiter The delimiter to use
	 * @return The joined String
	 */
	public static String join(Object[] input, String delimiter)
	{
		if (input.length < 1)
			return "";

		StringBuilder builder = new StringBuilder();

		builder.append(input[0]);

		for (int i = 1; i < input.length; i++)
		{
			builder.append(delimiter).append(input[i]);
		}

		return builder.toString();
	}
}
