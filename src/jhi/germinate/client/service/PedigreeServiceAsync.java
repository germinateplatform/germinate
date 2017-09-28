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
import jhi.germinate.shared.search.*;

/**
 * Async version of {@link PedigreeService}.
 *
 * @author Sebastian Raubach
 */
public interface PedigreeServiceAsync
{
	/**
	 * Returns a list of {@link PedigreeDefinition}s for the given {@link Accession} id.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param callback    The {@link AsyncCallback}
	 */
	void getPedigreeDefinitions(RequestProperties properties, Long accessionId, AsyncCallback<ServerResult<List<PedigreeDefinition>>> callback);

	/**
	 * Returns a paginated list of {@link Pedigree}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination, AsyncCallback<PaginatedServerResult<List<Pedigree>>> callback);

	/**
	 * Exports the pedigree information of a single {@link Accession} id with the given {@link Pedigree.PedigreeQuery} to a file and returns the
	 * result file name.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param queryType   The {@link Pedigree.PedigreeQuery}
	 * @param callback    The {@link AsyncCallback}
	 */
	void exportToHelium(RequestProperties properties, Collection<Long> accessionId, Pedigree.PedigreeQuery queryType, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports the pedigree information of all the accessions in a {@link Group} id and returns the result file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void exportToHelium(RequestProperties properties, Long groupId, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports all the data associated with {@link Pedigree}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Checks if pedigree data is available for either a given {@link Accession} id or for the whole dataset (if id is set to <code>null</code>).
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The {@link Accession} id (or <code>null</code>)
	 * @param callback   The {@link AsyncCallback}
	 */
	void exists(RequestProperties properties, Long id, AsyncCallback<ServerResult<Boolean>> callback);
}
