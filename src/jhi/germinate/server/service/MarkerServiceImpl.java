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

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link MarkerServiceImpl} is the implementation of {@link MarkerService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/marker"})
public class MarkerServiceImpl extends BaseRemoteServiceServlet implements MarkerService
{
	private static final long serialVersionUID = 3127051583642953437L;

	private static final String QUERY_MARKERS_DATA_IDS_DOWNLOAD = "SELECT markers.*, markertypes.description AS markertypes_description, mapdefinitions.definition_start AS mapdefinitions_definition_start, mapdefinitions.definition_end AS mapdefinitions_definition_end, mapdefinitions.chromosome AS mapdefinitions_chromosome, mapdefinitions.arm_impute AS mapdefinitions_arm_impute, maps.description AS maps_description FROM markers LEFT JOIN markertypes ON markertypes.id = markers.markertype_id LEFT JOIN mapdefinitions ON markers.id = mapdefinitions.marker_id LEFT JOIN maps ON maps.id = mapdefinitions.map_id LEFT JOIN mapfeaturetypes ON mapfeaturetypes.id = mapdefinitions.mapfeaturetype_id WHERE markers.id IN (%s)";

	private static final String QUERY_MARKER_DATA_WITH_NAMES = "SELECT mapdefinitions.chromosome, mapdefinitions.definition_start, mapfeaturetypes.description, markers.id, markers.marker_name FROM mapdefinitions, mapfeaturetypes, markers, maps WHERE mapdefinitions.mapfeaturetype_id = mapfeaturetypes.id AND mapdefinitions.marker_id = markers.id AND maps.id = mapdefinitions.map_id AND (maps.user_id = ? OR maps.visibility = 1) AND markers.marker_name IN (%s)";

	private static final String QUERY_MARKER_NAME_SUGGESTIONS = "SELECT id, marker_name FROM markers WHERE marker_name LIKE ? ORDER BY marker_name LIMIT ?";

	@Override
	public ServerResult<Marker> getById(RequestProperties properties, Long markerId) throws InvalidSessionException, DatabaseException
	{
		if (markerId == null)
			return new ServerResult<>(null, null);
		else
		{
			Session.checkSession(properties, this);
			UserAuth userAuth = UserAuth.getFromSession(this, properties);
			try
			{
				return new MarkerManager().getById(userAuth, markerId);
			}
			catch (InsufficientPermissionsException e)
			{
				return new ServerResult<>(null, null);
			}
		}
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = MapDefinitionManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-markers", FileType.txt.name());

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, result);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		return new ServerResult<>(streamer.getDebugInfo(), result.getName());
	}

	@Override
	public PaginatedServerResult<List<MapDefinition>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MapDefinitionManager.getForFilter(userAuth, pagination, filter);
	}

	@Override
	public List<ItemSuggestion> getSuggestions(RequestProperties properties, String query, int limit) throws DatabaseException, InvalidSessionException
	{
		GerminateTableStreamer streamer = new GerminateTableQuery(properties, this, QUERY_MARKER_NAME_SUGGESTIONS, new String[]{Marker.ID, Marker.MARKER_NAME})
				.setString(query + "%")
				.setInt(limit)
				.getStreamer();

		List<ItemSuggestion> suggestions = new ArrayList<>();

		GerminateRow row;
		while ((row = streamer.next()) != null)
		{
			suggestions.add(new ItemSuggestion(Long.parseLong(row.get(Marker.ID)), row.get(Marker.MARKER_NAME)));
		}

		streamer.close();

		return suggestions;
	}

	@Override
	public ServerResult<Marker> getByName(RequestProperties properties, String name) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return MarkerManager.getByName(userAuth, name);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, Set<String> markerNames) throws InvalidSessionException, DatabaseException, IOException
	{
		String query = String.format(QUERY_MARKER_DATA_WITH_NAMES, Util.generateSqlPlaceholderString(markerNames.size()));

		GerminateTableStreamer data = new GerminateTableQuery(properties, this, query, null)
				.setString(properties.getUserId())
				.setStrings(markerNames)
				.getStreamer();

		File file = createTemporaryFile("markers", FileType.txt.name());

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, data, file);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		return new ServerResult<>(data.getDebugInfo(), file.getName());
	}

	@Override
	public ServerResult<List<Marker>> getByIds(RequestProperties properties, Pagination pagination, List<String> ids) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MarkerManager.getByIds(userAuth, ids, pagination);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, List<String> ids) throws InvalidSessionException, DatabaseException, IOException
	{
		String formatted = String.format(QUERY_MARKERS_DATA_IDS_DOWNLOAD, Util.generateSqlPlaceholderString(ids.size()));
		GerminateTableStreamer streamer = new GerminateTableQuery(properties, this, formatted, null)
				.setStrings(ids)
				.getStreamer();

		File output = createTemporaryFile("export_marker_group", FileType.txt.name());

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, output);
		}
		catch (java.io.IOException e)
		{
			throw new jhi.germinate.shared.exception.IOException(e);
		}

		return new ServerResult<>(streamer.getDebugInfo(), output.getName());
	}

	@Override
	public ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MarkerManager.getIdsForFilter(userAuth, filter);
	}
}
