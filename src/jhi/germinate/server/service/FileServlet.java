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

import jhi.germinate.server.config.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * This class will return images from the server to the client Necessary parameters: <b>sid</b>, <b>filePath</b> and <b>fileLocation</b>.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/file"})
public class FileServlet extends BaseHttpServlet
{
	private static final long serialVersionUID = -5992060281333827418L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		/* Check parameters */
		String workloadSessionId = req.getParameter(ServletConstants.PARAM_SID);
		String filePath = req.getParameter(ServletConstants.PARAM_FILE_PATH);
		String fileLocation = req.getParameter(ServletConstants.PARAM_FILE_LOCATION);
		String fileLocale = req.getParameter(ServletConstants.PARAM_FILE_LOCALE);

        /* Check if the locale is a valid one */
		if (fileLocale != null)
		{
			if (!LocaleUtils.isValid(LocaleUtils.parseLocale(fileLocale)))
				fileLocale = null;
		}

		try
		{
			Session.checkSession(workloadSessionId, req, resp);
		}
		catch (InvalidSessionException e)
		{
			error(resp, HttpStatus.SC_UNAUTHORIZED, "Your session expired.");
			return;
		}

		if (StringUtils.isEmpty(filePath) || PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_UNDER_MAINTENANCE))
		{
			error(resp, HttpStatus.SC_BAD_REQUEST, "Requested resource not available.");
			return;
		}

        /* Get the file extension */
		String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

        /* Determine the file type */
		try
		{
			FileType type = FileType.valueOf(extension);

            /* Set the content type */
			resp.setContentType(type.getContentType());
		}
		catch (Exception e)
		{
			/* Set the content type */
			resp.setContentType("text/plain");
		}

        /* Determine the file location */
		FileLocation location;
		try
		{
			location = FileLocation.valueOf(fileLocation);
		}
		catch (Exception e)
		{
			location = FileLocation.temporary;
		}

        /* Get the absolute file path */
		File file = getFile(req, location, fileLocale, filePath);

		if (file == null || !file.exists())
		{
			error(resp, HttpStatus.SC_BAD_REQUEST, "Requested resource not available.");
			return;
		}

        /* Set the filename that will be used for file download */
		String filename;

		if (location == FileLocation.temporary)
			filename = Util.getDateTime() + "." + extension;
		else
			filename = file.getName();

        /* Set the header */
//		String attachment = (location == FileLocation.download) ? "attachment; " : "";
		/* Suggest a filename */
		resp.setHeader("Content-Disposition", "attachment;filename=\"" + filename + "\"");

        /* Delete old temporary files */
		Long timeInHours = PropertyReader.getLong(ServerProperty.GERMINATE_KEEP_TEMPORARY_FILES_FOR_HOURS);
		new DeleteOldFilesThread(getTemporaryFileFolder(req), timeInHours).start();

		FileUtils.setLastModifyDateNow(file);

		resp.setContentLength((int) file.length());

        /* Open the file and output streams */
		FileInputStream in = new FileInputStream(file);
		OutputStream out = resp.getOutputStream();

        /* Copy the contents of the file to the output stream */
		byte[] buf = new byte[1024];
		int count;
		while ((count = in.read(buf)) >= 0)
		{
			out.write(buf, 0, count);
		}

		in.close();
		out.close();
	}
}
