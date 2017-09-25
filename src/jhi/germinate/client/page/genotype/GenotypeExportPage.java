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

package jhi.germinate.client.page.genotype;

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
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class GenotypeExportPage extends GerminateComposite implements HasHyperlinkButton
{
	private FlowPanel resultPanel = new FlowPanel();

	@Override
	public Library[] getLibraryList()
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
				new DefaultAsyncCallback<ServerResult<FlapjackProjectCreationResult>>(true)
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
					public void onSuccessImpl(ServerResult<FlapjackProjectCreationResult> result)
					{
						if (result.getServerResult() != null)
						{
							resultPanel.clear();

							if (!StringUtils.isEmpty(result.getServerResult().getRawDataFile(), result.getServerResult().getProjectFile()))
							{
								resultPanel.add(new Heading(HeadingSize.H3, Text.LANG.genotypeResultTitleResult()));
								resultPanel.add(new HTML(Text.LANG.genotypeResultParagraphOne()));
								resultPanel.add(new HTML(Text.LANG.genotypeResultParagraphTwo()));

								List<String> files = new ArrayList<>();
								files.add(result.getServerResult().getRawDataFile());
								files.add(result.getServerResult().getMapFile());
								files.add(result.getServerResult().getProjectFile());

								List<String> names = new ArrayList<>();
								names.add(Text.LANG.genotypeResultDownloadRaw());
								names.add(Text.LANG.genotypeResultDownloadMap());
								names.add(Text.LANG.genotypeResultDownloadFlapjack());

								FileDownloadWidget fileDownload = new FileDownloadWidget(FileLocation.temporary, Text.LANG.downloadHeading(), null, files, names, null, true);

								resultPanel.add(fileDownload);

            					/* Show the deleted markers in a list */
								if (!CollectionUtils.isEmpty(result.getServerResult().getDeletedMarkers()))
								{
									SearchSection section = new SearchSection();
									section.setHeading(Text.LANG.genotypeResultDeletedMarkers());

									ListBox box = new ListBox();
									box.setVisibleItemCount(Math.min(20, result.getServerResult().getDeletedMarkers().size()));

									result.getServerResult().getDeletedMarkers().forEach(box::addItem);

									section.add(box);

									FileDownloadWidget widget = new OnDemandFileDownloadWidget((index, callback) -> MarkerService.Inst.get().export(Cookie.getRequestProperties(), result.getServerResult().getDeletedMarkers(), callback), false)
											.setIconStyle(FileDownloadWidget.IconStyle.FONT_AWESOME)
											.setHeading(null)
											.addFile(Text.LANG.downloadDeletedMarkersAsTxt())
											.addType(FileType.txt);

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

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.GENOTYPE_EXPORT)
				.addParam(Parameter.genotypeDatasetIds);
	}
}
