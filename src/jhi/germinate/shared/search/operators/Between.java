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

package jhi.germinate.shared.search.operators;

import com.google.gwt.i18n.shared.*;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.SearchCondition.*;

/**
 * Implementation of the BETWEEN operator
 *
 * @author Toby Philp
 * @author Sebastian Raubach
 */
public class Between implements ComparisonOperator, PotentialNumeric
{
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
		return RequiredNumberOfValues.TWO;
	}

	@Override
	public DataType[] getAllowedTypesOfValue()
	{
		return new DataType[]{DataType.ANY_NUMERIC, DataType.DATE, DataType.STRING};
	}

	@Override
	public String toPreparedStatementString(List<String> values) throws InvalidArgumentException
	{
		String condition;

		/* If either the collection is null or contains less than 2 items or either of the two items is empty of null, throw an exception */
		if (CollectionUtils.isEmpty(values) || values.size() < 2 || StringUtils.isEmpty(values.toArray(new String[values.size()])))
			throw new InvalidArgumentException("Between requires two arguments to be provided.");

		condition = " " + columnName + " BETWEEN ? AND ? ";

		return condition;
	}

	@Override
	public boolean isNumeric(List<String> values)
	{
		try
		{
			Long.parseLong(values.get(0));
			Long.parseLong(values.get(1));

			return true;
		}
		catch (Exception e1)
		{
			try
			{
				Double.parseDouble(values.get(0));
				Double.parseDouble(values.get(1));

				return true;
			}
			catch (Exception e2)
			{
				return false;
			}
		}
	}

	public static List<String> getCheckedValues(List<String> values, DataType[] dataTypes) throws InvalidArgumentException
	{
		try
		{
			long a = Long.parseLong(values.get(0));
			long b = Long.parseLong(values.get(1));

			long max = a >= b ? a : b;
			long min = a >= b ? b : a;

			return Arrays.asList(Long.toString(min), Long.toString(max));
		}
		catch (Exception e1)
		{
			try
			{
				double a = Double.parseDouble(values.get(0));
				double b = Double.parseDouble(values.get(1));

				double max = a >= b ? a : b;
				double min = a >= b ? b : a;

				return Arrays.asList(Double.toString(min), Double.toString(max));
			}
			catch (Exception e2)
			{
				try
				{
					DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
					DateTimeFormat dtf = new DateTimeFormat("yyyy-MM-dd", info)
					{
					};

					Date a = dtf.parse(values.get(0));
					Date b = dtf.parse(values.get(1));

					Date min = a.compareTo(b) < 0 ? a : b;
					Date max = a.equals(min) ? b : a;

					return Arrays.asList(dtf.format(min), dtf.format(max));
				}
				catch (Exception e3)
				{
					for (DataType type : dataTypes)
					{
						if (type == DataType.STRING)
							return values;
					}

					throw new InvalidArgumentException("Between requires two number/date arguments to be provided.");
				}
			}
		}
	}

	@Override
	public List<String> getValues(List<String> values) throws InvalidArgumentException
	{
		return getCheckedValues(values, getAllowedTypesOfValue());
	}

	@Override
	public KeyValuePairCompatibility getKeyValuePairCompatibility()
	{
		return KeyValuePairCompatibility.NOT_COMPATIBLE;
	}
}
