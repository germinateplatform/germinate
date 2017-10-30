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
public class LicenseData extends DatabaseObject
{
	public static final String ID         = "licensedata.id";
	public static final String LICENSE_ID = "licensedata.license_id";
	public static final String LOCALE_ID  = "licensedata.locale_id";
	public static final String CONTENT    = "licensedata.content";

	private Long   license;
	private Locale locale;
	private String content;
	private Long   createdOn;
	private Long   updatedOn;

	public LicenseData()
	{
	}

	public LicenseData(Long id)
	{
		super(id);
	}

	public Long getLicense()
	{
		return license;
	}

	public LicenseData setLicense(Long license)
	{
		this.license = license;
		return this;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public LicenseData setLocale(Locale locale)
	{
		this.locale = locale;
		return this;
	}

	public String getContent()
	{
		return content;
	}

	public LicenseData setContent(String content)
	{
		this.content = content;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public LicenseData setCreatedOn(Date createdOn)
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

	public LicenseData setUpdatedOn(Date updatedOn)
	{
		if (updatedOn == null)
			this.updatedOn = null;
		else
			this.updatedOn = updatedOn.getTime();
		return this;
	}

	@Override
	public String toString()
	{
		return "LicenseData{" +
				"license=" + license +
				", locale=" + locale +
				", createdOn=" + createdOn +
				", updatedOn=" + updatedOn +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<LicenseData>
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

		private static DatabaseObjectCache<Locale> LOCALE_CACHE;

		private Parser()
		{
			LOCALE_CACHE = createCache(Locale.class, LocaleManager.class);
		}

		@Override
		public LicenseData parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				try
				{
					return new LicenseData(id)
							.setLicense(row.getLong(LICENSE_ID))
							.setLocale(LOCALE_CACHE.get(user, row.getLong(LOCALE_ID), row, foreignsFromResultSet))
							.setContent(row.getString(CONTENT))
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
}
