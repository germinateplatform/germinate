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
public class PhenotypeManager extends AbstractManager<Phenotype>
{
	private static final String[] COLUMNS_SORTABLE = {Phenotype.ID, Phenotype.NAME, Phenotype.DESCRIPTION, Phenotype.CREATED_ON, Phenotype.UPDATED_ON, Unit.NAME, Unit.ABBREVIATION, Unit.DESCRIPTION, Phenotype.COUNT};

	private static final String SELECT_ALL_FOR_TYPE = "SELECT `phenotypes`.* FROM `phenotypes` WHERE EXISTS (SELECT 1 FROM `phenotypedata` LEFT JOIN `datasets` ON `datasets`.`id` = `phenotypedata`.`dataset_id` LEFT JOIN `experiments` ON `experiments`.`id` = `datasets`.`experiment_id` LEFT JOIN `experimenttypes` ON `experimenttypes`.`id` = `experiments`.`experiment_type_id` WHERE `phenotypes`.`id` = `phenotypedata`.`phenotype_id` AND `experimenttypes`.`description` = ? AND `datasets`.`id` IN (%s) LIMIT 1) %s ORDER BY `name`";

	private static final String COMMON_TABLES                 = " `germinatebase` LEFT JOIN `phenotypedata` ON `germinatebase`.`id` = `phenotypedata`.`germinatebase_id` LEFT JOIN `phenotypes` ON `phenotypes`.`id` = `phenotypedata`.`phenotype_id` LEFT JOIN `units` ON `units`.`id` = `phenotypes`.`unit_id` LEFT JOIN `datasets` ON `datasets`.`id` = `phenotypedata`.`dataset_id` LEFT JOIN `experiments` ON `experiments`.`id` = `datasets`.`experiment_id` LEFT JOIN `experimenttypes` ON `experimenttypes`.`id` = `experiments`.`experiment_type_id`";
	private static final String COMMON_TABLES_MIN             = " `phenotypes` LEFT JOIN `units` ON `units`.`id` = `phenotypes`.`unit_id` LEFT JOIN `phenotypedata` ON `phenotypedata`.`phenotype_id` = `phenotypes`.`id` ";
	private static final String SELECT_DATA_FOR_FILTER        = "SELECT * FROM " + COMMON_TABLES + " LEFT JOIN locations ON phenotypedata.location_id = locations.id LEFT JOIN countries ON countries.id = locations.country_id {{FILTER}} AND `datasets`.`id` IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_DATA_FOR_FILTER_EXPORT = "SELECT `germinatebase`.`id` AS germinatebase_id, `germinatebase`.`name` AS germinatebase_name, `germinatebase`.`general_identifier` AS germinatebase_gid, `datasets`.`name` AS datasets_name, `datasets`.`description` AS datasets_description, `experimenttypes`.`description` AS experimenttypes_description, `phenotypes`.`name` AS phenotypes_name, `phenotypes`.`short_name` AS phenotypes_short_name, `units`.`unit_name` AS units_name, `phenotypedata`.`phenotype_value` AS phenotypedata_value FROM " + COMMON_TABLES + " {{FILTER}} AND `datasets`.`id` IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_FILTER         = "SELECT DISTINCT `germinatebase`.`id` FROM " + COMMON_TABLES + " {{FILTER}} AND `datasets`.`id` IN (%s)";
	private static final String SELECT_ALL_FOR_FILTER         = "SELECT `phenotypes`.*, `units`.*, COUNT(1) AS count FROM " + COMMON_TABLES_MIN + " {{FILTER}} GROUP BY `phenotypes`.`id` %s LIMIT ?, ?";
	private static final String SELECT_HISTOGRAM_DATA         = "SELECT phenotype_value FROM phenotypedata WHERE phenotype_id = ? AND dataset_id = ? ";

	private static final String[] COLUMNS_PHENOTYPE_DATA_EXPORT = {"germinatebase_id", "germinatebase_name", "germinatebase_gid", "dataset_description", "experimenttypes_description", "phenotypes_name", "phenotypes_short_name", "units_name", "phenotypedata_value"};

	@Override
	protected String getTable()
	{
		return "phenotypes";
	}

	@Override
	protected DatabaseObjectParser<Phenotype> getParser()
	{
		return Phenotype.Parser.Inst.get();
	}

	/**
	 * Returns all the paginated {@link Phenotype}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param userAuth   The user requesting the data
	 * @param filter     The user-specified filter
	 * @param pagination The pagination object specifying the current chunk of data
	 * @return All the paginated {@link Phenotype}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<Phenotype>> getAllForFilter(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(COLUMNS_SORTABLE, Phenotype.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Phenotype>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, COLUMNS_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Phenotype.Parser.Inst.get(), true);
	}

	public static ServerResult<List<Phenotype>> getAllForType(UserAuth user, List<Long> datasetIds, ExperimentType type, boolean onlyNumeric) throws DatabaseException
	{
		String formatted = String.format(SELECT_ALL_FOR_TYPE, StringUtils.generateSqlPlaceholderString(datasetIds.size()), onlyNumeric ? "AND " + Phenotype.DATATYPE + " != 'char'" : "");

		DatasetManager.restrictToAvailableDatasets(user, datasetIds);

		return new DatabaseObjectQuery<Phenotype>(formatted, user)
				.setString(type.name())
				.setLongs(datasetIds)
				.run()
				.getObjects(Phenotype.Parser.Inst.get());
	}

	public static PaginatedServerResult<List<PhenotypeData>> getDataForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(PhenotypeService.COLUMNS_DATA_SORTABLE, Accession.ID);

		List<Long> datasetIds = DatabaseObject.getIds(DatasetManager.getForUser(user, true).getServerResult());

		if (CollectionUtils.isEmpty(datasetIds))
			return new PaginatedServerResult<>(DebugInfo.create(user), new ArrayList<>(), 0);

		String formatted = String.format(SELECT_DATA_FOR_FILTER, StringUtils.generateSqlPlaceholderString(datasetIds.size()), pagination.getSortQuery());

		return AbstractManager.<PhenotypeData>getFilteredDatabaseObjectQuery(user, filter, formatted, PhenotypeService.COLUMNS_DATA_SORTABLE, pagination.getResultSize())
				.setLongs(datasetIds)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(PhenotypeData.Parser.Inst.get(), true);
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(PhenotypeService.COLUMNS_DATA_SORTABLE, Accession.ID);
		List<Long> datasetIds = DatabaseObject.getIds(DatasetManager.getForUser(userAuth, true).getServerResult());

		String formatted = String.format(SELECT_DATA_FOR_FILTER_EXPORT, StringUtils.generateSqlPlaceholderString(datasetIds.size()), pagination.getSortQuery());

		return getFilteredGerminateTableQuery(userAuth, filter, formatted, PhenotypeService.COLUMNS_DATA_SORTABLE, COLUMNS_PHENOTYPE_DATA_EXPORT)
				.setLongs(datasetIds)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}

	public static ServerResult<List<String>> getIdsForFilter(UserAuth user, PartialSearchQuery filter) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		List<Long> datasetIds = DatabaseObject.getIds(DatasetManager.getForUser(user, true).getServerResult());

		if (CollectionUtils.isEmpty(datasetIds))
			return new PaginatedServerResult<>(DebugInfo.create(user), new ArrayList<>(), 0);

		String formatted = String.format(SELECT_IDS_FOR_FILTER, StringUtils.generateSqlPlaceholderString(datasetIds.size()));

		return AbstractManager.<CompoundData>getFilteredValueQuery(filter, user, formatted, PhenotypeService.COLUMNS_DATA_SORTABLE)
				.setLongs(datasetIds)
				.run(Accession.ID)
				.getStrings();
	}

	public static DefaultStreamer getStreamerForHistogramData(UserAuth userAuth, Long phenotypeId, Long datasetId) throws DatabaseException
	{
		ServerResult<Boolean> canAccess = DatasetManager.userHasAccessToDataset(userAuth, datasetId);

		if (canAccess.getServerResult())
		{
			return new DefaultQuery(SELECT_HISTOGRAM_DATA, userAuth)
					.setLong(phenotypeId)
					.setLong(datasetId)
					.getStreamer();
		}
		else
		{
			return null;
		}
	}
}
