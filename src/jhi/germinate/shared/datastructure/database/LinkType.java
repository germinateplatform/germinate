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

import com.google.gwt.core.shared.*;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class LinkType extends DatabaseObject
{
	private static final long serialVersionUID = 4863415808947750663L;

	public static final String ID            = "linktypes.id";
	public static final String DESCRIPTION   = "linktypes.description";
	public static final String TARGET_TABLE  = "linktypes.target_table";
	public static final String TARGET_COLUMN = "linktypes.target_column";
	public static final String PLACEHOLDER   = "linktypes.placeholder";

	private String description;
	private String targetTable;
	private String targetColumn;
	private String placeholder;
	private Long   createdOn;
	private Long   updatedOn;

	public LinkType()
	{
	}

	public LinkType(Long id)
	{
		super(id);
	}

	public String getDescription()
	{
		return description;
	}

	public LinkType setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public String getTargetTable()
	{
		return targetTable;
	}

	public LinkType setTargetTable(String targetTable)
	{
		this.targetTable = targetTable;
		return this;
	}

	public String getTargetColumn()
	{
		return targetColumn;
	}

	public LinkType setTargetColumn(String targetColumn)
	{
		this.targetColumn = targetColumn;
		return this;
	}

	public String getPlaceholder()
	{
		return placeholder;
	}

	public LinkType setPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public LinkType setCreatedOn(Date createdOn)
	{
		if (createdOn == null)
			this.createdOn = null;
		else
			this.createdOn = createdOn.getTime();
		return this;
	}

	public Long getUpdatedOn()
	{
		return updatedOn;
	}

	public LinkType setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<LinkType>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
			 * InstanceHolder#INSTANCE}, not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
			 * >Initialization-on-demand holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
			 * <code>synchronized</code>).
			 *
			 * @author Sebastian Raubach
			 */
			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}

			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		private Parser()
		{
		}

		@Override
		public LinkType parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new LinkType(id)
						.setDescription(row.getString(DESCRIPTION))
						.setTargetTable(row.getString(TARGET_TABLE))
						.setTargetColumn(row.getString(TARGET_COLUMN))
						.setPlaceholder(row.getString(PLACEHOLDER))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));
		}
	}
}
