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
public enum LocationType
{
	all(-1L, "All", "All locations"),
	collectingsites(1L, "Collecting sites", "Locations where accessions have been collected"),
	datasets(2L, "Dataset locations", "Locations associated with datasets"),
	trialsite(3L, "Trial sites", "Locations associated with a trial");

	private static final long serialVersionUID = -4993137085796903421L;

	public static final String ID          = "locationtypes.id";
	public static final String NAME        = "locationtypes.name";
	public static final String DESCRIPTION = "locationtypes.description";
	public static final String CREATED_ON  = "locationtypes.created_on";
	public static final String UPDATED_ON  = "locationtypes.updated_on";

	private Long   id;
	private String name;
	private String description;

	LocationType(Long id, String name, String description)
	{
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public static LocationType getById(Long id)
	{
		if (id != null)
		{
			for (LocationType type : values())
			{
				if (type.id == id.longValue())
					return type;
			}
		}

		return null;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public Long getId()
	{
		return id;
	}
}
