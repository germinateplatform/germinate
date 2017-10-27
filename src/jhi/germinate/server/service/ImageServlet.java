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

import org.apache.http.*;

import java.io.*;
import java.io.IOException;

import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * This class will return images from the server to the client Necessary parameters: <b>sid</b>, <b>size</b> and <b>imagePath</b>. <p/> Optionalal
 * parameters: <b>referenceFolder</b>
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/image"})
public class ImageServlet extends BaseHttpServlet
{
	private static final long   serialVersionUID = -5992060281333827418L;
	public static final  String THUMBNAILS       = "thumbnails";
	public static final  String FULL_SIZE        = "fullsize";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		/* Check parameters */
		String workloadSessionId = req.getParameter(ServletConstants.PARAM_SID);
		String size = req.getParameter(ServletConstants.PARAM_SIZE);
		String imagePath = req.getParameter(ServletConstants.PARAM_IMAGE_PATH);
		String referenceFolder = req.getParameter(ServletConstants.PARAM_REFERENCE_FOLDER);
		String fileLocale = req.getParameter(ServletConstants.PARAM_FILE_LOCALE);
		ReferenceFolder subFolder = null;

        /* Check if the locale is a valid one */
		if (fileLocale != null)
		{
			if (!LocaleUtils.isValid(LocaleUtils.parseLocale(fileLocale)))
				fileLocale = null;
		}

		try
		{
			Session.checkSession(workloadSessionId, req);
		}
		catch (InvalidSessionException e)
		{
			error(resp, HttpStatus.SC_UNAUTHORIZED, "Your session expired.");
			return;
		}

		if (StringUtils.isEmpty(size) || StringUtils.isEmpty(imagePath) || PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_UNDER_MAINTENANCE))
		{
			error(resp, HttpStatus.SC_BAD_REQUEST, "Requested resource not available.");
			return;
		}

		if (!StringUtils.isEmpty(referenceFolder))
		{
			try
			{
				subFolder = ReferenceFolder.valueOf(referenceFolder);
			}
			catch (Exception e)
			{
				/* Nothing to do here */
			}
		}

		File file;

		if (imagePath.equals(ImageService.MISSING_IMAGE))
		{
			file = getFile(req, FileLocation.download, fileLocale, ReferenceFolder.images, ImageService.MISSING_IMAGE);
		}
		else
		{
			String imageSizeFolder;
			if (subFolder != null)
			{
				imageSizeFolder = subFolder.name();
			}
			else if (size.equals(ImageService.SIZE_LARGE))
			{
				imageSizeFolder = FULL_SIZE;
			}
			else if (size.equals(ImageService.SIZE_SMALL))
			{
				imageSizeFolder = THUMBNAILS;
			}
			else
			{
				error(resp, HttpStatus.SC_BAD_REQUEST, "Requested resource not available.");
				return;
			}

			file = getFile(req, FileLocation.download, fileLocale, ReferenceFolder.images, imageSizeFolder + File.separator + imagePath);
		}

        /* Set the filename that will be used for file download */
		String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1).toLowerCase();

		ImageMimeType type = ImageMimeType.valueOf(extension);

        /* Set the content type */
		resp.setContentType(type.getContentType());

        /* If the image doesn't exist, use the missing/placeholder image instead */
		if (file == null || !file.exists())
		{
			file = getFile(req, FileLocation.download, fileLocale, ReferenceFolder.images, ImageService.MISSING_IMAGE);
		}

		/* Suggest a filename */
		resp.setHeader("Content-Disposition", "filename=\"" + file.getName() + "\"");

		resp.setContentLength((int) file.length());

		try (FileInputStream in = new FileInputStream(file);
			 OutputStream out = resp.getOutputStream())
		{
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) >= 0)
			{
				out.write(buf, 0, len);
			}

			out.flush();
			out.close();
			in.close();
		}
	}
}