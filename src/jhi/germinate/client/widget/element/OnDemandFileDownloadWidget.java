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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.Notification.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.ServletConstants.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link OnDemandFileDownloadWidget} provides a download panel that can be added to any page. It will provide download functionality of a given file
 * type. The callback will be called when the user clicks on the link. When the callback returns, the widget will create an anchor with the created
 * file as the target and click it programmatically. <p/> For static files that don't have to be generated on the server side, use {@link
 * FileDownloadWidget}
 *
 * @author Sebastian Raubach
 */
public class OnDemandFileDownloadWidget extends FileDownloadWidget
{
	private final OnDemandCallback callback;

	public OnDemandFileDownloadWidget(OnDemandCallback callback, boolean longRunning)
	{
		super(FileLocation.temporary, Text.LANG.downloadHeading(), null, new ArrayList<>(), null, new ArrayList<>(), longRunning);
		this.callback = callback;
	}

	/**
	 * Creates an on demand download widget that will start file creation on link click
	 *
	 * @param heading     The HTML to show above the table
	 * @param linkText    The array of texts for the links
	 * @param longRunning Is it a long running task?
	 * @param type        The array of type of the files (used for the mime icon symbol)
	 * @param callback    The callback that will be called when the user clicks on the link
	 */
	public OnDemandFileDownloadWidget(String heading, List<String> linkText, boolean longRunning, List<FileType> type, OnDemandCallback callback)
	{
		super(FileLocation.temporary, heading, null, linkText, linkText, type, longRunning);
		this.callback = callback;
	}

	@Override
	public Library[] getLibraryList()
	{
		return null;
	}

	@Override
	protected String getLinkURL(int index)
	{
		return "";
	}

	@Override
	protected void onItemClicked(int index, ClickEvent event)
	{
		event.preventDefault();
		Downloader.download(index, longRunning, callback);
	}

	/**
	 * Downloader is a class that takes care of the actual download
	 *
	 * @author Sebastian Raubach
	 */
	public static class Downloader
	{
		/**
		 * Called to initiate the download process
		 *
		 * @param index       The index of the file to download
		 * @param longRunning Is this a long running process?
		 * @param callback    The {@link OnDemandCallback} that is used to get the data
		 */
		public static void download(int index, boolean longRunning, OnDemandCallback callback)
		{
			/* Set up the callback object for the result file */
			callback.queryServer(index, new DefaultAsyncCallback<ServerResult<String>>(longRunning)
			{
				@Override
				public void onFailureImpl(Throwable caught)
				{
					if (caught instanceof KMLException)
					{
						Notification.notify(Type.ERROR, Text.LANG.notificationKMLError());
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
						String path = new Builder()
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
			});
		}
	}

	/**
	 * The callback that is called to retrieve the data from the server
	 *
	 * @author Sebastian Raubach
	 */
	public interface OnDemandCallback
	{
		/**
		 * Queries the server for the download file containing the demanded data for the link at position <code>index</code>
		 *
		 * @param index    The position in the {@link OnDemandFileDownloadWidget}'s list
		 * @param callback The callback
		 */
		void queryServer(int index, DefaultAsyncCallback<ServerResult<String>> callback);
	}
}
