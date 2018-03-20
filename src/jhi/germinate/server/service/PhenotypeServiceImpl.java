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

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.database.*;
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
 * {@link PhenotypeServiceImpl} is the implementation of {@link PhenotypeService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/phenotype"})
public class PhenotypeServiceImpl extends BaseRemoteServiceServlet implements PhenotypeService
{
	private static final long serialVersionUID = -4657496352502981361L;

	private static final String QUERY_CHECK_NUMBER             = "SELECT COUNT(1) as count FROM phenotypedata JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id JOIN germinatebase ON germinatebase.id = phenotypedata.germinatebase_id WHERE phenotypedata.dataset_id IN (%s) AND phenotype_id IN (%s);";
	private static final String QUERY_PHENOTYPE_NAMES          = "SELECT CONCAT(name, IF(ISNULL(phenotypes.unit_id), '', CONCAT(' [', units.unit_abbreviation, ']'))) AS name FROM phenotypes LEFT JOIN units ON units.id = phenotypes.unit_id WHERE phenotypes.id IN (%s)";
	private static final String QUERY_PHENOTYPE_NAMES_COMPLETE = "SELECT CONCAT(name, IF(ISNULL(phenotypes.unit_id), '', CONCAT(' [', units.unit_abbreviation, ']'))) AS name FROM phenotypes LEFT JOIN units ON units.id = phenotypes.unit_id WHERE EXISTS ( SELECT 1 FROM phenotypedata LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id WHERE phenotypedata.phenotype_id = phenotypes.id AND datasets.id IN (%s))";
	private static final String QUERY_DATA                     = "call " + StoredProcedureInitializer.PHENOTYPE_DATA + "(?, ?, ?)";
	private static final String QUERY_DATA_PHENOTYPES          = "call " + StoredProcedureInitializer.PHENOTYPE_DATA_PHENOTYPE + "(?, ?)";
	private static final String QUERY_DATA_COMPLETE            = "call " + StoredProcedureInitializer.PHENOTYPE_DATA_COMPLETE + "(?)";
	private static final String QUERY_PHENOTYPE_STATS          = "SELECT phenotypes.id , phenotypes.`name`, phenotypes.description, units.*, MIN(cast(phenotype_value AS DECIMAL(30,2))) as min, MAX(cast(phenotype_value AS DECIMAL(30,2))) as max, AVG(cast(phenotype_value AS DECIMAL(30,2))) as avg, STD(cast(phenotype_value AS DECIMAL(30,2))) as std, datasets.description as dataset_description FROM datasets LEFT JOIN phenotypedata ON datasets.id = phenotypedata.dataset_id LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN units ON units.id = phenotypes.unit_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE phenotypes.datatype != 'char' AND datasets.id in (%s) GROUP by phenotypes.id, datasets.id";

	@Override
	public ServerResult<Phenotype> getById(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		try
		{
			return new PhenotypeManager().getById(userAuth, id);
		}
		catch (InsufficientPermissionsException e)
		{
			return new ServerResult<>(null, null);
		}
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, List<Long> phenotypeIds, boolean includeId) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

        /* Check if debugging is activated */
		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		String datasets = Util.joinCollection(datasetIds, ", ", true);

		List<String> names = new ArrayList<>();
		names.add(PhenotypeService.NAME);
		if (includeId)
			names.add("dbId");
		names.add(DATASET_NAME);
		names.add(LICENSE_NAME);
		names.add(LOCATION_NAME);
		names.add(TREATMENT_DESCRIPTION);
		names.add(YEAR);

		// If the all items group is selected, export everything
		if (containsAllItemsGroup(groupIds))
			groupIds = null;

		DatabaseStatement stmt;

		/*
		 * Create a query that checks if there actually is data available. If
         * not, the prepared statement from the sql file will fail. Connect to
         * the database and check the session id
         */
		Database database = Database.connectAndCheckSession(properties, this);

		/* If both are empty, return everything */
		if (CollectionUtils.isEmpty(groupIds) && CollectionUtils.isEmpty(phenotypeIds))
		{
			String formatted = String.format(QUERY_PHENOTYPE_NAMES_COMPLETE, Util.generateSqlPlaceholderString(datasetIds.size()));

			ServerResult<List<String>> temp = new ValueQuery(formatted, userAuth)
					.setLongs(datasetIds)
					.run(NAME)
					.getStrings();

			sqlDebug.addAll(temp.getDebugInfo());
			if (!CollectionUtils.isEmpty(temp.getServerResult()))
				names.addAll(temp.getServerResult());

			stmt = database.prepareStatement(QUERY_DATA_COMPLETE);

			int i = 1;
			stmt.setString(i++, datasets);
		}
		/* If just one is empty, return nothing */
		else if (CollectionUtils.isEmpty(datasetIds, phenotypeIds))
		{
			return new ServerResult<>(sqlDebug, null);
		}
		else
		{
			String groups = Util.joinCollection(groupIds, ", ", true);
			String phenotypes = Util.joinCollection(phenotypeIds, ", ", true);

			String formatted = String.format(QUERY_PHENOTYPE_NAMES, Util.generateSqlPlaceholderString(phenotypeIds.size()));

			ServerResult<List<String>> temp = new ValueQuery(formatted, userAuth)
					.setLongs(phenotypeIds)
					.run(NAME)
					.getStrings();

			sqlDebug.addAll(temp.getDebugInfo());
			names.addAll(temp.getServerResult());

			formatted = String.format(QUERY_CHECK_NUMBER, datasets, phenotypes);

			stmt = database.prepareStatement(formatted);

			sqlDebug.add(stmt.getStringRepresentation());

			DatabaseResult rs = stmt.query();

			if (rs.next())
			{
				int number = rs.getInt(AbstractManager.COUNT);

				if (number > 0)
				{
					if (CollectionUtils.isEmpty(groupIds))
						stmt = database.prepareStatement(QUERY_DATA_PHENOTYPES);
					else
						stmt = database.prepareStatement(QUERY_DATA);

					int i = 1;
					if (!CollectionUtils.isEmpty(groupIds))
						stmt.setString(i++, groups);
					stmt.setString(i++, datasets);
					stmt.setString(i++, phenotypes);
				}
			}
		}


		sqlDebug.add(stmt.getStringRepresentation());

		GerminateTable result;

		try
		{
			result = stmt.runQuery(names.toArray(new String[names.size()]));
		}
		catch (DatabaseException e)
		{
			database.close();
			return new ServerResult<>(sqlDebug, null);
		}

		String filePath;

		/* Export the data to a temporary file */
		File file = createTemporaryFile("phenotype", datasetIds, FileType.txt.name());
		filePath = file.getName();

		try
		{
			exportDataToFile("#input=PHENOTYPE", names, result, file);
		}
		catch (java.io.IOException e)
		{
			filePath = null;
		}

		database.close();

		/*
		 * Return the debug information, the path to the temporary file
		 * and the resulting GerminateTable
		 */
		return new ServerResult<>(sqlDebug, filePath);
	}

	@Override
	public ServerResult<List<DataStats>> getOverviewStats(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);
		String formatted = String.format(QUERY_PHENOTYPE_STATS, Util.generateSqlPlaceholderString(datasetIds.size()));

		return new DatabaseObjectQuery<DataStats>(formatted, userAuth)
				.setLongs(datasetIds)
				.run()
				.getObjects(DataStats.Parser.Inst.get(), true);
	}

	@Override
	public PaginatedServerResult<List<PhenotypeData>> getDataForFilter(RequestProperties properties, List<Long> datasetIds, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return PhenotypeManager.getDataForFilter(userAuth, datasetIds, filter, pagination);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, jhi.germinate.shared.exception.IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = PhenotypeManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-phenotypes", FileType.txt.name());

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

	public static void exportDataToFile(String header, List<String> phenotypes, GerminateTable result, File file) throws java.io.IOException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8")))
		{
			bw.write(header);
			bw.newLine();
			bw.write(phenotypes.get(0));

			for (int i = 1; i < phenotypes.size(); i++)
			{
				bw.write("\t" + phenotypes.get(i));
			}

			bw.newLine();

			for (GerminateRow row : result)
			{
				String zero = row.get(phenotypes.get(0));
				String value;
				bw.write(zero == null ? "" : zero);

				for (int i = 1; i < phenotypes.size(); i++)
				{
					String ii = row.get(phenotypes.get(i));
					value = ii == null ? "" : ii;
					bw.write("\t" + value);
				}

				bw.newLine();
			}

		}
	}

	@Override
	public ServerResult<List<Phenotype>> get(RequestProperties properties, List<Long> datasetIds, ExperimentType type, boolean onlyNumeric) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return PhenotypeManager.getAllForType(userAuth, datasetIds, type, onlyNumeric);
	}

	@Override
	public ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return PhenotypeManager.getIdsForFilter(userAuth, filter);
	}
}
