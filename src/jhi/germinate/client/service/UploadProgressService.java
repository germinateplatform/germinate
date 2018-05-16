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

package jhi.germinate.client.service;

import com.google.gwt.core.shared.*;
import com.google.gwt.user.client.rpc.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link UploadProgressService} is a {@link RemoteService} providing methods to retrieve accession data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("upload-progress")
public interface UploadProgressService extends RemoteService
{
	String FILESIZE_LIMIT_EXCEEDED = "fileSizeLimitExceeded";

	final class Inst
	{
		private static final class InstanceHolder
		{
			private static final UploadProgressServiceAsync INSTANCE = GWT.create(UploadProgressService.class);
		}

		public static UploadProgressServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Check the progress of the currently running file upload
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The progress (in percent)
	 * @throws InvalidSessionException Thrown if the current session id is not valid
	 */
	Float getProgress(RequestProperties properties) throws InvalidSessionException;
}
