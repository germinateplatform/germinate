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
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Attribute extends DatabaseObject
{
	private static final long serialVersionUID = -6786321095022860722L;

	public static final String ID           = "attributes.id";
	public static final String NAME         = "attributes.name";
	public static final String DESCRIPTION  = "attributes.description";
	public static final String DATA_TYPE    = "attributes.datatype";
	public static final String TARGET_TABLE = "attributes.target_table";
	public static final String CREATED_ON   = "attributes.created_on";
	public static final String UPDATED_ON   = "attributes.updated_on";

	private String name;
	private String description;
	private String dataType;
	private String targetTable;
	private Long   createdOn;
	private Long   updatedOn;

	public Attribute()
	{
	}

	public Attribute(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public Attribute setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Attribute setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public String getDataType()
	{
		return dataType;
	}

	public Attribute setDataType(String dataType)
	{
		this.dataType = dataType;
		return this;
	}

	public String getTargetTable()
	{
		return targetTable;
	}

	public Attribute setTargetTable(String targetTable)
	{
		this.targetTable = targetTable;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Attribute setCreatedOn(Date createdOn)
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

	public Attribute setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Attribute>
	{
		public static final class Inst
		{
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
		public Attribute parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new Attribute(id)
						.setName(row.getString(NAME))
						.setDescription(row.getString(DESCRIPTION))
						.setDataType(row.getString(DATA_TYPE))
						.setTargetTable(row.getString(TARGET_TABLE))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Attribute>
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
		public void write(Database database, Attribute object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO attributes (" + NAME + ", " + DESCRIPTION + ", " + DATA_TYPE + ", " + DATA_TYPE + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?)")
					.setString(object.getName())
					.setString(object.getDescription())
					.setString(object.getDataType())
					.setString(GerminateDatabaseTable.germinatebase.name());

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
