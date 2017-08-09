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

import java.util.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link NewsServiceImpl} is the implementation of {@link NewsService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/news"})
public class NewsServiceImpl extends BaseRemoteServiceServlet implements NewsService
{
	private static final long serialVersionUID = -3982988282160791242L;

	@Override
	public PaginatedServerResult<List<News>> get(Pagination pagination) throws DatabaseException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		return NewsManager.getForType(pagination, NewsType.data, NewsType.general, NewsType.updates);
	}

	@Override
	public ServerResult<List<News>> getProjects(Pagination pagination) throws DatabaseException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		return NewsManager.getForType(pagination, NewsType.projects);
	}

	@Override
	public ServerResult<Integer> getPosition(RequestProperties properties, Long newsId) throws DatabaseException
	{
		return NewsManager.getIndex(newsId);

	}
}
