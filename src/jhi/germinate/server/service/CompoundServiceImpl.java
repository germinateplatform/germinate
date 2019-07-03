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
import java.nio.charset.*;
import java.sql.*;
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
 * {@link CompoundServiceImpl} is the implementation of {@link CompoundService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/compound"})
public class CompoundServiceImpl extends BaseRemoteServiceServlet implements CompoundService
{
	private static final long serialVersionUID = 1512922584501563921L;

	private static final String QUERY_COMPOUND_STATS = "SELECT `compounds`.`id`, `compounds`.`name`, `compounds`.`description`, 1 AS isNumeric, `units`.*, COUNT( 1 ) AS count, MIN( cast( `compound_value` AS DECIMAL (30, 2) ) ) AS min, MAX( cast( `compound_value` AS DECIMAL (30, 2) ) ) AS max, AVG( cast( `compound_value` AS DECIMAL (30, 2) ) ) AS avg, STD( cast( `compound_value` AS DECIMAL (30, 2) ) ) AS std, `datasets`.`name` AS datasets_name, `datasets`.`description` AS datasets_description FROM `datasets` LEFT JOIN `compounddata` ON `datasets`.`id` = `compounddata`.`dataset_id` LEFT JOIN `compounds` ON `compounds`.`id` = `compounddata`.`compound_id` LEFT JOIN `units` ON `units`.`id` = `compounds`.`unit_id` LEFT JOIN `experiments` ON `experiments`.`id` = `datasets`.`experiment_id` LEFT JOIN `experimenttypes` ON `experimenttypes`.`id` = `experiments`.`experiment_type_id` WHERE `experimenttypes`.`description` = 'compound' AND `datasets`.`id` IN (%s) GROUP BY `compounds`.`id`, `datasets`.`id`";
	private static final String QUERY_DATA = "call " + StoredProcedureInitializer.COMPOUND_DATA + "(?, ?, ?, ?)";

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
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);
		String formatted = String.format(QUERY_COMPOUND_STATS, StringUtils.generateSqlPlaceholderString(datasetIds.size()));

		return new DatabaseObjectQuery<DataStats>(formatted, userAuth)
				.setLongs(datasetIds)
				.run()
				.getObjects(DataStats.Parser.Inst.get(), true);
	}

	@Override
	public ServerResult<String> getExportFile(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, Set<String> markedAccessionIds, List<Long> compoundIds) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		if (CollectionUtils.isEmpty(datasetIds))
			return new ServerResult<>();

		// Check if debugging is activated
		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		// If the all items group is selected, export everything
		if (containsAllItemsGroup(groupIds))
			groupIds = null;

		DefaultQuery stmt = new DefaultQuery(QUERY_DATA, userAuth);

		if (CollectionUtils.isEmpty(groupIds))
			stmt.setNull(Types.VARCHAR);
		else
			stmt.setString(Util.joinCollection(groupIds, ", ", true));

		if (CollectionUtils.isEmpty(markedAccessionIds))
			stmt.setNull(Types.VARCHAR);
		else
			stmt.setString(Util.joinCollection(markedAccessionIds, ", ", true));

		if (CollectionUtils.isEmpty(datasetIds))
			stmt.setNull(Types.VARCHAR);
		else
			stmt.setString(Util.joinCollection(datasetIds, ", ", true));

		if (CollectionUtils.isEmpty(compoundIds))
			stmt.setNull(Types.VARCHAR);
		else
			stmt.setString(Util.joinCollection(compoundIds, ", ", true));

		DefaultStreamer result;

		try
		{
			result = stmt.getStreamer();

			if (!result.hasData())
				return new ServerResult<>(sqlDebug, null);
		}
		catch (DatabaseException e)
		{
			return new ServerResult<>(sqlDebug, null);
		}

		sqlDebug.add(stmt.getStringRepresentation());

		String filePath;

		// Export the data to a temporary file
		File file = createTemporaryFile("compound", datasetIds, FileType.txt.name());
		filePath = file.getName();

		try
		{
			PhenotypeServiceImpl.exportDataToFile("#input=COMPOUND", result.getColumnNames(), result, file);
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
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, jhi.germinate.shared.exception.IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DefaultStreamer streamer = CompoundDataManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-compounds", FileType.txt.name());

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
	public ServerResult<String> getHistogramData(RequestProperties properties, Long compoundId, Long datasetId) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		File file = createTemporaryFile("compound-histogram", datasetId, FileType.txt.name());

		try (DefaultStreamer streamer = CompoundManager.getStreamerForHistogramData(userAuth, compoundId, datasetId);
			 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (streamer != null)
			{
				bw.println("value");

				DatabaseResult rs;

				while ((rs = streamer.next()) != null)
				{
					bw.println(rs.getDouble("compound_value"));
				}

				return new ServerResult<>(streamer.getDebugInfo(), file.getName());
			}
			else
			{
				return new ServerResult<>(null, null);
			}
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e.getLocalizedMessage());
		}
	}
}
