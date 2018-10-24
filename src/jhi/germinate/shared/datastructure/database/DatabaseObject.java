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

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DatabaseObject implements Serializable
{
	private static final long serialVersionUID = 1226813007200682358L;

	public static final String COUNT = "count";

	protected Long                id;
	protected Map<String, String> extra;

	public DatabaseObject()
	{

	}

	public DatabaseObject(Long id)
	{
		this.id = id;
	}

	public Long getId()
	{
		return id;
	}

	public DatabaseObject setId(Long id)
	{
		this.id = id;
		return this;
	}

	public String getExtra(String key)
	{
		if (extra != null)
			return extra.get(key);
		else
			return null;
	}

	public void setExtra(String key, Long value)
	{
		if (extra == null)
			extra = new HashMap<>();

		extra.put(key, Long.toString(value));
	}

	public void setExtra(String key, String value)
	{
		if (extra == null)
			extra = new HashMap<>();

		extra.put(key, value);
	}

	@GwtIncompatible
	public static String fmt(double d)
	{
		if (d == (long) d)
			return String.format("%d", (long) d);
		else
			return String.format("%s", d);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof DatabaseObject) || obj == null)
			return false;
		else
		{
			DatabaseObject o = (DatabaseObject) obj;
			return Objects.equals(id, o.getId());
		}
	}

	@Override
	public int hashCode()
	{
		return id.hashCode();
	}

	public static List<Long> getIds(Collection<? extends DatabaseObject> objects)
	{
		List<Long> ids = new ArrayList<>();

		if (!CollectionUtils.isEmpty(objects))
			objects.forEach(o -> ids.add(o.getId()));

		return ids;
	}

	public static List<Long> getIds(DatabaseObject... objects)
	{
		List<Long> ids = new ArrayList<>();

		if (objects != null)
		{
			for (DatabaseObject o : objects)
			{
				ids.add(o.getId());
			}
		}

		return ids;
	}

	public static Long getGroupSpecificId(DatabaseObject o)
	{
		Long id = null;

		if (o != null)
		{
			if (o instanceof MapDefinition)
			{
				MapDefinition m = (MapDefinition) o;
				if (m.getMarker() != null)
					id = m.getMarker().getId();
			}
			else if (o instanceof AttributeData)
			{
				AttributeData a = (AttributeData) o;
				if (a.getForeign() != null)
					id = a.getForeign().getId();
			}
			else if (o instanceof PhenotypeData)
			{
				PhenotypeData p = (PhenotypeData) o;
				if (p.getAccession() != null)
					id = p.getAccession().getId();
			}
			else if (o instanceof CompoundData)
			{
				CompoundData c = (CompoundData) o;
				if (c.getAccession() != null)
					id = c.getAccession().getId();
			}
			else
			{
				id = o.getId();
			}
		}

		return id;
	}

	public static List<Long> getGroupSpecificIds(Collection<? extends DatabaseObject> objects)
	{
		List<Long> ids = new ArrayList<>();

		if (!CollectionUtils.isEmpty(objects))
		{
			ids = objects.stream()
						 .map(DatabaseObject::getGroupSpecificId)
						 .collect(Collectors.toList());
		}

		return ids;
	}

	@Override
	public String toString()
	{
		return "DatabaseObject{" +
				"id=" + id +
				'}';
	}

	@GwtIncompatible
	public abstract DatabaseObjectParser<? extends DatabaseObject> getDefaultParser();
}
