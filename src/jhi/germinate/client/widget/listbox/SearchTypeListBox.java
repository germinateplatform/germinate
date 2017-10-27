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

import jhi.germinate.client.page.search.*;

/**
 * SearchTypeListBox extends {@link GerminateValueListBox} and displays {@link SearchPage.SearchType}s.
 *
 * @author Sebastian Raubach
 */
public class SearchTypeListBox extends GerminateValueListBox<SearchPage.SearchType>
{
	public SearchTypeListBox()
	{
		this(false);
	}

	public SearchTypeListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<SearchPage.SearchType>()
		{
			@Override
			protected String getText(SearchPage.SearchType object)
			{
				return object.getTitle();
			}

			@Override
			protected String getUnit(SearchPage.SearchType object)
			{
				return null;
			}

			@Override
			protected BracketType getBracketType()
			{
				return null;
			}
		});

		setMultipleSelect(multiSelect);

		setValue(SearchPage.SearchType.ALL, false);
		setAcceptableValues(SearchPage.SearchType.values());
	}
}
