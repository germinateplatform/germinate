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

package jhi.germinate.client.util.callback;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetDownloadCallback extends SimpleCallback<Dataset>
{
	@Override
	public void onSuccess(Dataset dataset)
	{
		switch (dataset.getExperiment().getType())
		{
			case compound:
				downloadCompounds(dataset);
				break;
			case genotype:
				downloadGenotypes(dataset);
				break;
			case allelefreq:
				downloadAllelefreq(dataset);
				break;
			case trials:
				downloadTrials(dataset);
				break;
		}

		GerminateEventBus.BUS.fireEvent(new DatasetSelectionEvent(Collections.singletonList(dataset)));
	}

	public boolean isSupported(ExperimentType type)
	{
		switch (type)
		{
			case compound:
			case genotype:
			case allelefreq:
			case trials:
				return true;
			default:
				return false;
		}
	}

	private void invokeDownload(FileLocation folder, String filename)
	{
		/* If there is a result */
		if (!StringUtils.isEmpty(filename))
		{
			/* Create a new invisible dummy link on the page */
			ServletConstants.Builder builder = new ServletConstants.Builder()
					.setUrl(GWT.getModuleBaseURL())
					.setPath(ServletConstants.SERVLET_FILES)
					.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
					.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
					.setParam(ServletConstants.PARAM_FILE_PATH, filename);

			if (folder != null)
				builder.setParam(ServletConstants.PARAM_FILE_LOCATION, folder.name());

			String path = builder.build();

			GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), filename);

			/* Click it */
			JavaScript.invokeDownload(path);
		}
		else
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationNoDataFound());
		}
	}

	private void downloadGenotypes(Dataset dataset)
	{
		if (!StringUtils.isEmpty(dataset.getSourceFile()))
		{
			/* If we're dealing with an hdf5 file, convert it to tab-delimited */
			if (dataset.getSourceFile().endsWith(".hdf5"))
			{
				/* Start the export process */
				GenotypeService.Inst.get().convertHdf5ToText(Cookie.getRequestProperties(), dataset.getId(), new DefaultAsyncCallback<ServerResult<String>>(true)
				{
					@Override
					protected void onFailureImpl(Throwable caught)
					{
						if (caught instanceof InvalidArgumentException)
							Notification.notify(Notification.Type.ERROR, Text.LANG.notificationInsufficientPermissions());
						else
							super.onFailureImpl(caught);
					}

					@Override
					protected void onSuccessImpl(ServerResult<String> result)
					{
						invokeDownload(FileLocation.temporary, result.getServerResult());
					}
				});
			}
			else
			{
				invokeDownload(FileLocation.data, ReferenceFolder.genotype.name() + "/" + dataset.getSourceFile());
			}
		}
	}

	private void downloadAllelefreq(Dataset dataset)
	{
		invokeDownload(FileLocation.data, ReferenceFolder.allelefreq.name() + "/" + dataset.getSourceFile());
	}

	private void downloadCompounds(Dataset dataset)
	{
		/* Get the id of the selected dataset */
		List<Long> ids = new ArrayList<>();
		ids.add(dataset.getId());

		/* Start the export process */
		CompoundService.Inst.get().getExportFile(Cookie.getRequestProperties(), ids, null, null, null, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				invokeDownload(FileLocation.temporary, result.getServerResult());
			}
		});
	}

	private void downloadTrials(Dataset dataset)
	{
		/* Get the id of the selected dataset */
		List<Long> ids = new ArrayList<>();
		ids.add(dataset.getId());

		/* Start the export process */
		PhenotypeService.Inst.get().export(Cookie.getRequestProperties(), ids, null, null, null, new DefaultAsyncCallback<ServerResult<String>>(true)
		{
			@Override
			protected void onSuccessImpl(ServerResult<String> result)
			{
				invokeDownload(FileLocation.temporary, result.getServerResult());
			}
		});
	}
}
