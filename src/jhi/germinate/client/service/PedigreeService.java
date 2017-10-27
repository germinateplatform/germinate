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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link PedigreeService} is a {@link RemoteService} providing methods to retrieve passport data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("pedigree")
public interface PedigreeService extends RemoteService
{
	String CHILD_ID    = "Child.id";
	String CHILD_GID   = "Child.general_identifier";
	String CHILD_NAME  = "Child.name";
	String PARENT_ID   = "Parent.id";
	String PARENT_GID  = "Parent.general_identifier";
	String PARENT_NAME = "Parent.name";

	String[] COLUMNS_PEDIGREE_SORTABLE = {Pedigree.ID, Pedigree.RELATIONSHIP_TYPE, Pedigree.RELATIONSHIP_DESCRIPTION, PedigreeDescription.NAME, PedigreeDescription.DESCRIPTION, PedigreeDescription.AUTHOR, Accession.ID, Accession.GENERAL_IDENTIFIER, Accession.NAME, CHILD_ID, CHILD_GID, CHILD_NAME, PARENT_ID, PARENT_GID, PARENT_NAME};

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

			private static final PedigreeServiceAsync INSTANCE = GWT.create(PedigreeService.class);

		}

		public static PedigreeServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}

	}

	/**
	 * Returns a list of {@link PedigreeDefinition}s for the given {@link Accession} id.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @return A list of {@link PedigreeDefinition}s for the given {@link Accession} id.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<PedigreeDefinition>> getPedigreeDefinitions(RequestProperties properties, Long accessionId) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a paginated list of {@link Pedigree}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Pedigree}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<Pedigree>> getForFilter(RequestProperties properties, PartialSearchQuery filter, Pagination pagination) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Exports the pedigree information of a single {@link Accession} id with the given {@link Pedigree.PedigreeQuery} to a file and returns the
	 * result file name.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param queryType   The {@link Pedigree.PedigreeQuery}
	 * @return The result file name.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if writing the file fails
	 */
	ServerResult<String> exportToHelium(RequestProperties properties, Collection<Long> accessionId, Pedigree.PedigreeQuery queryType) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports the pedigree information of all the accessions in a {@link Group} id and returns the result file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param groupId    The {@link Group} id
	 * @return The result file name.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if writing the file fails
	 */
	ServerResult<String> exportToHelium(RequestProperties properties, Long groupId) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports all the data associated with {@link Pedigree}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return The name of the result file.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws IOException                 Thrown if an I/O operation fails
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, InvalidArgumentException, InvalidColumnException, InvalidSearchQueryException, DatabaseException, IOException;

	/**
	 * Checks if pedigree data is available for either a given {@link Accession} id or for the whole dataset (if id is set to <code>null</code>).
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The {@link Accession} id (or <code>null</code>)
	 * @return <code>true</code> if pedigree data exists, <code>false</code> otherwise.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<Boolean> exists(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException;
}
