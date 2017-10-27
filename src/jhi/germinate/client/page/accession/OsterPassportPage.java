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

package jhi.germinate.client.page.accession;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.gallery.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.enums.*;

/**
 * The {@link OsterPassportPage} is a special version of the {@link PassportPage} that shows information about very rare accessions.
 *
 * @author Sebastian Raubach
 * @see Parameter#accessionId
 */
public class OsterPassportPage extends PassportPage
{
	private static final String URL   = "URL";
	private static final String IMAGE = "IMAGE";

	private static final Taxonomy HOMO_SAPIENS = new Taxonomy(-1L)
			.setGenus("Homo")
			.setSpecies("Sapiens")
			.setPloidy(1);

	private static final Country GERMANY = new Country(-1L)
			.setCountryCode2("DE")
			.setCountryCode3("DEU")
			.setName("Germany");

	private static final Country UK = new Country(-2L)
			.setCountryCode2("GB")
			.setCountryCode3("GBR")
			.setName("United Kingdom of Great Britain and Northern Ireland");

	private static final Location COLOGNE = new Location(-1L)
			.setCountry(GERMANY)
			.setName("Cologne")
			.setShortName("Cologne")
			.setLatitude(57d)
			.setLongitude(50.8919)
			.setElevation(7.0511)
			.setSize(1L);

	private static final Location PERTH = new Location(-2L)
			.setCountry(UK)
			.setName("Perth")
			.setShortName("Perth")
			.setLatitude(107d)
			.setLongitude(56.3927)
			.setElevation(-3.4495)
			.setSize(1L);

	private static final Institution JHI = new Institution(-1L)
			.setCode("GBR048")
			.setName("The James Hutton Institute")
			.setAcronym("JHI")
			.setCountry(UK)
			.setPhone("+44 (0) 844 928 5428")
			.setContact("germinate@hutton.ac.uk")
			.setAddress("The James Hutton Institute, Invergowrie, Dundee, DD2 5DA, Scotland, UK");

	private static final Accession SEBASTIAN_RAUBACH = new Accession(-7L)
			.setName("Sebastian Raubach")
			.setTaxonomy(HOMO_SAPIENS)
			.setLocation(COLOGNE)
			.setInstitution(JHI)
			.setCollDate(DateUtils.getDateFromDatabaseString("1986-02-25"));

	private static final Accession PAUL_SHAW = new Accession(-999999L)
			.setName("Paul Shaw")
			.setTaxonomy(HOMO_SAPIENS)
			.setLocation(PERTH)
			.setInstitution(JHI)
			.setCollDate(DateUtils.getDateFromDatabaseString("1976-10-15"));

	static
	{
		SEBASTIAN_RAUBACH.setExtra(URL, "http://www.hutton.ac.uk/staff/sebastian-raubach");
		SEBASTIAN_RAUBACH.setExtra(IMAGE, "img/raubach-sebastian.png");

		PAUL_SHAW.setExtra(URL, "http://www.hutton.ac.uk/staff/paul-shaw");
		PAUL_SHAW.setExtra(IMAGE, "img/shaw-paul.png");
	}

	@Override
	protected void updateDownloads()
	{
	}

	@Override
	protected void updateImages()
	{
		imageWrapper.setVisible(true);
		imagePanel.add(new Gallery(false, false)
		{
			@Override
			protected void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback)
			{
				List<Image> table = new ArrayList<>();
				Image row = new Image(-1L);

				row.setDescription("Mugshot");
				row.setPath(GWT.getHostPageBaseURL() + accession.getExtra(IMAGE));

				table.add(row);
				callback.onSuccess(new PaginatedServerResult<>(null, table, 1));
			}
		});
	}

	@Override
	protected void updateExternalLinks()
	{
		linkWrapper.setVisible(true);
		linkWidget.removeFromParent();

		ULPanel ulPanel = new ULPanel();

		String description = accession.getName() + " @ JHI";
		String hyperlink = accession.getExtra(URL);

		ulPanel.add(new Anchor(description, hyperlink, "_blank"));

		linkWrapper.add(ulPanel);
	}

	@Override
	protected void updatePedigree()
	{
	}

	@Override
	protected void updateGroups()
	{
	}

	@Override
	protected void updateDatasets()
	{
	}

	@Override
	protected void updateAttributes()
	{
	}

	@Override
	protected void updateComments()
	{
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return null;
	}

	@Override
	protected void onLoad()
	{
		Long id = LongParameterStore.Inst.get().get(Parameter.accessionId);
		accession = getById(id);

		if (accession != null)
		{
			super.updateContent();
		}
		else
		{
			pageHeader.setText(Text.LANG.ostereiMessage());
		}
	}

	public static Accession getById(Long id)
	{
		if (Objects.equals(id, SEBASTIAN_RAUBACH.getId()))
			return SEBASTIAN_RAUBACH;
		else if (Objects.equals(id, PAUL_SHAW.getId()))
			return PAUL_SHAW;
		else
			return null;
	}

	public static long isOsterEi(String searchString) throws IllegalArgumentException
	{
		if (StringUtils.areEqualIgnoreCase(searchString, SEBASTIAN_RAUBACH.getName()))
			return SEBASTIAN_RAUBACH.getId();
		else if (StringUtils.areEqualIgnoreCase(searchString, PAUL_SHAW.getName()))
			return PAUL_SHAW.getId();
		else
			throw new IllegalArgumentException();
	}
}
