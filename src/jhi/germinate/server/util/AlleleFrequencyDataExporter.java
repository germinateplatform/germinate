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

package jhi.germinate.server.util;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class AlleleFrequencyDataExporter
{
	/** Keep track of missing values per column */
	private int[] qualityMissing;

	/** Remember the markers that have been used */
	private TreeSet<String> usedMarkers = new TreeSet<>();

	/** Remember deleted markers */
	private TreeSet<String> deletedMarkers = new TreeSet<>();

	private int linesToExport = 0;

	private DataExporter.DataExporterParameters parameters;
	private boolean                             allLines   = false;
	private boolean                             allMarkers = false;

	/**
	 * Creates a new instance of the AlleleFrequencyDataExporter
	 *
	 * @param parameters The {@link DataExporter.DataExporterParameters}
	 */
	public AlleleFrequencyDataExporter(DataExporter.DataExporterParameters parameters)
	{
		this.parameters = parameters;

		allLines = CollectionUtils.isEmpty(parameters.rowNames);
		allMarkers = CollectionUtils.isEmpty(parameters.colNames);
	}

	/**
	 * Reads the input first to figure out which markers need to be removed (if any)
	 *
	 * @throws IOException Thrown if any file I/O goes wrong
	 */
	public void readInput() throws IOException
	{
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(parameters.inputFile), "UTF8")))
		{
			String headerRow = br.readLine();

			String[] markers = headerRow.split("\t", -1);

			qualityMissing = new int[markers.length - 1];

			for (String line; (line = br.readLine()) != null; )
			{
				String[] parts = line.split("\t", -1);

				if (allLines || parameters.rowNames.contains(parts[0]))
				{
					linesToExport++;
					for (int i = 1; i < parts.length; i++)
					{
						if (allMarkers || parameters.colNames.contains(parts[i]) && isMissing(parts[i]))
						{
							qualityMissing[i - 1]++;
						}
					}
				}
			}
		}
	}

	/**
	 * Applies the quality measures and writes the data to the output file
	 *
	 * @param outputFile The path to the file to generate
	 * @param prefix     Any prefix to add as the first line(s) of the file
	 * @return The number of actual lines that were exported
	 * @throws IOException Thrown if any file I/O goes wrong
	 */
	public int exportResult(String outputFile, String prefix) throws IOException
	{
		/* Get the actual number of allowed items */
		double localMissingValue = parameters.qualityMissingValue * (linesToExport / 100.0);

		String line;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(parameters.inputFile), "UTF8"));
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8")))
		{
			if (!StringUtils.isEmpty(prefix))
			{
				bw.write(prefix);
			}

			String headerRow = br.readLine();
			String[] markers = headerRow.split("\t", -1);

			/* Top left header */
			bw.write("Line/marker");

			/* Column headers */
			for (int i = 0; i < qualityMissing.length; i++)
			{
				if ((!allMarkers && !parameters.colNames.contains(markers[i + 1])) || qualityMissing[i] > localMissingValue)
				{
					deletedMarkers.add(markers[i + 1]);

					continue;
				}

				usedMarkers.add(markers[i + 1]);
				bw.write(parameters.delimiter + markers[i + 1]);
			}
			bw.newLine();

			String[] parts;
			while ((line = br.readLine()) != null)
			{
				if (StringUtils.isEmpty(line))
					continue;

				parts = line.split(parameters.delimiter, -1);

				if (allLines || parameters.rowNames.contains(parts[0]))
				{
					bw.write(parts[0]);

					for (int i = 0; i < qualityMissing.length; i++)
					{
						if ((!allMarkers && !parameters.colNames.contains(markers[i + 1])) || qualityMissing[i] > localMissingValue)
							continue;

						bw.write(parameters.delimiter + parts[i + 1]);
					}

					bw.newLine();
				}
			}
			return linesToExport;
		}
	}

	/**
	 * Checks if the given input is "empty", i.e. if it's equal to <code>""</code> or <code>"-"</code>
	 *
	 * @param input The input to check
	 * @return True if it's equal to <code>""</code> or <code>"-"</code>
	 */
	private boolean isMissing(String input)
	{
		return StringUtils.isEmpty(input) || StringUtils.areEqual("-", input.trim());
	}

	/**
	 * Returns the column names that were actually present in the input file out of those that have been requested
	 *
	 * @return The column names that were actually present in the input file out of those that have been requested
	 */
	public Set<String> getUsedColumnNames()
	{
		return usedMarkers;
	}

	/**
	 * Returns the markers that have been deleted due to the missing data filter
	 *
	 * @return The markers that have been deleted due to the missing data filter
	 */
	public TreeSet<String> getDeletedMarkers()
	{
		return deletedMarkers;
	}
}
