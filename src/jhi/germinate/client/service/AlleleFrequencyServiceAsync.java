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

import jhi.germinate.client.service.AlleleFrequencyService.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.enums.*;

/**
 * Async version of {@link AlleleFrequencyService}.
 *
 * @author Sebastian Raubach
 */
public interface AlleleFrequencyServiceAsync
{
	/**
	 * Returns the histogram image data for the given {@link HistogramParams} as well as the Flapjack output
	 *
	 * @param properties The {@link RequestProperties}
	 * @param params     The {@link HistogramParams} specifying, e.g, width, height, {@link HistogramMethod}, etc.
	 * @param callback   The {@link AsyncCallback}
	 */
	void getHistogramImageData(RequestProperties properties, HistogramParams params, AsyncCallback<Pair<String, HistogramImageData>> callback);

	/**
	 * Creates the Flapjack project file and returns the {@link FlapjackProjectCreationResult} with all the necessary information.
	 *
	 * @param properties      The {@link RequestProperties}
	 * @param histogramParams The {@link HistogramParams}
	 * @param callback        The {@link AsyncCallback}
	 */
	void createProject(RequestProperties properties, HistogramParams histogramParams, AsyncCallback<Pair<String, FlapjackProjectCreationResult>> callback);

	/**
	 * Creates the histogram data and returns the {@link FlapjackAllelefreqBinningResult} with all the necessary information.
	 *
	 * @param properties      The {@link RequestProperties}
	 * @param accessionGroups The ids of the {@link jhi.germinate.shared.datastructure.database.Accession} {@link
	 *                        jhi.germinate.shared.datastructure.database.Group}s
	 * @param markerGroups    The ids of the {@link jhi.germinate.shared.datastructure.database.Marker} {@link jhi.germinate.shared.datastructure.database.Group}s
	 * @param datastId        The ids of the {@link jhi.germinate.shared.datastructure.database.Dataset}s
	 * @param missingOn       Should the missing data filter be applied?
	 * @param mapId           The {@link jhi.germinate.shared.datastructure.database.Map} id
	 * @param callback        The {@link AsyncCallback}
	 */
	void createHistogram(RequestProperties properties, List<Long> accessionGroups, Set<String> markedAccessionIds, List<Long> markerGroups, Set<String> markedMarkerIds, Long datastId, boolean missingOn, Long mapId, int nrOfBins, AsyncCallback<ServerResult<FlapjackAllelefreqBinningResult>> callback);
}
