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

import jhi.germinate.client.service.*;
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
public class Location extends DatabaseObject
{
	private static final long serialVersionUID = -9021326419560636314L;

	public static final String ID                     = "locations.id";
	public static final String COUNTRY_ID             = "locations.country_id";
	public static final String LOCATIONTYPE_ID        = "locations.locationtype_id";
	public static final String STATE                  = "locations.state";
	public static final String REGION                 = "locations.region";
	public static final String SITE_NAME              = "locations.site_name";
	public static final String SITE_NAME_SHORT        = "locations.site_name_short";
	public static final String ELEVATION              = "locations.elevation";
	public static final String LATITUDE               = "locations.latitude";
	public static final String LONGITUDE              = "locations.longitude";
	public static final String COORDINATE_UNCERTAINTY = "locations.coordinate_uncertainty";
	public static final String COORDINATE_DATUM       = "locations.coordinate_datum";
	public static final String GEOREFERENCING_METHOD  = "locations.georeferencing_method";
	public static final String CREATED_ON             = "locations.created_on";
	public static final String UPDATED_ON             = "locations.updated_on";

	private Country      country;
	private LocationType type;
	private String       region;
	private String       state;
	private String       name;
	private String       shortName;
	private Double       elevation;
	private Double       latitude;
	private Double       longitude;
	private Integer      coordinateUncertainty;
	private String       coordinateDatum;
	private String       georeferencingMethod;
	private Long         createdOn;
	private Long         updatedOn;
	private Long size = 0L;

	public Location()
	{
	}

	public Location(Long id)
	{
		super(id);
	}

	public Location setId(String id)
	{
		try
		{
			this.id = Long.parseLong(id);
		}
		catch (NumberFormatException e)
		{

		}

		return this;
	}

	public Country getCountry()
	{
		return country;
	}

	public Location setCountry(Country country)
	{
		this.country = country;
		return this;
	}

	public LocationType getType()
	{
		return type;
	}

	public Location setType(LocationType type)
	{
		this.type = type;
		return this;
	}

	public String getRegion()
	{
		return region;
	}

	public Location setRegion(String region)
	{
		this.region = region;
		return this;
	}

	public String getState()
	{
		return state;
	}

	public Location setState(String state)
	{
		this.state = state;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Location setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getShortName()
	{
		return shortName;
	}

	public Location setShortName(String shortName)
	{
		this.shortName = shortName;
		return this;
	}

	public Double getElevation()
	{
		return elevation;
	}

	public Location setElevation(Double elevation)
	{
		this.elevation = elevation;
		return this;
	}

	public Location setElevation(String elevation)
	{
		try
		{
			this.elevation = Double.parseDouble(elevation);
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}

		return this;
	}

	public Double getLatitude()
	{
		return latitude;
	}

	public Location setLatitude(Double latitude)
	{
		this.latitude = latitude;
		return this;
	}

	public Location setLatitude(String latitude)
	{
		try
		{
			this.latitude = Double.parseDouble(latitude);
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}

		return this;
	}

	public Double getLongitude()
	{
		return longitude;
	}

	public Location setLongitude(Double longitude)
	{
		this.longitude = longitude;
		return this;
	}

	public Location setLongitude(String longitude)
	{
		try
		{
			this.longitude = Double.parseDouble(longitude);
		}
		catch (NumberFormatException | NullPointerException e)
		{
		}

		return this;
	}

	public Integer getCoordinateUncertainty()
	{
		return coordinateUncertainty;
	}

	public Location setCoordinateUncertainty(Integer coordinateUncertainty)
	{
		this.coordinateUncertainty = coordinateUncertainty;
		return this;
	}

	public String getCoordinateDatum()
	{
		return coordinateDatum;
	}

	public Location setCoordinateDatum(String coordinateDatum)
	{
		this.coordinateDatum = coordinateDatum;
		return this;
	}

	public String getGeoreferencingMethod()
	{
		return georeferencingMethod;
	}

	public Location setGeoreferencingMethod(String georeferencingMethod)
	{
		this.georeferencingMethod = georeferencingMethod;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Location setCreatedOn(Date createdOn)
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

	public Location setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public Location setSize(Long size)
	{
		if (size != null)
			this.size = size;
		return this;
	}

	@Override
	public String toString()
	{
		return "Location{" +
				"id=" + id +
				'}';
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<Location>
	{
		public static final class Inst
		{
			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}

			public static Parser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		private static DatabaseObjectCache<Country> COUNTRY_CACHE;

		protected Parser()
		{
			COUNTRY_CACHE = createCache(Country.class, CountryManager.class);
		}

		@Override
		public Location parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
				{
					Location location = new Location(id)
							.setCountry(COUNTRY_CACHE.get(user, row.getLong(COUNTRY_ID), row, foreignsFromResultSet))
							.setType(LocationType.getById(row.getLong(LOCATIONTYPE_ID)))
							.setState(row.getString(STATE))
							.setRegion(row.getString(REGION))
							.setName(row.getString(SITE_NAME))
							.setShortName(row.getString(SITE_NAME_SHORT))
							.setElevation(row.getDouble(ELEVATION))
							.setLatitude(row.getDouble(LATITUDE))
							.setLongitude(row.getDouble(LONGITUDE))
							.setCoordinateUncertainty(row.getInt(COORDINATE_UNCERTAINTY))
							.setCoordinateDatum(row.getString(COORDINATE_DATUM))
							.setGeoreferencingMethod(row.getString(GEOREFERENCING_METHOD))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					try
					{
						location.setSize(row.getLong(LocationManager.COUNT));
					}
					catch (Exception e)
					{
						// Ignore this
					}

					return location;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}

	@GwtIncompatible
	public static class DistanceParser extends Parser
	{
		public static final class Inst
		{
			private static final class InstanceHolder
			{
				private static final DistanceParser INSTANCE = new DistanceParser();
			}

			public static DistanceParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		@Override
		public Location parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Location location = super.parse(row, user, foreignsFromResultSet);

			if (location != null)
				location.setExtra(LocationService.DISTANCE, row.getString(LocationService.DISTANCE));

			return location;
		}
	}

	@GwtIncompatible
	public static class ClimateDataParser extends Parser
	{
		public static final class Inst
		{
			private static final class InstanceHolder
			{
				private static final DistanceParser INSTANCE = new DistanceParser();
			}

			public static DistanceParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		@Override
		public Location parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Location location = super.parse(row, user, foreignsFromResultSet);

			if (location != null)
			{
				for (int i = 1; i <= 12; i++)
					location.setExtra(Integer.toString(i), row.getString(Integer.toString(i)));
			}

			return location;
		}
	}

	@GwtIncompatible
	public static class Writer implements DatabaseObjectWriter<Location>
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
		public void write(Database database, Location object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO `locations` (" + LOCATIONTYPE_ID + ", " + COUNTRY_ID + ", " + STATE + ", " + REGION + ", " + SITE_NAME + ", " + SITE_NAME_SHORT + ", " + ELEVATION + ", " + LATITUDE + ", " + LONGITUDE + ", " + COORDINATE_UNCERTAINTY + ", " + COORDINATE_DATUM + ", " + GEOREFERENCING_METHOD + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setLong(object.getType().getId())
					.setLong(object.getCountry().getId())
					.setString(object.getState())
					.setString(object.getRegion())
					.setString(object.getName())
					.setString(object.getShortName())
					.setDouble(object.getElevation())
					.setDouble(object.getLatitude())
					.setDouble(object.getLongitude())
					.setInt(object.getCoordinateUncertainty())
					.setString(object.getCoordinateDatum())
					.setString(object.getGeoreferencingMethod());

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
