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

package jhi.germinate.shared.datastructure.database;

import java.util.*;

/**
 * This {@link Enum} represents the available types of experiments. They have to match the entries of the 'experimenttypes' table of the database.
 *
 * @author Sebastian Raubach
 */
public enum ExperimentType
{
	genotype(1L),
	trials(3L),
	allelefreq(4L),
	climate(5L),
	compound(6L),
	unknown(-1L);

	public static final String DESCRIPTION = "experimenttypes.description";

	private final Long id;

	ExperimentType(Long id)
	{
		this.id = id;
	}

	public Long getId()
	{
		return id;
	}

	public static ExperimentType getById(Long id)
	{
		for (ExperimentType type : values())
		{
			if (Objects.equals(type.getId(), id))
				return type;
		}

		return unknown;
	}
}
