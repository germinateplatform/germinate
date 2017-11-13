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

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Image extends DatabaseObject
{
	private static final long serialVersionUID = 551323544290262794L;

	public static final String ID           = "images.id";
	public static final String IMAGETYPE_ID = "images.imagetype_id";
	public static final String DESCRIPTION  = "images.description";
	public static final String FOREIGN_ID   = "images.foreign_id";
	public static final String PATH         = "images.path";
	public static final String CREATED_ON   = "images.created_on";
	public static final String UPDATED_ON   = "images.updated_on";

	private ImageType type;
	private String    description;
	private Long      foreignId;
	private String    path;
	private int width  = 0;
	private int height = 0;
	private Long createdOn;
	private Long updatedOn;

	public Image()
	{
	}

	public Image(Long id)
	{
		super(id);
	}

	public ImageType getType()
	{
		return type;
	}

	public Image setType(ImageType type)
	{
		this.type = type;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Image setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Long getForeignId()
	{
		return foreignId;
	}

	public Image setForeignId(Long foreignId)
	{
		this.foreignId = foreignId;
		return this;
	}

	public String getPath()
	{
		return path;
	}

	public Image setPath(String path)
	{
		this.path = path;
		return this;
	}

	public int getWidth()
	{
		return width;
	}

	public Image setWidth(int width)
	{
		this.width = width;
		return this;
	}

	public int getHeight()
	{
		return height;
	}

	public Image setHeight(int height)
	{
		this.height = height;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Image setCreatedOn(Date createdOn)
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

	public Image setUpdatedOn(Date updatedOn)
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
		return "Image{" +
				"id=" + id +
				", type=" + type +
				", description='" + description + '\'' +
				", foreignId=" + foreignId +
				", path='" + path + '\'' +
				", width=" + width +
				", height=" + height +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Image>
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

		private static DatabaseObjectCache<ImageType> IMAGETYPE_CACHE;

		private Parser()
		{
			IMAGETYPE_CACHE = createCache(ImageType.class, ImageTypeManager.class);
		}

		@Override
		public Image parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
				{
					Image image = new Image(id)
							.setType(IMAGETYPE_CACHE.get(user, row.getLong(IMAGETYPE_ID), row, foreignsFromResultSet))
							.setDescription(row.getString(DESCRIPTION))
							.setForeignId(row.getLong(FOREIGN_ID))
							.setPath(row.getString(PATH))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					try
					{
						image.setExtra(ImageService.IMAGE_REFERENCE_NAME, row.getString(ImageService.IMAGE_REFERENCE_NAME));
					}
					catch (Exception e)
					{
					}

					return image;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}
}
