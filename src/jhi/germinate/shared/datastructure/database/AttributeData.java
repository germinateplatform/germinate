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
public class AttributeData extends DatabaseObject
{
	private static final long serialVersionUID = -6203605805580843637L;

	public static final String ID           = "attributedata.id";
	public static final String ATTRIBUTE_ID = "attributedata.attribute_id";
	public static final String FOREIGN_ID   = "attributedata.foreign_id";
	public static final String VALUE        = "attributedata.value";
	public static final String CREATED_ON   = "attributedata.created_on";
	public static final String UPDATED_ON   = "attributedata.updated_on";

	private Attribute      attribute;
	private DatabaseObject foreign;
	private String         value;
	private Long           createdOn;
	private Long           updatedOn;

	public AttributeData()
	{
	}

	public AttributeData(Long id)
	{
		super(id);
	}

	public Attribute getAttribute()
	{
		return attribute;
	}

	public AttributeData setAttribute(Attribute attribute)
	{
		this.attribute = attribute;
		return this;
	}

	public DatabaseObject getForeign()
	{
		return foreign;
	}

	public AttributeData setForeign(DatabaseObject foreign)
	{
		this.foreign = foreign;
		return this;
	}

	public String getValue()
	{
		return value;
	}

	public AttributeData setValue(String value)
	{
		this.value = value;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public AttributeData setCreatedOn(Date createdOn)
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

	public AttributeData setUpdatedOn(Date updatedOn)
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
		return "AttributeData{" +
				"attribute=" + attribute +
				", foreign=" + foreign +
				", value='" + value + '\'' +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return AccessionParser.Inst.get();
	}

	@GwtIncompatible
	public static class AccessionParser extends DatabaseObjectParser<AttributeData>
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
				private static final AccessionParser INSTANCE = new AccessionParser();
			}

			public static AccessionParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		private static DatabaseObjectCache<Attribute> ATTRIBUTE_CACHE;
		private static DatabaseObjectCache<Accession> ACCESSION_CACHE;

		private AccessionParser()
		{
			ATTRIBUTE_CACHE = createCache(Attribute.class, AttributeManager.class);
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
		}

		@Override
		public AttributeData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new AttributeData(id)
							.setAttribute(ATTRIBUTE_CACHE.get(user, row.getLong(ATTRIBUTE_ID), row, foreignsFromResultSet))
							.setForeign(ACCESSION_CACHE.get(user, row.getLong(FOREIGN_ID), row, foreignsFromResultSet))
							.setValue(row.getString(VALUE))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}

	@GwtIncompatible
	public static class DatasetParser extends DatabaseObjectParser<AttributeData>
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
				private static final DatasetParser INSTANCE = new DatasetParser();
			}

			public static DatasetParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		protected static DatabaseObjectCache<Attribute> ATTRIBUTE_CACHE;
		protected static DatabaseObjectCache<Dataset>   DATASET_CACHE;

		private DatasetParser()
		{
			ATTRIBUTE_CACHE = createCache(Attribute.class, AttributeManager.class);
			DATASET_CACHE = createCache(Dataset.class, DatasetManager.class);
		}

		@Override
		public AttributeData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new AttributeData(id)
							.setAttribute(ATTRIBUTE_CACHE.get(user, row.getLong(ATTRIBUTE_ID), row, foreignsFromResultSet))
							.setForeign(DATASET_CACHE.get(user, row.getLong(FOREIGN_ID), row, foreignsFromResultSet))
							.setValue(row.getString(VALUE))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}

	@GwtIncompatible
	public static class NonRecursiveDatasetParser extends DatasetParser
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
				private static final NonRecursiveDatasetParser INSTANCE = new NonRecursiveDatasetParser();
			}

			public static NonRecursiveDatasetParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		@Override
		public AttributeData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new AttributeData(id)
							.setAttribute(ATTRIBUTE_CACHE.get(user, row.getLong(ATTRIBUTE_ID), row, foreignsFromResultSet))
							.setForeign(new Dataset(row.getLong(FOREIGN_ID)))
							.setValue(row.getString(VALUE))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<AttributeData>
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
		public void write(Database database, AttributeData object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO attributedata (" + ATTRIBUTE_ID + ", " + FOREIGN_ID + ", " + VALUE + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?)")
					.setLong(object.getAttribute().getId())
					.setLong(object.getForeign().getId())
					.setString(object.getValue());

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
