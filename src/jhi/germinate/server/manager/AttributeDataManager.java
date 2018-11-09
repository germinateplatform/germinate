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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class AttributeDataManager extends AbstractManager<AttributeData>
{
	private static final String COMMON_TABLES_ACCESSIONS               = "`attributedata` LEFT JOIN `germinatebase` ON `germinatebase`.`id` = `attributedata`.`foreign_id` LEFT JOIN `attributes` ON `attributes`.`id` = `attributedata`.`attribute_id`";
	private static final String SELECT_ALL_FOR_ACCESSION_FILTER_EXPORT = "SELECT `germinatebase`.`id` AS germinatebase_id, `germinatebase`.`general_identifier` AS germinatebase_gid, `germinatebase`.`name` AS germinatebase_name, `attributes`.`name` AS attributes_name, `attributes`.`datatype` AS attributes_datatype, `attributedata`.`value` AS attributedata_value FROM " + COMMON_TABLES_ACCESSIONS + " {{FILTER}} AND `attributes`.`target_table` = 'germinatebase' %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_ACCESSION_FILTER        = "SELECT *                             FROM " + COMMON_TABLES_ACCESSIONS + " {{FILTER}} AND `attributes`.`target_table` = 'germinatebase' %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_ACCESSION_FILTER        = "SELECT DISTINCT `germinatebase`.`id` FROM " + COMMON_TABLES_ACCESSIONS + " {{FILTER}} AND `attributes`.`target_table` = 'germinatebase'";

	private static final String COMMON_TABLES_DATASETS               = "`attributedata` LEFT JOIN `datasets` ON `datasets`.`id` = `attributedata`.`foreign_id` LEFT JOIN `attributes` ON `attributes`.`id` = `attributedata`.`attribute_id`";
	private static final String SELECT_ALL_FOR_DATASET_FILTER_EXPORT = "SELECT `datasets`.`id` AS dataset_id, `datasets`.`name` AS datasets_name, `datasets`.`description` AS datasets_description, `attributes`.`name` AS attributes_name, `attributes`.`datatype` AS attributes_datatype, `attributedata`.`value` AS attributedata_value FROM " + COMMON_TABLES_DATASETS + " {{FILTER}} AND `attributes`.`target_table` = 'datasets' %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_DATASET_FILTER        = "SELECT *                         FROM " + COMMON_TABLES_DATASETS + " {{FILTER}} AND `attributes`.`target_table` = 'datasets' %s LIMIT ?, ?";

	private static final String[] COLUMNS_ATTRIBUTE_ACCESSION_DATA_EXPORT = {"germinatebase_id", "germinatebase_gid", "germinatebase_name", "attributes_name", "attributes_datatype", "attributedata_value"};
	private static final String[] COLUMNS_ATTRIBUTE_DATASET_DATA_EXPORT   = {"dataset_id", "dataset_description", "attributes_name", "attributes_datatype", "attributedata_value"};

	@Override
	protected String getTable()
	{
		return "attributedata";
	}

	@Override
	protected DatabaseObjectParser<AttributeData> getParser()
	{
		return AttributeData.AccessionParser.Inst.get();
	}

	public static PaginatedServerResult<List<AttributeData>> getAllForAccessionFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(AttributeService.COLUMNS_SORTABLE_ACCESSION, Accession.ID);
		String formatted = String.format(SELECT_ALL_FOR_ACCESSION_FILTER, pagination.getSortQuery());

		return AbstractManager.<AttributeData>getFilteredDatabaseObjectQuery(user, filter, formatted, AttributeService.COLUMNS_SORTABLE_ACCESSION, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(AttributeData.AccessionParser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getIdsForAccessionFilter(UserAuth user, PartialSearchQuery filter) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		return getFilteredValueQuery(filter, user, SELECT_IDS_FOR_ACCESSION_FILTER, AttributeService.COLUMNS_SORTABLE_ACCESSION)
				.run(Accession.ID)
				.getStrings();
	}

	public static DefaultStreamer getStreamerForAccessionFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(AttributeService.COLUMNS_SORTABLE_ACCESSION, Accession.ID);
		String formatted = String.format(SELECT_ALL_FOR_ACCESSION_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredDefaultQuery(userAuth, filter, formatted, AttributeService.COLUMNS_SORTABLE_ACCESSION)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}

	public static PaginatedServerResult<List<AttributeData>> getAllForDatasetFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination, boolean recursive) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(AttributeService.COLUMNS_SORTABLE_DATASET, Dataset.ID);
		String formatted = String.format(SELECT_ALL_FOR_DATASET_FILTER, pagination.getSortQuery());

		return AbstractManager.<AttributeData>getFilteredDatabaseObjectQuery(user, filter, formatted, AttributeService.COLUMNS_SORTABLE_DATASET, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(recursive ? AttributeData.DatasetParser.Inst.get() : AttributeData.NonRecursiveDatasetParser.Inst.get(), true);
	}

	public static DefaultStreamer getStreamerForDatasetFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(AttributeService.COLUMNS_SORTABLE_DATASET, Dataset.ID);
		String formatted = String.format(SELECT_ALL_FOR_DATASET_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredDefaultQuery(userAuth, filter, formatted, AttributeService.COLUMNS_SORTABLE_DATASET)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}
}
