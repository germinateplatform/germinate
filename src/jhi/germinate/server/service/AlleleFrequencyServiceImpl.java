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

package jhi.germinate.server.service;

import java.io.*;
import java.util.*;

import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.FlapjackUtils.*;
import jhi.germinate.server.util.FlapjackUtils.FlapjackParams.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;

/**
 * {@link AlleleFrequencyServiceImpl} is the implementation of {@link AlleleFrequencyService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/allelefreq"})
public class AlleleFrequencyServiceImpl extends DataExportServlet implements AlleleFrequencyService
{
	private static final long serialVersionUID = 8117627823769568395L;

	private static final String QUERY_EXPORT_MAP = "SELECT markers.marker_name, mapdefinitions.chromosome, mapdefinitions.definition_start FROM mapdefinitions, mapfeaturetypes, markers WHERE mapdefinitions.mapfeaturetype_id = mapfeaturetypes.id AND mapdefinitions.marker_id = markers.id AND map_id = ? AND marker_name IN (%s) ORDER BY chromosome, definition_start";

	/**
	 * Retrieves the map used for genotype export
	 *
	 * @param sqlDebug The {@link DebugInfo} to use
	 * @param mapToUse The map id to use
	 * @param markers  The markers to use (we only want to include these markers)
	 * @return The map information (marker_name, chromosome, definition_start)
	 * @throws DatabaseException Thrown if the database interaction fails
	 */
	private GerminateTable getMap(UserAuth userAuth, DebugInfo sqlDebug, Long mapToUse, Set<String> markers) throws DatabaseException
	{
		String formatted = String.format(QUERY_EXPORT_MAP, Util.generateSqlPlaceholderString(markers.size()));
		ServerResult<GerminateTable> temp = new GerminateTableQuery(formatted, userAuth, new String[]{Marker.MARKER_NAME, MapDefinition.CHROMOSOME, MapDefinition.DEFINITION_START})
				.setLong(mapToUse)
				.setStrings(markers)
				.run();

		sqlDebug.addAll(temp.getDebugInfo());

		return temp.getServerResult();
	}

	@Override
	public Pair<String, HistogramImageData> getHistogramImageData(RequestProperties properties, HistogramParams params) throws InvalidSessionException,
			jhi.germinate.shared.exception.IOException, FlapjackException, MissingPropertyException
	{
		Session.checkSession(properties, this);

		File mapFile = new File((String) getFromSession(SESSION_PARAM_MAP));
		File subsetForFlapjack = new File((String) getFromSession(SESSION_PARAM_ALLELE_DATA_FILE));
		File histogramFile = new File((String) getFromSession(SESSION_PARAM_HISTOGRAM));

		/* Update the last modified date => it won't get deleted by the automatic file delete thread */
		FileUtils.setLastModifyDateNow(mapFile);
		FileUtils.setLastModifyDateNow(histogramFile);
		FileUtils.setLastModifyDateNow(subsetForFlapjack);

		File imageDataFile = createTemporaryFile("histogram_" + params.method, FileType.txt.name());
		String output = FlapjackUtils.createImage(imageDataFile.getAbsolutePath(), histogramFile.getAbsolutePath(), params);

		return new Pair<>(output, readImageDataFile(imageDataFile));
	}

	/**
	 * Reads the widths and colors for the flapjack histogram image from the given {@link File}
	 *
	 * @param file The {@link File}
	 * @return The arrays of widths and colors
	 * @throws jhi.germinate.shared.exception.IOException Thrown if any file interaction fails
	 */
	private HistogramImageData readImageDataFile(File file) throws IOException
	{
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			/* Read the two lines */
			String widths = br.readLine();
			String colors = br.readLine();

			if (!StringUtils.isEmpty(widths, colors))
			{
				/* Split them into parts */
				String[] widthArray = widths.split(",");
				String[] colorArray = colors.split(",");

				if (widthArray.length == colorArray.length && widthArray.length > 0)
				{
					double[] widthArrayDouble = new double[widthArray.length];

					for (int i = 0; i < widthArray.length; i++)
					{
						widthArrayDouble[i] = Double.parseDouble(widthArray[i]);
					}

					return new HistogramImageData(widthArrayDouble, colorArray);
				}
			}

			return null;
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}
	}

	@Override
	public Pair<String, FlapjackProjectCreationResult> createProject(RequestProperties properties, HistogramParams params) throws InvalidSessionException, FlapjackException
	{
		HttpServletRequest req = getThreadLocalRequest();

		Session.checkSession(properties, req, getThreadLocalResponse());

		File mapFile = new File((String) getFromSession(SESSION_PARAM_MAP));
		File subsetForFlapjack = new File((String) getFromSession(SESSION_PARAM_ALLELE_DATA_FILE));
		File histogramFile = new File((String) getFromSession(SESSION_PARAM_HISTOGRAM));
		File binnedFile = createTemporaryFile("allelefreq_binned_", FileType.txt.name());

		if (mapFile.exists())
		{
			/* Update the last modified date => it won't get deleted by the
			 * automatic file delete thread */
			FileUtils.setLastModifyDateNow(mapFile);
		}

		String debugOutput = FlapjackUtils.createBinnedFile(params, subsetForFlapjack.getAbsolutePath(), binnedFile.getAbsolutePath(), histogramFile.getAbsolutePath());

        /* Now we call Flapjack to create the project file for us */
		File flapjackResultFile = createTemporaryFile("genotype", "flapjack");

		FlapjackParams flapjackParams = new FlapjackParams()
				.add(Param.map, mapFile)
				.add(Param.genotypes, binnedFile)
				.add(Param.project, flapjackResultFile);
		debugOutput += "     " + FlapjackUtils.createProject(flapjackParams);

		FlapjackProjectCreationResult fjExportResult = new FlapjackProjectCreationResult()
				.setMapFile(mapFile.getName())
				.setRawDataFile(binnedFile.getName())
				.setProjectFile(flapjackResultFile.getName());

		return new Pair<>(debugOutput, fjExportResult);
	}

	@Override
	public ServerResult<FlapjackAllelefreqBinningResult> createHistogram(RequestProperties properties, List<Long> accessionGroups, List<Long> markerGroups, Long datasetId, boolean missingOn, Long mapId, int nrOfBins) throws InvalidSessionException, DatabaseException, InvalidArgumentException, IOException, FlapjackException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		FlapjackAllelefreqBinningResult result = new FlapjackAllelefreqBinningResult();

		DebugInfo sqlDebug = DebugInfo.create(userAuth);
		DataExporter.DataExporterParameters settings = getDataExporterParameters(sqlDebug, userAuth, DataExporter.Type.ALLELEFREQ, accessionGroups, markerGroups, datasetId, mapId, false, missingOn);
		CommonServiceImpl.ExportResult exportResult = getExportResult(DataExporter.Type.ALLELEFREQ, this);

        /* Kick off the extraction process, because we need the exported data before we can start with the histogram */
		try
		{
			AlleleFrequencyDataExporter exporter = new AlleleFrequencyDataExporter(settings);
			exporter.readInput();
			int size = exporter.exportResult(exportResult.subsetWithFlapjackLinks.getAbsolutePath(), "# fjFile = ALLELE_FREQUENCY\n" + exportResult.flapjackLinks);

			/* Now we call Flapjack to create histogram file for us */
			File histogramFile = createTemporaryFile("histogram", FileType.txt.name());
			String debugOutput = FlapjackUtils.makeHistogram(exportResult.subsetWithFlapjackLinks.getAbsolutePath(), histogramFile.getAbsolutePath(), nrOfBins);

			storeInSession(SESSION_PARAM_HISTOGRAM, histogramFile.getAbsolutePath());
			storeInSession(SESSION_PARAM_ALLELE_DATA_FILE, exportResult.subsetWithFlapjackLinks.getAbsolutePath());

            /* Get the map */
			GerminateTable mapData = getMap(userAuth, sqlDebug, mapId, exporter.getUsedColumnNames());

			if (mapData == null || mapData.size() < 1)
				throw new InvalidArgumentException("There is no data to export for the current selection.");

			try
			{
				Set<String> keptMarkers = exporter.getUsedColumnNames();
				Set<String> deletedMarkers = exporter.getDeletedMarkers();
				File filename = createTemporaryFile("map", "map");
				FlapjackUtils.writeTemporaryMapFile(filename, mapData, keptMarkers, null);

				/* Remember the map file location in the session */
				storeInSession(SESSION_PARAM_MAP, filename.getAbsolutePath());
				storeInSession(SESSION_PARAM_DELETED_MARKERS, deletedMarkers);
			}
			catch (java.io.IOException e1)
			{
				throw new IOException(e1);
			}

			if (size < 1)
				throw new InvalidArgumentException("There is no data to export for the current selection.");

			result.setDebugOutput(debugOutput);
			result.setHistogramFile(histogramFile.getName());
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
			throw new IOException(e);
		}

		return new ServerResult<>(sqlDebug, result);
	}
}
