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
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Synonym extends DatabaseObject
{
	private static final long serialVersionUID = -6201911253666472293L;

	public static final String ID             = "synonyms.id";
	public static final String FOREIGN_ID     = "synonyms.foreign_id";
	public static final String SYNONYMTYPE_ID = "synonyms.synonymtype_id";
	public static final String SYNONYM        = "synonyms.synonym";
	public static final String CREATED_ON     = "synonyms.created_on";
	public static final String UPDATED_ON     = "synonyms.updated_on";

	private Long        foreignId;
	private SynonymType type;
	private String      synonym;
	private Long        createdOn;
	private Long        updatedOn;

	public Synonym()
	{
	}

	public Synonym(Long id)
	{
		super(id);
	}

	public Long getForeignId()
	{
		return foreignId;
	}

	public Synonym setForeignId(Long foreignId)
	{
		this.foreignId = foreignId;
		return this;
	}

	public SynonymType getType()
	{
		return type;
	}

	public Synonym setType(SynonymType type)
	{
		this.type = type;
		return this;
	}

	public String getSynonym()
	{
		return synonym;
	}

	public Synonym setSynonym(String synonym)
	{
		this.synonym = synonym;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Synonym setCreatedOn(Date createdOn)
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

	public Synonym setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Synonym>
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

		private Parser(){
		}

		@Override
		public Synonym parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
			{
				return null;
			}
			else
			{
				return new Synonym(id)
						.setForeignId(row.getLong(FOREIGN_ID))
						.setType(SynonymType.getById(row.getLong(SYNONYMTYPE_ID)))
						.setSynonym(row.getString(SYNONYM))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));

			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Synonym>
	{
		@Override
		public void write(Database database, Synonym object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO synonyms (" + FOREIGN_ID + ", " + SYNONYMTYPE_ID + ", " + SYNONYM + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?)")
					.setLong(object.getForeignId())
					.setLong(object.getType().getId())
					.setString(object.getSynonym());

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

		public static final class Inst
		{
			public static Writer get()
			{
				return Writer.Inst.InstanceHolder.INSTANCE;
			}

			private static final class InstanceHolder
			{
				private static final Writer INSTANCE = new Writer();
			}
		}
	}
}
