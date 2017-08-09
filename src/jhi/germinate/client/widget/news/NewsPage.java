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

package jhi.germinate.client.widget.news;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.Pager;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class NewsPage extends Composite
{
	private static final int DEFAULT_NEWS_PER_PAGE = 4;

	interface NewsPageUiBinder extends UiBinder<HTMLPanel, NewsPage>
	{
	}

	private static NewsPageUiBinder ourUiBinder = GWT.create(NewsPageUiBinder.class);

	private Long newsId;
	private int perPage = DEFAULT_NEWS_PER_PAGE;

	protected List<News> storedResult;

	@UiField
	FlowPanel news;
	@UiField
	Pager     pager;

	public NewsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		newsId = LongParameterStore.Inst.get().get(Parameter.newsId);

		requestNumberOfNews();
	}

	/**
	 * Request the number of images from the server first
	 */
	private void requestNumberOfNews()
	{
		requestNews(0);
	}

	/**
	 * Request the next chunk of images from the server
	 *
	 * @param start The start position of the new chunk
	 */
	private void requestNews(int start)
	{
		storedResult = null;
		NewsService.Inst.get().get(new Pagination(start, perPage, null, false), new DefaultAsyncCallback<PaginatedServerResult<List<News>>>()
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				if (!(caught instanceof DatabaseException))
					super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(PaginatedServerResult<List<News>> result)
			{
				/*
				 * If there are more news than there is space on one page,
				 * add a pager
				 */
				if (result.getResultSize() > perPage)
				{
					pager.setVisible(true);
					pager.update(perPage, result.getResultSize());
					pager.setPagerClickHandler((button, currentPosition) -> requestNews(currentPosition));
				}
				else
				{
					pager.setVisible(false);
				}

				if (result.getServerResult().size() > 0)
				{
					storedResult = result.getServerResult();
					fillContent();
				}

				if (newsId != null)
					queryForItemPosition();
			}
		});
	}

	protected void queryForItemPosition()
	{
		NewsService.Inst.get().getPosition(Cookie.getRequestProperties(), newsId, new DefaultAsyncCallback<ServerResult<Integer>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				if (!(caught instanceof DatabaseException))
					super.onFailureImpl(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<Integer> result)
			{
				if (pager != null)
				{
					if (result.getServerResult() != null)
					{
						pager.jumpToPosition(result.getServerResult());
					}
					else
					{
						pager.jumpToPosition(0);
					}
				}
				else
				{
					requestNews(0);
				}
			}
		});
		newsId = null;
	}

	private void fillContent()
	{
		if (storedResult == null || storedResult.size() < 1)
			return;

		news.clear();

		if (storedResult != null && storedResult.size() > 0)
			news.add(new NewsPanel(storedResult, false));
		else
			news.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoDataFound()));
	}
}