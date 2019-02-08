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

package jhi.germinate.client.page.genotype;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.ListBox;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExportPage extends GerminateComposite implements HasHyperlinkButton, ParallaxBannerPage, HasHelp
{
	private FlowPanel resultPanel = new FlowPanel();

	@Override
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		panel.add(new DataExportWizard(DataExportWizard.ExportType.genotype)
		{
			@Override
			protected void onContinuePressed(List<Long> datasets, List<Long> accessionGroups, List<Long> markerGroups, List<Long> maps, boolean missingOn, boolean heterozygousOn)
			{
				GenotypeExportPage.this.onContinuePressed(datasets, accessionGroups, markerGroups, maps, missingOn, heterozygousOn);
			}
		});

		panel.add(resultPanel);
	}

	private void onContinuePressed(List<Long> datasets, List<Long> accessionGroups, List<Long> markerGroups, List<Long> maps, boolean missingOn, boolean heterozygousOn)
	{
		Long mapToUse = maps.size() > 0 ? maps.get(0) : null;

		GenotypeService.Inst.get().computeExportDataset(Cookie.getRequestProperties(), accessionGroups, markerGroups, datasets.get(0), heterozygousOn, missingOn, mapToUse,
				new DefaultAsyncCallback<ServerResult<List<CreatedFile>>>(true)
				{
					@Override
					public void onFailureImpl(Throwable caught)
					{
						if (caught instanceof InvalidArgumentException)
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
						else
							super.onFailureImpl(caught);
					}

					@Override
					public void onSuccessImpl(ServerResult<List<CreatedFile>> result)
					{
						if (result.hasData())
						{
							resultPanel.clear();

							if (!CollectionUtils.isEmpty(result.getServerResult()) && result.getServerResult().get(0) != null && result.getServerResult().get(1) != null)
							{
								final CreatedFile mapFile = result.getServerResult().get(0);
								final CreatedFile genotypeFile = result.getServerResult().get(1);

								resultPanel.add(new Heading(HeadingSize.H2, Text.LANG.genotypeResultTitleResult()));

								String linkToGalaxy = StringParameterStore.Inst.get().get(Parameter.GALAXY_URL);
								String toolId = StringParameterStore.Inst.get().get(Parameter.tool_id);
								StringParameterStore.Inst.get().remove(Parameter.GALAXY_URL);
								StringParameterStore.Inst.get().remove(Parameter.tool_id);

								if (!StringUtils.isEmpty(linkToGalaxy))
									linkToGalaxy += "?tool_id=" + toolId;

								List<DownloadWidget.FileConfig> files = new ArrayList<>();
								files.add(new DownloadWidget.FileConfig(FileLocation.temporary, Text.LANG.genotypeResultDownloadRaw(), genotypeFile).setStyle(FileType.IconStyle.IMAGE));
								files.add(new DownloadWidget.FileConfig(FileLocation.temporary, Text.LANG.genotypeResultDownloadMap(), mapFile).setStyle(FileType.IconStyle.IMAGE));
								files.add(new DownloadWidget.FileConfig(Text.LANG.genotypeResultDownloadFlapjack()).setType(FileType.flapjack).setStyle(FileType.IconStyle.IMAGE));

								if (!StringUtils.isEmpty(linkToGalaxy))
									files.add(new DownloadWidget.FileConfig(FileLocation.temporary, "Send to Galaxy", new CreatedFile(linkToGalaxy, null)).setStyle(FileType.IconStyle.IMAGE)); // TODO: i18n

								final String galaxyUrl = linkToGalaxy;

								DownloadWidget fileDownload = new DownloadWidget()
								{
									@Override
									protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
									{
										if (config.getName().equals(Text.LANG.genotypeResultDownloadFlapjack()))
										{
											exportToFlapjack(mapFile.getName(), genotypeFile.getName(), callback);
										}
										else if (config.getPath().equals(galaxyUrl))
										{
											event.preventDefault();

											String map = new ServletConstants.Builder()
													.setUrl(GWT.getModuleBaseURL())
													.setPath(ServletConstants.SERVLET_FILES)
													.setParam(ServletConstants.PARAM_FILE_PATH, mapFile.getName())
													.build();

											String data = new ServletConstants.Builder()
													.setUrl(GWT.getModuleBaseURL())
													.setPath(ServletConstants.SERVLET_FILES)
													.setParam(ServletConstants.PARAM_FILE_PATH, genotypeFile.getName())
													.build();

											FlowPanel formPanel = new FlowPanel();

											FormPanel form = new FormPanel(new NamedFrame("_self"));
											form.setAction(galaxyUrl);
											form.setWidget(formPanel);
											form.setVisible(false);
											form.setMethod(FormPanel.METHOD_POST);
											formPanel.add(new Hidden("genotypeMap", map));
											formPanel.add(new Hidden("genotypeData", data));
											resultPanel.add(form);

											CommonService.Inst.get().makeFilesAvailablePublically(Cookie.getRequestProperties(), ExperimentType.genotype, new AsyncCallback<Void>()
											{
												@Override
												public void onFailure(Throwable caught)
												{
													Notification.notify(Notification.Type.ERROR, Text.LANG.notificationUnspecifiedServerError());
												}

												@Override
												public void onSuccess(Void result)
												{
													form.submit();
												}
											});
										}
										else
										{
											super.onItemClicked(event, config, callback);
										}
									}
								};
								fileDownload.addAll(files);
								resultPanel.add(fileDownload);
							}
							else
							{
								resultPanel.add(new HTML(Text.LANG.genotypeResultTitleResult()));
								resultPanel.add(new HTML(Text.LANG.genotypeResultNoData()));
							}

							JavaScript.smoothScrollTo(resultPanel.getElement());
						}
					}
				});
	}

	private void exportToFlapjack(final String map, final String genotype, AsyncCallback<ServerResult<String>> callback)
	{
		GenotypeService.Inst.get().convertToFlapjack(Cookie.getRequestProperties(), map, genotype, new DefaultAsyncCallback<ServerResult<FlapjackProjectCreationResult>>()
		{
			@Override
			protected void onFailureImpl(Throwable caught)
			{
				callback.onFailure(caught);
			}

			@Override
			protected void onSuccessImpl(ServerResult<FlapjackProjectCreationResult> result)
			{
				callback.onSuccess(new ServerResult<>(result.getServerResult().getProjectFile().getName()));

				/* Show the deleted markers in a list */
				if (!CollectionUtils.isEmpty(result.getServerResult().getDeletedMarkers()))
				{
					SearchSection section = new SearchSection();
					section.setHeading(Text.LANG.genotypeResultDeletedMarkers());

					ListBox box = new ListBox();
					box.setVisibleItemCount(Math.min(20, result.getServerResult().getDeletedMarkers().size()));

					result.getServerResult().getDeletedMarkers().forEach(box::addItem);

					section.add(box);

					DownloadWidget widget = new DownloadWidget()
					{
						@Override
						protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
						{
							MarkerService.Inst.get().export(Cookie.getRequestProperties(), result.getServerResult().getDeletedMarkers(), callback);
						}
					};
					widget.add(new DownloadWidget.FileConfig(Text.LANG.downloadDeletedMarkersAsTxt()).setStyle(FileType.IconStyle.IMAGE));

					section.add(widget);

					resultPanel.add(section);
				}

				if (!StringUtils.isEmpty(result.getServerResult().getDebugOutput()))
				{
					SearchSection section = new SearchSection();
					section.setHeading(Text.LANG.genotypeResultFlapjackTitle());
					section.add(new HTML(Text.LANG.genotypeResultFlapjack().asString() + "<div class=" + Styles.WELL + ">" + result.getServerResult().getDebugOutput() + "</div>"));
					resultPanel.add(section);
				}
			}
		});
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.GENOTYPE_DATASETS)
				.addParam(Parameter.genotypeDatasetIds);
	}

	@Override
	public String getParallaxStyle()
	{
		return ParallaxResource.INSTANCE.css().parallaxGenotype();
	}

	@Override
	public Widget getHelpContent()
	{
		return new HTML(Text.LANG.genotypicExportHelp());
	}
}
