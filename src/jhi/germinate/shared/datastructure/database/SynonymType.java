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
public class SynonymType extends DatabaseObject
{
	private static final long serialVersionUID = 4591661116753716031L;

	public static final String ID              = "synonymtypes.id";
	public static final String REFERENCE_TABLE = "synonymtypes.target_table";
	public static final String NAME            = "synonymtypes.name";
	public static final String DESCRIPTION     = "synonymtypes.description";
	public static final String CREATED_ON      = "synonymtypes.created_on";
	public static final String UPDATED_ON      = "synonymtypes.updated_on";

	private GerminateDatabaseTable targetTable;
	private String                 name;
	private String                 description;
	private Long                   createdOn;
	private Long                   updatedOn;

	public SynonymType()
	{
	}

	public SynonymType(Long id)
	{
		super(id);
	}

	public GerminateDatabaseTable getTargetTable()
	{
		return targetTable;
	}

	public SynonymType setTargetTable(GerminateDatabaseTable targetTable)
	{
		this.targetTable = targetTable;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public SynonymType setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public SynonymType setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public SynonymType setCreatedOn(Date createdOn)
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

	public SynonymType setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<SynonymType>
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
		public SynonymType parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new SynonymType(id)
							.setTargetTable(GerminateDatabaseTable.valueOf(row.getString(REFERENCE_TABLE)))
							.setName(row.getString(NAME))
							.setDescription(row.getString(DESCRIPTION))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (IllegalArgumentException e)
			{
				return null;
			}
		}
	}
}
