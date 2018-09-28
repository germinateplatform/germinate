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

package jhi.germinate.server.watcher;

import org.apache.commons.io.monitor.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.servlet.*;

import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link TemplateWatcher} creates thumbnails of all the inluded images. This way, the users don't have to do that themselves.
 *
 * @author Sebastian Raubach
 */
public class TemplateWatcher
{
	private static Map<String, String>   filesToWatch = new HashMap<>();
	private static FileAlterationMonitor monitor;

	private static void setFilesToWatch(ServletContext context)
	{
		String base = context.getRealPath("/");

		filesToWatch.put("custom.css", base + "/css/custom.css");
		filesToWatch.put("custom.html", base + "/custom.html");
	}

	public static void initialize(ServletContext context)
	{
		setFilesToWatch(context);

		/* Start to listen for file changes within the full scale image folder */
		File folder = FileUtils.getFromExternalDataDirectory(FileLocation.template, null, null, null);

		Path path = folder.toPath();

		try
		{
			FileFilter filter = pathname -> filesToWatch.keySet().contains(pathname.getName());

			FileAlterationObserver observer = new FileAlterationObserver(path.toFile(), filter);
			monitor = new FileAlterationMonitor(1000L);
			observer.addListener(new FileAlterationListenerAdaptor()
			{
				@Override
				public void onFileChange(File file)
				{
					moveFiles(file);
				}
			});
			monitor.addObserver(observer);
			monitor.start();

			moveFiles(folder);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void moveFiles(final File... files)
	{
		for (File file : files)
		{
			if (file.isDirectory())
			{
				for (String source : filesToWatch.keySet())
				{
					File sourceFile = new File(file, source);

					copy(sourceFile);
				}
			}
			else
			{
				copy(file);
			}
		}
	}

	private static void copy(File source)
	{
		if(source.exists() && source.isFile())
		{
			String target = filesToWatch.get(source.getName());

			if (!StringUtils.isEmpty(target))
			{
				File targetFile = new File(target);

				try
				{
					Files.copy(source.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static void stopFileWatcher()
	{
		try
		{
			if (monitor != null)
				monitor.stop();

			monitor = null;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
