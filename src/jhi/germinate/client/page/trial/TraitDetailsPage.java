/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.client.page.trial;

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
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#compoundId
 */
public class TraitDetailsPage extends Composite
{
	private static CompoundDetailsPageUiBinder ourUiBinder = GWT.create(CompoundDetailsPageUiBinder.class);
	@UiField
	PageHeader    pageHeader;
	@UiField
	FlowPanel     resultsPanel;
	@UiField
	SynonymWidget synonyms;
	@UiField
	SimplePanel   phenotypeDataTablePanel;
	@UiField
	SimplePanel   galleryPanel;
	@UiField
	LinkWidget    linkWidget;
	private Phenotype phenotype;

	public TraitDetailsPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		Long id = LongParameterStore.Inst.get().get(Parameter.phenotypeId);

		if (id != null)
		{
			PhenotypeService.Inst.get().getById(Cookie.getRequestProperties(), id, new DefaultAsyncCallback<ServerResult<Phenotype>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<Phenotype> result)
				{
					phenotype = result.getServerResult();

					if (phenotype != null)
					{
						resultsPanel.setVisible(true);
						pageHeader.setText(Text.LANG.traitDetailsFor(phenotype.getName()));

						synonyms.update(GerminateDatabaseTable.phenotypes, phenotype.getId());

						showPhenotypeDataTable();
						showImages();

						linkWidget.update(GerminateDatabaseTable.phenotypes, phenotype.getId());
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
				ImageService.Inst.get().getForId(Cookie.getRequestProperties(), GerminateDatabaseTable.phenotypes, phenotype.getId(), pagination, callback);
			}
		});
	}

	private void showPhenotypeDataTable()
	{
		final PhenotypeDataTable table = new PhenotypeDataTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
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
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<PhenotypeData>>> callback)
			{
				if (filter == null)
					filter = new PartialSearchQuery();

				if (filter.getColumnNames().size() > 0)
					filter.addLogicalOperator(new And());

				filter.add(new SearchCondition(Phenotype.NAME, new Equal(), phenotype.getName(), String.class));

				return PhenotypeService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		};
		phenotypeDataTablePanel.add(table);
	}

	interface CompoundDetailsPageUiBinder extends UiBinder<HTMLPanel, TraitDetailsPage>
	{
	}
}