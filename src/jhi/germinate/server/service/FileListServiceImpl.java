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
import java.util.stream.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link FileListServiceImpl} is the implementation of {@link FileListService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/file-list"})
public class FileListServiceImpl extends BaseRemoteServiceServlet implements FileListService
{
	private static final long serialVersionUID = -177872580557382217L;

	@Override
	public List<String> getForFolder(RequestProperties properties, FileLocation location, ReferenceFolder referenceFolder) throws InvalidSessionException
	{
		Session.checkSession(properties, this);

		File folder = getFileFolder(location, referenceFolder);
		File folderLocale = getFileFolder(location, properties.getLocale(), referenceFolder);

		List<String> result = new ArrayList<>();

        /* First add the localized files */
		if (folderLocale != null && folderLocale.exists() && folderLocale.isDirectory())
		{
			File[] files = folderLocale.listFiles();

			if (files != null)
			{
				result.addAll(Arrays.stream(files)
									.filter(File::isFile)
									.map(File::getName)
									.collect(Collectors.toList()));
			}
		}

		if (folder != null && folder.exists() && folder.isDirectory())
		{
			File[] files = folder.listFiles();
			if (files != null)
			{
				/* Then add the fallback files */
				result.addAll(Arrays.stream(files)
									.filter(file -> file.isFile() && !result.contains(file.getName()))
									.map(File::getName)
									.collect(Collectors.toList()));
			}
		}

		return result;
	}
}
