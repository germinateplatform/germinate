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

package jhi.germinate.client.page;

import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.Map;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.tour.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;

/**
 * @author Sebastian Raubach
 */
public class Home extends GerminateComposite implements HasHelp
{
	private NewsWidget newsWidget = new NewsWidget();

	@Override
	public Library[] getLibraryList()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		Heading h = new Heading(HeadingSize.H1, Text.LANG.homeTitle());
		h.addStyleName(Styles.PAGE_HEADER);

		panel.add(h);

		Row row = new Row();
		panel.add(row);

		CommonService.Inst.get().getOverviewStats(Cookie.getRequestProperties(), new DefaultAsyncCallback<ServerResult<Map<String, Long>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<Map<String, Long>> result)
			{
				int i = 0;
				Column accessionColumn = new Column(ColumnSize.XS_6, ColumnSize.MD_6, ColumnSize.LG_3);
				accessionColumn.addStyleName(Style.COL_XXS_12);

				if (GerminateSettingsHolder.isPageAvailable(Page.ACCESSION_OVERVIEW))
				{
					long number = result.getServerResult().get(Accession.class.getName());
					String value = NumberUtils.format(number);
					String tooltip = NumberUtils.INTEGER_FORMAT.format(number);
					accessionColumn.getElement().appendChild(new InfoPanel(value, tooltip, Text.LANG.searchAccessions(), Style.MDI_FLOWER, Text.LANG.generalContinue(), GerminateSettingsHolder.getCategoricalColor(i++), Page.ACCESSION_OVERVIEW).getElement());
					row.add(accessionColumn);
				}

				Column markerColumn = new Column(ColumnSize.XS_6, ColumnSize.MD_6, ColumnSize.LG_3);
				markerColumn.addStyleName(Style.COL_XXS_12);

				if (GerminateSettingsHolder.isPageAvailable(Page.MAP_DETAILS))
				{
					long number = result.getServerResult().get(Marker.class.getName());
					String value = NumberUtils.format(number);
					String tooltip = NumberUtils.INTEGER_FORMAT.format(number);
					markerColumn.getElement().appendChild(new InfoPanel(value, tooltip, Text.LANG.searchMarkers(), Style.MDI_DNA, Text.LANG.generalContinue(), GerminateSettingsHolder.getCategoricalColor(i++), Page.MAP_DETAILS).getElement());
					row.add(markerColumn);
				}

				Column locationColumn = new Column(ColumnSize.XS_6, ColumnSize.MD_6, ColumnSize.LG_3);
				locationColumn.addStyleName(Style.COL_XXS_12);

				if (GerminateSettingsHolder.isPageAvailable(Page.LOCATIONS))
				{
					long number = result.getServerResult().get(Location.class.getName());
					String value = NumberUtils.format(number);
					String tooltip = NumberUtils.INTEGER_FORMAT.format(number);
					locationColumn.getElement().appendChild(new InfoPanel(value, tooltip, Text.LANG.searchCollectingsites(), Style.MDI_MAP_MARKER, Text.LANG.generalContinue(), GerminateSettingsHolder.getCategoricalColor(i++), Page.LOCATIONS).getElement());
					row.add(locationColumn);
				}

				Column groupColumn = new Column(ColumnSize.XS_6, ColumnSize.MD_6, ColumnSize.LG_3);
				groupColumn.addStyleName(Style.COL_XXS_12);

				if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
				{
					long number = result.getServerResult().get(Group.class.getName());
					String value = NumberUtils.format(number);
					String tooltip = NumberUtils.INTEGER_FORMAT.format(number);
					groupColumn.getElement().appendChild(new InfoPanel(value, tooltip, Text.LANG.searchGroup(), Style.MDI_GROUP, Text.LANG.generalContinue(), GerminateSettingsHolder.getCategoricalColor(i++), Page.GROUPS).getElement());
					row.add(groupColumn);
				}

				updateSize(i, accessionColumn, markerColumn, locationColumn, groupColumn);
			}
		});

		panel.add(new HTML(Text.LANG.homeText()));

		Button intro = new Button(Text.LANG.introductionTourButton(), e ->
		{
			Tour tour = Tour.newInstance();

			tour.addStep(TourStep.newInstance(Text.LANG.introductionTourMessageWelcome()));
			tour.addStep(TourStep.newInstance("#" + Id.STRUCTURE_MAIN_MENU_UL, Text.LANG.introductionTourMessageNavigation()));
			if (LanguageSelector.hasLanguages)
				tour.addStep(TourStep.newInstance("#" + Id.STRUCTURE_LANGUAGE_SELECTOR_UL, Text.LANG.introductionTourMessageLanguage()));
			tour.addStep(TourStep.newInstance("#" + Id.STRUCTURE_SHARE_UL, Text.LANG.introductionTourMessageShare()));
			if (GerminateSettingsHolder.isPageAvailable(Page.MARKED_ITEMS))
				tour.addStep(TourStep.newInstance("#" + Id.STRUCTURE_MARKED_ITEM_UL, Text.LANG.introductionTourMessageShoppingCart()));

			tour.addStep(TourStep.newInstance("#" + newsWidget.getNewsColumnId(), Text.LANG.introductionTourMessageNews()));
			tour.addStep(TourStep.newInstance("#" + newsWidget.getProjectColumnId(), Text.LANG.introductionTourMessageProjects()));
			tour.addStep(TourStep.newInstance("#" + Id.STRUCTURE_HELP_A, Text.LANG.introductionTourMessageHelp()));
			tour.addStep(TourStep.newInstance(Text.LANG.introductionTourMessageFinal()));

			tour.start();
		});
		intro.addStyleName(Style.mdiLg(Style.MDI_PLAY));
		intro.setType(ButtonType.PRIMARY);
		panel.add(intro);

		panel.add(newsWidget);
	}

	private void updateSize(int count, Column... columns)
	{
		for (Column column : columns)
		{
			switch (count)
			{
				case 1:
					column.setSize(ColumnSize.XS_12);
					break;
				case 2:
					column.setSize(ColumnSize.XS_6, ColumnSize.MD_6);
					break;
				case 3:
					column.setSize(ColumnSize.XS_6, ColumnSize.MD_6, ColumnSize.LG_4);
					break;
				case 4:
					column.setSize(ColumnSize.XS_6, ColumnSize.MD_6, ColumnSize.LG_3);
					break;
			}
		}
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.homeHelp());
	}
}
