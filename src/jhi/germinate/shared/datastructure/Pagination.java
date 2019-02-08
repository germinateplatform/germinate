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

package jhi.germinate.shared.datastructure;

import com.google.gwt.core.shared.*;

import java.io.*;

import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Pagination implements Serializable
{
	private static final long serialVersionUID = -3472389651719345925L;

	private int     start      = 0;
	private int     length     = Integer.MAX_VALUE;
	private String  sortColumn = "";
	private boolean ascending  = true;
	private Integer resultSize = null;

	public static Pagination getDefault()
	{
		return new Pagination();
	}

	public Pagination()
	{
	}

	/**
	 * Creates a new instance of Pagination
	 *
	 * @param start  The start of the chunk of data
	 * @param length The length of the chunk of data
	 */
	public Pagination(int start, int length)
	{
		this.start = start;
		this.length = length;
	}

	/**
	 * Creates a new instance of Pagination
	 *
	 * @param start      The start of the chunk of data
	 * @param length     The length of the chunk of data
	 * @param sortColumn The column to sort by
	 * @param ascending  The sorting get
	 */
	public Pagination(int start, int length, String sortColumn, boolean ascending)
	{
		this.start = start;
		this.length = length;
		this.sortColumn = sortColumn;
		this.ascending = ascending;
	}

	public Pagination(int start, int length, String sortColumn, boolean ascending, Integer resultSize)
	{
		this.start = start;
		this.length = length;
		this.sortColumn = sortColumn;
		this.ascending = ascending;
		this.resultSize = resultSize;
	}

	public Pagination update(int start, int length, String sortColumn, boolean ascending)
	{
		setStart(start);
		setLength(length);
		setSortColumn(sortColumn);
		setAscending(ascending);
		return this;
	}

	public Pagination update(int start, int length)
	{
		setStart(start);
		setLength(length);
		return this;
	}

	public boolean hasSize()
	{
		return resultSize != null;
	}

	public int getStart()
	{
		return start;
	}

	public void setStart(int start)
	{
		this.start = start;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public String getSortColumn()
	{
		return sortColumn;
	}

	public void setSortColumn(String sortColumn)
	{
		this.sortColumn = sortColumn;
	}

	public boolean isAscending()
	{
		return ascending;
	}

	public void setAscending(boolean ascending)
	{
		this.ascending = ascending;
	}

	public Integer getResultSize()
	{
		return resultSize;
	}

	public void setResultSize(Integer resultSize)
	{
		this.resultSize = resultSize;
	}

	public String getSortQuery()
	{
		if (!StringUtils.isEmpty(sortColumn))
			return " ORDER BY " + sortColumn + (ascending ? " ASC " : " DESC ");
		else
			return "";
	}

	@GwtIncompatible
	public void updateSortColumn(String[] columns, String fallback) throws InvalidColumnException
	{
		//		if (!StringUtils.isEmpty(this.sortColumn))
		this.sortColumn = Util.checkSortColumn(sortColumn, columns, fallback);
	}

	@Override
	public String toString()
	{
		return "Pagination{" +
				"start=" + start +
				", length=" + length +
				", sortColumn='" + sortColumn + '\'' +
				", ascending=" + ascending +
				", resultSize=" + resultSize +
				'}';
	}
}
