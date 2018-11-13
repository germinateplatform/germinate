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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class LinkTypeManager extends AbstractManager<LinkType>
{
	private static final String SELECT_ALL_FOR_TARGET_TABLE = "SELECT * FROM `linktypes` WHERE linktypes.target_table = ?";

	@Override
	protected String getTable()
	{
		return "linktypes";
	}

	@Override
	protected DatabaseObjectParser<LinkType> getParser()
	{
		return LinkType.Parser.Inst.get();
	}

	public static ServerResult<List<LinkType>> getForTargetTable(GerminateDatabaseTable table, UserAuth auth) throws DatabaseException
	{
		return new DatabaseObjectQuery<LinkType>(SELECT_ALL_FOR_TARGET_TABLE, auth)
				.setString(table.name())
				.run()
				.getObjects(LinkType.Parser.Inst.get());

	}
}
