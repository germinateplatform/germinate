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

package jhi.germinate.client.page.geography;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public class AccessionsAtCollsitePage extends Composite implements HasHyperlinkButton
{
	interface AccessionsAtCollsitePageUiBinder extends UiBinder<HTMLPanel, AccessionsAtCollsitePage>
	{
	}

	private static AccessionsAtCollsitePageUiBinder ourUiBinder = GWT.create(AccessionsAtCollsitePageUiBinder.class);

	@UiField
	PageHeader    header;
	@UiField
	SimplePanel   accessionTablePanel;
	@UiField
	CommentWidget commentWidget;
	@UiField
	SimplePanel   downloadPanel;

	private Long collsiteId;

	public AccessionsAtCollsitePage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		collsiteId = LongParameterStore.Inst.get().get(Parameter.collectingsiteId);

		if (collsiteId == null)
		{
			header.setText(Text.LANG.notificationGeographyNoCollsiteSelected());
		}
		else
		{
			PartialSearchQuery filter = new PartialSearchQuery(new SearchCondition(Location.ID, new Equal(), Long.toString(collsiteId), Long.class));
			LocationService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, Pagination.getDefault(), new DefaultAsyncCallback<PaginatedServerResult<List<Location>>>()
			{
				@Override
				protected void onSuccessImpl(PaginatedServerResult<List<Location>> result)
				{
					if (result.hasData())
					{
						String collsiteName = result.getServerResult().get(0).getName();
						header.setText(Text.LANG.geographyTitle(HTMLUtils.stripHtmlTags(collsiteName)));
					}
				}
			});

			addTable();
			commentWidget.update(collsiteId, GerminateDatabaseTable.locations);
			addDownloadWidget();
		}
	}

	private void addDownloadWidget()
	{
		DownloadWidget widget = new DownloadWidget()
		{
			@Override
			protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
			{
				LocationService.Inst.get().exportToKml(Cookie.getRequestProperties(), KmlType.collectingsite, collsiteId, callback);
			}
		};
		widget.add(new DownloadWidget.FileConfig(Text.LANG.downloadGoogleEarth()).setType(FileType.kmz));
		downloadPanel.add(widget);
	}


	private void addTable()
	{
		accessionTablePanel.add(new AccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				filter = addToFilter(filter);

				AccessionService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Accession>>> callback)
			{
				filter = addToFilter(filter);

				return AccessionService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, new AsyncCallback<PaginatedServerResult<List<Accession>>>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						downloadPanel.setVisible(false);
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(PaginatedServerResult<List<Accession>> result)
					{
						if (result.getResultSize() != null && result.getResultSize() < 1)
							downloadPanel.setVisible(false);

						callback.onSuccess(result);
					}
				});
			}

			private PartialSearchQuery addToFilter(PartialSearchQuery filter)
			{
				if (filter == null)
					filter = new PartialSearchQuery();
				SearchCondition condition = new SearchCondition(Location.ID, new Equal(), Long.toString(collsiteId), Long.class);
				filter.add(condition);

				if (filter.getAll().size() > 1)
					filter.addLogicalOperator(new And());

				return filter;
			}

			@Override
			protected boolean supportsFiltering()
			{
				return false;
			}
		});
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.ACCESSIONS_FOR_COLLSITE)
				.addParam(Parameter.collectingsiteId);
	}
}