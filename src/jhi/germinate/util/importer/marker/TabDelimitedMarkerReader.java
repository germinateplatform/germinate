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

import org.apache.poi.openxml4j.exceptions.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link TabDelimitedMarkerReader} implements {@link IStreamableReader} and reads and streams one {@link MapDefinition} object at a time.
 *
 * @author Sebastian Raubach
 */
public class TabDelimitedMarkerReader implements IStreamableReader<MapDefinition>
{
	private static final String CHROMOSOME_UNKNOWN = "UNK";

	private int            colCount   = 0;
	private int            currentCol = 0;
	private BufferedReader br;
	private String[]       chromosomes;
	private String[]       positions;
	private String[]       markerNames;

	@Override
	public boolean hasNext() throws IOException
	{
		return ++currentCol <= colCount;
	}

	@Override
	public MapDefinition next() throws IOException
	{
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));

		String line;

		while ((line = br.readLine()) != null && line.startsWith("#"))
		{
			// Do nothing here, just skip headers.
		}

		chromosomes = line.split("\t", -1);
		positions = br.readLine().split("\t", -1);
		markerNames = br.readLine().split("\t", -1);

		colCount = markerNames.length - 1;
	}

	@Override
	public void close() throws IOException
	{
		if (br != null)
			br.close();

		br = null;
	}

	private MapDefinition parse()
	{
		try
		{
			String chromosome = chromosomes[currentCol];
			String position = positions[currentCol];

			if (StringUtils.isEmpty(chromosome))
				chromosome = CHROMOSOME_UNKNOWN;
			if (StringUtils.isEmpty(position))
				position = currentCol + "";

			return new MapDefinition()
					.setChromosome(chromosome)
					.setDefinitionStart(position)
					.setDefinitionEnd(position)
					.setCreatedOn(new Date())
					.setUpdatedOn(new Date())
					.setMarker(new Marker()
							.setName(markerNames[currentCol])
							.setCreatedOn(new Date())
							.setUpdatedOn(new Date()));
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
