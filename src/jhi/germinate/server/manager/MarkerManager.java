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
	private static final String SELECT_BY_IDS             = "SELECT markers.* FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id WHERE markers.id IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_SEARCH     = "SELECT mapdefinitions.* FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id LEFT JOIN mapfeaturetypes ON mapfeaturetypes.id = mapdefinitions.mapfeaturetype_id WHERE %s LIKE ? GROUP BY markers.id %s LIMIT ?, ?";
	private static final String COMMON_TABLES             = "mapdefinitions LEFT JOIN mapfeaturetypes ON mapdefinitions.mapfeaturetype_id = mapfeaturetypes.id LEFT JOIN markers ON markers.id = mapdefinitions.marker_id LEFT JOIN maps ON maps.id = mapdefinitions.map_id";
	private static final String SELECT_ALL_FOR_GROUP      = "SELECT * FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id LEFT JOIN groupmembers ON markers.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id WHERE groups.id = ? GROUP BY markers.id, groupmembers.id %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_GROUP      = "SELECT markers.id FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id LEFT JOIN groupmembers ON markers.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id WHERE groups.id = ? GROUP BY markers.id, groupmembers.id";
	private static final String SELECT_BY_NAME            = "SELECT * FROM " + COMMON_TABLES + " WHERE markers.marker_name = ?";
	private static final String SELECT_IDS_FOR_FILTER_MAP = "SELECT DISTINCT(markers.id) FROM " + COMMON_TABLES + " {{FILTER}} AND (maps.user_id = ? OR maps.visibility = 1)";
	private static final String SELECT_IDS_FOR_FILTER     = "SELECT DISTINCT(markers.id) FROM " + COMMON_TABLES + " {{FILTER}}";
	private static final String SELECT_COUNT              = "SELECT COUNT(1) AS count FROM markers";

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

	public static ServerResult<Marker> getByName(UserAuth userAuth, String name) throws DatabaseException
	{
		return new DatabaseObjectQuery<Marker>(SELECT_BY_NAME, userAuth)
				.setString(name)
				.run()
				.getObject(Marker.Parser.Inst.get());
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
