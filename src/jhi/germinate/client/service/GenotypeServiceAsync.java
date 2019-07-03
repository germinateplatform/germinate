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
	 * Kicks of the data extraction and returns the list of generated files.
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
	void computeExportDataset(RequestProperties properties, List<Long> accessionGroups, Set<String> markedAccessionIds, List<Long> markerGroups, Set<String> markedMarkerIds, Long datasetId, boolean heterozygousFilter, boolean misingDataFilter, Long mapToUse, AsyncCallback<ServerResult<List<CreatedFile>>> callback);

	/**
	 * Exports the Hdf5 file of the specified {@link jhi.germinate.shared.datastructure.database.Dataset} id to a flat file and returns the result
	 * file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The {@link jhi.germinate.shared.datastructure.database.Dataset} id
	 * @param callback   The {@link AsyncCallback}
	 */
	void convertHdf5ToText(RequestProperties properties, Long datasetId, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Converts the given genotype and map file into a Flapjack project file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param map        The filename of the map file
	 * @param genotype   The filename of the genotype file
	 * @param callback   The {@link AsyncCallback}
	 */
	void convertToFlapjack(RequestProperties properties, String map, String genotype, AsyncCallback<ServerResult<FlapjackProjectCreationResult>> callback);
}
