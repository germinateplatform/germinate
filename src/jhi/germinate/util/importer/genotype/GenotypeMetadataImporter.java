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

package jhi.germinate.util.importer.genotype;

import java.io.*;
import java.io.IOException;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.common.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link GenotypeMetadataImporter} uses an {@link IDataReader} to read and parse the {@link Dataset} object and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class GenotypeMetadataImporter extends MetadataImporter
{
	private File                        hdf5File;

	public GenotypeMetadataImporter(ExperimentType type)
	{
		super(type);
	}

	@Override
	public void deleteInsertedItems()
	{
		super.deleteInsertedItems();

		if (hdf5File != null && hdf5File.exists())
			hdf5File.delete();
	}

	@Override
	protected void createOrGetDataset() throws DatabaseException
	{
		super.createOrGetDataset();

		try
		{
			hdf5File = File.createTempFile("germinate_genotype_", ".hdf5");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}

	public File getHdf5File()
	{
		return hdf5File;
	}
}
