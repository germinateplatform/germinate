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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class MegaEnvironment extends DatabaseObject
{
	private static final long serialVersionUID = 3208912675996165659L;

	public static final String ID   = "megaenvironments.id";
	public static final String NAME = "megaenvironments.name";

	private String name;
	private Long size = 0L;

	public MegaEnvironment()
	{
	}

	public MegaEnvironment(Long id)
	{
		super(id);
	}

	public String getName()
	{
		return name;
	}

	public MegaEnvironment setName(String name)
	{
		this.name = name;
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public MegaEnvironment setSize(Long size)
	{
		this.size = size;
		return this;
	}

	@Override
	public String toString()
	{
		return "MegaEnvironment{" +
				"name='" + name + '\'' +
				", size=" + size +
				'}';
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<MegaEnvironment>
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
		public MegaEnvironment parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				MegaEnvironment megaEnvironment = new MegaEnvironment(id)
						.setName(row.getString(NAME));

				try
				{
					megaEnvironment.setSize(row.getLong(DatasetManager.COUNT));
				}
				catch (Exception e)
				{
					// Ignore this
				}

				return megaEnvironment;
			}
		}
	}

	@GwtIncompatible
	public static class IdNameParser extends DatabaseObjectParser<MegaEnvironment>
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
				private static final IdNameParser INSTANCE = new IdNameParser();
			}

			public static IdNameParser get()
			{
				return InstanceHolder.INSTANCE;
			}
		}

		private IdNameParser()
		{
		}

		@Override
		public MegaEnvironment parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong("id");

			if (id == null)
				return null;
			else
			{
				MegaEnvironment megaEnvironment = new MegaEnvironment(id)
						.setName(row.getString("name"));

				try
				{
					megaEnvironment.setSize(row.getLong(DatasetManager.COUNT));
				}
				catch (Exception e)
				{
					// Ignore this
				}

				return megaEnvironment;
			}
		}
	}
}
