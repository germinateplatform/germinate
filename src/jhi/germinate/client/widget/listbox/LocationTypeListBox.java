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

package jhi.germinate.client.widget.listbox;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;

/**
 * LocationTypeListBox LocationTypeListBox {@link GerminateValueListBox} and displays {@link Location}s.
 *
 * @author Sebastian Raubach
 */
public class LocationTypeListBox extends GerminateValueListBox<LocationType>
{
	public LocationTypeListBox()
	{
		this(true, false);
	}

	public LocationTypeListBox(boolean includeAll, boolean multiSelect)
	{
		super(new GerminateUnitRenderer<LocationType>()
		{
			@Override
			protected String getText(LocationType object)
			{
				return object.getName();
			}

			@Override
			protected String getUnit(LocationType object)
			{
				return null;
			}

			@Override
			protected BracketType getBracketType()
			{
				return BracketType.SQUARE;
			}
		});

		setMultipleSelect(multiSelect);

		List<LocationType> types = new ArrayList<>(Arrays.asList(LocationType.values()));

		if (!includeAll)
		{
			types.remove(LocationType.all);
		}

		setValue(types.get(0), false);
		setAcceptableValues(types);
	}
}
