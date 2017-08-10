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
public class GroupTypeManager extends AbstractManager<GroupType>
{
	private static final String SELECT_FOR_TYPE = "SELECT id FROM grouptypes WHERE target_table = ?";
	private static final String SELECT_ALL      = "SELECT * FROM grouptypes ORDER BY description ASC";

	@Override
	protected String getTable()
	{
		return "grouptypes";
	}

	@Override
	protected DatabaseObjectParser<GroupType> getParser()
	{
		return GroupType.Parser.Inst.get();
	}

	public static ServerResult<Long> getForType(UserAuth userAuth, GerminateDatabaseTable table) throws DatabaseException
	{
		return new ValueQuery(SELECT_FOR_TYPE, userAuth)
				.setString(table.name())
				.run(GroupType.ID)
				.getLong();
	}

	public static ServerResult<List<GroupType>> getAll(UserAuth userAuth) throws DatabaseException
	{
		return new DatabaseObjectQuery<GroupType>(SELECT_ALL, userAuth)
				.run()
				.getObjects(GroupType.Parser.Inst.get());
	}
}
