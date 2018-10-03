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

import java.util.*;

import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public class TraitPage extends Composite
{
	private static CompoundPageUiBinder ourUiBinder = GWT.create(CompoundPageUiBinder.class);
	@UiField
	SimplePanel tablePanel;

	public TraitPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		tablePanel.add(new PhenotypeTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected boolean supportsFiltering()
			{
				return true;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Phenotype>>> callback)
			{
				return PhenotypeService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, filter, callback);
			}
		});
	}

	interface CompoundPageUiBinder extends UiBinder<HTMLPanel, TraitPage>
	{
	}
}