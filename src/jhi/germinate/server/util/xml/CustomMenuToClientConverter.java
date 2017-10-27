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

package jhi.germinate.server.util.xml;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class CustomMenuToClientConverter
{
	public static CustomMenu convertCustomMenu(CustomMenuServer server)
	{
		CustomMenu result = new CustomMenu();

		convert(server.getItems(), result);

		return result;
	}

	private static void convert(List<CustomMenuServer.Item> items, CustomMenu menu)
	{
		for (CustomMenuServer.Item item : items)
		{
			/* If this item represents a page */
			if (CollectionUtils.isEmpty(item.getItems()))
			{
				try
				{
					CustomMenu i = new CustomMenu();
					i.setPage(Page.valueOf(item.getKey()));
					i.setLabels(item.getLabels());
					i.setIcon(item.getIcon());
					menu.addMenu(i);
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
			}
			/* Else, it represents a submenu */
			else
			{
				CustomMenu sub = new CustomMenu();
				sub.setId(item.getKey());
				sub.setLabels(item.getLabels());
				sub.setIcon(item.getIcon());
				menu.addMenu(sub);
				convert(item.getItems(), sub);
			}
		}
	}
}
