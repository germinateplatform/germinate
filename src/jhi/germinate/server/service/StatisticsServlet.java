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

import java.io.*;
import java.io.IOException;

import javax.servlet.annotation.*;
import javax.servlet.http.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * This class will return images from the server to the client Necessary parameters: <b>sid</b>, <b>filePath</b> and <b>fileLocation</b>.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/statistics"})
public class StatisticsServlet extends HttpServlet
{
	private static final long serialVersionUID = -6728562648161658071L;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String username = req.getParameter(ServletConstants.PARAM_USERNAME);
		String password = req.getParameter(ServletConstants.PARAM_PASSWORD);
		String viewString = req.getParameter(ServletConstants.PARAM_STATISTICS_VIEW);

		boolean isPrivate = PropertyWatcher.getBoolean(ServerProperty.GERMINATE_USE_AUTHENTICATION);

		if (isPrivate)
		{
			try
			{
				GatekeeperUserWithPassword userDetails = GatekeeperUserManager.getForUsernameAndSystem(username);

				/* Check if the user exists and has permissions */
				if (userDetails == null || userDetails.getId() == null || userDetails.isSuspended() || !userDetails.isAdmin() || userDetails.getPassword() == null || !BCrypt.checkpw(password, userDetails.getPassword()))
				{
					throw new DatabaseException("The given user doesn't have sufficient permissions to access statistics of this instance of Germinate.");
				}
			}
			catch (DatabaseException e)
			{
				e.printStackTrace();
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		ViewInitializer.View view;

		try
		{
			view = ViewInitializer.View.fromViewName(viewString);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		try
		{
			File file = getStatistics(req, view);

			resp.setContentLength((int) file.length());

			/* Open the file and output streams */
			try (FileInputStream in = new FileInputStream(file);
				 OutputStream out = resp.getOutputStream())
			{
				/* Copy the contents of the file to the output stream */
				byte[] buf = new byte[1024];
				int count;
				while ((count = in.read(buf)) >= 0)
				{
					out.write(buf, 0, count);
				}
			}

			file.delete();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	public static File getStatistics(HttpServletRequest req, ViewInitializer.View view) throws DatabaseException, IOException
	{
		DefaultStreamer table = new DefaultQuery("SELECT * FROM " + view.getViewName(), null)
				.getStreamer();

		File file = BaseHttpServlet.createTemporaryFile(req, "stats", FileType.txt.name());

		Util.writeDefaultToFile(Util.getOperatingSystem(req), view.getColumns(), table, file);

		return file;
	}
}
