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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.*;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.*;

import org.gwtbootstrap3.client.ui.*;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.constants.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.page.*;
import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.ServletConstants.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link FileDownloadWidget} provides a download panel that can be added to any page. It will provide download functionality for the list of given
 * files. These files have to be actual files that already exist on the server side. <p/> For files that have to be generated on demand, use {@link
 * OnDemandFileDownloadWidget}.
 *
 * @author Sebastian Raubach
 */
public class FileDownloadWidget extends GerminateComposite
{
	public enum IconStyle
	{
		IMAGE,
		MDI
	}

	protected IconStyle iconStyle = IconStyle.IMAGE;
	protected FileLocation location;
	protected String       prefix;
	protected String       heading;
	protected List<String>   files = new ArrayList<>();
	protected List<String>   names = new ArrayList<>();
	protected List<FileType> types = new ArrayList<>();
	protected ULPanel ulPanel;
	protected boolean longRunning;

	public FileDownloadWidget()
	{
	}

	/**
	 * Creates a new download widget for file attachments
	 *
	 * @param location The type of the file location
	 * @param heading  The heading (<h3>HEADING</h3>)
	 * @param prefix   Optional prefix for the files
	 * @param files    The list of files
	 * @param names    The list of file names to display or <code>null</code>
	 * @param types    The list of {@link FileType}s
	 */
	public FileDownloadWidget(FileLocation location, String heading, String prefix, List<String> files, List<String> names, List<FileType> types, boolean longRunning)
	{
		this.location = location;
		this.heading = heading;
		this.prefix = prefix;
		this.files = files;
		this.names = names;
		this.types = types;
		this.longRunning = longRunning;
	}

	public FileLocation getLocation()
	{
		return location;
	}

	public FileDownloadWidget setLocation(FileLocation location)
	{
		this.location = location;
		return this;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public FileDownloadWidget setPrefix(String prefix)
	{
		this.prefix = prefix;
		return this;
	}

	public List<String> getFiles()
	{
		return files;
	}

	public FileDownloadWidget setFiles(List<String> files)
	{
		this.files = files;
		return this;
	}

	public FileDownloadWidget addFile(String file)
	{
		files.add(file);
		return this;
	}

	public List<String> getNames()
	{
		return names;
	}

	public FileDownloadWidget setNames(List<String> names)
	{
		this.names = names;
		return this;
	}

	public FileDownloadWidget addName(String name)
	{
		names.add(name);
		return this;
	}

	public String getHeading()
	{
		return heading;
	}

	public FileDownloadWidget setHeading(String heading)
	{
		this.heading = heading;
		return this;
	}

	public List<FileType> getTypes()
	{
		return types;
	}

	public FileDownloadWidget setTypes(List<FileType> types)
	{
		this.types = types;
		return this;
	}

	public FileDownloadWidget addType(FileType type)
	{
		types.add(type);
		return this;
	}

	public FileDownloadWidget setIconStyle(IconStyle iconStyle)
	{
		this.iconStyle = iconStyle;
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
		ulPanel.setStyleName(jhi.germinate.shared.Style.WIDGET_UL_ICON_LIST);

		for (int i = 0; i < files.size(); i++)
		{
			FlowPanel p = new FlowPanel();
			p.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
			/* Create the link to the result file */
			Anchor fileAnchor = new Anchor((CollectionUtils.isEmpty(names) || names.size() <= i) ? files.get(i) : names.get(i), getLinkURL(i));

			final int index = i;
			fileAnchor.addClickHandler(event ->
			{
				JavaScript.GoogleAnalytics.trackEvent(JavaScript.GoogleAnalytics.Category.DOWNLOAD, location.name(), files.get(index));
				onItemClicked(index, event);
			});

			String extension;
			FileType type;

			if (!CollectionUtils.isEmpty(types) && types.size() > i)
			{
				type = types.get(i);
			}
			else
			{
				extension = files.get(i).substring(files.get(i).lastIndexOf(".") + 1);

				try
				{
					type = FileType.valueOf(extension);
				}
				catch (Exception e)
				{
					type = FileType.unknown;
				}
			}

			p.add(fileAnchor);

			if (longRunning)
			{
				fileAnchor.getElement().getStyle().setMarginRight(15, Style.Unit.PX);
				p.add(new Label(LabelType.DEFAULT, Text.LANG.notificationLongRunning()));
			}

            /* Add it to the list and style it */
			ulPanel.add(p, iconStyle, type);
		}

		if (!StringUtils.isEmpty(heading))
			panel.add(new Heading(HeadingSize.H3, heading));
		panel.add(ulPanel);
	}

	/**
	 * Returns the link URL of the link at position <code>index</code>
	 *
	 * @param index The position of the link
	 * @return The link URL
	 */
	protected String getLinkURL(int index)
	{
		String prefixToUse = prefix == null ? "" : prefix + "/";

		return new Builder().setUrl(GWT.getModuleBaseURL())
							.setPath(ServletConstants.SERVLET_FILES)
							.setParam(ServletConstants.PARAM_SID, Cookie.getSessionId())
							.setParam(ServletConstants.PARAM_FILE_LOCALE, LocaleInfo.getCurrentLocale().getLocaleName())
							.setParam(ServletConstants.PARAM_FILE_LOCATION, location.name())
							.setParam(ServletConstants.PARAM_FILE_PATH, prefixToUse + files.get(index))
							.build();
	}

	/**
	 * Handles click events on the item at position <code>index</code>
	 *
	 * @param index The position of the link
	 * @param event The original {@link ClickEvent}. Call {@link ClickEvent#preventDefault()} to prevent the browser from following the link URL.
	 */
	protected void onItemClicked(int index, ClickEvent event)
	{

	}
}
