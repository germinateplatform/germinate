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

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperUserManager extends AbstractManager<GatekeeperUser>
{
	private static final String[] COLUMNS_TABLE = {GatekeeperUser.USERNAME, GatekeeperUser.FULL_NAME, GatekeeperUser.EMAIL, "institutions.name", "database_systems.system_name", "database_systems.server_name"};

	private static final String COMMON_TABLES = " `users` LEFT JOIN `user_has_access_to_databases` ON `users`.`id` = `user_has_access_to_databases`.`user_id` LEFT JOIN `user_types` ON `user_types`.`id` = `user_has_access_to_databases`.`user_type_id` LEFT JOIN `database_systems` ON `database_systems`.`id` = `user_has_access_to_databases`.`database_id` ";

	private static final String SELECT_BY_ID                    = "SELECT * FROM " + COMMON_TABLES + " WHERE `users`.`id` = ? AND `database_systems`.`system_name` = ? AND `database_systems`.`server_name` = ?";
	private static final String SELECT_BY_NAME_AND_SYSTEM       = "SELECT * FROM " + COMMON_TABLES + " WHERE `users`.`username` = ? AND `database_systems`.`system_name` = ? AND `database_systems`.`server_name` = ?";
	private static final String SELECT_BY_NAME_GLOBAL           = "SELECT * FROM " + COMMON_TABLES + " WHERE `users`.`username` = ?";
	private static final String SELECT_ALL_FOR_FILTER           = "SELECT * FROM " + COMMON_TABLES + " LEFT JOIN `institutions` ON `institutions`.`id` = `users`.`institution_id` {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER_AND_GROUP = "SELECT * FROM `users` LEFT JOIN `institutions` ON `institutions`.`id` = `users`.`institution_id` {{FILTER}} AND `users`.`id` IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_DATASET          = "SELECT `datasetpermissions`.`user_id` FROM `datasetpermissions` WHERE `dataset_id` = ?";

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

	public static GatekeeperUserWithPassword getByIdWithPasswordForSystem(UserAuth user, Long id) throws DatabaseException
	{
		String database = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
		String server = PropertyWatcher.get(ServerProperty.DATABASE_SERVER);

		if (id == null || !PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(database, server))
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
		if (!PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(username))
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
		String database = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
		String server = PropertyWatcher.get(ServerProperty.DATABASE_SERVER);

		if (!PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(username, database, server))
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
		if (!PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION))
			return;

		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			return;

		Integer rounds = PropertyWatcher.getInteger(ServerProperty.GERMINATE_GATEKEEPER_BCRYPT_ROUNDS);
		String hashed = BCrypt.hashpw(password, BCrypt.gensalt(rounds));

		new ValueQuery(UPDATE_PASSWORD_FROM_ID)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setString(hashed)
				.setLong(id)
				.execute();
	}

	public static PaginatedServerResult<List<GatekeeperUser>> getForFilterAndGroup(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter, Long id, GerminateDatabaseTable table) throws DatabaseException, InsufficientPermissionsException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		UserGroupManager.checkIfAdmin(userAuth);

		if (id == null)
		{
			pagination.updateSortColumn(COLUMNS_TABLE, GatekeeperUser.USERNAME);
			String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

			// Make sure the filtering is restricted to only the current database!
			String database = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
			String server = PropertyWatcher.get(ServerProperty.DATABASE_SERVER);

			if (filter == null)
				filter = new PartialSearchQuery();

			filter.add(new SearchCondition("database_systems.system_name", new Equal(), database, String.class));
			if (filter.getAll().size() > 1)
				filter.addLogicalOperator(new And());

			filter.add(new SearchCondition("database_systems.server_name", new Equal(), server, String.class));
			if (filter.getAll().size() > 1)
				filter.addLogicalOperator(new And());

			return AbstractManager.<GatekeeperUser>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, COLUMNS_TABLE, pagination.getResultSize(), Database.QueryType.AUTHENTICATION)
					.setQueryType(Database.QueryType.AUTHENTICATION)
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(GatekeeperUser.Parser.Inst.get(), true);
		}
		else
		{
			ServerResult<List<Long>> ids;

			if (table == GerminateDatabaseTable.datasets)
				ids = getUsersWithAccessToDataset(userAuth, id);
			else
				ids = UserGroupManager.getUsersInGroup(userAuth, id);

			if (CollectionUtils.isEmpty(ids.getServerResult()))
			{
				return new PaginatedServerResult<>(ids.getDebugInfo(), new ArrayList<>(), 0);
			}
			else
			{
				pagination.updateSortColumn(COLUMNS_TABLE, GatekeeperUser.USERNAME);
				String formatted = String.format(SELECT_ALL_FOR_FILTER_AND_GROUP, StringUtils.generateSqlPlaceholderString(ids.getServerResult().size()), pagination.getSortQuery());

				return AbstractManager.<GatekeeperUser>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, COLUMNS_TABLE, pagination.getResultSize(), Database.QueryType.AUTHENTICATION)
						.setLongs(ids.getServerResult())
						.setInt(pagination.getStart())
						.setInt(pagination.getLength())
						.run()
						.getObjectsPaginated(GatekeeperUser.Parser.Inst.get(), true);
			}
		}
	}

	private static ServerResult<List<Long>> getUsersWithAccessToDataset(UserAuth userAuth, Long datasetId) throws DatabaseException
	{
		return new ValueQuery(SELECT_IDS_FOR_DATASET, userAuth)
				.setLong(datasetId)
				.run("user_id")
				.getLongs();
	}

	@Override
	public ServerResult<GatekeeperUser> getById(UserAuth user, Long id) throws DatabaseException
	{
		String database = PropertyWatcher.get(ServerProperty.DATABASE_NAME);
		String server = PropertyWatcher.get(ServerProperty.DATABASE_SERVER);

		if (id == null || !PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || StringUtils.isEmpty(database, server))
			return new ServerResult<>(null, null);


		return new DatabaseObjectQuery<GatekeeperUser>(SELECT_BY_ID, user)
				.setQueryType(Database.QueryType.AUTHENTICATION)
				.setLong(id)
				.setString(database)
				.setString(server)
				.run()
				.getObject(GatekeeperUser.Parser.Inst.get());
	}
}
