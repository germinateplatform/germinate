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

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class DataStats extends DatabaseObject
{
	private String name;
	private String description;
	private Unit   unit;
	private Double min;
	private Double max;
	private Double avg;
	private Double std;
	private String dataset;

	public DataStats()
	{
	}

	public DataStats(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public DataStats setName(String name)
	{
		this.name = name;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public DataStats setDescription(String description)
	{
		this.description = description;
		return this;
	}

	public Unit getUnit()
	{
		return unit;
	}

	public DataStats setUnit(Unit unit)
	{
		this.unit = unit;
		return this;
	}

	public Double getMin()
	{
		return min;
	}

	public DataStats setMin(Double min)
	{
		this.min = min;
		return this;
	}

	public Double getMax()
	{
		return max;
	}

	public DataStats setMax(Double max)
	{
		this.max = max;
		return this;
	}

	public Double getAvg()
	{
		return avg;
	}

	public DataStats setAvg(Double avg)
	{
		this.avg = avg;
		return this;
	}

	public Double getStd()
	{
		return std;
	}

	public DataStats setStd(Double std)
	{
		this.std = std;
		return this;
	}

	public String getDataset()
	{
		return dataset;
	}

	public DataStats setDataset(String dataset)
	{
		this.dataset = dataset;
		return this;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<DataStats>
	{
		private DatabaseObjectCache<Unit> UNIT_CACHE;

		private Parser()
		{
			UNIT_CACHE = createCache(Unit.class, UnitManager.class);
		}

		@Override
		public DataStats parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong("id");

			if (id == null)
				return null;
			else
			{
				try
				{
					return new DataStats(id)
							.setName(row.getString("name"))
							.setDescription(row.getString("description"))
							.setUnit(UNIT_CACHE.get(user, row.getLong("units.id"), row, foreignsFromResultSet))
							.setMin(row.getDouble("min"))
							.setAvg(row.getDouble("avg"))
							.setMax(row.getDouble("max"))
							.setStd(row.getDouble("std"))
							.setDataset(row.getString("dataset_description"));
				}
				catch (InsufficientPermissionsException e)
				{
					return null;
				}
			}
		}

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
	}
}
