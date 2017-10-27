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

package jhi.germinate.client.util;

import com.google.gwt.user.client.ui.*;

import java.util.*;

/**
 * {@link ListBoxUtils} contains methods that make using {@link ListBox}s easier
 *
 * @author Sebastian Raubach
 */
public class ListBoxUtils
{
	/**
	 * Retrieves the values of all the selected items in the given ListBox
	 *
	 * @param box The ListBox to extract the information from
	 * @return The List of selected ids
	 */
	public static List<String> getValues(ListBox box)
	{
		return getValues(box, false);
	}

	/**
	 * Retrieves the values of all the selected items in the given ListBox
	 *
	 * @param box         The ListBox to extract the information from
	 * @param ignoreFirst Ignore the first list entry?
	 * @return The List of selected ids
	 */
	public static List<String> getValues(ListBox box, boolean ignoreFirst)
	{
		if (box == null)
			throw new RuntimeException("Box is null");
		else if (!box.isAttached())
			throw new RuntimeException("Box is not attached");

		List<String> result = new ArrayList<>();

		for (int i = ignoreFirst ? 1 : 0; i < box.getItemCount(); i++)
		{
			if (box.isItemSelected(i))
				result.add(box.getValue(i));
		}

		return result;
	}

	/**
	 * Retrieves the value of the selected item in the given ListBox
	 *
	 * @param box The ListBox to extract the information from
	 * @return The selected id
	 */
	public static String getValue(ListBox box)
	{
		return getValue(box, false);
	}

	/**
	 * Retrieves the value of the selected item in the given ListBox
	 *
	 * @param box         The ListBox to extract the information from
	 * @param ignoreFirst Ignore the first list entry?
	 * @return The selected id
	 */
	public static String getValue(ListBox box, boolean ignoreFirst)
	{
		if (box == null)
			throw new RuntimeException("Box is null");
		else if (!box.isAttached())
			throw new RuntimeException("Box is not attached");

		for (int i = ignoreFirst ? 1 : 0; i < box.getItemCount(); i++)
		{
			if (box.isItemSelected(i))
				return box.getValue(i);
		}

		return null;
	}

	/**
	 * Retrieves the item text of all the selected items in the given ListBox
	 *
	 * @param box The ListBox to extract the information from
	 * @return The List of selected names
	 */
	public static List<String> getItemTexts(ListBox box)
	{
		return getItemTexts(box, false);
	}

	/**
	 * Retrieves the item text of all the selected items in the given ListBox
	 *
	 * @param box         The ListBox to extract the information from
	 * @param ignoreFirst Ignore the first list entry?
	 * @return The List of selected names
	 */
	public static List<String> getItemTexts(ListBox box, boolean ignoreFirst)
	{
		if (box == null)
			throw new RuntimeException("Box is null");
		else if (!box.isAttached())
			throw new RuntimeException("Box is not attached");

		List<String> result = new ArrayList<>();

		for (int i = ignoreFirst ? 1 : 0; i < box.getItemCount(); i++)
		{
			if (box.isItemSelected(i))
				result.add(box.getItemText(i));
		}

		return result;
	}

	/**
	 * Retrieves the item text of the selected item in the given ListBox
	 *
	 * @param box The ListBox to extract the information from
	 * @return The selected name
	 */
	public static String getItemText(ListBox box)
	{
		return getItemText(box, false);
	}

	/**
	 * Retrieves the item text of the selected item in the given ListBox
	 *
	 * @param box         The ListBox to extract the information from
	 * @param ignoreFirst Ignore the first list entry?
	 * @return The selected name
	 */
	public static String getItemText(ListBox box, boolean ignoreFirst)
	{
		if (box == null)
			throw new RuntimeException("Box is null");
		else if (!box.isAttached())
			throw new RuntimeException("Box is not attached");

		for (int i = ignoreFirst ? 1 : 0; i < box.getItemCount(); i++)
		{
			if (box.isItemSelected(i))
				return box.getItemText(i);
		}

		return null;
	}

	public static boolean[] getSelectedIndices(ListBox box)
	{
		boolean[] result = new boolean[box.getItemCount()];

		for (int i = 0; i < box.getItemCount(); i++)
		{
			if (box.isItemSelected(i))
				result[i] = true;
		}

		return result;
	}
}
