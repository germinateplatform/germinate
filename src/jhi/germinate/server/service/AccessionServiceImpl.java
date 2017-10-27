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
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

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
 * {@link AccessionServiceImpl} is the implementation of {@link AccessionService}.
 *
 * @author Sebastian Raubach
 * @author Gordon Stephen
 */
@WebServlet(urlPatterns = {"/germinate/accession"})
public class AccessionServiceImpl extends BaseRemoteServiceServlet implements AccessionService
{
	private static final long serialVersionUID = -8616536189132171506L;

	static final String GROUP_PREVIEW_LIST     = "group-preview-list";
	static final String GROUP_PREVIEW_FILENAME = "group-preview-filename";

	@Override
	public ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		try (GerminateTableStreamer streamer = AccessionManager.getStreamerForFilter(userAuth, filter, new Pagination(0, Integer.MAX_VALUE)))
		{
			File result = createTemporaryFile("download-accessions", FileType.txt.name());

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

	@Override
	public PaginatedServerResult<List<Accession>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getAllForFilter(userAuth, filter, pagination);
	}

	@Override
	public ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getIdsForFilter(userAuth, filter);
	}

	@Override
	public ServerResult<List<Accession>> getByIds(RequestProperties properties, Pagination pagination, List<String> ids) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getByIds(userAuth, ids, pagination);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, String idColumn, Set<String> accessionIds, boolean includeAttributes) throws InvalidSessionException, DatabaseException, IOException, InsufficientPermissionsException
	{
		/* Check if the column contains any malicious sql */
		if (!SearchCondition.checkSqlString(idColumn, false))
			throw new DatabaseException("\"" + idColumn + "\" is not a valid column.");

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		String idString = Util.joinCollection(accessionIds, ",", true);

		GerminateTableQuery query = new GerminateTableQuery("call " + StoredProcedureInitializer.GERMINATEBASE_ATTRIBUTE_ACCESSION_DATA + "(?, ?)", userAuth, null)
				.setBoolean(includeAttributes)
				.setString(idString);

		return exportData(query, idColumn, sqlDebug);
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, String idColumn, Long groupId, boolean includeAttributes) throws InvalidSessionException, DatabaseException, IOException, InsufficientPermissionsException
	{
		/* Check if the column contains any malicious sql */
		if (!SearchCondition.checkSqlString(idColumn, false))
			throw new DatabaseException("'" + idColumn + "' is not a valid column.");

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DebugInfo sqlDebug = DebugInfo.create(userAuth);

		GerminateTableQuery query;

		if (groupId != null && groupId != -1)
		{
			Group group = new GroupManager().getById(userAuth, groupId).getServerResult();

			if (userAuth.isAdmin() || group.getVisibility() || (group.getCreatedBy() != null && Objects.equals(group.getCreatedBy(), properties.getUserId())))
			{
				/* If we get here, the user either has permissions to edit the group
			 	 * or the group is public */
				query = new GerminateTableQuery("call " + StoredProcedureInitializer.GERMINATEBASE_ATTRIBUTE_GROUP_DATA + "(?, ?)", userAuth, null)
						.setBoolean(includeAttributes)
						.setLong(groupId);
			}
			else
			{
				throw new InsufficientPermissionsException();
			}

		}
		else
		{
			query = new GerminateTableQuery("call " + StoredProcedureInitializer.GERMINATEBASE_ATTRIBUTE_DATA + "(?)", userAuth, null)
					.setBoolean(includeAttributes);
		}

		return exportData(query, idColumn, sqlDebug);
	}

	private ServerResult<String> exportData(GerminateTableQuery query, String idColumn, DebugInfo sqlDebug) throws DatabaseException, IOException
	{
		/* Create the result file */
		File file = createTemporaryFile("germinatebase", FileType.txt.name());

		try (GerminateTableStreamer streamer = query.getStreamer();
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8")))
		{
			String[] columnNames = streamer.getColumnNames();

			if (ArrayUtils.isEmpty(columnNames))
				throw new DatabaseException("No columns selected");

			bw.write(idColumn);

			if (!ArrayUtils.isEmpty(columnNames))
				bw.write("\t");
			/* Write the table headers */
			bw.write(Arrays.stream(columnNames)
						   .filter(s -> !Objects.equals(s, idColumn))
						   .collect(Collectors.joining("\t")));

			bw.newLine();

            /* Write the actual data */
			GerminateRow row;
			String cellValue;
			while ((row = streamer.next()) != null)
			{
				/* Write the id column separately */
				cellValue = row.get(idColumn);
				if (StringUtils.isEmpty(cellValue))
					bw.write("");
				else
					bw.write(cellValue);

				for (String columnName : columnNames)
				{
					/* Skip the id column, as we've already written it */
					if (columnName.equals(idColumn))
						continue;

					cellValue = row.get(columnName);
					if (StringUtils.isEmpty(cellValue))
						bw.write("\t");
					else
						bw.write("\t" + cellValue);
				}

				bw.newLine();
			}

			sqlDebug.addAll(streamer.getDebugInfo());
		}
		catch (java.io.IOException e)
		{
			throw new jhi.germinate.shared.exception.IOException(e);
		}

		return new ServerResult<>(sqlDebug, file.getName());

	}

	@Override
	public PaginatedServerResult<List<Accession>> getForMegaEnv(RequestProperties properties, Long megaEnvId, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getAllForMegaEnv(userAuth, megaEnvId, pagination);
	}

	@Override
	public ServerResult<List<String>> getIdsForMegaEnv(RequestProperties properties, Long megaEnvId) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getIdsForMegaEnv(userAuth, megaEnvId);
	}

	@Override
	public ServerResult<String> exportForIds(RequestProperties properties, List<String> ids) throws InvalidSessionException, DatabaseException, IOException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		try (GerminateTableStreamer streamer = AccessionManager.getStreamerForIds(userAuth, ids))
		{
			File output = createTemporaryFile("export_accession_group", FileType.txt.name());

			try
			{
				Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, output);
			}
			catch (java.io.IOException e)
			{
				throw new jhi.germinate.shared.exception.IOException(e);
			}

			return new ServerResult<>(streamer.getDebugInfo(), output.getName());
		}
	}

	@SuppressWarnings("unchecked")
	public ServerResult<Integer> getGroupPreviewSize(UserAuth userAuth, String filename, Pagination pagination) throws DatabaseException, IOException
	{
		if (pagination.getResultSize() != null)
			return new ServerResult<>(DebugInfo.create(userAuth), pagination.getResultSize());
		else
		{
			try
			{
				/* Check if there already is a filename stored in the session */
				String sessionFilename = (String) getThreadLocalRequest().getSession().getAttribute(GROUP_PREVIEW_FILENAME);
				if (StringUtils.areEqual(filename, sessionFilename))
				{
					/* Get it and return the size */
					List<Accession> foundAccessions = (List<Accession>) getThreadLocalRequest().getSession().getAttribute(GROUP_PREVIEW_LIST);
					return new ServerResult<>(null, foundAccessions.size());
				}
				else
				{
					/* If not, readAll the new file */
					List<String> accessions = Files.readAllLines(getFile(FileLocation.temporary, filename).toPath(), StandardCharsets.UTF_8);
					List<Accession> foundAccessions = new ArrayList<>();

					/* Loop over each row */
					for (String accession : accessions)
					{
						/* Get matching accessions */
						List<Accession> matchingItems = AccessionManager.getByUnknownIdentifier(null, accession).getServerResult();

						if (matchingItems != null)
							foundAccessions.addAll(matchingItems);
					}

					/* Stash the list of ids in the session object */
					getThreadLocalRequest().getSession().setAttribute(GROUP_PREVIEW_LIST, foundAccessions);
					/* Stash the filename associated with the session and the above list of ids */
					getThreadLocalRequest().getSession().setAttribute(GROUP_PREVIEW_FILENAME, filename);

					return new ServerResult<>(null, foundAccessions.size());
				}
			}
			catch (java.io.IOException e)
			{
				throw new jhi.germinate.shared.exception.IOException(e);
			}
		}
	}

	@Override
	public PaginatedServerResult<List<Accession>> getForGroupPreview(RequestProperties properties, Pagination pagination, String filename) throws InvalidSessionException, DatabaseException, IOException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		/* Get the list of accessions from the session */
		ServerResult<Integer> nrOfItems = getGroupPreviewSize(userAuth, filename, pagination);
		@SuppressWarnings("unchecked")
		List<Accession> foundAccessions = (List<Accession>) getThreadLocalRequest().getSession().getAttribute(GROUP_PREVIEW_LIST);

		if (foundAccessions == null)
			return new PaginatedServerResult<>(null, null, nrOfItems);

		/* Subset the list based on the pagination information */
		List<Accession> finalAccessions = new ArrayList<>(foundAccessions.subList(pagination.getStart(), Math.min(pagination.getStart() + pagination.getLength(), foundAccessions.size())));

		return new PaginatedServerResult<>(null, finalAccessions, nrOfItems);
	}

	@Override
	public void removeFromGroupPreview(RequestProperties properties, List<Long> ids, String filename) throws InvalidSessionException
	{
		String oldFileName = (String) getThreadLocalRequest().getSession().getAttribute(GROUP_PREVIEW_FILENAME);

		if (StringUtils.areEqual(oldFileName, filename))
		{
			/* Get the list of accessions from the session */
			@SuppressWarnings("unchecked")
			List<Accession> foundAccessions = (List<Accession>) getThreadLocalRequest().getSession().getAttribute(GROUP_PREVIEW_LIST);

			if (foundAccessions == null)
				return;

			/* Iterate over them */
			Iterator<Accession> it = foundAccessions.iterator();
			while (it.hasNext())
			{
				Long id = it.next().getId();
				if (ids.contains(id))
				{
				/* Remove the ones in question */
					it.remove();
				}
			}
		}
	}

	@Override
	public void clearGroupPreview(RequestProperties properties, String filename) throws InvalidSessionException
	{
		String oldFileName = (String) getThreadLocalRequest().getSession().getAttribute(GROUP_PREVIEW_FILENAME);

		if (StringUtils.areEqual(oldFileName, filename))
		{
			getThreadLocalRequest().getSession().setAttribute(GROUP_PREVIEW_LIST, null);
			getThreadLocalRequest().getSession().setAttribute(GROUP_PREVIEW_FILENAME, null);
		}
	}

	@Override
	public PaginatedServerResult<List<Accession>> getByDistance(RequestProperties properties, Double latitude, Double longitude, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		if (StringUtils.isEmpty(pagination.getSortColumn()))
			pagination.setSortColumn(LocationService.DISTANCE);

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getAllSortedByDistance(userAuth, latitude, longitude, pagination);
	}

	@Override
	public PaginatedServerResult<List<Accession>> getInPolygon(RequestProperties properties, Pagination pagination, List<LatLngPoint> bounds) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getAllInPolygon(userAuth, bounds, pagination);
	}

	@Override
	public ServerResult<List<String>> getIdsInPolygon(RequestProperties properties, List<LatLngPoint> polygon) throws InvalidSessionException, DatabaseException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getIdsInPolygon(userAuth, polygon);
	}

	@Override
	public ServerResult<Mcpd> getMcpd(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		return AccessionManager.getMcpd(userAuth, id);
	}
}
