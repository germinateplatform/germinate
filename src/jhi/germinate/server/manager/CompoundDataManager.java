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
public class CompoundDataManager extends AbstractManager<CompoundData>
{
	private static final String[] COLUMNS_DATA_SORTABLE = {CompoundData.ID, Accession.GENERAL_IDENTIFIER, Accession.NAME, EntityType.NAME, Compound.NAME, Dataset.NAME, Dataset.DESCRIPTION, AnalysisMethod.NAME, Unit.NAME, CompoundData.COMPOUND_VALUE, Compound.COUNT};

	private static final String COMMON_TABLES = " `compounddata` LEFT JOIN `compounds` ON `compounds`.`id` = `compounddata`.`compound_id` LEFT JOIN `germinatebase` ON `germinatebase`.`id` = `compounddata`.`germinatebase_id` LEFT JOIN `entitytypes` ON `entitytypes`.`id` = `germinatebase`.`entitytype_id` LEFT JOIN `datasets` ON `datasets`.`id` = `compounddata`.`dataset_id` LEFT JOIN `analysismethods` ON `analysismethods`.`id` = `compounddata`.`analysismethod_id` LEFT JOIN `units` ON `units`.`id` = `compounds`.`unit_id` ";

	private static final String SELECT_IDS_FOR_FILTER        = "SELECT DISTINCT(`germinatebase`.`id`) FROM " + COMMON_TABLES + " {{FILTER}} AND `datasets`.`id` IN (%s)";
	private static final String SELECT_ALL_FOR_FILTER        = "SELECT * FROM " + COMMON_TABLES + " {{FILTER}} AND `datasets`.`id` IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER_EXPORT = "SELECT `germinatebase`.`id` AS germinatebase_id, `germinatebase`.`general_identifier` AS germinatebase_gid, `germinatebase`.`name` AS germinatebase_name, `compounds`.`name` AS compounds_name, `datasets`.`name` AS datasets_name, `datasets`.`description` AS datasets_description, `analysismethods`.`name` AS analysismethods_name, `units`.`unit_name` AS units_unit_name, `compounddata`.`compound_value` AS compounddata_compound_value FROM " + COMMON_TABLES + " {{FILTER}} AND `datasets`.`id` IN (%s) %s LIMIT ?, ?";

	private static final String[] COLUMNS_COMPOUND_DATA_EXPORT = {"germinatebase_id", "germinatebase_gid", "germinatebase_name", "compounds_name", "datasets_description", "analysismethods_name", "units_unit_name", "compounddata_compound_value"};

	@Override
	protected String getTable()
	{
		return "compounddata";
	}

	@Override
	protected DatabaseObjectParser<CompoundData> getParser()
	{
		return CompoundData.Parser.Inst.get();
	}

	/**
	 * Returns all the paginated {@link CompoundData}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param userAuth   The user requesting the data
	 * @param filter     The user-specified filter
	 * @param pagination The pagination object specifying the current chunk of data
	 * @return All the paginated {@link CompoundData}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<CompoundData>> getAllForFilter(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		List<Long> datasetIds = DatabaseObject.getIds(DatasetManager.getForUser(userAuth, true).getServerResult());

		if (CollectionUtils.isEmpty(datasetIds))
			return new PaginatedServerResult<>(DebugInfo.create(userAuth), new ArrayList<>(), 0);

		pagination.updateSortColumn(COLUMNS_DATA_SORTABLE, null);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, StringUtils.generateSqlPlaceholderString(datasetIds.size()), pagination.getSortQuery());

		return AbstractManager.<CompoundData>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, COLUMNS_DATA_SORTABLE, pagination.getResultSize())
				.setLongs(datasetIds)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(CompoundData.Parser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getIdsForFilter(UserAuth userAuth, PartialSearchQuery filter) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		List<Long> datasetIds = DatabaseObject.getIds(DatasetManager.getForUser(userAuth, true).getServerResult());

		if (CollectionUtils.isEmpty(datasetIds))
			return new PaginatedServerResult<>(DebugInfo.create(userAuth), new ArrayList<>(), 0);

		String formatted = String.format(SELECT_IDS_FOR_FILTER, StringUtils.generateSqlPlaceholderString(datasetIds.size()));

		return AbstractManager.getFilteredValueQuery(filter, userAuth, formatted, COLUMNS_DATA_SORTABLE)
				.setLongs(datasetIds)
				.run(Accession.ID)
				.getStrings();
	}

	public static DefaultStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(COLUMNS_DATA_SORTABLE, Accession.ID);
		List<Long> datasetIds = DatabaseObject.getIds(DatasetManager.getForUser(userAuth, true).getServerResult());

		String formatted = String.format(SELECT_ALL_FOR_FILTER_EXPORT, StringUtils.generateSqlPlaceholderString(datasetIds.size()), pagination.getSortQuery());

		return getFilteredDefaultQuery(userAuth, filter, formatted, COLUMNS_DATA_SORTABLE)
				.setLongs(datasetIds)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}
}
