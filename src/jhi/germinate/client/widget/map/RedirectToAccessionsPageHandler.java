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

package jhi.germinate.client.widget.map;

import com.google.gwt.user.client.*;

import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link RedirectToAccessionsPageHandler} is an implementation of {@link LeafletUtils.OnMarkerClickHandler} which will redirect the user to {@link
 * Page#ACCESSIONS_FOR_COLLSITE}
 *
 * @author Sebastian Raubach
 */
public class RedirectToAccessionsPageHandler implements LeafletUtils.OnMarkerClickHandler
{
	@Override
	public void onMarkerClicked(String id, String name)
	{
		if (!StringUtils.isEmpty(id))
		{
			try
			{
				LongParameterStore.Inst.get().putAsString(Parameter.collectingsiteId, id);
				History.newItem(Page.ACCESSIONS_FOR_COLLSITE.name());
			}
			catch (UnsupportedDataTypeException e)
			{
			}
		}
	}
}