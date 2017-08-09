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
 * GroupTypeListBox extends {@link GerminateValueListBox} and displays {@link GroupType}s.
 *
 * @author Sebastian Raubach
 */
public class GroupTypeListBox extends GerminateValueListBox<GroupType>
{
	public GroupTypeListBox()
	{
		this(true);
	}

	public GroupTypeListBox(boolean multiSelect)
	{
		super(new GerminateUnitRenderer<GroupType>()
		{
			@Override
			protected String getText(GroupType object)
			{
				return object.getDescription();
			}

			@Override
			protected String getUnit(GroupType object)
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
