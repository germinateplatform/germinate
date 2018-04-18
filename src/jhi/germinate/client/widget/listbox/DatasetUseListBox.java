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

import java.util.*;

import jhi.germinate.client.i18n.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetUseListBox extends GerminateValueListBox<String>
{
	public DatasetUseListBox()
	{
		this(false);
	}

	public DatasetUseListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<String>()
		{
			@Override
			protected String getText(String object)
			{
				return object;
			}

			@Override
			protected String getUnit(String object)
			{
				return null;
			}

			@Override
			protected BracketType getBracketType()
			{
				return BracketType.SQUARE;
			}
		});
		setTooltipProvider(item -> item);
		setMultipleSelect(multiSelect);

		List<String> values = new ArrayList<>();
		values.add("");
		values.add(Text.LANG.userTrackingExplanationOptionBasic());
		values.add(Text.LANG.userTrackingExplanationOptionPreBreeding());
		values.add(Text.LANG.userTrackingExplanationOptionBreedingCultivar());
		values.add(Text.LANG.userTrackingExplanationOptionEducation());
		values.add(Text.LANG.userTrackingExplanationOptionDirectUse());
		values.add(Text.LANG.userTrackingExplanationOptionOther());

		setValue("", false);
		setAcceptableValues(values);
	}
}
