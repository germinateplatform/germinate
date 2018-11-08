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

import jhi.germinate.shared.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelGenotypeDataReader} implements {@link IStreamableReader} and reads and streams one {@link String}[] object at a time (one row of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class TabDelimitedGenotypeDataReader implements IStreamableReader<String[]>
{
	private String[] parts;

	private BufferedReader br;

	private String currentLine = null;

	@Override
	public void close() throws IOException
	{
		if (br != null)
			br.close();

		br = null;
	}

	@Override
	public void init(File input) throws IOException
	{
		br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));

		String line;

		while ((line = br.readLine()) != null && line.startsWith("#"))
		{
			// Do nothing here, we just need to move down the file
		}

		br.readLine();
	}

	@Override
	public boolean hasNext() throws IOException
	{
		boolean hasNext = (currentLine = br.readLine()) != null;

		if (!hasNext)
		{
			close();
		}

		return hasNext;
	}

	@Override
	public String[] next()
	{
		parts = currentLine.split("\t", -1);

		StringUtils.trim(parts);

		return parts;
	}
}
