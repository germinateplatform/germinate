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

package jhi.germinate.util.importer.attribute;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link AttributeDataImporter} uses an {@link IDataReader} to read and parse {@link AttributeData} objects and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class AttributeDataImporter extends DataImporter<AttributeData>
{
	private HashMap<String, Accession> accessionCache = new HashMap<>();
	private HashMap<String, Attribute> attributeCache = new HashMap<>();

	private Set<Long> createdAttributeIds     = new HashSet<>();
	private Set<Long> createdAttributeDataIds = new HashSet<>();

	public static void main(String[] args)
	{
		new AttributeDataImporter()
				.run(args);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelAttributeDataReader();
	}

	@Override
	public void deleteInsertedItems()
	{
		deleteItems(createdAttributeIds, "attributes");
		deleteItems(createdAttributeDataIds, "attributedata");
	}

	@Override
	protected void write(AttributeData entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getValue()))
		{
			// Get the accession itself
			getAccession(entry);

			// Create or get the attribute
			createOrGetAttribute(entry);

			// Create the attribute data
			createAttributeData(entry);
		}
	}

	/**
	 * Import the {@link AttributeData} object into the database.
	 *
	 * @param entry The {@link AttributeData} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createAttributeData(AttributeData entry) throws DatabaseException
	{
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM attributedata WHERE attribute_id = ? AND germinatebase_id = ? AND value = ?");
		int i = 1;
		stmt.setLong(i++, entry.getAttribute().getId());
		stmt.setLong(i++, entry.getForeign().getId());
		stmt.setString(i++, entry.getValue());

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			AttributeData.Writer.Inst.get().write(databaseConnection, entry);
			createdAttributeDataIds.add(entry.getId());
		}
	}

	/**
	 * Get the {@link Accession} object for this {@link AttributeData}
	 *
	 * @param entry The {@link AttributeData} containing the {@link Accession} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void getAccession(AttributeData entry) throws DatabaseException
	{
		Accession accession = (Accession) entry.getForeign();

		if (StringUtils.isEmpty(accession.getGeneralIdentifier()))
			throw new DatabaseException("ACCENUMB cannot be empty!");

		Accession cached = accessionCache.get(accession.getGeneralIdentifier());

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT * FROM germinatebase WHERE general_identifier = ?");
			int i = 1;
			stmt.setString(i++, accession.getGeneralIdentifier());

			DatabaseResult rs = stmt.query();

			if (rs.next())
				cached = Accession.Parser.Inst.get().parse(rs, null, true);
			else
				throw new DatabaseException("Accession not found: " + accession);
		}

		entry.setForeign(cached);

		accessionCache.put(accession.getGeneralIdentifier(), cached);
	}

	/**
	 * Creates or gets the {@link Attribute} object for this {@link AttributeData}
	 *
	 * @param entry The {@link AttributeData} containing the {@link Attribute} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetAttribute(AttributeData entry) throws DatabaseException
	{
		String name = entry.getAttribute().getName();

		if (StringUtils.isEmpty(name))
			return;

		Attribute cached = attributeCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM attributes WHERE name = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				cached = Attribute.Parser.Inst.get().parse(rs, null, true);
			}
			else
			{
				cached = entry.getAttribute();
				Attribute.Writer.Inst.get().write(databaseConnection, entry.getAttribute());
				createdAttributeIds.add(entry.getAttribute().getId());
			}
		}

		entry.setAttribute(cached);

		attributeCache.put(name, cached);
	}
}
