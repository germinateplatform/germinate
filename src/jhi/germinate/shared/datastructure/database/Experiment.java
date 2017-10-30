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
public class Experiment extends DatabaseObject
{
	private static final long serialVersionUID = 2105101159915189682L;

	public static final String ID                 = "experiments.id";
	public static final String EXPERIMENT_NAME    = "experiments.experiment_name";
	public static final String USER_ID            = "experiments.user_id";
	public static final String DESCRIPTION        = "experiments.description";
	public static final String EXPERIMENT_DATE    = "experiments.experiment_date";
	public static final String EXPERIMENT_TYPE_ID = "experiments.experiment_type_id";

	private String         name;
	private Long           userId;
	private String         description;
	private Date           date;
	private ExperimentType type;
	private Long           createdOn;
	private Long           updatedOn;

	public Experiment()
	{
	}

	public Experiment(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public Experiment setName(String name)
	{
		this.name = name;
		return this;
	}

	public Long getUserId()
	{
		return userId;
	}

	public Experiment setUserId(Long userId)
	{
		this.userId = userId;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public Experiment setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Date getDate()
	{
		return date;
	}

	public Experiment setDate(Date date)
	{
		this.date = date;
		return this;
	}

	public ExperimentType getType()
	{
		return type;
	}

	public Experiment setType(ExperimentType type)
	{
		this.type = type;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Experiment setCreatedOn(Date createdOn)
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

	public Experiment setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Experiment>
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

		private Parser()
		{
		}

		@Override
		public Experiment parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new Experiment(id)
						.setName(row.getString(EXPERIMENT_NAME))
						.setUserId(row.getLong(USER_ID))
						.setDescription(row.getString(DESCRIPTION))
						.setDate(row.getDate(EXPERIMENT_DATE))
						.setType(ExperimentType.getById(row.getLong(EXPERIMENT_TYPE_ID)))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Experiment>
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
		public void write(Database database, Experiment object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO experiments (" + EXPERIMENT_NAME + ", " + USER_ID + ", " + DESCRIPTION + ", " + EXPERIMENT_DATE + ", " + EXPERIMENT_TYPE_ID + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?)")
					.setString(object.getName())
					.setLong(object.getUserId())
					.setString(object.getDescription())
					.setDate(object.getDate())
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
	}
}
