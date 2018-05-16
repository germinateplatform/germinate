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
public class CompoundManager extends AbstractManager<Compound>
{
	private static final String COMMON_TABLES = " compounds LEFT JOIN units ON units.id = compounds.unit_id ";

	private static final String SELECT_ALL_FOR_FILTER = "SELECT * FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";

	private static final String SELECT_ALL_FOR_DATASET = "SELECT * FROM " + COMMON_TABLES + " WHERE EXISTS (SELECT 1 FROM compounddata WHERE compounddata.compound_id = compounds.id AND compounddata.dataset_id IN (%s))";

	@Override
	protected String getTable()
	{
		return "compounds";
	}

	@Override
	protected DatabaseObjectParser<Compound> getParser()
	{
		return Compound.Parser.Inst.get();
	}

	/**
	 * Returns all the paginated {@link Compound}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param userAuth   The user requesting the data
	 * @param filter     The user-specified filter
	 * @param pagination The pagination object specifying the current chunk of data
	 * @return All the paginated {@link Compound}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<Compound>> getAllForFilter(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(CompoundService.COLUMNS_SORTABLE, Compound.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Compound>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, CompoundService.COLUMNS_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Compound.Parser.Inst.get(), true);
	}

	/**
	 * Returns all the {@link Compound}s for the given dataset ids.
	 *
	 * @param userAuth   The user requesting the data
	 * @param datasetIds The ids of the {@link Dataset}s
	 * @return All the {@link Compound}s for the given dataset ids.
	 * @throws DatabaseException Thrown if the interaction with the database failed
	 */
	public static ServerResult<List<Compound>> getAllForDataset(UserAuth userAuth, List<Long> datasetIds) throws DatabaseException
	{
		String formatted = String.format(SELECT_ALL_FOR_DATASET, StringUtils.generateSqlPlaceholderString(datasetIds.size()));

		return new DatabaseObjectQuery<Compound>(formatted, userAuth)
				.setLongs(datasetIds)
				.run()
				.getObjects(Compound.Parser.Inst.get());
	}
}
