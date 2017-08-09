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

package jhi.germinate.shared.datastructure;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class CustomMenu implements Serializable
{
	public Page                page   = null;
	public String              id     = null;
	public String              icon   = null;
	public List<CustomMenu>    menus  = new ArrayList<>();
	public Map<String, String> labels = new HashMap<>();

	public CustomMenu()
	{
	}

	public String getId()
	{
		return id;
	}

	public CustomMenu setId(String id)
	{
		this.id = id;
		return this;
	}

	public String getIcon()
	{
		return icon;
	}

	public CustomMenu setIcon(String icon)
	{
		this.icon = icon;
		return this;
	}

	public Page getPage()
	{
		return page;
	}

	public CustomMenu setPage(Page page)
	{
		this.page = page;
		return this;
	}

	public List<CustomMenu> getMenus()
	{
		return menus;
	}

	public CustomMenu setMenus(List<CustomMenu> menus)
	{
		this.menus = menus;
		return this;
	}

	public CustomMenu addMenu(CustomMenu menu)
	{
		menus.add(menu);
		return this;
	}

	public Map<String, String> getLabels()
	{
		return labels;
	}

	public CustomMenu setLabels(Map<String, String> labels)
	{
		this.labels = labels;
		return this;
	}

	@Override
	public String toString()
	{
		return "CustomMenu{" + "page=" + page +
				", id='" + id + '\'' +
				", menus=" + menus +
				", labels=" + labels +
				'}';
	}
}
