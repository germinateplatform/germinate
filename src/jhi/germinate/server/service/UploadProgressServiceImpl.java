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

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link UploadProgressServiceImpl} is the implementation of {@link UploadProgressService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/upload-progress"})
public class UploadProgressServiceImpl extends BaseRemoteServiceServlet implements UploadProgressService
{
	private static final long serialVersionUID = 883278735379837399L;

	@Override
	public Float getProgress(RequestProperties properties) throws InvalidSessionException
	{
		Session.checkSession(properties, this);

		UploadServlet.UploadProgressListener listener = (UploadServlet.UploadProgressListener) getThreadLocalRequest().getSession().getAttribute(UploadServlet.UploadProgressListener.PARAMETER_NAME);

		if (listener == null)
			return null;
		else
			return listener.getProgress();
	}
}
