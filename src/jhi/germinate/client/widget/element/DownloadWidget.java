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

	private String           heading;
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
	public Library[] getLibraries()
	{
		return null;
	}

	@Override
	protected void setUpContent()
	{
		// Create an unordered list
		ulPanel = new ULPanel();
		// Style the list
		ulPanel.setStyleName(Style.WIDGET_UL_ICON_LIST);

		for (FileConfig config : files)
		{
			String name = config.getName();

			// If we know the filesize, append it so the user knows how big the file is
			if (config.getPath() != null && config.getPath().getSize() != null)
				name += " (" + NumberUtils.format(config.getPath().getSize(), true) + "B)";

			Anchor anchor = new Anchor(name);
			anchor.addClickHandler(event -> onItemClicked(event, config, new DefaultAsyncCallback<ServerResult<String>>(config.longRunning)
			{
				@Override
				protected void onSuccessImpl(ServerResult<String> result)
				{
					config.path = new CreatedFile(result.getServerResult(), null);
					String url = getLinkURL(config);

					// Track the click event
					GoogleAnalytics.trackEvent(GoogleAnalytics.Category.DOWNLOAD, config.getLocation().name(), config.getPath().getName());

					// Then, actually invoke the download
					JavaScript.invokeDownload(url);
				}
			}));

			ulPanel.add(anchor, config.getStyle(), config.getType(), config.longRunning);
		}

		if (!StringUtils.isEmpty(heading))
			panel.add(new Heading(HeadingSize.H3, heading));

		panel.add(ulPanel);
	}

	protected void onItemClicked(ClickEvent event, FileConfig config, AsyncCallback<ServerResult<String>> callback)
	{
		callback.onSuccess(new ServerResult<>(config.path.getName()));
	}

	/**
	 * Returns the link URL of the given {@link FileConfig}
	 *
	 * @param config The {@link FileConfig} of the file for which to return the URL
	 * @return The link URL of the given {@link FileConfig}
	 */
	protected String getLinkURL(FileConfig config)
	{
		String url = config.getPath().getName();

		// If it's an actual link, just return it
		if (url.startsWith("http") || url.startsWith("ftp"))
		{
			return url;
		}
		// Otherwise, it's a link to one of our files. Do the magic necessary to get the actual link URL
		else
		{
			return new ServletConstants.Builder().setUrl(GWT.getModuleBaseURL())
												 .setPath(ServletConstants.SERVLET_FILES)
												 .setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
												 .setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
												 .setParam(ServletConstants.PARAM_FILE_LOCATION, config.getLocation().name())
												 .setParam(ServletConstants.PARAM_FILE_PATH, url)
												 .build();
		}
	}

	public static class FileConfig
	{
		private String             name;
		private CreatedFile        path;
		private FileType           type;
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
		}

		public FileConfig(FileLocation location, String name, CreatedFile path)
		{
			this.location = location;
			this.name = name;
			this.path = path;
		}

		public FileConfig(FileLocation location, String name, CreatedFile path, FileType type, boolean longRunning)
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
			else if (path != null && !StringUtils.isEmpty(path.getName()))
			{
				String extension = path.getName().substring(path.getName().lastIndexOf(".") + 1);

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

		public CreatedFile getPath()
		{
			return path;
		}

		public FileConfig setPath(CreatedFile path)
		{
			this.path = path;
			return this;
		}
	}
}
