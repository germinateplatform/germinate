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

package jhi.germinate.shared.datastructure;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link Page} is an attempt to clone the functionality of an {@link Enum} while allowing inheritance.
 *
 * @author Sebastian Raubach
 */
public class Page implements Serializable
{
	private static final long serialVersionUID = 4904154902449249967L;

	private static final Map<String, Page> pages = new TreeMap<>();

	public static final Page ABOUT_GERMINATE          = new Page("about-germinate", true, Style.MDI_INFORMATION_OUTLINE);
	public static final Page ABOUT_PROJECT            = new Page("about-project", false, Style.MDI_BULLETIN_BOARD);
	public static final Page ACCESSION_OVERVIEW       = new Page("accession-overview", false, Style.MDI_FLOWER).addLegacyNames("browse-accessions");
	public static final Page ACCESSIONS_FOR_COLLSITE  = new Page("accessions-for-collsite", false);
	public static final Page ACKNOWLEDGEMENTS         = new Page("acknowledgements", false, Style.MDI_ACCOUNT_MULTIPLE);
	public static final Page ADMIN_CONFIG             = new Page("admin-config", false);
	public static final Page ALLELE_FREQUENCY_DATASET = new Page("allele-freq-dataset", false, Style.MDI_PULSE);
	public static final Page ALLELE_FREQUENCY_EXPORT  = new Page("allele-freq-export", false);
	public static final Page ALLELE_FREQUENCY_RESULT  = new Page("allele-freq-result", false);
	public static final Page CLIMATE                  = new Page("climate-data", false).addLegacyNames("climate");
	public static final Page CLIMATE_DATASETS         = new Page("climate-datasets", false, Style.MDI_WEATHER_SNOWY_RAIN);
	public static final Page COMPOUNDS                = new Page("compounds", false, Style.MDI_ATOM);
	public static final Page COMPOUND_DETAILS         = new Page("compound-details", false);
	public static final Page COMPOUND_DATASETS        = new Page("compound-datasets", false, Style.MDI_FLASK);
	public static final Page COMPOUND_DATA            = new Page("compound-data", false);
	public static final Page TRAITS                   = new Page("traits", false, Style.MDI_TAG_TEXT_OUTLINE);
	public static final Page TRAIT_DETAILS            = new Page("trait-details", false);
	public static final Page COOKIE                   = new Page("cookie", true);
	public static final Page DATA_STATISTICS          = new Page("data-stats", false, Style.MDI_CHART_AREASPLINE);
	public static final Page DATASET_OVERVIEW         = new Page("dataset-overview", false, Style.MDI_DATABASE);
	public static final Page EXPERIMENT_DETAILS       = new Page("experiment-details", false);
	public static final Page GENOTYPE_DATASETS        = new Page("genotype-datasets", false, Style.MDI_DNA);
	public static final Page GENOTYPE_EXPORT          = new Page("genotype-export", false);
	public static final Page GEOGRAPHIC_SEARCH        = new Page("geographic-search", false, Style.MDI_CROSSHAIRS_GPS);
	public static final Page GROUP_PREVIEW            = new Page("group-preview", false);
	public static final Page GROUPS                   = new Page("groups", false, Style.MDI_GROUP);
	public static final Page IMAGE_GALLERY            = new Page("image-gallery", false, Style.MDI_IMAGE_MULTIPLE).addLegacyNames("gallery");
	public static final Page LOCATIONS                = new Page("locations", false, Style.MDI_GOOGLE_MAPS).addLegacyNames("geography", "collsite-treemap");
	public static final Page HOME                     = new Page("home", false, Style.MDI_HOME);
	public static final Page INSTITUTIONS             = new Page("institutions", false);
	public static final Page MAP_DETAILS              = new Page("maps", false, Style.MDI_REORDER_VERTICAL).addLegacyNames("map-details");
	public static final Page MARKED_ITEMS             = new Page("marked-items", false).addLegacyNames("cart");
	public static final Page MARKER_DETAILS           = new Page("marker-details", false);
	public static final Page MEGA_ENVIRONMENT         = new Page("mega-environments", false, Style.MDI_EARTH);
	public static final Page NEWS                     = new Page("news", true);
	public static final Page PASSPORT                 = new Page("passport", false);
	public static final Page OSTEREI                  = new Page("osterei", true);
	public static final Page SEARCH                   = new Page("search", false, Style.MDI_MAGNIFY);
	public static final Page TRIALS                   = new Page("trials", false).addLegacyNames("categorical-export");
	public static final Page TRIALS_DATASETS          = new Page("trials-datasets", false, Style.MDI_SHOVEL).addLegacyNames("categorical-datasets");
	public static final Page USER_PERMISSIONS         = new Page("user-permissions", false, Style.MDI_ACCOUNT_MULTIPLE);

	private String      name;
	private boolean     isPublic;
	private String      icon;
	/** Names this page used to have in the past. This can either be caused by a simple renaming or by merging pages. */
	private Set<String> legacyNames = new HashSet<>();

	public Page()
	{
	}

	protected Page(String name, boolean isPublic)
	{
		this(name, isPublic, null);
	}

	private Page(String name, boolean isPublic, String icon)
	{
		this.name = name;
		this.isPublic = isPublic;
		this.icon = icon;

		pages.put(name, this);
	}

	public String name()
	{
		return name;
	}

	public boolean isPublic()
	{
		return isPublic;
	}

	public String getIcon()
	{
		return icon;
	}

	/**
	 * Tries to create an instance from the constants
	 *
	 * @param name The possible name of one of the constants
	 * @return The {@link Page} instance
	 * @throws IllegalArgumentException If none of the existing pages match the given name
	 */
	public static Page valueOf(String name)
	{
		// Check the collection
		Page page = pages.get(name);

		if (page != null)
		{
			return page;
		}
		else
		{
			// Check the legacy names of each page
			for (Page p : pages.values())
			{
				if (p.legacyNames.contains(name))
					page = p;
			}
		}

		if (page != null)
			return page;
		else
			throw new IllegalArgumentException("Invalid Page value: " + name);
	}

	public boolean is(Page page)
	{
		return Objects.equals(name, page.name) || legacyNames.contains(page.name);
	}

	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Returns an array of all {@link Page}s
	 *
	 * @return An array of all {@link Page}s
	 */
	public static Page[] values()
	{
		return pages.values().toArray(new Page[pages.size()]);
	}

	public Page addLegacyNames(String... names)
	{
		for (String name : names)
			legacyNames.add(name);
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Page page = (Page) o;

		if (isPublic != page.isPublic) return false;
		return name.equals(page.name);

	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + (isPublic ? 1 : 0);
		return result;
	}

	/**
	 * Checks if the history token matches any of the available {@link Page}s.
	 *
	 * @param historyToken The history token, i.e. the part after the hashtag in the URL.
	 * @return The corresponding page
	 * @throws InvalidPageException Thrown if the history token doesn't match any {@link Page}.
	 */
	public static Page parse(String historyToken) throws InvalidPageException
	{
		Page page = null;

		for (int i = historyToken.length(); i > 0; i--)
		{
			try
			{
				page = Page.valueOf(historyToken.substring(0, i));
				break;
			}
			catch (IllegalArgumentException e)
			{
			}
		}

		if (StringUtils.isEmpty(historyToken))
			return Page.HOME;
		else if (page == null)
			throw new InvalidPageException(historyToken);
		else
			return page;
	}
}
