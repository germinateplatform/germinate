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
public class MapDefinition extends DatabaseObject
{
	private static final long serialVersionUID = -1042648883748200628L;

	public static final String ID                = "mapdefinitions.id";
	public static final String MAPFEATURETYPE_ID = "mapdefinitions.mapfeaturetype_id";
	public static final String MARKER_ID         = "mapdefinitions.marker_id";
	public static final String MAP_ID            = "mapdefinitions.map_id";
	public static final String DEFINITION_START  = "mapdefinitions.definition_start";
	public static final String DEFINITION_END    = "mapdefinitions.definition_end";
	public static final String CHROMOSOME        = "mapdefinitions.chromosome";
	public static final String ARM_IMPUTE        = "mapdefinitions.arm_impute";
	public static final String CREATED_ON        = "mapdefinitions.created_on";
	public static final String UPDATED_ON        = "mapdefinitions.updated_on";


	private MapFeatureType type;
	private Marker         marker;
	private Map            map;
	private Double         definitionStart;
	private Double         definitionEnd;
	private String         chromosome;
	private String         armImpute;
	private Long           createdOn;
	private Long           updatedOn;

	public MapDefinition()
	{
	}

	public MapDefinition(Long id)
	{
		super(id);
	}

	public MapFeatureType getType()
	{
		return type;
	}

	public MapDefinition setType(MapFeatureType type)
	{
		this.type = type;
		return this;
	}

	public Marker getMarker()
	{
		return marker;
	}

	public MapDefinition setMarker(Marker marker)
	{
		this.marker = marker;
		return this;
	}

	public Map getMap()
	{
		return map;
	}

	public MapDefinition setMap(Map map)
	{
		this.map = map;
		return this;
	}

	public Double getDefinitionStart()
	{
		return definitionStart;
	}

	public MapDefinition setDefinitionStart(Double definitionStart)
	{
		this.definitionStart = definitionStart;
		return this;
	}

	public MapDefinition setDefinitionStart(String definitionStart)
	{
		try
		{
			this.definitionStart = Double.parseDouble(definitionStart);
		}
		catch (Exception e)
		{
		}

		return this;
	}

	public Double getDefinitionEnd()
	{
		return definitionEnd;
	}

	public MapDefinition setDefinitionEnd(Double definitionEnd)
	{
		this.definitionEnd = definitionEnd;
		return this;
	}

	public MapDefinition setDefinitionEnd(String definitionEnd)
	{
		try
		{
			this.definitionEnd = Double.parseDouble(definitionEnd);
		}
		catch (Exception e)
		{
		}

		return this;
	}

	public String getChromosome()
	{
		return chromosome;
	}

	public MapDefinition setChromosome(String chromosome)
	{
		this.chromosome = chromosome;
		return this;
	}

	public String getArmImpute()
	{
		return armImpute;
	}

	public MapDefinition setArmImpute(String armImpute)
	{
		this.armImpute = armImpute;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public MapDefinition setCreatedOn(Date createdOn)
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

	public MapDefinition setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<MapDefinition>
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

		private static DatabaseObjectCache<MapFeatureType> MAPFEATURETYPE_CACHE;
		private static DatabaseObjectCache<Marker>         MARKER_CACHE;
		private static DatabaseObjectCache<Map>            MAP_CACHE;

		private Parser()
		{
			MAPFEATURETYPE_CACHE = createCache(MapFeatureType.class, MapFeatureTypeManager.class);
			MARKER_CACHE = createCache(Marker.class, MarkerManager.class);
			MAP_CACHE = createCache(Map.class, MapManager.class);
		}

		@Override
		public MapDefinition parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new MapDefinition(id)
							.setType(MAPFEATURETYPE_CACHE.get(user, row.getLong(MAPFEATURETYPE_ID), row, foreignsFromResultSet))
							.setMarker(MARKER_CACHE.get(user, row.getLong(MARKER_ID), row, foreignsFromResultSet))
							.setMap(MAP_CACHE.get(user, row.getLong(MAP_ID), row, foreignsFromResultSet))
							.setDefinitionStart(row.getDouble(DEFINITION_START))
							.setDefinitionEnd(row.getDouble(DEFINITION_END))
							.setChromosome(row.getString(CHROMOSOME))
							.setArmImpute(row.getString(ARM_IMPUTE))
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
	public static class Writer implements DatabaseObjectWriter<MapDefinition>
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
		public void write(Database database, MapDefinition object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `mapdefinitions` (" + MAPFEATURETYPE_ID + ", " + MARKER_ID + ", " + MAP_ID + ", " + DEFINITION_START + ", " + DEFINITION_END + ", " + CHROMOSOME + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
					.setLong(object.getType().getId())
					.setLong(object.getMarker().getId())
					.setLong(object.getMap().getId())
					.setDouble(object.getDefinitionStart())
					.setDouble(object.getDefinitionEnd())
					.setString(object.getChromosome());

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

		public void writeBatched(DatabaseStatement stmt, MapDefinition object) throws DatabaseException
		{
			int i = 1;
			stmt.setLong(i++, object.getType().getId());
			stmt.setLong(i++, object.getMarker().getId());
			stmt.setLong(i++, object.getMap().getId());
			stmt.setDouble(i++, object.getDefinitionStart());
			stmt.setDouble(i++, object.getDefinitionEnd());
			stmt.setString(i++, object.getChromosome());
			stmt.addBatch();
		}
	}
}
