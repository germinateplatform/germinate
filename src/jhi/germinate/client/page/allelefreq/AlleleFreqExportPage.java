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

package jhi.germinate.client.page.allelefreq;

import com.google.gwt.user.client.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.Notification.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * The {@link AlleleFreqExportPage} shows the lists of accession groups, marker groups, maps and raw input files as well as the Crap Data Filter. This
 * page will redirect to {@link Page#ALLELE_FREQUENCY_RESULT} when the user clicks on continue.
 *
 * @author Sebastian Raubach
 * @see Parameter#allelefreqDatasetIds
 */
public class AlleleFreqExportPage extends GerminateComposite implements HasHyperlinkButton
{
	private static final int NR_OF_BINS = 500;

	/**
	 * Runs the actual extraction on the server and receives the relative path to the generated temporary files
	 */
	private void onContinuePressed(List<Long> datasets, List<Long> accessionGroups, List<Long> markerGroups, List<Long> maps, boolean missingOn)
	{
		Long mapToUse = maps.size() > 0 ? maps.get(0) : null;

		AlleleFrequencyService.Inst.get().createHistogram(Cookie.getRequestProperties(), accessionGroups, markerGroups, datasets.get(0), missingOn, mapToUse, NR_OF_BINS,
				new DefaultAsyncCallback<ServerResult<FlapjackAllelefreqBinningResult>>(true)
				{
					@Override
					public void onFailureImpl(Throwable caught)
					{
						if (caught instanceof InvalidArgumentException)
							Notification.notify(Type.ERROR, Text.LANG.notificationNoDataFound());
						else
							super.onFailureImpl(caught);
					}

					@Override
					public void onSuccessImpl(ServerResult<FlapjackAllelefreqBinningResult> result)
					{
						if (result != null)
						{
							DebugInfoParameterStore.Inst.get().put(Parameter.debugInfo, result.getDebugInfo());
							FlapjackAllelefreqBinningResultParameterStore.Inst.get().put(Parameter.flapjackExportResult, result.getServerResult());

							History.newItem(Page.ALLELE_FREQUENCY_RESULT.name());
						}
					}
				});
	}

	@Override
	public Library[] getLibraryList()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		panel.add(new DataExportWizard(DataExportWizard.ExportType.allelefreq)
		{
			@Override
			protected void onContinuePressed(List<Long> datasets, List<Long> accessionGroups, List<Long> markerGroups, List<Long> maps, boolean missingOn, boolean heterozygousOn)
			{
				AlleleFreqExportPage.this.onContinuePressed(datasets, accessionGroups, markerGroups, maps, missingOn);
			}
		});
	}

	@Override
	public HyperlinkPopupOptions getHyperlinkOptions()
	{
		return new HyperlinkPopupOptions()
				.setPage(Page.ALLELE_FREQUENCY_DATASET)
				.addParam(Parameter.allelefreqDatasetIds);
	}
}
