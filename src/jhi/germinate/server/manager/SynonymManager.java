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
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * SynonymManager handles all interactions with the database that concern synonyms.
 *
 * @author Sebastian Raubach
 */
public class SynonymManager extends AbstractManager<Synonym>
{
	private static final String COMMON_TABLES = " synonyms LEFT JOIN synonymtypes ON synonymtypes.id = synonyms.synonymtype_id ";

	private static final String SELECT_ALL_FOR_TABLE = "SELECT * FROM " + COMMON_TABLES + " WHERE synonymtypes.id = ? AND synonyms.foreign_id = ?";

	@Override
	protected String getTable()
	{
		return "synonyms";
	}

	@Override
	protected DatabaseObjectParser<Synonym> getParser()
	{
		return Synonym.Parser.Inst.get();
	}

	public static ServerResult<List<Synonym>> getAllForTable(UserAuth userAuth, GerminateDatabaseTable table, Long id) throws DatabaseException
	{
		ServerResult<SynonymType> type = SynonymTypeManager.getForTable(userAuth, table);

		if (type.getServerResult() != null)
		{
			return new DatabaseObjectQuery<Synonym>(SELECT_ALL_FOR_TABLE, userAuth)
					.setLong(type.getServerResult().getId())
					.setLong(id)
					.run()
					.getObjects(Synonym.Parser.Inst.get(), true);
		}
		else
		{
			return new ServerResult<>(type.getDebugInfo(), null);
		}
	}
}
