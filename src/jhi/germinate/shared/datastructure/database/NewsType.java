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

package jhi.germinate.shared.datastructure.database;

import jhi.germinate.shared.*;

/**
 * This {@link Enum} represents the available types of news. They have to match the entries of the 'newstypes' table of the database.
 *
 * @author Sebastian Raubach
 */
public enum NewsType
{
	general(1L, "General", "General news"),
	updates(2L, "Updates", "News about updates to the page"),
	data(3L, "Data", "News about new data"),
	projects(4L, "Projects", "News about new projects");

	public static final String NAME        = "newstypes.name";
	public static final String DESCRIPTION = "newstypes.description";

	private final Long   id;
	private final String name;
	private final String description;

	NewsType(Long id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public long getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public static NewsType getByName(String name)
	{
		for (NewsType type : values())
		{
			if (StringUtils.areEqual(type.getName(), name))
				return type;
		}

		return null;
	}

	public static NewsType getById(Long id)
	{
		for (NewsType type : values())
		{
			if (type.getId() == id)
				return type;
		}

		return null;
	}
}
