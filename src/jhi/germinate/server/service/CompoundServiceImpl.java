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
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link CompoundServiceImpl} is the implementation of {@link CompoundService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/compound"})
public class CompoundServiceImpl extends BaseRemoteServiceServlet implements CompoundService
{
	private static final long serialVersionUID = 1512922584501563921L;

	private static final String QUERY_CHECK_NUMBER = "SELECT COUNT(1) as count FROM compounddata JOIN compounds ON compounds.id = compounddata.compound_id JOIN germinatebase ON germinatebase.id = compounddata.germinatebase_id WHERE compounddata.dataset_id IN (%s) AND compound_id IN (%s)";

	private static final String QUERY_COMPOUND_DATA = "SELECT germinatebase.`name` AS name, compounddata.compound_value AS value FROM compounddata INNER JOIN compounds ON compounddata.compound_id = compounds.id INNER JOIN germinatebase ON compounddata.germinatebase_id = germinatebase.id WHERE compounds.id = ? AND compounddata.dataset_id = ? ORDER BY germinatebase.name";

	private static final String QUERY_PHENOTYPE_STATS      = "SELECT compounds.id, compounds.`name`, compounds.description, MIN( cast( compound_value AS DECIMAL (30, 2) ) ) AS min, MAX( cast( compound_value AS DECIMAL (30, 2) ) ) AS max, AVG( cast( compound_value AS DECIMAL (30, 2) ) ) AS avg, STD( cast( compound_value AS DECIMAL (30, 2) ) ) AS std, datasets.description AS dataset_description FROM datasets LEFT JOIN compounddata ON datasets.id = compounddata.dataset_id LEFT JOIN compounds ON compounds.id = compounddata.compound_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'compound' AND datasets.id IN (%s) GROUP BY compounds.id, datasets.id";
	private static final String QUERY_COMPOUND_BY_COMPOUND = "SELECT DATE_FORMAT(a.recording_date, '%%Y') AS recording_date, datasets.description AS dataset, %s AS name, a.germinatebase_id AS id, TRUNCATE(a.compound_value, 2) AS x, TRUNCATE(b.compound_value, 2) AS y FROM compounddata AS a JOIN compounddata AS b ON a.germinatebase_id = b.germinatebase_id AND a.recording_date <=> b.recording_date AND a.dataset_id = b.dataset_id AND a.dataset_id IN (%s) LEFT JOIN germinatebase ON germinatebase.id = a.germinatebase_id LEFT JOIN groupmembers ON groupmembers.foreign_id = a.germinatebase_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN datasets ON datasets.id = a.dataset_id WHERE groups.id LIKE ? AND a.compound_id = ? AND b.compound_id = ? GROUP BY id, x, y, recording_date, datasets.id";

	private static final String QUERY_COMPOUND_NAMES          = "SELECT CONCAT(name, IF(ISNULL(compounds.unit_id), '', CONCAT(' [', units.unit_abbreviation, ']'))) AS name FROM compounds LEFT JOIN units ON units.id = compounds.unit_id WHERE compounds.id IN (%s)";
	private static final String QUERY_COMPOUND_NAMES_COMPLETE = "SELECT CONCAT(name, IF(ISNULL(compounds.unit_id), '', CONCAT(' [', units.unit_abbreviation, ']'))) AS name FROM compounds LEFT JOIN units ON units.id = compounds.unit_id WHERE EXISTS ( SELECT 1 FROM compounddata LEFT JOIN datasets ON datasets.id = compounddata.dataset_id WHERE compounddata.compound_id = compounds.id AND datasets.id IN (%s))";

	private static final String QUERY_DATA          = "call " + StoredProcedureInitializer.COMPOUND_DATA + "(?, ?, ?)";
	private static final String QUERY_DATA_COMPLETE = "call " + StoredProcedureInitializer.COMPOUND_DATA_COMPLETE + "(?)";

	@Override
	public ServerResult<List<Compound>> getForDatasetIds(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return CompoundManager.getAllForDataset(userAuth, datasetIds);
	}

	@Override
	public PaginatedServerResult<List<Compound>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return CompoundManager.getAllForFilter(userAuth, pagination, filter);
	}

	@Override
	public PaginatedServerResult<List<CompoundData>> getDataForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return CompoundDataManager.getAllForFilter(userAuth, pagination, filter);
	}

	@Override
	public ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return CompoundDataManager.getIdsForFilter(userAuth, filter);
	}

	@Override
	public ServerResult<Compound> getById(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		try
		{
			return new CompoundManager().getById(userAuth, id);
		}
		catch (InsufficientPermissionsException e)
		{
			return new ServerResult<>(null, null);
		}
	}

	@Override
	public ServerResult<List<DataStats>> getDataStatsForDatasets(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);
		String formatted = String.format(QUERY_PHENOTYPE_STATS, Util.generateSqlPlaceholderString(datasetIds.size()));

		return new DatabaseObjectQuery<DataStats>(formatted, userAuth)
				.setLongs(datasetIds)
				.run()
				.getObjects(DataStats.Parser.Inst.get());
	}

	@Override
	public ServerResult<String> getCompoundByCompoundFile(RequestProperties properties, List<Long> datasetIds, Long firstId, Long secondId, Long groupId) throws InvalidSessionException, DatabaseException,
			IOException, InsufficientPermissionsException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String nameColumn = Accession.NAME;

		String formatted = String.format(QUERY_COMPOUND_BY_COMPOUND, nameColumn, Util.generateSqlPlaceholderString(datasetIds.size()));

		GerminateTableStreamer streamer = new GerminateTableQuery(properties, this, formatted, null)
				.setLongs(datasetIds)
				.setString((groupId == null || groupId == -1) ? "%" : Long.toString(groupId))
				.setLong(firstId)
				.setLong(secondId)
				.getStreamer();

		CompoundManager manager = new CompoundManager();

		Compound one = manager.getById(userAuth, firstId).getServerResult();
		Compound two = manager.getById(userAuth, secondId).getServerResult();

		File tempFile = createTemporaryFile("c_by_c", FileType.txt.name());
		File finalFile = createTemporaryFile("c_by_c", FileType.txt.name());

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, tempFile);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

        /* Check if there are at least two rows. Also, replace the "x" and "y" headers with the actual phenotype names and units. */
		try (BufferedReader br = new BufferedReader(new FileReader(tempFile));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(finalFile)))
		{
			int counter = 0;

			for (String line; (line = br.readLine()) != null; counter++)
			{
				/* If it's the header row, replace "x" and "y" column headers with the phenotype names and units */
				if (counter == 0)
				{
					String firstUnit = one.getUnit() != null ? one.getUnit().getAbbreviation() : null;
					String secondUnit = two.getUnit() != null ? two.getUnit().getAbbreviation() : null;
					String firstHeader = one.getName() + (StringUtils.isEmpty(firstUnit) ? "" : " [" + one.getUnit().getAbbreviation() + "]");
					String secondHeader = two.getName() + (StringUtils.isEmpty(secondUnit) ? "" : " [" + two.getUnit().getAbbreviation() + "]");
					line = line.replace("\tx\ty", "\t" + firstHeader + "\t" + secondHeader);
				}

				/* Then write to the final file */
				bw.write(line);
				bw.newLine();
			}

			tempFile.delete();

			if (counter < 2)
			{
				finalFile.delete();
				return new ServerResult<>(streamer.getDebugInfo(), null);
			}

		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

        /* If we get here, the file was successfully generated and contains data */
		return new ServerResult<>(streamer.getDebugInfo(), finalFile.getName());
	}

	@Override
	public ServerResult<String> getExportFile(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, List<Long> compoundIds, boolean includeId)
			throws InvalidSessionException, DatabaseException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		HttpServletRequest req = this.getThreadLocalRequest();

        /* Check if debugging is activated */
		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		String datasets = Util.joinCollection(datasetIds, ", ", true);

		List<String> names = new ArrayList<>();
		names.add(PhenotypeService.NAME);
		if (includeId)
			names.add("dbId");
		names.add(PhenotypeService.DATASET_NAME);

		DatabaseStatement stmt;

		/*
		 * Create a query that checks if there actually is data available. If
         * not, the prepared statement from the sql file will fail. Connect to
         * the database and check the session id
         */
		Database database = Database.connectAndCheckSession(properties, this);

		/* If both are empty, return everything */
		if (CollectionUtils.isEmpty(groupIds) && CollectionUtils.isEmpty(compoundIds))
		{
			String formatted = String.format(QUERY_COMPOUND_NAMES_COMPLETE, Util.generateSqlPlaceholderString(datasetIds.size()));

			ServerResult<List<String>> temp = new ValueQuery(properties, this, formatted)
					.setLongs(datasetIds)
					.run(PhenotypeService.NAME)
					.getStrings();

			sqlDebug.addAll(temp.getDebugInfo());
			names.addAll(temp.getServerResult());

			stmt = database.prepareStatement(QUERY_DATA_COMPLETE);

			int i = 1;
			stmt.setString(i++, datasets);
		}
		/* If just one is empty, return nothing */
		else if (CollectionUtils.isEmpty(datasetIds, groupIds, compoundIds))
		{
			return new ServerResult<>(sqlDebug, null);
		}
		else
		{
			String groups = Util.joinCollection(groupIds, ", ", true);
			String phenotypes = Util.joinCollection(compoundIds, ", ", true);

			String formatted = String.format(QUERY_COMPOUND_NAMES, Util.generateSqlPlaceholderString(compoundIds.size()));

			ServerResult<List<String>> temp = new ValueQuery(properties, this, formatted)
					.setLongs(compoundIds)
					.run(PhenotypeService.NAME)
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
					stmt = database.prepareStatement(QUERY_DATA);

					int i = 1;
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
		File file = createTemporaryFile("phenotype", FileType.txt.name());
		filePath = file.getName();

		try
		{
			PhenotypeServiceImpl.exportDataToFile("#input=COMPOUND", names, result, file);
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
	public ServerResult<String> getBarChartData(RequestProperties properties, Long compoundId, Long datasetId) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);

		GerminateTableStreamer streamer = new GerminateTableQuery(QUERY_COMPOUND_DATA, null)
				.setLong(compoundId)
				.setLong(datasetId)
				.getStreamer();

		File result = createTemporaryFile("compounds-data", FileType.txt.name());

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
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, jhi.germinate.shared.exception.IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = CompoundDataManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-compounds", FileType.txt.name());

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
