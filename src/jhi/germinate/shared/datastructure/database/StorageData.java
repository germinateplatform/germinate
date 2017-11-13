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
public class StorageData extends DatabaseObject
{
	private static final long serialVersionUID = -3446227106496704279L;

	public static final String ID               = "storagedata.id";
	public static final String GERMINATEBASE_ID = "storagedata.germinatebase_id";
	public static final String STORAGE_ID       = "storagedata.storage_id";
	public static final String CREATED_ON       = "phenotypedata.created_on";
	public static final String UPDATED_ON       = "phenotypedata.updated_on";

	private Accession accession;
	private Storage   storage;
	private Long   createdOn;
	private Long   updatedOn;

	public StorageData()
	{
	}

	public StorageData(Long id)
	{
		super(id);
	}

	public Accession getAccession()
	{
		return accession;
	}

	public StorageData setAccession(Accession accession)
	{
		this.accession = accession;
		return this;
	}

	public Storage getStorage()
	{
		return storage;
	}

	public StorageData setStorage(Storage storage)
	{
		this.storage = storage;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public StorageData setCreatedOn(Date createdOn)
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

	public StorageData setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<StorageData>
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

		private static DatabaseObjectCache<Accession> ACCESSION_CACHE;
		private static DatabaseObjectCache<Storage>   STORAGE_CACHE;

		private Parser()
		{
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
			STORAGE_CACHE = createCache(Storage.class, StorageManager.class);
		}

		@Override
		public StorageData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new StorageData(id)
							.setAccession(ACCESSION_CACHE.get(user, row.getLong(GERMINATEBASE_ID), row, foreignsFromResultSet))
							.setStorage(STORAGE_CACHE.get(user, row.getLong(STORAGE_ID), row, foreignsFromResultSet))
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
	public static class Writer implements DatabaseObjectWriter<StorageData>
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
		public void write(Database database, StorageData object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO storagedata (" + GERMINATEBASE_ID + ", " + STORAGE_ID + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?)")
					.setLong(object.getAccession().getId())
					.setLong(object.getStorage().getId());

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
