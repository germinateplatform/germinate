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

import jhi.germinate.client.service.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class GroupManager extends AbstractManager<Group>
{
	public static final String[] COLUMNS_TABLE = {Group.ID, Group.NAME, Group.DESCRIPTION, GroupType.DESCRIPTION, Group.CREATED_BY, Group.CREATED_ON, DatabaseObject.COUNT};

	private static final String SELECT_ALL_FOR_FILTER                        = "SELECT groups.*, COUNT(groupmembers.id) AS count, grouptypes.id, grouptypes.description, grouptypes.target_table FROM groups LEFT JOIN grouptypes ON groups.grouptype_id = grouptypes.id LEFT JOIN groupmembers ON groupmembers.group_id = groups.id {{FILTER}} AND %s GROUP BY groups.id %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_ACCESSION                     = "SELECT groups.*, COUNT(groupmembers.id) AS count FROM germinatebase LEFT JOIN groupmembers ON germinatebase.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id WHERE grouptypes.target_table = 'germinatebase' AND %s AND EXISTS (SELECT 1 FROM groupmembers WHERE groupmembers.foreign_id = ? AND groupmembers.group_id = groups.id) GROUP BY groups.id %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_TYPE                          = "SELECT groups.*, COUNT(%s.id) AS count FROM groups LEFT JOIN groupmembers ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id LEFT JOIN %s ON %s.id = groupmembers.foreign_id WHERE grouptypes.target_table = ? AND %s GROUP BY groups.id ORDER BY groups.id";
	private static final String SELECT_TRIALS_ACCESSION_GROUPS_FOR_DATASET   = "SELECT DISTINCT groups.*, COUNT( DISTINCT (phenotypedata.germinatebase_id) ) AS count FROM phenotypedata LEFT JOIN groupmembers ON groupmembers.foreign_id = phenotypedata.germinatebase_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND grouptypes.target_table = 'germinatebase' AND datasets.id IN (%s) AND %s GROUP BY groups.id";
	private static final String SELECT_COMPOUND_ACCESSION_GROUPS_FOR_DATASET = "SELECT DISTINCT groups.*, COUNT( DISTINCT (compounddata.germinatebase_id) ) AS count FROM compounddata LEFT JOIN groupmembers ON groupmembers.foreign_id = compounddata.germinatebase_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id LEFT JOIN datasets ON datasets.id = compounddata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'compound' AND grouptypes.target_table = 'germinatebase' AND datasets.id IN (%s) AND %s GROUP BY groups.id";
	private static final String SELECT_ACCESSION_GROUPS_FOR_DATSET           = "SELECT DISTINCT groups.*, COUNT( DISTINCT ( datasetmembers.foreign_id ) ) AS count FROM groups LEFT JOIN groupmembers ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id LEFT JOIN datasetmembers ON datasetmembers.foreign_id = groupmembers.foreign_id LEFT JOIN datasets ON datasets.id = datasetmembers.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE datasetmembers.datasetmembertype_id = 2 AND (experimenttypes.description = 'genotype' OR experimenttypes.description = 'allelefreq' ) AND grouptypes.target_table = 'germinatebase' AND datasets.id IN (%s) AND %s GROUP BY groups.id";
	private static final String SELECT_MARKER_GROUPS_FOR_DATSET              = "SELECT DISTINCT groups.*, COUNT( DISTINCT ( datasetmembers.foreign_id ) ) AS count FROM groups LEFT JOIN groupmembers ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id LEFT JOIN datasetmembers ON datasetmembers.foreign_id = groupmembers.foreign_id LEFT JOIN datasets ON datasets.id = datasetmembers.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE datasetmembers.datasetmembertype_id = 1 AND (experimenttypes.description = 'genotype' OR experimenttypes.description = 'allelefreq' ) AND grouptypes.target_table = 'markers' AND datasets.id IN (%s) AND %s GROUP BY groups.id";

	private static final String SELECT_COUNT = "SELECT COUNT(1) AS count FROM groups WHERE %s";

	private static final String SELECT_REGULAR_USER  = "(groups.created_by = ? OR visibility = 1)";
	private static final String SELECT_ADMINISTRATOR = "1=1";

	private static final String INSERT_MEMBERS = "INSERT INTO groupmembers (foreign_id, group_id) SELECT ?, ? FROM dual WHERE NOT EXISTS (SELECT foreign_id, group_id FROM groupmembers WHERE foreign_id = ? AND group_id = ?) LIMIT 1";
	private static final String INSERT         = "INSERT INTO groups (grouptype_id, name, description, visibility, created_by, created_on) SELECT ?, ?, ?, 0, ?, NOW() FROM dual WHERE NOT EXISTS (SELECT grouptype_id, name, created_by FROM groups WHERE grouptype_id = ? AND name = ? AND created_by = ?)";

	private static final String DELETE         = "DELETE FROM groups WHERE id = ?";
	private static final String DELETE_MEMBERS = "DELETE FROM groupmembers WHERE group_id = ? AND foreign_id IN (%s)";

	private static final String UPDATE_VISIBILITY = "UPDATE groups SET visibility = ?, updated_on = NOW() WHERE id = ?";
	private static final String UPDATE_NAME       = "UPDATE groups SET name = ?, description = ?, updated_on = NOW() WHERE id = ?";

	@Override
	protected String getTable()
	{
		return "groups";
	}

	@Override
	protected DatabaseObjectParser<Group> getParser()
	{
		return Group.Parser.Inst.get();
	}

	/**
	 * Checks the user permissions for the given group
	 *
	 * @param userAuth The current user
	 * @param groupId  The id of the group in question
	 * @return <code>true</code> if the user is allowed to view the group, <code>false</code> if not
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static boolean hasAccessToGroup(UserAuth userAuth, Long groupId, boolean isEditOperation) throws DatabaseException, InsufficientPermissionsException
	{
		if (groupId == null || groupId < 0)
			return true;

		/* We don't allow edit operations if authentication is turned off or if we're operating in readAll-only mode */
		if (isEditOperation && (!PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION) || PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY)))
			return false;

		Group group = new GroupManager().getById(userAuth, groupId).getServerResult();

		/* Get the user details */
		GatekeeperUserWithPassword userDetails = GatekeeperUserManager.getByIdWithPasswordForSystem(null, userAuth.getId());

		/* First check if the user needs to authenticate */
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION))
		{
			/* If the user needs to log in, but there it's not a valid user */
			if (userDetails == null)
			{
				return false;
			}
			/* If it's an administrator */
			if (userDetails.isAdmin())
			{
				return true;
			}
			/* Else, regular user. Query for visibility or ownership */
			else
			{
				if (isEditOperation)
					return Objects.equals(group.getCreatedBy(), userAuth.getId());
				else
					return Objects.equals(group.getCreatedBy(), userAuth.getId()) || group.getVisibility();
			}
		}
		/* No login required, only show public groups */
		else
		{
			return group.getVisibility();
		}
	}

//	@Override
//	public ServerResult<Group> getById(UserAuth userAuth, Long id) throws DatabaseException
//	{
//		if (id == null || !hasAccessToGroup(userAuth, id, false))
//			return new ServerResult<>(null, null);
//
//		return new DatabaseObjectQuery<Group>(SELECT_BY_ID, userAuth)
//				.setLong(id)
//				.run()
//				.getObject(Group.Parser.Inst.get());
//	}

	/**
	 * Returns all the paginated {@link Group}s fulfilling the {@link PartialSearchQuery} filter.
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
	public static PaginatedServerResult<List<Group>> getAllForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(GroupService.COLUMNS_SORTABLE, GroupType.DESCRIPTION + ", " + Group.NAME);

		String formatted;

		if (user.isAdmin())
			formatted = String.format(SELECT_ALL_FOR_FILTER, SELECT_ADMINISTRATOR, pagination.getSortQuery());
		else
			formatted = String.format(SELECT_ALL_FOR_FILTER, SELECT_REGULAR_USER, pagination.getSortQuery());

		DatabaseObjectQuery<Group> query = AbstractManager.getFilteredDatabaseObjectQuery(user, filter, formatted, GroupService.COLUMNS_SORTABLE, pagination.getResultSize());

		if (!user.isAdmin())
			query.setLong(user.getId());

		query.setInt(pagination.getStart())
			 .setInt(pagination.getLength());

		return query.run()
					.getObjectsPaginated(Group.Parser.Inst.get(), true);
	}

	public static ServerResult<List<Group>> getAllForType(UserAuth userAuth, GerminateDatabaseTable table) throws DatabaseException
	{
		String formatted;

		if (userAuth.isAdmin())
			formatted = String.format(SELECT_ALL_FOR_TYPE, table.name(), table.name(), table.name(), SELECT_ADMINISTRATOR);
		else
			formatted = String.format(SELECT_ALL_FOR_TYPE, table.name(), table.name(), table.name(), SELECT_REGULAR_USER);

		DatabaseObjectQuery<Group> query = new DatabaseObjectQuery<Group>(formatted, userAuth)
				.setString(table.name());

		if (!userAuth.isAdmin())
			query.setLong(userAuth.getId());

		return query.run()
					.getObjects(Group.Parser.Inst.get());
	}

	public static ServerResult<Set<Long>> addToGroup(UserAuth userAuth, Long groupId, List<Long> groupMembers) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		if (!hasAccessToGroup(userAuth, groupId, true))
			throw new InsufficientPermissionsException();

		Set<Long> newIds = new HashSet<>();

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		for (Long memberId : groupMembers)
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

	public static ServerResult<Group> create(UserAuth userAuth, Group group, GerminateDatabaseTable table) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		ServerResult<Long> groupTypeId = GroupTypeManager.getForType(userAuth, table);

		ServerResult<List<Long>> newIds = new ValueQuery(INSERT, userAuth)
				.setLong(groupTypeId.getServerResult())
				.setString(group.getName())
				.setString(group.getDescription())
				.setLong(userAuth.getId())
				.setLong(groupTypeId.getServerResult())
				.setString(group.getName())
				.setLong(userAuth.getId())
				.execute();

		DebugInfo finalInfo = groupTypeId.getDebugInfo().addAll(newIds.getDebugInfo());

		if (CollectionUtils.isEmpty(newIds.getServerResult()))
			return new ServerResult<>(finalInfo, null);
		else
			return new GroupManager().getById(userAuth, newIds.getServerResult().get(0));
	}

	public static DebugInfo delete(UserAuth userAuth, List<Long> groupIds) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		for (Long groupId : groupIds)
		{
			if (!hasAccessToGroup(userAuth, groupId, true))
				throw new InsufficientPermissionsException();

			sqlDebug.addAll(new ValueQuery(DELETE, userAuth)
					.setLong(groupId)
					.execute()
					.getDebugInfo());
		}

		resetAutoIncrement(GerminateDatabaseTable.groupmembers);

		return sqlDebug;
	}

	public static DebugInfo deleteFromGroup(UserAuth userAuth, Long groupId, List<Long> memberIds) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		if (!hasAccessToGroup(userAuth, groupId, true))
			throw new InsufficientPermissionsException();

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		String formatted = String.format(DELETE_MEMBERS, Util.generateSqlPlaceholderString(memberIds.size()));

		sqlDebug.addAll(new ValueQuery(formatted, userAuth)
				.setLong(groupId)
				.setLongs(memberIds)
				.execute()
				.getDebugInfo());

		resetAutoIncrement(GerminateDatabaseTable.groupmembers);

		return sqlDebug;
	}

	public static DebugInfo setVisibility(UserAuth userAuth, Long groupId, boolean isPublic) throws DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		if (!hasAccessToGroup(userAuth, groupId, true))
			throw new InsufficientPermissionsException();

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		sqlDebug.addAll(new ValueQuery(UPDATE_VISIBILITY, userAuth)
				.setBoolean(isPublic)
				.setLong(groupId)
				.execute()
				.getDebugInfo());

		return sqlDebug;
	}

	public static ServerResult<Long> getCount(UserAuth userAuth) throws DatabaseException
	{
		String formatted;

		if (userAuth.isAdmin())
			formatted = String.format(SELECT_COUNT, SELECT_ADMINISTRATOR);
		else
			formatted = String.format(SELECT_COUNT, SELECT_REGULAR_USER);

		ValueQuery query = new ValueQuery(formatted, userAuth);

		if (!userAuth.isAdmin())
			query.setLong(userAuth.getId());

		return query
				.run(COUNT)
				.getLong(0L);
	}

	public static PaginatedServerResult<List<Group>> getAllForAccession(UserAuth userAuth, Long accessionId, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Group.ID);
		String formatted;

		if (userAuth.isAdmin())
			formatted = String.format(SELECT_ALL_FOR_ACCESSION, SELECT_ADMINISTRATOR, pagination.getSortQuery());
		else
			formatted = String.format(SELECT_ALL_FOR_ACCESSION, SELECT_REGULAR_USER, pagination.getSortQuery());

		DatabaseObjectQuery<Group> query = new DatabaseObjectQuery<>(formatted, userAuth);
		query.setFetchesCount(pagination.getResultSize());

		if (!userAuth.isAdmin())
			query.setLong(userAuth.getId());

		return query.setLong(accessionId)
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Group.Parser.Inst.get());
	}

	private static ServerResult<List<Group>> getGroupsForDataset(UserAuth userAuth, List<Long> datasetIds, String q) throws DatabaseException
	{
		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);
		String formatted;

		if (userAuth.isAdmin())
			formatted = String.format(q, Util.generateSqlPlaceholderString(datasetIds.size()), SELECT_ADMINISTRATOR);
		else
			formatted = String.format(q, Util.generateSqlPlaceholderString(datasetIds.size()), SELECT_REGULAR_USER);

		DatabaseObjectQuery<Group> query = new DatabaseObjectQuery<Group>(formatted, userAuth)
				.setLongs(datasetIds);

		if (!userAuth.isAdmin())
			query.setLong(userAuth.getId());

		return query.run()
					.getObjects(Group.Parser.Inst.get());
	}

	public static ServerResult<List<Group>> getAccessionGroupsForDatasets(UserAuth userAuth, List<Long> datasetIds, ExperimentType type) throws DatabaseException
	{
		switch (type)
		{
			case compound:
				return getGroupsForDataset(userAuth, datasetIds, SELECT_COMPOUND_ACCESSION_GROUPS_FOR_DATASET);
			case trials:
				return getGroupsForDataset(userAuth, datasetIds, SELECT_TRIALS_ACCESSION_GROUPS_FOR_DATASET);
			case allelefreq:
			case genotype:
				return getGroupsForDataset(userAuth, datasetIds, SELECT_ACCESSION_GROUPS_FOR_DATSET);
			default:
				return new ServerResult<>(null, new ArrayList<>());
		}
	}

	public static ServerResult<List<Group>> getMarkerGroupsForDataset(UserAuth userAuth, List<Long> datasetIds, ExperimentType type) throws DatabaseException
	{
		switch (type)
		{
			case allelefreq:
			case genotype:
				return getGroupsForDataset(userAuth, datasetIds, SELECT_MARKER_GROUPS_FOR_DATSET);
			default:
				return new ServerResult<>(null, new ArrayList<>());
		}
	}

	public static ServerResult<Void> rename(UserAuth userAuth, Group group) throws SystemInReadOnlyModeException, InsufficientPermissionsException, DatabaseException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		if (!hasAccessToGroup(userAuth, group.getId(), true))
			throw new InsufficientPermissionsException();

		ServerResult<List<Long>> result = new ValueQuery(UPDATE_NAME, userAuth)
				.setString(group.getName())
				.setString(group.getDescription())
				.setLong(group.getId())
				.execute();

		return new ServerResult<>(result.getDebugInfo(), null);
	}
}