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

import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;

/**
 * Async version of {@link ImageService}.
 *
 * @author Sebastian Raubach
 */
public interface ImageServiceAsync
{
	/**
	 * Returns a paginated list of {@link Image}s for the given {@link DatabaseObject} id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param table      The {@link GerminateDatabaseTable}
	 * @param id         The {@link DatabaseObject} id
	 * @param pagination The {@link Pagination}
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForId(RequestProperties properties, GerminateDatabaseTable table, Long id, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback);

	/**
	 * Returns a {@link List} of all {@link ImageType}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getTypes(RequestProperties properties, AsyncCallback<ServerResult<List<ImageType>>> callback);

	void getForType(RequestProperties properties, ImageType imageType, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Image>>> callback);
}
