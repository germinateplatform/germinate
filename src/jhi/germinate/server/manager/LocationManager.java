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

package jhi.germinate.server.manager;

import java.util.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class LocationManager extends AbstractManager<Location>
{
	public static final String[] COLUMNS_TABLE = {Location.ID, Location.SITE_NAME, Location.REGION, Location.STATE, Country.COUNTRY_NAME, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, COUNT};

	public static final String[] COLUMNS_TABLE_CLIMATE = {Location.ID, Location.SITE_NAME, "m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8", "m9", "m10", "m11", "m12"};

	private static final String SELECT_BY_IDS = "SELECT locations.* FROM locations LEFT JOIN countries ON locations.country_id = countries.id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id WHERE locations.id IN (%s) %s LIMIT ?, ?";

	private static final String COMMON_TABLES = "locations LEFT JOIN countries ON locations.country_id = countries.id LEFT JOIN locationtypes ON locations.locationtype_id = locationtypes.id";

	private static final String SELECT_ALL_FOR_TRIAL         = "SELECT countries.*, COUNT(1) AS count FROM countries LEFT JOIN locations ON locations.country_id = countries.id LEFT JOIN germinatebase ON germinatebase.location_id = locations.id LEFT JOIN phenotypedata ON germinatebase.id = phenotypedata.germinatebase_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE datasets.id IN (%s) AND experimenttypes.description = ? GROUP BY countries.id";
	private static final String SELECT_ALL_FOR_PHENOTYPE     = "SELECT countries.*, AVG(phenotypedata.phenotype_value) AS avg FROM countries LEFT JOIN locations ON locations.country_id = countries.id LEFT JOIN germinatebase ON germinatebase.location_id = locations.id LEFT JOIN phenotypedata ON germinatebase.id = phenotypedata.germinatebase_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE datasets.id IN (%s) AND experimenttypes.description = ? AND phenotypedata.phenotype_id = ? GROUP BY countries.id";
	private static final String SELECT_ALL_FOR_GROUP         = "SELECT locations.* FROM locations LEFT JOIN groupmembers ON locations.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN countries ON locations.country_id = countries.id WHERE groups.id = ? %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_MEGA_ENV      = "SELECT locations.*, COUNT(DISTINCT germinatebase.id) AS count FROM locations LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id LEFT JOIN germinatebase ON germinatebase.location_id = locations.id LEFT JOIN megaenvironmentdata ON locations.id = megaenvironmentdata.location_id WHERE megaenvironmentdata.is_final = 1 AND locationtypes.name = 'collectingsites' AND megaenvironmentdata.megaenvironment_id = ? GROUP BY locations.id %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_MEGA_ENV_UNK  = "SELECT locations.*, COUNT(DISTINCT germinatebase.id) AS count FROM locations LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id LEFT JOIN germinatebase ON germinatebase.location_id = locations.id WHERE locationtypes.name = 'collectingsites' AND NOT EXISTS (SELECT 1 FROM megaenvironmentdata WHERE megaenvironmentdata.is_final = 1 AND megaenvironmentdata.location_id = locations.id) GROUP BY locations.id %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_MEGA_ENV      = "SELECT DISTINCT(locations.id) FROM locations LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id LEFT JOIN germinatebase ON germinatebase.location_id = locations.id LEFT JOIN megaenvironmentdata ON locations.id = megaenvironmentdata.location_id WHERE megaenvironmentdata.is_final = 1 AND locationtypes.name = 'collectingsites' AND megaenvironmentdata.megaenvironment_id = ?";
	private static final String SELECT_IDS_FOR_MEGA_ENV_UNK  = "SELECT DISTINCT(locations.id) FROM locations LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id LEFT JOIN germinatebase ON germinatebase.location_id = locations.id WHERE locationtypes.name = 'collectingsites' AND NOT EXISTS (SELECT 1 FROM megaenvironmentdata WHERE megaenvironmentdata.is_final = 1 AND megaenvironmentdata.location_id = locations.id)";
	private static final String SELECT_ALL_FOR_FILTER        = "SELECT * FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_FILTER        = "SELECT DISTINCT(locations.id) FROM " + COMMON_TABLES + " {{FILTER}}";
	private static final String SELECT_ALL_FOR_FILTER_EXPORT = "SELECT locations.id AS locations_id, locations.site_name AS locations_site_name, locations.region AS locations_region, locations.state AS locations_state, locations.latitude AS locations_latitude, locations.longitude AS locations_longitude, locations.elevation AS locations_elevation, countries.country_name AS countries_country_name FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_CLIMATE_GROUP = "SELECT DISTINCT locations.*, countries.*, locationtypes.* FROM " + COMMON_TABLES + " %s WHERE %s EXISTS (SELECT 1 FROM climatedata WHERE climatedata.location_id = locations.id AND climatedata.climate_id = ? AND climatedata.dataset_id IN (%s))";

	private static final String SELECT_CLIMATE_DATA = "SELECT locations.*, countries.*, climates.name AS climates_name, climates.description AS climates_description, units.unit_name, units.unit_description, MAX(CASE WHEN recording_date = 1 THEN climate_value END) `m1`, MAX(CASE WHEN recording_date = 2 THEN climate_value END) `m2`, MAX(CASE WHEN recording_date = 3 THEN climate_value END) `m3`, MAX(CASE WHEN recording_date = 4 THEN climate_value END) `m4`, MAX(CASE WHEN recording_date = 5 THEN climate_value END) `m5`, MAX(CASE WHEN recording_date = 6 THEN climate_value END) `m6`, MAX(CASE WHEN recording_date = 7 THEN climate_value END) `m7`, MAX(CASE WHEN recording_date = 8 THEN climate_value END) `m8`, MAX(CASE WHEN recording_date = 9 THEN climate_value END) `m9`, MAX(CASE WHEN recording_date = 10 THEN climate_value END) `m10`, MAX(CASE WHEN recording_date = 11 THEN climate_value END) `m11`, MAX(CASE WHEN recording_date = 12 THEN climate_value END) `m12` FROM climatedata LEFT JOIN locations ON climatedata.location_id = locations.id LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN climates ON climatedata.climate_id = climates.id LEFT JOIN units ON units.id = climates.unit_id %s WHERE climatedata.dataset_id IN (%s) AND climate_id = ? %s GROUP BY location_id %s LIMIT ?, ?";

	private static final String SELECT_SORTED_BY_DISTANCE = "SELECT *, CAST(REPLACE(FORMAT(6378.7 * ACOS(SIN(RADIANS(latitude)) * SIN(RADIANS(?)) + COS(RADIANS(latitude)) * COS(RADIANS(?)) * COS(RADIANS(?) - RADIANS(longitude))),   2), ',','') AS DECIMAL(10,4)) AS distance FROM " + COMMON_TABLES + " WHERE locationtypes.name = 'collectingsites' AND locations.latitude IS NOT NULL AND locations.longitude IS NOT NULL %s LIMIT ?, ?";

	private static final String SELECT_IDS_FOR_GROUP = "SELECT locations.id FROM locations LEFT JOIN groupmembers ON locations.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN countries ON locations.country_id = countries.id WHERE groups.id = ?";

	private static final String SELECT_ALL_IN_POLYGON = "SELECT * FROM " + COMMON_TABLES + " WHERE locationtypes.name = ? AND !ISNULL(locations.latitude) AND !ISNULL(locations.longitude) AND ST_CONTAINS (ST_PolygonFromText(?), ST_GeomFromText (CONCAT( 'POINT(', locations.longitude, ' ', locations.latitude, ')'))) %s LIMIT ?, ?";
	private static final String SELECT_IDS_IN_POLYGON = "SELECT DISTINCT(locations.id) FROM " + COMMON_TABLES + " WHERE locationtypes.name = ? AND !ISNULL(locations.latitude) AND !ISNULL(locations.longitude) AND ST_CONTAINS (ST_PolygonFromText(?), ST_GeomFromText (CONCAT( 'POINT(', locations.longitude, ' ', locations.latitude, ')')))";

	private static final String SELECT_COUNT = "SELECT COUNT(1) AS count FROM locations";

	private static final String[] COLUMNS_LOCATION_DATA_EXPORT = {"locations_id", "locations_site_name", "locations_region", "locations_state", "locations_latitude", "locations_longitude", "locations_elevation", "countries_country_name"};

	@Override
	protected String getTable()
	{
		return "locations";
	}

	@Override
	protected DatabaseObjectParser<Location> getParser()
	{
		return Location.Parser.Inst.get();
	}

	public static ServerResult<Long> getCount(UserAuth user) throws DatabaseException
	{
		return new ValueQuery(SELECT_COUNT, user)
				.run(COUNT)
				.getLong(0L);
	}

	public static PaginatedServerResult<List<Location>> getAllForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		pagination.updateSortColumn(LocationService.COLUMNS_LOCATION_SORTABLE, Location.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Location>getFilteredDatabaseObjectQuery(userAuth, filter, formatted, LocationService.COLUMNS_LOCATION_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Location.Parser.Inst.get(), true);
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException
	{
		pagination.updateSortColumn(LocationService.COLUMNS_LOCATION_SORTABLE, Location.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredGerminateTableQuery(userAuth, filter, formatted, LocationService.COLUMNS_LOCATION_SORTABLE, COLUMNS_LOCATION_DATA_EXPORT)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}

	public static ServerResult<List<String>> getIdsForMegaEnv(UserAuth userAuth, Long megaEnvId) throws DatabaseException
	{
		if (megaEnvId == null || Objects.equals(megaEnvId, -1L))
		{
			return new ValueQuery(SELECT_IDS_FOR_MEGA_ENV_UNK, userAuth)
					.run(Location.ID)
					.getStrings();
		}
		else
		{
			return new ValueQuery(SELECT_IDS_FOR_MEGA_ENV, userAuth)
					.setLong(megaEnvId)
					.run(Location.ID)
					.getStrings();
		}
	}

	public static ServerResult<List<String>> getIdsInPolygon(UserAuth userAuth, List<LatLngPoint> bounds) throws DatabaseException
	{
		String polygon = getPolygon(bounds);

		return new ValueQuery(SELECT_IDS_IN_POLYGON, userAuth)
				.setString(LocationType.collectingsites.name())
				.setString(polygon)
				.run(Location.ID)
				.getStrings();
	}

	public static ServerResult<List<String>> getIdsForFilter(UserAuth userAuth, PartialSearchQuery filter) throws InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException
	{
		return AbstractManager.<Location>getFilteredValueQuery(filter, userAuth, SELECT_IDS_FOR_FILTER, LocationService.COLUMNS_LOCATION_SORTABLE)
				.run(Location.ID)
				.getStrings();
	}

	public static ServerResult<List<Country>> getAllForPhenotype(UserAuth user, List<Long> datasetIds, ExperimentType type, Long phenotypeId) throws DatabaseException
	{
		if (phenotypeId == null)
		{
			String formatted = String.format(SELECT_ALL_FOR_TRIAL, Util.generateSqlPlaceholderString(datasetIds.size()));
			return new DatabaseObjectQuery<Country>(formatted, user)
					.setLongs(datasetIds)
					.setString(type.name())
					.run()
					.getObjects(Country.CountParser.Inst.get());
		}
		else
		{
			String formatted = String.format(SELECT_ALL_FOR_PHENOTYPE, Util.generateSqlPlaceholderString(datasetIds.size()));
			return new DatabaseObjectQuery<Country>(formatted, user)
					.setLongs(datasetIds)
					.setString(type.name())
					.setLong(phenotypeId)
					.run()
					.getObjects(Country.AverageParser.Inst.get());
		}
	}

	public static PaginatedServerResult<List<Location>> getAllForGroup(UserAuth userAuth, Long groupId, Pagination pagination) throws DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		pagination.updateSortColumn(COLUMNS_TABLE, Location.ID);
		String formatted = String.format(SELECT_ALL_FOR_GROUP, pagination.getSortQuery());

		return new DatabaseObjectQuery<Location>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLong(groupId)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Location.Parser.Inst.get());
	}

	public static ServerResult<List<Location>> getAllForClimateGroup(UserAuth userAuth, List<Long> datasetIds, Long climateId, Long groupId) throws InsufficientPermissionsException, DatabaseException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String formatted;

		if (groupId != null && groupId >= 0)
			formatted = String.format(SELECT_ALL_FOR_CLIMATE_GROUP, "LEFT JOIN groupmembers ON groupmembers.foreign_id = locations.id", " groupmembers.group_id = ? AND ", Util.generateSqlPlaceholderString(datasetIds.size()));
		else
			formatted = String.format(SELECT_ALL_FOR_CLIMATE_GROUP, "", "", Util.generateSqlPlaceholderString(datasetIds.size()));

		DatabaseObjectQuery<Location> query = new DatabaseObjectQuery<>(formatted, userAuth);

		if (groupId != null && groupId >= 0)
			query.setLong(groupId);

		return query.setLong(climateId)
					.setLongs(datasetIds)
					.run()
					.getObjectsPaginated(Location.Parser.Inst.get());
	}

	public static ServerResult<List<String>> getIdsForGroup(UserAuth userAuth, Long groupId) throws DatabaseException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		return new ValueQuery(SELECT_IDS_FOR_GROUP, userAuth)
				.setLong(groupId)
				.run(Location.ID)
				.getStrings();
	}

	public static PaginatedServerResult<List<Location>> getAllForMegaEnv(UserAuth userAuth, Long megaEnvId, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Location.ID);

		if (megaEnvId == null || Objects.equals(megaEnvId, -1L))
		{
			String formatted = String.format(SELECT_ALL_FOR_MEGA_ENV_UNK, pagination.getSortQuery());
			return new DatabaseObjectQuery<Location>(formatted, userAuth)
					.setFetchesCount(pagination.getResultSize())
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Location.Parser.Inst.get());
		}
		else
		{
			String formatted = String.format(SELECT_ALL_FOR_MEGA_ENV, pagination.getSortQuery());
			return new DatabaseObjectQuery<Location>(formatted, userAuth)
					.setFetchesCount(pagination.getResultSize())
					.setLong(megaEnvId)
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Location.Parser.Inst.get());
		}
	}

	public static PaginatedServerResult<List<Location>> getSortedByDistance(UserAuth userAuth, double latitude, double longitude, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(LocationService.COLUMNS_LOCATION_DISTANCE_SORTABLE, LocationService.DISTANCE);

		String formatted = String.format(SELECT_SORTED_BY_DISTANCE, pagination.getSortQuery());
		return new DatabaseObjectQuery<Location>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setDouble(latitude)
				.setDouble(latitude)
				.setDouble(longitude)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Location.DistanceParser.Inst.get(), true);
	}

	public static ServerResult<List<Location>> getByIds(UserAuth userAuth, List<String> ids, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(LocationService.COLUMNS_LOCATION_SORTABLE, Location.ID);

		String formatted = String.format(SELECT_BY_IDS, Util.generateSqlPlaceholderString(ids.size()), pagination.getSortQuery());
		return new DatabaseObjectQuery<Location>(formatted, userAuth)
				.setStrings(ids)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjects(Location.Parser.Inst.get());
	}

	public static String getPolygon(List<LatLngPoint> bounds)
	{
		StringBuilder builder = new StringBuilder();

		builder.append("POLYGON((");

		if (!CollectionUtils.isEmpty(bounds))
		{
			builder.append(bounds.get(0).longitude)
				   .append(" ")
				   .append(bounds.get(0).latitude);

			for (int i = 1; i < bounds.size(); i++)
			{
				builder.append(", ")
					   .append(bounds.get(i).longitude)
					   .append(" ")
					   .append(bounds.get(i).latitude);
			}

			builder.append(", ")
				   .append(bounds.get(0).longitude)
				   .append(" ")
				   .append(bounds.get(0).latitude);
		}

		builder.append("))");

		return builder.toString();
	}

	public static PaginatedServerResult<List<Location>> getInPolygon(UserAuth userAuth, List<LatLngPoint> bounds, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Location.ID);

		String formatted = String.format(SELECT_ALL_IN_POLYGON, pagination.getSortQuery());

		String polygon = getPolygon(bounds);
		return new DatabaseObjectQuery<Location>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setString(LocationType.collectingsites.name())
				.setString(polygon)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Location.Parser.Inst.get(), true);
	}

	public static PaginatedServerResult<List<ClimateYearData>> getClimateYearData(UserAuth userAuth, List<Long> datasetIds, Long climateId, Long groupId, Pagination pagination) throws DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		if (datasetIds.size() < 1)
			return new PaginatedServerResult<>(null, new ArrayList<>(), 0);

		pagination.updateSortColumn(COLUMNS_TABLE_CLIMATE, Location.ID);

		String formatted;

		if (groupId != null && groupId >= 0)
			formatted = String.format(SELECT_CLIMATE_DATA, "LEFT JOIN groupmembers ON groupmembers.foreign_id = climatedata.location_id", Util.generateSqlPlaceholderString(datasetIds.size()), " AND groupmembers.group_id = ?", pagination.getSortQuery());
		else
			formatted = String.format(SELECT_CLIMATE_DATA, "", Util.generateSqlPlaceholderString(datasetIds.size()), "", pagination.getSortQuery());

		DatabaseObjectQuery<ClimateYearData> query = new DatabaseObjectQuery<ClimateYearData>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLongs(datasetIds)
				.setLong(climateId);

		if (groupId != null && groupId >= 0)
			query.setLong(groupId);

		return query.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(ClimateYearData.Parser.Inst.get(), true);
	}

	public static GerminateTableStreamer getStreamerForClimateYearData(UserAuth userAuth, List<Long> datasetIds, Long climateId, Long groupId, Pagination pagination) throws DatabaseException, InsufficientPermissionsException, InvalidColumnException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		pagination.updateSortColumn(COLUMNS_TABLE_CLIMATE, Location.ID);

		String formatted;

		if (groupId != null && groupId >= 0)
			formatted = String.format(SELECT_CLIMATE_DATA, "LEFT JOIN groupmembers ON groupmembers.foreign_id = climatedata.location_id", Util.generateSqlPlaceholderString(datasetIds.size()), " AND groupmembers.group_id = ?", pagination.getSortQuery());
		else
			formatted = String.format(SELECT_CLIMATE_DATA, "", Util.generateSqlPlaceholderString(datasetIds.size()), "", pagination.getSortQuery());

		GerminateTableQuery query = new GerminateTableQuery(formatted, userAuth, null)
				.setLongs(datasetIds)
				.setLong(climateId);

		if (groupId != null && groupId >= 0)
			query.setLong(groupId);

		return query.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.getStreamer();
	}
}
