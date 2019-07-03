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
public class Pedigree extends DatabaseObject
{
	private static final long serialVersionUID = -6786321095022860722L;

	public static final String ID                       = "pedigrees.id";
	public static final String GERMINATEBASE_ID         = "pedigrees.germinatebase_id";
	public static final String PARENT_ID                = "pedigrees.parent_id";
	public static final String RELATIONSHIP_TYPE        = "pedigrees.relationship_type";
	public static final String PEDIGREEDESCRIPTION_ID   = "pedigrees.pedigreedescription_id";
	public static final String RELATIONSHIP_DESCRIPTION = "pedigrees.relationship_description";
	public static final String CREATED_ON               = "pedigrees.created_on";
	public static final String UPDATED_ON               = "pedigrees.updated_on";

	private Accession           accession;
	private Accession           parent;
	private String              relationshipType;
	private PedigreeDescription pedigreeDescription;
	private String              relationShipDescription;
	private Long                createdOn;
	private Long                updatedOn;

	public Pedigree()
	{
	}

	public Pedigree(Long id)
	{
		super(id);
	}

	public Accession getAccession()
	{
		return accession;
	}

	public Pedigree setAccession(Accession accession)
	{
		this.accession = accession;
		return this;
	}

	public Accession getParent()
	{
		return parent;
	}

	public Pedigree setParent(Accession parent)
	{
		this.parent = parent;
		return this;
	}

	public String getRelationshipType()
	{
		return relationshipType;
	}

	public Pedigree setRelationshipType(String relationshipType)
	{
		this.relationshipType = relationshipType;
		return this;
	}

	public PedigreeDescription getPedigreeDescription()
	{
		return pedigreeDescription;
	}

	public Pedigree setPedigreeDescription(PedigreeDescription pedigreeDescription)
	{
		this.pedigreeDescription = pedigreeDescription;
		return this;
	}

	public String getRelationShipDescription()
	{
		return relationShipDescription;
	}

	public Pedigree setRelationShipDescription(String relationShipDescription)
	{
		this.relationShipDescription = relationShipDescription;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Pedigree setCreatedOn(Date createdOn)
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

	public Pedigree setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Pedigree>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE},
			 * not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand
			 * holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
			 * <code>synchronized</code>).
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

		private static DatabaseObjectCache<Accession>           ACCESSION_CACHE;
		private static DatabaseObjectCache<PedigreeDescription> PEDIGREEDESCRIPTION_CACHE;

		private Parser()
		{
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
			PEDIGREEDESCRIPTION_CACHE = createCache(PedigreeDescription.class, PedigreeDescriptionManager.class);
		}

		@Override
		public Pedigree parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				try
				{
					return new Pedigree(id)
							.setAccession(ACCESSION_CACHE.get(user, row.getLong(GERMINATEBASE_ID), row, foreignsFromResultSet))
							.setParent(ACCESSION_CACHE.get(user, row.getLong(PARENT_ID), row, foreignsFromResultSet))
							.setRelationshipType(row.getString(RELATIONSHIP_TYPE))
							.setPedigreeDescription(PEDIGREEDESCRIPTION_CACHE.get(user, row.getLong(PEDIGREEDESCRIPTION_ID), row, foreignsFromResultSet))
							.setRelationShipDescription(row.getString(RELATIONSHIP_DESCRIPTION))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));
				}
				catch (InsufficientPermissionsException e)
				{
					return null;
				}
			}
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Pedigree>
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
		public void write(Database database, Pedigree object, boolean isUpdate) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `pedigrees` (" + GERMINATEBASE_ID + ", " + PARENT_ID + ", " + RELATIONSHIP_TYPE + ", " + PEDIGREEDESCRIPTION_ID + ", " + RELATIONSHIP_DESCRIPTION + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?)")
					.setLong(object.getAccession().getId())
					.setLong(object.getParent().getId())
					.setString(object.getRelationshipType())
					.setLong(object.getPedigreeDescription() != null ? object.getPedigreeDescription().getId() : null)
					.setString(object.getRelationShipDescription());

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

	public enum PedigreeQuery
	{
		UP_DOWN(1, 1),
		UP_DOWN_GRANDPARENTS(2, 1),
		UP_DOWN_RECURSIVE(5, 5);

		private int up;
		private int down;

		PedigreeQuery(int up, int down)
		{
			this.up = up;
			this.down = down;
		}

		public int getUp()
		{
			return up;
		}

		public int getDown()
		{
			return down;
		}
	}
}
