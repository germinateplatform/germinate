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
 * @author Sebastian Raubach
 */
public class SynonymTypeManager extends AbstractManager<SynonymType>
{
	private static final String SELECT_ALL           = "SELECT * FROM synonymtypes WHERE target_table = ? ORDER BY description";
	private static final String SELECT_ALL_FOR_TABLE = "SELECT * FROM synonymtypes WHERE target_table = ?";

	@Override
	protected String getTable()
	{
		return "synonymtypes";
	}

	@Override
	protected DatabaseObjectParser<SynonymType> getParser()
	{
		return SynonymType.Parser.Inst.get();
	}

	public static ServerResult<List<SynonymType>> getAll(UserAuth userAuth, GerminateDatabaseTable reference) throws DatabaseException
	{
		return new DatabaseObjectQuery<SynonymType>(SELECT_ALL, userAuth)
				.setString(reference.name())
				.run()
				.getObjects(SynonymType.Parser.Inst.get());
	}

	public static ServerResult<SynonymType> getForTable(UserAuth userAuth, GerminateDatabaseTable table) throws DatabaseException
	{
		return new DatabaseObjectQuery<SynonymType>(SELECT_ALL_FOR_TABLE, userAuth)
				.setString(table.name())
				.run()
				.getObject(SynonymType.Parser.Inst.get());
	}
}
