/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.phenotype;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.mcpd.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link PhenotypeImporter} uses an {@link IDataReader} to read and parse {@link Phenotype}s and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class PhenotypeImporter extends DataImporter<Phenotype>
{
	private static Set<Long> createdPhenotypeIds = new HashSet<>();
	private static Set<Long> createdUnitIds      = new HashSet<>();

	public static void main(String[] args)
	{
		new PhenotypeImporter()
				.run(args);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new TabDelimitedMcpdReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdPhenotypeIds, "phenotypes");
		deleteItems(createdUnitIds, "units");
	}

	@Override
	protected void write(Phenotype entry) throws DatabaseException
	{
		if (!StringUtils.isEmpty(entry.getName()))
		{
			// Write the phenotype itself
			createPhenotype(entry);
		}
	}

	/**
	 * Imports the {@link Phenotype} into the database if it doesn't already exist
	 *
	 * @param entry The {@link Phenotype} to import
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createPhenotype(Phenotype entry) throws DatabaseException
	{
		String name = entry.getName();

		if (StringUtils.isEmpty(name))
			return;

		createOrGetUnit(entry);

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM phenotypes WHERE name = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (!rs.next())
		{
			Phenotype.Writer.Inst.get().write(databaseConnection, entry);
			createdPhenotypeIds.add(entry.getId());
		}
	}

	/**
	 * Imports the {@link Unit} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Phenotype} object containing the {@link Unit} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetUnit(Phenotype entry) throws DatabaseException
	{
		String name = entry.getUnit().getName();

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM units WHERE unit_name = ? AND unit_abbreviation = ?");
		stmt.setString(1, entry.getUnit().getName());
		stmt.setString(2, entry.getUnit().getAbbreviation());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setUnit(Unit.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Unit.Writer.Inst.get().write(databaseConnection, entry.getUnit());
			createdUnitIds.add(entry.getUnit().getId());
		}
	}
}
