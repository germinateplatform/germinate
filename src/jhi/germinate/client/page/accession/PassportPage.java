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

package jhi.germinate.client.page.accession;

import com.google.gwt.core.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.query.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.Locale;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.gallery.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.structure.resource.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * The {@link PassportPage} shows all the information about a single {@link Accession}.
 *
 * @author Sebastian Raubach
 * @see Parameter#accessionId
 * @see Parameter#accessionName
 */
public class PassportPage extends Composite implements HasLibraries, HasHelp, HasHyperlinkButton, ParallaxBannerPage
{
	interface PassportPageUiBinder extends UiBinder<HTMLPanel, PassportPage>
	{
	}

	private static PassportPageUiBinder ourUiBinder = GWT.create(PassportPageUiBinder.class);

	@UiField
	PageHeader pageHeader;
	@UiField
	HTML       html;

	@UiField
	Row topWrapper;

	@UiField
	FlowPanel mcpdPanel;
	@UiField
	FlowPanel institutionPanel;

	@UiField
	FlowPanel     synonymsWrapper;
	@UiField
	SynonymWidget synonyms;

	@UiField
	FlowPanel   pedigreeWrapper;
	@UiField
	SimplePanel pedigreeListPanel;
	@UiField
	SimplePanel pedigreeTablePanel;
	@UiField
	SimplePanel pedigreeDownloadPanel;

	@UiField
	FlowPanel locationWrapper;
	@UiField
	FlowPanel locationPanel;

	@UiField
	FlowPanel imageWrapper;
	@UiField
	FlowPanel imagePanel;

	@UiField
	FlowPanel groupWrapper;
	@UiField
	HTML      groupHtml;
	@UiField
	FlowPanel groupPanel;

	@UiField
	FlowPanel datasetWrapper;
	@UiField
	HTML      datasetHtml;
	@UiField
	FlowPanel datasetPanel;

	@UiField
	FlowPanel attributeWrapper;
	@UiField
	FlowPanel attributePanel;

	@UiField
	FlowPanel     commentWrapper;
	@UiField
	CommentWidget commentWidget;

	@UiField
	FlowPanel  linkWrapper;
	@UiField
	LinkWidget linkWidget;

	@UiField
	FlowPanel   downloadWrapper;
	@UiField
	SimplePanel downloadPanel;

	protected Accession accession;

	public PassportPage()
	{
		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		super.onLoad();

		html.setHTML(Text.LANG.passportText());
		groupHtml.setHTML(Text.LANG.passportGroupsOverviewText());
		datasetHtml.setHTML(Text.LANG.passportDatasetsOverviewText());

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
			filter = new PartialSearchQuery(new SearchCondition(Accession.GENERAL_IDENTIFIER, new Equal(), stateGeneralId, Long.class.getSimpleName()));
		/* We also prefer the "default display name" as this is the new way of representing an accession during export to Flapjack etc. */
		else if (!StringUtils.isEmpty(accessionName))
			filter = new PartialSearchQuery(new SearchCondition(Accession.NAME, new Equal(), accessionName, String.class.getSimpleName()));
		else if (stateAccessionId != null)
			filter = new PartialSearchQuery(new SearchCondition(Accession.ID, new Equal(), Long.toString(stateAccessionId), Long.class.getSimpleName()));

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
					if (!CollectionUtils.isEmpty(result.getServerResult()))
					{
						accession = result.getServerResult().get(0);
						updateContent();
					}
				}
			});
		}
		else
		{
			html.setText(Text.LANG.errorNoParameterAccession());
		}
	}

	protected void updateContent()
	{
		if (accession != null)
		{
//			ContentHolder.getInstance().updateShoppingCartButton(this, MarkedItemList.ItemType.ACCESSION); // TODO

			updateHeader();
			updateMcpd();
			updateInstitution();
			updateSynonyms();
			updatePedigree();
			updateLocation();
			updateImages();
			updateGroups();
			updateDatasets();
			updateAttributes();
			updateComments();
			updateExternalLinks();
			updateDownloads();
		}
	}

	protected void updateDownloads()
	{
		FileListService.Inst.get().getForFolder(Cookie.getRequestProperties(), FileLocation.download, ReferenceFolder.passport, new DefaultAsyncCallback<List<String>>()
		{
			@Override
			public void onSuccessImpl(List<String> files)
			{
				if (!CollectionUtils.isEmpty(files))
				{
					downloadWrapper.setVisible(true);
					downloadPanel.add(new FileDownloadWidget()
							.setIconStyle(FileDownloadWidget.IconStyle.MDI)
							.setLocation(FileLocation.download)
							.setHeading(null)
							.setPrefix(ReferenceFolder.passport.name())
							.setFiles(files));
				}
			}
		});
	}

	protected void updateExternalLinks()
	{
		linkWrapper.setVisible(true);
		linkWidget.update(GerminateDatabaseTable.germinatebase, accession.getId());
	}

	protected void updateComments()
	{
		commentWrapper.setVisible(true);
		commentWidget.update(accession.getId(), GerminateDatabaseTable.germinatebase);
	}

	protected void updateAttributes()
	{
		attributeWrapper.setVisible(true);
		attributePanel.add(new AttributeDataForAccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<AttributeData>>> callback)
			{
				try
				{
					if (filter == null)
						filter = new PartialSearchQuery();
					SearchCondition condition = new SearchCondition();
					condition.setColumnName(Accession.ID);
					condition.setComp(new Equal());
					condition.addConditionValue(Long.toString(accession.getId()));
					condition.setType(Long.class.getSimpleName());
					filter.add(condition);

					if (filter.getAll().size() > 1)
						filter.addLogicalOperator(new And());
				}
				catch (InvalidArgumentException | InvalidSearchQueryException e)
				{
					e.printStackTrace();
				}

				return AttributeService.Inst.get().getForFilter(Cookie.getRequestProperties(), pagination, GerminateDatabaseTable.germinatebase, filter, callback);
			}
		});
	}

	protected void updateDatasets()
	{
		datasetWrapper.setVisible(true);
		datasetPanel.add(new DatasetTable(DatasetTable.SelectionMode.NONE, true, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForAccession(Cookie.getRequestProperties(), accession.getId(), pagination, callback);
			}
		});
	}

	protected void updateGroups()
	{
		groupWrapper.setVisible(true);
		groupPanel.add(new GroupTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Group>>> callback)
			{
				return GroupService.Inst.get().getForAccession(Cookie.getRequestProperties(), accession.getId(), pagination, callback);
			}
		});
	}

	protected void updateImages()
	{
		imageWrapper.setVisible(true);
		imagePanel.add(new Gallery(false, false)
		{
			@Override
			protected void getData(Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback)
			{
				ImageService.Inst.get().getForId(Cookie.getRequestProperties(), GerminateDatabaseTable.germinatebase, accession.getId(), pagination, callback);
			}
		});
	}

	private void updateLocation()
	{
		if (accession.getLocation() != null && accession.getLocation().getLatitude() != null && accession.getLocation().getLongitude() != null)
		{
			locationWrapper.setVisible(true);
			new LeafletUtils.IndividualMarkerCreator(locationPanel, Collections.singletonList(accession.getLocation()), null);
		}
	}

	protected void updatePedigree()
	{
		pedigreeWrapper.setVisible(true);
		PedigreeService.Inst.get().getPedigreeDefinitions(Cookie.getRequestProperties(), accession.getId(), new DefaultAsyncCallback<ServerResult<List<PedigreeDefinition>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<PedigreeDefinition>> result)
			{
				if (!CollectionUtils.isEmpty(result.getServerResult()))
				{
					ULPanel list = new ULPanel();

					for (PedigreeDefinition def : result.getServerResult())
					{
						if (!StringUtils.isEmpty(def.getNotation().getReferenceUrl()))
						{
							HTML html = new HTML("<a target='_blank' href='" + def.getNotation().getReferenceUrl() + "'>" + def.getNotation().getName() + "</a>: " + def.getDefinition());
							html.getElement().getStyle().setDisplay(com.google.gwt.dom.client.Style.Display.INLINE_BLOCK);
							list.add(html, Style.MDI_SITEMAP);
						}
						else
						{
							list.add(new InlineLabel(def.getNotation().getName() + ": " + def.getDefinition()), Style.MDI_SITEMAP);
						}
					}

					pedigreeListPanel.add(list);
				}
			}
		});

		pedigreeTablePanel.add(new PedigreeTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected boolean supportsFiltering()
			{
				return false;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Pedigree>>> callback)
			{
				try
				{
					filter = new PartialSearchQuery();

					SearchCondition condition = new SearchCondition();
					condition.setColumnName("Child.id");
					condition.setComp(new Equal());
					condition.addConditionValue(Long.toString(accession.getId()));
					condition.setType(Long.class.getSimpleName());
					filter.add(condition);

					filter.addLogicalOperator(new Or());

					condition = new SearchCondition();
					condition.setColumnName("Parent.id");
					condition.setComp(new Equal());
					condition.addConditionValue(Long.toString(accession.getId()));
					condition.setType(Long.class.getSimpleName());
					filter.add(condition);
				}
				catch (InvalidArgumentException | InvalidSearchQueryException e)
				{
					e.printStackTrace();
				}


				return PedigreeService.Inst.get().getForFilter(Cookie.getRequestProperties(), filter, pagination, new AsyncCallback<PaginatedServerResult<List<Pedigree>>>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						pedigreeDownloadPanel.clear();

						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(PaginatedServerResult<List<Pedigree>> result)
					{
						if (result.getResultSize() < 1)
							pedigreeDownloadPanel.clear();

						callback.onSuccess(result);
					}
				});


			}
		});

		pedigreeDownloadPanel.add(new OnDemandFileDownloadWidget((index, callback) -> PedigreeService.Inst.get().exportToHelium(Cookie.getRequestProperties(), Collections.singletonList(accession.getId()), Pedigree.PedigreeQuery.UP_DOWN_RECURSIVE, callback), true)
				.setHeading(null)
				.setIconStyle(FileDownloadWidget.IconStyle.IMAGE)
				.addFile(Text.LANG.downloadPedigreeHelium())
				.addType(FileType.helium));
	}

	private void updateSynonyms()
	{
		synonymsWrapper.setVisible(true);
		synonyms.update(GerminateDatabaseTable.germinatebase, accession.getId());
	}

	private void updateInstitution()
	{
		final Institution institution = accession.getInstitution();

		if (institution != null)
		{
			topWrapper.setVisible(true);
			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnId(), Long.toString(institution.getId()));
			if (GerminateSettingsHolder.isPageAvailable(Page.INSTITUTIONS))
			{
				Anchor anchor = new Anchor(institution.getName());
				GQuery.$(anchor).click(new Function()
				{
					@Override
					public boolean f(Event e)
					{
						LongParameterStore.Inst.get().put(Parameter.institutionId, institution.getId());
						History.newItem(Page.INSTITUTIONS.name());
						return false;
					}
				});
//				anchor.addClickHandler(event ->
//				{
//					LongParameterStore.Inst.get().put(Parameter.institutionId, institution.getId());
//					History.newItem(Page.INSTITUTIONS.name());
//				});
				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnName(), anchor, true);
			}
			else
			{
				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnName(), institution.getName());
			}

			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnAcronym(), institution.getAcronym());
			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnCode(), institution.getCode());

			if (institution.getCountry() != null)
				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnCountry(), "<span class='" + Style.COUNTRY_FLAG + " " + institution.getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "'></span>" + institution.getCountry().getName(), true);

			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnContact(), institution.getContact());
			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnPhone(), institution.getPhone());

			if (!StringUtils.isEmpty(institution.getEmail()))
				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnEmail(), SimpleHtmlTemplate.INSTANCE.mailto(institution.getEmail()).asString(), true);
			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnAddress(), institution.getAddress());
		}
		else
		{
			institutionPanel.add(new Heading(HeadingSize.H4, Text.LANG.notificationNoDataFound()));
		}
	}

	private void updateHeader()
	{
		String name = accession.getName();
		String number = accession.getNumber();
		String gid = accession.getGeneralIdentifier() == null ? "" : accession.getGeneralIdentifier();

		String toDisplay = StringUtils.join(" / ", name, number, gid);

		pageHeader.setText(toDisplay);
	}

	private void updateMcpd()
	{
		AccessionService.Inst.get().getMcpd(Cookie.getRequestProperties(), accession.getId(), new DefaultAsyncCallback<ServerResult<Mcpd>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				super.onFailureImpl(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<Mcpd> result)
			{
				if (result.getServerResult() != null)
				{
					topWrapper.setVisible(true);
					new DescriptionWidget(mcpdPanel, Text.LANG.passportColumnGID(), accession.getGeneralIdentifier());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdAccename(), accession.getNumber());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdPuid(), accession.getPuid());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdAccenumb(), accession.getName());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCollnumb(), accession.getCollNumb());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCollcode(), accession.getCollCode());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCollname(), accession.getCollName());
					if (accession.getInstitution() != null)
					{
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdInstcode(), accession.getInstitution().getCode());
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCollinstaddress(), accession.getInstitution().getAddress());
					}
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCollmissid(), accession.getCollMissId());

					if (accession.getTaxonomy() != null)
					{
						if (!StringUtils.isEmpty(accession.getTaxonomy().getGenus()))
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdGenus(), "<i>" + accession.getTaxonomy().getGenus() + "</i>", true);
						if (!StringUtils.isEmpty(accession.getTaxonomy().getSpecies()))
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSpecies(), "<i>" + accession.getTaxonomy().getSpecies() + "</i>", true);

						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCropname(), accession.getTaxonomy().getCropName());
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSpauthor(), accession.getTaxonomy().getAuthor());
						if (accession.getSubtaxa() != null)
						{
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSubtaxa(), accession.getSubtaxa().getTaxonomyIdentifier());
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSubtauthor(), accession.getSubtaxa().getAuthor());
						}
					}

					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdAcqdate(), accession.getAcqDate());

					if (accession.getLocation() != null)
					{
						if (accession.getLocation().getCountry() != null)
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdOrigcty(), "<span class='" + Style.COUNTRY_FLAG + " " + accession.getLocation().getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "'></span>" + accession.getLocation().getCountry().getName(), true);

						if (accession.getLocation().getCoordinateUncertainty() != null)
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCoorduncert(), Integer.toString(accession.getLocation().getCoordinateUncertainty()));
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCoorddatum(), accession.getLocation().getCoordinateDatum());
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdGeorefmeth(), accession.getLocation().getGeoreferencingMethod());
					}

					if (accession.getCollDate() != null)
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdColldate(), DateUtils.getLocalizedDate(accession.getCollDate()));

					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdBredcode(), accession.getBreedersCode());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdBredname(), accession.getBreedersName());

					if (accession.getBiologicalStatus() != null)
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSampstat(), accession.getBiologicalStatus().getSampStat());

					if (accession.getCollSrc() != null)
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdCollsrc(), accession.getCollSrc().getCollSrc());

					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdDonorcode(), accession.getDonorCode());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdDonorname(), accession.getDonorName());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdDonornumb(), accession.getDonorNumber());

					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdOthernumb(), accession.getOtherNumb());

					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdDuplsite(), accession.getDuplSite());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdDuplinstname(), accession.getDuplInstName());

					if (!CollectionUtils.isEmpty(result.getServerResult().getStorage()))
					{
						for (Storage storage : result.getServerResult().getStorage())
						{
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdStorage(), storage.getDescription());
						}
					}

					if (accession.getMlsStatus() != null)
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdMlsstat(), accession.getMlsStatus().getDescription());

					if (result.getServerResult().getRemarks() != null)
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdRemarks(), result.getServerResult().getRemarks().getValue());
				}
			}
		});
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