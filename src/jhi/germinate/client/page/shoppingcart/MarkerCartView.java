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

package jhi.germinate.client.page.shoppingcart;

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.*;

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
public class MarkerCartView extends AbstractCartView<Marker>
{
	@Override
	protected ShoppingCart.ItemType getItemType()
	{
		return ShoppingCart.ItemType.MARKER;
	}

	@Override
	protected void writeToFile(List<String> markedIds, AsyncCallback<ServerResult<String>> callback)
	{
		MarkerService.Inst.get().export(Cookie.getRequestProperties(), markedIds, callback);
	}

	@Override
	protected MarkerTable getTable(final List<String> markedIds)
	{
		return new MarkerTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
			{
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Marker>>> callback)
			{
				return MarkerService.Inst.get().getByIds(Cookie.getRequestProperties(), pagination, markedIds, new AsyncCallback<ServerResult<List<Marker>>>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(ServerResult<List<Marker>> result)
					{
						callback.onSuccess(new PaginatedServerResult<>(result.getDebugInfo(), result.getServerResult(), markedIds.size()));
					}
				});
			}
		};
	}
}
