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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Button;

import java.util.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DropdownInputButton<T> extends Composite
{
	interface DropdownInputButtonUiBinder extends UiBinder<InputGroupButton, DropdownInputButton>
	{
	}

	private static DropdownInputButtonUiBinder ourUiBinder = GWT.create(DropdownInputButtonUiBinder.class);

	@UiField
	Button       button;
	@UiField
	DropDownMenu menu;

	private List<T> data      = new ArrayList<>();
	private T       selection = null;

	public DropdownInputButton()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	public void setData(List<T> data, boolean selectFirst)
	{
		this.data.clear();
		this.data = new ArrayList<>(data);

		for (int i = 0; i < data.size(); i++)
		{
			final int index = i;
			AnchorListItem anchor = new AnchorListItem(getLabel(data.get(index)));
			anchor.addClickHandler(e ->
			{
				selection = data.get(index);
				button.setText(((Anchor) e.getSource()).getText());
			});
			menu.add(anchor);
		}

		if (selectFirst && data.size() > 0)
		{
			selection = this.data.get(0);
			button.setText(getLabel(selection));
		}
	}

	public T getSelection()
	{
		return selection;
	}

	protected abstract String getLabel(T item);
}