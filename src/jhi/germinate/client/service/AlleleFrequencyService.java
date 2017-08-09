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

import com.google.gwt.core.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;

/**
 * {@link AlleleFrequencyService} is a {@link RemoteService} providing methods to retrieve accession data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("allelefreq")
public interface AlleleFrequencyService extends RemoteService
{
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
			private static final AlleleFrequencyServiceAsync INSTANCE = GWT.create(AlleleFrequencyService.class);
		}

		public static AlleleFrequencyServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	class HistogramParams implements Serializable
	{
		private static final long serialVersionUID = -5973074493484593372L;

		public HistogramMethod method;
		public double          splitPoint;
		public int             nrOfBins;
		public int             nrOfBinsRight;

		public HistogramParams()
		{

		}

		@Override
		public String toString()
		{
			return "HistogramParams{" +
					"method=" + method +
					", splitPoint=" + splitPoint +
					", nrOfBins=" + nrOfBins +
					", nrOfBinsRight=" + nrOfBinsRight +
					'}';
		}
	}

	class HistogramImageData implements Serializable
	{
		private static final long serialVersionUID = 1515056576140753048L;

		public double[] widths;
		public String[] colors;

		public HistogramImageData()
		{
		}

		public HistogramImageData(double[] widths, String[] colors)
		{
			this.widths = widths;
			this.colors = colors;
		}
	}

	/**
	 * Returns the histogram image data for the given {@link HistogramParams} as well as the Flapjack output
	 *
	 * @param properties The {@link RequestProperties}
	 * @param params     The {@link HistogramParams} specifying, e.g, width, height, {@link HistogramMethod}, etc.
	 * @return The histogram image data for the given {@link HistogramParams} as well as the Flapjack output
	 * @throws InvalidSessionException  Thrown if the current session is invalid
	 * @throws IOException              Thrown if flapjack fails to write the image or readAll the input data
	 * @throws FlapjackException        Thrown if Flapjack crashes
	 * @throws MissingPropertyException Thrown if a required flapjack property is missing from the properties file
	 */
	Pair<String, HistogramImageData> getHistogramImageData(RequestProperties properties, HistogramParams params) throws InvalidSessionException, IOException, FlapjackException, MissingPropertyException;

	/**
	 * Creates the Flapjack project file and returns the {@link FlapjackProjectCreationResult} with all the necessary information.
	 *
	 * @param properties      The {@link RequestProperties}
	 * @param histogramParams The {@link HistogramParams}
	 * @return The {@link FlapjackProjectCreationResult} with all the necessary information.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws FlapjackException       Thrown if Flapjack crashes
	 */
	Pair<String, FlapjackProjectCreationResult> createProject(RequestProperties properties, HistogramParams histogramParams) throws InvalidSessionException, FlapjackException;

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
	 * @param nrOfBins        The number of bins to use for the histogram
	 * @return The {@link FlapjackAllelefreqBinningResult} with all the necessary information.
	 * @throws InvalidSessionException  Thrown if the current session is invalid
	 * @throws DatabaseException        Thrown if the query fails on the server
	 * @throws InvalidArgumentException Thrown if there is no data to export for the selected parameters
	 * @throws IOException              Thrown if an I/O operation fails
	 * @throws FlapjackException        Thrown if Flapjack crashes
	 */
	ServerResult<FlapjackAllelefreqBinningResult> createHistogram(RequestProperties properties, List<Long> accessionGroups, List<Long> markerGroups, Long datastId, boolean missingOn, Long mapId, int nrOfBins) throws InvalidSessionException, DatabaseException, InvalidArgumentException, IOException, FlapjackException;
}