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

package jhi.germinate.client.page.groups;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.TextBox;

import java.util.*;

import jhi.germinate.client.widget.listbox.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class AddGroupDialog extends Composite
{
	interface AddGroupDialogUiBinder extends UiBinder<FlowPanel, AddGroupDialog>
	{
	}

	private static AddGroupDialogUiBinder ourUiBinder = GWT.create(AddGroupDialogUiBinder.class);

	@UiField
	GroupTypeListBox groupType;

	@UiField
	TextBox groupName;

	public AddGroupDialog(List<GroupType> types, GroupType toSelect)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		if (toSelect != null)
		{
			groupType.setValue(toSelect, false);
			groupType.setEnabled(false);
		}
		else
		{
			groupType.setValue(types.get(0), false);
		}

		groupType.setAcceptableValues(types);
	}

	public String getName()
	{
		return groupName.getText();
	}

	public GroupType getType()
	{
		return groupType.getSelection();
	}
}