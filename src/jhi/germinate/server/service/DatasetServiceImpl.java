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
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link DatasetServiceImpl} is the implementation of {@link CommonService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/dataset"})
public class DatasetServiceImpl extends BaseRemoteServiceServlet implements DatasetService
{
	private static final long serialVersionUID = -2599538621272643710L;

	private static final String QUERY_DATASET_STATS = "SELECT experimenttypes.description AS experimentType, DATE_FORMAT(datasets.created_on, '%%Y') AS theYear, SUM(datasetmeta.nr_of_data_objects) AS nrOfDataObjects, SUM(datasetmeta.nr_of_data_points) AS nrOfDataPoints FROM datasets LEFT JOIN datasetmeta ON datasets.id = datasetmeta.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id LEFT JOIN datasetpermissions ON datasetpermissions.dataset_id = datasets.id LEFT JOIN datasetstates ON datasets.dataset_state_id = datasetstates.id WHERE datasets.is_external = 0 AND DATE_FORMAT(datasets.created_on, '%%Y') IS NOT NULL AND %s GROUP BY experimenttypes.description, theYear ORDER BY experimenttypes.description, theYear";

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException, InsufficientPermissionsException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = DatasetManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-datasets", FileType.txt.name());

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

		boolean isPrivate = PropertyReader.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION);

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

		GerminateTableQuery q = new GerminateTableQuery(formatted, userAuth, null);


		/* If the user isn't an admin, we have to add the user id twice more to the query */
		if (isPrivate && !details.isAdmin())
			q.setLong(userAuth.getId());

		ServerResult<GerminateTable> table = q.run();

		/* Get the distinct years */
		TreeSet<String> years = table.getServerResult()
									 .stream()
									 .map(r -> r.get("theYear"))
									 .collect(Collectors.toCollection(TreeSet::new));

		Map<String, DatasetStats> stats = new TreeMap<>();

		/* For each row in the result */
		for (GerminateRow row : table.getServerResult())
		{
			/* Get the stats */
			String experimentType = row.get("experimentType");
			String year = row.get("theYear");
			String value = row.get("nrOfDataPoints");

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
			return new ServerResult<>(table.getDebugInfo(), file.getName());
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


		return LicenseLogManager.update(userAuth, logs);
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
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
			throw new SystemInReadOnlyModeException();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_DOWNLOAD_TRACKING_ENABLED) && !PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_UNDER_MAINTENANCE))
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

	private static class DatasetStats
	{
		public String experimentType;
		public Map<String, String> yearToCount = new HashMap<>();

		public DatasetStats(String experimentType)
		{
			this.experimentType = experimentType;
		}
	}
}
