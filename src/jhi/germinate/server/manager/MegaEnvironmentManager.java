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

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class MegaEnvironmentManager extends AbstractManager<MegaEnvironment>
{
	private static final String[] COLUMNS_SORT = {"id", "name", COUNT};

	private static final String SELECT_ALL = "SELECT * FROM ((SELECT megaenvironments.id, megaenvironments.name, COUNT( DISTINCT locations.id ) AS count FROM megaenvironments LEFT JOIN megaenvironmentdata ON megaenvironmentdata.megaenvironment_id = megaenvironments.id LEFT JOIN locations ON locations.id = megaenvironmentdata.location_id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id WHERE locationtypes.name = 'collectingsites' AND megaenvironmentdata.is_final = 1 GROUP BY megaenvironments.id ORDER BY megaenvironments.name) UNION ( SELECT - 1 AS id, 'UNKNOWN' AS name, COUNT( DISTINCT locations.id ) AS count FROM locations LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id WHERE locationtypes.name = 'collectingsites' AND NOT EXISTS ( SELECT 1 FROM megaenvironmentdata WHERE megaenvironmentdata.is_final = 1 AND megaenvironmentdata.location_id = locations.id ))) a %s LIMIT ?, ?";

	@Override
	protected String getTable()
	{
		return "megaenvironments";
	}

	@Override
	protected DatabaseObjectParser<MegaEnvironment> getParser()
	{
		return MegaEnvironment.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<MegaEnvironment>> getAll(UserAuth userAuth, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_SORT, "id");

		String formatted = String.format(SELECT_ALL, pagination.getSortQuery());

		return new DatabaseObjectQuery<MegaEnvironment>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(MegaEnvironment.IdNameParser.Inst.get());
	}
}
