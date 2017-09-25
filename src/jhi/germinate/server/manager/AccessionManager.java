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
public class AccessionManager extends AbstractManager<Accession>
{
	public static final String[] COLUMNS_TABLE = {Accession.ID, Accession.GENERAL_IDENTIFIER, Accession.NAME, Accession.NUMBER, Accession.COLLNUMB, Taxonomy.GENUS, Taxonomy.SPECIES, Subtaxa.TAXONOMY_IDENTIFIER, Location.LATITUDE, Location.LONGITUDE, Location.ELEVATION, Accession.COLLDATE, Country.COUNTRY_NAME, COUNT, LocationService.DISTANCE};

	private static final String COMMON_TABLES = "germinatebase LEFT JOIN subtaxa ON germinatebase.subtaxa_id = subtaxa.id LEFT JOIN taxonomies ON germinatebase.taxonomy_id = taxonomies.id LEFT JOIN locations ON germinatebase.location_id = locations.id LEFT JOIN countries ON locations.country_id = countries.id LEFT JOIN biologicalstatus ON biologicalstatus.id = germinatebase.biologicalstatus_id LEFT JOIN institutions ON institutions.id = germinatebase.institution_id LEFT JOIN collectingsources ON collectingsources.id = germinatebase.collsrc_id";

	private static final String SELECT_BY_GID                  = "SELECT * FROM germinatebase WHERE general_identifier = ?";
	private static final String SELECT_BY_DEFAULT_DISPLAY_NAME = "SELECT * FROM germinatebase WHERE name = ?";
	private static final String SELECT_BY_UNKNOWN_IDENTIFIER   = "SELECT * FROM germinatebase WHERE name LIKE ? OR id LIKE ? OR number LIKE ? OR general_identifier LIKE ?";

	private static final String SELECT_IDS_FOR_FILTER   = "SELECT DISTINCT germinatebase.id FROM " + COMMON_TABLES + " {{FILTER}}";
	private static final String SELECT_NAMES_FOR_FILTER = "SELECT DISTINCT germinatebase.name FROM " + COMMON_TABLES + " {{FILTER}}";

	private static final String SELECT_ALL_FOR_FILTER_EXPORT  = "SELECT germinatebase.id AS germinatebase_id, germinatebase.general_identifier AS germinatebase_gid, germinatebase.name AS germinatebase_name, germinatebase.number AS germinatebase_number, germinatebase.collnumb AS germinatebase_collnumb, taxonomies.genus AS taxonomies_genus, taxonomies.species AS taxomonies_species, locations.latitude AS locations_latitude, locations.longitude AS locations_longitude, locations.elevation AS locations_elevation, countries.country_name AS countries_country_name, germinatebase.colldate AS germinatebase_colldate FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_FILTER         = "SELECT * FROM " + COMMON_TABLES + " {{FILTER}} %s LIMIT ?, ?";
	private static final String SELECT_BY_IDS                 = "SELECT * FROM " + COMMON_TABLES + " WHERE germinatebase.id IN (%s) %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_COLLSITE       = "SELECT * FROM " + COMMON_TABLES + " WHERE locations.id = ? %s LIMIT ?, ?";
	private static final String SELECT_IDS_FOR_MEGA_ENV       = "SELECT DISTINCT(germinatebase.id) FROM " + COMMON_TABLES + " LEFT JOIN megaenvironmentdata ON megaenvironmentdata.location_id = locations.id LEFT JOIN megaenvironments ON megaenvironments.id = megaenvironmentdata.megaenvironment_id WHERE megaenvironments.id = ?";
	private static final String SELECT_IDS_FOR_MEGA_ENV_UNK   = "SELECT DISTINCT(germinatebase.id) FROM " + COMMON_TABLES + " WHERE location_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM megaenvironmentdata WHERE megaenvironmentdata.location_id = locations.id)";
	private static final String SELECT_ALL_FOR_MEGA_ENV       = "SELECT * FROM " + COMMON_TABLES + " LEFT JOIN megaenvironmentdata ON megaenvironmentdata.location_id = locations.id LEFT JOIN megaenvironments ON megaenvironments.id = megaenvironmentdata.megaenvironment_id WHERE megaenvironments.id = ? %s LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_MEGA_ENV_UNK   = "SELECT * FROM " + COMMON_TABLES + " WHERE location_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM megaenvironmentdata WHERE megaenvironmentdata.location_id = locations.id) %s LIMIT ?, ?";
	private static final String SELECT_FOR_GROUP              = "SELECT * FROM " + COMMON_TABLES + " LEFT JOIN groupmembers ON germinatebase.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id WHERE groups.id = ? %s LIMIT ?, ?";
	private static final String SELECT_ALL_SORTED_BY_DISTANCE = "SELECT *, CAST(REPLACE(FORMAT(6378.7 * ACOS(SIN(RADIANS(latitude)) * SIN(RADIANS(?)) + COS(RADIANS(latitude)) * COS(RADIANS(?)) * COS(RADIANS(?) - RADIANS(longitude))),   2), ',','') AS DECIMAL(10,4)) AS distance FROM " + COMMON_TABLES + " LEFT JOIN locationtypes ON locations.locationtype_id = locationtypes.id WHERE locationtypes.name = 'collectingsites' AND locations.latitude IS NOT NULL AND locations.longitude IS NOT NULL %s LIMIT ?, ?";
	private static final String SELECT_IDS_IN_POLYGON         = "SELECT DISTINCT(germinatebase.id) FROM " + COMMON_TABLES + " LEFT JOIN locationtypes ON locations.locationtype_id = locationtypes.id WHERE locationtypes.name = ? AND !ISNULL(locations.latitude) AND !ISNULL(locations.longitude) AND ST_CONTAINS (ST_PolygonFromText(?), ST_GeomFromText (CONCAT( 'POINT(', locations.longitude, ' ', locations.latitude, ')')))";
	private static final String SELECT_ALL_IN_POLYGON         = "SELECT * FROM " + COMMON_TABLES + " LEFT JOIN locationtypes ON locations.locationtype_id = locationtypes.id WHERE locationtypes.name = ? AND !ISNULL(locations.latitude) AND !ISNULL(locations.longitude) AND ST_CONTAINS (ST_PolygonFromText(?), ST_GeomFromText (CONCAT( 'POINT(', locations.longitude, ' ', locations.latitude, ')'))) %s LIMIT ?, ?";

	private static final String SELECT_COUNT = "SELECT COUNT(1) AS count FROM germinatebase";

	private static final String SELECT_IDS_FOR_GROUP = "SELECT germinatebase.id FROM " + COMMON_TABLES + " LEFT JOIN groupmembers ON germinatebase.id = groupmembers.foreign_id LEFT JOIN groups ON groups.id = groupmembers.group_id WHERE groups.id = ?";

	private static final String EXPORT_MCPD = "SELECT puid AS PUID, institutions. CODE AS INSTCODE, germinatebase.name AS ACCENUMB, collnumb AS COLLNUMB, collcode AS COLLCODE, collname AS COLLNAME, institutions.address AS COLLINSTADDRESS, collmissid AS COLLMISSID, taxonomies.genus AS GENUS, taxonomies.species AS SPECIES, taxonomies.species_author AS SPAUTHOR, subtaxa.taxonomic_identifier AS SUBTAXA, subtaxa.subtaxa_author AS SUBTAUTHOR, taxonomies.cropname AS CROPNAME, number AS ACCENAME, acqdate AS acqdate, countries.country_code3 AS ORIGCTY, locations.site_name AS COLLSITE, locations.latitude AS DECLATITUDE, NULL AS LATITUDE, locations.longitude AS DECLONGITUDE, NULL AS LONGITUDE, locations.coordinate_uncertainty AS COORDUNCERT, locations.coordinate_datum AS COORDDATUM, locations.georeferencing_method AS GEOREFMETH, locations.elevation AS ELEVATION, DATE_FORMAT(colldate, \"%Y%m%d\") AS COLLDATE, breeders_code AS BREDCODE, breeders_name AS BREDNAME, biologicalstatus_id AS SAMPSTAT, pedigreedefinitions.definition AS ANCEST, collsrc_id AS COLLSRC, donor_code AS DONORCODE, donor_name AS DONORNAME, donor_number AS DONORNUMB, othernumb AS OTHERNUMB, duplsite AS DUPLSITE, duplinstname AS DUPLINSTNAME, GROUP_CONCAT(storage_id SEPARATOR \";\") AS STORAGE, mlsstatus_id AS MLSSTAT, GROUP_CONCAT( B.attributevalue SEPARATOR \";\") AS REMARKS FROM germinatebase LEFT JOIN institutions ON institutions.id = germinatebase.institution_id LEFT JOIN taxonomies ON taxonomies.id = germinatebase.taxonomy_id LEFT JOIN subtaxa ON subtaxa.id = germinatebase.subtaxa_id LEFT JOIN locations ON locations.id = germinatebase.location_id LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN storagedata ON storagedata.germinatebase_id = germinatebase.id LEFT JOIN STORAGE ON STORAGE .id = storagedata.storage_id LEFT JOIN pedigreedefinitions ON pedigreedefinitions.germinatebase_id = germinatebase.id LEFT JOIN ( SELECT germinatebase_id AS id, attributedata.value AS attributevalue FROM attributes LEFT JOIN attributedata ON attributes.id = attributedata.attribute_id AND attributes.description = \"Remarks\" ) B ON B.id = germinatebase.id GROUP BY germinatebase.id, pedigreedefinitions.id";

	private static final String[] COLUMNS_ACCESSION_DATA_EXPORT = {"germinatebase_id", "germinatebase_gid", "germinatebase_name", "germinatebase_number", "germinatebase_collnumb", "taxonomies_genus", "taxomonies_species", "locations_latitude", "locations_longitude", "locations_elevation", "countries_country_name", "germinatebase_colldate"};

	@Override
	protected String getTable()
	{
		return "germinatebase";
	}

	@Override
	protected DatabaseObjectParser<Accession> getParser()
	{
		return Accession.Parser.Inst.get();
	}

	/**
	 * Returns the {@link Accession}s with the given ids
	 *
	 * @param user       The current user
	 * @param ids        The id of the accession
	 * @param pagination The {@link Pagination} object defining the current chunk of data
	 * @return The {@link Accession}s with the given ids
	 * @throws DatabaseException      Thrown if the interaction with the database failed
	 * @throws InvalidColumnException Thrown if the sort column is invalid
	 */
	public static ServerResult<List<Accession>> getByIds(UserAuth user, List<String> ids, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(AccessionService.COLUMNS_SORTABLE, Accession.ID);

		String formatted = String.format(SELECT_BY_IDS, Util.generateSqlPlaceholderString(ids.size()), pagination.getSortQuery());
		return new DatabaseObjectQuery<Accession>(formatted, user)
				.setStrings(ids)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjects(Accession.Parser.Inst.get(), true);
	}

	public static ServerResult<Long> getCount(UserAuth user) throws DatabaseException
	{
		return new ValueQuery(SELECT_COUNT, user)
				.run(COUNT)
				.getLong(0L);
	}

	/**
	 * Returns the {@link Accession} with the given {@link Accession#NAME}.
	 *
	 * @param user The current user
	 * @param name The id of the accession
	 * @return The {@link Accession} with the given {@link Accession#NAME}.
	 * @throws DatabaseException Thrown if the interaction with the database failed
	 */
	public static ServerResult<Accession> getByDefaultDisplayName(UserAuth user, String name) throws DatabaseException
	{
		if (StringUtils.isEmpty(name))
			return new ServerResult<>(null, null);

		return new DatabaseObjectQuery<Accession>(SELECT_BY_DEFAULT_DISPLAY_NAME, user)
				.setString(name)
				.run()
				.getObject(Accession.Parser.Inst.get());
	}

	/**
	 * Returns the {@link Accession} with the given {@link Accession#GENERAL_IDENTIFIER}
	 *
	 * @param userAuth The user requesting the data
	 * @param name     The name of the accession
	 * @return The {@link Accession} with the given {@link Accession#GENERAL_IDENTIFIER}
	 * @throws DatabaseException Thrown if the interaction with the database failed
	 */
	public static ServerResult<List<Accession>> getByUnknownIdentifier(UserAuth userAuth, String name) throws DatabaseException
	{
		if (StringUtils.isEmpty(name))
			return new ServerResult<>(null, null);

		return new DatabaseObjectQuery<Accession>(SELECT_BY_UNKNOWN_IDENTIFIER, userAuth)
				.setString(name)
				.setString(name)
				.setString(name)
				.setString(name)
				.run()
				.getObjects(Accession.Parser.Inst.get());
	}

	/**
	 * Returns the ids of all the {@link Accession}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param user   The user requesting the data
	 * @param filter The user-specified filter
	 * @return The ids of all the {@link Accession}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static ServerResult<List<String>> getIdsForFilter(UserAuth user, PartialSearchQuery filter) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		return getFilteredValueQuery(filter, user, SELECT_IDS_FOR_FILTER, AccessionService.COLUMNS_SORTABLE)
				.run(Accession.ID)
				.getStrings();
	}

	/**
	 * Returns the names of all the {@link Accession}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param user   The user requesting the data
	 * @param filter The user-specified filter
	 * @return The names of all the {@link Accession}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static ServerResult<List<String>> getNamesForFilter(UserAuth user, PartialSearchQuery filter) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		return getFilteredValueQuery(filter, user, SELECT_NAMES_FOR_FILTER, AccessionService.COLUMNS_SORTABLE)
				.run(Accession.NAME)
				.getStrings();
	}

	/**
	 * Returns all the paginated {@link Accession}s fulfilling the {@link PartialSearchQuery} filter.
	 *
	 * @param user       The user requesting the data
	 * @param filter     The user-specified filter
	 * @param pagination The pagination object specifying the current chunk of data
	 * @return All the paginated {@link Accession}s fulfilling the {@link PartialSearchQuery} filter.
	 * @throws DatabaseException           Thrown if the interaction with the database failed
	 * @throws InvalidColumnException      Thrown if the sort column is invalid
	 * @throws InvalidArgumentException    Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<Accession>> getAllForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException
	{
		pagination.updateSortColumn(AccessionService.COLUMNS_SORTABLE, Accession.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER, pagination.getSortQuery());

		return AbstractManager.<Accession>getFilteredDatabaseObjectQuery(user, filter, formatted, AccessionService.COLUMNS_SORTABLE, pagination.getResultSize())
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Accession.Parser.Inst.get(), true);
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth userAuth, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, DatabaseException, InvalidSearchQueryException, InvalidArgumentException
	{
		pagination.updateSortColumn(AccessionService.COLUMNS_SORTABLE, Accession.ID);
		String formatted = String.format(SELECT_ALL_FOR_FILTER_EXPORT, pagination.getSortQuery());

		return getFilteredGerminateTableQuery(userAuth, filter, formatted, AccessionService.COLUMNS_SORTABLE, COLUMNS_ACCESSION_DATA_EXPORT)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}

	/**
	 * Returns the accession based on the given {@link Accession#GENERAL_IDENTIFIER}.
	 *
	 * @param user The user requesting the data
	 * @param gid  The {@link Accession#GENERAL_IDENTIFIER} of the accession.
	 * @return The accession based on the given {@link Accession#GENERAL_IDENTIFIER}
	 * @throws DatabaseException Thrown if the interaction with the database failed
	 */
	public static ServerResult<Accession> getByGid(UserAuth user, String gid) throws DatabaseException
	{
		return new DatabaseObjectQuery<Accession>(SELECT_BY_GID, user)
				.setString(gid)
				.run()
				.getObject(Accession.Parser.Inst.get());
	}

	/**
	 * Returns the paginated {@link Accession}s for a specific {@link Group}.
	 *
	 * @param userAuth   The user requesting the data
	 * @param groupId    The id of the group
	 * @param pagination The {@link Pagination} object defining the current chunk of data
	 * @return The paginated {@link Accession}s for a specific {@link Group}
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InvalidColumnException           Thrown if the sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to perform this action
	 */
	public static PaginatedServerResult<List<Accession>> getForGroup(UserAuth userAuth, Long groupId, Pagination pagination) throws DatabaseException, InvalidColumnException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		pagination.updateSortColumn(COLUMNS_TABLE, Accession.ID);
		String formatted = String.format(SELECT_FOR_GROUP, pagination.getSortQuery());

		return new DatabaseObjectQuery<Accession>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setLong(groupId)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Accession.Parser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getIdsForGroup(UserAuth userAuth, Long groupId) throws DatabaseException, InsufficientPermissionsException
	{
		if (!GroupManager.hasAccessToGroup(userAuth, groupId, false))
			throw new InsufficientPermissionsException();

		return new ValueQuery(SELECT_IDS_FOR_GROUP, userAuth)
				.setLong(groupId)
				.run(Accession.ID)
				.getStrings();
	}

	public static PaginatedServerResult<List<Accession>> getAllForMegaEnv(UserAuth userAuth, Long megaEnvId, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.setSortColumn(Util.checkSortColumn(pagination.getSortColumn(), COLUMNS_TABLE, Accession.ID));
		if (Objects.equals(megaEnvId, -1L))
		{
			String formatted = String.format(SELECT_ALL_FOR_MEGA_ENV_UNK, pagination.getSortQuery());

			return new DatabaseObjectQuery<Accession>(formatted, userAuth)
					.setFetchesCount(pagination.getResultSize())
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Accession.Parser.Inst.get(), true);
		}
		else
		{
			String formatted = String.format(SELECT_ALL_FOR_MEGA_ENV, pagination.getSortQuery());

			return new DatabaseObjectQuery<Accession>(formatted, userAuth)
					.setFetchesCount(pagination.getResultSize())
					.setLong(megaEnvId)
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Accession.Parser.Inst.get(), true);
		}
	}

	public static ServerResult<List<String>> getIdsForMegaEnv(UserAuth userAuth, Long megaEnvId) throws DatabaseException
	{
		if (Objects.equals(megaEnvId, -1L))
		{
			return new ValueQuery(SELECT_IDS_FOR_MEGA_ENV_UNK, userAuth)
					.run(Accession.ID)
					.getStrings();
		}
		else
		{
			return new ValueQuery(SELECT_IDS_FOR_MEGA_ENV, userAuth)
					.setLong(megaEnvId)
					.run(Accession.ID)
					.getStrings();
		}
	}

	public static PaginatedServerResult<List<Accession>> getAllSortedByDistance(UserAuth userAuth, Double latitude, Double longitude, Pagination pagination) throws InvalidColumnException, DatabaseException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, LocationService.DISTANCE);

		String formatted = String.format(SELECT_ALL_SORTED_BY_DISTANCE, pagination.getSortQuery());
		return new DatabaseObjectQuery<Accession>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setDouble(latitude)
				.setDouble(latitude)
				.setDouble(longitude)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Accession.DistanceParser.Inst.get(), true);
	}

	public static PaginatedServerResult<List<Accession>> getAllInPolygon(UserAuth userAuth, List<LatLngPoint> bounds, Pagination pagination) throws DatabaseException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Accession.ID);

		String formatted = String.format(SELECT_ALL_IN_POLYGON, pagination.getSortQuery());

		String polygon = LocationManager.getPolygon(bounds);
		return new DatabaseObjectQuery<Accession>(formatted, userAuth)
				.setFetchesCount(pagination.getResultSize())
				.setString(jhi.germinate.shared.enums.LocationType.collectingsites.name())
				.setString(polygon)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Accession.Parser.Inst.get(), true);
	}

	public static ServerResult<List<String>> getIdsInPolygon(UserAuth userAuth, List<LatLngPoint> bounds) throws DatabaseException
	{
		String polygon = LocationManager.getPolygon(bounds);
		return new ValueQuery(SELECT_IDS_IN_POLYGON, userAuth)
				.setString(jhi.germinate.shared.enums.LocationType.collectingsites.name())
				.setString(polygon)
				.run(Accession.ID)
				.getStrings();
	}

	public static ServerResult<Mcpd> getMcpd(UserAuth userAuth, Long id) throws DatabaseException
	{
		try
		{
			Mcpd mcpd = new Mcpd();
			ServerResult<Accession> accession = new AccessionManager().getById(userAuth, id);
			ServerResult<List<Storage>> storage = StorageManager.getForAccessionId(userAuth, id);

			mcpd.setAccession(accession.getServerResult());
			mcpd.setStorage(storage.getServerResult());

			ServerResult<Mcpd> result = new ServerResult<>(DebugInfo.create(userAuth), mcpd);
			result.getDebugInfo().addAll(accession.getDebugInfo());
			result.getDebugInfo().addAll(storage.getDebugInfo());
			return result;
		}
		catch (InsufficientPermissionsException e)
		{
			return null;
		}
	}
}
