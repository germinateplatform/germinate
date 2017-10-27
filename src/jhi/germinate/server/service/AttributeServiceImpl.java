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
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;
import jhi.germinate.shared.search.*;

/**
 * {@link AttributeServiceImpl} is the implementation of {@link AccessionService}.
 *
 * @author Sebastian Raubach
 * @author Gordon Stephen
 */
@WebServlet(urlPatterns = {"/germinate/attribute"})
public class AttributeServiceImpl extends BaseRemoteServiceServlet implements AttributeService
{
	@Override
	public PaginatedServerResult<List<AttributeData>> getForFilter(RequestProperties properties, Pagination pagination, GerminateDatabaseTable target, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		switch (target)
		{
			case germinatebase:
				return AttributeDataManager.getAllForAccessionFilter(userAuth, filter, pagination);
			case datasets:
				return AttributeDataManager.getAllForDatasetFilter(userAuth, filter, pagination, true);
			default:
				return new PaginatedServerResult<>(null, null, 0);
		}
	}

	@Override
	public ServerResult<List<String>> getIdsForFilter(RequestProperties properties, GerminateDatabaseTable target, PartialSearchQuery filter) throws InvalidSessionException,
			DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		switch (target)
		{
			case germinatebase:
				return AttributeDataManager.getIdsForAccessionFilter(userAuth, filter);
			default:
				return new PaginatedServerResult<>(null, null, 0);
		}
	}

	@Override
	public ServerResult<String> export(RequestProperties properties, GerminateDatabaseTable target, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		GerminateTableStreamer streamer = null;

		switch (target)
		{
			case germinatebase:
				streamer = AttributeDataManager.getStreamerForAccessionFilter(userAuth, filter, Pagination.getDefault());
				break;
			case datasets:
				streamer = AttributeDataManager.getStreamerForDatasetFilter(userAuth, filter, Pagination.getDefault());
				break;
			default:
		}

		if (streamer != null)
		{
			File result = createTemporaryFile("download-attributes", FileType.txt.name());

			try
			{
				Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, result);
			}
			catch (java.io.IOException e)
			{
				throw new IOException(e);
			}

			streamer.close();

			return new ServerResult<>(streamer.getDebugInfo(), result.getName());
		}
		else
			return new ServerResult<>(null, null);
	}
}
