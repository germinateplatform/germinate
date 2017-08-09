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

	private Accession accession;
	private Storage   storage;

	public StorageData()
	{
	}

	public StorageData(Long id)
	{
		super(id);
	}

	public StorageData(Long id, Accession accession, Storage storage)
	{
		super(id);
		this.accession = accession;
		this.storage = storage;
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
							.setStorage(STORAGE_CACHE.get(user, row.getLong(STORAGE_ID), row, foreignsFromResultSet));
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
			ValueQuery query = new ValueQuery(database, "INSERT INTO storagedata (" + GERMINATEBASE_ID + ", " + STORAGE_ID + ") VALUES (?, ?)")
					.setLong(object.getAccession().getId())
					.setLong(object.getStorage().getId());

			ServerResult<List<Long>> ids = query.execute(false);

			if (ids != null && !CollectionUtils.isEmpty(ids.getServerResult()))
				object.setId(ids.getServerResult().get(0));
		}
	}
}
