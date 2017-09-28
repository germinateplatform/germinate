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

package jhi.germinate.client.page.search;

import com.google.gwt.user.client.rpc.*;

import jhi.germinate.shared.datastructure.*;

public class SearchCallback<T> implements AsyncCallback<PaginatedServerResult<T>>
{
	private SearchSection                           section;
	private AsyncCallback<PaginatedServerResult<T>> callback;

	public SearchCallback(SearchSection section, AsyncCallback<PaginatedServerResult<T>> callback)
	{
		this.section = section;
		this.callback = callback;

		section.setVisible(true);
		section.setLoading(true);
	}

	@Override
	public void onFailure(Throwable caught)
	{
		section.setLoading(false);
		section.setLabel(null);

		section.setVisible(false);

		callback.onFailure(caught);
	}

	@Override
	public void onSuccess(PaginatedServerResult<T> result)
	{
		section.setLoading(false);
		section.setLabel(result.getResultSize());

		section.setVisible(true);

		callback.onSuccess(result);
	}
}