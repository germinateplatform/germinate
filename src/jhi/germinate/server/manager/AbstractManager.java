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

package jhi.germinate.server.manager;

import java.sql.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * The base class for all managers.
 *
 * @author Sebastian Raubach
 */
public abstract class AbstractManager<T extends DatabaseObject>
{
	public static final String COUNT = "count";

	private static final String SELECT_TRUSTED_CELL   = "SELECT %s FROM %s WHERE id = ?";
	private static final String UPDATE_AUTO_INCREMENT = "call " + StoredProcedureInitializer.UPDATE_AUTO_INCREMENT + "(?)";

	/**
	 * Returns the cell value in the row and column for the given id <p> <b>ONLY USE IF YOU KNOW THAT 'table' AND 'column' ARE NOT (!!!) SPECIFIED BY
	 * THE USER</b>
	 *
	 * @param userAuth The current user
	 * @param table    The database table
	 * @param column   The database column in that table
	 * @param id       The id of the object
	 * @return The cell value in the row and column for the given id
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static ServerResult<String> getTrustedCellValue(UserAuth userAuth, String table, String column, Long id) throws DatabaseException
	{
		String formatted = String.format(SELECT_TRUSTED_CELL, column, table);

		return new ValueQuery(formatted)
				.setLong(id)
				.run(column)
				.getString();
	}

	/**
	 * Resets the AUTO_INCREMENT of this table.
	 *
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public void resetAutoIncrement() throws DatabaseException
	{
		new ValueQuery(UPDATE_AUTO_INCREMENT)
				.setString(getTable())
				.execute();
	}

	/**
	 * Resets the AUTO_INCREMENT of this table.
	 *
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static void resetAutoIncrement(GerminateDatabaseTable table) throws DatabaseException
	{
		new ValueQuery(UPDATE_AUTO_INCREMENT)
				.setString(table.name())
				.execute();
	}

	/**
	 * Returns the {@link DatabaseObject} with the given id
	 *
	 * @param user The user requesting the data
	 * @param id   The id of the object
	 * @return The {@link DatabaseObject} with the given id
	 * @throws DatabaseException                Thrown if the interaction with the database fails
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to access the data
	 */
	public ServerResult<T> getById(UserAuth user, Long id) throws DatabaseException, InsufficientPermissionsException
	{
		if (id == null)
			return new ServerResult<>(null, null);

		return new DatabaseObjectQuery<T>("SELECT * FROM " + getTable() + " WHERE id = ?", user)
				.setLong(id)
				.run()
				.getObject(getParser());
	}

	/**
	 * Extracts and returns the {@link DatabaseObject} from the {@link DatabaseResult} without running additional queries. This means that all the
	 * information for the {@link DatabaseObject}s that are fields of this item needs to be contained in the {@link DatabaseResult} as well.
	 *
	 * @param user The user requesting the data
	 * @param res  The {@link DatabaseResult} containing the data
	 * @return The {@link DatabaseObject} from the {@link DatabaseResult} without running additional queries. This means that all the information for
	 * the {@link DatabaseObject}s that are fields of this item needs to be contained in the {@link DatabaseResult} as well.
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public T getFromResult(UserAuth user, DatabaseResult res) throws DatabaseException
	{
		return getParser().parse(res, user, true);
	}

	/**
	 * Returns the database table name of which this {@link DatabaseObject} is the representation
	 *
	 * @return The database table name of which this {@link DatabaseObject} is the representation
	 */
	protected abstract String getTable();

	/**
	 * Returns the {@link DatabaseObjectParser} that can be used to parse the {@link DatabaseObject} from the {@link DatabaseResult}.
	 *
	 * @return The {@link DatabaseObjectParser} that can be used to parse the {@link DatabaseObject} from the {@link DatabaseResult}.
	 */
	protected abstract DatabaseObjectParser<T> getParser();

	/**
	 * This method is a utility method that will replace a placeholder (<code>{{FILTER}}</code>) with the SQL version of the given {@link
	 * PartialSearchQuery}. <p> <b>IMPORTANT:</b> Make sure that the given SQL string contains this placeholder and also make sure that it is placed
	 * in the location where the SQL keyword <code>WHERE</code> would usually be placed. This method will in ALL cases add a <code>WHERE</code>
	 * clause. If no filter is given or it's empty, this "where" clause will be <code>WHERE 1=1</code>. Make sure to join additional conditions with
	 * "AND" or "OR" to id.
	 *
	 * @param userAuth       The current user
	 * @param filter         The {@link PartialSearchQuery}
	 * @param input          The given SQL string with the filter placeholder
	 * @param allowedColumns The allowed columns to filter on
	 * @return Returns the given SQL string with the filter placeholder replaced with the SQL representation of the {@link PartialSearchQuery}
	 * @throws DatabaseException           Thrown if the interaction with the database fails
	 * @throws InvalidSearchQueryException Thrown if any part of the {@link PartialSearchQuery} contains a missing comparison operator
	 * @throws InvalidColumnException      Thrown if the filter is trying to filter a column that hasn't been specified in the allowedColumns
	 *                                     parameter
	 * @throws InvalidArgumentException    Thrown if any part of the {@link PartialSearchQuery} is invalid
	 */
	public static <T extends DatabaseObject> DatabaseObjectQuery<T> getFilteredDatabaseObjectQuery(UserAuth userAuth, PartialSearchQuery filter, String input, String[] allowedColumns, Integer previousCount) throws InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException
	{
		String formatted = getFormattedString(filter, input, allowedColumns);

		DatabaseObjectQuery<T> query = new DatabaseObjectQuery<>(formatted, userAuth);
		query.setFetchesCount(previousCount);

		if (filter != null)
			setParameters(query, filter);

		return query;
	}

	/**
	 * This method is a utility method that will replace a placeholder (<code>{{FILTER}}</code>) with the SQL version of the given {@link
	 * PartialSearchQuery}. <p> <b>IMPORTANT:</b> Make sure that the given SQL string contains this placeholder and also make sure that it is placed
	 * in the location where the SQL keyword <code>WHERE</code> would usually be placed. This method will in ALL cases add a <code>WHERE</code>
	 * clause. If no filter is given or it's empty, this "where" clause will be <code>WHERE 1=1</code>. Make sure to join additional conditions with
	 * "AND" or "OR" to id.
	 *
	 * @param filter         The {@link PartialSearchQuery}
	 * @param input          The given SQL string with the filter placeholder
	 * @param allowedColumns The allowed columns to filter on
	 * @return Returns the given SQL string with the filter placeholder replaced with the SQL representation of the {@link PartialSearchQuery}
	 * @throws DatabaseException           Thrown if the interaction with the database fails
	 * @throws InvalidSearchQueryException Thrown if any part of the {@link PartialSearchQuery} contains a missing comparison operator
	 * @throws InvalidColumnException      Thrown if the filter is trying to filter a column that hasn't been specified in the allowedColumns
	 *                                     parameter
	 * @throws InvalidArgumentException    Thrown if any part of the {@link PartialSearchQuery} is invalid
	 */
	public static ValueQuery getFilteredValueQuery(PartialSearchQuery filter, String input, String[] allowedColumns) throws InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException
	{
		String formatted = getFormattedString(filter, input, allowedColumns);

		ValueQuery query = new ValueQuery(formatted);

		if (filter != null)
			setParameters(query, filter);

		return query;
	}

	/**
	 * This method is a utility method that will replace a placeholder (<code>{{FILTER}}</code>) with the SQL version of the given {@link
	 * PartialSearchQuery}. <p> <b>IMPORTANT:</b> Make sure that the given SQL string contains this placeholder and also make sure that it is placed
	 * in the location where the SQL keyword <code>WHERE</code> would usually be placed. This method will in ALL cases add a <code>WHERE</code>
	 * clause. If no filter is given or it's empty, this "where" clause will be <code>WHERE 1=1</code>. Make sure to join additional conditions with
	 * "AND" or "OR" to id.
	 *
	 * @param filter         The {@link PartialSearchQuery}
	 * @param input          The given SQL string with the filter placeholder
	 * @param allowedColumns The allowed columns to filter on
	 * @return Returns the given SQL string with the filter placeholder replaced with the SQL representation of the {@link PartialSearchQuery}
	 * @throws DatabaseException           Thrown if the interaction with the database fails
	 * @throws InvalidSearchQueryException Thrown if any part of the {@link PartialSearchQuery} contains a missing comparison operator
	 * @throws InvalidColumnException      Thrown if the filter is trying to filter a column that hasn't been specified in the allowedColumns
	 *                                     parameter
	 * @throws InvalidArgumentException    Thrown if any part of the {@link PartialSearchQuery} is invalid
	 */
	public static GerminateTableQuery getFilteredGerminateTableQuery(PartialSearchQuery filter, String input, String[] allowedColumns, String[] columnNames) throws InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException
	{
		String formatted = getFormattedString(filter, input, allowedColumns);

		GerminateTableQuery query = new GerminateTableQuery(formatted, columnNames);

		if (filter != null)
			setParameters(query, filter);

		return query;
	}

	private static void setParameters(GerminateQuery<?> query, PartialSearchQuery filter) throws InvalidSearchQueryException, InvalidArgumentException, DatabaseException
	{
		List<SearchCondition> queries = filter.getAll();

		if (queries != null)
		{
			for (SearchCondition part : queries)
			{
				if (part.getComp() instanceof PotentialNumeric)
				{
					if (((PotentialNumeric) part.getComp()).isNumeric(part.getValues()))
					{
						for (String value : part.getValues())
						{
							try
							{
								query.setDouble(Double.parseDouble(value));
							}
							catch (Exception e)
							{
								query.setNull(Types.VARCHAR);
							}
						}
					}
					else
					{
						query.setStrings(part.getValues());
					}
				}
				else
				{
					query.setStrings(part.getValues());
				}
			}
		}
	}

	private static String getFormattedString(PartialSearchQuery filter, String input, String[] allowedColumns) throws InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		if (filter != null)
		{
			/* Assemble the overall query */
			List<SearchCondition> queries = filter.getAll();
			List<LogicalOperator> operators = filter.getLogicalOperators();

			StringBuilder builder = new StringBuilder();

			for (int part = 0; part < queries.size(); part++)
			{
				SearchCondition query = queries.get(part);

				/* We need to save this, so we can set it back to the default if we modify it */
				String oldColumnName = query.getColumnName();

				/* Try to parse them into an enum to check that there was no manipulation => Only valid columns are allowed. */
				Util.checkSortColumn(oldColumnName, allowedColumns, "doesn't matter");

				if (part > 0)
					builder.append(operators.get(part - 1).toString());

				/* Make sure we only compare the visible digits in the case of decimal columns */
				if (Objects.equals(Double.class.getSimpleName(), query.getType()) || Objects.equals(Float.class.getSimpleName(), query.getType()))
				{
					query.setColumnName("CAST(" + oldColumnName + " AS DECIMAL(30,2))");
				}

				builder.append(query.toPreparedStatementString());

				query.setColumnName(oldColumnName);
			}

			return input.replace("{{FILTER}}", builder.length() > 0 ? " WHERE (" + builder.toString() + ")" : " WHERE 1=1");
		}
		else
		{
			return input.replace("{{FILTER}}", " WHERE 1=1");
		}
	}
}
