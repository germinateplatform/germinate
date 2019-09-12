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

package jhi.germinate.server.service;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link GroupServiceImpl} is the implementation of {@link GroupService}.
 *
 * @author Sebastian Raubach
 * @author Gordon Stephen
 */
@WebServlet(urlPatterns = {"/germinate/group"})
public class GroupServiceImpl extends BaseRemoteServiceServlet implements GroupService
{
	private static final long serialVersionUID = -5552253474876329201L;

	private static final String QUERY_NEW_GROUP_MEMBER_IDS_LOCATION   = "SELECT `locations`.`id`     FROM `locations` LEFT JOIN `countries` ON `countries`.`id` = `locations`.`country_id` WHERE %s IN (%s)";
	private static final String QUERY_NEW_GROUP_MEMBER_IDS_ACCESSIONS = "SELECT `germinatebase`.`id` FROM `germinatebase` LEFT JOIN `locations` ON `locations`.`id` = `germinatebase`.`location_id` LEFT JOIN `countries` ON `countries`.`id` = `locations`.`country_id` LEFT JOIN `taxonomies` ON `taxonomies`.`id` = `germinatebase`.`taxonomy_id` WHERE %s IN (%s)";
	private static final String QUERY_NEW_GROUP_MEMBER_IDS_MARKERS    = "SELECT `markers`.`id`       FROM `markers` LEFT JOIN `mapdefinitions` ON `markers`.`id` = `mapdefinitions`.`marker_id` WHERE %s IN (%s)";

	private static final String QUERY_MARKERS_DATA_IDS_DOWNLOAD = "SELECT `markers`.*, `markertypes`.`description` AS markertypes_description, `mapdefinitions`.`definition_start` AS mapdefinitions_definition_start, `mapdefinitions`.`definition_end` AS mapdefinitions_definition_end, `mapdefinitions`.`chromosome` AS mapdefinitions_chromosome, `mapdefinitions`.`arm_impute` AS mapdefinitions_arm_impute, `maps`.`description` AS maps_description FROM `markers` LEFT JOIN `markertypes` ON `markertypes`.`id` = `markers`.`markertype_id` LEFT JOIN `mapdefinitions` ON `markers`.`id` = `mapdefinitions`.`marker_id` LEFT JOIN `maps` ON `maps`.`id` = `mapdefinitions`.`map_id` LEFT JOIN `mapfeaturetypes` ON `mapfeaturetypes`.`id` = `mapdefinitions`.`mapfeaturetype_id` WHERE (`maps`.`visibility` = 1 OR `maps`.`user_id` = ?) AND `markers`.`id` IN (%s)";
	private static final String QUERY_COLLSITES_BY_IDS_DOWNLOAD = "SELECT `locations`.*, `locationtypes`.`name` AS locationtypes_name, `locationtypes`.`description` AS locationtypes_description, `countries`.`country_name` AS countries_country_name FROM `locations` LEFT JOIN `countries` ON `locations`.`country_id` = `countries`.`id` LEFT JOIN `locationtypes` ON `locationtypes`.`id` = `locations`.`locationtype_id` WHERE `locations`.`id` IN (%s)";

	@Override
	public PaginatedServerResult<List<Group>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.getAllForFilter(userAuth, filter, pagination);
	}

	@Override
	public ServerResult<List<Group>> getForType(RequestProperties properties, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.getAllForType(userAuth, table);
	}

	@Override
	public ServerResult<Set<Long>> addItems(RequestProperties properties, Long groupId, List<Long> groupMembers) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.addToGroup(userAuth, groupId, groupMembers);
	}

	@Override
	public ServerResult<Group> createNew(RequestProperties properties, Group group, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException, SystemInReadOnlyModeException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.create(userAuth, group, table);
	}

	@Override
	@SuppressWarnings("unchecked")
	public ServerResult<Integer> addItemsFromPreview(RequestProperties properties, Long groupId, String filename) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException, InvalidArgumentException
	{
		String sessionFilename = (String) getThreadLocalRequest().getSession().getAttribute(AccessionServiceImpl.GROUP_PREVIEW_FILENAME);
		if (!StringUtils.areEqual(sessionFilename, filename))
			throw new InvalidArgumentException();

		List<Accession> accessions = (List<Accession>) getThreadLocalRequest().getSession().getAttribute(AccessionServiceImpl.GROUP_PREVIEW_LIST);
		List<Long> accessionIds = accessions.stream()
											.map(Accession::getId)
											.collect(Collectors.toList());

		ServerResult<Set<Long>> result = addItems(properties, groupId, accessionIds);

		return new ServerResult<>(result.getDebugInfo(), result.getServerResult().size());
	}

	@Override
	public ServerResult<List<GroupType>> getTypes(RequestProperties properties) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupTypeManager.getAll(userAuth);
	}

	@Override
	public DebugInfo delete(RequestProperties properties, List<Long> groupIds) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.delete(userAuth, groupIds);
	}

	@Override
	public DebugInfo removeItems(RequestProperties properties, Long groupId, List<Long> memberIds) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.deleteFromGroup(userAuth, groupId, memberIds);
	}

	@Override
	public DebugInfo setVisibility(RequestProperties properties, Long groupId, boolean isPublic) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.setVisibility(userAuth, groupId, isPublic);
	}

	@Override
	public PaginatedServerResult<List<Accession>> getAccessionItems(RequestProperties properties, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getForGroup(userAuth, groupId, pagination);
	}

	@Override
	public ServerResult<List<String>> getAccessionItemIds(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getIdsForGroupAsStrings(userAuth, groupId);
	}

	@Override
	public PaginatedServerResult<List<Location>> getLocationItems(RequestProperties properties, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getAllForGroup(userAuth, groupId, pagination);
	}

	@Override
	public ServerResult<List<String>> getLocationItemIds(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getIdsForGroup(userAuth, groupId);
	}

	@Override
	public PaginatedServerResult<List<Marker>> getMarkerItems(RequestProperties properties, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MarkerManager.getAllForGroup(userAuth, groupId, pagination);
	}

	@Override
	public ServerResult<List<String>> getMarkerItemIds(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MarkerManager.getIdsForGroup(userAuth, groupId);
	}

	@Override
	public ServerResult<String> exportForGroupId(RequestProperties properties, Long groupId, GerminateDatabaseTable table) throws InvalidSessionException, InsufficientPermissionsException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		Group group = new GroupManager().getById(userAuth, groupId).getServerResult();

		if (group != null)
		{
			if (userAuth.isAdmin() || group.canAccess(properties.getUserId()))
			{
				return exportForIds(properties, GroupManager.getItemIds(userAuth, groupId).getServerResult(), table);
			}
			else
			{
				throw new InsufficientPermissionsException();
			}
		}

		return new ServerResult<>(null, null);
	}

	@Override
	public ServerResult<String> exportForIds(RequestProperties properties, List<String> ids, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		String formatted;

		if(CollectionUtils.isEmpty(ids))
			return new ServerResult<>(null, null);

		switch (table)
		{
			case markers:
				formatted = String.format(QUERY_MARKERS_DATA_IDS_DOWNLOAD, StringUtils.generateSqlPlaceholderString(ids.size()));
				try (DefaultStreamer streamer = new DefaultQuery(formatted, userAuth)
						.setLong(userAuth.getId())
						.setStrings(ids)
						.getStreamer())
				{
					File output = createTemporaryFile("export_marker_group", FileType.txt.name());

					try
					{
						Util.writeDefaultToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, output);
					}
					catch (java.io.IOException e)
					{
						throw new IOException(e);
					}

					return new ServerResult<>(streamer.getDebugInfo(), output.getName());
				}

			case germinatebase:
				try (DefaultStreamer streamer = AccessionManager.getStreamerForIds(userAuth, ids))
				{
					File output = createTemporaryFile("export_accession_group", FileType.txt.name());

					try
					{
						Util.writeDefaultToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, output);
					}
					catch (java.io.IOException e)
					{
						throw new jhi.germinate.shared.exception.IOException(e);
					}

					return new ServerResult<>(streamer.getDebugInfo(), output.getName());
				}

			case locations:
				formatted = String.format(QUERY_COLLSITES_BY_IDS_DOWNLOAD, StringUtils.generateSqlPlaceholderString(ids.size()));
				try (DefaultStreamer streamer = new DefaultQuery(formatted, userAuth)
						.setStrings(ids)
						.getStreamer())
				{
					File output = createTemporaryFile("export_collsite_group", FileType.txt.name());

					try
					{
						Util.writeDefaultToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, output);
					}
					catch (java.io.IOException e)
					{
						throw new jhi.germinate.shared.exception.IOException(e);
					}

					return new ServerResult<>(streamer.getDebugInfo(), output.getName());
				}

			default:
				return new ServerResult<>(null, null);
		}
	}

	@Override
	public ServerResult<Tuple.Pair<Integer, Integer>> addItems(RequestProperties properties, String result, GerminateDatabaseTable referenceTable, String column, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InvalidColumnException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		try (BufferedReader reader = new BufferedReader(new FileReader(getFile(FileLocation.temporary, result))))
		{
			String line;

			List<String> lines = new ArrayList<>();
			while ((line = reader.readLine()) != null)
				lines.add(line);

			return addItems(properties, lines.toArray(new String[0]), referenceTable, column, groupId);
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
			throw new IOException(e);
		}
	}

	@Override
	public ServerResult<Tuple.Pair<Integer, Integer>> addItems(RequestProperties properties, String[] split, GerminateDatabaseTable referenceTable, String column, Long groupId) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		column = Util.checkSortColumn(column, referenceTable.getColumnNames(), null);

		/* Get the ids from the database */
		String query;

		switch (referenceTable)
		{
			case germinatebase:
				query = QUERY_NEW_GROUP_MEMBER_IDS_ACCESSIONS;
				break;
			case markers:
				query = QUERY_NEW_GROUP_MEMBER_IDS_MARKERS;
				break;
			case locations:
				query = QUERY_NEW_GROUP_MEMBER_IDS_LOCATION;
				break;
			default:
				return null;
		}

		int newItemCount = 0;
		DebugInfo debug = DebugInfo.create(userAuth);

		List<String> items = Arrays.asList(split);

		for (int i = 0; i < items.size(); i += 1000)
		{
			List<String> currentItems = items.subList(i, Math.min(items.size(), i + 1000));
			String formatted = String.format(query, column, StringUtils.generateSqlPlaceholderString(currentItems.size()));
			ServerResult<List<Long>> result = new ValueQuery(formatted, userAuth)
					.setStrings(currentItems)
					.run("id")
					.getLongs();

			if (result.getServerResult() != null)
			{
				ServerResult<Set<Long>> newIds = addItems(properties, groupId, result.getServerResult());
				newItemCount += newIds.getServerResult().size();

				debug.addAll(result.getDebugInfo());
				debug.addAll(newIds.getDebugInfo());
			}
		}

		return new ServerResult<>(debug, new Tuple.Pair<>(newItemCount, Math.max(0, split.length - newItemCount)));
	}

	@Override
	public PaginatedServerResult<List<Group>> getForAccession(RequestProperties properties, Long accessionId, Pagination pagination) throws InvalidSessionException, InvalidColumnException, DatabaseException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return GroupManager.getAllForAccession(userAuth, accessionId, pagination);
	}

	@Override
	public ServerResult<List<Group>> getAccessionGroups(RequestProperties properties, List<Long> datasetIds, ExperimentType type) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return GroupManager.getAccessionGroupsForDatasets(userAuth, datasetIds, type);
	}

	@Override
	public ServerResult<List<Group>> getMarkerGroups(RequestProperties properties, List<Long> datasetIds, ExperimentType type) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return GroupManager.getMarkerGroupsForDataset(userAuth, datasetIds, type);
	}

	@Override
	public ServerResult<Void> renameGroup(RequestProperties properties, Group group) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, SystemInReadOnlyModeException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return GroupManager.rename(userAuth, group);
	}
}
