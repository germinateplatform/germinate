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

import java.util.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.service.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class DataExportServlet extends BaseRemoteServiceServlet
{
	protected static final String SESSION_PARAM_MAP              = "SESSION_PARAM_MAP";
	protected static final String SESSION_PARAM_HISTOGRAM        = "SESSION_PARAM_HISTOGRAM";
	protected static final String SESSION_PARAM_DELETED_MARKERS  = "SESSION_PARAM_DELETED_MARKERS";
	protected static final String SESSION_PARAM_ALLELE_DATA_FILE = "SESSION_PARAM_ALLELE_DATA_FILE";

	private static final int QUALITY_HETERO  = 25;
	private static final int QUALITY_MISSING = 50;

	public CommonServiceImpl.ExportResult getExportResult(DataExporter.Type type, BaseRemoteServiceServlet servlet)
	{
		CommonServiceImpl.ExportResult exportResult = new CommonServiceImpl.ExportResult();

        /* Set up the temporary file and make sure the output folder exists */
		exportResult.subsetWithFlapjackLinks = createTemporaryFile(type.name() + "_links", FileType.txt.name());

		/* Check which Germinate pages are available. We do this to only add links to those pages that are actually available */
		Set<Page> availablePages = PropertyReader.getSet(ServerProperty.GERMINATE_AVAILABLE_PAGES, Page.class);

		/* Get the base URL of the server */
		String serverBase = PropertyReader.getServerBase(servlet.getRequest());

		/* Set up the flapjack links */
		exportResult.flapjackLinks = "";

		/* For genotypic files, add a link to the accession page */
		if (type == DataExporter.Type.GENOTYPE)
		{
			if (availablePages.contains(Page.PASSPORT))
				exportResult.flapjackLinks += "# fjDatabaseLineSearch = " + serverBase + "/?" + Parameter.accessionName + "=$LINE#" + Page.PASSPORT + "\n";

			if (availablePages.contains(Page.GROUP_PREVIEW))
			{
				exportResult.flapjackLinks += "# fjDatabaseGroupPreview = " + serverBase + "/?" + Parameter.groupPreviewFile + "=$GROUP#" + Page.GROUP_PREVIEW + "\n";
				exportResult.flapjackLinks += "# fjDatabaseGroupUpload = " + serverBase + "/germinate/" + ServletConstants.SERVLET_UPLOAD + "\n";
			}
		}
		/* For both types, add a link to the marker page */
		if (availablePages.contains(Page.MARKER_DETAILS))
			exportResult.flapjackLinks += "# fjDatabaseMarkerSearch = " + serverBase + "/?" + Parameter.markerName + "=$MARKER#" + Page.MARKER_DETAILS + "\n";

		return exportResult;
	}

	public DataExporter.DataExporterParameters getDataExporterParameters(DebugInfo sqlDebug, UserAuth userAuth, DataExporter.Type type, RequestProperties properties, List<Long> accessionGroups, List<Long> markerGroups, Long datasetId, Long mapId, boolean heterozygousFilter, boolean missingDataFilter) throws DatabaseException, InvalidArgumentException
	{
		/* Get the datasets the user is allowed to use */
		Boolean isAllowedToUse = DatasetManager.userHasAccessToDataset(userAuth, datasetId).getServerResult();

        /* Get the line names to extract */
		List<String> rowNames = isAllowedToUse ? getRowNames(userAuth, type, sqlDebug, accessionGroups, Collections.singletonList(datasetId)) : new ArrayList<>();
		/* Get the marker names to extract */
		List<String> colNames = getColumnNames(type, sqlDebug, markerGroups, mapId);

		/* If we specified accession and marker groups, but one of them is empty, then there is no data */
		if (!CollectionUtils.isEmpty(accessionGroups, markerGroups) && CollectionUtils.isEmpty(rowNames, colNames))
			throw new InvalidArgumentException();

		/* Set the filter values */
		int qualityHetero = heterozygousFilter ? QUALITY_HETERO : 100;
		int qualityMissing = missingDataFilter ? QUALITY_MISSING : 100;

		/* Get the data file associated with the datasets that contains the actual genotypic data */
		String dataFileToUse = null;
		try
		{
			dataFileToUse = new DatasetManager().getById(userAuth, datasetId).getServerResult().getSourceFile();
		}
		catch (InsufficientPermissionsException e)
		{
			e.printStackTrace();
		}

		DataExporter.DataExporterParameters parameters = new DataExporter.DataExporterParameters();
		parameters.inputFile = getFile(FileLocation.data, null, type.getReferenceFolder(), dataFileToUse);
		parameters.delimiter = "\t";
		parameters.rowNames = rowNames;
		parameters.colNames = colNames;
		parameters.qualityHeteroValue = qualityHetero;
		parameters.qualityMissingValue = qualityMissing;

		return parameters;
	}

	/**
	 * Retrieves the marker names
	 *
	 * @param type         The {@link DataExporter.Type} of data export
	 * @param sqlDebug     The {@link DebugInfo} to use
	 * @param markerGroups The marker groups
	 * @param mapToUse     The map id to use
	 * @return The marker names
	 * @throws DatabaseException Thrown if the database interaction fails
	 */
	private static List<String> getColumnNames(DataExporter.Type type, DebugInfo sqlDebug, List<Long> markerGroups, Long mapToUse) throws DatabaseException
	{
		if (CollectionUtils.isEmpty(markerGroups))
			return null;

		String formatted = String.format(type.getQueryColumns(), Util.generateSqlPlaceholderString(markerGroups.size()));
		ServerResult<List<String>> temp = new ValueQuery(formatted)
				.setLongs(markerGroups)
				.setLong(mapToUse)
				.run(Marker.MARKER_NAME)
				.getStrings();

		sqlDebug.addAll(temp.getDebugInfo());

		return temp.getServerResult();
	}

	/**
	 * Retrieves the line/accession names
	 *
	 * @param userAuth        The {@link UserAuth} of the current user
	 * @param type            The {@link DataExporter.Type} of data export
	 * @param sqlDebug        The {@link DebugInfo} to use
	 * @param accessionGroups The accession group ids
	 * @param datasetIds      The dataset ids
	 * @return The line names (name, afp_number)
	 * @throws DatabaseException Thrown if the database interaction fails
	 */
	private static List<String> getRowNames(UserAuth userAuth, DataExporter.Type type, DebugInfo sqlDebug, List<Long> accessionGroups, List<Long> datasetIds) throws DatabaseException
	{
		if (CollectionUtils.isEmpty(accessionGroups))
			return null;

		try
		{
			ServerResult<Boolean> hasLocalResourceFile = DatasetManager.hasSourceFile(userAuth, datasetIds);

			if (!hasLocalResourceFile.getServerResult())
			{
				/* Build up the query */
				String formatted = String.format(type.getQueryRowsInternal(), Util.generateSqlPlaceholderString(accessionGroups.size()), Util.generateSqlPlaceholderString(datasetIds.size()));

        		/* Run the query */
				ServerResult<List<String>> temp = new ValueQuery(formatted)
						.setLongs(accessionGroups)
						.setLongs(datasetIds)
						.run(type.getColumnName())
						.getStrings();
				sqlDebug.addAll(temp.getDebugInfo());

				return temp.getServerResult();
			}
			else
			{
				/* Build up the query */
				String formatted = String.format(type.getQueryRowsExternal(), Util.generateSqlPlaceholderString(accessionGroups.size()), Util.generateSqlPlaceholderString(datasetIds.size()));

        		/* Run the query */
				ValueQuery query = new ValueQuery(formatted)
						.setLongs(accessionGroups);

				// TODO: Fix this for allelefreq data
				if (type == DataExporter.Type.ALLELEFREQ)
					query.setLongs(datasetIds);

				ServerResult<List<String>> temp = query.run(type.getColumnName())
													   .getStrings();
				sqlDebug.addAll(temp.getDebugInfo());

				return temp.getServerResult();
			}
		}
		catch (InsufficientPermissionsException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
