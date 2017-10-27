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
 * {@link PhenotypeService} is a {@link RemoteService} providing methods to retrieve phenotype data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("phenotype")
public interface PhenotypeService extends RemoteService
{
	String NAME                  = "name";
	String DATASET_NAME          = "dataset_name";
	String LICENSE_NAME          = "license_name";
	String LOCATION_NAME         = "location_name";
	String TREATMENT_DESCRIPTION = "treatments_description";
	String YEAR                  = "year";

	String[] COLUMNS_DATA_SORTABLE = {Accession.ID, Accession.GENERAL_IDENTIFIER, Accession.NAME, Dataset.DESCRIPTION, ExperimentType.DESCRIPTION, Phenotype.NAME, Phenotype.SHORT_NAME, Unit.NAME, PhenotypeData.PHENOTYPE_VALUE, PhenotypeData.RECORDING_DATE};
	String[] COLUMNS_SORTABLE      = {Phenotype.ID, Phenotype.NAME, Phenotype.DESCRIPTION, Phenotype.SHORT_NAME, Unit.ID, Unit.NAME, Unit.ABBREVIATION, Unit.DESCRIPTION};

	/**
	 * Returns a paginated list of {@link PhenotypeData}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return A paginated list of {@link PhenotypeData}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	PaginatedServerResult<List<PhenotypeData>> getDataForFilter(RequestProperties properties, List<Long> datasetIds, Pagination pagination, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;

	/**
	 * Returns the {@link Phenotype} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The id of the accession
	 * @return The {@link Phenotype} for the given id
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<Phenotype> getById(RequestProperties properties, Long id) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a list of {@link Phenotype}s for the given {@link Dataset} ids, {@link ExperimentType} and numeric setting.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param datasetIds  The {@link Dataset} ids
	 * @param onlyNumeric Should only numeric phenotypes be returned?
	 * @return A list of {@link Phenotype}s for the given {@link Dataset} ids, {@link ExperimentType} and numeric setting.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Phenotype>> get(RequestProperties properties, List<Long> datasetIds, ExperimentType type, boolean onlyNumeric) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports the genotype information to a file and returns the file path as well as the data to the client
	 *
	 * @param properties   The {@link RequestProperties}
	 * @param datasetIds   The dataset id
	 * @param groupIds     The list of groups to export
	 * @param phenotypeIds The list of phenotypes to export
	 * @return The path to the generated file as well as the actual data
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<String> export(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, List<Long> phenotypeIds, boolean includeId) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a list of {@link DataStats} for the given {@link Dataset} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link Dataset} ids
	 * @return A list of {@link DataStats} for the given {@link Dataset} ids.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<DataStats>> getOverviewStats(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException;

	final class Inst
	{
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE}, not
		 * before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
		 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final PhenotypeServiceAsync INSTANCE = GWT.create(PhenotypeService.class);
		}

		public static PhenotypeServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Exports all the data associated with {@link PhenotypeData}s mathing the given {@link PartialSearchQuery}.
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

	/**
	 * Returns the ids of the {@link Accession}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @return The ids of the {@link Accession}s that match the given {@link PartialSearchQuery}.
	 * @throws InvalidSessionException     Thrown if the current session is invalid
	 * @throws DatabaseException           Thrown if the query fails on the server
	 * @throws InvalidColumnException      Thrown if the filtering is trying to access a column that isn't available for filtering
	 * @throws InvalidSearchQueryException Thrown if the search query is invalid
	 * @throws InvalidArgumentException    Thrown if one of the provided arguments for the filtering is invalid
	 */
	ServerResult<List<String>> getIdsForFilter(RequestProperties properties, PartialSearchQuery filter) throws InvalidSessionException, DatabaseException, InvalidColumnException, InvalidSearchQueryException, InvalidArgumentException;
}
