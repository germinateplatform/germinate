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
 * Async version of {@link PhenotypeService}.
 *
 * @author Sebastian Raubach
 */
public interface PhenotypeServiceAsync
{
	/**
	 * Returns the {@link Phenotype} with the given id.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param id         The id of the accession
	 * @param callback   The {@link AsyncCallback}
	 */
	void getById(RequestProperties properties, Long id, AsyncCallback<ServerResult<Phenotype>> callback);

	/**
	 * Returns a list of {@link Phenotype}s for the given {@link Dataset} ids, {@link ExperimentType} and numeric setting.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param datasetIds  The {@link Dataset} ids
	 * @param onlyNumeric Should only numeric phenotypes be returned?
	 * @param callback    The {@link AsyncCallback}
	 */
	void get(RequestProperties properties, List<Long> datasetIds, ExperimentType type, boolean onlyNumeric, AsyncCallback<ServerResult<List<Phenotype>>> callback);

	/**
	 * Exports the genotype information to a file and returns the file path as well as the data to the client
	 *
	 * @param properties   The {@link RequestProperties}
	 * @param datasetIds   The dataset id
	 * @param groupIds     The list of groups to export
	 * @param phenotypeIds The list of phenotypes to export
	 * @param callback     The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, List<Long> datasetIds, List<Long> groupIds, List<Long> phenotypeIds, boolean includeId, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a list of {@link DataStats} for the given {@link Dataset} ids.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link Dataset} ids
	 * @param callback   The {@link AsyncCallback}
	 */
	void getOverviewStats(RequestProperties properties, List<Long> datasetIds, AsyncCallback<ServerResult<List<DataStats>>> callback);

	/**
	 * Returns a paginated list of {@link PhenotypeData}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param pagination The {@link Pagination} The {@link Pagination}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	Request getDataForFilter(RequestProperties properties, List<Long> datasetIds, Pagination pagination, PartialSearchQuery filter, AsyncCallback<PaginatedServerResult<List<PhenotypeData>>> callback);

	/**
	 * Exports all the data associated with {@link PhenotypeData}s mathing the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void export(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns the ids of the {@link Accession}s that match the given {@link PartialSearchQuery}.
	 *
	 * @param properties The {@link RequestProperties} The {@link RequestProperties}
	 * @param filter     The {@link PartialSearchQuery} representing the user filtering
	 * @param callback   The {@link AsyncCallback}
	 */
	void getIdsForFilter(RequestProperties properties, PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback);
}
