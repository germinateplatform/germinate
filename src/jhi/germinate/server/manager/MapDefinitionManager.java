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
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class MapDefinitionManager extends AbstractManager<MapDefinition>
{
	private static final String COMMON_TABLES                = "markers LEFT JOIN mapdefinitions ON markers.id = mapdefinitions.marker_id LEFT JOIN mapfeaturetypes ON mapfeaturetypes.id = mapdefinitions.mapfeaturetype_id LEFT JOIN maps ON maps.id = mapdefinitions.map_id";
	private static final String SELECT_FOR_MARKER            = "SELECT * FROM " + COMMON_TABLES + " WHERE mapdefinitions.marker_id = ? GROUP BY maps.id, markers.id, mapdefinitions.id ORDER BY mapdefinitions.chromosome, mapdefinitions.definition_start LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER_EXPORT = "SELECT markers.id AS markers_id, markers.marker_name AS markers_marker_name, mapfeaturetypes.description AS markertypes_description, maps.description AS maps_description, mapdefinitions.chromosome AS mapdefinitions_chromosome, mapdefinitions.definition_start AS mapdefinitions_definition_start, GROUP_CONCAT(synonyms.synonym SEPARATOR ', ') AS synonyms_synonym FROM " + COMMON_TABLES + " " + MarkerManager.COMMOM_SYNONYMS + " {{FILTER}} AND " + MarkerManager.WHERE_SYNONYMS + " AND (maps.user_id = ? OR maps.visibility = 1) GROUP BY markers.id, mapdefinitions.id %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER        = "SELECT " + MarkerManager.SELECT_SYNONYMS + " FROM " + COMMON_TABLES + " " + MarkerManager.COMMOM_SYNONYMS + " {{FILTER}} AND " + MarkerManager.WHERE_SYNONYMS + " AND (maps.user_id = ? OR maps.visibility = 1) GROUP BY markers.id, mapdefinitions.id %s LIMIT ?, ?";

	private static final String[] COLUMNS_MARKER_DATA_EXPORT = {"markers_id", "markers_marker_name", "markertypes_description", "maps_description", "mapdefinitions_chromosome", "mapdefinitions_definition_start", "synonyms_synonym"};

	@Override
	protected String getTable()
	{
		return "mapdefinitions";
	}

	@Override
	protected DatabaseObjectParser<MapDefinition> getParser()
	{
		return MapDefinition.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<MapDefinition>> getForMarker(UserAuth userAuth, Long markerId, Pagination pagination) throws DatabaseException
	{
		return new DatabaseObjectQuery<MapDefinition>(SELECT_FOR_MARKER, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLong(markerId)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(MapDefinition.Parser.Inst.get(), true);
	}

	public static PaginatedServerResult<List<MapDefinition>> getForFilter(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (StringUtils.isEmpty(pagination.getSortColumn()))
			pagination.setSortColumn(MapDefinition.CHROMOSOME + ", " + MapDefinition.DEFINITION_START);
		else
			pagination.updateSortColumn(MarkerService.COLUMNS_MAPDEFINITION_TABLE, MapDefinition.CHROMOSOME + ", " + MapDefinition.DEFINITION_START);

		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<MapDefinition>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, MarkerService.COLUMNS_MAPDEFINITION_TABLE, pagination.getResultSize())
				.setLong(userAuth.getId())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(MapDefinition.Parser.Inst.get(), true);
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(MarkerService.COLUMNS_MAPDEFINITION_TABLE, MapDefinition.CHROMOSOME + ", " + MapDefinition.DEFINITION_START);
		String formatted = String.format(SELECT_ALL_FOR_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredGerminateTableQuery(userAuth, filter, formatted, MarkerService.COLUMNS_MAPDEFINITION_TABLE, COLUMNS_MARKER_DATA_EXPORT)
				.setLong(userAuth.getId())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}
}
