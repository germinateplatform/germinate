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
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class License extends DatabaseObject
{
	public static final String ID          = "licenses.id";
	public static final String NAME        = "licenses.name";
	public static final String DESCRIPTION = "licenses.description";
	public static final String CREATED_ON  = "licenses.created_on";
	public static final String UPDATED_ON  = "licenses.updated_on";

	private String     name;
	private String     description;
	private LicenseLog licenseLog;
	private Long       createdOn;
	private Long       updatedOn;
	private Map<Locale, LicenseData> licenseDataMap = new HashMap<>();

	public License()
	{
	}

	public License(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public License setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public License setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Map<Locale, LicenseData> getLicenseDataMap()
	{
		return licenseDataMap;
	}

	public License setLicenseDataMap(Map<Locale, LicenseData> licenseDataMap)
	{
		this.licenseDataMap = licenseDataMap;
		return this;
	}

	public LicenseData getLicenseData(String locale)
	{
		LicenseData result = null;

		for (Map.Entry<Locale, LicenseData> entry : licenseDataMap.entrySet())
		{
			if (StringUtils.areEqual(locale, entry.getKey().getName()))
			{
				result = entry.getValue();
				break;
			}
		}

		if (result == null && !StringUtils.areEqual(locale, "en_GB"))
			result = getLicenseData("en_GB");

		return result;
	}

	public LicenseLog getLicenseLog()
	{
		return licenseLog;
	}

	public License setLicenseLog(LicenseLog licenseLog)
	{
		this.licenseLog = licenseLog;
		return this;
	}

	public Long getCreatedOn()
	{
		return createdOn;
	}

	public License setCreatedOn(Date createdOn)
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

	public License setUpdatedOn(Date updatedOn)
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
		return "License{" +
				"name='" + name + '\'' +
				", description='" + description + '\'' +
				", licenseLog=" + licenseLog +
				", createdOn=" + createdOn +
				", updatedOn=" + updatedOn +
				", licenseDataMap=" + licenseDataMap +
				"} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<License>
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

		private Parser()
		{
		}

		@Override
		public License parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				License license = new License(id)
						.setName(row.getString(NAME))
						.setDescription(row.getString(DESCRIPTION))
						.setCreatedOn(row.getTimestamp(CREATED_ON))
						.setUpdatedOn(row.getTimestamp(UPDATED_ON));

				try
				{
					license.setLicenseDataMap(LicenseDataManager.getForLicense(user, license.getId()).getServerResult());
				}
				catch (Exception e)
				{
				}

				try
				{
					license.setLicenseLog(LicenseLogManager.getForUserAndLicense(license.getId(), user).getServerResult());
				}
				catch (Exception e)
				{
				}

				return license;
			}
		}
	}
}
