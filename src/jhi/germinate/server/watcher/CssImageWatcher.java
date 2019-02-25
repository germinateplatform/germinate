/*
 *  Copyright 2019 Information and Computational Sciences,
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

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

import javax.servlet.*;

import jhi.germinate.server.util.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link CssImageWatcher} creates thumbnails of all the inluded images. This way, the users don't have to do that themselves.
 *
 * @author Sebastian Raubach
 */
public class CssImageWatcher
{
	private static PropertyChangeListenerThread fileWatcher;
	private static Thread                       fileWatcherThread;
	private static WatchKey                     watchKey;
	private static File                         target;

	private static FilenameFilter filter = (dir, name) ->
	{
		/* Check valid image types */
		for (ImageMimeType extension : ImageMimeType.values())
		{
			if (name.toLowerCase().endsWith(extension.name()))
				return true;
		}

		return false;
	};
	private static File           source;


	public static void initialize(ServletContext context)
	{
		source = FileUtils.getFromExternalDataDirectory(FileLocation.download, null, ReferenceFolder.images, "css-images");
		target = new File(new File(new File(context.getRealPath(File.separator), "css"), "images"), "css-images");

		if (source != null && source.exists() && target != null && target.exists())
		{
			Logger.getLogger("").log(Level.INFO, "Setting up css-image watcher: " + source.getAbsolutePath() + " -> " + target.getAbsolutePath());
			Path sourcePath = source.toPath();
			FileSystem fs = sourcePath.getFileSystem();

			try
			{
				WatchService service = fs.newWatchService();
				/* start the file watcher thread below */
				fileWatcher = new PropertyChangeListenerThread(service);
				fileWatcherThread = new Thread(fileWatcher, "CssImageFileWatcher");
				fileWatcherThread.start();

				/* Register events */
				watchKey = sourcePath.register(service, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			copyImages();
		}
		else
		{
			Logger.getLogger("").log(Level.INFO, "Css-image watcher not set up. Source or target folder not present.");
		}
	}

	private static void copyImages(File... toCopy)
	{
		File[] images;

		if (toCopy != null && toCopy.length > 0)
			images = toCopy;
		else
			images = source.listFiles(filter);

		if (images != null)
		{
			Arrays.stream(images)
				  .forEach(f -> {
					  File t = new File(target, f.getName());

					  try
					  {
					  	  Logger.getLogger("").log(Level.INFO, "Copy css-image: " + f.getAbsolutePath() + " -> " + t.getAbsolutePath());
						  Files.copy(f.toPath(), t.toPath(), StandardCopyOption.REPLACE_EXISTING);
					  }
					  catch (IOException e)
					  {
						  e.printStackTrace();
					  }
				  });
		}
	}

	public static void stopFileWatcher()
	{
		if (watchKey != null)
			watchKey.cancel();
		if (fileWatcher != null)
			fileWatcher.stop();
		if (fileWatcherThread != null)
			fileWatcherThread.interrupt();
	}

	/**
	 * This Runnable is used to constantly attempt to take from the watch queue, and will receive all events that are registered with the fileWatcher
	 * it is associated.
	 */
	private static class PropertyChangeListenerThread implements Runnable
	{

		/** the watchService that is passed in from above */
		private WatchService watcher;

		private boolean stopped = false;

		PropertyChangeListenerThread(WatchService watcher)
		{
			this.watcher = watcher;
		}

		public void stop()
		{
			stopped = true;
		}

		/**
		 * In get to implement a file watcher, we loop forever ensuring requesting to take the next item from the file watchers queue.
		 */
		@Override
		public void run()
		{
			while (!stopped)
			{
				/* Wait for key to be signaled */
				WatchKey key;
				try
				{
					key = watcher.take();
				}
				catch (InterruptedException x)
				{
					return;
				}

				/*
				 * We have a polled event, now we traverse it and receive all
				 * the states from it
				 */
				for (WatchEvent<?> event : key.pollEvents())
				{
					WatchEvent.Kind<?> kind = event.kind();

					if (kind == StandardWatchEventKinds.OVERFLOW)
					{
						continue;
					}

					/*
					 * The filename is the context of the event
					 */
					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path file = ev.context();

					copyImages(file.toFile());
				}

				/*
				 * Reset the key -- this step is critical if you want to receive
				 * further watch events. If the key is no longer valid, the
				 * directory is inaccessible so exit the loop.
				 */
				boolean valid = key.reset();

				try
				{
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					throw new RuntimeException(e);
				}

				if (!valid)
				{
					break;
				}
			}
		}
	}
}
