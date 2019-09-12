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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;
import java.util.Locale;
import java.util.stream.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.d3js.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.gallery.*;
import jhi.germinate.client.widget.map.*;
import jhi.germinate.client.widget.table.pagination.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Pagination;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Image;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * The {@link PassportWidget} shows all the information about a single {@link Accession}.
 *
 * @author Sebastian Raubach
 * @see Parameter#accessionId
 * @see Parameter#accessionName
 */
public class PassportWidget extends Composite
{
	private static PassportPageUiBinder ourUiBinder = GWT.create(PassportPageUiBinder.class);
	protected      Accession            accession;
	@UiField
	MarkedItemPageHeader pageHeader;
	@UiField
	HTML                 html;
	@UiField
	FlowPanel            pdciWrapper;
	@UiField
	HTML                 pdci;
	@UiField
	Anchor               pdciInfo;

	@UiField
	Row topWrapper;

	@UiField
	FlowPanel mcpdPanel;
	@UiField
	FlowPanel institutionPanel;

	@UiField
	FlowPanel     synonymsWrapper;
	@UiField(provided = true)
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
	FlowPanel   pedigreeChartPanel;
	@UiField
	SimplePanel pedigreeChart;

	@UiField
	FlowPanel   entityWrapper;
	@UiField
	SimplePanel entityTablePanel;

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
	@UiField(provided = true)
	LinkWidget linkWidget;

	@UiField
	FlowPanel   downloadWrapper;
	@UiField
	SimplePanel downloadPanel;

	public PassportWidget(Accession accession)
	{
		this.accession = accession;

		synonyms = new SynonymWidget()
		{
			@Override
			protected void onDataChanged()
			{
				if (!synonymsWrapper.isVisible())
					synonymsWrapper.setVisible(true);
			}
		};
		linkWidget = new LinkWidget()
		{
			@Override
			protected void onDataChanged()
			{
				if (!linkWrapper.isVisible())
					linkWrapper.setVisible(true);
			}
		};

		initWidget(ourUiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad()
	{
		html.setHTML(Text.LANG.passportText());
		groupHtml.setHTML(Text.LANG.passportGroupsOverviewText());
		datasetHtml.setHTML(Text.LANG.passportDatasetsOverviewText());

		updateContent();
	}

	protected void updateContent()
	{
		if (accession != null)
		{
			updateHeader();
			updateMcpd();
			updateInstitution();
			updateSynonyms();
			updatePedigree();
			updateEntityData();
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
		FileListService.Inst.get().getForFolder(Cookie.getRequestProperties(), FileLocation.download, ReferenceFolder.passport, new DefaultAsyncCallback<List<CreatedFile>>()
		{
			@Override
			public void onSuccessImpl(List<CreatedFile> files)
			{
				if (!CollectionUtils.isEmpty(files))
				{
					downloadWrapper.setVisible(true);

					List<DownloadWidget.FileConfig> conf = files.stream()
																.map(s -> {
																	String orig = s.getName();
																	s.setName(ReferenceFolder.passport.name() + "/" + orig);
																	return new DownloadWidget.FileConfig()
																			.setLocation(FileLocation.download)
																			.setName(orig)
																			.setPath(s);
																})
																.collect(Collectors.toList());

					downloadPanel.add(new DownloadWidget().addAll(conf));
				}
			}
		});
	}

	protected void updateExternalLinks()
	{
		linkWidget.update(GerminateDatabaseTable.germinatebase, accession.getId());
	}

	protected void updateComments()
	{
		commentWrapper.setVisible(true);
		commentWidget.update(accession.getId(), GerminateDatabaseTable.germinatebase);
	}

	protected void updateAttributes()
	{
		attributePanel.add(new AttributeDataForAccessionTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected void onDataChanged()
			{
				if (!attributeWrapper.isVisible())
					attributeWrapper.setVisible(true);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<AttributeData>>> callback)
			{
				if (filter == null)
					filter = new PartialSearchQuery();
				SearchCondition condition = new SearchCondition(Accession.ID, new Equal(), Long.toString(accession.getId()), Long.class);
				filter.add(condition);

				if (filter.getAll().size() > 1)
					filter.addLogicalOperator(new And());

				return AttributeService.Inst.get().getDataForFilter(Cookie.getRequestProperties(), pagination, GerminateDatabaseTable.germinatebase, filter, callback);
			}
		});
	}

	protected void updateDatasets()
	{
		DatasetTable datasetTable = new DatasetTable(DatasetTable.SelectionMode.NONE, true, true)
		{
			@Override
			protected void onDataChanged()
			{
				if (!datasetWrapper.isVisible())
					datasetWrapper.setVisible(true);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Dataset>>> callback)
			{
				return DatasetService.Inst.get().getForAccession(Cookie.getRequestProperties(), accession.getId(), pagination, callback);
			}
		};
		datasetTable.setShowDownload(true);
		datasetPanel.add(datasetTable);
	}

	protected void updateGroups()
	{
		groupPanel.add(new GroupTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected void onDataChanged()
			{
				if (!groupWrapper.isVisible())
					groupWrapper.setVisible(true);
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<Group>>> callback)
			{
				return GroupService.Inst.get().getForAccession(Cookie.getRequestProperties(), accession.getId(), pagination, callback);
			}
		});
	}

	protected void updateImages()
	{
		imagePanel.add(new Gallery(false, false)
		{
			@Override
			protected void onDataChanged()
			{
				if (!imageWrapper.isVisible())
					imageWrapper.setVisible(true);
			}

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
		PedigreeService.Inst.get().getPedigreeDefinitions(Cookie.getRequestProperties(), accession.getId(), new DefaultAsyncCallback<ServerResult<List<PedigreeDefinition>>>()
		{
			@Override
			protected void onSuccessImpl(ServerResult<List<PedigreeDefinition>> result)
			{
				if (result.hasData())
				{
					pedigreeWrapper.setVisible(true);
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
			boolean chartInitialized = false;

			@Override
			protected void onDataChanged()
			{
				if (!pedigreeWrapper.isVisible())
					pedigreeWrapper.setVisible(true);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return false;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<Pedigree>>> callback)
			{
				filter = new PartialSearchQuery();

				SearchCondition condition = new SearchCondition("Child.id", new Equal(), Long.toString(accession.getId()), Long.class);
				filter.add(condition);

				filter.addLogicalOperator(new Or());

				condition = new SearchCondition("Parent.id", new Equal(), Long.toString(accession.getId()), Long.class);
				filter.add(condition);

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

						if (!chartInitialized && result.getResultSize() > 0)
						{
							pedigreeChart.add(new PedigreeChart(accession.getId()));
							chartInitialized = true;
						}
						else
						{
							pedigreeChartPanel.clear();
						}
					}
				});


			}
		});

		DownloadWidget widget = new DownloadWidget()
		{
			@Override
			protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
			{
				PedigreeService.Inst.get().exportToHelium(Cookie.getRequestProperties(), Collections.singletonList(accession.getId()), Pedigree.PedigreeQuery.UP_DOWN_RECURSIVE, callback);
			}
		};
		widget.add(new DownloadWidget.FileConfig(Text.LANG.downloadPedigreeHelium())
				.setType(FileType.helium)
				.setLongRunning(true)
				.setStyle(FileType.IconStyle.IMAGE));
		pedigreeDownloadPanel.add(widget);
	}

	private void updateEntityData()
	{
		entityTablePanel.add(new EntityPairTable(DatabaseObjectPaginationTable.SelectionMode.NONE, true)
		{
			@Override
			protected void onDataChanged()
			{
				if (!entityWrapper.isVisible())
					entityWrapper.setVisible(true);
			}

			@Override
			protected boolean supportsFiltering()
			{
				return false;
			}

			@Override
			protected Request getData(Pagination pagination, PartialSearchQuery filter, final AsyncCallback<PaginatedServerResult<List<EntityPair>>> callback)
			{
				return AccessionService.Inst.get().getEntityPairs(Cookie.getRequestProperties(), accession.getId(), pagination, callback);
			}
		});
	}

	private void updateSynonyms()
	{
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
				JavaScript.click(anchor, new ClickCallback()
				{
					@Override
					public void onSuccess(Event event)
					{
						LongParameterStore.Inst.get().put(Parameter.institutionId, institution.getId());
						History.newItem(Page.INSTITUTIONS.name());
					}
				});

				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnName(), anchor, true);
			}
			else
			{
				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnName(), institution.getName());
			}

			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnAcronym(), institution.getAcronym());
			new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnCode(), institution.getCode());

			if (institution.getCountry() != null)
				new DescriptionWidget(institutionPanel, Text.LANG.institutionsColumnCountry(), "<span class='" + Style.COUNTRY_FLAG + " " + institution.getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "'></span><span class='" + Style.LAYOUT_V_ALIGN_MIDDLE + "'>" + institution.getCountry().getName() + "</span>", true);

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
		pageHeader.setSubText(accession.getEntityType().getName());
		pageHeader.setType(MarkedItemList.ItemType.ACCESSION);
		pageHeader.setId(Long.toString(accession.getId()));

		if (!GerminateSettingsHolder.get().pdciEnabled.getValue() || accession.getPdci() == null)
		{
			pdciWrapper.removeFromParent();
		}
		else
		{
			pdciWrapper.setVisible(true);
			pdci.setHTML(Text.LANG.passportPDCIScore(NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(accession.getPdci())));
		}
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
				if (result.hasData())
				{
					topWrapper.setVisible(true);
					new DescriptionWidget(mcpdPanel, Text.LANG.passportColumnGID(), accession.getGeneralIdentifier());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdAccename(), accession.getName());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdPuid(), accession.getPuid());
					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdAccenumb(), accession.getNumber());
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
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSpauthor(), accession.getTaxonomy().getTaxonomyAuthor());
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSubtaxa(), accession.getTaxonomy().getSubtaxa());
						new DescriptionWidget(mcpdPanel, Text.LANG.mcpdSubtauthor(), accession.getTaxonomy().getSubtaxaAuthor());
					}

					new DescriptionWidget(mcpdPanel, Text.LANG.mcpdAcqdate(), accession.getAcqDate());

					if (accession.getLocation() != null)
					{
						if (accession.getLocation().getCountry() != null)
							new DescriptionWidget(mcpdPanel, Text.LANG.mcpdOrigcty(), "<span class='" + Style.COUNTRY_FLAG + " " + accession.getLocation().getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "'></span><span class='" + Style.LAYOUT_V_ALIGN_MIDDLE + "'>" + accession.getLocation().getCountry().getName() + "</span>", true);

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

	@UiHandler("pdciInfo")
	void onPdcdiInfoClicked(ClickEvent e)
	{
		new AlertDialog(Text.LANG.passportPDCITitle(), new HTML(Text.LANG.passportPDCIExplanation()))
				.setPositiveButtonConfig(new AlertDialog.ButtonConfig(Text.LANG.generalDone(), Style.MDI_CHECK, null))
				.open();
	}

	interface PassportPageUiBinder extends UiBinder<HTMLPanel, PassportWidget>
	{
	}
}