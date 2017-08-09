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
 * ClimateListBox extends {@link GerminateValueListBox} and displays {@link Climate}s with their {@link Unit}s.
 *
 * @author Sebastian Raubach
 */
public class ClimateListBox extends GerminateValueListBox<Climate>
{
	public ClimateListBox()
	{
		this(false);
	}

	public ClimateListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<Climate>()
		{
			@Override
			protected String getText(Climate object)
			{
				return object.getName();
			}

			@Override
			protected String getUnit(Climate object)
			{
				return object.getUnit() != null ? object.getUnit().getAbbreviation() : null;
			}

			@Override
			protected BracketType getBracketType()
			{
				return BracketType.SQUARE;
			}
		});

		setMultipleSelect(multiSelect);
	}
}
