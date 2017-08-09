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

package jhi.germinate.client.widget.table.resource;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

/**
 * @author Sebastian Raubach
 */
public class TableOptionsPanel extends Composite
{
	interface TableOptionsPanelUiBinder extends UiBinder<FlowPanel, TableOptionsPanel>
	{
	}

	private static final TableOptionsPanelUiBinder ourUiBinder = GWT.create(TableOptionsPanelUiBinder.class);

	@UiField
	FlowPanel optionsPanel;

	public TableOptionsPanel(Float f, BorderStyleResource.BorderStyle... borders)
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		for (BorderStyleResource.BorderStyle border : borders)
		{
			this.addStyleName(border.getStyle());
		}

		if (f == Float.RIGHT)
		{
			this.getElement().getStyle().setFloat(Style.Float.RIGHT);
		}
	}

	public TableOptionsPanel()
	{
		this(Float.LEFT, BorderStyleResource.BorderStyle.LEFT, BorderStyleResource.BorderStyle.BOTTOM, BorderStyleResource.BorderStyle.RIGHT);
	}

	public void add(Widget widget)
	{
		widget.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
//		widget.addStyleName(StyleConstants.TEXT_COLOR);
		optionsPanel.add(widget);
	}

	public enum Float
	{
		RIGHT,
		LEFT
	}
}