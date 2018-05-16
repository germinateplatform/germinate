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
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Climate extends DatabaseObject
{
	private static final long serialVersionUID = 383772881131720309L;

	public static final String ID          = "climates.id";
	public static final String NAME        = "climates.name";
	public static final String SHORT_NAME  = "climates.short_name";
	public static final String DESCRIPTION = "climates.description";
	public static final String DATA_TYPE   = "climates.datatype";
	public static final String UNIT_ID     = "climates.unit_id";
	public static final String CREATED_ON  = "climates.created_on";
	public static final String UPDATED_ON  = "climates.updated_on";

	private String name;
	private String shortName;
	private String description;
	private String dataType;
	private Unit   unit;
	private Long   createdOn;
	private Long   updatedOn;

	public Climate()
	{
	}

	public Climate(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public Climate setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getShortName()
	{
		return shortName;
	}

	public Climate setShortName(String shortName)
	{
		this.shortName = shortName;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Climate setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public String getDataType()
	{
		return dataType;
	}

	public Climate setDataType(String dataType)
	{
		this.dataType = dataType;
		return this;
	}

	public Unit getUnit()
	{
		return unit;
	}

	public Climate setUnit(Unit unit)
	{
		this.unit = unit;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Climate setCreatedOn(Date createdOn)
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

	public Climate setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Climate>
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

		private static DatabaseObjectCache<Unit> UNIT_CACHE;

		private Parser()
		{
			UNIT_CACHE = createCache(Unit.class, UnitManager.class);
		}

		@Override
		public Climate parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new Climate(id)
							.setName(row.getString(NAME))
							.setShortName(row.getString(SHORT_NAME))
							.setDescription(row.getString(DESCRIPTION))
							.setDataType(row.getString(DATA_TYPE))
							.setUnit(UNIT_CACHE.get(user, row.getLong(UNIT_ID), row, foreignsFromResultSet))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}
}
