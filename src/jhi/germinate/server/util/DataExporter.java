/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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

import jhi.flapjack.io.cmd.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link DataExporter} is a class taking lines and markers and extracting these subsets from a file on the filesystem.
 *
 * @author Sebastian Raubach
 */
public class DataExporter
{
	private static final String QUERY_EXPORT_ROWS_GENOTYPE_INTERNAL   = "SELECT null AS name";
	private static final String QUERY_EXPORT_ROWS_GENOTYPE_EXTERNAL   = "SELECT DISTINCT germinatebase.name FROM germinatebase LEFT JOIN groupmembers ON groupmembers.foreign_id = germinatebase.id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN grouptypes ON grouptypes.id = groups.grouptype_id WHERE groups.id IN (%s)";
	private static final String QUERY_EXPORT_COLS_GENOTYPE            = "SELECT mapdefinitions.*, mapfeaturetypes.description as mapfeature_description, markers.marker_name FROM mapdefinitions, mapfeaturetypes, markers, groupmembers WHERE mapdefinitions.mapfeaturetype_id = mapfeaturetypes.id AND mapdefinitions.marker_id = markers.id AND groupmembers.foreign_id = markers.id AND groupmembers.group_id IN (%s) AND mapdefinitions.map_id = ? ORDER BY chromosome, definition_start";
	private static final String QUERY_EXPORT_ROWS_ALLELEFREQ_INTERNAL = "SELECT DISTINCT (allelefrequencydata.sample_id) FROM allelefrequencydata LEFT JOIN groupmembers ON groupmembers.foreign_id = allelefrequencydata.germinatebase_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN datasets ON datasets.id = allelefrequencydata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE groups.id IN (%s) AND datasets.id = ?";
	// TODO: FIX
	private static final String QUERY_EXPORT_ROWS_ALLELEFREQ_EXTERNAL = "SELECT DISTINCT (allelefrequencydata.sample_id) FROM allelefrequencydata LEFT JOIN groupmembers ON groupmembers.foreign_id = allelefrequencydata.germinatebase_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN datasets ON datasets.id = allelefrequencydata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE groups.id IN (%s) AND datasets.id = ?";
	private static final String QUERY_EXPORT_COLS_ALLELEFREQ          = "SELECT mapdefinitions.*, mapfeaturetypes.description as mapfeature_description, markers.marker_name FROM mapdefinitions, mapfeaturetypes, markers, groupmembers WHERE mapdefinitions.mapfeaturetype_id = mapfeaturetypes.id AND mapdefinitions.marker_id = markers.id AND groupmembers.foreign_id = markers.id AND groupmembers.group_id IN (%s) AND mapdefinitions.map_id = ? ORDER BY chromosome, definition_start";


	public enum Type
	{
		GENOTYPE(QUERY_EXPORT_ROWS_GENOTYPE_INTERNAL, QUERY_EXPORT_ROWS_GENOTYPE_EXTERNAL, QUERY_EXPORT_COLS_GENOTYPE, Accession.NAME, ReferenceFolder.genotype),

		// TODO: change column name
		ALLELEFREQ(QUERY_EXPORT_ROWS_ALLELEFREQ_INTERNAL, QUERY_EXPORT_ROWS_ALLELEFREQ_EXTERNAL, QUERY_EXPORT_COLS_ALLELEFREQ, "sample_id", ReferenceFolder.allelefreq);

		private String          queryRowsInternal;
		private String          queryRowsExternal;
		private String          queryColumns;
		private String          columnName;
		private ReferenceFolder referenceFolder;

		Type(String queryRowsInternal, String queryRowsExternal, String queryColumns, String columnName, ReferenceFolder referenceFolder)
		{
			this.queryRowsInternal = queryRowsInternal;
			this.queryRowsExternal = queryRowsExternal;
			this.queryColumns = queryColumns;
			this.columnName = columnName;
			this.referenceFolder = referenceFolder;
		}

		public String getQueryRowsInternal()
		{
			return queryRowsInternal;
		}

		public String getQueryRowsExternal()
		{
			return queryRowsExternal;
		}

		public String getQueryColumns()
		{
			return queryColumns;
		}

		public String getColumnName()
		{
			return columnName;
		}

		public ReferenceFolder getReferenceFolder()
		{
			return referenceFolder;
		}
	}

	public static class DataExporterParameters
	{
		/** The percentage of allowed heterozygous values per column */
		public float qualityHeteroValue;
		/** The percentage of allowed missing values per column */
		public float qualityMissingValue;

		/** The delimiter */
		public String delimiter;

		/** The row names to extract */
		public List<String> rowNames;
		/** The column names to extract */
		public List<String> colNames;

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

	private static final boolean SORT_FILES = true;

	private Hdf5ToFJTabbedConverter converter;

	/**
	 * Creates a new instance of the DataExporter
	 *
	 * @param parameters The {@link DataExporterParameters}
	 */
	public DataExporter(DataExporter.DataExporterParameters parameters, String outputFile)
	{
		if (SORT_FILES && !CollectionUtils.isEmpty(parameters.rowNames))
			Collections.sort(parameters.rowNames);

		LinkedHashSet<String> lines = null;
		LinkedHashSet<String> markers = null;
		if (parameters.rowNames != null)
			lines = new LinkedHashSet<>(parameters.rowNames);
		if (parameters.colNames != null)
			markers = new LinkedHashSet<>(parameters.colNames);

		converter = new Hdf5ToFJTabbedConverter(parameters.inputFile, lines, markers, outputFile, parameters.qualityMissingValue != 100, parameters.qualityHeteroValue != 100);
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
