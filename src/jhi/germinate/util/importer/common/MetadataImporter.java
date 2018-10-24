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

import java.io.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link MetadataImporter} uses an {@link IDataReader} to read and parse the {@link Dataset} object and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class MetadataImporter extends DataImporter<Dataset>
{
	private static Set<Long> createdDatasetIds             = new HashSet<>();
	private static Set<Long> createdExperimentIds          = new HashSet<>();
	private static Set<Long> createdLocationIds            = new HashSet<>();
	private static Set<Long> createdDatasetCollaboratorIds = new HashSet<>();

	protected ExperimentType     type;
	protected Dataset            dataset;
	private   List<Collaborator> collaborators = new ArrayList<>();

	private CollaboratorImporter collaboratorImporter;

	public MetadataImporter(ExperimentType type)
	{
		this.type = type;
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		// Import the meta-data first. Get the created dataset
		collaboratorImporter = new CollaboratorImporter();
		collaboratorImporter.run(input, server, database, username, password, port, ExcelCollaboratorReader.class.getCanonicalName());
		collaborators = collaboratorImporter.getCollaborators();

		// Then run the rest of this importer
		super.run(input, server, database, username, password, port, readerName);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelMetadataReader(type);
	}

	@Override
	public void deleteInsertedItems()
	{
		if(collaboratorImporter != null)
			collaboratorImporter.deleteInsertedItems();
		deleteItems(createdExperimentIds, "experiments");
		deleteItems(createdDatasetIds, "datasets");
		deleteItems(createdDatasetCollaboratorIds, "datasetcollaborators");
		deleteItems(createdLocationIds, "locations");
	}

	@Override
	protected void prepareReader(IDataReader reader)
	{
		if (reader instanceof ExcelMetadataReader)
			((ExcelMetadataReader) reader).setType(type);
	}

	@Override
	protected void write(Dataset entry) throws DatabaseException
	{
		dataset = entry;
		createOrGetExperiment();
		createOrGetDataset();
		createOrGetDatasetCollaborators(collaborators);
	}

	private void createOrGetDatasetCollaborators(List<Collaborator> collaborators) throws DatabaseException
	{
		for (Collaborator collaborator : collaborators)
		{
			int i = 1;
			DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM datasetcollaborators WHERE dataset_id = ? AND collaborator_id = ?");
			stmt.setLong(i++, dataset.getId());
			stmt.setLong(i++, collaborator.getId());

			DatabaseResult rs = stmt.query();

			if (!rs.next())
			{
				i = 1;
				stmt = databaseConnection.prepareStatement("INSERT INTO datasetcollaborators (dataset_id, collaborator_id) VALUES (?, ?)");
				stmt.setLong(i++, dataset.getId());
				stmt.setLong(i++, collaborator.getId());
				List<Long> ids = stmt.execute();

				createdDatasetCollaboratorIds.addAll(ids);
			}
		}
	}

	private void getCountry(Location location) throws DatabaseException
	{
		if (location.getCountry() == null || StringUtils.isEmpty(location.getCountry().getCountryCode2()))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM countries WHERE country_code2 = ?");
		stmt.setString(1, location.getCountry().getCountryCode2());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			location.setCountry(Country.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			location.setCountry(new Country(-1L));
		}
	}

	private void createOrGetLocation(Dataset entry) throws DatabaseException
	{
		Location location = entry.getLocation();

		if (location == null)
			return;

		getCountry(location);

		String name = entry.getLocation().getName();

		if (StringUtils.isEmpty(name))
			return;

		int i = 1;
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM locations WHERE site_name <=> ? AND site_name_short <=> ? AND elevation <=> ? AND latitude <=> ? AND longitude <=> ? AND country_id <=> ?");
		stmt.setString(i++, location.getName());
		stmt.setString(i++, location.getShortName());
		stmt.setDouble(i++, location.getElevation());
		stmt.setDouble(i++, location.getLatitude());
		stmt.setDouble(i++, location.getLongitude());
		stmt.setLong(i++, location.getCountry() != null ? location.getCountry().getId() : -1L);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setLocation(Location.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Location.Writer.Inst.get().write(databaseConnection, location);
			createdLocationIds.add(location.getId());
		}
	}

	/**
	 * Imports the {@link Experiment} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Dataset} object containing the {@link Experiment} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	protected void createOrGetExperiment() throws DatabaseException
	{
		String name = dataset.getExperiment().getName();

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM experiments WHERE experiment_name = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			dataset.setExperiment(Experiment.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Experiment.Writer.Inst.get().write(databaseConnection, dataset.getExperiment());
			createdExperimentIds.add(dataset.getExperiment().getId());
		}
	}

	/**
	 * Imports the {@link Dataset} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Dataset} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	protected void createOrGetDataset() throws DatabaseException
	{
		String name = dataset.getName();

		if (StringUtils.isEmpty(name) || dataset.getExperiment() == null)
			throw new DatabaseException("Invalid experiment or dataset name.");

		createOrGetLocation(dataset);

		int i = 1;
		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM datasets WHERE name <=> ? AND location_id <=> ? AND experiment_id <=> ? AND dublin_core <=> CAST(? AS JSON) AND version <=> ?");
		stmt.setString(i++, name);
		stmt.setLong(i++, dataset.getLocation() == null ? null : dataset.getLocation().getId());
		stmt.setLong(i++, dataset.getExperiment().getId());
		stmt.setString(i++, dataset.getDublinCore());
		stmt.setString(i++, dataset.getVersion());

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			dataset = Dataset.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			Dataset.Writer.Inst.get().write(databaseConnection, dataset);
			createdDatasetIds.add(dataset.getId());
		}
	}

	public Dataset getDataset()
	{
		return dataset;
	}
}
