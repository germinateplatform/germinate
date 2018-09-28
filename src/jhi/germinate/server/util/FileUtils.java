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

package jhi.germinate.server.util;

import java.io.*;
import java.net.*;

import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link FileUtils} is a utility class for {@link File} interactions
 *
 * @author Sebastian Raubach
 */
public class FileUtils
{
	public static File getFromExternalDataDirectory(FileLocation location, String localeSubFolder, ReferenceFolder folder, String filePath)
	{
		File result = null;
		String extra = "";

		if(folder != null)
			extra = folder.name();
		if(filePath != null)
			extra += File.separator + filePath;

		String externalFolder = PropertyWatcher.get(ServerProperty.GERMINATE_EXTERNAL_DATA_FOLDER);

		switch (location)
		{
			case data:
			case res:
			case download:
			case apps:
			case template:
				if (!StringUtils.isEmpty(externalFolder))
				{
					/* Remove tailing "/" or "\" */
					if (externalFolder.endsWith(File.separator))
						externalFolder = externalFolder.substring(0, externalFolder.length() - 1);

					File externalData = new File(new File(new File(externalFolder), location.name()), extra);
					File externalDataI18n = new File(new File(new File(externalFolder + "-" + localeSubFolder), location.name()), extra);

					if (externalDataI18n.exists() && !StringUtils.isEmpty(localeSubFolder))
					{
						result = externalDataI18n;
					}
					else if (externalData.exists())
					{
						result = externalData;
					}
				}
				break;
		}

		return result;
	}

	public static File getFromPath(FileLocation location, String localeSubFolder, ReferenceFolder folder, String filePath)
	{
		File result = null;
		String extra = folder == null ? filePath : folder.name() + File.separator + filePath;
		switch (location)
		{
			case data:
			case res:
			case download:
			case apps:
				/* Use the class loader to find the working directory */
				URL localData = FileUtils.class.getClassLoader().getResource(location.name() + File.separator + extra);
				URL localDataI18n = FileUtils.class.getClassLoader().getResource(location.name() + "-" + localeSubFolder + File.separator + extra);

				if (localDataI18n != null && !StringUtils.isEmpty(localeSubFolder))
				{
					try
					{
						result = new File(localDataI18n.toURI().getPath());
					}
					catch (URISyntaxException e)
					{
					}
				}
				else if (localData != null)
				{
					try
					{
						result = new File(localData.toURI().getPath());
					}
					catch (URISyntaxException e)
					{
					}
				}
				break;
		}

		return result;
	}

	/**
	 * Sets the last modify date of the given file to NOW
	 *
	 * @param file The file to modify
	 */
	public static void setLastModifyDateNow(File file)
	{
		if (file != null && file.exists() && file.isFile())
		{
			file.setLastModified(System.currentTimeMillis());
		}
	}
}
