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
import java.util.stream.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetManager extends AbstractManager<Dataset>
{
	public static final String[] COLUMNS_TABLE = {Dataset.ID, Experiment.ID, "experimenttypes.description", "experiment_name", Dataset.DATATYPE, License.NAME, License.DESCRIPTION, Dataset.DESCRIPTION, Dataset.CONTACT, Dataset.DATE_START, Dataset.DATE_END, Dataset.NR_OF_DATA_OBJECTS, Dataset.NR_OF_DATA_POINTS};

	private static final String DATA_POINTS_SUB_QUERY = "datasets LEFT JOIN datasetmeta ON datasets.id = datasetmeta.dataset_id LEFT JOIN datasetstates ON datasetstates.id = datasets.dataset_state_id LEFT JOIN datasetpermissions ON datasetpermissions.dataset_id = datasets.id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id LEFT JOIN locations ON locations.id = datasets.location_id LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN licenses ON licenses.id = datasets.license_id";

	private static final String SELECT_BY_ID               = "SELECT datasets.* FROM datasets LEFT JOIN datasetstates ON datasetstates.id = datasets.dataset_state_id LEFT JOIN datasetpermissions ON datasets.id = datasetpermissions.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE %s %s AND datasets.id IN ({{DATASET_IDS}}) LIMIT ?, ?";
	private static final String SELECT_ALL                 = "SELECT datasets.*, experiments.*, experimenttypes.*, locations.*, countries.*, datasetmeta.*, licenses.* FROM " + DATA_POINTS_SUB_QUERY + " {{FILTER}} %s %s AND datasets.is_external = ? GROUP BY datasets.id, datasetmeta.id {{SORT_BITS}} LIMIT ?, ?";
	private static final String SELECT_ALL_WITHOUT_LICENSE = "SELECT datasets.*, experiments.*, experimenttypes.*, locations.*, countries.*, datasetmeta.*, licenses.* FROM " + DATA_POINTS_SUB_QUERY + " {{FILTER}} %s %s AND !ISNULL(licenses.id) AND NOT EXISTS (SELECT 1 FROM licenselogs WHERE licenselogs.license_id = licenses.id AND licenselogs.user_id = ?) AND datasets.is_external = ? GROUP BY datasets.id, datasetmeta.id {{SORT_BITS}} LIMIT ?, ?";
	private static final String SELECT_ALL_EXPORT          = "SELECT datasets.id AS datasets_id, experimenttypes.description AS experimenttypes_description, experiments.description AS experiments_description, datasets.description AS datasets_description, licenses.name AS licenses_name, licenses.description AS licenses_description, datasets.contact AS datasets_contact, datasets.date_start AS datasets_date_start, datasets.date_end AS datasets_date_end, datasetmeta.nr_of_data_objects AS datasetmeta_nr_of_data_objects, datasetmeta.nr_of_data_points AS datasets_nr_of_data_points FROM " + DATA_POINTS_SUB_QUERY + " {{FILTER}} %s %s AND datasets.is_external = ? GROUP BY datasets.id, datasetmeta.id {{SORT_BITS}} LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_ACCESSION   = "SELECT datasets.*, experiments.*, experimenttypes.*, locations.*, countries.*, datasetmeta.*, licenses.* FROM " + DATA_POINTS_SUB_QUERY + " WHERE %s %s AND (EXISTS (SELECT 1 FROM phenotypedata WHERE phenotypedata.dataset_id = datasets.id AND phenotypedata.germinatebase_id = ?) OR EXISTS (SELECT 1 FROM compounddata WHERE compounddata.dataset_id = datasets.id AND compounddata.germinatebase_id = ?) OR EXISTS (SELECT 1 FROM datasetmembers WHERE datasetmembers.dataset_id = datasets.id AND datasetmembers.datasetmembertype_id = 2 AND datasetmembers.foreign_id = ? )) AND datasets.is_external = ? GROUP BY datasets.id, datasetmeta.id {{SORT_BITS}} LIMIT ?, ?";
	private static final String SELECT_ALL_FOR_MARKER      = "SELECT datasets.*, experiments.*, experimenttypes.*, locations.*, countries.*, datasetmeta.*, licenses.* FROM " + DATA_POINTS_SUB_QUERY + " WHERE %s %s AND EXISTS (SELECT 1 FROM datasetmembers WHERE datasetmembers.dataset_id = datasets.id AND datasetmembers.datasetmembertype_id = 1 AND datasetmembers.foreign_id = ? ) AND datasets.is_external = ? GROUP BY datasets.id, datasetmeta.id {{SORT_BITS}} LIMIT ?, ?";

	public static final  String BITS_PRIVATE_REGULAR   = " (datasetstates.name = '" + DatasetState.PUBLIC.getName() + "' OR (datasetstates.name = '" + DatasetState.PRIVATE.getName() + "' AND datasets.created_by = ?) OR EXISTS (SELECT 1 FROM datasetpermissions WHERE datasetpermissions.user_id = ? AND datasetpermissions.dataset_id = datasets.id) OR EXISTS (SELECT 1 FROM datasetpermissions LEFT JOIN usergroups ON usergroups.id = datasetpermissions.group_id LEFT JOIN usergroupmembers ON usergroupmembers.usergroup_id = usergroups.id WHERE usergroupmembers.user_id = ? AND datasetpermissions.dataset_id = datasets.id))";
	public static final  String BITS_PRIVATE_ADMIN      = " 1=1";
	public static final  String BITS_PUBLIC             = " (datasetstates.name = '" + DatasetState.PUBLIC.getName() + "')";
	private static final String SELECT_FOR_USER_ADMIN  = "SELECT datasets.* FROM datasets LEFT JOIN datasetstates ON datasets.dataset_state_id = datasetstates.id LEFT JOIN datasetpermissions ON datasets.id = datasetpermissions.dataset_id WHERE datasets.is_external = 0";
	private static final String SELECT_FOR_USER_REGULAR = "SELECT datasets.* FROM datasets LEFT JOIN datasetstates ON datasets.dataset_state_id = datasetstates.id LEFT JOIN datasetpermissions ON datasets.id = datasetpermissions.dataset_id WHERE datasets.is_external = 0 AND (datasetstates.name = '" + DatasetState.PUBLIC + "' OR (datasetstates.name = '" + DatasetState.PRIVATE + "' AND datasets.created_by = ?) OR EXISTS (SELECT 1 FROM datasetpermissions WHERE datasetpermissions.user_id = ? AND datasetpermissions.dataset_id = datasets.id) OR EXISTS (SELECT 1 FROM datasetpermissions LEFT JOIN usergroups ON usergroups.id = datasetpermissions.group_id LEFT JOIN usergroupmembers ON usergroupmembers.usergroup_id = usergroups.id WHERE usergroupmembers.user_id = ? AND datasetpermissions.dataset_id = datasets.id))";
	private static final String SELECT_FOR_USER_PUBLIC = "SELECT datasets.* FROM datasets LEFT JOIN datasetstates ON datasets.dataset_state_id = datasetstates.id LEFT JOIN datasetpermissions ON datasets.id = datasetpermissions.dataset_id WHERE datasets.is_external = 0 AND (datasetstates.name = '" + DatasetState.PUBLIC + "')";

	private static final String[] COLUMNS_DATASET_DATA_EXPORT = {"datasets_id", "experimenttypes_description", "experiments_description", "datasets_description", "datasets_contact", "datasets_date_start", "datasets_date_end", "datasetmeta_nr_of_data_objects", "datasets_nr_of_data_points"};

	@Override
	protected String getTable()
	{
		return "countries";
	}

	@Override
	protected DatabaseObjectParser<Dataset> getParser()
	{
		return Dataset.Parser.Inst.get();
	}

	@Override
	public ServerResult<Dataset> getById(UserAuth user, Long id) throws DatabaseException, InsufficientPermissionsException
	{
		ServerResult<List<Dataset>> list = getByIds(user, Collections.singletonList(id));

		if (CollectionUtils.isEmpty(list.getServerResult()))
		{
			return new ServerResult<>(list.getDebugInfo(), null);
		}
		else
		{
			return new ServerResult<>(list.getDebugInfo(), list.getServerResult().get(0));
		}
	}

	/**
	 * Returns the list of {@link Dataset}s that correspond to the list of ids
	 *
	 * @param user The user requesting the data
	 * @param ids  The ids of the datasets
	 * @return The list of {@link Dataset}s that correspond to the list of ids
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to access the data
	 */
	public static ServerResult<List<Dataset>> getByIds(UserAuth user, List<Long> ids) throws DatabaseException, InsufficientPermissionsException
	{
		if (CollectionUtils.isEmpty(ids))
			return new ServerResult<>(null, null);

		try
		{
			String formatted = SELECT_BY_ID.replace("{{DATASET_IDS}}", Util.generateSqlPlaceholderString(ids.size()));

			return getDatabaseObjectQuery(formatted, user, null, (ExperimentType) null, null)
					.setLongs(ids)
					.setInt(0)
					.setInt(Integer.MAX_VALUE)
					.run()
					.getObjects(Dataset.Parser.Inst.get());
		}
		catch (InvalidArgumentException | InvalidSearchQueryException | InvalidColumnException e)
		{
			e.printStackTrace();
			return new ServerResult<>(null, null);
		}
	}

	/**
	 * Returns the paginated {@link Dataset}s matching the filter
	 *
	 * @param user       The user requesting the data
	 * @param filter     The {@link PartialSearchQuery} defining the user-specified filter
	 * @param type       The {@link ExperimentType}
	 * @param isExternal Set to <code>true</code> to only get internal datasets, <code>false</code> to get external datasets
	 * @param pagination The {@link Pagination} object defining the current chunk of data
	 * @return The paginated {@link Dataset}s matching the filter
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InvalidColumnException           Thrown if the sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the use doesn't have sufficient permissions to access the data
	 * @throws InvalidArgumentException         Thrown if the query assembly fails
	 * @throws InvalidSearchQueryException      Thrown if the search query is invalid
	 */
	public static PaginatedServerResult<List<Dataset>> getAllForFilter(UserAuth user, PartialSearchQuery filter, ExperimentType type, boolean isExternal, Pagination pagination) throws DatabaseException, InvalidSearchQueryException, InvalidArgumentException, InvalidColumnException, InsufficientPermissionsException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Dataset.ID);

		String formatted = SELECT_ALL.replace("{{SORT_BITS}}", pagination.getSortQuery());
		return getDatabaseObjectQuery(formatted, user, filter, type, pagination.getResultSize())
				.setBoolean(isExternal)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Dataset.Parser.Inst.get(), true);
	}

	public static GerminateTableStreamer getStreamerForFilter(UserAuth user, PartialSearchQuery filter, Pagination pagination) throws InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException, InsufficientPermissionsException, DatabaseException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Dataset.ID);

		String formatted = SELECT_ALL_EXPORT.replace("{{SORT_BITS}}", pagination.getSortQuery());
		return getGerminateTableQuery(formatted, user, filter, null, COLUMNS_DATASET_DATA_EXPORT)
				.setBoolean(false)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.getStreamer();
	}

	/**
	 * Returns the paginated {@link Dataset}s the marker is part of
	 *
	 * @param user        The user requesting the data
	 * @param accessionId The id of the {@link Marker}
	 * @param pagination  The {@link Pagination} object defining the current chunk of data
	 * @return The paginated {@link Dataset}s the marker is part of
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InvalidColumnException           Thrown if the sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the use doesn't have sufficient permissions to access the data
	 */
	public static PaginatedServerResult<List<Dataset>> getAllForMarkerId(UserAuth user, Long markerId, Pagination pagination) throws DatabaseException, InsufficientPermissionsException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Dataset.ID);

		String formatted = SELECT_ALL_FOR_MARKER.replace("{{SORT_BITS}}", pagination.getSortQuery());
		try
		{
			return getDatabaseObjectQuery(formatted, user, null, (ExperimentType) null, pagination.getResultSize())
					.setLong(markerId)
					.setBoolean(false)
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Dataset.Parser.Inst.get());
		}
		catch (InvalidArgumentException | InvalidSearchQueryException e)
		{
			e.printStackTrace();
			return new PaginatedServerResult<>(null, new ArrayList<>(), 0);
		}
	}

	/**
	 * Returns the paginated {@link Dataset}s the accession is part of
	 *
	 * @param user        The user requesting the data
	 * @param accessionId The id of the accession
	 * @param pagination  The {@link Pagination} object defining the current chunk of data
	 * @return The paginated {@link Dataset}s the accession is part of
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InvalidColumnException           Thrown if the sort column is invalid
	 * @throws InsufficientPermissionsException Thrown if the use doesn't have sufficient permissions to access the data
	 */
	public static PaginatedServerResult<List<Dataset>> getAllForAccessionId(UserAuth user, Long accessionId, Pagination pagination) throws DatabaseException, InsufficientPermissionsException, InvalidColumnException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Dataset.ID);

		String formatted = SELECT_ALL_FOR_ACCESSION.replace("{{SORT_BITS}}", pagination.getSortQuery());
		try
		{
			return getDatabaseObjectQuery(formatted, user, null, (ExperimentType) null, pagination.getResultSize())
					.setLong(accessionId)
					.setLong(accessionId)
					.setLong(accessionId)
					.setBoolean(false)
					.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(Dataset.Parser.Inst.get());
		}
		catch (InvalidArgumentException | InvalidSearchQueryException e)
		{
			e.printStackTrace();
			return new PaginatedServerResult<>(null, new ArrayList<>(), 0);
		}
	}

	/**
	 * This is a very ugly and clumsy way of getting the datasets for all scenarios while making sure that only the visible datasets are returned
	 *
	 * @param query The base query containing a <code>{{FILTER}}</code> placeholder
	 * @param user  The current user
	 * @param type  The {@link ExperimentType}; can be <code>null</code>
	 * @return The query that'll return the available datasets
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InsufficientPermissionsException Thrown if the use doesn't have sufficient permissions to access the data
	 */
	private static GerminateTableQuery getGerminateTableQuery(String query, UserAuth user, PartialSearchQuery filter, ExperimentType type, String[] columnNames) throws DatabaseException, InvalidArgumentException, InsufficientPermissionsException, InvalidSearchQueryException, InvalidColumnException
	{
		boolean isPrivate = PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION);

		GatekeeperUserWithPassword details = null;

		if (user != null)
			details = GatekeeperUserManager.getByIdWithPasswordForSystem(null, user.getId());

		String formatted = getFormattedQuery(query, isPrivate, details, Collections.singletonList(type));

		GerminateTableQuery result = getFilteredGerminateTableQuery(user, filter, formatted, COLUMNS_TABLE, columnNames);

		setParameters(result, isPrivate, details, user);

		return result;
	}

	/**
	 * This is a very ugly and clumsy way of getting the datasets for all scenarios while making sure that only the visible datasets are returned
	 *
	 * @param query The base query containing a <code>{{FILTER}}</code> placeholder
	 * @param user  The current user
	 * @param type  The {@link ExperimentType}; can be <code>null</code>
	 * @return The query that'll return the available datasets
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InsufficientPermissionsException Thrown if the use doesn't have sufficient permissions to access the data
	 */
	private static DatabaseObjectQuery<Dataset> getDatabaseObjectQuery(String query, UserAuth user, PartialSearchQuery filter, ExperimentType type, Integer previousCount) throws DatabaseException, InvalidArgumentException, InsufficientPermissionsException, InvalidSearchQueryException, InvalidColumnException
	{
		return getDatabaseObjectQuery(query, user, filter, Collections.singletonList(type), previousCount);
	}

	/**
	 * This is a very ugly and clumsy way of getting the datasets for all scenarios while making sure that only the visible datasets are returned
	 *
	 * @param query The base query containing a <code>{{FILTER}}</code> placeholder
	 * @param user  The current user
	 * @param types The {@link ExperimentType}s; can be <code>null</code>
	 * @return The query that'll return the available datasets
	 * @throws DatabaseException                Thrown if the interaction with the database failed
	 * @throws InsufficientPermissionsException Thrown if the use doesn't have sufficient permissions to access the data
	 */
	private static DatabaseObjectQuery<Dataset> getDatabaseObjectQuery(String query, UserAuth user, PartialSearchQuery filter, List<ExperimentType> types, Integer previousCount) throws DatabaseException, InvalidArgumentException, InsufficientPermissionsException, InvalidSearchQueryException, InvalidColumnException
	{
		boolean isPrivate = PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION);

		GatekeeperUserWithPassword details = null;

		if (user != null)
			details = GatekeeperUserManager.getByIdWithPasswordForSystem(null, user.getId());

		String formatted = getFormattedQuery(query, isPrivate, details, types);

		DatabaseObjectQuery<Dataset> result = getFilteredDatabaseObjectQuery(user, filter, formatted, COLUMNS_TABLE, previousCount);

		setParameters(result, isPrivate, details, user);

		return result;
	}

	private static void setParameters(GerminateQuery<?> query, boolean isPrivate, GatekeeperUserWithPassword details, UserAuth user) throws DatabaseException
	{
		/* If the user isn't an admin, we have to add the user id once more to the query */
		if (isPrivate)
		{
			if (!details.isAdmin())
			{
				query.setLong(user.getId())
					 .setLong(user.getId())
					 .setLong(user.getId());
			}
		}
	}

	private static String getExperimentTypes(List<ExperimentType> types)
	{
		if (CollectionUtils.isEmptyOrNull(types))
			return "";
		else
			return types.stream()
						.map(t -> "'" + t.name() + "'")
						.collect(Collectors.joining(",", "(", ")"));
	}

	private static String getFormattedQuery(String query, boolean isPrivate, GatekeeperUserWithPassword details, List<ExperimentType> types) throws InsufficientPermissionsException
	{
		/*
		 * If login is required, but the given user id is either invalid or
		 * there is no password set, fail
		 */
		if (isPrivate && (details == null || StringUtils.isEmpty(details.getPassword())))
			throw new InsufficientPermissionsException();

		/* If there is a type, make sure to just select this type */
		String experimentTypeExtras = CollectionUtils.isEmptyOrNull(types) ? "" : " AND experimenttypes.description IN " + getExperimentTypes(types);

		String combination = query.contains("{{FILTER}}") ? " AND " : "";

		/* We restrict the list of visible datasets, if login is required */
		if (isPrivate)
		{
			/* Admins can see everything */
			if (details.isAdmin())
			{
				return String.format(query, combination + BITS_PRIVATE_ADMIN, experimentTypeExtras);
			}
			/* Regular users can see public datasets and private ones they created themselves or were given permission to access */
			else
			{
				return String.format(query, combination + BITS_PRIVATE_REGULAR, experimentTypeExtras);
			}
		}
		else
		{
			/* Else, show all public datasets */
			return String.format(query, combination + BITS_PUBLIC, experimentTypeExtras);
		}
	}

	/**
	 * Returns <code>true</code> if the given user has access to the given dataset
	 *
	 * @param userAuth The {@link UserAuth}
	 * @param dataset  The dataset id
	 * @return <code>true</code> if the user has access to the dataset
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public static ServerResult<Boolean> userHasAccessToDataset(UserAuth userAuth, Long dataset) throws DatabaseException
	{
		List<Long> datasetIds = new ArrayList<>(Collections.singletonList(dataset));
		DebugInfo debugInfo = restrictToAvailableDatasets(userAuth, datasetIds);

		boolean result = datasetIds.contains(dataset);

		return new ServerResult<>(debugInfo, result);
	}

	/**
	 * Returns the subset of datasets from the input {@link List} to which the user has access. <p/> If the input is {1,2,3,4}, but the user only has
	 * access to {1,3,7}, then the result will be {1,3}
	 *
	 * @param userAuth   The {@link UserAuth}
	 * @param datasetIds The datasets in question
	 * @return The subset of datasets from the input {@link List} to which the user has access.
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public static DebugInfo restrictToAvailableDatasets(UserAuth userAuth, List<Long> datasetIds) throws DatabaseException
	{
		if (CollectionUtils.isEmpty(datasetIds))
			return DebugInfo.create(userAuth);

		ServerResult<List<Dataset>> availableDatasets = getForUser(userAuth, PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION));

		if (CollectionUtils.isEmpty(availableDatasets.getServerResult()))
		{
			datasetIds.clear();
		}
		else
		{
			List<Long> availableIds = availableDatasets.getServerResult()
													   .stream()
													   .map(Dataset::getId)
													   .collect(Collectors.toList());

			datasetIds.retainAll(availableIds);
		}

		return availableDatasets.getDebugInfo();
	}

	/**
	 * Returns the list of {@link Dataset}s the given user has access to
	 *
	 * @param userAuth The user requesting the data
	 * @return The list of {@link Dataset}s the given user has access to
	 * @throws DatabaseException Thrown if the communication with the database fails
	 */
	public static ServerResult<List<Dataset>> getForUser(UserAuth userAuth, boolean checkLicense) throws DatabaseException
	{
		boolean isPrivate = PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION);

		GatekeeperUserWithPassword details = GatekeeperUserManager.getByIdWithPasswordForSystem(null, userAuth.getId());

		/* If login is required, but the given user id is either invalid or there is no password set, fail */
		if (isPrivate && (details == null || StringUtils.isEmpty(details.getPassword())))
			return new ServerResult<>(DebugInfo.create(userAuth), new ArrayList<>());

		ServerResult<List<Dataset>> result;

		/* We restrict the list of visible datasets, if login is required */
		if (isPrivate)
		{
			/* Admins can see everything (if license accepted) */
			if (details.isAdmin())
			{
				result = new DatabaseObjectQuery<Dataset>(SELECT_FOR_USER_ADMIN, userAuth)
						.run()
						.getObjects(Dataset.Parser.Inst.get());
			}
			/* Regular users can see public datasets and private ones they created themselves */
			else
			{
				result = new DatabaseObjectQuery<Dataset>(SELECT_FOR_USER_REGULAR, userAuth)
						.setLong(userAuth.getId())
						.setLong(userAuth.getId())
						.setLong(userAuth.getId())
						.run()
						.getObjects(Dataset.Parser.Inst.get());
			}
		}
		/* Else, show all public datasets */
		else
		{
			/* Check if the dataset is public */
			result = new DatabaseObjectQuery<Dataset>(SELECT_FOR_USER_PUBLIC, userAuth)
					.run()
					.getObjects(Dataset.Parser.Inst.get());
		}

		if (checkLicense)
		{
			if (!CollectionUtils.isEmpty(result.getServerResult()))
			{
				// Filter the datasets to make sure only the ones where the user accepted the license are included
				// This also means that datasets that have a license in a public environment won't be shown, because we just don't know if the user accepted the license
				List<Dataset> datasets = result.getServerResult()
											   .parallelStream()
											   .filter(d -> {
												   if (d.getLicense() == null)
													   return true;
												   else if (!isPrivate)
													   return false;
												   else
													   return d.hasLicenseBeenAccepted(userAuth.getId());
											   })
											   .collect(Collectors.toList());

				result.setServerResult(datasets);
			}
			else
			{
				result.setServerResult(new ArrayList<>());
			}
		}

		return result;
	}

	public static PaginatedServerResult<List<Dataset>> getAllWithUnacceptedLicense(UserAuth userAuth, List<ExperimentType> types, Pagination pagination) throws InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException, InsufficientPermissionsException, DatabaseException
	{
		pagination.updateSortColumn(COLUMNS_TABLE, Dataset.ID);

		String formatted = SELECT_ALL_WITHOUT_LICENSE.replace("{{SORT_BITS}}", pagination.getSortQuery());
		return getDatabaseObjectQuery(formatted, userAuth, null, types, pagination.getResultSize())
				.setLong(userAuth.getId())
				.setBoolean(false)
				.setInt(pagination.getStart())
				.setInt(pagination.getLength())
				.run()
				.getObjectsPaginated(Dataset.Parser.Inst.get(), true);
	}
}
