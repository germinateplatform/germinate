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
public class Group extends DatabaseObject
{
	private static final long serialVersionUID = 344654284944184140L;

	public static final String ID            = "groups.id";
	public static final String NAME          = "groups.name";
	public static final String DESCRIPTION   = "groups.description";
	public static final String GROUP_TYPE_ID = "groups.grouptype_id";
	public static final String VISIBILITY    = "groups.visibility";
	public static final String CREATED_BY    = "groups.created_by";
	public static final String CREATED_ON    = "groups.created_on";
	public static final String UPDATED_ON    = "groups.updated_on";

	private GroupType type;
	private String    name;
	private String    description;
	private boolean   visibility;
	private Long      createdBy;
	private String user = GatekeeperUser.UNKNOWN.getFullName();
	private Long createdOn;
	private Long updatedOn;
	private Long size = 0L;

	public Group()
	{
	}

	public Group(Long id)
	{
		super(id);
	}

	public GroupType getType()
	{
		return type;
	}

	public Group setType(GroupType type)
	{
		this.type = type;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Group setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Group setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public boolean getVisibility()
	{
		return visibility;
	}

	public Group setVisibility(boolean visibility)
	{
		this.visibility = visibility;
		return this;
	}

	public Group setUser(GatekeeperUser user)
	{
		this.user = user != null ? user.getFullName() : null;
		this.createdBy = user != null ? user.getId() : null;
		return this;
	}

	public String getUser()
	{
		return user;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Group setCreatedOn(Date createdOn)
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

	public Group setUpdatedOn(Date updatedOn)
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

	public Group setSize(Long size)
	{
		this.size = size;
		return this;
	}

	public Long getCreatedBy()
	{
		return createdBy;
	}

	@Override
	public String toString()
	{
		return "Group{" +
				"type=" + type +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", visibility=" + visibility +
				", createdBy=" + createdBy +
				", user='" + user + '\'' +
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
	public static class Parser extends DatabaseObjectParser<Group>
	{
		@Override
		public Group parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
				{
					Group group = new Group(id)
							.setName(row.getString(NAME))
							.setDescription(row.getString(DESCRIPTION))
							.setType(GROUPTYPE_CACHE.get(user, row.getLong(GROUP_TYPE_ID), row, foreignsFromResultSet))
							.setVisibility(row.getBoolean(VISIBILITY))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					try
					{
						group.setSize(row.getLong(GroupManager.COUNT));
					}
					catch (Exception e)
					{
						// Ignore this
					}

					try
					{
						GatekeeperUser u = GATEKEEPER_USER_CACHE.get(user, row.getLong(CREATED_BY), row, false);

						if (u != null)
							group.setUser(u);
					}
					catch (Exception e)
					{
					/* Do nothing here */
					}

					return group;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		private static DatabaseObjectCache<GroupType>      GROUPTYPE_CACHE;
		private static DatabaseObjectCache<GatekeeperUser> GATEKEEPER_USER_CACHE;

		private Parser()
		{
			GROUPTYPE_CACHE = createCache(GroupType.class, GroupTypeManager.class);
			GATEKEEPER_USER_CACHE = createCache(GatekeeperUser.class, GatekeeperUserManager.class);
		}

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
	}
}
