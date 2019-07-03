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

import jhi.germinate.server.manager.*;
import jhi.germinate.server.service.*;
import jhi.germinate.server.watcher.*;
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
	 * @param sqlDebug     The {@link DebugInfo} to use
	 * @param markerGroups The marker groups
	 * @param mapToUse     The map id to use
	 * @return The marker names
	 * @throws DatabaseException Thrown if the database interaction fails
	 */
	private static Set<String> getColumnNames(DebugInfo sqlDebug, List<Long> markerGroups, Set<String> markedMarkerIds, Long mapToUse, UserAuth userAuth) throws DatabaseException
	{
		if ((CollectionUtils.isEmpty(markerGroups) && CollectionUtils.isEmpty(markedMarkerIds)))
		{
			return null;
		}
		// If it contains the "All items group"
		else if (containsAllItemsGroup(markerGroups))
		{
			try
			{
				ServerResult<List<String>> result = MarkerManager.getNamesForMap(userAuth, mapToUse);
				sqlDebug.addAll(result.getDebugInfo());
				return new LinkedHashSet<>(result.getServerResult());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		try
		{
			Set<String> result = null;
			List<String> groupIds = markerGroups.stream()
												.map(g -> Long.toString(g))
												.collect(Collectors.toList());

			if (!CollectionUtils.isEmpty(groupIds))
			{
				PartialSearchQuery q = new PartialSearchQuery();
				q.add(new SearchCondition(Map.ID, new Equal(), Long.toString(mapToUse), Long.class));
				q.addLogicalOperator(new And());
				q.add(new SearchCondition(Group.ID, new InSet(), groupIds, Long.class));

				ServerResult<List<String>> groupData = MarkerManager.getNamesForFilter(userAuth, q);
				if (groupData != null)
				{
					sqlDebug.addAll(groupData.getDebugInfo());
					result = new LinkedHashSet<>();
					if (groupData.hasData())
						result = new LinkedHashSet<>(groupData.getServerResult());
				}
			}
			if (!CollectionUtils.isEmpty(markedMarkerIds))
			{
				PartialSearchQuery q = new PartialSearchQuery();
				q.add(new SearchCondition(Map.ID, new Equal(), Long.toString(mapToUse), Long.class));
				q.addLogicalOperator(new And());
				q.add(new SearchCondition(Group.ID, new InSet(), groupIds, Long.class));

				ServerResult<List<String>> markedData = MarkerManager.getNamesForIds(userAuth, markedMarkerIds);

				if (markedData != null)
				{
					if (result == null)
						result = new LinkedHashSet<>();
					sqlDebug.addAll(markedData.getDebugInfo());

					if (markedData.hasData())
						result.addAll(markedData.getServerResult());
				}
			}

			return result;

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
	private static Set<String> getRowNames(UserAuth userAuth, DebugInfo sqlDebug, List<Long> accessionGroups, Set<String> markedAccessionIds) throws DatabaseException
	{
		// If no groups are selected or if it contains the "All items group"
		if (containsAllItemsGroup(accessionGroups) || (CollectionUtils.isEmpty(accessionGroups) && CollectionUtils.isEmpty(markedAccessionIds)))
			return null;

		// Return null by default
		Set<String> result = null;
		ServerResult<List<String>> groupData = AccessionManager.getNamesForGroups(userAuth, accessionGroups);
		ServerResult<List<String>> markedData = AccessionManager.getNamesForIds(userAuth, markedAccessionIds);

		if (groupData != null)
		{
			sqlDebug.addAll(groupData.getDebugInfo());
			if (groupData.hasData())
				result = new LinkedHashSet<>(groupData.getServerResult());
		}
		if (markedData != null)
		{
			if (result == null)
				result = new LinkedHashSet<>();
			sqlDebug.addAll(markedData.getDebugInfo());
			if (markedData.hasData())
				result.addAll(markedData.getServerResult());
		}

		return result;
	}

	public CommonServiceImpl.ExportResult getExportResult(Long datasetId, ExperimentType type, BaseRemoteServiceServlet servlet)
	{
		CommonServiceImpl.ExportResult exportResult = new CommonServiceImpl.ExportResult();

		/* Set up the temporary file and make sure the output folder exists */
		exportResult.subsetWithFlapjackLinks = createTemporaryFile(type.name() + "_links", datasetId, FileType.txt.name());

		/* Check which Germinate pages are available. We do this to only add links to those pages that are actually available */
		Set<Page> availablePages = PropertyWatcher.getSet(ServerProperty.GERMINATE_AVAILABLE_PAGES, Page.class);

		/* Get the base URL of the server */
		String serverBase = PropertyWatcher.getServerBase(servlet.getRequest());

		/* Set up the flapjack links */
		exportResult.flapjackLinks = "";

		/* Add a link to the accession page */
		if (availablePages.contains(Page.PASSPORT))
			exportResult.flapjackLinks += "# fjDatabaseLineSearch = " + serverBase + "/?" + Parameter.accessionName + "=$LINE#" + Page.PASSPORT + "\n";

		if (availablePages.contains(Page.GROUP_PREVIEW))
		{
			exportResult.flapjackLinks += "# fjDatabaseGroupPreview = " + serverBase + "/?" + Parameter.groupPreviewFile + "=$GROUP#" + Page.GROUP_PREVIEW + "\n";
			exportResult.flapjackLinks += "# fjDatabaseGroupUpload = " + serverBase + "/germinate/" + ServletConstants.SERVLET_UPLOAD + "\n";
		}
		/* For both types, add a link to the marker page */
		if (availablePages.contains(Page.MARKER_DETAILS))
			exportResult.flapjackLinks += "# fjDatabaseMarkerSearch = " + serverBase + "/?" + Parameter.markerName + "=$MARKER#" + Page.MARKER_DETAILS + "\n";

		return exportResult;
	}

	public DataExporter.DataExporterParameters getDataExporterParameters(DebugInfo sqlDebug, UserAuth userAuth, ExperimentType type, List<Long> accessionGroups, Set<String> markedAccessionIds, List<Long> markerGroups, Set<String> markedMarkerIds, Long datasetId, Long mapId, boolean heterozygousFilter, boolean missingDataFilter) throws DatabaseException, InvalidArgumentException
	{
		/* Get the datasets the user is allowed to use */
		Boolean isAllowedToUse = DatasetManager.userHasAccessToDataset(userAuth, datasetId).getServerResult();

		/* Get the line names to extract */
		Set<String> rowNames = isAllowedToUse ? getRowNames(userAuth, sqlDebug, accessionGroups, markedAccessionIds) : new LinkedHashSet<>();
		/* Get the marker names to extract */
		Set<String> colNames = getColumnNames(sqlDebug, markerGroups, markedMarkerIds, mapId, userAuth);

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
