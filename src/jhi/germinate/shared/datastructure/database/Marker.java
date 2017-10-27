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
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Marker extends DatabaseObject
{
	private static final long serialVersionUID = -6527842511797276823L;

	public static final String ID            = "markers.id";
	public static final String MARKERTYPE_ID = "markers.markertype_id";
	public static final String MARKER_NAME   = "markers.marker_name";

	public static final String SYNONYMS = "synonyms";

	private MarkerType type;
	private String     name;
	private String     synonyms;
	private Long       createdOn;
	private Long       updatedOn;

	public Marker()
	{
	}

	public Marker(Long id)
	{
		super(id);
	}

	public MarkerType getType()
	{
		return type;
	}

	public Marker setType(MarkerType type)
	{
		this.type = type;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Marker setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getSynonyms()
	{
		return synonyms;
	}

	public Marker setSynonyms(String synonyms)
	{
		this.synonyms = synonyms;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Marker setCreatedOn(Date createdOn)
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

	public Marker setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Marker>
	{
		@Override
		public Marker parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new Marker(id)
							.setType(MARKERTYPE_CACHE.get(user, row.getLong(MARKERTYPE_ID), row, foreignsFromResultSet))
							.setName(row.getString(MARKER_NAME))
							.setSynonyms(row.getString(SYNONYMS))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		private static DatabaseObjectCache<MarkerType> MARKERTYPE_CACHE;

		private Parser()
		{
			MARKERTYPE_CACHE = createCache(MarkerType.class, MarkerTypeManager.class);
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

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Marker>
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
		public void write(Database database, Marker object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO markers (" + MARKER_NAME + ", " + MARKERTYPE_ID + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?)")
					.setString(object.getName())
					.setLong(object.getType().getId());

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

		public void writeBatched(DatabaseStatement stmt, Marker object) throws DatabaseException
		{
			int i = 1;
			stmt.setString(i++, object.getName());
			stmt.setLong(i++, object.getType().getId());

			if (object.getCreatedOn() != null)
				stmt.setTimestamp(i++, new Date(object.getCreatedOn()));
			else
				stmt.setNull(i++, Types.TIMESTAMP);
			if (object.getUpdatedOn() != null)
				stmt.setTimestamp(i++, new Date(object.getUpdatedOn()));
			else
				stmt.setNull(i++, Types.TIMESTAMP);

			stmt.addBatch();
		}
	}
}
