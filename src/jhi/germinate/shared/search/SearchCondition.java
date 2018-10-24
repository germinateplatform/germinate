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

package jhi.germinate.shared.search;

import com.google.gwt.i18n.shared.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;

/**
 * SearchCondition class represents a single condition in a SearchQuery. The search condition is composed of the PhenotypeDef for the column it is
 * applied to, a instance of a ComparisonOperator and a list of values.
 *
 * @author TobyPhilp
 * @author Sebastian Raubach
 */
public class SearchCondition implements Serializable, HasToSqlString
{
	private static final long serialVersionUID = -7150710647196633015L;

	private static final String[] forbiddenCharacters = {";", "=", "<", ">", "!", "(", ")", " ", "#", "\"", "'", "--", "/*", "*/", "`"};

	/**
	 * RequiredNumberOfValues enum defines the possible value requirements for ComparisonOperator instances that can be used with this SearchCondition
	 * class.
	 *
	 * @author TP41882
	 */
	public enum RequiredNumberOfValues
	{
		ZERO,
		ONE,
		TWO,
		MANY
	}

	/**
	 * DataType enum defined the possible datatype requirements for ComarisonOperator instance that can be used with this SearchCondition class
	 *
	 * @author TP41882
	 */
	public enum DataType
	{
		ANY_NUMERIC,
		DATE,
		STRING,
		ANY
	}

	/**
	 * KeyValuePairCompatibility enum defines the different compatibility requirements a ComparisonOperator instance, compatible with this
	 * SearchCondition class, might have.
	 *
	 * @author TP41882
	 */
	public enum KeyValuePairCompatibility
	{
		REQUIRED,
		COMPATIBLE,
		NOT_COMPATIBLE
	}

	private String             columnName;
	/* We can't send Class<?> or Class, so we're sending Class#getSimpleName() */
	private String             type;
	private ComparisonOperator comp;
	private List<String>       conditionValues = new ArrayList<>();

	public SearchCondition()
	{
	}

	public SearchCondition(String columnName, ComparisonOperator comp, String conditionValue, Class<?> clazz)
	{
		this.columnName = columnName;
		this.comp = comp;
		try
		{
			addConditionValue(conditionValue);
		}
		catch (InvalidArgumentException | InvalidSearchQueryException e)
		{
			e.printStackTrace();
		}
		this.type = clazz.getSimpleName();
	}

	public SearchCondition(String columnName, ComparisonOperator comp, List<?> conditionValues, Class<?> clazz)
	{
		this.columnName = columnName;
		this.comp = comp;
		for (Object value : conditionValues)
		{
			try
			{
				addConditionValue(value.toString());
			}
			catch (InvalidArgumentException | InvalidSearchQueryException e)
			{
				e.printStackTrace();
			}
		}
		this.type = clazz.getSimpleName();
	}

	public SearchCondition(String columnName, ComparisonOperator comp, Object conditionValue, Class<?> clazz)
	{
		this(columnName, comp, conditionValue.toString(), clazz);
	}

	/**
	 * Gets the current column name being used to define the search column in this condition.
	 *
	 * @return String.
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * Sets the current PhenotypeDef to used when defining the search column in this condition.
	 *
	 * @param columnName - the PhenotypeDef instance to use
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public String getType()
	{
		return type;
	}

	/**
	 * Gets the comparison operator instance being used to define this condition.
	 *
	 * @return Inst of ComparisonOperator
	 */
	public ComparisonOperator getComp()
	{
		return comp;
	}

	/**
	 * addConditionValue adds the given value to the internal list of values that will be compared by the ComparisonOperator instance
	 *
	 * @param conditionValue - the value to add
	 * @throws InvalidArgumentException    - thrown when the value is illegal in some way, the wrong type or not injection safe
	 * @throws InvalidSearchQueryException - thrown if values are being added and the PhenotypeDef and ComparisonOperators being used have not already
	 *                                     been set (they are required for value validation)
	 */
	public void addConditionValue(String conditionValue) throws InvalidArgumentException, InvalidSearchQueryException
	{
		if (comp == null)
		{
			throw new InvalidSearchQueryException("Comparison operator must be specified before values can be handled.");
		}
		else if (StringUtils.isEmpty(conditionValue))
		{
			throw new InvalidSearchQueryException("Comparison conditional value cannot be empty.");
		}

		validateConditionValue(conditionValue);

		if (comp.getRequiredNumberOfvalues() == RequiredNumberOfValues.MANY || comp.getRequiredNumberOfvalues() == RequiredNumberOfValues.TWO)
		{
			try
			{
				String[] values = conditionValue.split(",");

				for (String value : values)
					conditionValues.add(value.trim());
			}
			catch (Exception e)
			{
				conditionValues.add(conditionValue);
			}
		}
		else
		{
			conditionValues.add(conditionValue);
		}
	}

	/**
	 * Replaces the comparison value at the given index of the values list.
	 *
	 * @param newValue - the new value to use
	 * @param index    - the index of the value to be replaced
	 * @throws InvalidArgumentException    - thrown when the value is illegal in some way, the wrong type or not injection safe
	 * @throws InvalidSearchQueryException - thrown if values are being changed/added and the PhenotypeDef and ComparisonOperators being used have not
	 *                                     already been set (they are required for value validation)
	 */
	public void replaceConditionValue(String newValue, int index) throws InvalidArgumentException, InvalidSearchQueryException
	{
		if (comp == null)
		{
			throw new InvalidSearchQueryException("Comparison operator must be specified before values can be handled.");
		}

		validateConditionValue(newValue);
		conditionValues.set(index, newValue);
	}

	/**
	 * Clears the internal list of condition values
	 */
	public void clearConditionValues()
	{
		conditionValues.clear();
	}

	/**
	 * Converts this single condition into a SQL string representation which can be used in the where clause of a query.
	 *
	 * @return SQL String
	 * @throws InvalidArgumentException    - Thrown if a search value has been added but is now found to be invalid somehow
	 * @throws InvalidSearchQueryException - Thrown if there is no comparison operator set
	 */
	public String toPreparedStatementString() throws InvalidArgumentException, InvalidSearchQueryException
	{
		if (comp != null)
		{
			comp.setColumnName(columnName);
			return comp.toPreparedStatementString(conditionValues);
		}

		throw new InvalidSearchQueryException("A search condition exists without a comparison operator selected.");
	}

	@Override
	public List<String> getValues() throws InvalidArgumentException, InvalidSearchQueryException
	{
		if (comp != null)
		{
			comp.setColumnName(columnName);
			return comp.getValues(conditionValues);
		}
		else
		{
			throw new InvalidSearchQueryException("A search condition exists without a comparison operator selected.");
		}
	}

	private void validateConditionValue(String value) throws InvalidArgumentException
	{
		DataType[] allowedDataTypes = comp.getAllowedTypesOfValue();

		boolean valid = false;

		for (DataType dt : allowedDataTypes)
		{

			if (dt == DataType.ANY_NUMERIC)
			{
				// try parse double to validate, it doesn't matter if it should be
				// int but is floating point as DB will cast
				if (value.length() != 0)
				{
					try
					{
						// NumberUtils.parseDouble(value);
						Double.parseDouble(value);
						valid = true;
					}
					catch (Exception e)
					{
					}
				}
			}
			else if (dt == DataType.DATE)
			{
				try
				{
					DefaultDateTimeFormatInfo info = new DefaultDateTimeFormatInfo();
					DateTimeFormat dtf = new DateTimeFormat("yyyy-MM-dd", info)
					{
					};

					dtf.parse(value);
					valid = true;
				}
				catch (Exception e)
				{
				}
			}
			else if (dt == DataType.STRING || dt == DataType.ANY)
			{
				//			for (String forbidden : forbiddenCharacters)
				//			{
				//				if (value.contains(forbidden))
				//					throw new InvalidArgumentException("This type of comparison is not permitted to contain the character '" + forbidden + "'");
				//			}
				valid = true;
			}
		}

		if (!valid)
			throw new InvalidArgumentException("The value '" + value + "' is not valid for this type of comparison.");
	}

	public static boolean checkSqlString(String value, boolean allowWildcards)
	{
		if (!allowWildcards && value.contains("*"))
		{
			return false;
		}
		else if (!allowWildcards && value.contains("%"))
		{
			return false;
		}
		else
		{
			for (String forbidden : forbiddenCharacters)
			{
				if (value.contains(forbidden))
					return false;
			}
		}

		return true;
	}
}
