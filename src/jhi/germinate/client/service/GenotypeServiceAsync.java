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

import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.shared.datastructure.*;

/**
 * Async version of {@link GenotypeService}.
 *
 * @author Sebastian Raubach
 */
public interface GenotypeServiceAsync
{
	/**
	 * Kicks of the data extraction and returns the {@link FlapjackProjectCreationResult} with all the required information.
	 *
	 * @param properties         The {@link RequestProperties}
	 * @param accessionGroups    The {@link jhi.germinate.shared.datastructure.database.Accession} {@link jhi.germinate.shared.datastructure.database.Group}
	 *                           ids
	 * @param markerGroups       The {@link jhi.germinate.shared.datastructure.database.Marker} {@link jhi.germinate.shared.datastructure.database.Group}
	 *                           ids
	 * @param datasetId          The {@link jhi.germinate.shared.datastructure.database.Dataset} id
	 * @param heterozygousFilter Should the heterozygous data filtering be enabled?
	 * @param misingDataFilter   Should the missing data filtering be enabled?
	 * @param mapToUse           The {@link jhi.germinate.shared.datastructure.database.Map} file to use
	 * @param callback           The {@link AsyncCallback}
	 */
	void computeExportDataset(RequestProperties properties, List<Long> accessionGroups, List<Long> markerGroups, Long datasetId, boolean heterozygousFilter, boolean misingDataFilter, Long mapToUse, AsyncCallback<ServerResult<FlapjackProjectCreationResult>> callback);

	/**
	 * Exports the Hdf5 file of the specified {@link jhi.germinate.shared.datastructure.database.Dataset} id to a flat file and returns the result
	 * file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The {@link jhi.germinate.shared.datastructure.database.Dataset} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void convertHdf5ToFlapjack(RequestProperties properties, Long datasetId, AsyncCallback<ServerResult<String>> callback);
}
