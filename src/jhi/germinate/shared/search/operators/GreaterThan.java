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

package jhi.germinate.shared.search.operators;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.SearchCondition.*;

/**
 * Implementation of the > operator
 *
 * @author Toby Philp
 * @author Sebastian Raubach
 */
public class GreaterThan implements ComparisonOperator, PotentialNumeric
{
	private static final long serialVersionUID = 4958206935909077537L;

	private String columnName;

	@Override
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	@Override
	public String getColumnName()
	{
		return columnName;
	}

	@Override
	public RequiredNumberOfValues getRequiredNumberOfvalues()
	{
		return RequiredNumberOfValues.ONE;
	}

	@Override
	public DataType[] getAllowedTypesOfValue()
	{
		return new DataType[]{DataType.ANY_NUMERIC, DataType.DATE};
	}

	@Override
	public String toString()
	{
		return " > ";
	}

	@Override
	public String toPreparedStatementString(List<String> values) throws InvalidArgumentException
	{
		if (values.size() < 1)
		{
			throw new InvalidArgumentException("Greater than comparison requires that one comparison value is provided.");
		}
		else if (StringUtils.isEmpty(values.get(0)))
		{
			throw new InvalidArgumentException("Greater than comparison requires that one comparison value is provided.");
		}
		return " " + columnName + " > ?";
	}

	@Override
	public List<String> getValues(List<String> values) throws InvalidArgumentException
	{
		return values;
	}

	@Override
	public boolean isNumeric(List<String> values)
	{
		try
		{
			Long.parseLong(values.get(0));

			return true;
		}
		catch (Exception e1)
		{
			try
			{
				Double.parseDouble(values.get(0));

				return true;
			}
			catch (Exception e2)
			{
				return false;
			}
		}
	}

	@Override
	public KeyValuePairCompatibility getKeyValuePairCompatibility()
	{
		return KeyValuePairCompatibility.NOT_COMPATIBLE;
	}
}
