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

package jhi.germinate.client.widget.listbox;

import jhi.germinate.shared.datastructure.database.*;

/**
 * GroupListBox extends {@link GerminateValueListBox} and displays {@link Group}s with their size.
 *
 * @author Sebastian Raubach
 */
public class MapListBox extends GerminateValueListBox<Map>
{
	public MapListBox()
	{
		this(false);
	}

	public MapListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<Map>()
		{
			@Override
			protected String getText(Map object)
			{
				return object.getDescription();
			}

			@Override
			protected String getUnit(Map object)
			{
				return (object.getSize() != null && object.getSize() != 0L) ? Long.toString(object.getSize()) : null;
			}

			@Override
			protected BracketType getBracketType()
			{
				return BracketType.ROUND;
			}
		});

		setMultipleSelect(multiSelect);
	}
}
