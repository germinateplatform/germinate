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

package jhi.germinate.client.util.handler;

import java.util.*;

import jhi.germinate.client.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * This {@link DatasetSelectionHandler} handles {@link DatasetSelectionEvent}s, i.e. tracks dataset selections to the server.
 *
 * @author Sebastian Raubach
 */
public class DatasetSelectionHandler implements DatasetSelectionEvent.DatasetSelectionEventHandler
{
	@Override
	public void onDatasetSelected(DatasetSelectionEvent event)
	{
		UnapprovedUser user = null;
		if (ModuleCore.getUseAuthentication())
		{
			UserAuth u = ModuleCore.getUserAuth();
			user = new UnapprovedUser();
			user.id = u.getId();
		}
		else if (GerminateSettingsHolder.get().downloadTrackingEnabled.getValue())
		{
			user = UnapprovedUserParameterStore.Inst.get().get(Parameter.user);
		}

		if (user != null)
		{
			final List<Long> ids = DatabaseObject.getIds(event.getDatasets());
			DatasetService.Inst.get().trackDatasetAccess(Cookie.getRequestProperties(), ids, user, new DefaultAsyncCallback<ServerResult<Boolean>>());
		}
	}
}
