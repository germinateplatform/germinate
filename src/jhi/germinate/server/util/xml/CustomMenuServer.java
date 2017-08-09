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

package jhi.germinate.server.util.xml;

import org.simpleframework.xml.*;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
@Root(name = "menu", strict = false)
public class CustomMenuServer
{
	@ElementList(name = "item", inline = true, required = false)
	public List<Item> items = new ArrayList<>();

	public CustomMenuServer()
	{
	}

	public List<Item> getItems()
	{
		return items;
	}

	public CustomMenuServer setItems(List<Item> items)
	{
		this.items = items;
		return this;
	}

	@Root(name = "item")
	public static class Item
	{
		@Attribute
		public String key;

		@Attribute(required = false)
		public String icon;

		@ElementMap(entry = "label", key = "key", attribute = true, inline = true, required = false)
		public Map<String, String> labels = new HashMap<>();

		@ElementList(name = "item", inline = true, required = false)
		public List<Item> items = new ArrayList<>();

		public Item()
		{
		}

		public String getKey()
		{
			return key;
		}

		public Item setKey(String key)
		{
			this.key = key;
			return this;
		}

		public String getIcon()
		{
			return icon;
		}

		public Item setIcon(String icon)
		{
			this.icon = icon;
			return this;
		}

		public Map<String, String> getLabels()
		{
			return labels;
		}

		public Item setLabels(Map<String, String> labels)
		{
			this.labels = labels;
			return this;
		}

		public List<Item> getItems()
		{
			return items;
		}

		public Item setItems(List<Item> items)
		{
			this.items = items;
			return this;
		}
	}
}