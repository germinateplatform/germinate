/*
 *  Copyright 2018 Information and Computational Sciences,
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
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link FilenameMetadataImporter} uses an {@link IDataReader} to read and parse the {@link Dataset} object and then writes it to a Germinate
 * database.
 *
 * @author Sebastian Raubach
 */
public class FilenameMetadataImporter extends GenotypeMetadataImporter
{
	public FilenameMetadataImporter(ExperimentType type)
	{
		super(type);
	}

	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		String name = input.getName();
		if (name.contains("."))
			name.substring(0, name.lastIndexOf("."));

		this.dataset = new Dataset()
				.setName(name)
				.setVersion("1")
				.setDatasetState(DatasetState.PUBLIC)
				.setIsExternal(false)
				.setExperiment(new Experiment().setName(name)
											   .setDescription(name)
											   .setType(type)
											   .setCreatedOn(new Date())
											   .setUpdatedOn(new Date()))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());

		try
		{
			databaseConnection = Database.connect(Database.DatabaseType.MYSQL, server + (StringUtils.isEmpty(port) ? "" : (":" + port)) + "/" + database + "?useSSL=false", username, password);
			createOrGetExperiment();
			createOrGetDataset();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}
}
