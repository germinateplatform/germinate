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

package jhi.germinate.client.util.callback;

import com.google.gwt.core.client.*;
import com.google.gwt.i18n.client.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class FileDownloadCallback extends DefaultAsyncCallback<ServerResult<String>>
{
	public FileDownloadCallback(boolean longRunning)
	{
		super(longRunning);
	}

	@Override
	public void onFailureImpl(Throwable caught)
	{
		if (caught instanceof KMLException)
		{
			Notification.notify(Notification.Type.ERROR, Text.LANG.notificationKMLError());
		}
		else
		{
			super.onFailureImpl(caught);
		}
	}

	@Override
	public void onSuccessImpl(ServerResult<String> result)
	{
		/* If the file creation succeeded on the server */
		if (result.getServerResult() != null)
		{
			/* Create a new invisible dummy link on the page */
			String path = new ServletConstants.Builder()
					.setUrl(GWT.getModuleBaseURL())
					.setPath(ServletConstants.SERVLET_FILES)
					.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
					.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
					.setParam(ServletConstants.PARAM_FILE_PATH, result.getServerResult()).build();

			JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, FileLocation.temporary.name(), result.getServerResult());

			/* Click it */
			JavaScript.invokeDownload(path);
		}
	}
}
