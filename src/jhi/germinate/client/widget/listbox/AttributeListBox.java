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

package jhi.germinate.client.widget.listbox;

import jhi.germinate.shared.datastructure.database.*;

/**
 * ClimateListBox extends {@link GerminateValueListBox} and displays {@link Climate}s with their {@link Unit}s.
 *
 * @author Sebastian Raubach
 */
public class AttributeListBox extends GerminateValueListBox<Attribute>
{
	public AttributeListBox()
	{
		this(true);
	}

	public AttributeListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<Attribute>()
		{
			@Override
			protected String getText(Attribute object)
			{
				return object.getName();
			}

			@Override
			protected String getUnit(Attribute object)
			{
				return null;
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
