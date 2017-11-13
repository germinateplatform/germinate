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

import java.util.*;
import java.util.stream.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.service.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

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
	private static List<String> getColumnNames(DebugInfo sqlDebug, List<Long> markerGroups, Long mapToUse, UserAuth userAuth) throws DatabaseException
	{
		// If no groups are selected
		if (CollectionUtils.isEmpty(markerGroups))
			return null;
			// If it contains the "All items group"
		else if (containsAllItemsGroup(markerGroups))
		{

			try
			{
				PartialSearchQuery q = new PartialSearchQuery();
				SearchCondition c = new SearchCondition();
				c.setColumnName(Map.ID);
				c.setComp(new Equal());
				c.addConditionValue(Long.toString(mapToUse));
				q.add(c);
				ServerResult<List<String>> result = MarkerManager.getNamesForFilter(userAuth, q);
				sqlDebug.addAll(result.getDebugInfo());
				return result.getServerResult();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		try
		{
			List<String> groupIds = markerGroups.stream()
												.map(g -> Long.toString(g))
												.collect(Collectors.toList());

			if (CollectionUtils.isEmpty(groupIds))
				return null;

			PartialSearchQuery q = new PartialSearchQuery();
			q.add(new SearchCondition(Map.ID, new Equal(), Long.toString(mapToUse), Long.class.getSimpleName()));
			q.addLogicalOperator(new And());
			q.add(new SearchCondition(Group.ID, new InSet(), groupIds, Long.class.getSimpleName()));

			ServerResult<List<String>> result = MarkerManager.getNamesForFilter(userAuth, q);
			sqlDebug.addAll(result.getDebugInfo());
			return result.getServerResult();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the line/accession names
	 *
	 * @param userAuth        The {@link UserAuth} of the current user
	 * @param sqlDebug        The {@link DebugInfo} to use
	 * @param accessionGroups The accession group ids
	 * @return The line names (name, afp_number)
	 * @throws DatabaseException Thrown if the database interaction fails
	 */
	private static List<String> getRowNames(UserAuth userAuth, DebugInfo sqlDebug, List<Long> accessionGroups) throws DatabaseException
	{
		// If no groups are selected or if it contains the "All items group"
		if (CollectionUtils.isEmpty(accessionGroups) || containsAllItemsGroup(accessionGroups))
			return null;

		ServerResult<List<String>> names = AccessionManager.getNamesForGroups(userAuth, accessionGroups);

		sqlDebug.addAll(names.getDebugInfo());

		return names.getServerResult();
	}

	public CommonServiceImpl.ExportResult getExportResult(ExperimentType type, BaseRemoteServiceServlet servlet)
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
		if (type == ExperimentType.genotype)
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

	public DataExporter.DataExporterParameters getDataExporterParameters(DebugInfo sqlDebug, UserAuth userAuth, ExperimentType type, List<Long> accessionGroups, List<Long> markerGroups, Long datasetId, Long mapId, boolean heterozygousFilter, boolean missingDataFilter) throws DatabaseException, InvalidArgumentException
	{
		/* Get the datasets the user is allowed to use */
		Boolean isAllowedToUse = DatasetManager.userHasAccessToDataset(userAuth, datasetId).getServerResult();

        /* Get the line names to extract */
		List<String> rowNames = isAllowedToUse ? getRowNames(userAuth, sqlDebug, accessionGroups) : new ArrayList<>();
		/* Get the marker names to extract */
		List<String> colNames = getColumnNames(sqlDebug, markerGroups, mapId, userAuth);

		/* If we specified accession and marker groups, but one of them is empty, then there is no data */
//		if (!CollectionUtils.isEmpty(accessionGroups, markerGroups) && CollectionUtils.isEmpty(rowNames, colNames))
//			throw new InvalidArgumentException();

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
		parameters.inputFile = getFile(FileLocation.data, null, ReferenceFolder.valueOf(type.name()), dataFileToUse);
		parameters.delimiter = "\t";
		parameters.rowNames = rowNames;
		parameters.colNames = colNames;
		parameters.qualityHeteroValue = qualityHetero;
		parameters.qualityMissingValue = qualityMissing;

		return parameters;
	}
}
