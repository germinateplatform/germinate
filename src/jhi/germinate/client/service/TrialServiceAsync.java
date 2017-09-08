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
import java.util.Map;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;

public interface TrialServiceAsync
{
	/**
	 * Exports the data for a list of {@link Dataset}s, two {@link Phenotype}s and a {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The selected dataset ids
	 * @param firstId    The id of the first phenotype
	 * @param secondId   The id of the second phenotype
	 * @param callback   The {@link AsyncCallback}
	 */
	void exportPhenotypeScatter(RequestProperties properties, List<Long> datasetIds, Long firstId, Long secondId, Long groupId, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns the list of years, the table data and the mapping between {@link TrialsRow.TrialsAttribute} and the associated d3 chart file.
	 *
	 * @param properties    The {@link RequestProperties}
	 * @param datasetIds    The {@link Dataset} ids
	 * @param phenotypes    The {@link Phenotype} ids
	 * @param selectedYears The list of selected years
	 * @param callback      The {@link AsyncCallback}
	 */
	void getPhenotypeOverviewTable(RequestProperties properties, List<Long> datasetIds, List<Long> phenotypes, List<Integer> selectedYears, AsyncCallback<ServerResult<Triple<List<String>, List<TrialsRow>, Map<TrialsRow.TrialsAttribute, String>>>> callback);

	/**
	 * Returns a list of all the years where trials have been conducted within the given {@link Dataset}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link Dataset} ids
	 * @param callback   The {@link AsyncCallback}
	 */
	void getTrialYears(RequestProperties properties, List<Long> datasetIds, AsyncCallback<ServerResult<List<Integer>>> callback);
}
