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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class Country extends DatabaseObject
{
	private static final long serialVersionUID = 4863415808947750663L;

	public static final String ID            = "countries.id";
	public static final String COUNTRY_CODE2 = "countries.country_code2";
	public static final String COUNTRY_CODE3 = "countries.country_code3";
	public static final String COUNTRY_NAME  = "countries.country_name";

	public static final String AVERAGE = "avg";

	private String countryCode2;
	private String countryCode3;
	private String name;
	private Long   createdOn;
	private Long   updatedOn;

	public Country()
	{
	}

	public Country(Long id)
	{
		super(id);
	}

	public String getCountryCode2()
	{
		return countryCode2;
	}

	public Country setCountryCode2(String countryCode2)
	{
		this.countryCode2 = countryCode2;
		return this;
	}

	public String getCountryCode3()
	{
		return countryCode3;
	}

	public Country setCountryCode3(String countryCode3)
	{
		this.countryCode3 = countryCode3;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Country setName(String name)
	{
		this.name = name;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Country setCreatedOn(Date createdOn)
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

	public Country setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Country>
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

		private Parser()
		{
		}

		@Override
		public Country parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
				return new Country(id)
						.setCountryCode2(row.getString(COUNTRY_CODE2))
						.setCountryCode3(row.getString(COUNTRY_CODE3))
						.setName(row.getString(COUNTRY_NAME))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));
		}
	}

	@GwtIncompatible
	public static class CountParser extends Parser
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
				private static final CountParser INSTANCE = new CountParser();
			}

			public static CountParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}


		@Override
		public Country parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Country country = super.parse(row, user, foreignsFromResultSet);

			if (country != null)
				country.setExtra(COUNT, row.getString(COUNT));

			return country;
		}
	}

	@GwtIncompatible
	public static class AverageParser extends Parser
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
				private static final AverageParser INSTANCE = new AverageParser();
			}

			public static AverageParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}


		@Override
		public Country parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Country country = super.parse(row, user, foreignsFromResultSet);

			if (country != null)
				country.setExtra(AVERAGE, row.getString(AVERAGE));

			return country;
		}
	}
}
