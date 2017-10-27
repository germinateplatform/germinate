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
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class LinkManager extends AbstractManager<Link>
{
	private static final String COMMON_TABLES = " linktypes LEFT JOIN links ON links.linktype_id = linktypes.id ";

	private static final String SELECT_FOR_TABLE_PLACEHOLDER = "SELECT links.* FROM " + COMMON_TABLES + " WHERE linktypes.target_table = ? AND visibility = 1 AND ISNULL(links.foreign_id)";
	private static final String SELECT_FOR_TABLE_STATIC      = "SELECT links.* FROM " + COMMON_TABLES + " WHERE linktypes.target_table = ? AND visibility = 1 AND links.foreign_id = ?";

	@Override
	protected String getTable()
	{
		return "links";
	}

	@Override
	protected DatabaseObjectParser<Link> getParser()
	{
		return Link.Parser.Inst.get();
	}

	public static ServerResult<List<Link>> getPlaceholderLinksForTable(UserAuth userAuth, Long referenceId, GerminateDatabaseTable referenceTable) throws DatabaseException
	{
		ServerResult<List<Link>> links = new DatabaseObjectQuery<Link>(SELECT_FOR_TABLE_PLACEHOLDER, userAuth)
				.setString(referenceTable.name())
				.run()
				.getObjects(Link.Parser.Inst.get());

		if (!CollectionUtils.isEmpty(links.getServerResult()))
		{
			for (Iterator<Link> it = links.getServerResult().iterator(); it.hasNext(); )
			{
				Link link = it.next();
				/* If we have a target column to use, replace the placeholder with the content of the column */
				if (!StringUtils.isEmpty(link.getType().getTargetColumn()))
				{
					ServerResult<String> cellContent = getTrustedCellValue(userAuth, referenceTable.name(), link.getType().getTargetColumn(), referenceId);

					links.getDebugInfo().addAll(cellContent.getDebugInfo());

					if (StringUtils.isEmpty(link.getHyperlink(), link.getType().getPlaceholder(), cellContent.getServerResult()))
						link.setHyperlink(null);
					else
						link.setHyperlink(link.getHyperlink().replace(link.getType().getPlaceholder(), cellContent.getServerResult()));
				}
				/* Else it's not a placeholder link, remove it */
				else
				{
					it.remove();
				}
			}
		}

		return links;
	}

	public static ServerResult<List<Link>> getStaticLinksForTable(UserAuth userAuth, Long referenceId, GerminateDatabaseTable referenceTable) throws DatabaseException
	{
		return new DatabaseObjectQuery<Link>(SELECT_FOR_TABLE_STATIC, userAuth)
				.setString(referenceTable.name())
				.setLong(referenceId)
				.run()
				.getObjects(Link.Parser.Inst.get());
	}
}
