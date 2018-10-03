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

import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public enum SynonymType
{
	germinatebase(1L, "germinatebase", "Accessions", "Accession synonyms"),
	markers(2L, "markers", "Markers", "Marker synonyms"),
	compounds(3L, "compounds", "Compounds", "Compound synonyms"),
	phenotypes(4L, "phenotypes", "Phenotypes", "Phenotype synonyms");

	private final Long   id;
	private final String targetTable;
	private final String name;
	private final String description;

	SynonymType(Long id, String targetTable, String name, String description)
	{
		this.id = id;
		this.targetTable = targetTable;
		this.name = name;
		this.description = description;
	}

	public static SynonymType getById(Long id)
	{
		if (id != null)
		{
			for (SynonymType type : values())
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

	public static SynonymType getForTable(GerminateDatabaseTable table)
	{
		for (SynonymType type : values())
		{
			if (type.targetTable.equals(table.name()))
				return type;
		}

		return null;
	}

	public Long getId()
	{
		return id;
	}
}
