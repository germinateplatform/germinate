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

package jhi.germinate.client.page.search;

import com.google.gwt.core.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.Panel;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class SearchSection extends Composite
{
	interface SearchSectionUiBinder extends UiBinder<Panel, SearchSection>
	{
	}

	private static SearchSectionUiBinder ourUiBinder = GWT.create(SearchSectionUiBinder.class);

	@UiField
	Panel         panel;
	@UiField
	PanelHeader   panelHeader;
	@UiField
	PanelCollapse target;
	@UiField
	Heading       header;
	@UiField
	Progress      progress;
	@UiField
	Label         label;
	@UiField
	PanelBody     body;

	public SearchSection()
	{
		initWidget(ourUiBinder.createAndBindUi(this));

		String id = "search-" + RandomUtils.RANDOM.nextLong();

		target.setId(id);
		panelHeader.setDataTarget("#" + id);
	}

	public void setPreventHideSibling(String preventHideSibling)
	{
		setPreventHideSibling(Boolean.parseBoolean(preventHideSibling));
	}

	public void setPreventHideSibling(boolean preventHideSibling)
	{
		panelHeader.setDataParent(null);
	}

	public void setHeading(String title)
	{
		header.setText(title);
	}

	public void clear()
	{
		body.clear();
		label.setVisible(true);
		label.setText("0");
		progress.setVisible(false);
	}

	public void add(Widget widget)
	{
		body.add(widget);
	}

	public void setLoading(boolean loading)
	{
		progress.setVisible(loading);
		label.setVisible(!loading);
	}

	public void setLabel(String value)
	{
		if (StringUtils.isEmpty(value))
		{
			label.setVisible(false);
		}
		else
		{
			label.setVisible(true);
			label.setText(value);
		}
	}
}