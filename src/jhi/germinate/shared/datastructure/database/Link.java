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
public class Link extends DatabaseObject
{
	private static final long serialVersionUID = 4863415808947750663L;

	public static final String ID          = "links.id";
	public static final String LINKTYPE_ID = "links.linktype_id";
	public static final String FOREIGN_ID  = "links.foreign_id";
	public static final String HYPERLINK   = "links.hyperlink";
	public static final String DESCRIPTION = "links.description";
	public static final String VISIBILITY  = "links.visibility";
	public static final String CREATED_ON  = "links.created_on";
	public static final String UPDATED_ON  = "links.updated_on";

	private LinkType type;
	private Long     foreignId;
	private String   hyperlink;
	private String   description;
	private boolean  isVisible;
	private Long     createdOn;
	private Long     updatedOn;

	public Link()
	{
	}

	public Link(Long id)
	{
		super(id);
	}

	public LinkType getType()
	{
		return type;
	}

	public Link setType(LinkType type)
	{
		this.type = type;
		return this;
	}

	public Long getForeignId()
	{
		return foreignId;
	}

	public Link setForeignId(Long foreignId)
	{
		this.foreignId = foreignId;
		return this;
	}

	public String getHyperlink()
	{
		return hyperlink;
	}

	public Link setHyperlink(String hyperlink)
	{
		this.hyperlink = hyperlink;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Link setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public Link setVisible(boolean isVisible)
	{
		this.isVisible = isVisible;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Link setCreatedOn(Date createdOn)
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

	public Link setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	@Override
	public String toString()
	{
		return "Link{" +
				"type=" + type +
				", foreignId=" + foreignId +
				", hyperlink='" + hyperlink + '\'' +
				", description='" + description + '\'' +
				", isVisible=" + isVisible +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Link>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE},
			 * not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand
			 * holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
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

		private static DatabaseObjectCache<LinkType> LINKTYPE_CACHE;

		private Parser()
		{
			LINKTYPE_CACHE = createCache(LinkType.class, LinkTypeManager.class);
		}

		@Override
		public Link parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				try
				{
					return new Link(id)
							.setForeignId(row.getLong(FOREIGN_ID))
							.setType(LINKTYPE_CACHE.get(user, row.getLong(LINKTYPE_ID), row, foreignsFromResultSet))
							.setHyperlink(row.getString(HYPERLINK))
							.setDescription(row.getString(DESCRIPTION))
							.setVisible(row.getBoolean(VISIBILITY))
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
}
