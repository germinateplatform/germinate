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
public class AttributeManager extends AbstractManager<Attribute>
{
	private static final String[] COLUMNS_SORTABLE = {Attribute.ID, Attribute.NAME, Attribute.DESCRIPTION, Attribute.TARGET_TABLE, AttributeData.FOREIGN_ID, AttributeData.VALUE};

	private static final String SELECT_ALL_FOR_FILTER = "SELECT DISTINCT attributes.* FROM attributes LEFT JOIN attributedata ON attributes.id = attributedata.attribute_id {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_BY_ID          = "SELECT DISTINCT attributes.* FROM attributes WHERE id IN (%s)";

	@Override
	protected String getTable()
	{
		return "attributes";
	}

	@Override
	protected DatabaseObjectParser<Attribute> getParser()
	{
		return Attribute.Parser.Inst.get();
	}

	/**
	 * Returns all the paginated {@link Attribute}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param user       The user requesting the data
	 * @param filter     The user-specified filter
	 * @param pagination The pagination object specifying the current chunk of data
	 * @return All the paginated {@link Attribute}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<Attribute>> getAllForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_SORTABLE, Attribute.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Attribute>getFilteredDatabaseObjectQuery(user, filter, formatted, COLUMNS_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Attribute.Parser.Inst.get(), true);
	}

	public static ServerResult<List<Attribute>> getForIds(UserAuth user, List<Long> attributeIds) throws DatabaseException
	{
		String formatted = String.format(SELECT_BY_ID, StringUtils.generateSqlPlaceholderString(attributeIds.size()));

		return new DatabaseObjectQuery<Attribute>(formatted, user)
				.setLongs(attributeIds)
				.run()
				.getObjects(Attribute.Parser.Inst.get(), true);
	}
}
