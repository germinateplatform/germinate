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
public class InstitutionManager extends AbstractManager<Institution>
{
	private static final String COMMON_TABLES         = " `institutions` LEFT JOIN `countries` ON `countries`.`id` = `institutions`.`country_id`";
	private static final String SELECT_ALL_FOR_FILTER = "SELECT `institutions`.* FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";

	private static final String SELECT_GROUPED_BY_COUNTRY = "SELECT `countries`.*, COUNT(1) AS count FROM " + COMMON_TABLES + " GROUP BY `countries`.`id`";

	@Override
	protected String getTable()
	{
		return "institutions";
	}

	@Override
	protected DatabaseObjectParser<Institution> getParser()
	{
		return Institution.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<Institution>> getAllForFilter(UserAuth userAuth, Pagination pagination, PartialSearchQuery filter) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		pagination.updateSortColumn(LocationService.COLUMNS_INSTITUTION_SORTABLE, Institution.ID);

		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Institution>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, LocationService.COLUMNS_INSTITUTION_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Institution.Parser.Inst.get());
	}

	public static ServerResult<List<Country>> getGroupedByCountry(UserAuth userAuth) throws DatabaseException
	{
		return new DatabaseObjectQuery<Country>(SELECT_GROUPED_BY_COUNTRY, userAuth)
				.run()
				.getObjects(Country.CountParser.Inst.get());
	}
}
