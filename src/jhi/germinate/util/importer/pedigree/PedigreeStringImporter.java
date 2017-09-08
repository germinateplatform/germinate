/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.pedigree;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link PedigreeStringImporter} uses an {@link IDataReader} to read and parse {@link PedigreeDefinition} objects and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class PedigreeStringImporter extends DataImporter<PedigreeDefinition>
{
	private Set<Long> createdPedigreeNotationIds    = new HashSet<>();
	private Set<Long> createdPedigreeDescriptionIds = new HashSet<>();
	private Set<Long> createdPedigreeDefinitionIds  = new HashSet<>();

	private HashMap<String, PedigreeNotation>    pedigreeNotationCache    = new HashMap<>();
	private HashMap<String, PedigreeDescription> pedigreeDescriptionCache = new HashMap<>();

	public static void main(String[] args)
	{
		new PedigreeStringImporter()
				.run(args);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelPedigreeStringReader();
	}

	/**
	 * Deletes all the database entries that have been created by this import tool.
	 */
	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdPedigreeNotationIds, "pedigreenotations");
		deleteItems(createdPedigreeDescriptionIds, "pedigreedescriptions");
		deleteItems(createdPedigreeDefinitionIds, "pedigreedefinitions");
	}

	@Override
	protected void write(PedigreeDefinition entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getAccession().getGeneralIdentifier()))
		{
			// Write or get the accession itself
			getAccession(entry);
			createOrGetNotation(entry);
			createOrGetDescription(entry);
			createPedigreeDefinition(entry);
		}
	}

	/**
	 * Imports the {@link PedigreeNotation} object into the database if it doesn't already exist, otherwise returns the existing object from the
	 * database.
	 *
	 * @param entry The {@link PedigreeDefinition} object containing the {@link PedigreeNotation} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetNotation(PedigreeDefinition entry) throws DatabaseException
	{
		String name = entry.getNotation().getName();

		if (StringUtils.isEmpty(name))
			return;

		PedigreeNotation cached = pedigreeNotationCache.get(name);

		if (cached == null)
		{
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM pedigreenotations WHERE name = ?");
			stmt.setString(1, name);

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				cached = PedigreeNotation.Parser.Inst.get().parse(rs, null, true);
			}
			else
			{
				cached = entry.getNotation();
				PedigreeNotation.Writer.Inst.get().write(databaseConnection, entry.getNotation());
				createdPedigreeNotationIds.add(entry.getNotation().getId());
			}
		}

		entry.setNotation(cached);
		pedigreeNotationCache.put(name, cached);
	}

	/**
	 * Imports the {@link PedigreeDescription} object into the database if it doesn't already exist, otherwise returns the existing object from the
	 * database.
	 *
	 * @param entry The {@link PedigreeDefinition} object containing the {@link PedigreeDescription} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetDescription(PedigreeDefinition entry) throws DatabaseException
	{
		String name = entry.getDescription().getName();

		if (StringUtils.isEmpty(name))
			return;

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
				cached = entry.getDescription();
				PedigreeDescription.Writer.Inst.get().write(databaseConnection, cached);
				createdPedigreeDescriptionIds.add(cached.getId());
			}
		}
		entry.setDescription(cached);

		pedigreeDescriptionCache.put(name, cached);
	}

	/**
	 * Returns the {@link Accession} with the given {@link Accession#GENERAL_IDENTIFIER}.
	 *
	 * @param name The {@link Accession#GENERAL_IDENTIFIER} of the {@link Accession} to get.
	 * @return The {@link Accession} with the given {@link Accession#GENERAL_IDENTIFIER}.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void getAccession(PedigreeDefinition entry) throws DatabaseException
	{
		String name = entry.getAccession().getGeneralIdentifier();

		if (StringUtils.isEmpty(name))
			throw new DatabaseException("Invalid accession ACCENUMB");

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM germinatebase WHERE general_identifier = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
			entry.setAccession(Accession.Parser.Inst.get().parse(rs, null, true));
		else
			throw new DatabaseException("Invalid accession ACCENUMB");
	}

	/**
	 * Imports the {@link PedigreeDefinition} into the database if it doesn't already exist
	 *
	 * @param entry The {@link PedigreeDefinition} to import
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createPedigreeDefinition(PedigreeDefinition entry) throws DatabaseException
	{
		String name = entry.getDefinition();

		if (StringUtils.isEmpty(name))
			throw new DatabaseException("Invalid pedigree string");

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM pedigreedefinitions WHERE definition = ? AND germinatebase_id = ? AND pedigreenotation_id = ? AND pedigreedescription_id <=> ?");
		int i = 1;
		stmt.setString(i++, name);
		stmt.setLong(i++, entry.getAccession().getId());
		stmt.setLong(i++, entry.getNotation().getId());
		stmt.setLong(i++, entry.getDescription() == null ? null : entry.getDescription().getId());

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			PedigreeDefinition.Writer.Inst.get().write(databaseConnection, entry);
			createdPedigreeDefinitionIds.add(entry.getId());
		}
	}
}
