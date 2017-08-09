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
import com.google.gwt.user.client.ui.Panel;

import org.gwtbootstrap3.client.ui.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class DescriptionWidget extends Composite
{
	interface DescriptionWidgetUiBinder extends UiBinder<Description, DescriptionWidget>
	{
	}

	private static DescriptionWidgetUiBinder ourUiBinder = GWT.create(DescriptionWidgetUiBinder.class);

	@UiField
	Description      description;
	@UiField
	DescriptionTitle title;
	@UiField
	DescriptionData  data;

	public DescriptionWidget(Panel parent, String title, String data, boolean isHtml, boolean horizontal)
	{
		if (StringUtils.isEmpty(data))
			return;

		initWidget(ourUiBinder.createAndBindUi(this));

		this.title.setText(title);
		this.title.setTitle(title);
		if (isHtml)
			this.data.setHTML(data);
		else
			this.data.setText(data);
		description.setHorizontal(horizontal);

		parent.add(this);
	}

	public DescriptionWidget(Panel parent, String title, Widget data, boolean horizontal)
	{
		if (data == null)
			return;

		initWidget(ourUiBinder.createAndBindUi(this));

		this.title.setText(title);
		this.data.getElement().appendChild(data.getElement());
		description.setHorizontal(horizontal);

		parent.add(this);
	}

	public DescriptionWidget(Panel parent, String title, String data, boolean isHtml)
	{
		this(parent, title, data, isHtml, true);
	}

	public DescriptionWidget(Panel parent, String title, String data)
	{
		this(parent, title, data, false, true);
	}
}