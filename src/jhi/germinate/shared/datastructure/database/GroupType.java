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

package jhi.germinate.shared.datastructure.database;

import com.google.gwt.core.shared.*;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class GroupType extends DatabaseObject
{
	private static final long serialVersionUID = -7372321105208083617L;

	public static final String ID           = "grouptypes.id";
	public static final String DESCRIPTION  = "grouptypes.description";
	public static final String TARGET_TABLE = "grouptypes.target_table";
	public static final String CREATED_ON   = "grouptypes.created_on";
	public static final String UPDATED_ON   = "grouptypes.updated_on";

	private String                 description;
	private GerminateDatabaseTable targetTable;
	private Long                   createdOn;
	private Long                   updatedOn;

	public GroupType()
	{
	}

	public GroupType(Long id)
	{
		super(id);
	}

	public String getDescription()
	{
		return description;
	}

	public GroupType setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public GerminateDatabaseTable getTargetTable()
	{
		return targetTable;
	}

	public GroupType setTargetTable(GerminateDatabaseTable targetTable)
	{
		this.targetTable = targetTable;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public GroupType setCreatedOn(Date createdOn)
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

	public GroupType setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<GroupType>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE},
			 * not before.
			 * <p/>
			 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
			 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
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
		public GroupType parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new GroupType(id)
						.setDescription(row.getString(DESCRIPTION))
						.setTargetTable(GerminateDatabaseTable.valueOf(row.getString(TARGET_TABLE)))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));
		}
	}
}
