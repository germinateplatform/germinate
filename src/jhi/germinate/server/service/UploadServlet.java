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

package jhi.germinate.server.service;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.http.*;
import org.apache.http.entity.*;

import java.io.*;
import java.io.IOException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * The {@link UploadServlet} is used to check the progress of file uploads
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/upload"})
public class UploadServlet extends BaseHttpServlet
{
	private static final long serialVersionUID = -8773479730190882601L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String workloadSessionId = request.getParameter(ServletConstants.PARAM_SID);

		/* If there's a session id, check it */
		if (!StringUtils.isEmpty(workloadSessionId))
		{
			try
			{
				Session.checkSession(workloadSessionId, request);
			}
			catch (InvalidSessionException e)
			{
				return;
			}
		}

		/* If the system is under maintenance or in read-only-mode, don't accept file uploads */
		if (PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_UNDER_MAINTENANCE) || PropertyReader.getBoolean(ServerProperty.GERMINATE_IS_READ_ONLY))
		{
			return;
		}

		PrintWriter pw = response.getWriter();
		response.setContentType(ContentType.TEXT_HTML.getMimeType());

		try
		{
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload fu = new ServletFileUpload(factory);

			/* Set up a progress listener */
			if (!StringUtils.isEmpty(workloadSessionId))
			{
				UploadProgressListener progressListener = new UploadProgressListener();
				fu.setProgressListener(progressListener);
				HttpSession session = request.getSession();
				session.setAttribute(UploadProgressListener.PARAMETER_NAME, progressListener);
			}

			/* Calculate the file size limit */
			Float fileSizeLimitMB = PropertyReader.getFloat(ServerProperty.GERMINATE_UPLOAD_SIZE_LIMIT_MB);
			long fileSizeLimitB = (long) (fileSizeLimitMB * 1024d * 1024d);

			List<FileItem> fileItems = fu.parseRequest(request);

			/* Iterate over the list of items */
			for (FileItem fi : fileItems)
			{
				/* If any of the files exceeds the limit, throw an exception */
				if (fi.getSize() > fileSizeLimitB)
					throw new FileUploadBase.FileSizeLimitExceededException("File size limit exceeded", fi.getSize(), fileSizeLimitB);

				File fNew = createTemporaryFile(request, "upload", FileType.txt.name());

				/* Else write it to the target file */
				fi.write(fNew);
				pw.write(fNew.getName());
			}
		}
		catch (FileUploadBase.FileSizeLimitExceededException ex)
		{
			pw.write(UploadProgressService.FILESIZE_LIMIT_EXCEEDED);
			response.setStatus(HttpStatus.SC_INSUFFICIENT_STORAGE);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			/* Delete old temporary files */
			Long timeInHours = PropertyReader.getLong(ServerProperty.GERMINATE_KEEP_TEMPORARY_FILES_FOR_HOURS);
			new DeleteOldFilesThread(getTemporaryFileFolder(request), timeInHours).start();

			pw.close();
		}
	}

	/**
	 * The {@link UploadProgressListener} can be added to {@link ServletFileUpload} instances to track their progress
	 *
	 * @author Sebastian Raubach
	 */
	public static class UploadProgressListener implements ProgressListener
	{
		public static final String PARAMETER_NAME = "UploadProgressListener";

		private long num100Ks = 0;

		private float   percentDone        = 0;
		private boolean contentLengthKnown = false;

		@Override
		public void update(long bytesRead, long contentLength, int items)
		{

			if (contentLength > -1)
			{
				contentLengthKnown = true;
			}

			long nowNum100Ks = bytesRead / 100000;

            /* Only run this code once every 100K */
			if (nowNum100Ks >= num100Ks)
			{
				num100Ks = nowNum100Ks;
				if (contentLengthKnown)
				{
					percentDone = 100f * bytesRead / contentLength;
				}
			}
		}

		/**
		 * Returns the progress in percent
		 *
		 * @return The progress in percent
		 */
		public float getProgress()
		{
			return percentDone;
		}
	}
}
