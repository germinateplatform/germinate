/*
 *  Copyright 2018 Information and Computational Sciences,
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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class UserGroup extends DatabaseObject
{
	public static final String ID          = "usergroups.id";
	public static final String NAME        = "usergroups.name";
	public static final String DESCRIPTION = "usergroups.description";
	public static final String CREATED_ON  = "usergroups.created_on";
	public static final String UPDATED_ON  = "usergroups.updated_on";

	private String name;
	private String description;
	private Long   createdOn;
	private Long   updatedOn;
	private Long   size = 0L;

	public UserGroup()
	{
	}

	public UserGroup(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public UserGroup setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public UserGroup setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public UserGroup setCreatedOn(Date createdOn)
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

	public UserGroup setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public UserGroup setSize(Long size)
	{
		this.size = size;
		return this;
	}

	@Override
	public String toString()
	{
		return "Group{" +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", createdOn=" + createdOn +
				", updatedOn=" + updatedOn +
				", size=" + size +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<UserGroup>
	{
		private Parser()
		{
		}

		@Override
		public UserGroup parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				UserGroup group = new UserGroup(id)
						.setName(row.getString(NAME))
						.setDescription(row.getString(DESCRIPTION))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));

				try
				{
					group.setSize(row.getLong(UserGroupManager.COUNT));
				}
				catch (Exception e)
				{
					// Ignore this
				}

				return group;
			}
		}

		public static final class Inst
		{
			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}

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
		}
	}
}
