/*
 *  Copyright 2018 Information and Computational Sciences,
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

import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.shared.event.*;
import org.gwtbootstrap3.client.ui.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.page.search.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 * @see Parameter#accessionId
 * @see Parameter#accessionName
 */
public class PassportPage extends GerminateComposite implements HasLibraries, HasHelp, HasHyperlinkButton, ParallaxBannerPage
{
	private Accession accession;
	private boolean   isOsterei;

	public PassportPage(boolean isOsterei)
	{
		this.isOsterei = isOsterei;
	}

	@Override
	protected void setUpContent()
	{
		PageHeader pageHeader = new PageHeader();
		pageHeader.setText(Text.LANG.passportPassportData());
		HTML html = new HTML();
		panel.add(pageHeader);
		panel.add(html);

		if (isOsterei)
		{
			panel.add(new OsterPassportWidget());
		}
		else
		{
			/* See if there is information about the selected accession */
			Long stateAccessionId = LongParameterStore.Inst.get().get(Parameter.accessionId);
			String stateGeneralId = StringParameterStore.Inst.get().get(Parameter.generalId);
			String accessionName = StringParameterStore.Inst.get().get(Parameter.accessionName);

			/* Remove these parameters as they are only used to get here */
			StringParameterStore.Inst.get().remove(Parameter.generalId);
			StringParameterStore.Inst.get().remove(Parameter.accessionName);

			/*
			 * We prefer the generalId, since it's only used for hard links to
			 * Germinate. In this case we don't want to use internally stored
			 * accession ids, but rather use the external one
			 */
			PartialSearchQuery filter = null;
			if (stateGeneralId != null)
				filter = new PartialSearchQuery(new SearchCondition(Accession.GENERAL_IDENTIFIER, new Equal(), stateGeneralId, Long.class));
				/* We also prefer the "default display name" as this is the new way of representing an accession during export to Flapjack etc. */
			else if (!StringUtils.isEmpty(accessionName))
				filter = new PartialSearchQuery(new SearchCondition(Accession.NAME, new Equal(), accessionName, String.class));
			else if (stateAccessionId != null)
				filter = new PartialSearchQuery(new SearchCondition(Accession.ID, new Equal(), Long.toString(stateAccessionId), Long.class));

			if (filter != null)
			{
				AccessionService.Inst.get().getForFilter(Cookie.getRequestProperties(), Pagination.getDefault(), filter, new DefaultAsyncCallback<PaginatedServerResult<List<Accession>>>()
				{
					@Override
					public void onFailureImpl(Throwable caught)
					{
						if (caught instanceof DatabaseException)
						{
							html.setText(Text.LANG.errorNoParameterAccession());
						}
						else
						{
							super.onFailureImpl(caught);
						}
					}

					@Override
					public void onSuccessImpl(PaginatedServerResult<List<Accession>> result)
					{
						if (result.hasData())
						{
							accession = result.getServerResult().get(0);

							createContent();
						}
					}
				});
			}
			else
			{
				html.setText(Text.LANG.errorNoParameterAccession());
			}
		}
	}

	private void createContent()
	{
		// We've got a parent entity, go and get their data
		if (accession.getEntityParentId() != null)
		{
			List<String> ids = new ArrayList<>();
			ids.add(Long.toString(accession.getEntityParentId()));
			AccessionService.Inst.get().getByIds(Cookie.getRequestProperties(), Pagination.getDefault(), ids, new DefaultAsyncCallback<ServerResult<List<Accession>>>()
			{
				@Override
				protected void onSuccessImpl(ServerResult<List<Accession>> result)
				{
					if (result.hasData())
					{
						createTwinPanel(result.getServerResult().get(0));
					}
				}
			});
		}
		else
		{
			// Get the information for this entity
			panel.add(new PassportWidget(accession));
		}
	}

	private void createTwinPanel(Accession parent)
	{
		PanelGroup group = new PanelGroup();
		group.setId("accordion");
		panel.add(group);

		// Add the current accession
		final SearchSection section = new SearchSection();
		section.setPreventHideSibling(true);
		section.setHeading(accession.getEntityType().getName() + ": " + accession.getGeneralIdentifier());
		section.setMdi(Style.combine(Style.MDI_LG, accession.getEntityType().getMdi()));
		section.addShownHandler(new ShownHandler()
		{
			private boolean isInit = false;

			@Override
			public void onShown(ShownEvent shownEvent)
			{
				if (!isInit)
				{
					isInit = true;
					section.add(new PassportWidget(accession));
				}
			}
		});
		group.add(section);

		// Add the parent
		final SearchSection parentSection = new SearchSection();
		parentSection.setPreventHideSibling(true);
		parentSection.setHeading(parent.getEntityType().getName() + ": " + parent.getGeneralIdentifier());
		parentSection.setMdi(Style.combine(Style.MDI_LG, parent.getEntityType().getMdi()));
		parentSection.addShownHandler(new ShownHandler()
		{
			private boolean isInit = false;

			@Override
			public void onShown(ShownEvent shownEvent)
			{
				if (!isInit)
				{
					isInit = true;
					parentSection.add(new PassportWidget(parent));
				}
			}
		});
		group.add(parentSection);
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.passportHelp());
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.PASSPORT)
				.addParam(Parameter.accessionId);
	}

	@Override
	public Library[] getLibraries()
	{
		return new Library[]{Library.LEAFLET_COMPLETE};
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxPassport();
	}
}
