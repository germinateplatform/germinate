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
import jhi.germinate.server.util.kml.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.enums.LocationType;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link LocationServiceImpl} is the implementation of {@link LocationService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/location"})
public class LocationServiceImpl extends BaseRemoteServiceServlet implements LocationService
{
	private static final long serialVersionUID = -534823136023353625L;

	private static final String QUERY_COLLSITES_BY_IDS_DOWNLOAD = "SELECT locations.*, locationtypes.name AS locationtypes_name, locationtypes.description AS locationtypes_description, countries.country_name AS countries_country_name FROM locations LEFT JOIN countries ON locations.country_id = countries.id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id WHERE locations.id IN (%s)";

	private static final String QUERY_JSON_LOCATIONS = "call " + StoredProcedureInitializer.LOCATION_JSON + "(?)";

	@Override
	public ServerResult<Location> getById(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		try
		{
			return new LocationManager().getById(userAuth, id);
		}
		catch (InsufficientPermissionsException e)
		{
			return new ServerResult<>(null, null);
		}
	}

	@Override
	public PaginatedServerResult<List<Location>> getByDistance(RequestProperties properties, double latitude, double longitude, Pagination pagination)
			throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		if (StringUtils.isEmpty(pagination.getSortColumn()))
			pagination.setSortColumn(LocationService.DISTANCE);

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getSortedByDistance(userAuth, latitude, longitude, pagination);
	}

	@Override
	public ServerResult<List<String>> getIdsForMegaEnv(RequestProperties properties, Long megaEnvId) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getIdsForMegaEnv(userAuth, megaEnvId);
	}

	@Override
	public ServerResult<List<String>> getIdsInPolygon(RequestProperties properties, List<LatLngPoint> polygon) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getIdsInPolygon(userAuth, polygon);
	}

	@Override
	public ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return LocationManager.getIdsForFilter(userAuth, filter);
	}

	@Override
	public PaginatedServerResult<List<Location>> getForMegaEnv(RequestProperties properties, Long megaEnvId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getAllForMegaEnv(userAuth, megaEnvId, pagination);
	}

	@Override
	public PaginatedServerResult<List<Institution>> getInstitutionsForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return InstitutionManager.getAllForFilter(userAuth, pagination, filter);
	}

	@Override
	public ServerResult<List<Country>> getInstitutionsByCountry(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return InstitutionManager.getGroupedByCountry(userAuth);
	}

	@Override
	public ServerResult<List<Location>> getByIds(RequestProperties properties, Pagination pagination, List<String> ids) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getByIds(userAuth, ids, pagination);
	}

	@Override
	public ServerResult<MegaEnvironment> getMegaEnv(RequestProperties properties, Long megaEnvId) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		try
		{
			return new MegaEnvironmentManager().getById(userAuth, megaEnvId);
		}
		catch (InsufficientPermissionsException e)
		{
			return new ServerResult<>(null, null);
		}
	}

	@Override
	public PaginatedServerResult<List<MegaEnvironment>> getMegaEnvs(RequestProperties properties, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return MegaEnvironmentManager.getAll(userAuth, pagination);
	}

	@Override
	public PaginatedServerResult<List<Location>> getInPolygon(RequestProperties properties, Pagination pagination, List<LatLngPoint> bounds) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getInPolygon(userAuth, bounds, pagination);
	}

	@Override
	public PaginatedServerResult<List<Location>> getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return LocationManager.getAllForFilter(userAuth, filter, pagination);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = LocationManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-locations", FileType.txt.name());

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
	public ServerResult<List<Location>> getForClimateAndGroup(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return LocationManager.getAllForClimateGroup(userAuth, datasetIds, climateId, groupId);
	}

	@Override
	public ServerResult<String> exportForIds(RequestProperties properties, List<String> ids) throws InvalidSessionException, DatabaseException, IOException
	{
		String formatted = String.format(QUERY_COLLSITES_BY_IDS_DOWNLOAD, Util.generateSqlPlaceholderString(ids.size()));
		GerminateTableStreamer streamer = new GerminateTableQuery(properties, this, formatted, null)
				.setStrings(ids)
				.getStreamer();

		File output = createTemporaryFile("export_collsite_group", FileType.txt.name());

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
	public ServerResult<String> getJsonForType(RequestProperties properties, LocationType type) throws InvalidSessionException, DatabaseException, IOException
	{
		/* Get the column from the config file */
//		String hierarchyBy = PropertyReader.get(ServerProperty.GERMINATE_COLLECTINGSITE_TREEMAP_COLUMN, Country.COUNTRY_NAME);
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		ServerResult<String> json = new ValueQuery(QUERY_JSON_LOCATIONS, userAuth)
				.setString(type.name())
				.run("json")
				.getString();

        /* Start writing the file */
		String filePath;

		File file = createTemporaryFile("json", "json");

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")))
		{
			// Replace single quotes with doubles, but make sure to not replace them inside words.
			String replaced = json.getServerResult()
								  .replaceAll("\\s'", " \"")
								  .replaceAll("',", "\",")
								  .replaceAll("':", "\":")
								  .replaceAll("\\\\'", "'")
								  .replaceAll("'}", "\"}");
			bw.write(replaced);

			filePath = file.getName();
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		json.setServerResult(filePath);
		/* Return the debug information and the relative file path */
		return json;
	}

	@Override
	public ServerResult<String> exportToKml(RequestProperties properties, KmlType type, Long id) throws InvalidSessionException, DatabaseException, KMLException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		HttpServletRequest req = this.getThreadLocalRequest();

        /* Check if the session is valid */
		Session.checkSession(properties, req, getThreadLocalResponse());

        /* Get the base url */
		String baseURL = req.getRequestURL().toString().replace(req.getRequestURI(), req.getContextPath());

        /* Create a temporary file */
		File file = createTemporaryFile("kml", "kmz");

		KMLCreator creator = null;

		try
		{
			switch (type)
			{
				case megaEnvironment:
					creator = new KMLCreatorMegaEnv(sqlDebug);
					break;
				case collectingsite:
					creator = new KMLCreatorLocation(sqlDebug);
					break;
				case all:
					creator = new KMLCreatorAll(sqlDebug);
					break;
			}

			creator.createKML(baseURL, id, file);
		}
		catch (java.io.IOException e)
		{
			throw new KMLException(e);
		}

		return new ServerResult<>(sqlDebug, file.getName());
	}

	@Override
	public ServerResult<List<Country>> getCountryValues(RequestProperties properties, List<Long> datasetIds, ExperimentType type, Long phenotypeId) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getAllForPhenotype(userAuth, datasetIds, type, phenotypeId);
	}
}
