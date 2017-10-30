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

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link FileListService} is a {@link RemoteService} providing methods to retrieve file data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("file-list")
public interface FileListService extends RemoteService
{
	final class Inst
	{
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE}, not
		 * before.
		 * <p/>
		 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder idiom</a>) is
		 * thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final FileListServiceAsync INSTANCE = GWT.create(FileListService.class);
		}

		public static FileListServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a the list of files contained in the reference folder in the reference location
	 *
	 * @param properties The {@link RequestProperties}
	 * @param location   The reference {@link FileLocation}
	 * @param folder     The {@link ReferenceFolder}
	 * @return A the list of files contained in the reference folder in the reference location
	 * @throws InvalidSessionException Thrown if the current session id is invalid
	 */
	List<String> getForFolder(RequestProperties properties, FileLocation location, ReferenceFolder folder) throws InvalidSessionException;
}
