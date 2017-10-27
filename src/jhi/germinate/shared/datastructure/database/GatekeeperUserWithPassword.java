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
public class GatekeeperUserWithPassword extends GatekeeperUser
{
	private static final long serialVersionUID = 9139764173445205972L;

	public static final String PASSWORD = "users.password";

	private String password;

	public GatekeeperUserWithPassword()
	{
	}

	public GatekeeperUserWithPassword(Long id)
	{
		super(id);
	}

	public GatekeeperUserWithPassword(Long id, String username, String fullName, boolean isAdmin, boolean isSuspended, String databaseName, String databaseServer, String password)
	{
		super(id, username, fullName, isAdmin, isSuspended, databaseName, databaseServer);
		this.password = password;
	}

	public String getPassword()
	{
		return password;
	}

	public GatekeeperUserWithPassword setPassword(String password)
	{
		this.password = password;
		return this;
	}

	@Override
	public String toString()
	{
		return "GatekeeperUserWithPassword{} " + super.toString();
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<GatekeeperUserWithPassword>
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
		public GatekeeperUserWithPassword parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				GatekeeperUser u = new GatekeeperUserWithPassword(id)
						.setPassword(row.getString(PASSWORD))
						.setUsername(row.getString(USERNAME))
						.setFullName(row.getString(FULL_NAME));

				try
				{
					u.setDatabaseName(row.getString(DATABASE_NAME));
				}
				catch (Exception e)
				{
				}
				try
				{
					u.setDatabaseServer(row.getString(DATABASE_SERVER));
				}
				catch (Exception e)
				{
				}
				try
				{
					u.setIsAdmin(Objects.equals(row.getString(USER_TYPE_DESCRIPTION), "Administrator"));
				}
				catch (Exception e)
				{
				}
				try
				{
					u.setIsSuspended(Objects.equals(row.getString(USER_TYPE_DESCRIPTION), "Suspended User"));
				}
				catch (Exception e)
				{
				}

				return (GatekeeperUserWithPassword) u;
			}
		}
	}
}
