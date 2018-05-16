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
 * {@link CompoundService} is a {@link RemoteService} providing methods to retrieve compound data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("compound")
public interface CompoundService extends RemoteService
{
	String[] COLUMNS_SORTABLE      = {Compound.ID, Compound.NAME, Compound.DESCRIPTION, Compound.MOLECULAR_FORMULA, Compound.AVERAGE_MASS, Compound.MONOISOTOPIC_MASS, Compound.COMPOUND_CLASS, Compound.CREATED_ON, Compound.UPDATED_ON, Unit.NAME, Unit.ABBREVIATION, Unit.DESCRIPTION};
	String[] COLUMNS_DATA_SORTABLE = {CompoundData.ID, Accession.GENERAL_IDENTIFIER, Accession.NAME, Compound.NAME, Dataset.DESCRIPTION, AnalysisMethod.NAME, Unit.NAME, CompoundData.COMPOUND_VALUE};

	final class Inst
	{
		private static final class InstanceHolder
		{
			private static final CompoundServiceAsync INSTANCE = GWT.create(CompoundService.class);

		}

		public static CompoundServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a list of {@link Compound}s for the given {@link Dataset} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The ids of the {@link Dataset}s
	 * @return A list of {@link Compound}s for the given {@link Dataset} ids.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 */
	ServerResult<List<Compound>> getForDatasetIds(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a paginated list of {@link Compound}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link Compound}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<Compound>> getForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns the ids of the {@link Compound}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return The ids of the {@link Compound}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns a paginated list of {@link CompoundData} objectss that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link CompoundData} objectss that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<CompoundData>> getDataForFilter(RequestProperties properties, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidArgumentException, InvalidSearchQueryException;

	/**
	 * Returns the {@link Compound} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The id of the {@link Compound}
	 * @return The {@link Accession} for the given id
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<Compound> getById(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns overview stats in the for of {@link DataStats} for the given {@link Dataset} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link Dataset} ids
	 * @return Overview stats in the for of {@link DataStats} for the given {@link Dataset} ids.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<DataStats>> getDataStatsForDatasets(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports the {@link Compound} information to a file and returns the name of the result file.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param datasetIds  The dataset id
	 * @param groupIds    The list of groups to export
	 * @param compoundIds The list of compounds to export
	 * @return The {@link Compound} information to a file and returns the name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<String> getExportFile(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, List<Long> compoundIds, boolean includeId) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports the {@link CompoundData} information for the given {@link Compound} id and {@link Dataset} id and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param compoundId The {@link Compound} id
	 * @param datasetId  The {@link Dataset} id
	 * @return The {@link CompoundData} information for the given {@link Compound} id and {@link Dataset} ids and returns the name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if the file creation fails
	 */
	ServerResult<String> getBarChartData(RequestProperties properties, Long compoundId, Long datasetId) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports all the data associated with {@link CompoundData} objects mathing the given {@link PartialSearchQuery}.
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
	ServerResult<String> export(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, IOException, InvalidArgumentException, InvalidSearchQueryException, InvalidColumnException;
}
