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

/**
 * {@link DataExporter} is a class taking lines and markers and extracting these subsets from a file on the filesystem.
 *
 * @author Sebastian Raubach
 */
public class DataExporter
{
	public static class DataExporterParameters
	{
		/** The percentage of allowed heterozygous values per column */
		public float qualityHeteroValue;
		/** The percentage of allowed missing values per column */
		public float qualityMissingValue;

		/** The delimiter */
		public String delimiter;

		/** The row names to extract */
		public Set<String> rowNames;
		/** The column names to extract */
		public Set<String> colNames;

		/** The source of the data */
		public File inputFile;

		@Override
		public String toString()
		{
			return "DataExporterParameters{" +
					"qualityHeteroValue=" + qualityHeteroValue +
					", qualityMissingValue=" + qualityMissingValue +
					", delimiter='" + delimiter + '\'' +
					", rowNames=" + rowNames +
					", colNames=" + colNames +
					", inputFile=" + inputFile +
					'}';
		}
	}

	private Hdf5ToFJTabbedConverter converter;

	/**
	 * Creates a new instance of the DataExporter
	 *
	 * @param parameters The {@link DataExporterParameters}
	 */
	public DataExporter(DataExporter.DataExporterParameters parameters, String outputFile)
	{
		LinkedHashSet<String> lines = null;
		LinkedHashSet<String> markers = null;
		if (parameters.rowNames != null)
			lines = new LinkedHashSet<>(parameters.rowNames);
		if (parameters.colNames != null)
			markers = new LinkedHashSet<>(parameters.colNames);

		converter = new Hdf5ToFJTabbedConverter(parameters.inputFile, lines, markers, outputFile, false, parameters.qualityMissingValue != 100, parameters.qualityHeteroValue != 100);
	}

	/**
	 * Extracts the requested subset into a temporary file
	 */
	public void readInput()
	{
	}

	/**
	 * Applies the quality measures and writes the data to the output file
	 *
	 * @param prefix Any prefix to add as the first line(s) of the file
	 * @return The number of actual lines that were exported
	 */
	public int exportResult(String prefix)
	{
		converter.readInput();
		converter.extractData(prefix);

		return 1;
	}

	/**
	 * Returns the {@link Set} of deleted markers
	 *
	 * @return The {@link Set} of deleted markers
	 */
	public List<String> getKeptMarkers()
	{
		return new ArrayList<>(converter.getKeptMarkers());
	}
}
