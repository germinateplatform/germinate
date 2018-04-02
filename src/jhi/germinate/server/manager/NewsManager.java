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

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class NewsManager extends AbstractManager<News>
{
	private static final String SELECT_FOR_TYPE = "SELECT news.* FROM news LEFT JOIN newstypes ON news.newstype_id = newstypes.id WHERE newstypes.name IN (%s) %s LIMIT ?, ?";

	@Override
	protected String getTable()
	{
		return "news";
	}

	@Override
	protected DatabaseObjectParser<News> getParser()
	{
		return News.Parser.Inst.get();
	}

	public static PaginatedServerResult<List<News>> getForType(Pagination pagination, NewsType... newsTypes) throws DatabaseException
	{
		pagination.setSortColumn(News.UPDATED_ON);
		pagination.setAscending(false);

		String formatted = String.format(SELECT_FOR_TYPE, StringUtils.generateSqlPlaceholderString(newsTypes.length), pagination.getSortQuery());

		DatabaseObjectQuery<News> query = new DatabaseObjectQuery<>(formatted, null);

		query.setFetchesCount(pagination.getResultSize());

		for (NewsType newsType : newsTypes)
			query.setString(newsType.getName());

		return query.setInt(pagination.getStart())
					.setInt(pagination.getLength())
					.run()
					.getObjectsPaginated(News.Parser.Inst.get());
	}

	public static ServerResult<Integer> getIndex(Long newsId) throws DatabaseException
	{
		NewsType[] newsTypes = {NewsType.general, NewsType.data, NewsType.updates};
		String formatted = String.format(SELECT_FOR_TYPE, StringUtils.generateSqlPlaceholderString(newsTypes.length), "ORDER BY " + News.UPDATED_ON + " DESC");


		ValueQuery query = new ValueQuery(formatted);

		for (NewsType newsType : newsTypes)
			query.setString(newsType.getName());

		query.setInt(0)
			 .setInt(Integer.MAX_VALUE);

		ServerResult<List<Long>> ids = query.run(News.ID)
											.getLongs();

		int index = ids.getServerResult().indexOf(newsId);

		if (index != -1)
			return new ServerResult<>(ids.getDebugInfo(), index);
		else
			return new ServerResult<>(ids.getDebugInfo(), null);
	}
}
