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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.exception.*;

/**
 * NOTE: All methods returning map related data will check the visibility of the map first. If the map is either visible or the author is requesting
 * the information, then it'll be returned.
 *
 * @author Sebastian Raubach
 */
public class MapManager extends AbstractManager<Map>
{
	private static final String SELECT_ALL                    = "SELECT maps.*, COUNT(1) AS count          FROM maps LEFT JOIN mapdefinitions ON mapdefinitions.map_id = maps.id WHERE maps.user_id <=> ? OR maps.visibility = 1 GROUP BY maps.id %s LIMIT ?, ?";
	private static final String SELECT_ALL_HAVING_ALLELE_FREQ = "SELECT maps.*, COUNT(markers.id) AS count FROM maps LEFT JOIN mapdefinitions ON mapdefinitions.map_id = maps.id LEFT JOIN markers ON mapdefinitions.marker_id = markers.id WHERE EXISTS (SELECT 1 FROM allelefrequencydata WHERE allelefrequencydata.marker_id = markers.id) AND (maps.user_id = ? OR maps.visibility = 1) GROUP BY maps.id %s LIMIT ?, ?";

	@Override
	protected String getTable()
	{
		return "maps";
	}

	@Override
	protected DatabaseObjectParser<Map> getParser()
	{
		return Map.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<Map>> getAll(UserAuth userAuth, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(MapService.COLUMNS_MAP_SORTABLE, Map.ID);

		String formatted = String.format(SELECT_ALL, pagination.getSortQuery());

		return new DatabaseObjectQuery<Map>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLong(userAuth.getId())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Map.Parser.Inst.get());
	}

	public static PaginatedServerResult<List<Map>> getAllHavingAlleleFreqData(UserAuth userAuth, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(MapService.COLUMNS_MAP_SORTABLE, Map.ID);

		String formatted = String.format(SELECT_ALL_HAVING_ALLELE_FREQ, pagination.getSortQuery());

		return new DatabaseObjectQuery<Map>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLong(userAuth.getId())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Map.Parser.Inst.get());
	}
}
