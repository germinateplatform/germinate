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

package jhi.germinate.client.widget.element;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.Anchor;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.callback.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class DownloadWidget extends GerminateComposite
{
	private ULPanel ulPanel;

	private String heading;
	private List<FileConfig> files = new ArrayList<>();

	public DownloadWidget()
	{
	}

	public DownloadWidget(String heading)
	{
		this.heading = heading;
	}

	public DownloadWidget(String heading, List<FileConfig> files)
	{
		this.heading = heading;
		this.files = files;
	}

	public DownloadWidget addAll(List<FileConfig> configs)
	{
		files.addAll(configs);
		return this;
	}

	public DownloadWidget add(FileConfig config)
	{
		files.add(config);
		return this;
	}

	@Override
	public Library[] getLibraryList()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		/* Create an unordered list */
		ulPanel = new ULPanel();
		/* Style the list */
		ulPanel.setStyleName(Style.WIDGET_UL_ICON_LIST);

		for (FileConfig config : files)
		{
			Anchor anchor = new Anchor(config.getName());
			anchor.addClickHandler(event -> {
				onItemClicked(event, config, new DefaultAsyncCallback<ServerResult<String>>(config.longRunning)
				{
					@Override
					protected void onSuccessImpl(ServerResult<String> result)
					{
						config.path = result.getServerResult();
						String url = getLinkURL(config);

						JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, config.getLocation().name(), config.getPath());

						/* Click it */
						JavaScript.invokeDownload(url);
					}
				});
			});

			ulPanel.add(anchor, config.getStyle(), config.getType(), config.longRunning);
		}

		if (!StringUtils.isEmpty(heading))
			panel.add(new Heading(HeadingSize.H3, heading));
		panel.add(ulPanel);
	}

	protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
	{
		callback.onSuccess(new ServerResult<>(config.path));
	}

	/**
	 * Returns the link URL of the link at position <code>index</code>
	 *
	 * @param index The position of the link
	 * @return The link URL
	 */
	protected String getLinkURL(FileConfig config)
	{
		String url = config.getPath();
		if (url.startsWith("http") || url.startsWith("ftp"))
			return url;

		return new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
											 .setPath(ServletConstants.SERVLET_FILES)
											 .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
											 .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
											 .setParam(ServletConstants.PARAM_FILE_LOCATION, config.getLocation().name())
											 .setParam(ServletConstants.PARAM_FILE_PATH, url)
											 .build();
	}

	public static class FileConfig
	{
		private String   name;
		private String   path;
		private FileType type;
		private FileLocation       location    = FileLocation.temporary;
		private FileType.IconStyle style       = FileType.IconStyle.MDI;
		private boolean            longRunning = false;

		public FileConfig()
		{
		}

		/**
		 * To be used for long running jobs where only the name is available
		 *
		 * @param name
		 */
		public FileConfig(String name)
		{
			this.name = name;
			this.longRunning = true;
		}

		public FileConfig(String name, FileType type)
		{
			this.name = name;
			this.type = type;
			this.longRunning = longRunning;
		}

		public FileConfig(FileLocation location, String name, String path)
		{
			this.location = location;
			this.name = name;
			this.path = path;
		}

		public FileConfig(FileLocation location, String name, String path, FileType type, boolean longRunning)
		{
			this.location = location;
			this.name = name;
			this.path = path;
			this.type = type;
			this.longRunning = longRunning;
		}

		public FileType.IconStyle getStyle()
		{
			return style;
		}

		public FileConfig setStyle(FileType.IconStyle style)
		{
			this.style = style;
			return this;
		}

		public boolean isLongRunning()
		{
			return longRunning;
		}

		public FileConfig setLongRunning(boolean longRunning)
		{
			this.longRunning = longRunning;
			return this;
		}

		public FileType getType()
		{
			if (type != null)
			{
				return type;
			}
			else if (!StringUtils.isEmpty(path))
			{
				String extension = path.substring(path.lastIndexOf(".") + 1);

				try
				{
					type = FileType.valueOf(extension);
				}
				catch (Exception e)
				{
					type = FileType.unknown;
				}

				return type;
			}
			else
			{
				return FileType.unknown;
			}
		}

		public FileConfig setType(FileType type)
		{
			this.type = type;
			return this;
		}

		public FileLocation getLocation()
		{
			return location;
		}

		public FileConfig setLocation(FileLocation location)
		{
			this.location = location;
			return this;
		}

		public String getName()
		{
			return name;
		}

		public FileConfig setName(String name)
		{
			this.name = name;
			return this;
		}

		public String getPath()
		{
			return path;
		}

		public FileConfig setPath(String path)
		{
			this.path = path;
			return this;
		}
	}
}
