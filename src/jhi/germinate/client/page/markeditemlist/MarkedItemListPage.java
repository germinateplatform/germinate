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

package jhi.germinate.client.page.markeditemlist;

import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link MarkedItemListPage} is a page listing the items marked in the {@link MarkedItemList}
 *
 * @author Sebastian Raubach
 */
public class MarkedItemListPage extends GerminateComposite implements HasHelp
{
	@Override
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		PageHeader header = new PageHeader();
		header.setText(Text.LANG.shoppingCartPageTitle());
		NavTabs tabs = new NavTabs();
		TabContent content = new TabContent();

		MarkedItemList.ItemType selectedType = ItemTypeParameterStore.Inst.get().get(Parameter.markedItemType);

		for (MarkedItemList.ItemType type : MarkedItemList.ItemType.values())
		{
			MdiTabListItem item = new MdiTabListItem(type.getDisplayName());

			item.setDataTarget("#gm8-cart-" + type.name());
			item.setActive(type == selectedType);
			item.setMdi(Style.combine(Style.MDI_LG, type.getIcon()));
			item.setBadgeText(NumberUtils.INTEGER_FORMAT.format(MarkedItemList.get(type).size()));

			tabs.add(item);

			TabPane pane = new TabPane();
			pane.setId("gm8-cart-" + type.name());
			pane.setActive(type == selectedType);
			pane.addStyleName(type == selectedType ? (Styles.FADE + " " + Styles.IN) : Styles.FADE);

			pane.add(type.getContent());

			content.add(pane);
		}

		panel.add(header);
		panel.add(tabs);
		panel.add(content);
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.markedItemsHelp());
	}
}
