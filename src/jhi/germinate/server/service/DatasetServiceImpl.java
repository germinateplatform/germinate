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

package jhi.germinate.server.service;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * {@link DatasetServiceImpl} is the implementation of {@link CommonService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/dataset"})
public class DatasetServiceImpl extends BaseRemoteServiceServlet implements DatasetService
{
	private static final long serialVersionUID = -2599538621272643710L;

	private static final String QUERY_DATASET_STATS = "SELECT `experimenttypes`.`description` AS experimentType, DATE_FORMAT(`datasets`.`date_start`, '%%Y') AS theYear, SUM(`datasetmeta`.`nr_of_data_objects`) AS nrOfDataObjects, SUM(`datasetmeta`.`nr_of_data_points`) AS nrOfDataPoints FROM `datasets` LEFT JOIN `datasetmeta` ON `datasets`.`id` = `datasetmeta`.`dataset_id` LEFT JOIN `experiments` ON `experiments`.`id` = `datasets`.`experiment_id` LEFT JOIN `experimenttypes` ON `experimenttypes`.`id` = `experiments`.`experiment_type_id` LEFT JOIN `datasetpermissions` ON `datasetpermissions`.`dataset_id` = `datasets`.`id` LEFT JOIN `datasetstates` ON `datasets`.`dataset_state_id` = `datasetstates`.`id` WHERE `datasets`.`is_external` = 0 AND DATE_FORMAT(`datasets`.`created_on`, '%%Y') IS NOT NULL AND %s GROUP BY `experimenttypes`.`description`, theYear ORDER BY `experimenttypes`.`description`, theYear";

	private static final String QUERY_ATTRIBUTE_DATA = "call " + StoredProcedureInitializer.DATASET_ATTRIBUTES + "(?, ?)";

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DefaultStreamer streamer = DatasetManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-datasets", FileType.txt.name());

		try
		{
			Util.writeDefaultToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, result);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		return new ServerResult<>(streamer.getDebugInfo(), result.getName());
	}

	@Override
	public PaginatedServerResult<List<Dataset>> getForFilter(RequestProperties properties, PartialSearchQuery filter, ExperimentType experimentType, Pagination pagination) throws InsufficientPermissionsException, InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return DatasetManager.getAllForFilter(userAuth, filter, experimentType, pagination);
	}

	@Override
	public PaginatedServerResult<List<Dataset>> getForFilterAndTrait(RequestProperties properties, PartialSearchQuery filter, ExperimentType type, Long phenotypeId, Pagination pagination) throws InsufficientPermissionsException, InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return DatasetManager.getAllForFilterAndTrait(userAuth, filter, type, phenotypeId, pagination);
	}

	@Override
	public PaginatedServerResult<List<Dataset>> getForAccession(RequestProperties properties, Long accessionId, Pagination pagination) throws InsufficientPermissionsException, InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return DatasetManager.getAllForAccessionId(userAuth, accessionId, pagination);
	}

	@Override
	public ServerResult<String> getDatasetStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		boolean isPrivate = PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION);

		GatekeeperUserWithPassword details = null;

		if (userAuth != null)
			details = GatekeeperUserManager.getByIdWithPasswordForSystem(null, userAuth.getId());

		/*
		 * If login is required, but the given user id is either invalid or
		 * there is no password set, fail
		 */
		if (isPrivate && (details == null || StringUtils.isEmpty(details.getPassword())))
			return new ServerResult<>(null, null);

		String formatted;

		/*
		 * We restrict the list of visible datasets, if login is required AND
		 * the user isn't an admin
		 */
		if (isPrivate)
		{
			/* Admins can see everything */
			if (details.isAdmin())
			{
				formatted = String.format(QUERY_DATASET_STATS, DatasetManager.BITS_PRIVATE_ADMIN);
			}
			/*
			 * Regular users can see public datasets and private ones they
			 * created themselves
			 */
			else
			{
				formatted = String.format(QUERY_DATASET_STATS, DatasetManager.BITS_PRIVATE_REGULAR);
			}
		}
		else
		{
			/* Else, show all public datasets */
			formatted = String.format(QUERY_DATASET_STATS, DatasetManager.BITS_PUBLIC);
		}

		DefaultQuery q = new DefaultQuery(formatted, userAuth);

		/* If the user isn't an admin, we have to add the user id twice more to the query */
		if (isPrivate && !details.isAdmin())
			q.setLong(userAuth.getId())
			 .setLong(userAuth.getId())
			 .setLong(userAuth.getId());

		DefaultStreamer streamer = q.getStreamer();

		/* Get the distinct years */
		Set<String> years = new TreeSet<>();
		Map<String, DatasetStats> stats = new TreeMap<>();
		DatabaseResult rs;

		while ((rs = streamer.next()) != null)
		{
			String year = rs.getString("theYear");

			if (StringUtils.isEmpty(year))
				continue;

			years.add(year);

			/* Get the stats */
			String experimentType = rs.getString("experimentType");
			String value = rs.getString("nrOfDataPoints");

			/* Store them in the POJO */
			DatasetStats stat = stats.get(experimentType);

			if (stat == null)
				stat = new DatasetStats(experimentType);

			/* Remember the mapping */
			stat.yearToCount.put(year, value);

			stats.put(experimentType, stat);
		}

		boolean hasResult = false;

		/* Write the results to a file */
		File file = createTemporaryFile("dataset_stats", FileType.txt.name());
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
		{
			/* Header row */
			bw.write("ExperimentType\t");
			bw.write(years.stream().collect(Collectors.joining("\t")));
			bw.newLine();

			/* Data rows: For each experimenttype, print the number of data points per year */
			for (DatasetStats stat : stats.values())
			{
				hasResult = true;

				bw.write(stat.experimentType);
				for (String year : years)
				{
					String value = stat.yearToCount.get(year);

					if (StringUtils.isEmpty(value))
						value = "";

					bw.write("\t" + value);
				}

				bw.newLine();
			}
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		if (!hasResult)
			throw new IOException();
		else
			return new ServerResult<>(streamer.getDebugInfo(), file.getName());
	}

	@Override
	public PaginatedServerResult<List<Dataset>> getForMarker(RequestProperties properties, Pagination pagination, Long markerId) throws InvalidSessionException, DatabaseException, InvalidColumnException, InsufficientPermissionsException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return DatasetManager.getAllForMarkerId(userAuth, markerId, pagination);
	}

	@Override
	public ServerResult<Boolean> updateLicenseLogs(RequestProperties properties, List<LicenseLog> logs) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		ServerResult<Boolean> result = LicenseLogManager.update(userAuth, logs);

		// We need to put it back into the session.
		storeInSession(Session.USER, userAuth);

		return result;
	}

	@Override
	public PaginatedServerResult<List<Dataset>> getWithUnacceptedLicense(RequestProperties properties, List<ExperimentType> types, Pagination pagination) throws InvalidSessionException, InsufficientPermissionsException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return DatasetManager.getAllWithUnacceptedLicense(userAuth, types, pagination);
	}

	@Override
	public ServerResult<Experiment> getExperiment(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return new ExperimentManager().getById(userAuth, id);
	}

	@Override
	public ServerResult<Boolean> trackDatasetAccess(RequestProperties properties, List<Long> datasetIds, UnapprovedUser user) throws InvalidSessionException, DatabaseException, SystemInReadOnlyModeException
	{
		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		if (PropertyWatcher.getBoolean(ServerProperty.GERMINATE_DOWNLOAD_TRACKING_ENABLED) && !PropertyWatcher.getBoolean(ServerProperty.GERMINATE_IS_UNDER_MAINTENANCE))
		{
			boolean worked = true;
			for (Long dataset : datasetIds)
				worked |= DatasetManager.addTracking(userAuth, dataset, user);

			return new ServerResult<>(worked);
		}
		else
		{
			return new ServerResult<>(false);
		}
	}

	@Override
	public ServerResult<String> exportAttributes(RequestProperties properties, List<Long> datasetIds, List<Long> attributeIds) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		/* Check if debugging is activated */
		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		String datasets = Util.joinCollection(datasetIds, ", ", true);
		String attributes = null;

		List<String> names = new ArrayList<>();
		names.add(PhenotypeService.DATASET_NAME);
		names.add(PhenotypeService.DATASET_DESCRIPTION);
		names.add(PhenotypeService.DATASET_VERSION);
		names.add(PhenotypeService.LICENSE_NAME);

		DefaultQuery stmt;

		ServerResult<List<Attribute>> attributeList = null;

		// If no specific attributes have been requested, get them all
		if (CollectionUtils.isEmpty(attributeIds))
		{
			PartialSearchQuery filter = new PartialSearchQuery();
			filter.add(new SearchCondition(Attribute.TARGET_TABLE, new Equal(), GerminateDatabaseTable.datasets.name(), String.class));
			try
			{
				attributeList = AttributeManager.getAllForFilter(userAuth, filter, Pagination.getDefault());
			}
			catch (InvalidSearchQueryException | InvalidArgumentException | InvalidColumnException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			attributeList = AttributeManager.getForIds(userAuth, attributeIds);
			attributes = Util.joinCollection(attributeIds, ", ", true);
		}

		if (attributeList != null)
		{
			sqlDebug.addAll(attributeList.getDebugInfo());
			if (!CollectionUtils.isEmpty(attributeList.getServerResult()))
			{
				attributeList.getServerResult().stream()
							 .map(Attribute::getName)
							 .forEach(names::add);
			}
		}

		stmt = new DefaultQuery(QUERY_ATTRIBUTE_DATA, null);

		stmt.setString(datasets);
		if (attributes == null)
			stmt.setNull(Types.VARCHAR);
		else
			stmt.setString(attributes);

		DefaultStreamer result;

		try
		{
			result = stmt.getStreamer();
		}
		catch (DatabaseException e)
		{
			return new ServerResult<>(sqlDebug, null);
		}

		sqlDebug.add(stmt.getStringRepresentation());

		String filePath;

		/* Export the data to a temporary file */
		File file = createTemporaryFile("attributes", datasetIds, FileType.txt.name());
		filePath = file.getName();

		try
		{
			PhenotypeServiceImpl.exportDataToFile(null, names.toArray(new String[0]), result, file);
		}
		catch (java.io.IOException e)
		{
			filePath = null;
		}

		/*
		 * Return the debug information, the path to the temporary file
		 * and the resulting GerminateTable
		 */
		return new ServerResult<>(sqlDebug, filePath);
	}

	@Override
	public ServerResult<String> getDublinCoreJson(RequestProperties properties, Long datasetId) throws InvalidSessionException, DatabaseException, InsufficientPermissionsException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		ServerResult<Dataset> dataset = new DatasetManager().getById(userAuth, datasetId);

		if (dataset.hasData())
		{
			File file = createTemporaryFile("dublin-core-", datasetId, FileType.json.name());

			try
			{
				Files.write(file.toPath(), dataset.getServerResult().getDublinCore().getBytes());
				return new ServerResult<>(dataset.getDebugInfo(), file.getName());
			}
			catch (java.io.IOException e)
			{
				throw new IOException(e);
			}
		}

		return new ServerResult<>(dataset.getDebugInfo(), null);
	}

	private static class DatasetStats
	{
		public String              experimentType;
		public Map<String, String> yearToCount = new HashMap<>();

		public DatasetStats(String experimentType)
		{
			this.experimentType = experimentType;
		}
	}
}
