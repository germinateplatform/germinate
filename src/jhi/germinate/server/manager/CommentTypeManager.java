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
public class CommentTypeManager extends AbstractManager<CommentType>
{
	private static final String SELECT_ALL = "SELECT * FROM commenttypes WHERE reference_table = ? ORDER BY description";

	@Override
	protected String getTable()
	{
		return "commenttypes";
	}

	@Override
	protected DatabaseObjectParser<CommentType> getParser()
	{
		return CommentType.Parser.Inst.get();
	}

	public static ServerResult<List<CommentType>> getAll(UserAuth userAuth, GerminateDatabaseTable reference) throws DatabaseException
	{
		return new DatabaseObjectQuery<CommentType>(SELECT_ALL, userAuth)
				.setString(reference.name())
				.run()
				.getObjects(CommentType.Parser.Inst.get());
	}
}
