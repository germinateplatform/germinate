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
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;

/**
 * {@link ClimateServiceImpl} is the implementation of {@link ClimateService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/climate"})
public class ClimateServiceImpl extends BaseRemoteServiceServlet implements ClimateService
{
	private static final long serialVersionUID = -389762789211435314L;

	private static final String[] COLUMNS_MIN_AVG_MAX_COLLSITE = {"MinCollsite", "MaxCollsite", "recording_date", "MIN", "AVG", "MAX", "unit_abbreviation"};

	//private static final String QUERY_MIN_AVG_MAX_COLLSITE       = "SELECT MaxCollsite, MinCollsite, A.climate_value, A.recording_date, Min, Avg, Max, unit_abbreviation FROM (SELECT * FROM (SELECT site_name AS MaxCollsite, climate_value, recording_date FROM climatedata LEFT JOIN locations ON climatedata.location_id = locations.id LEFT JOIN climates ON climates.id = climatedata.climate_id WHERE climates.id = ? ORDER BY recording_date, climate_value DESC) x GROUP BY recording_date ORDER BY cast(recording_date AS UNSIGNED)) A LEFT JOIN (SELECT recording_date, MIN(CAST(climate_value AS DECIMAL (20, 2))) AS Min, CAST(AVG(climate_value) AS DECIMAL (20, 2)) AS Avg, MAX(CAST(climate_value AS DECIMAL (20, 2))) AS Max, unit_abbreviation FROM climatedata, climates, units, locations WHERE climatedata.climate_id = climates.id AND units.id = climates.unit_id AND locations.id = climatedata.location_id AND climates.id = ? GROUP BY recording_date ORDER BY cast(recording_date AS UNSIGNED)) B ON A.recording_date = B.recording_date LEFT JOIN (SELECT * FROM (SELECT site_name AS MinCollsite, climate_value, recording_date FROM climatedata LEFT JOIN locations ON climatedata.location_id = locations.id LEFT JOIN climates ON climates.id = climatedata.climate_id WHERE climates.id = ? ORDER BY recording_date, climate_value ASC) x GROUP BY recording_date ORDER BY cast(recording_date AS UNSIGNED)) C ON B.recording_date = C.recording_date";
	private static final String QUERY_MIN_AVG_MAX_COLLSITE       = "SELECT MaxCollsite, MinCollsite, A.climate_value, A.recording_date, Min, Avg, Max, unit_abbreviation FROM ( SELECT GROUP_CONCAT(site_name SEPARATOR '; ') AS MaxCollsite, c.recording_date, c.climate_value FROM ( SELECT recording_date, MAX(climate_value) AS max FROM climatedata WHERE climate_id = ? GROUP BY recording_date ) AS x INNER JOIN climatedata AS c ON c.recording_date = x.recording_date AND c.climate_value = x.max LEFT JOIN locations ON locations.id = c.location_id WHERE c.climate_id = ? GROUP BY c.recording_date ORDER BY CAST(c.recording_date AS UNSIGNED) ) A LEFT JOIN ( SELECT recording_date, MIN( CAST( climate_value AS DECIMAL (20, 2) ) ) AS Min, CAST( AVG(climate_value) AS DECIMAL (20, 2) ) AS Avg, MAX( CAST( climate_value AS DECIMAL (20, 2) ) ) AS Max, unit_abbreviation FROM climatedata, climates, units, locations WHERE climatedata.climate_id = climates.id AND units.id = climates.unit_id AND locations.id = climatedata.location_id AND climates.id = ? GROUP BY recording_date ORDER BY cast(recording_date AS UNSIGNED) ) B ON A.recording_date = B.recording_date LEFT JOIN ( SELECT GROUP_CONCAT(site_name SEPARATOR '; ') AS MinCollsite, c.recording_date, c.climate_value FROM ( SELECT recording_date, MIN(climate_value) AS min FROM climatedata WHERE climate_id = ? GROUP BY recording_date ) AS x INNER JOIN climatedata AS c ON c.recording_date = x.recording_date AND c.climate_value = x.min LEFT JOIN locations ON locations.id = c.location_id WHERE c.climate_id = ? GROUP BY c.recording_date ORDER BY CAST(c.recording_date AS UNSIGNED) ) C ON B.recording_date = C.recording_date";
	private static final String QUERY_GROUP_MIN_AVG_MAX_COLLSITE = "SELECT MaxCollsite, MinCollsite, A.climate_value, A.recording_date, Min, Avg, Max, unit_abbreviation FROM ( SELECT GROUP_CONCAT(site_name SEPARATOR '; ') AS MaxCollsite, c.recording_date, c.climate_value FROM ( SELECT recording_date, MAX(climate_value) AS max FROM climatedata LEFT JOIN groupmembers ON groupmembers.foreign_id = climatedata.location_id WHERE climate_id = ? AND groupmembers.group_id = ? GROUP BY recording_date ) AS x INNER JOIN climatedata AS c ON c.recording_date = x.recording_date AND c.climate_value = x.max LEFT JOIN locations ON locations.id = c.location_id LEFT JOIN groupmembers ON groupmembers.foreign_id = c.location_id WHERE c.climate_id = ? AND groupmembers.group_id = ? GROUP BY c.recording_date ORDER BY CAST(c.recording_date AS UNSIGNED) ) A LEFT JOIN ( SELECT recording_date, MIN( CAST( climate_value AS DECIMAL (20, 2) ) ) AS Min, CAST( AVG(climate_value) AS DECIMAL (20, 2) ) AS Avg, MAX( CAST( climate_value AS DECIMAL (20, 2) ) ) AS Max, unit_abbreviation FROM climatedata, climates, units, locations, groupmembers WHERE climatedata.climate_id = climates.id AND units.id = climates.unit_id AND locations.id = climatedata.location_id AND groupmembers.foreign_id = climatedata.location_id AND climates.id = ? AND groupmembers.group_id = ? GROUP BY recording_date ORDER BY cast(recording_date AS UNSIGNED) ) B ON A.recording_date = B.recording_date LEFT JOIN ( SELECT GROUP_CONCAT(site_name SEPARATOR '; ') AS MinCollsite, c.recording_date, c.climate_value FROM ( SELECT recording_date, MIN(climate_value) AS min FROM climatedata LEFT JOIN groupmembers ON groupmembers.foreign_id = climatedata.location_id WHERE climate_id = ? AND groupmembers.group_id = ? GROUP BY recording_date ) AS x INNER JOIN climatedata AS c ON c.recording_date = x.recording_date AND c.climate_value = x.min LEFT JOIN locations ON locations.id = c.location_id LEFT JOIN groupmembers ON groupmembers.foreign_id = c.location_id WHERE c.climate_id = ? AND groupmembers.group_id = ? GROUP BY c.recording_date ORDER BY CAST(c.recording_date AS UNSIGNED) ) C ON B.recording_date = C.recording_date";

	@Override
	public ServerResult<List<Climate>> get(RequestProperties properties, List<Long> datasetIds, boolean hasClimateData) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return ClimateManager.getAll(userAuth, datasetIds, hasClimateData);
	}

	@Override
	public ServerResult<List<Climate>> getWithGroundOverlays(RequestProperties properties) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return ClimateManager.getAllHavingOverlay(userAuth);
	}

	@Override
	public ServerResult<Pair<String, String>> getMinAvgMaxFile(RequestProperties properties, Long climateId, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InvalidSelectionException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableQuery query;

		if (groupId == null)
		{
			query = new GerminateTableQuery(QUERY_MIN_AVG_MAX_COLLSITE, userAuth, COLUMNS_MIN_AVG_MAX_COLLSITE)
					.setLong(climateId)
					.setLong(climateId)
					.setLong(climateId)
					.setLong(climateId)
					.setLong(climateId);
		}
		else
		{
			query = new GerminateTableQuery(QUERY_GROUP_MIN_AVG_MAX_COLLSITE, userAuth, COLUMNS_MIN_AVG_MAX_COLLSITE)
					.setLong(climateId)
					.setLong(groupId)
					.setLong(climateId)
					.setLong(groupId)
					.setLong(climateId)
					.setLong(groupId)
					.setLong(climateId)
					.setLong(groupId)
					.setLong(climateId)
					.setLong(groupId);
		}

		GerminateTableStreamer tempResult = query.getStreamer();

        /* If there is no data, there is no need to continue */
		if (tempResult == null)
		{
			query.close();
			throw new InvalidSelectionException();
		}

		File file = createTemporaryFile("climate", FileType.txt.name());

		Pair<Integer, GerminateRow> stats;

		try
		{
			stats = Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), COLUMNS_MIN_AVG_MAX_COLLSITE, tempResult, file);
		}
		catch (java.io.IOException e)
		{
			throw new jhi.germinate.shared.exception.IOException(e);
		}

		String unit = null;
		if (stats != null && stats.getFirst() > 0)
		{
			unit = stats.getSecond().get("unit_abbreviation");
		}

		return new ServerResult<>(tempResult.getDebugInfo(), new Pair<>(unit, file.getName()));
	}

	@Override
	public PaginatedServerResult<List<ClimateYearData>> getGroupData(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return LocationManager.getClimateYearData(userAuth, datasetIds, climateId, groupId, pagination);
	}

	@Override
	public ServerResult<List<ClimateOverlay>> getClimateOverlays(RequestProperties properties, Long climateId) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return ClimateOverlayManager.getAllForClimate(userAuth, climateId);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, List<Long> datasetIds, Long climateId, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InvalidColumnException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		try (GerminateTableStreamer streamer = LocationManager.getStreamerForClimateYearData(userAuth, datasetIds, climateId, groupId, new Pagination(0, Integer.MAX_VALUE)))
		{
			File result = createTemporaryFile("download-climate", FileType.txt.name());

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
	}
}
