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

package jhi.germinate.client.service;

import com.google.gwt.core.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link ImageService} is a {@link RemoteService} providing methods to retrieve gallery data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("image-list")
public interface ImageService extends RemoteService
{
	String MISSING_IMAGE        = "image-missing.png";
	String SIZE_LARGE           = "large";
	String SIZE_SMALL           = "small";
	String IMAGE_REFERENCE_NAME = "name";

	final class Inst
	{
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
		 * InstanceHolder#INSTANCE}, not before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom"
		 * >Initialization-on-demand holder idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or
		 * <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final ImageServiceAsync INSTANCE = GWT.create(ImageService.class);
		}

		public static ImageServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a paginated list of {@link Image}s for the given {@link DatabaseObject} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param table      The {@link GerminateDatabaseTable}
	 * @param id         The {@link DatabaseObject} id
	 * @param pagination The {@link Pagination}
	 * @return A paginated list of {@link Image}s for the given {@link DatabaseObject} id.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	PaginatedServerResult<List<Image>> getForId(RequestProperties properties, GerminateDatabaseTable table, Long id, Pagination pagination) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a {@link List} of all {@link ImageType}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return A {@link List} of all {@link ImageType}s.
	 */
	ServerResult<List<ImageType>> getTypes(RequestProperties properties) throws InvalidSessionException, DatabaseException;

	PaginatedServerResult<List<Image>> getForType(RequestProperties properties, ImageType imageType, Pagination pagination) throws InvalidSessionException, DatabaseException;
}
