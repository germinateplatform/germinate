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

package jhi.germinate.util.importer.marker;

import java.io.*;
import java.util.*;

import jhi.germinate.server.database.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.exception.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link FilenameMapImporter} uses an {@link IDataReader} to read and parse attribute data and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class FilenameMapImporter extends ExcelMapImporter
{
	@Override
	public void run(File input, String server, String database, String username, String password, String port, String readerName)
	{
		String name = input.getName();
		if (name.contains("."))
			name.substring(0, name.lastIndexOf("."));

		Map map = new Map()
				.setDescription(name)
				.setVisibility(true)
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());

		try
		{
			databaseConnection = Database.connect(Database.DatabaseType.MYSQL, server + (StringUtils.isEmpty(port) ? "" : (":" + port)) + "/" + database + "?useSSL=false", username, password);
			createOrGetMap(map);
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}
}
