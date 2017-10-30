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
public class Subtaxa extends DatabaseObject
{
	private static final long serialVersionUID = 9113560669657113601L;

	public static final String ID                  = "subtaxa.id";
	public static final String TAXONOMY_ID         = "subtaxa.taxonomy_id";
	public static final String AUTHOR              = "subtaxa.subtaxa_author";
	public static final String TAXONOMY_IDENTIFIER = "subtaxa.taxonomic_identifier";

	private Taxonomy taxonomy;
	private String   author;
	private String   taxonomyIdentifier;
	private Long     createdOn;
	private Long     updatedOn;

	public Subtaxa()
	{
	}

	public Subtaxa(Long id)
	{
		super(id);
	}

	public Taxonomy getTaxonomy()
	{
		return taxonomy;
	}

	public Subtaxa setTaxonomy(Taxonomy taxonomy)
	{
		this.taxonomy = taxonomy;
		return this;
	}

	public String getAuthor()
	{
		return author;
	}

	public Subtaxa setAuthor(String author)
	{
		this.author = author;
		return this;
	}

	public String getTaxonomyIdentifier()
	{
		return taxonomyIdentifier;
	}

	public Subtaxa setTaxonomyIdentifier(String taxonomyIdentifier)
	{
		this.taxonomyIdentifier = taxonomyIdentifier;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Subtaxa setCreatedOn(Date createdOn)
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

	public Subtaxa setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Subtaxa>
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

		private static DatabaseObjectCache<Taxonomy> TAXONOMY_CACHE;

		private Parser()
		{
			TAXONOMY_CACHE = createCache(Taxonomy.class, TaxonomyManager.class);
		}

		@Override
		public Subtaxa parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new Subtaxa(id)
							.setTaxonomy(TAXONOMY_CACHE.get(user, row.getLong(TAXONOMY_ID), row, foreignsFromResultSet))
							.setAuthor(row.getString(AUTHOR))
							.setTaxonomyIdentifier(row.getString(TAXONOMY_IDENTIFIER))
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
	public static class Writer implements DatabaseObjectWriter<Subtaxa>
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
		public void write(Database database, Subtaxa object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO subtaxa (" + TAXONOMY_ID + ", " + AUTHOR + ", " + TAXONOMY_IDENTIFIER + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?)")
					.setLong(object.getTaxonomy() != null ? object.getTaxonomy().getId() : null)
					.setString(object.getAuthor())
					.setString(object.getTaxonomyIdentifier());

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
