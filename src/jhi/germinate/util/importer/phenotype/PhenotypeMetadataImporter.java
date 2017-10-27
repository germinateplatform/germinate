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

package jhi.germinate.util.importer.phenotype;

import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link PhenotypeMetadataImporter} uses an {@link IDataReader} to read and parse the {@link Dataset} object and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class PhenotypeMetadataImporter extends DataImporter<Dataset>
{
	private static Set<Long> createdDatasetIds    = new HashSet<>();
	private static Set<Long> createdExperimentIds = new HashSet<>();

	private Dataset dataset;

	public static void main(String[] args)
	{
		new PhenotypeMetadataImporter()
				.run(args);
	}

	@Override
	protected IDataReader getFallbackReader()
	{
		return new ExcelPhenotypeMetadataReader();
	}

	@Override
	protected void deleteInsertedItems()
	{
		deleteItems(createdDatasetIds, "datasets");
		deleteItems(createdExperimentIds, "experiments");
	}

	@Override
	protected void write(Dataset entry) throws DatabaseException
	{
		createOrGetExperiment(entry);
		createOrGetDataset(entry);
	}

	/**
	 * Imports the {@link Experiment} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Dataset} object containing the {@link Experiment} to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetExperiment(Dataset entry) throws DatabaseException
	{
		String name = entry.getExperiment().getName();

		if (StringUtils.isEmpty(name))
			return;

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM experiments WHERE experiment_name = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			entry.setExperiment(Experiment.Parser.Inst.get().parse(rs, null, true));
		}
		else
		{
			Experiment.Writer.Inst.get().write(databaseConnection, entry.getExperiment());
			createdExperimentIds.add(entry.getExperiment().getId());
		}
	}

	/**
	 * Imports the {@link Dataset} object into the database if it doesn't already exist, otherwise returns the existing object from the database.
	 *
	 * @param entry The {@link Dataset} object to import.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	private void createOrGetDataset(Dataset entry) throws DatabaseException
	{
		String name = entry.getDescription();

		if (StringUtils.isEmpty(name) || entry.getExperiment() == null)
			throw new DatabaseException("Invalid experiment or dataset name.");

		DatabaseStatement stmt = databaseConnection.prepareStatement("SELECT id FROM datasets WHERE description = ?");
		stmt.setString(1, name);

		DatabaseResult rs = stmt.query();

		if (rs.next())
		{
			dataset = Dataset.Parser.Inst.get().parse(rs, null, true);
		}
		else
		{
			dataset = entry;
			Dataset.Writer.Inst.get().write(databaseConnection, dataset);
			createdDatasetIds.add(dataset.getId());
		}
	}

	public Dataset getDataset()
	{
		return dataset;
	}
}
