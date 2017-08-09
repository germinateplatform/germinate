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
public class News extends DatabaseObject
{
	private static final long serialVersionUID = -1050621532224426332L;

	public static final String ID          = "news.id";
	public static final String NEWSTYPE_ID = "news.newstype_id";
	public static final String TITLE       = "news.title";
	public static final String CONTENT     = "news.content";
	public static final String IMAGE       = "news.image";
	public static final String HYPERLINK   = "news.hyperlink";
	public static final String USER_ID     = "news.user_id";

	private NewsType type;
	private String   title;
	private String   content;
	private String   image;
	private String   hyperlink;
	private String user = GatekeeperUser.UNKNOWN.getFullName();
	private Long createdBy;
	private Long createdOn;
	private Long updatedOn;

	public News()
	{
	}

	public News(Long id)
	{
		super(id);
	}

	public News(Long id, NewsType type, String title, String content, String image, String hyperlink, String user, Long createdOn, Long updatedOn)
	{
		super(id);
		this.type = type;
		this.title = title;
		this.content = content;
		this.image = image;
		this.hyperlink = hyperlink;
		this.user = user;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public NewsType getType()
	{
		return type;
	}

	public News setType(NewsType type)
	{
		this.type = type;
		return this;
	}

	public String getTitle()
	{
		return title;
	}

	public News setTitle(String title)
	{
		this.title = title;
		return this;
	}

	public String getContent()
	{
		return content;
	}

	public News setContent(String content)
	{
		this.content = content;
		return this;
	}

	public String getImage()
	{
		return image;
	}

	public News setImage(String image)
	{
		this.image = image;
		return this;
	}

	public String getHyperlink()
	{
		return hyperlink;
	}

	public News setHyperlink(String hyperlink)
	{
		this.hyperlink = hyperlink;
		return this;
	}

	public String getUser()
	{
		return user;
	}

	public News setUser(GatekeeperUser user)
	{
		this.user = user != null ? user.getFullName() : null;
		this.createdBy = user != null ? user.getId() : null;
		return this;
	}

	public Long getCreatedBy()
	{
		return createdBy;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public News setCreatedOn(Date createdOn)
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

	public News setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<News>
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

		private Parser()
		{
			GATEKEEPER_USER_CACHE = createCache(GatekeeperUser.class, GatekeeperUserManager.class);
		}

		@Override
		public News parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
			{
				return null;
			}
			else
			{
				News news = new News(id)
						.setType(NewsType.getById(row.getLong(NEWSTYPE_ID)))
						.setTitle(row.getString(TITLE))
						.setContent(row.getString(CONTENT))
						.setImage(row.getString(IMAGE))
						.setHyperlink(row.getString(HYPERLINK))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));

				try
				{
					GatekeeperUser u = GATEKEEPER_USER_CACHE.get(user, row.getLong(USER_ID), row, foreignsFromResultSet);

					if (u != null)
						news.setUser(u);
				}
				catch (Exception e)
				{
					/* Do nothing here */
				}

				return news;
			}
		}
	}
}