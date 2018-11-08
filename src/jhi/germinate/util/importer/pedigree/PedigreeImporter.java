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

package jhi.germinate.util.importer.pedigree;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link PedigreeImporter} uses an {@link IDataReader} to read and parse {@link Pedigree} objects and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class PedigreeImporter extends DataImporter<List<Pedigree>>
{
	private Set<Long> createdPedigreeNotationIds    = new HashSet<>();
	private Set<Long> createdPedigreeDescriptionIds = new HashSet<>();
	private Set<Long> createdPedigreeIds            = new HashSet<>();

	private HashMap<String, Accession>           accessionCache           = new HashMap<>();
	private HashMap<String, PedigreeDescription> pedigreeDescriptionCache = new HashMap<>();

	public static void main(String[] args)
	{
		new PedigreeImporter()
				.run(args);
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelPedigreeReader();
	}

	/**
	 * Deletes all the database entries that have been created by this import tool.
	 */
	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdPedigreeNotationIds, "pedigreenotations");
		deleteItems(createdPedigreeDescriptionIds, "pedigreedescriptions");
		deleteItems(createdPedigreeIds, "pedigrees");
	}

	@Override
	protected void write(List<Pedigree> entry) throws DatabaseException
	{
		if (!CollectionUtils.isEmpty(entry))
		{
			Pedigree pedigree;

			// If parent one exists
			if (entry.size() > 0)
			{
				pedigree = entry.get(0);
				pedigree.setAccession(getAccession(pedigree.getAccession().getGeneralIdentifier()))
						.setParent(getAccession(pedigree.getParent().getGeneralIdentifier()))
						.setPedigreeDescription(createOrGetDescription(pedigree));
				createPedigree(pedigree);
			}
			// If parent two exists
			if (entry.size() > 1)
			{
				pedigree = entry.get(1);
				pedigree.setAccession(getAccession(pedigree.getAccession().getGeneralIdentifier()))
						.setParent(getAccession(pedigree.getParent().getGeneralIdentifier()))
						.setPedigreeDescription(createOrGetDescription(pedigree));
				createPedigree(pedigree);
			}
		}
	}

	/**
	 * Returns the {@link Accession} with the given {@link Accession#GENERAL_IDENTIFIER}.
	 *
	 * @param name The {@link Accession#GENERAL_IDENTIFIER} of the {@link Accession} to get.
	 * @return The {@link Accession} with the given {@link Accession#GENERAL_IDENTIFIER}.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private Accession getAccession(String name) throws DatabaseException
	{
		if (StringUtils.isEmpty(name))
			throw new DatabaseException("Invalid accession ACCENUMB");

		Accession cached = accessionCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM germinatebase WHERE general_identifier = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
				cached = Accession.Parser.Inst.get().parse(rs, null, true);
			else
				throw new DatabaseException("Invalid accession ACCENUMB");
		}

		return cached;
	}

	/**
	 * Imports the {@link PedigreeDescription} object into the database if it doesn't already exist, otherwise returns the existing object from the
	 * database.
	 *
	 * @param entry The {@link Pedigree} object containing the {@link PedigreeDescription} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private PedigreeDescription createOrGetDescription(Pedigree entry) throws DatabaseException
	{
		String name = entry.getPedigreeDescription().getName();

		if (StringUtils.isEmpty(name))
			return null;

		PedigreeDescription cached = pedigreeDescriptionCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM pedigreedescriptions WHERE name = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				cached = PedigreeDescription.Parser.Inst.get().parse(rs, null, true);
			}
			else
			{
				cached = entry.getPedigreeDescription();

				PedigreeDescription.Writer.Inst.get().write(databaseConnection, cached);
				createdPedigreeDescriptionIds.add(cached.getId());
			}
		}

		pedigreeDescriptionCache.put(name, cached);

		return cached;
	}

	/**
	 * Imports the {@link Pedigree} into the database if it doesn't already exist
	 *
	 * @param entry The {@link Pedigree} to import
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createPedigree(Pedigree entry) throws DatabaseException
	{
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM pedigrees WHERE germinatebase_id = ? AND parent_id = ? AND pedigreedescription_id <=> ? AND relationship_description <=> ?");
		int i = 1;
		stmt.setLong(i++, entry.getAccession().getId());
		stmt.setLong(i++, entry.getParent().getId());
		stmt.setLong(i++, entry.getPedigreeDescription() == null ? null : entry.getPedigreeDescription().getId());
		stmt.setString(i++, entry.getRelationShipDescription());

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			Pedigree.Writer.Inst.get().write(databaseConnection, entry);
			createdPedigreeIds.add(entry.getId());
		}
	}
}
