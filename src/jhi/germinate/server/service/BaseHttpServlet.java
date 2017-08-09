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

package jhi.germinate.server.service;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public abstract class BaseHttpServlet extends HttpServlet
{
	protected void error(HttpServletResponse resp, int errorCode, String errorMessage) throws IOException
	{
		resp.setStatus(errorCode);
		resp.setContentLength(errorMessage.length());

		OutputStream out = resp.getOutputStream();
		out.write(errorMessage.getBytes());
		out.close();
	}

	/**
	 * Returns the file based on the location
	 *
	 * @param req             The {@link HttpServletRequest}
	 * @param location        The file's location
	 * @param localeSubFolder The locale based subfolder of the {@link FileLocation}
	 * @param filePath        The file path starting from this location
	 * @return The absolute path
	 */
	public static File getFile(HttpServletRequest req, FileLocation location, String localeSubFolder, String filePath)
	{
		return getFile(req, location, localeSubFolder, null, filePath);
	}

	/**
	 * Returns the file based on the location
	 *
	 * @param req             The {@link HttpServletRequest}
	 * @param location        The file's location
	 * @param localeSubFolder The locale based subfolder of the {@link FileLocation}
	 * @param folder          The {@link ReferenceFolder} within the {@link FileLocation}
	 * @param filePath        The file path starting from this location
	 * @return The absolute path
	 */
	public static File getFile(HttpServletRequest req, FileLocation location, String localeSubFolder, ReferenceFolder folder, String filePath)
	{
		File result;
		switch (location)
		{
			case data:
			case res:
			case download:
			case apps:

				File externalFile = FileUtils.getFromExternalDataDirectory(location, localeSubFolder, folder, filePath);
				File resourceFile = FileUtils.getFromPath(location, localeSubFolder, folder, filePath);

				if (externalFile != null && externalFile.exists())
					result = externalFile;
				else
					result = resourceFile;

				break;

			case temporary:
			default:
				result = new File(getTemporaryFileFolder(req), filePath);
		}

		return result;
	}

	/**
	 * Returns the path to the temporary file folder of the application
	 *
	 * @param req The {@link HttpServletRequest}
	 * @return The path to the temporary file folder of the application
	 */
	public static File getTemporaryFileFolder(HttpServletRequest req)
	{
		makeSureTempFolderExists(req);

		return new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(req));
	}

	/**
	 * Returns a file object created in the temporary folder with a random file name. This file is guaranteed to be unique during the time of
	 * creation, i.e. there is no other file in the temporary directory with this name.
	 *
	 * @param req       The {@link HttpServletRequest}
	 * @param prefix    An optional prefix to simplify identification in the folder
	 * @param extension The file extension to use
	 * @return The file object
	 */
	public static File createTemporaryFile(HttpServletRequest req, String prefix, String extension)
	{
		makeSureTempFolderExists(req);

		File file;

		extension = extension.replace(".", "");

		do
		{
			file = new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(req) + File.separator + prefix + "_" + UUID.randomUUID() + "." + extension);
		} while (file.exists());

		return file;
	}

	/**
	 * Makes sure that the temporary folder of this instance exists
	 *
	 * @param req The request
	 */
	private static void makeSureTempFolderExists(HttpServletRequest req)
	{
		File file = new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(req) + File.separator);

		if (!file.exists())
			file.mkdirs();
	}
}
