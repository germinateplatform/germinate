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

import net.coobird.thumbnailator.*;
import net.coobird.thumbnailator.geometry.*;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import jhi.germinate.server.config.*;
import jhi.germinate.server.service.*;
import jhi.germinate.shared.enums.*;

import static jhi.germinate.shared.enums.ServerProperty.*;

/**
 * {@link ImageScaler} creates thumbnails of all the inluded images. This way, the users don't have to do that themselves.
 *
 * @author Sebastian Raubach
 */
class ImageScaler
{
	private static PropertyChangeListenerThread fileWatcher;
	private static Thread                       fileWatcherThread;
	private static WatchKey                     watchKey;

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

	public static void initialize()
	{
		List<File> imageFolders = new ArrayList<>();

		/* Start to listen for file changes within the full scale image folder */
		File internalImageFolder = FileUtils.getFromPath(FileLocation.download, null, ReferenceFolder.images, ImageServlet.FULL_SIZE);
		File externalImageFolder = FileUtils.getFromExternalDataDirectory(FileLocation.download, null, ReferenceFolder.images, ImageServlet.FULL_SIZE);

		if (internalImageFolder != null && internalImageFolder.exists())
			imageFolders.add(internalImageFolder);
		if (externalImageFolder != null && externalImageFolder.exists())
			imageFolders.add(externalImageFolder);

		int counter = 0;
		for (File folder : imageFolders)
		{
			Path fullSizeFolder = folder.toPath();
			FileSystem fs = fullSizeFolder.getFileSystem();

			try
			{
				WatchService service = fs.newWatchService();
				/* start the file watcher thread below */
				fileWatcher = new PropertyChangeListenerThread(fullSizeFolder.toFile().getParentFile(), service);
				fileWatcherThread = new Thread(fileWatcher, "ImageFileWatcher-" + counter++);
				fileWatcherThread.start();

            	/* Register events */
				watchKey = fullSizeFolder.register(service, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}

			scaleImages(fullSizeFolder.toFile().getParentFile());
		}
	}

	private static void scaleImages(final File folder)
	{
		File fullsize = new File(folder, ImageServlet.FULL_SIZE);
		File thumbnails = new File(folder, ImageServlet.THUMBNAILS);

		if (!thumbnails.exists())
			thumbnails.mkdirs();

		File[] fullSizeImages = fullsize.listFiles(filter);
		File[] thumbnailImages = thumbnails.listFiles(filter);

		/* Get just the names of the existing thumbnails */
		List<String> thumbnailNames = Arrays.stream(thumbnailImages)
											.map(File::getName)
											.collect(Collectors.toList());

		/* Stream over the fullsize images */
		Arrays.stream(fullSizeImages)
			  .filter(file -> !thumbnailNames.contains(file.getName())) // Get only those that don't have a matching thumbnail already
			  .forEach(file ->
			  {
				  /* Create a new thumbnail */
				  try
				  {
					  File target = new File(thumbnails, file.getName());

					  if (PropertyReader.getBoolean(GERMINATE_GALLERY_MAKE_THUMBNAILS_SQUARE))
					  {
						  Thumbnails.of(file)
									.height(250)
									.width(250)
									.crop(Positions.CENTER)
									.toFile(target);
					  }
					  else
					  {
						  Thumbnails.of(file)
									.height(250)
									.keepAspectRatio(true)
									.toFile(target);
					  }
				  }
				  catch (IOException e)
				  {
					  e.printStackTrace();
				  }
			  });
	}

	static void stopFileWatcher()
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
		private File         folder;

		private boolean stopped = false;

		PropertyChangeListenerThread(File folder, WatchService watcher)
		{
			this.folder = folder;
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
					Path filename = ev.context();

					scaleImages(new File(folder, filename.toFile().getName()).getParentFile());
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
