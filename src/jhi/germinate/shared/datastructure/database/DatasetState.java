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

/**
 * @author Sebastian Raubach
 */
public enum DatasetState
{
	PUBLIC(1L, "public", "Public datasets are visible to all registered users on private web interfaces and everybody on public web interfaces."),
	PRIVATE(2L, "private", "Private datasets are visible to all registered admin users and the creator of the dataset. They are not visible on the public web interface."),
	HIDDEN(3L, "hidden", "Hidden datasets are only visible to admins.");

	private final Long   id;
	private final String name;
	private final String description;

	DatasetState(Long id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Long getId()
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

	public static DatasetState getById(Long id)
	{
		if (id != null)
		{
			for (DatasetState type : values())
			{
				if (type.id == id.longValue())
					return type;
			}
		}

		return null;
	}

	public static DatasetState getByName(String name)
	{
		for (DatasetState type : values())
		{
			if (type.name.equals(name))
				return type;
		}

		return null;
	}
}
