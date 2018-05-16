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

import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.server.rpc.*;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import jhi.germinate.server.config.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link BaseRemoteServiceServlet} checks before each call if the system is undergoing maintenance. If so, it will throw a {@link
 * SystemUnderMaintenanceException}.
 *
 * @author Sebastian Raubach
 */
public class BaseRemoteServiceServlet extends RemoteServiceServlet
{
	private static final long serialVersionUID = 6652635089572873941L;

	@Override
	public String processCall(String payload) throws SerializationException
	{
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_UNDER_MAINTENANCE))
			return RPC.encodeResponseForFailure(null, new SystemUnderMaintenanceException());
		else
			return super.processCall(payload);
	}

	public HttpServletRequest getRequest()
	{
		return getThreadLocalRequest();
	}

	public HttpServletResponse getResponse()
	{
		return getThreadLocalResponse();
	}

	protected void storeInSession(String key, Object object)
	{
		getRequest().getSession().setAttribute(key, object);
	}

	protected Object getFromSession(String key)
	{
		return getRequest().getSession().getAttribute(key);
	}

	/**
	 * Returns the file based on the location
	 *
	 * @param location The file's location
	 * @param filePath The file path starting from this location
	 * @return The absolute path
	 */
	public File getFile(FileLocation location, String filePath)
	{
		return getFile(location, null, filePath);
	}

	/**
	 * Returns the file based on the location
	 *
	 * @param location        The file's location
	 * @param localeSubFolder The locale based subfolder of the {@link FileLocation}
	 * @param filePath        The file path starting from this location
	 * @return The absolute path
	 */
	public File getFile(FileLocation location, String localeSubFolder, String filePath)
	{
		return getFile(location, localeSubFolder, null, filePath);
	}

	/**
	 * Returns the file based on the location
	 *
	 * @param location        The file's location
	 * @param localeSubFolder The locale based subfolder of the {@link FileLocation}
	 * @param folder          The {@link ReferenceFolder} within the {@link FileLocation}
	 * @param filePath        The file path starting from this location
	 * @return The absolute path
	 */
	public File getFile(FileLocation location, String localeSubFolder, ReferenceFolder folder, String filePath)
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
				result = new File(getTemporaryFileFolder(), filePath);
		}

		return result;
	}

	/**
	 * Returns the absolute path to the given file based on the location
	 *
	 * @param location The file's location
	 * @param folder   The {@link ReferenceFolder}
	 * @return The absolute path
	 */
	public File getFileFolder(FileLocation location, ReferenceFolder folder)
	{
		return getFileFolder(location, null, folder);
	}

	/**
	 * Returns the absolute path to the given file based on the location
	 *
	 * @param location        The file's location
	 * @param localeSubFolder The locale based subfolder of the {@link FileLocation}
	 * @param folder          The {@link ReferenceFolder}
	 * @return The absolute path
	 */
	public File getFileFolder(FileLocation location, String localeSubFolder, ReferenceFolder folder)
	{
		return getFile(location, localeSubFolder, folder, "");
	}

	/**
	 * Returns the path to the temporary file folder of the application
	 *
	 * @return The path to the temporary file folder of the application
	 */
	public File getTemporaryFileFolder()
	{
		makeSureTempFolderExists();

		return new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(getRequest()));
	}

	/**
	 * Returns a file object created in the temporary folder with a random file name. This file is guaranteed to be unique during the time of
	 * creation, i.e. there is no other file in the temporary directory with this name.
	 *
	 * @param prefix    An optional prefix to simplify identification in the folder
	 * @param ids       A {@link List} of database ids that need to be part of the generated filename
	 * @param extension The file extension to use
	 * @return The file object
	 */
	public File createTemporaryFile(String prefix, List<Long> ids, String extension)
	{
		prefix += "_dataset-" + CollectionUtils.join(ids, "-");

		return createTemporaryFile(prefix, extension);
	}

	/**
	 * Returns a file object created in the temporary folder with a random file name. This file is guaranteed to be unique during the time of
	 * creation, i.e. there is no other file in the temporary directory with this name.
	 *
	 * @param prefix    An optional prefix to simplify identification in the folder
	 * @param id        The database id that needs to be part of the generated filename
	 * @param extension The file extension to use
	 * @return The file object
	 */
	public File createTemporaryFile(String prefix, Long id, String extension)
	{
		prefix += "_dataset-" + id;

		return createTemporaryFile(prefix, extension);
	}

	/**
	 * Returns a file object created in the temporary folder with a random file name. This file is guaranteed to be unique during the time of
	 * creation, i.e. there is no other file in the temporary directory with this name.
	 *
	 * @param prefix    An optional prefix to simplify identification in the folder
	 * @param extension The file extension to use
	 * @return The file object
	 */
	public File createTemporaryFile(String prefix, String extension)
	{
		makeSureTempFolderExists();

		File file;

		extension = extension.replace(".", "");

		do
		{
			file = new File(new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(getRequest())), prefix + "_" + UUID.randomUUID() + "." + extension);
		} while (file.exists());

		return file;
	}

	/**
	 * Makes sure that the temporary folder of this instance exists
	 */
	private void makeSureTempFolderExists()
	{
		File file = new File(System.getProperty("java.io.tmpdir") + PropertyReader.getContextPath(getRequest()) + File.separator);

		if (!file.exists())
			file.mkdirs();
	}

	protected static boolean containsAllItemsGroup(List<Long> ids)
	{
		if (CollectionUtils.isEmpty(ids))
			return false;
		else
			return ids.stream().filter(id -> id < 1).count() > 0;
	}
}
