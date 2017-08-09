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
public class Institution extends DatabaseObject
{
	private static final long serialVersionUID = 8001188593051466730L;

	public static final String ID         = "institutions.id";
	public static final String CODE       = "institutions.code";
	public static final String NAME       = "institutions.name";
	public static final String ACRONYM    = "institutions.acronym";
	public static final String COUNTRY_ID = "institutions.country_id";
	public static final String CONTACT    = "institutions.contact";
	public static final String PHONE      = "institutions.phone";
	public static final String EMAIL      = "institutions.email";
	public static final String ADDRESS    = "institutions.address";

	private String  code;
	private String  name;
	private String  acronym;
	private Country country;
	private String  contact;
	private String  phone;
	private String  email;
	private String  address;
	private Long    createdOn;
	private Long    updatedOn;

	public Institution()
	{
	}

	public Institution(Long id)
	{
		super(id);
	}

	public Institution(Long id, String code, String name, String acronym, Country country, String contact, String phone, String email, String address, Long createdOn, Long updatedOn)
	{
		super(id);
		this.code = code;
		this.name = name;
		this.acronym = acronym;
		this.country = country;
		this.contact = contact;
		this.phone = phone;
		this.email = email;
		this.address = address;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}

	public String getCode()
	{
		return code;
	}

	public Institution setCode(String code)
	{
		this.code = code;
		return this;
	}

	public String getName()
	{
		return name;
	}

	public Institution setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getAcronym()
	{
		return acronym;
	}

	public Institution setAcronym(String acronym)
	{
		this.acronym = acronym;
		return this;
	}

	public Country getCountry()
	{
		return country;
	}

	public Institution setCountry(Country country)
	{
		this.country = country;
		return this;
	}

	public String getContact()
	{
		return contact;
	}

	public Institution setContact(String contact)
	{
		this.contact = contact;
		return this;
	}

	public String getPhone()
	{
		return phone;
	}

	public Institution setPhone(String phone)
	{
		this.phone = phone;
		return this;
	}

	public String getEmail()
	{
		return email;
	}

	public Institution setEmail(String email)
	{
		this.email = email;
		return this;
	}

	public String getAddress()
	{
		return address;
	}

	public Institution setAddress(String address)
	{
		this.address = address;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public Institution setCreatedOn(Date createdOn)
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

	public Institution setUpdatedOn(Date updatedOn)
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
	public static class Parser extends DatabaseObjectParser<Institution>
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

		private static DatabaseObjectCache<Country> COUNTRY_CACHE;

		private Parser()
		{
			COUNTRY_CACHE = createCache(Country.class, CountryManager.class);
		}

		@Override
		public Institution parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				Long id = row.getLong(ID);

				if (id == null)
					return null;
				else
					return new Institution(id)
							.setCode(row.getString(CODE))
							.setName(row.getString(NAME))
							.setAcronym(row.getString(ACRONYM))
							.setCountry(COUNTRY_CACHE.get(user, row.getLong(COUNTRY_ID), row, foreignsFromResultSet))
							.setContact(row.getString(CONTACT))
							.setPhone(row.getString(PHONE))
							.setEmail(row.getString(EMAIL))
							.setAddress(row.getString(ADDRESS))
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
	public static class Writer implements DatabaseObjectWriter<Institution>
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
		public void write(Database database, Institution object) throws DatabaseException
		{
			ValueQuery query = new ValueQuery(database, "INSERT INTO institutions (" + CODE + ", " + NAME + ", " + ACRONYM + ", " + COUNTRY_ID + ", " + CONTACT + ", " + PHONE + ", " + ADDRESS + ", " + CREATED_ON + ", " + UPDATED_ON + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
					.setString(object.getCode())
					.setString(object.getName())
					.setString(object.getAcronym())
					.setLong(object.getCountry() != null ? object.getCountry().getId() : null)
					.setString(object.getContact())
					.setString(object.getPhone())
					.setString(object.getAddress());

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
