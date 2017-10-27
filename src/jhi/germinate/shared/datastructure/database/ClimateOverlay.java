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
public class ClimateOverlay extends DatabaseObject
{
	private static final long serialVersionUID = 383772881131720309L;

	public static final String ID                    = "climateoverlays.id";
	public static final String CLIMATE_ID            = "climateoverlays.climate_id";
	public static final String PATH                  = "climateoverlays.path";
	public static final String BOTTOM_LEFT_LONGITUDE = "climateoverlays.bottom_left_longitude";
	public static final String BOTTOM_LEFT_LATITUDE  = "climateoverlays.bottom_left_latitude";
	public static final String TOP_RIGHT_LONGITUDE   = "climateoverlays.top_right_longitude";
	public static final String TOP_RIGHT_LATITUDE    = "climateoverlays.top_right_latitude";
	public static final String IS_LEGEND             = "climateoverlays.is_legend";
	public static final String DESCCRIPTION          = "climateoverlays.description";

	private String  name;
	private Climate climate;
	private String  path;
	private Double  bottomLeftLongitude;
	private Double  bottomLeftLatitude;
	private Double  topRightLongitude;
	private Double  topRightLatitude;
	private Boolean isLegend;
	private String  description;
	private Long    createdOn;
	private Long    updatedOn;

	public ClimateOverlay()
	{
	}

	public ClimateOverlay(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public ClimateOverlay setName(String name)
	{
		this.name = name;
		return this;
	}

	public Climate getClimate()
	{
		return climate;
	}

	public ClimateOverlay setClimate(Climate climate)
	{
		this.climate = climate;
		return this;
	}

	public String getPath()
	{
		return path;
	}

	public ClimateOverlay setPath(String path)
	{
		this.path = path;
		return this;
	}

	public Double getBottomLeftLongitude()
	{
		return bottomLeftLongitude;
	}

	public ClimateOverlay setBottomLeftLongitude(Double bottomLeftLongitude)
	{
		this.bottomLeftLongitude = bottomLeftLongitude;
		return this;
	}

	public Double getBottomLeftLatitude()
	{
		return bottomLeftLatitude;
	}

	public ClimateOverlay setBottomLeftLatitude(Double bottomLeftLatitude)
	{
		this.bottomLeftLatitude = bottomLeftLatitude;
		return this;
	}

	public Double getTopRightLongitude()
	{
		return topRightLongitude;
	}

	public ClimateOverlay setTopRightLongitude(Double topRightLongitude)
	{
		this.topRightLongitude = topRightLongitude;
		return this;
	}

	public Double getTopRightLatitude()
	{
		return topRightLatitude;
	}

	public ClimateOverlay setTopRightLatitude(Double topRightLatitude)
	{
		this.topRightLatitude = topRightLatitude;
		return this;
	}

	public Boolean isLegend()
	{
		return isLegend;
	}

	public ClimateOverlay setIsLegend(Boolean isLegend)
	{
		this.isLegend = isLegend;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public ClimateOverlay setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public ClimateOverlay setCreatedOn(Date createdOn)
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

	public ClimateOverlay setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<ClimateOverlay>
	{
		public static final class Inst
		{
			/**
			 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
			 * InstanceHolder#INSTANCE}, not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
			 * >Initialization-on-demand holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
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

		private static DatabaseObjectCache<Climate> CLIMATE_CACHE;

		private Parser()
		{
			CLIMATE_CACHE = createCache(Climate.class, ClimateManager.class);
		}

		@Override
		public ClimateOverlay parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new ClimateOverlay(id)
							.setClimate(CLIMATE_CACHE.get(user, row.getLong(CLIMATE_ID), row, foreignsFromResultSet))
							.setPath(row.getString(PATH))
							.setBottomLeftLatitude(row.getDouble(BOTTOM_LEFT_LATITUDE))
							.setBottomLeftLongitude(row.getDouble(BOTTOM_LEFT_LONGITUDE))
							.setTopRightLatitude(row.getDouble(TOP_RIGHT_LATITUDE))
							.setTopRightLongitude(row.getDouble(TOP_RIGHT_LONGITUDE))
							.setIsLegend(row.getBoolean(IS_LEGEND))
							.setDescription(row.getString(DESCCRIPTION))
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
