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

/**
 * @author Sebastian Raubach
 */
public class CustomFloatPanel extends Composite
{
	interface CustomFloatPanelUiBinder extends UiBinder<FlowPanel, CustomFloatPanel>
	{
	}

	private static CustomFloatPanelUiBinder ourUiBinder = GWT.create(CustomFloatPanelUiBinder.class);

	@UiField
	FlowPanel left;

	@UiField
	FlowPanel right;

	public CustomFloatPanel()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	public void setLeft(Widget widget)
	{
		left.clear();
		left.add(widget);
	}

	public void setRight(Widget widget)
	{
		right.clear();
		right.add(widget);
	}
}