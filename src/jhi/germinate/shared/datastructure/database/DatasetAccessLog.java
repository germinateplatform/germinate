/*
 *  Copyright 2018 Information and Computational Sciences,
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
public class DatasetAccessLog extends DatabaseObject
{
	public static final String ID               = "datasetaccesslogs.id";
	public static final String USER_ID          = "datasetaccesslogs.user_id";
	public static final String USER_NAME        = "datasetaccesslogs.user_name";
	public static final String USER_EMAIL       = "datasetaccesslogs.user_email";
	public static final String USER_INSTITUTION = "datasetaccesslogs.user_institution";
	public static final String DATASET_ID       = "datasetaccesslogs.dataset_id";
	public static final String REASON           = "datasetaccesslogs.reason";
	public static final String CREATED_ON       = "datasetaccesslogs.created_on";
	public static final String UPDATED_ON       = "datasetaccesslogs.updated_on";
	private static final long serialVersionUID = -6786321095022860722L;
	private UnapprovedUser user;
	private Dataset        dataset;
	private String         reason;
	private Long           createdOn;
	private Long           updatedOn;

	public DatasetAccessLog()
	{
	}

	public DatasetAccessLog(Long id)
	{
		super(id);
	}

	public UnapprovedUser getUser()
	{
		return user;
	}

	public DatasetAccessLog setUser(UnapprovedUser user)
	{
		this.user = user;
		return this;
	}

	public Dataset getDataset()
	{
		return dataset;
	}

	public DatasetAccessLog setDataset(Dataset dataset)
	{
		this.dataset = dataset;
		return this;
	}

	public String getReason()
	{
		return reason;
	}

	public DatasetAccessLog setReason(String reason)
	{
		this.reason = reason;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public DatasetAccessLog setCreatedOn(Date createdOn)
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

	public DatasetAccessLog setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<DatasetAccessLog>
	{
		private static DatabaseObjectCache<GatekeeperUser> GATEKEEPER_USER_CACHE;
		private static DatabaseObjectCache<Dataset>        DATASET_CACHE;
		private Parser()
		{
			GATEKEEPER_USER_CACHE = createCache(GatekeeperUser.class, GatekeeperUserManager.class);
			DATASET_CACHE = createCache(Dataset.class, DatasetManager.class);
		}

		@Override
		public DatasetAccessLog parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
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
					DatasetAccessLog log = new DatasetAccessLog(id)
							.setDataset(DATASET_CACHE.get(user, row.getLong(DATASET_ID), row, false))
							.setReason(row.getString(REASON))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					UnapprovedUser u = new UnapprovedUser();

					try
					{
						GatekeeperUser gatekeeper = GATEKEEPER_USER_CACHE.get(user, row.getLong(USER_ID), row, false);

						u.id = gatekeeper.getId();
						u.userFullName = gatekeeper.getFullName();
						u.userEmailAddress = gatekeeper.getEmail();

						if (gatekeeper.getInstitution() != null)
							u.institutionName = gatekeeper.getInstitution().getName();
					}
					catch (Exception e)
					{
						/* Do nothing here */
						e.printStackTrace();

						u.id = null;
						u.userFullName = row.getString(USER_NAME);
						u.userEmailAddress = row.getString(USER_EMAIL);
						u.institutionName = row.getString(USER_INSTITUTION);
					}

					log.setUser(u);

					return log;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		public static final class Inst
		{
			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}

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
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<DatasetAccessLog>
	{
		@Override
		public void write(Database database, DatasetAccessLog object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO datasetaccesslogs (" + USER_ID + ", " + USER_NAME + ", " + USER_EMAIL + ", " + USER_INSTITUTION + ", " + DATASET_ID + ", " + REASON + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

			query.setLong(object.getUser().id)
				 .setString(object.getUser().userFullName)
				 .setString(object.getUser().userEmailAddress)
				 .setString(object.getUser().institutionName)
				 .setLong(object.getDataset().getId())
				 .setString(object.getReason());

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
