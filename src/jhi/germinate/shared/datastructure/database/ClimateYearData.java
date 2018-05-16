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
import java.util.Map;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateYearData extends Location
{
	private Map<Integer, Double> yearToValues = new HashMap<>();

	public ClimateYearData()
	{
	}

	public ClimateYearData(Long id)
	{
		super(id);
	}

	public Double getClimateValue(int month)
	{
		return yearToValues.get(month);
	}

	public Map<Integer, Double> getYearToValues()
	{
		return yearToValues;
	}

	public void addValues(Integer key, Double value)
	{
		yearToValues.put(key, value);
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<ClimateYearData>
	{
		public static final class Inst
		{
			/**
			 * {@link Inst.InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
			 * Inst.InstanceHolder#INSTANCE}, not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
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
				return Inst.InstanceHolder.INSTANCE;
			}
		}

		private static DatabaseObjectCache<Country> COUNTRY_CACHE;

		private Parser()
		{
			COUNTRY_CACHE = createCache(Country.class, CountryManager.class);
		}

		@Override
		public ClimateYearData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
				{
					ClimateYearData location = new ClimateYearData(id);
					location.setCountry(COUNTRY_CACHE.get(user, row.getLong(COUNTRY_ID), row, foreignsFromResultSet))
							.setType(LocationType.getById(row.getLong(LOCATIONTYPE_ID)))
							.setState(row.getString(STATE))
							.setRegion(row.getString(REGION))
							.setName(row.getString(SITE_NAME))
							.setShortName(row.getString(SITE_NAME_SHORT))
							.setElevation(row.getDouble(ELEVATION))
							.setLatitude(row.getDouble(LATITUDE))
							.setLongitude(row.getDouble(LONGITUDE))
							.setCreatedOn(row.getTimestamp(CREATED_ON))
							.setUpdatedOn(row.getTimestamp(UPDATED_ON));

					for (int i = 1; i <= 12; i++)
						location.addValues(i, row.getDouble("m" + i));

					return location;
				}
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}
	}
}
