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
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;

/**
 * {@link MapServiceImpl} is the implementation of {@link MarkerService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/map"})
public class MapServiceImpl extends BaseRemoteServiceServlet implements MapService
{
	private static final long serialVersionUID = 3127051583642953437L;

	private static final String QUERY_MAP_BY_ID = "SELECT description FROM maps WHERE id = ?";

	private static final String QUERY_MAP_EXPORT                      = "SELECT definition_start, definition_end, chromosome, mapfeaturetypes.description, markers.marker_name FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id LEFT JOIN mapfeaturetypes ON mapfeaturetypes.id = mapdefinitions.mapfeaturetype_id WHERE map_id = ? %s ORDER BY chromosome, definition_start, marker_name";
	private static final String QUERY_MAP_EXPORT_INTERVAL_APPENDIX    = " AND definition_start >= (SELECT definition_end FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND markers.marker_name LIKE ?) AND definition_end <= (SELECT definition_start FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND markers.marker_name LIKE ?) AND chromosome = (SELECT chromosome FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND markers.marker_name LIKE ?) AND chromosome = (SELECT chromosome FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND markers.marker_name LIKE ?)";
	private static final String QUERY_MAP_EXPORT_RADIUS_APPENDIX      = " AND definition_start >= (SELECT (definition_start - ?) FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND marker_name LIKE ?) AND definition_end <= (SELECT (definition_end + ?) FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND marker_name LIKE ?) AND chromosome = (SELECT chromosome FROM mapdefinitions LEFT JOIN markers ON markers.id = mapdefinitions.marker_id WHERE map_id = ? AND markers.marker_name LIKE ?)";
	private static final String QUERY_MAP_EXPORT_CHROMOSOMES_APPENDIX = " AND chromosome IN (%s)";
	private static final String QUERY_CHROMOSOMES                     = "SELECT DISTINCT(chromosome) FROM mapdefinitions WHERE map_id = ? ORDER BY chromosome";

	private static final String[] COLUMNS_MAP_EXPORT = {"marker_name", "chromosome", "definition_start", "definition_end", "description", "count"};

	@Override
	public ServerResult<Map> getById(RequestProperties properties, Long mapId) throws InvalidSessionException, DatabaseException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		Session.checkSession(properties, this);
		try
		{
			return new MapManager().getById(userAuth, mapId);
		}
		catch (InsufficientPermissionsException e)
		{
			return new ServerResult<>(null, null);
		}
	}

	@Override
	public PaginatedServerResult<List<Map>> get(RequestProperties properties, ExperimentType experimentType, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		if (experimentType == null)
			return MapManager.getAll(userAuth, pagination);
		switch (experimentType)
		{
			case allelefreq:
				return MapManager.getAllHavingAlleleFreqData(userAuth, pagination);
			case genotype:
				return MapManager.getAll(userAuth, pagination);
			default:
				return new PaginatedServerResult<>(null, null, 0);
		}
	}

	/**
	 * Writes a map file in flapjack format
	 *
	 * @param table The {@link GerminateTableStreamer} containing the data
	 * @param bw    The buffered writer to use
	 * @throws java.io.IOException Thrown if writing the file fails
	 * @throws DatabaseException   Thrown if the interaction with the database fails
	 */
	private void writeFlapjackFile(DatabaseResult table, BufferedWriter bw) throws java.io.IOException, DatabaseException
	{
		bw.write("# fjFile = MAP");
		bw.newLine();

		while (table.next())
		{
			String markerName = table.getString(Marker.MARKER_NAME);
			String chromosome = table.getString(MapDefinition.CHROMOSOME);
			String defStart = table.getString(MapDefinition.DEFINITION_START);

			bw.write(markerName == null ? "" : markerName);
			bw.write("\t");
			bw.write(chromosome == null ? "" : chromosome);
			bw.write("\t");
			bw.write(defStart == null ? "" : defStart);
			bw.newLine();
		}
	}

	/**
	 * Writes a map file in mapchart format
	 *
	 * @param table   The {@link GerminateTableStreamer} containing the data
	 * @param bw      The buffered writer to use
	 * @param mapName The name of the map
	 * @throws java.io.IOException Thrown if writing the file fails
	 * @throws DatabaseException   Thrown if the interaction with the database fails
	 */
	private void writeMapChartFile(DatabaseResult table, BufferedWriter bw, String mapName) throws java.io.IOException, DatabaseException
	{
		String currentChromosome = "";

		if (table.next())
		{
			bw.write("; " + mapName);
			bw.newLine();
			bw.newLine();

			do
			{
				String markerName = table.getString(Marker.MARKER_NAME);
				String chromosome = table.getString(MapDefinition.CHROMOSOME);
				String defStart = table.getString(MapDefinition.DEFINITION_START);

				if (!Objects.equals(chromosome, currentChromosome))
				{
					currentChromosome = chromosome;
					bw.write("group " + currentChromosome);
					bw.newLine();
				}
				bw.write(markerName == null ? "" : markerName);
				bw.write("\t");
				bw.write(defStart == null ? "" : defStart);
				bw.newLine();
			}
			while (table.next());
		}
	}

	/**
	 * Writes a map file in strudel format
	 *
	 * @param table   The {@link GerminateTableStreamer} containing the data
	 * @param bw      The buffered writer to use
	 * @param mapName The name of the map
	 * @param mapId   The id of the map
	 * @param baseUrl The base URL for backlinks
	 * @throws java.io.IOException Thrown if writing the file fails
	 * @throws DatabaseException   Thrown if the interaction with the database fails
	 */
	private void writeStrudelFile(DatabaseResult table, BufferedWriter bw, String mapName, Long mapId, String baseUrl) throws java.io.IOException, DatabaseException
	{
		while (table.next())
		{
			String markerName = table.getString(Marker.MARKER_NAME);
			String chromosome = table.getString(MapDefinition.CHROMOSOME);
			String defStart = table.getString(MapDefinition.DEFINITION_START);
			String defEnd = table.getString(MapDefinition.DEFINITION_END);
			String mapFeat = table.getString(MapFeatureType.DESCRIPTION);

			bw.write("feature\t" + mapName + "\t");
			bw.write(chromosome == null ? "" : chromosome);
			bw.write("\t");
			bw.write(markerName == null ? "" : markerName);
			bw.write("\t");
			bw.write(mapFeat == null ? "" : mapFeat);
			bw.write("\t");
			bw.write(defStart == null ? "" : defStart);
			bw.write("\t");
			bw.write(defEnd == null ? "" : defEnd);
			bw.write("\t");
			bw.write("Imported from Germinate 3!");
			bw.newLine();
		}

		String url = baseUrl + "?" + Parameter.mapId + "=" + mapId + "#" + Page.MAP_DETAILS;

		bw.write("URL\t" + mapName + "\t" + url);
	}

	/**
	 * Returns the map description for a given map id
	 *
	 * @param mapId the map id
	 * @return The map description
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	private ServerResult<String> getMapDescription(Long mapId) throws DatabaseException
	{
		return new ValueQuery(QUERY_MAP_BY_ID)
				.setLong(mapId)
				.run(Map.DESCRIPTION)
				.getString("");
	}

	private String getMapExportPositionPlaceholder(int size)
	{
		if (size < 1)
			return "(1=1)";

		StringBuilder builder = new StringBuilder();

		builder.append("(");
		builder.append("(definition_start >= ? AND definition_end <= ?)");

		for (int i = 1; i < size; i++)
		{
			builder.append(" OR (definition_start >= ? AND definition_end <= ?)");
		}

		builder.append(") ");

		return builder.toString();
	}

	/**
	 * Creates an SQL String containing placeholders for all the regions and chromosome values
	 *
	 * @param regions The mapping between chromosome and region
	 * @return The placeholder String
	 */
	private String getMapExportChromosomePlaceholder(java.util.Map<String, List<Region>> regions)
	{
		if (regions.size() < 1)
			return "";

		StringBuilder builder = new StringBuilder();

		builder.append(" AND (");

		int counter = 0;
		List<String> keys = new ArrayList<>(regions.keySet());
		Collections.sort(keys);
		for (String chromosome : keys)
		{
			if (counter++ > 0)
				builder.append(" OR ");
			builder.append("( chromosome LIKE ? AND (");
			builder.append(getMapExportPositionPlaceholder(regions.get(chromosome).size()));
			builder.append("))");
		}

		builder.append(") ");

		return builder.toString();
	}

	@Override
	public ServerResult<String> getInFormat(RequestProperties properties, Long mapId, MapFormat format, MapExportOptions options) throws InvalidSessionException, DatabaseException, IOException
	{
		HttpServletRequest req = getThreadLocalRequest();

		GerminateTableQuery dataQuery;

		if (options != null)
		{
			/* Export chromosomes */
			if (options.getChromosomes() != null)
			{
				List<String> chromosomes = options.getChromosomes();
				/* Create the query for the actual data */
				String dataString = String.format(QUERY_MAP_EXPORT, QUERY_MAP_EXPORT_CHROMOSOMES_APPENDIX);
				dataString = String.format(dataString, Util.generateSqlPlaceholderString(chromosomes.size()));
				dataQuery = new GerminateTableQuery(properties, this, dataString, COLUMNS_MAP_EXPORT)
						.setLong(mapId);

				for (String chromosome : chromosomes)
				{
					dataQuery.setString(chromosome);
				}
			}
			/* Export regions */
			else if (options.getRegions() != null)
			{
				java.util.Map<String, List<Region>> regions = options.getRegions();
				/* Create the query for the actual data */
				String dataString = String.format(QUERY_MAP_EXPORT, getMapExportChromosomePlaceholder(regions));
				dataQuery = new GerminateTableQuery(properties, this, dataString, COLUMNS_MAP_EXPORT)
						.setLong(mapId);

				List<String> keys = new ArrayList<>(regions.keySet());
				Collections.sort(keys);
				for (String key : keys)
				{
					dataQuery.setString(key);
					for (Region region : regions.get(key))
					{
						dataQuery.setLong(region.start)
								 .setLong(region.end);
					}
				}
			}
			/* Export interval between two markers */
			else if (options.getInterval() != null)
			{
				/* Create the query for the actual data */
				String queryString = String.format(QUERY_MAP_EXPORT, QUERY_MAP_EXPORT_INTERVAL_APPENDIX);
				dataQuery = new GerminateTableQuery(properties, this, queryString, COLUMNS_MAP_EXPORT)
						.setLong(mapId);

				String f = options.getInterval().getFirst();
				String s = options.getInterval().getSecond();

				dataQuery
						.setLong(mapId)
						.setString(f)
						.setLong(mapId)
						.setString(s)
						.setLong(mapId)
						.setString(f)
						.setLong(mapId)
						.setString(s);
			}
			/* Export area around marker */
			else if (options.getRadius() != null)
			{
				/* Create the query for the actual data */
				String queryString = String.format(QUERY_MAP_EXPORT, QUERY_MAP_EXPORT_RADIUS_APPENDIX);
				dataQuery = new GerminateTableQuery(properties, this, queryString, COLUMNS_MAP_EXPORT)
						.setLong(mapId);

				String marker = options.getRadius().getFirst();
				Region region = options.getRadius().getSecond();

				dataQuery
						.setLong(region.start)
						.setLong(mapId)
						.setString(marker)
						.setLong(region.end)
						.setLong(mapId)
						.setString(marker)
						.setLong(mapId)
						.setString(marker);
			}
			/* If there are no specific requirements, just export the whole map */
			else
			{
				/* Create the query for the actual data */
				String queryString = String.format(QUERY_MAP_EXPORT, "");
				dataQuery = new GerminateTableQuery(properties, this, queryString, COLUMNS_MAP_EXPORT)
						.setLong(mapId);
			}
		}
		/* If there are no specific requirements, just export the whole map */
		else
		{
			/* Create the query for the actual data */
			String queryString = String.format(QUERY_MAP_EXPORT, "");
			dataQuery = new GerminateTableQuery(properties, this, queryString, COLUMNS_MAP_EXPORT)
					.setLong(mapId);
		}

		DatabaseResult streamer = dataQuery.getResult();

		ServerResult<String> mapName = getMapDescription(mapId);

		File filename = createTemporaryFile("map", format.getFileType().name());

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8")))
		{
			switch (format)
			{
				case flapjack:
					writeFlapjackFile(streamer, bw);
					break;
				case mapchart:
					writeMapChartFile(streamer, bw, mapName.getServerResult());
					break;
				case strudel:
					String baseURL = req.getRequestURL().toString().replace(req.getRequestURI().substring(1), req.getContextPath());
					writeStrudelFile(streamer, bw, mapName.getServerResult(), mapId, baseURL);
					break;
			}

			bw.close();
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e.getLocalizedMessage());
		}
		finally
		{
			streamer.close();
			dataQuery.close();
		}

		return new ServerResult<>(mapName.getDebugInfo(), filename.getName());
	}

	@Override
	public ServerResult<List<String>> getChromosomesForMap(RequestProperties properties, Long mapId) throws InvalidSessionException, DatabaseException
	{
		return new ValueQuery(properties, this, QUERY_CHROMOSOMES)
				.setLong(mapId)
				.run(MapDefinition.CHROMOSOME)
				.getStrings();
	}

	@Override
	public PaginatedServerResult<List<MapDefinition>> getDataForMarker(RequestProperties properties, Long markerId, Pagination pagination) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MapDefinitionManager.getForMarker(userAuth, markerId, pagination);
	}
}
