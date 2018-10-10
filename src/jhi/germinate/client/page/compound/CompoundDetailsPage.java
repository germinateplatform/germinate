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

package jhi.germinate.client.page.compound;

import com.google.gwt.core.client.*;
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
import jhi.germinate.client.widget.gallery.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#compoundId
 */
public class CompoundDetailsPage extends Composite
{
	interface CompoundDetailsPageUiBinder extends UiBinder<HTMLPanel, CompoundDetailsPage>
	{
	}

	private static CompoundDetailsPageUiBinder ourUiBinder = GWT.create(CompoundDetailsPageUiBinder.class);

	private Compound compound;

	@UiField
	PageHeader    pageHeader;
	@UiField
	FlowPanel     resultsPanel;
	@UiField
	SynonymWidget synonyms;
	@UiField
	SimplePanel   compoundDataTablePanel;
	@UiField
	SimplePanel   galleryPanel;
	@UiField
	LinkWidget    linkWidget;

	public CompoundDetailsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long id = LongParameterStore.Inst.get().get(Parameter.compoundId);

		if (id != null)
		{
			CompoundService.Inst.get().getById(Cookie.getRequestProperties(), id, new DefaultAsyncCallback<ServerResult<Compound>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Compound> result)
				{
					compound = result.getServerResult();

					if (compound != null)
					{
						resultsPanel.setVisible(true);
						pageHeader.setText(Text.LANG.compoundDetailsFor(compound.getName()));

						synonyms.update(GerminateDatabaseTable.compounds, compound.getId());

						showCompoundDataTable();
						showImages();

						linkWidget.update(GerminateDatabaseTable.compounds, compound.getId());
					}
					else
					{
						pageHeader.setText(Text.LANG.notificationNoDataFound());
					}
				}
			});
		}
		else
		{
			pageHeader.setText(Text.LANG.notificationNoDataFound());
		}
	}

	private void showImages()
	{
		galleryPanel.add(new Gallery(false, false)
		{
			@Override
			protected void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback)
			{
				ImageService.Inst.get().getForId(Cookie.getRequestProperties(), GerminateDatabaseTable.compounds, compound.getId(), pagination, callback);
			}
		});
	}

	private void showCompoundDataTable()
	{
		final CompoundDataTable table = new CompoundDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			public boolean supportsFullIdMarking()
			{
				return true;
			}

			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
				CompoundService.Inst.get().getIdsForFilter(Cookie.getRequestProperties(), filter, callback);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<CompoundData>>> callback)
			{
				if (filter == null)
					filter = new PartialSearchQuery();

				if (filter.getColumnNames().size() > 0)
					filter.addLogicalOperator(new And());

				filter.add(new SearchCondition(Compound.NAME, new Equal(), compound.getName(), String.class));

				return CompoundService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};
		compoundDataTablePanel.add(table);
	}
}