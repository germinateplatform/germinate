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
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link ImageServiceImpl} is the implementation of {@link ImageService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/image-list"})
public class ImageServiceImpl extends BaseRemoteServiceServlet implements ImageService
{
	private static final long serialVersionUID = 7192904708637210440L;

	/**
	 * Returns a {@link List} of all the available images for download
	 *
	 * @return A {@link List} of all the available images for download
	 */
	private List<Tuple.Pair<File, String>> getImageArray(String locale)
	{
		String imageFolderPath = getFileFolder(FileLocation.download, ReferenceFolder.images) + File.separator + ImageServlet.THUMBNAILS;
		String imageFolderPathLocale = getFileFolder(FileLocation.download, locale, ReferenceFolder.images) + File.separator + ImageServlet.THUMBNAILS;

		File imageFolder = new File(imageFolderPath);
		File imageFolderLocale = new File(imageFolderPathLocale);

		List<Tuple.Pair<File, String>> files = new ArrayList<>();

        /* First get the localized files */
		if (imageFolderLocale.exists())
		{
			List<File> temp = new ArrayList<>(org.apache.commons.io.FileUtils.listFiles(imageFolderLocale, ImageMimeType.stringValues(), true));

			Path p = imageFolderLocale.toPath();

			for (File f : temp)
				files.add(new Tuple.Pair<>(f, p.relativize(f.toPath()).toString()));
		}

        /* Then check the fallback files */
		Collection<File> images = new ArrayList<>(org.apache.commons.io.FileUtils.listFiles(imageFolder, ImageMimeType.stringValues(), true));

        /* Only add the files that don't exist already */
		outer:
		for (File file : images)
		{
			Path fileRel = imageFolder.toPath().relativize(file.toPath());
			for (Tuple.Pair<File, String> fileLocale : files)
			{
				Path fileLocaleRel = imageFolderLocale.toPath().relativize(fileLocale.getFirst().toPath());

				if (fileRel.startsWith(fileLocaleRel) && fileRel.endsWith(fileLocaleRel))
					continue outer;
			}

			files.add(new Tuple.Pair<>(file, fileRel.relativize(file.toPath()).toString()));
		}

		return files;
	}

	@Override
	public PaginatedServerResult<List<Image>> get(RequestProperties properties, Pagination pagination) throws InvalidSessionException
	{
		/* Check if the session is valid */
		Session.checkSession(properties, this);

		List<Image> result = new ArrayList<>();

		List<Tuple.Pair<File, String>> images = getImageArray(properties.getLocale());

		for (int i = pagination.getStart(); i < Math.min(images.size(), pagination.getStart() + pagination.getLength()); i++)
		{
			Image row = new Image((long) i);

			try
			{
				/* Try to load the actual image */
				row.setDescription(images.get(i).getFirst().getName());
				row.setPath(images.get(i).getSecond());

				Util.setImageDimensions(row, images.get(i).getFirst());
			}
			catch (IOException e)
			{
				e.printStackTrace();
				/* If this fails, try to load the missing image icon */
				try
				{
					File imageFile = getFile(FileLocation.download, null, ReferenceFolder.images, ImageService.MISSING_IMAGE);

					row.setDescription(images.get(i).getFirst().getName());
					row.setPath(MISSING_IMAGE);

					Util.setImageDimensions(row, imageFile);
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
					/* If this fails, then screw it... just continue */
					continue;
				}
			}

			result.add(row);
		}

		return new PaginatedServerResult<>(null, result, images.size());
	}

	@Override
	public PaginatedServerResult<List<Image>> getForId(RequestProperties properties, GerminateDatabaseTable table, Long id, Pagination pagination) throws InvalidSessionException, DatabaseException
	{
		if (pagination == null)
			pagination = Pagination.getDefault();

		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);
		PaginatedServerResult<List<Image>> images = ImageManager.getForId(userAuth, table, id, pagination);

		if (!CollectionUtils.isEmpty(images.getServerResult()))
		{
			images.getServerResult().parallelStream()
				  .forEach(image ->
				  {
					  try
					  {
						  String relPath = image.getPath();

						  if (!relPath.startsWith(File.separator))
							  relPath = File.separator + relPath;

                			/* Try to load the actual image */
						  File imageFile = getFile(FileLocation.download, null, ReferenceFolder.images, ImageServlet.THUMBNAILS + relPath);

						  Util.setImageDimensions(image, imageFile);
					  }
					  catch (Exception e)
					  {
						  e.printStackTrace();
							/* If this fails, try to load the missing image icon */
						  try
						  {
							  File imageFile = getFile(FileLocation.download, null, ReferenceFolder.images, ImageService.MISSING_IMAGE);

							  image.setPath(ImageService.MISSING_IMAGE);

							  Util.setImageDimensions(image, imageFile);
						  }
						  catch (Exception e1)
						  {
							  e1.printStackTrace();
						  }
					  }
				  });
		}

		return images;
	}

	@Override
	public ServerResult<List<ImageType>> getTypes(RequestProperties properties) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return ImageTypeManager.getAllWithImages(userAuth);
	}

	@Override
	public PaginatedServerResult<List<Image>> getForType(RequestProperties properties, ImageType imageType, Pagination pagination) throws InvalidSessionException, DatabaseException
	{
		Session.checkSession(properties, this);
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		return ImageManager.getForType(userAuth, imageType, pagination);
	}
}
