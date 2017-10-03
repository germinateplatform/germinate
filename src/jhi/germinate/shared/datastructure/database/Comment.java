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
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Comment extends DatabaseObject
{
	private static final long serialVersionUID = -6786321095022860722L;

	public static final String ID             = "comments.id";
	public static final String COMMENTTYPE_ID = "comments.commenttype_id";
	public static final String USER_ID        = "comments.user_id";
	public static final String VISIBILITY     = "comments.visibility";
	public static final String DESCRIPTION    = "comments.description";
	public static final String REFERENCE_ID   = "comments.reference_id";


	private CommentType type;
	private String user = GatekeeperUser.UNKNOWN.getFullName();
	private boolean visibility;
	private String  description;
	private Long    createdBy;
	private Long    referenceId;
	private Long    createdOn;
	private Long    updatedOn;

	public Comment()
	{
	}

	public Comment(Long id)
	{
		super(id);
	}

	public CommentType getType()
	{
		return type;
	}

	public Comment setType(CommentType type)
	{
		this.type = type;
		return this;
	}

	public String getUser()
	{
		return user;
	}

	public Comment setUser(String user)
	{
		this.user = user;
		return this;
	}

	public Comment setUser(GatekeeperUser user)
	{
		this.user = user != null ? user.getFullName() : null;
		this.createdBy = user != null ? user.getId() : null;
		return this;
	}

	public Long getCreatedBy()
	{
		return createdBy;
	}

	public boolean isVisibility()
	{
		return visibility;
	}

	public Comment setVisibility(boolean visibility)
	{
		this.visibility = visibility;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Comment setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Long getReferenceId()
	{
		return referenceId;
	}

	public Comment setReferenceId(Long referenceId)
	{
		this.referenceId = referenceId;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Comment setCreatedOn(Date createdOn)
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

	public Comment setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Comment>
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

		private static DatabaseObjectCache<GatekeeperUser> GATEKEEPER_USER_CACHE;
		private static DatabaseObjectCache<CommentType>    COMMENT_TYPE_CACHE;

		private Parser()
		{
			GATEKEEPER_USER_CACHE = createCache(GatekeeperUser.class, GatekeeperUserManager.class);
			COMMENT_TYPE_CACHE = createCache(CommentType.class, CommentTypeManager.class);
		}

		@Override
		public Comment parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
				{
					return null;
				}
				else
				{
					Comment news = new Comment(id)
							.setType(COMMENT_TYPE_CACHE.get(user, row.getLong(COMMENTTYPE_ID), row, foreignsFromResultSet))
							.setVisibility(row.getBoolean(VISIBILITY))
							.setDescription(row.getString(DESCRIPTION))
							.setReferenceId(row.getLong(REFERENCE_ID))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					try
					{
						news.setUser(GATEKEEPER_USER_CACHE.get(user, row.getLong(USER_ID), row, false));
					}
					catch (Exception e)
					{
						/* Do nothing here */
						e.printStackTrace();
					}

					return news;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}
}
