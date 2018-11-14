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

package jhi.germinate.client.widget.element;

import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.widget.news.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class NewsWidget extends Composite
{
	private       Column projectColumn;
	private final Column newsColumn;

	public NewsWidget()
	{
		Row row = new Row();
		row.setMarginTop(15);
		newsColumn = new Column(ColumnSize.XS_12, ColumnSize.MD_6);
		newsColumn.setId("news-" + RandomUtils.RANDOM.nextLong());
		row.add(newsColumn);
		initWidget(row);

		NewsService.Inst.get().getProjects(new Pagination(0, 2, null, false), new DefaultAsyncCallback<ServerResult<List<News>>>()
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				if (caught instanceof DatabaseException)
					newsColumn.setSize(ColumnSize.XS_12);
				else
					super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(ServerResult<List<News>> result)
			{
				if (result.hasData())
				{
					projectColumn = new Column(ColumnSize.XS_12, ColumnSize.MD_3);
					projectColumn.addStyleName(Style.COL_XXS_12);
					ProjectItem first = new ProjectItem(result.getServerResult().get(0));
					projectColumn.add(first);

					row.add(projectColumn);

					if (result.getServerResult().size() > 1)
					{
						projectColumn.setSize(ColumnSize.XS_6, ColumnSize.MD_3);
						projectColumn = new Column(ColumnSize.XS_6, ColumnSize.MD_3);
						projectColumn.addStyleName(Style.COL_XXS_12);
						ProjectItem second = new ProjectItem(result.getServerResult().get(1));

						projectColumn.add(second);

						row.add(projectColumn);
					}
					else
					{
						newsColumn.setSize(ColumnSize.XS_12, ColumnSize.MD_9);
					}

					projectColumn.setId("project-" + RandomUtils.RANDOM.nextLong());
				}
				/* If there are no projects, adjust the columns */
				else
				{
					newsColumn.setSize(ColumnSize.XS_12);
				}
			}
		});

		NewsService.Inst.get().get(new Pagination(0, 6, null, false), new DefaultAsyncCallback<PaginatedServerResult<List<News>>>()
		{
			@Override
			public void onFailureImpl(Throwable caught)
			{
				if (caught instanceof DatabaseException)
				{
					newsColumn.add(new NewsPanel(null, true));
				}
				else
					super.onFailureImpl(caught);
			}

			@Override
			public void onSuccessImpl(PaginatedServerResult<List<News>> result)
			{
				newsColumn.add(new NewsPanel(result.getServerResult(), true));
			}
		});
	}

	public String getNewsColumnId()
	{
		return newsColumn.getId();
	}

	public String getProjectColumnId()
	{
		if (projectColumn != null)
			return projectColumn.getId();
		else
			return null;
	}
}
