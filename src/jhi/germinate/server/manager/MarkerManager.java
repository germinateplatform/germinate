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
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * NOTE: All methods returning map related data will check the visibility of the map first. If the map is either visible or the author is requesting
 * the information, then it'll be returned.
 *
 * @author Sebastian Raubach
 */
public class MarkerManager extends AbstractManager<Marker>
{
	public static final  String COMMOM_SYNONYMS             = "LEFT JOIN synonyms ON (synonyms.foreign_id = markers.id AND synonyms.synonymtype_id = " + SynonymType.markers.getId() + " )";
	public static final  String SELECT_SYNONYMS             = "mapdefinitions.*, mapfeaturetypes.*, maps.*, markers.*, GROUP_CONCAT(synonyms.synonym SEPARATOR ', ') AS synonyms";
	private static final String COMMON_TABLES               = "mapdefinitions LEFT JOIN mapfeaturetypes ON mapdefinitions.mapfeaturetype_id = mapfeaturetypes.id LEFT JOIN markers ON markers.id = mapdefinitions.marker_id LEFT JOIN maps ON maps.id = mapdefinitions.map_id";
	private static final String SELECT_BY_IDS               = "SELECT markers.* FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id WHERE markers.id IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_GROUP        = "SELECT markers.*, markertypes.*, GROUP_CONCAT(synonyms.synonym SEPARATOR ', ') AS synonyms FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id LEFT JOIN groupmembers ON markers.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id " + COMMOM_SYNONYMS + " WHERE groups.id = ? GROUP BY markers.id, groupmembers.id %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_GROUP        = "SELECT markers.id FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id LEFT JOIN groupmembers ON markers.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id WHERE groups.id = ? GROUP BY markers.id, groupmembers.id";
	private static final String SELECT_FOR_FILTER           = "SELECT " + SELECT_SYNONYMS + " FROM " + COMMON_TABLES + " " + COMMOM_SYNONYMS + " {{FILTER}} GROUP BY markers.id, mapdefinitions.id %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_FILTER_MAP   = "SELECT DISTINCT(markers.id) FROM " + COMMON_TABLES + " {{FILTER}} AND (maps.user_id = ? OR maps.visibility = 1)";
	private static final String SELECT_NAMES_FOR_FILTER_MAP = "SELECT DISTINCT(markers.marker_name) FROM " + COMMON_TABLES + " LEFT JOIN groupmembers ON groupmembers.foreign_id = markers.id LEFT JOIN groups ON groups.id = groupmembers.group_id {{FILTER}} AND (maps.user_id = ? OR maps.visibility = 1)";
	private static final String SELECT_COUNT                = "SELECT COUNT(1) AS count FROM markers";

	@Override
	protected String getTable()
	{
		return "markers";
	}

	@Override
	protected DatabaseObjectParser<Marker> getParser()
	{
		return Marker.Parser.Inst.get();
	}

	public static ServerResult<List<String>> getIdsForFilter(UserAuth userAuth, PartialSearchQuery filter) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		return getFilteredValueQuery(filter, userAuth, SELECT_IDS_FOR_FILTER_MAP, MarkerService.COLUMNS_MAPDEFINITION_TABLE)
				.setLong(userAuth.getId())
				.run(Marker.ID)
				.getStrings();
	}

	public static PaginatedServerResult<List<Marker>> getForFilter(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		pagination.updateSortColumn(AccessionService.COLUMNS_SORTABLE, Marker.ID);
		String formatted = String.format(SELECT_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Marker>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, MarkerService.COLUMNS_MARKER_TABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Marker.Parser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getNamesForFilter(UserAuth userAuth, PartialSearchQuery filter) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		return getFilteredValueQuery(filter, userAuth, SELECT_NAMES_FOR_FILTER_MAP, MarkerService.COLUMNS_MAPDEFINITION_TABLE)
				.setLong(userAuth.getId())
				.run(Marker.MARKER_NAME)
				.getStrings();
	}

	public static ServerResult<List<Marker>> getByIds(UserAuth userAuth, List<String> ids, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(MarkerService.COLUMNS_MARKER_TABLE, Marker.ID);

		String formatted = String.format(SELECT_BY_IDS, Util.generateSqlPlaceholderString(ids.size()), pagination.getSortQuery());
		return new DatabaseObjectQuery<Marker>(formatted, userAuth)
				.setStrings(ids)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjects(Marker.Parser.Inst.get());
	}

	public static PaginatedServerResult<List<Marker>> getAllForGroup(UserAuth userAuth, Long groupId, Pagination pagination) throws DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		pagination.updateSortColumn(MarkerService.COLUMNS_MARKER_TABLE, Marker.ID);
		String formatted = String.format(SELECT_ALL_FOR_GROUP, pagination.getSortQuery());

		return new DatabaseObjectQuery<Marker>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLong(groupId)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Marker.Parser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getIdsForGroup(UserAuth userAuth, Long groupId) throws DatabaseException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		return new ValueQuery(SELECT_IDS_FOR_GROUP, userAuth)
				.setLong(groupId)
				.run(Marker.ID)
				.getStrings();
	}

	public static ServerResult<Long> getCount(UserAuth user) throws DatabaseException
	{
		return new ValueQuery(SELECT_COUNT, user)
				.run(COUNT)
				.getLong(0L);
	}
}
