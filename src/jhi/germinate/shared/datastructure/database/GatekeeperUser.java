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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperUser extends DatabaseObject
{
	private static final long serialVersionUID = -3771426102208619594L;

	public static final GatekeeperUser UNKNOWN = new GatekeeperUser(-1L, "Admin", "Administrator", true, false, null, null);

	public static final String ID                    = "users.id";
	public static final String USERNAME              = "users.username";
	public static final String FULL_NAME             = "users.full_name";
	public static final String USER_TYPE_DESCRIPTION = "user_types.description";
	public static final String DATABASE_NAME         = "database_systems.system_name";
	public static final String DATABASE_SERVER       = "database_systems.server_name";

	private String  username;
	private String  fullName;
	private boolean isAdmin;
	private boolean isSuspended;
	private String  databaseName;
	private String  databaseServer;

	public GatekeeperUser()
	{
	}

	public GatekeeperUser(Long id)
	{
		super(id);
	}

	public GatekeeperUser(Long id, String username, String fullName, boolean isAdmin, boolean isSuspended, String databaseName, String databaseServer)
	{
		super(id);
		this.username = username;
		this.fullName = fullName;
		this.isAdmin = isAdmin;
		this.isSuspended = isSuspended;
		this.databaseName = databaseName;
		this.databaseServer = databaseServer;
	}

	public String getUsername()
	{
		return username;
	}

	public GatekeeperUser setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getFullName()
	{
		return fullName;
	}

	public GatekeeperUser setFullName(String fullName)
	{
		this.fullName = fullName;
		return this;
	}

	public boolean isAdmin()
	{
		return isAdmin;
	}

	public GatekeeperUser setIsAdmin(boolean isAdmin)
	{
		this.isAdmin = isAdmin;
		return this;
	}

	public boolean isSuspended()
	{
		return isSuspended;
	}

	public GatekeeperUser setIsSuspended(boolean isSuspended)
	{
		this.isSuspended = isSuspended;
		return this;
	}

	public String getDatabaseName()
	{
		return databaseName;
	}

	public GatekeeperUser setDatabaseName(String databaseName)
	{
		this.databaseName = databaseName;
		return this;
	}

	public String getDatabaseServer()
	{
		return databaseServer;
	}

	public GatekeeperUser setDatabaseServer(String databaseServer)
	{
		this.databaseServer = databaseServer;
		return this;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<GatekeeperUser>
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

		protected Parser()
		{
		}

		@Override
		public GatekeeperUser parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			Long id = row.getLong(ID);

			if (id == null)
				return null;
			else
			{
				GatekeeperUser u = new GatekeeperUser(id)
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

				return u;
			}
		}
	}
}
