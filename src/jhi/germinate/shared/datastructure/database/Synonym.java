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

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
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

		private static DatabaseObjectCache<SynonymType> SYNONYM_TYPE_CACHE;

		private Parser()
		{
			SYNONYM_TYPE_CACHE = createCache(SynonymType.class, SynonymTypeManager.class);
		}

		@Override
		public Synonym parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
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
					return new Synonym(id)
							.setForeignId(row.getLong(FOREIGN_ID))
							.setType(SYNONYM_TYPE_CACHE.get(user, row.getLong(SYNONYMTYPE_ID), row, foreignsFromResultSet))
							.setSynonym(row.getString(SYNONYM))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}
}
