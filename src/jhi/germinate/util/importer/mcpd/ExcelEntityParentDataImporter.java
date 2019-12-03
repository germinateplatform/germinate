/*
 *  Copyright 2019 Information and Computational Sciences,
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

package jhi.germinate.util.importer.mcpd;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelEntityParentDataImporter} uses an {@link IDataReader} to read and parse {@link Accession}s and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class ExcelEntityParentDataImporter extends DataImporter<Accession>
{
	public static void main(String[] args)
	{
		try
		{
			new ExcelEntityParentDataImporter()
				.run(args);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelEntityParentDataReader();
	}

	/**
	 * Deletes all the database entries that have been created by this import tool.
	 */
	@Override
	protected void deleteInsertedItems()
	{
	}

	@Override
	protected void write(Accession entry)
		throws DatabaseException
	{
		// Write or get the accession itself
		updateAccession(entry);
	}

	private void updateAccession(Accession entry)
		throws DatabaseException
	{
		String childAccenumb = entry.getGeneralIdentifier();
		String parentAccenumb = entry.getExtra(ExcelEntityParentDataReader.ENTITY_PARENT);
		EntityType entityType = entry.getEntityType();

		if (StringUtils.isEmpty(childAccenumb))
			throw new DatabaseException("ACCENUMB cannot be empty!");
		if (StringUtils.isEmpty(parentAccenumb))
			return;

		DatabaseStatement stmtParent = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier <=> ?");
		stmtParent.setString(1, parentAccenumb);
		DatabaseStatement stmtChild = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier <=> ?");
		stmtChild.setString(1, childAccenumb);

		DatabaseResult rsParent = stmtParent.query();
		DatabaseResult rsChild = stmtChild.query();

		Accession parent;
		Accession child;

		if (rsParent.next() && rsChild.next())
		{
			parent = Accession.ImportParser.Inst.get().parse(rsParent, null, true);
			child = Accession.ImportParser.Inst.get().parse(rsChild, null, true);

			if (parent != null && child != null)
			{
				child.setEntityParentId(parent.getId());

				new ValueQuery(databaseConnection, "UPDATE germinatebase SET entitytype_id = ?, entityparent_id = ? WHERE id = ?")
					.setLong(entityType.getId())
					.setLong(parent.getId())
					.setLong(child.getId())
					.execute(false);
			}
			else
			{
				throw new DatabaseException("Child or parent not found: " + parentAccenumb + " " + childAccenumb);
			}
		}
		else
		{
			throw new DatabaseException("Child or parent not found: " + parentAccenumb + " " + childAccenumb);
		}
	}
}
