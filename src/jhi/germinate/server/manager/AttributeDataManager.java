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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class AttributeDataManager extends AbstractManager<AttributeData>
{
	private static final String COMMON_TABLES                = "attributedata LEFT JOIN germinatebase ON germinatebase.id = attributedata.germinatebase_id LEFT JOIN attributes ON attributes.id = attributedata.attribute_id";
	private static final String SELECT_ALL_FOR_FILTER_EXPORT = "SELECT germinatebase.id AS germinatebase_id, germinatebase.general_identifier AS germinatebase_gid, germinatebase.name AS germinatebase_name, attributes.name AS attributes_name, attributes.datatype AS attributes_datatype, attributedata.value AS attributedata_value FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER        = "SELECT *                         FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_FILTER        = "SELECT DISTINCT germinatebase.id FROM " + COMMON_TABLES + " {{FILTER}}";

	private static final String[] COLUMNS_ATTRIBUTE_DATA_EXPORT = {"germinatebase_id", "germinatebase_gid", "germinatebase_name", "attributes_name", "attributes_datatype", "attributedata_value"};

	@Override
	protected String getTable()
	{
		return "attributedata";
	}

	@Override
	protected DatabaseObjectParser<AttributeData> getParser()
	{
		return AttributeData.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<AttributeData>> getAllForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(AttributeService.COLUMNS_SORTABLE, Accession.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<AttributeData>getFilteredDatabaseObjectQuery(user, filter, formatted, AttributeService.COLUMNS_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(AttributeData.Parser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getIdsForFilter(UserAuth user, PartialSearchQuery filter) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		return getFilteredValueQuery(filter, user, SELECT_IDS_FOR_FILTER, AttributeService.COLUMNS_SORTABLE)
				.run(Accession.ID)
				.getStrings();
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(AttributeService.COLUMNS_SORTABLE, Accession.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredGerminateTableQuery(filter, formatted, AttributeService.COLUMNS_SORTABLE, COLUMNS_ATTRIBUTE_DATA_EXPORT)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}
}
