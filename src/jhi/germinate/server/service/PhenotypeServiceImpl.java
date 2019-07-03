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
 * {@link PhenotypeServiceImpl} is the implementation of {@link PhenotypeService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/phenotype"})
public class PhenotypeServiceImpl extends BaseRemoteServiceServlet implements PhenotypeService
{
	private static final long serialVersionUID = -4657496352502981361L;

	private static final String QUERY_DATA            = "call " + StoredProcedureInitializer.PHENOTYPE_DATA + "(?, ?, ?, ?)";
	private static final String QUERY_PHENOTYPE_STATS = "SELECT `phenotypes`.`id`, `phenotypes`.`name`, `phenotypes`.`description`, (`phenotypes`.`datatype` != 'char') AS isNumeric, `units`.*, COUNT( 1 ) AS count, MIN(cast(`phenotype_value` AS DECIMAL(30,2))) as min, MAX(cast(`phenotype_value` AS DECIMAL(30,2))) as max, AVG(cast(`phenotype_value` AS DECIMAL(30,2))) as avg, STD(cast(`phenotype_value` AS DECIMAL(30,2))) as std, `datasets`.`name` AS datasets_name, `datasets`.`description` as datasets_description FROM `datasets` LEFT JOIN `phenotypedata` ON `datasets`.`id` = `phenotypedata`.`dataset_id` LEFT JOIN `phenotypes` ON `phenotypes`.`id` = `phenotypedata`.`phenotype_id` LEFT JOIN `units` ON `units`.`id` = `phenotypes`.`unit_id` LEFT JOIN `experiments` ON `experiments`.`id` = `datasets`.`experiment_id` LEFT JOIN `experimenttypes` ON `experimenttypes`.`id` = `experiments`.`experiment_type_id` WHERE `datasets`.`id` IN (%s) GROUP BY `phenotypes`.`id`, `datasets`.`id`";

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
	public PaginatedServerResult<List<Phenotype>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return PhenotypeManager.getAllForFilter(userAuth, pagination, filter);
	}

	@Override
	public ServerResult<List<DataStats>> getOverviewStats(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);
		String formatted = String.format(QUERY_PHENOTYPE_STATS, StringUtils.generateSqlPlaceholderString(datasetIds.size()));

		return new DatabaseObjectQuery<DataStats>(formatted, userAuth)
				.setLongs(datasetIds)
				.run()
				.getObjects(DataStats.Parser.Inst.get(), true);
	}

	public static void exportDataToFile(String header, String[] phenotypes, DefaultStreamer result, File file) throws java.io.IOException, DatabaseException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
			 DefaultStreamer streamer = result)
		{
			if (!StringUtils.isEmpty(header))
			{
				bw.write(header);
				bw.newLine();
			}
			bw.write(phenotypes[0]);

			for (int i = 1; i < phenotypes.length; i++)
			{
				bw.write("\t" + phenotypes[i]);
			}

			bw.newLine();

			DatabaseResult rs;
			while ((rs = streamer.next()) != null)
			{
				String zero = rs.getString(phenotypes[0]);
				String value;
				bw.write(zero == null ? "" : zero);

				for (int i = 1; i < phenotypes.length; i++)
				{
					String ii = rs.getString(phenotypes[i]);
					value = ii == null ? "" : ii;
					bw.write("\t" + value);
				}

				bw.newLine();
			}
		}
	}

	@Override
	public PaginatedServerResult<List<PhenotypeData>> getDataForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return PhenotypeManager.getDataForFilter(userAuth, filter, pagination);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, jhi.germinate.shared.exception.IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DefaultStreamer streamer = PhenotypeManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE));

		File result = createTemporaryFile("download-phenotypes", FileType.txt.name());

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
	public ServerResult<String> getHistogramData(RequestProperties properties, Long phenotypeId, Long datasetId) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		File file = createTemporaryFile("phenotype-histogram", datasetId, FileType.txt.name());

		try (DefaultStreamer streamer = PhenotypeManager.getStreamerForHistogramData(userAuth, phenotypeId, datasetId);
			 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
		{
			if (streamer != null)
			{
				bw.println("value");

				DatabaseResult rs;

				while ((rs = streamer.next()) != null)
					bw.println(rs.getDouble("phenotype_value"));

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

	@Override
	public ServerResult<String> export(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, Set<String> markedAccessionIds, List<Long> phenotypeIds) throws InvalidSessionException, DatabaseException
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

		if (CollectionUtils.isEmpty(phenotypeIds))
			stmt.setNull(Types.VARCHAR);
		else
			stmt.setString(Util.joinCollection(phenotypeIds, ", ", true));

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
		File file = createTemporaryFile("phenotype", datasetIds, FileType.txt.name());
		filePath = file.getName();

		try
		{
			exportDataToFile("#input=PHENOTYPE", result.getColumnNames(), result, file);
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
