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

package jhi.germinate.util.importer.common;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link CollaboratorImporter} uses an {@link IDataReader} to read and parse {@link Phenotype}s and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class CollaboratorImporter extends DataImporter<Collaborator>
{
	private static Set<Long> createdCollaboratorIds = new HashSet<>();
	private static Set<Long> createdInstitutionIds  = new HashSet<>();

	private List<Collaborator> collaborators = new ArrayList<>();

	public static void main(String[] args)
	{
		try
		{
			new CollaboratorImporter()
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
		return new ExcelCollaboratorReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdCollaboratorIds, "collaborators");
		deleteItems(createdInstitutionIds, "institutions");
	}

	@Override
	protected void write(Collaborator entry) throws DatabaseException
	{
		Collaborator collaborator = createOrGetCollaborator(entry);
		if(collaborator != null)
			collaborators.add(collaborator);
	}

	/**
	 * Imports the {@link Phenotype} into the database if it doesn't already exist
	 *
	 * @param entry The {@link Phenotype} to import
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private Collaborator createOrGetCollaborator(Collaborator entry) throws DatabaseException
	{
		if (StringUtils.isEmpty(entry.getFirstName(), entry.getLastName()))
			return null;

		createOrGetInstitution(entry);

		int i = 1;
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM collaborators WHERE first_name <=> ? AND last_name <=> ? AND email <=> ? AND phone <=> ? AND institution_id <=> ?");
		stmt.setString(i++, entry.getFirstName());
		stmt.setString(i++, entry.getLastName());
		stmt.setString(i++, entry.getEmail());
		stmt.setString(i++, entry.getPhone());
		stmt.setLong(i++, entry.getInstitution() == null ? null : entry.getInstitution().getId());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			return Collaborator.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			Collaborator.Writer.Inst.get().write(databaseConnection, entry);
			createdCollaboratorIds.add(entry.getId());
		}

		return entry;
	}

	/**
	 * Imports the {@link Unit} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Phenotype} object containing the {@link Unit} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetInstitution(Collaborator entry) throws DatabaseException
	{
		if (entry.getInstitution() == null || StringUtils.isEmpty(entry.getInstitution().getName()))
			return;

		getCountry(entry);

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM institutions WHERE name = ? AND address = ? AND country_id = ?");
		stmt.setString(1, entry.getInstitution().getName());
		stmt.setString(2, entry.getInstitution().getAddress());
		stmt.setLong(3, entry.getInstitution().getCountry().getId());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setInstitution(Institution.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Institution.Writer.Inst.get().write(databaseConnection, entry.getInstitution());
			createdInstitutionIds.add(entry.getInstitution().getId());
		}
	}

	private void getCountry(Collaborator entry) throws DatabaseException
	{
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM countries WHERE country_code2 = ?");
		stmt.setString(1, entry.getInstitution().getCountry().getCountryCode2());

		DatabaseResult rs = stmt.query();

		if (rs.next())
			entry.getInstitution().setCountry(Country.Parser.Inst.get().parse(rs, null, true));
		else
			throw new DatabaseException("Invalid country code: " + entry.getInstitution().getCountry().getCountryCode2());
	}

	public List<Collaborator> getCollaborators()
	{
		return collaborators;
	}
}
