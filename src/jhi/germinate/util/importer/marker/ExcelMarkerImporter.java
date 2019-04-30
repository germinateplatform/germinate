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

package jhi.germinate.util.importer.marker;

import java.io.*;

import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMarkerImporter} uses an {@link IDataReader} to read and parse attribute data and then writes it to a Germinate database.
 *
 * @author Sebastian Raubach
 */
public class ExcelMarkerImporter extends TabDelimitedMarkerImporter
{
	public ExcelMarkerImporter(String mapName, String markerTypeName)
	{
		super(mapName, markerTypeName);
	}

	@Override
	protected void preImport(File input, String server, String database, String username, String password, String port) throws Exception
	{
		mapImporter = new ExcelMapImporter();
		mapImporter.run(input, server, database, username, password, port);
		map = mapImporter.getMap();

		markerTypeImporter = new ExcelMarkerTypeImporter();
		markerTypeImporter.run(input, server, database, username, password, port);
		markerType = markerTypeImporter.getMarkerType();
		mapFeatureType = markerTypeImporter.getMapFeatureType();
	}

	@Override
	protected IDataReader getReader()
	{
		return new ExcelMarkerReader();
	}
}
