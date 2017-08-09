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
import java.util.Map;

import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class TrialAccessionYears implements Serializable
{
	private static final long serialVersionUID = 1185957583958698034L;

	private Phenotype phenotype;
	private Map<String, Integer> yearToValues = new HashMap<>();

	public TrialAccessionYears()
	{
	}

	public TrialAccessionYears(Phenotype phenotype)
	{
		this.phenotype = phenotype;
	}

	public Phenotype getPhenotype()
	{
		return phenotype;
	}

	public void setPhenotype(Phenotype phenotype)
	{
		this.phenotype = phenotype;
	}

	public Map<String, Integer> getYearToValues()
	{
		return yearToValues;
	}

	public void addValues(String key, Integer value)
	{
		yearToValues.put(key, value);
	}

	public void addValues(String key, String value)
	{
		try
		{
			addValues(key, Integer.parseInt(value));
		}
		catch (NumberFormatException e)
		{
			// Do nothing here
		}
	}
}
