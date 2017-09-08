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
public class PedigreeManager extends AbstractManager<Pedigree>
{
	private static final String COMMON_TABLES = " pedigrees LEFT JOIN pedigreedescriptions ON pedigreedescriptions.id = pedigrees.pedigreedescription_id LEFT JOIN germinatebase Child ON Child.id = pedigrees.germinatebase_id LEFT JOIN germinatebase Parent ON Parent.id = pedigrees.parent_id ";

	private static final String SELECT_ALL_FOR_ACCESSION     = "SELECT * FROM " + COMMON_TABLES + " WHERE pedigrees.germinatebase_id = ? %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER        = "SELECT * FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER_EXPORT = "SELECT pedigrees.id AS pedigrees_id, Child.general_identifier AS germinatebase_gid_child, Child.name AS germinatebase_name_child, Parent.general_identifier AS germinatebase_gid_parent, Parent.name AS germinatebase_name_parent, pedigrees.relationship_type AS pedigrees_relationship_type, pedigrees.relationship_description AS pedigrees_relationship_description, pedigreedescriptions.name AS pedigreedescriptions_name, pedigreedescriptions.author AS pedigreedescriptions_author FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";

	private static final String[] COLUMNS_PEDIGREE_DATA_EXPORT = {"pedigrees_id", "germinatebase_gid_child", "germinatebase_name_child", "germinatebase_gid_parent", "germinatebase_name_parent", "pedigrees_relationship_type", "pedigrees_relationship_description", "pedigreedescriptions_name", "pedigreedescriptions_author"};

	@Override
	protected String getTable()
	{
		return "pedigrees";
	}

	@Override
	protected DatabaseObjectParser<Pedigree> getParser()
	{
		return Pedigree.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<Pedigree>> getAllByAccessionId(UserAuth user, Long accessionId, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(PedigreeService.COLUMNS_PEDIGREE_SORTABLE, Pedigree.GERMINATEBASE_ID);

		String formatted = String.format(SELECT_ALL_FOR_ACCESSION, pagination.getSortQuery());

		return new DatabaseObjectQuery<Pedigree>(formatted, user)
				.setFetchesCount(pagination.getResultSize())
				.setLong(accessionId)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Pedigree.Parser.Inst.get());
	}

	public static PaginatedServerResult<List<Pedigree>> getAllForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		pagination.updateSortColumn(PedigreeService.COLUMNS_PEDIGREE_SORTABLE, Pedigree.GERMINATEBASE_ID);

		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Pedigree>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, PedigreeService.COLUMNS_PEDIGREE_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Pedigree.Parser.Inst.get(), false);
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		pagination.updateSortColumn(PedigreeService.COLUMNS_PEDIGREE_SORTABLE, Pedigree.GERMINATEBASE_ID);

		String formatted = String.format(SELECT_ALL_FOR_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredGerminateTableQuery(userAuth, filter, formatted, PedigreeService.COLUMNS_PEDIGREE_SORTABLE, COLUMNS_PEDIGREE_DATA_EXPORT)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}
}
