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
 * @author Sebastian Raubach
 */
public enum EntityType
{
	ACCESSION(1L, "Accession", "The basic working unit of conservation in the genebanks.", Style.MDI_ALPHA_A_BOX),
	PLANT_PLOT(2L, "Plant/Plot", "An individual grown from an accession OR a plot of individuals from the same accession.", Style.MDI_ALPHA_P_BOX),
	SAMPLE(3L, "Sample", "A sample from a plant. An example would be taking multiple readings for the same phenotype from a plant.", Style.MDI_ALPHA_S_BOX);

	public static final String ID          = "entitytypes.id";
	public static final String NAME        = "entitytypes.name";
	public static final String DESCRIPTION = "entitytypes.description";
	public static final String CREATED_ON  = "entitytypes.created_on";
	public static final String UPDATED_ON  = "entitytypes.updated_on";

	private final Long   id;
	private final String name;
	private final String description;
	private final String mdi;

	EntityType(Long id, String name, String description, String mdi)
	{
		this.id = id;
		this.name = name;
		this.description = description;
		this.mdi = mdi;
	}

	public static EntityType getById(Long id)
	{
		if (id != null)
		{
			for (EntityType type : values())
			{
				if (type.id == id.longValue())
					return type;
			}
		}

		return null;
	}

	public static EntityType getByName(String name)
	{
		for (EntityType type : values())
		{
			if (type.name.equals(name))
				return type;
		}

		return null;
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

	public String getMdi()
	{
		return mdi;
	}
}
