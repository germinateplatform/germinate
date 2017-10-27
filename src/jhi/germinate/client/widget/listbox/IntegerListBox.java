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

/**
 * StringListBox extends {@link GerminateValueListBox} and displays simple {@link String}s.
 *
 * @author Sebastian Raubach
 */
public class IntegerListBox extends GerminateValueListBox<Integer>
{
	public IntegerListBox()
	{
		this(false);
	}

	public IntegerListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<Integer>()
		{
			@Override
			protected String getText(Integer object)
			{
				return object == null ? null : Integer.toString(object);
			}

			@Override
			protected String getUnit(Integer object)
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
	}
}
