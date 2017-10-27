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

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link OverallSearchQuery} class represents a collection of {@link HasToSqlString}s and {@link LogicalOperator}s that join the {@link
 * HasToSqlString}s. The combination of conditions and logical operators can then be turned into a SQL string representation that can be used in the
 * where clause of a query.
 *
 * @param <T> The class implementing {@link HasToSqlString}
 * @author Toby Philp
 * @author Sebastian Raubach
 */
public class OverallSearchQuery<T extends HasToSqlString> implements Serializable, HasToSqlString
{
	private static final long serialVersionUID = -7276446056031162442L;

	protected List<T>               conditions       = new ArrayList<>();
	protected List<LogicalOperator> logicalOperators = new ArrayList<>();

	// bean style getters and setters for serialisation
	public List<T> getAll()
	{
		return conditions;
	}

	public void set(List<T> conditions)
	{
		this.conditions = conditions;
	}

	public List<LogicalOperator> getLogicalOperators()
	{
		return logicalOperators;
	}

	public void setLogicalOperators(List<LogicalOperator> logicalOperators)
	{
		this.logicalOperators = logicalOperators;
	}

	/**
	 * Adds the given condition to the internal list of conditions that forms this query.
	 *
	 * @param condition - the condition to add
	 */
	public void add(T condition)
	{
		conditions.add(condition);
	}

	/**
	 * Removes the given condition instance from the internal conditions list
	 *
	 * @param condition - condition instance to remove
	 */
	public void remove(T condition)
	{
		conditions.remove(condition);
	}

	/**
	 * Removes the condition at the given index from the internal conditions list
	 *
	 * @param index - index of the condition to remove
	 */
	public void remove(int index)
	{
		conditions.remove(index);
	}

	/**
	 * Adds a logical operator instance to the internal list of logical operators
	 *
	 * @param op - the LogicalOperator instance to add
	 */
	public void addLogicalOperator(LogicalOperator op)
	{
		logicalOperators.add(op);
	}

	/**
	 * Removes a logical operator instance from the internal list of logical operators
	 *
	 * @param op - the LogicalOperator instance to remove
	 */
	public void removeLogicalOperator(LogicalOperator op)
	{
		logicalOperators.remove(op);
	}

	/**
	 * Removes the logical operator at the given index from the internal list of logical operators
	 *
	 * @param index - index of the operator to remove
	 */
	public void removeLogicalOperator(int index)
	{
		logicalOperators.remove(index);
	}

	/**
	 * Clears the SearchQuery by removing all added SearchCondition instances and all LogicalOperator instances from the internal lists.
	 */
	public void clear()
	{
		logicalOperators.clear();
		conditions.clear();
	}

	/**
	 * Converts the current instance of SearchQuery into a SQL string representation that can be used in a where clause.
	 *
	 * @return SQL string
	 * @throws InvalidArgumentException    - thrown if any part of a condition includes an invalid argument
	 * @throws InvalidSearchQueryException - thrown if the SearchQuery or any of its SearchConditions is incomplete
	 */
	public String toPreparedStatementString() throws InvalidArgumentException, InvalidSearchQueryException
	{
		if (conditions.size() == 0)
		{
			throw new InvalidArgumentException("A search query contains no conditions");
		}
		else if (conditions.size() == 1)
		{
			String value = conditions.get(0).toPreparedStatementString();
			if (!StringUtils.isEmpty(value))
			{
				return value;
			}
			else
			{
				return " 1=1 ";
			}
		}
		else if (conditions.size() > 1)
		{
			StringBuilder conditionsQuery = new StringBuilder();
			for (int i = 0; i < conditions.size(); i++)
			{
				String value = conditions.get(i).toPreparedStatementString();
				if (!StringUtils.isEmpty(value))
				{
					conditionsQuery.append("(")
								   .append(value)
								   .append(")");
				}

				if (logicalOperators == null || logicalOperators.size() - 1 < i)
				{
					break;
				}
				else
				{
					if (!StringUtils.isEmpty(value))
					{
						LogicalOperator l = logicalOperators.get(i);
						conditionsQuery.append(l.toString());
					}
				}
			}

			if (StringUtils.isEmpty(conditionsQuery.toString()))
			{
				return "1=1";
			}
			return conditionsQuery.toString();
		}
		return null;
	}

	@Override
	public List<String> getValues() throws InvalidArgumentException, InvalidSearchQueryException
	{
		if (conditions.size() == 0)
		{
			throw new InvalidArgumentException("A search query contains no conditions");
		}
		else if (conditions.size() == 1)
		{
			return conditions.get(0).getValues();
		}
		else if (conditions.size() > 1)
		{
			List<String> values = new ArrayList<>();

			for (T condition : conditions)
			{
				values.addAll(condition.getValues());
			}

			return values;
		}

		return null;
	}
}
