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

package jhi.germinate.client.widget.structure;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;

/**
 * {@link MainMenu} is a utility class used to create and interact with the global Germinate menu
 *
 * @author Sebastian Raubach
 */
public final class MainMenu
{
	private static MenuItem root;

	private MainMenu()
	{

	}

	/**
	 * Initializes the global menu of Germinate
	 */
	public static void init()
	{
		if (root != null)
			return;

		Panel menuPanel = RootPanel.get(Id.STRUCTURE_MAIN_MENU_UL);

		root = new MenuItem();

		CustomMenu customMenu = GerminateSettingsHolder.get() != null ? GerminateSettingsHolder.get().customMenu : null;

		if (customMenu == null)
		{
			/* Add the menu menus */
			if (GerminateSettingsHolder.isPageAvailable(Page.HOME))
				root.addChild(new MenuItem(root, Text.LANG.menuHome(), Page.HOME, true));

			MenuItem data = new MenuItem(root, Text.LANG.menuData(), Style.MDI_HARDDISK);

			if (GerminateSettingsHolder.isPageAvailable(Page.ACCESSION_OVERVIEW))
				data.addChild(new MenuItem(data, Text.LANG.menuBrowseAccessions(), Page.ACCESSION_OVERVIEW));

			MenuItem genetic = new MenuItem(data, Text.LANG.menuGenetic(), Style.MDI_DNA);
			if (GerminateSettingsHolder.isPageAvailable(Page.MAP_DETAILS))
				genetic.addChild(new MenuItem(genetic, Text.LANG.menuMaps(), Page.MAP_DETAILS));
			if (GerminateSettingsHolder.isPageAvailable(Page.GENOTYPE_DATASETS))
				genetic.addChild(new MenuItem(genetic, Text.LANG.menuGenotypes(), Page.GENOTYPE_DATASETS));
			if (GerminateSettingsHolder.isPageAvailable(Page.ALLELE_FREQUENCY_DATASET))
				genetic.addChild(new MenuItem(genetic, Text.LANG.menuAlleleFreq(), Page.ALLELE_FREQUENCY_DATASET));

			if (genetic.hasChildren())
				data.addChild(genetic);

			MenuItem phenotype = new MenuItem(data, Text.LANG.menuPhenotypes(), Style.MDI_TAG_MULTIPLE);
			if (GerminateSettingsHolder.isPageAvailable(Page.TRAITS))
				phenotype.addChild(new MenuItem(phenotype, Text.LANG.menuTraits(), Page.TRAITS));
			if (GerminateSettingsHolder.isPageAvailable(Page.TRIALS_DATASETS))
				phenotype.addChild(new MenuItem(phenotype, Text.LANG.menuTrials(), Page.TRIALS_DATASETS));

			if (phenotype.hasChildren())
				data.addChild(phenotype);

			MenuItem compounds = new MenuItem(data, Text.LANG.menuCompounds(), Style.MDI_FLASK);

			if (GerminateSettingsHolder.isPageAvailable(Page.COMPOUNDS))
				compounds.addChild(new MenuItem(compounds, Text.LANG.menuCompounds(), Page.COMPOUNDS));
			if (GerminateSettingsHolder.isPageAvailable(Page.COMPOUND_DATASETS))
				compounds.addChild(new MenuItem(compounds, Text.LANG.menuCompoundData(), Page.COMPOUND_DATASETS));

			if (compounds.hasChildren())
				data.addChild(compounds);

			if (GerminateSettingsHolder.isPageAvailable(Page.DATASET_OVERVIEW))
				data.addChild(new MenuItem(data, Text.LANG.menuDatasetOverview(), Page.DATASET_OVERVIEW));
			if (GerminateSettingsHolder.isPageAvailable(Page.DATA_STATISTICS))
				data.addChild(new MenuItem(data, Text.LANG.menuDataStatistics(), Page.DATA_STATISTICS));

			if (data.hasChildren())
				root.addChild(data);

			MenuItem environment = new MenuItem(root, Text.LANG.menuEnvironment(), Style.MDI_NATURE_PEOPLE);

			MenuItem locations = new MenuItem(environment, Text.LANG.menuLocations(), Style.MDI_MAP_MARKER);
			if (GerminateSettingsHolder.isPageAvailable(Page.GEOGRAPHIC_SEARCH))
				locations.addChild(new MenuItem(locations, Text.LANG.menuGeographicSearch(), Page.GEOGRAPHIC_SEARCH));
			//			if (GerminateSettingsHolder.isPageAvailable(Page.LOCATION_TREEMAP))
			//				locations.addChild(new MenuItem(locations, Text.LANG.menuGeographicTreemap(), Page.LOCATION_TREEMAP));
			if (GerminateSettingsHolder.isPageAvailable(Page.LOCATIONS))
				locations.addChild(new MenuItem(locations, Text.LANG.menuGeography(), Page.LOCATIONS));

			if (locations.hasChildren())
				environment.addChild(locations);

			if (GerminateSettingsHolder.isPageAvailable(Page.MEGA_ENVIRONMENT))
				environment.addChild(new MenuItem(environment, Text.LANG.menuMegaEnvironments(), Page.MEGA_ENVIRONMENT));
			if (GerminateSettingsHolder.isPageAvailable(Page.CLIMATE_DATASETS))
				environment.addChild(new MenuItem(environment, Text.LANG.menuClimate(), Page.CLIMATE_DATASETS));

			if (environment.hasChildren())
				root.addChild(environment);

			if (GerminateSettingsHolder.isPageAvailable(Page.GROUPS))
				root.addChild(new MenuItem(root, Text.LANG.menuGroups(), Page.GROUPS));
			if (GerminateSettingsHolder.isPageAvailable(Page.IMAGE_GALLERY))
				root.addChild(new MenuItem(root, Text.LANG.menuGallery(), Page.IMAGE_GALLERY));

			MenuItem aboutParent = new MenuItem(root, Text.LANG.menuAbout(), Style.MDI_INFORMATION);

			if (GerminateSettingsHolder.isPageAvailable(Page.ABOUT_GERMINATE))
				aboutParent.addChild(new MenuItem(aboutParent, Text.LANG.menuAboutGerminate(), Page.ABOUT_GERMINATE));
			if (GerminateSettingsHolder.isPageAvailable(Page.ABOUT_PROJECT))
				aboutParent.addChild(new MenuItem(aboutParent, Text.LANG.menuAboutProject(), Page.ABOUT_PROJECT));
			if (GerminateSettingsHolder.isPageAvailable(Page.ACKNOWLEDGEMENTS))
				aboutParent.addChild(new MenuItem(aboutParent, Text.LANG.menuAcknowledgements(), Page.ACKNOWLEDGEMENTS));

			if (aboutParent.hasChildren())
				root.addChild(aboutParent);
		}
		else
		{
			for (CustomMenu m : customMenu.getMenus())
			{
				if (m.page != null)
				{
					MenuItem item = new MenuItem(root, getText(m), m.page);
					if (!StringUtils.isEmpty(m.icon))
						item.icon = m.icon;
					root.addChild(item);
				}
				else
				{
					String text = getText(m);
					MenuItem item = new MenuItem(root, text);
					if (!StringUtils.isEmpty(m.icon))
						item.icon = m.icon;
					addMenus(m.getMenus(), item);
					root.addChild(item);
				}
			}
		}

		/* Add the menu to its panel */
		root.addToParent(menuPanel.getElement());

		/* Remove the menuPanel, since we cannot get the children as RootPanels
		 * if their parent is still attached */
		menuPanel.removeFromParent();

		Scheduler.get().scheduleDeferred(() -> fancyUp(menuPanel.getElement().getId()));

		addToggleFunctionality();
	}

	private static void addToggleFunctionality()
	{
		JavaScript.click(".navbar-toggle", false, new ClickCallback()
		{
			@Override
			public void onSuccess(Event event)
			{
				// Toggle the class name on the main page div
				JavaScript.toggleClass("#" + Id.STRUCTURE_PAGE, Style.LAYOUT_SIDEBAR_TOGGLED);

				ResizeRegister.triggerResize();

				// Supress the event propagation if the window is big
				if (Window.getClientWidth() >= 992)
				{
					event.stopPropagation();
					event.preventDefault();
				}
			}
		});
	}

	private static native void fancyUp(String id)/*-{
		$wnd.$('#' + id).metisMenu();
	}-*/;

	private static String getText(CustomMenu m)
	{
		String currentLocale = LocaleInfo.getCurrentLocale().getLocaleName();
		String fallbackLocale = "en_GB";

		String targetLocale = m.getLabels().get(currentLocale);

		if (StringUtils.isEmpty(targetLocale))
			targetLocale = m.getLabels().get(fallbackLocale);
		if (StringUtils.isEmpty(targetLocale) && m.getPage() != null)
			targetLocale = m.getPage().name();
		if (StringUtils.isEmpty(targetLocale))
			targetLocale = "unknown page";

		return targetLocale;
	}

	private static void addMenus(List<CustomMenu> menus, MenuItem parent)
	{
		for (CustomMenu m : menus)
		{
			if (m.page != null)
			{
				parent.addChild(new MenuItem(parent, getText(m), m.page));
			}
			else
			{
				MenuItem item = new MenuItem(parent, getText(m));
				addMenus(m.getMenus(), item);
				parent.addChild(item);
			}
		}
	}

	/**
	 * Removes the 'active' state from  all menu items and the contained links. <p/>
	 */
	public static void removeActiveStateMenuItems()
	{
		JavaScript.removeClass("#" + Id.STRUCTURE_MAIN_MENU_UL + " li a", Style.STATE_ACTIVE);
	}

	public static boolean highlightMenuItem(Page page)
	{
		MenuItem item = MenuItem.pageToItem.get(page);

		boolean atLeastOneActivated = false;

		while (item != null)
		{
			if (item.thisElement != null)
			{
				JavaScript.addClass("#" + item.thisElement.getId() + " > a", Style.STATE_ACTIVE);
				atLeastOneActivated = true;
			}

			item = item.parent;
		}

		return atLeastOneActivated;
	}

	/**
	 * @author Sebastian Raubach
	 */
	public static class MenuItem
	{
		private static Map<Page, MenuItem> pageToItem = new HashMap<>();

		private MenuItem       parent;
		private List<MenuItem> children;
		private Page           page;
		private String         name;
		private boolean        isActive;
		private Element        thisElement;
		private int            level = 0;
		private String         icon  = null;

		public MenuItem()
		{
		}

		/**
		 * Create a new {@link MenuItem} with the given name linking to the given page
		 *
		 * @param name The name to show
		 */
		public MenuItem(MenuItem parent, String name)
		{
			this(parent, name, (Page) null);
		}

		public MenuItem(MenuItem parent, String name, String icon)
		{
			this(parent, name, (Page) null);
			this.icon = icon;
		}

		/**
		 * Create a new {@link MenuItem} with the given name linking to the given page
		 *
		 * @param name The name to show
		 * @param page The page to link to
		 */
		public MenuItem(MenuItem parent, String name, Page page)
		{
			this(parent, name, page, false);
		}

		/**
		 * Creates a new {@link MenuItem} with the given name linking to the given page and using the given active state.
		 *
		 * @param name     The name to show
		 * @param page     The page to link to
		 * @param isActive Should this item be active?
		 */
		public MenuItem(MenuItem parent, String name, Page page, boolean isActive)
		{
			this.parent = parent;
			this.name = name;
			this.page = page;
			this.isActive = isActive;
			pageToItem.put(page, this);

			if (page != null && page.getIcon() != null)
				this.icon = page.getIcon();

			this.level = getLevel();
		}

		private int getLevel()
		{
			if (parent == null)
				return 0;
			else
				return parent.getLevel() + 1;
		}

		private String getStyle()
		{
			switch (level)
			{
				case 1:
					return "nav nav-second-level";
				case 2:
					return "nav nav-third-level";
				case 0:
				default:
					return "";
			}
		}

		/**
		 * Adds a new child to the current {@link MenuItem}
		 *
		 * @param child The new child
		 */
		public void addChild(MenuItem child)
		{
			if (children == null)
				children = new ArrayList<>();

			children.add(child);
		}

		/**
		 * Checks if the current {@link MenuItem} has children
		 *
		 * @return True if it has children
		 */
		public boolean hasChildren()
		{
			return children != null && children.size() > 0;
		}

		public void addToParent(Element element)
		{
			if (parent == null)
			{
				if (children != null)
				{
					for (MenuItem child : children)
					{
						child.addToParent(element);
					}
				}
			}
			else
			{
				String identifier = page == null ? null : page.name();

				thisElement = Document.get().createLIElement();
				thisElement.setId(RandomUtils.generateRandomId());

				if (identifier != null)
					thisElement.setId(identifier);

				Element i = null;

				if (icon != null)
				{
					i = Document.get().createElement("i");
					i.setClassName(Style.combine(Style.MDI, icon, Style.FA_FIXED_WIDTH, Style.MDI_LG));
				}

				AnchorElement e = Document.get().createAnchorElement();
				SpanElement span = Document.get().createSpanElement();

				/* Are there any children? */
				if (!CollectionUtils.isEmpty(children))
				{
					if (page == null)
					{
						/* This element doesn't represent a link */
						span.setInnerText(name);
						e.setHref("#");

						SpanElement child = Document.get().createSpanElement();
						child.setClassName(Style.mdi(Style.combine(Style.MDI_CHEVRON_RIGHT, "arrow")));

						e.appendChild(child);
					}
					else
					{
						/* This element represents a link */
						e.setHref("#" + identifier);
						span.setInnerText(name);
					}

					thisElement.appendChild(e);

					/* Create a list of children */
					UListElement ul = Document.get().createULElement();

					String style = getStyle();
					if (!StringUtils.isEmpty(style))
						ul.addClassName(style);

					thisElement.appendChild(ul);

					for (MenuItem child : children)
						child.addToParent(ul);
				}
				/* If there are no children and we don't have an id, there is
				 * nothing to do */
				else if (page == null)
					return;
					/* Else, the item doesn't have children but represents a link */
				else
				{
					e = Document.get().createAnchorElement();
					e.setHref("#" + identifier);
					span.setInnerText(name);
					thisElement.appendChild(e);
				}

				e.setClassName(isActive ? Style.STATE_ACTIVE : "");
				e.insertFirst(span);

				if (i != null)
					e.insertFirst(i);

				element.appendChild(thisElement);
			}
		}
	}
}
