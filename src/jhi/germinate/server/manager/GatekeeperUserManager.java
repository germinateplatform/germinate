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

package jhi.germinate.server.manager;

import jhi.germinate.server.config.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperUserManager extends AbstractManager<GatekeeperUser>
{
	private static final String SELECT_BY_ID              = "SELECT * FROM users LEFT JOIN user_has_access_to_databases ON users.id = user_has_access_to_databases.user_id LEFT JOIN user_types ON user_types.id = user_has_access_to_databases.user_type_id LEFT JOIN database_systems ON database_systems.id = user_has_access_to_databases.database_id WHERE users.id = ? AND database_systems.system_name = ? AND database_systems.server_name = ?";
	private static final String SELECT_BY_NAME_AND_SYSTEM = "SELECT * FROM users LEFT JOIN user_has_access_to_databases ON users.id = user_has_access_to_databases.user_id LEFT JOIN user_types ON user_types.id = user_has_access_to_databases.user_type_id LEFT JOIN database_systems ON database_systems.id = user_has_access_to_databases.database_id WHERE users.username = ? AND database_systems.system_name = ? AND database_systems.server_name = ?";
	private static final String SELECT_BY_NAME_GLOBAL     = "SELECT * FROM users LEFT JOIN user_has_access_to_databases ON users.id = user_has_access_to_databases.user_id LEFT JOIN user_types ON user_types.id = user_has_access_to_databases.user_type_id LEFT JOIN database_systems ON database_systems.id = user_has_access_to_databases.database_id WHERE users.username = ?";

	private static final String UPDATE_PASSWORD_FROM_ID = "UPDATE users SET password = ? WHERE id = ?";

	@Override
	protected String getTable()
	{
		return "users";
	}

	@Override
	protected DatabaseObjectParser<GatekeeperUser> getParser()
	{
		return GatekeeperUser.Parser.Inst.get();
	}

	@Override
	public ServerResult<GatekeeperUser> getById(UserAuth user, Long id) throws DatabaseException
	{
		String database = PropertyReader.get(ServerProperty.DATABASE_NAME);
		String server = PropertyReader.get(ServerProperty.DATABASE_SERVER);

		if (id == null || !PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(database, server))
			return null;

		if (id == null || !PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION))
			return new ServerResult<>(null, null);


		return new DatabaseObjectQuery<GatekeeperUser>(SELECT_BY_ID, user)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setLong(id)
				.setString(database)
				.setString(server)
				.run()
				.getObject(GatekeeperUser.Parser.Inst.get());
	}

	public static GatekeeperUserWithPassword getByIdWithPasswordForSystem(UserAuth user, Long id) throws DatabaseException
	{
		String database = PropertyReader.get(ServerProperty.DATABASE_NAME);
		String server = PropertyReader.get(ServerProperty.DATABASE_SERVER);

		if (id == null || !PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(database, server))
			return null;


		return new DatabaseObjectQuery<GatekeeperUserWithPassword>(SELECT_BY_ID, user)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setLong(id)
				.setString(database)
				.setString(server)
				.run()
				.getObject(GatekeeperUserWithPassword.Parser.Inst.get())
				.getServerResult();
	}

	public static GatekeeperUserWithPassword getForUsernameGlobal(String username) throws DatabaseException
	{
		if (!PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(username))
			return null;

		return new DatabaseObjectQuery<GatekeeperUserWithPassword>(SELECT_BY_NAME_GLOBAL, null)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setString(username)
				.run()
				.getObject(GatekeeperUserWithPassword.Parser.Inst.get())
				.getServerResult();
	}

	public static GatekeeperUserWithPassword getForUsernameAndSystem(String username) throws DatabaseException
	{
		String database = PropertyReader.get(ServerProperty.DATABASE_NAME);
		String server = PropertyReader.get(ServerProperty.DATABASE_SERVER);

		if (!PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(username, database, server))
			return null;

		return new DatabaseObjectQuery<GatekeeperUserWithPassword>(SELECT_BY_NAME_AND_SYSTEM, null)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setString(username)
				.setString(database)
				.setString(server)
				.run()
				.getObject(GatekeeperUserWithPassword.Parser.Inst.get())
				.getServerResult();
	}

	public static void updatePassword(Long id, String password) throws DatabaseException
	{
		if (!PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION))
			return;

		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			return;

		Integer rounds = PropertyReader.getInteger(ServerProperty.GERMINATE_GATEKEEPER_BCRYPT_ROUNDS);
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt(rounds));

		new ValueQuery(UPDATE_PASSWORD_FROM_ID)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setString(hashed)
				.setLong(id)
				.execute();
	}
}
