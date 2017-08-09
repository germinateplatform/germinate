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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.view.client.*;

/**
 * RefreshableAsyncDataProvider extends {@link AsyncDataProvider} and adds the method {@link #refresh(HasData)} which will re-fetch the data from the
 * server.
 *
 * @author Sebastian Raubach
 */
public abstract class RefreshableAsyncDataProvider<T> extends AsyncDataProvider<T>
{
	public void refresh(HasData<T> table)
	{
		onRangeChanged(table);
	}
}
