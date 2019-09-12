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

package jhi.germinate.server.database;

import java.sql.*;
import java.util.*;

import jhi.germinate.shared.exception.*;

/**
 * {@link DatabaseResult} is a wrapper class for {@link ResultSet}.
 *
 * @author Sebastian Raubach
 */
public final class DatabaseResult
{
	private final ResultSet         rs;
	private       Database          database;
	private       ResultSetMetaData rsmd;

	/**
	 * Creates a new {@link DatabaseResult} wrapping the given {@link ResultSet} and the {@link Database}.
	 *
	 * @param rs       The {@link ResultSet} containing the actual data
	 * @param database The {@link Database} used to create the {@link ResultSet}
	 */
	public DatabaseResult(ResultSet rs, Database database) throws DatabaseException
	{
		this.rs = rs;
		this.database = database;
		try
		{
			this.rsmd = rs.getMetaData();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Moves the cursor forward one row from its current position. A ResultSet cursor is initially positioned before the first row; the first call to
	 * the method next makes the first row the current row; the second call makes the second row the current row, and so on. When a call to the next
	 * method returns false, the cursor is positioned after the last row. Any invocation of a ResultSet method which requires a current row will
	 * result in a SQLException being thrown. If the result set type is <code>TYPE_FORWARD_ONLY</code>, it is vendor specified whether their JDBC
	 * driver implementation will return false or throw an {@link SQLException} on a subsequent call to next.
	 * <p/>
	 * If an input stream is open for the current row, a call to the method next will implicitly close it. A {@link ResultSet} object's warning chain
	 * is cleared when a new row is readAll.
	 *
	 * @return <code>true</code> if the new current row is valid; <code>false</code> if there are no more rows
	 * @throws DatabaseException Thrown if {@link ResultSet#next()} fails
	 * @see ResultSet#next()
	 */
	public boolean next() throws DatabaseException
	{
		try
		{
			return this.rs.next();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves whether the cursor is before the first row in
	 * this <code>ResultSet</code> object.
	 * <p>
	 * <strong>Note:</strong>Support for the <code>isBeforeFirst</code> method
	 * is optional for <code>ResultSet</code>s with a result
	 * set type of <code>TYPE_FORWARD_ONLY</code>
	 *
	 * @return <code>true</code> if the cursor is before the first row;
	 * <code>false</code> if the cursor is at any other position or the
	 * result set contains no rows
	 * @throws DatabaseException Thrown if {@link ResultSet#isBeforeFirst()} ()} fails
	 */
	public boolean isBeforeFirst() throws DatabaseException
	{
		try
		{
			return this.rs.isBeforeFirst();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as an int in the Java programming
	 * language.
	 *
	 * @param columnLabel the label for the column specified with the SQL <code>AS</code> clause. If the SQL <code>AS</code> clause was not specified,
	 *                    then the label is the name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is 0
	 * @throws DatabaseException Thrown if {@link ResultSet#getInt(String)} fails
	 * @see ResultSet#getInt(String)
	 */
	public Integer getInt(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			int result = this.rs.getInt(columnLabel);

			if (this.rs.wasNull())
				return null;
			else
				return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as an int in the Java programming
	 * language.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is 0
	 * @throws DatabaseException Thrown if {@link ResultSet#getInt(int)} fails
	 * @see ResultSet#getInt(int)
	 */
	public int getInt(int columnIndex) throws DatabaseException
	{
		try
		{
			return this.rs.getInt(columnIndex);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as a long in the Java programming
	 * language.
	 *
	 * @param columnLabel the label for the column specified with the SQL <code>AS</code> clause. If the SQL <code>AS</code> clause was not specified,
	 *                    then the label is the name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is 0
	 * @throws DatabaseException Thrown if {@link ResultSet#getInt(String)} fails
	 * @see ResultSet#getInt(String)
	 */
	public Long getLong(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			Long result = this.rs.getLong(columnLabel);

			if (this.rs.wasNull())
				result = null;

			return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as a long in the Java programming
	 * language.
	 *
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is 0
	 * @throws DatabaseException Thrown if {@link ResultSet#getInt(int)} fails
	 * @see ResultSet#getInt(int)
	 */
	public long getLong(int columnIndex) throws DatabaseException
	{
		try
		{
			return this.rs.getLong(columnIndex);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as a String in the Java programming
	 * language.
	 *
	 * @param columnLabel the label for the column specified with the SQL <code>AS</code> clause. If the SQL <code>AS</code> clause was not specified,
	 *                    then the label is the name of the column
	 * @return the column value; if the value is SQL NULL, the value returned is <code>null</code>; if the column does not exist, the value returned
	 * is <code>null</code>
	 * @throws DatabaseException Thrown if {@link ResultSet#getString(String)} fails
	 * @see ResultSet#getString(String)
	 */
	public String getString(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			return this.rs.getString(columnLabel);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e + " " + e.getSQLState());
		}
	}

	public java.util.Date getDate(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			return this.rs.getDate(columnLabel);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	public java.util.Date getTimestamp(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			return this.rs.getTimestamp(columnLabel);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as a double in the Java programming
	 * language.
	 *
	 * @param columnLabel the label for the column specified with the SQL <code>AS</code> clause. If the SQL <code>AS</code> clause was not specified,
	 *                    then the label is the name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is 0
	 * @throws DatabaseException Thrown if {@link ResultSet#getDouble(String)} fails
	 * @see ResultSet#getDouble(String)
	 */
	public Double getDouble(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			double result = this.rs.getDouble(columnLabel);

			if (this.rs.wasNull())
				return null;
			else
				return result;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e);
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this {@link DatabaseResult} object as a boolean in the Java programming
	 * language. If the designated column has a datatype of <code>CHAR</code> or <code>VARCHAR</code> and contains a "0" or has a datatype of
	 * <code>BIT</code>, <code>TINYINT</code>, <code>SMALLINT</code>, <code>INTEGER</code> or <code>BIGINT</code> and contains a 0, a value of false
	 * is returned. If the designated column has a datatype of <code>CHAR</code> or <code>VARCHAR</code> and contains a "1" or has a datatype of
	 * <code>BIT</code>, <code>TINYINT</code>, <code>SMALLINT</code> , <code>INTEGER</code> or <code>BIGINT</code> and contains a 1, a value of true
	 * is returned.
	 *
	 * @param columnLabel the label for the column specified with the SQL <code>AS</code> clause. If the SQL <code>AS</code> clause was not specified,
	 *                    then the label is the name of the column
	 * @return the column value; if the value is SQL <code>NULL</code>, the value returned is <code>false</code>
	 * @throws DatabaseException Thrown if {@link ResultSet#getBoolean(String)} fails
	 * @see ResultSet#getBoolean(String)
	 */
	public Boolean getBoolean(String columnLabel) throws DatabaseException
	{
		try
		{
			/* Check if this column exists in the result */
			this.rs.findColumn(columnLabel);
		}
		catch (SQLException e)
		{
			/* If not, fail gracefully */
			return null;
		}

		try
		{
			return this.rs.getBoolean(columnLabel);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			database.close();
			throw new DatabaseException(e + " " + e.getSQLState());
		}
	}

	/**
	 * Get the designated column's name.
	 *
	 * @param index the first column is 1, the second is 2, ...
	 * @return column name
	 * @throws DatabaseException if a database access error occurs
	 */
	public String getColumnName(int index) throws DatabaseException
	{
		if (rsmd != null)
		{
			try
			{
				return rsmd.getColumnLabel(index);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				database.close();
				throw new DatabaseException(e);
			}
		}
		else
		{
			database.close();
			throw new DatabaseException("ResultSetMetaData is not available");
		}
	}

	/**
	 * Gets the number of columns in the {@link ResultSet}
	 *
	 * @return The number of columns in the {@link ResultSet}
	 * @throws DatabaseException if a database access error occurs
	 */
	public int getColumnCount() throws DatabaseException
	{
		if (rsmd != null)
		{
			try
			{
				return rsmd.getColumnCount();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				database.close();
				throw new DatabaseException(e);
			}
		}
		else
		{
			database.close();
			throw new DatabaseException("ResultSetMetaData is not available");
		}
	}

	/**
	 * Gets an array of all the column names in the {@link ResultSet}
	 *
	 * @param columnsToIgnore The column names to ignore
	 * @return An array of all the column names in the {@link ResultSet}
	 * @throws DatabaseException if a database access error occurs
	 */
	public String[] getColumnNames(String... columnsToIgnore) throws DatabaseException
	{
		if (rsmd != null)
		{
			List<String> columnNames = new ArrayList<>();

			for (int col = 1; col <= getColumnCount(); col++)
			{
				columnNames.add(getColumnName(col));
			}

			columnNames.removeAll(Arrays.asList(columnsToIgnore));

			return columnNames.toArray(new String[0]);
		}
		else
		{
			database.close();
			throw new DatabaseException("ResultSetMetaData is not available");
		}
	}

	public void close()
	{
		if (!database.isClosed())
			database.close();
	}

	@Override
	public String toString()
	{
		return rs.toString();
	}
}
