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
public class Taxonomy extends DatabaseObject
{
	private static final long serialVersionUID = -235820564023498703L;

	public static final String ID              = "taxonomies.id";
	public static final String GENUS           = "taxonomies.genus";
	public static final String SPECIES         = "taxonomies.species";
	public static final String SUBTAXA         = "taxonomies.subtaxa";
	public static final String TAXONOMY_AUTHOR = "taxonomies.species_author";
	public static final String SUBTAXA_AUTHOR  = "taxonomies.subtaxa_author";
	public static final String CROPNAME        = "taxonomies.cropname";
	public static final String PLOIDY          = "taxonomies.ploidy";
	public static final String CREATED_ON      = "taxonomies.created_on";
	public static final String UPDATED_ON      = "taxonomies.updated_on";

	private String  genus;
	private String  species;
	private String  subtaxa;
	private String  taxonomyAuthor;
	private String  subtaxaAuthor;
	private String  cropName;
	private Integer ploidy;
	private Long    createdOn;
	private Long    updatedOn;

	public Taxonomy()
	{
	}

	public Taxonomy(Long id)
	{
		super(id);
	}

	public String getGenus()
	{
		return genus;
	}

	public Taxonomy setGenus(String genus)
	{
		this.genus = genus;
		return this;
	}

	public String getSpecies()
	{
		return species;
	}

	public Taxonomy setSpecies(String species)
	{
		this.species = species;
		return this;
	}

	public String getSubtaxa()
	{
		return subtaxa;
	}

	public Taxonomy setSubtaxa(String subtaxa)
	{
		this.subtaxa = subtaxa;
		return this;
	}

	public String getTaxonomyAuthor()
	{
		return taxonomyAuthor;
	}

	public Taxonomy setTaxonomyAuthor(String taxonomyAuthor)
	{
		this.taxonomyAuthor = taxonomyAuthor;
		return this;
	}

	public String getSubtaxaAuthor()
	{
		return subtaxaAuthor;
	}

	public Taxonomy setSubtaxaAuthor(String subtaxaAuthor)
	{
		this.subtaxaAuthor = subtaxaAuthor;
		return this;
	}

	public String getCropName()
	{
		return cropName;
	}

	public Taxonomy setCropName(String cropName)
	{
		this.cropName = cropName;
		return this;
	}

	public int getPloidy()
	{
		return ploidy;
	}

	public Taxonomy setPloidy(Integer ploidy)
	{
		this.ploidy = ploidy;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Taxonomy setCreatedOn(Date createdOn)
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

	public Taxonomy setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Taxonomy>
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
		public Taxonomy parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new Taxonomy(id)
						.setGenus(row.getString(GENUS))
						.setSpecies(row.getString(SPECIES))
						.setSubtaxa(row.getString(SUBTAXA))
						.setTaxonomyAuthor(row.getString(TAXONOMY_AUTHOR))
						.setSubtaxaAuthor(row.getString(SUBTAXA_AUTHOR))
						.setCropName(row.getString(CROPNAME))
						.setPloidy(row.getInt(PLOIDY))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Taxonomy>
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
		public void write(Database database, Taxonomy object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `taxonomies` (" + GENUS + ", " + SPECIES + ", " + SUBTAXA + ", " + TAXONOMY_AUTHOR + ", " + SUBTAXA_AUTHOR + ", " + CROPNAME + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
					.setString(object.getGenus())
					.setString(object.getSpecies())
					.setString(object.getSubtaxa())
					.setString(object.getTaxonomyAuthor())
					.setString(object.getSubtaxaAuthor())
					.setString(object.getCropName());

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
