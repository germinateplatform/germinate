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

import jhi.germinate.shared.datastructure.database.*;

/**
 * InstitutionListBox extends {@link GerminateValueListBox} and displays {@link Institution}s with their acronyms.
 *
 * @author Sebastian Raubach
 */
public class InstitutionListBox extends GerminateValueListBox<Institution>
{
	public InstitutionListBox()
	{
		this(false);
	}

	public InstitutionListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<Institution>()
		{
			@Override
			protected String getText(Institution object)
			{
				return object.getName();
			}

			@Override
			protected String getUnit(Institution object)
			{
				return object.getAcronym();
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
