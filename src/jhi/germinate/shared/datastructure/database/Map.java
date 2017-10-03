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

import java.sql.*;
import java.util.Date;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.database.query.writer.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Map extends DatabaseObject
{
	private static final long serialVersionUID = -2723037593946808092L;

	public static final String ID          = "maps.id";
	public static final String DESCRIPTION = "maps.description";
	public static final String VISIBILITY  = "maps.visibility";
	public static final String USER_ID     = "maps.user_id";

	private String  description;
	private Boolean visibility;
	private Long    userId;
	private Long    createdOn;
	private Long    updatedOn;
	private Long size = 0L;

	public Map()
	{
	}

	public Map(Long id)
	{
		super(id);
	}

	public String getDescription()
	{
		return description;
	}

	public Map setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Boolean isVisibility()
	{
		return visibility;
	}

	public Map setVisibility(Boolean visibility)
	{
		this.visibility = visibility;
		return this;
	}

	public Long getUserId()
	{
		return userId;
	}

	public Map setUserId(Long userId)
	{
		this.userId = userId;
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public Map setSize(Long size)
	{
		this.size = size;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Map setCreatedOn(Date createdOn)
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

	public Map setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Map>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
			 * InstanceHolder#INSTANCE}, not before.
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
		public Map parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				Map map = new Map(id)
						.setDescription(row.getString(DESCRIPTION))
						.setVisibility(row.getBoolean(VISIBILITY))
						.setUserId(row.getLong(USER_ID))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));

				try
				{
					map.setSize(row.getLong(MapManager.COUNT));
				}
				catch (Exception e)
				{
					// Ignore this
				}

				return map;
			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Map>
	{
		public static final class Inst
		{
			private static final class InstanceHolder
			{
				private static final Writer INSTANCE = new Writer();
			}

			public static Writer get()
			{
				return Writer.Inst.InstanceHolder.INSTANCE;
			}
		}

		@Override
		public void write(Database database, Map object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO maps (" + DESCRIPTION + ", " + VISIBILITY + ", " + USER_ID + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?)")
					.setString(object.getDescription())
					.setBoolean(object.isVisibility())
					.setLong(object.getUserId());

			if (object.getCreatedOn() != null)
				query.setTimestamp(new Date(object.getCreatedOn()));
			else
				query.setNull(Types.TIMESTAMP);
			if (object.getUpdatedOn() != null)
				query.setTimestamp(new Date(object.getUpdatedOn()));
			else
				query.setNull(Types.TIMESTAMP);

			ServerResult<List<Long>> ids = query.execute(false);

			if (ids != null && !CollectionUtils.isEmpty(ids.getServerResult()))
				object.setId(ids.getServerResult().get(0));
		}
	}
}
