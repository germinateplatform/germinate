/*
 *  Copyright 2018 Information and Computational Sciences,
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

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class UserGroupManager extends AbstractManager<UserGroup>
{
	public static final String[] COLUMNS_TABLE = {UserGroup.ID, UserGroup.NAME, UserGroup.DESCRIPTION, UserGroup.CREATED_ON, UserGroup.UPDATED_ON, DatabaseObject.COUNT, "datasetpermissions.dataset_id"};

	private static final String SELECT_ALL_FOR_FILTER = "SELECT usergroups.*, COUNT(usergroupmembers.id) AS count FROM usergroups LEFT JOIN usergroupmembers ON usergroupmembers.usergroup_id = usergroups.id LEFT JOIN datasetpermissions ON datasetpermissions.group_id = usergroups.id {{FILTER}} GROUP BY usergroups.id %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_GROUP  = "SELECT usergroupmembers.user_id FROM usergroupmembers WHERE usergroup_id = ?";

	private static final String UPDATE_NAME = "UPDATE usergroups SET name = ?, description = ?, updated_on = NOW() WHERE id = ?";

	private static final String INSERT         = "INSERT INTO usergroups (name, description, created_on) SELECT ?, ?, NOW() FROM dual WHERE NOT EXISTS (SELECT name FROM usergroups WHERE name = ?)";
	private static final String INSERT_MEMBERS = "INSERT INTO usergroupmembers (user_id, usergroup_id) SELECT ?, ? FROM dual WHERE NOT EXISTS (SELECT user_id, usergroup_id FROM usergroupmembers WHERE user_id = ? AND usergroup_id = ?) LIMIT 1";

	private static final String DELETE = "DELETE FROM usergroups WHERE id = ?";

	private static final String DELETE_MEMBERS = "DELETE FROM usergroupmembers WHERE usergroup_id = ? AND user_id IN (%s)";

	/**
	 * Returns all the paginated {@link UserGroup}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param user       The user requesting the data
	 * @param filter     The user-specified filter
	 * @param pagination The pagination object specifying the current chunk of data
	 * @return All the paginated {@link Group}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<UserGroup>> getAllForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException, InsufficientPermissionsException
	{
		checkIfAdmin(user);

		pagination.updateSortColumn(COLUMNS_TABLE, UserGroup.ID);

		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<UserGroup>getFilteredDatabaseObjectQuery(user, filter, formatted, COLUMNS_TABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(UserGroup.Parser.Inst.get(), true);
	}

	public static ServerResult<Void> rename(UserAuth userAuth, UserGroup group) throws SystemInReadOnlyModeException, InsufficientPermissionsException, DatabaseException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		checkIfAdmin(userAuth);

		ServerResult<List<Long>> result = new ValueQuery(UPDATE_NAME, userAuth)
				.setString(group.getName())
				.setString(group.getDescription())
				.setLong(group.getId())
				.execute();

		return new ServerResult<>(result.getDebugInfo(), null);
	}

	public static void checkIfAdmin(UserAuth user) throws InsufficientPermissionsException, DatabaseException
	{
		GatekeeperUserWithPassword details = GatekeeperUserManager.getByIdWithPasswordForSystem(user, user.getId());

		if (details == null || !details.isAdmin())
			throw new InsufficientPermissionsException();
	}

	public static ServerResult<UserGroup> create(UserAuth userAuth, UserGroup group) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		checkIfAdmin(userAuth);

		ServerResult<List<Long>> newIds = new ValueQuery(INSERT, userAuth)
				.setString(group.getName())
				.setString(group.getDescription())
				.setString(group.getName())
				.execute();

		if (CollectionUtils.isEmpty(newIds.getServerResult()))
			return new ServerResult<>(newIds.getDebugInfo(), null);
		else
			return new UserGroupManager().getById(userAuth, newIds.getServerResult().get(0));
	}

	public static DebugInfo delete(UserAuth userAuth, List<Long> groupIds) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		checkIfAdmin(userAuth);

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		for (Long groupId : groupIds)
		{
			sqlDebug.addAll(new ValueQuery(DELETE, userAuth)
					.setLong(groupId)
					.execute()
					.getDebugInfo());
		}

		resetAutoIncrement(GerminateDatabaseTable.usergroupmembers);
		resetAutoIncrement(GerminateDatabaseTable.usergroups);

		return sqlDebug;
	}

	public static ServerResult<List<Long>> getUsersInGroup(UserAuth userAuth, Long groupId) throws DatabaseException, InsufficientPermissionsException
	{
		checkIfAdmin(userAuth);

		return new ValueQuery(SELECT_IDS_FOR_GROUP, userAuth)
				.setLong(groupId)
				.run("user_id")
				.getLongs();
	}

	public static void removeFromGroup(UserAuth userAuth, Long groupId, List<Long> ids) throws DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		checkIfAdmin(userAuth);

		String formatted = String.format(DELETE_MEMBERS, StringUtils.generateSqlPlaceholderString(ids.size()));

		new ValueQuery(formatted, userAuth)
				.setLong(groupId)
				.setLongs(ids)
				.execute();

		resetAutoIncrement(GerminateDatabaseTable.usergroupmembers);
	}

	public static ServerResult<Set<Long>> addToGroup(UserAuth userAuth, Long groupId, List<Long> ids) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		checkIfAdmin(userAuth);

		Set<Long> newIds = new HashSet<>();

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		for (Long memberId : ids)
		{
			ServerResult<List<Long>> temp = new ValueQuery(INSERT_MEMBERS, userAuth)
					.setLong(memberId)
					.setLong(groupId)
					.setLong(memberId)
					.setLong(groupId)
					.execute();

			newIds.addAll(temp.getServerResult());

			sqlDebug.addAll(temp.getDebugInfo());
		}

		return new ServerResult<>(sqlDebug, newIds);
	}

	@Override
	protected String getTable()
	{
		return "usergroups";
	}

	@Override
	protected DatabaseObjectParser<UserGroup> getParser()
	{
		return UserGroup.Parser.Inst.get();
	}
}