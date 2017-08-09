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

import java.util.*;
import java.util.Map;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link TrialService} is a {@link RemoteService} providing methods to retrieve trials data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("trial")
public interface TrialService extends RemoteService
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
			private static final TrialServiceAsync INSTANCE = GWT.create(TrialService.class);
		}

		public static TrialServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	/**
	 * Returns a list of {@link TrialAccessionYears} along with a list of the contained years.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param datasetIds  The {@link Dataset} ids
	 * @return A list of {@link TrialAccessionYears} along with a list of the contained years.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 */
	ServerResult<Pair<List<TrialAccessionYears>, List<String>>> getPhenotypeYearTable(RequestProperties properties, Long accessionId, List<Long> datasetIds) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports the individual data per site and treatment for an {@link Accession}, a list of {@link Dataset}s, a {@link Phenotype} and a year and
	 * writes it to a file. Returns the name of the file.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param datasetIds  The {@link Dataset} ids
	 * @param phenotypeId The {@link Phenotype} id
	 * @param trialsYear  The selected year
	 * @return The name of the file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 * @throws IOException             Thrown if the file creation fails
	 */
	ServerResult<String> exportIndividualData(RequestProperties properties, Long accessionId, List<Long> datasetIds, Long phenotypeId, String trialsYear) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports the values for an {@link Accession}, a list of {@link Dataset}s, a {@link Phenotype} and a year and writes it to a file. Returns the
	 * name of the file.
	 *
	 * @param properties  The {@link RequestProperties}
	 * @param accessionId The {@link Accession} id
	 * @param datasetIds  The {@link Dataset} ids
	 * @param phenotypeId The {@link Phenotype} id
	 * @param trialsYear  The selected year
	 * @return The name of the file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 * @throws IOException             Thrown if the file creation fails
	 */
	ServerResult<Map<String, String>> exportHistogram(RequestProperties properties, Long accessionId, List<Long> datasetIds, Long phenotypeId, String trialsYear) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports the so-called line performance data for an {@link Accession}, a list of {@link Dataset}s and a {@link Phenotype} across years and
	 * returns the name of the result file.
	 *
	 * @param properties      The {@link RequestProperties}
	 * @param accessionId     The {@link Accession} id
	 * @param datasetIds      The {@link Dataset} ids
	 * @param phenotypeId     The {@link Phenotype} id
	 * @param siteAverageText The text to use for the "Average site"
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 * @throws IOException             Thrown if the file creation fails
	 */
	ServerResult<Map<String, String>> exportLinePerformance(RequestProperties properties, Long accessionId, List<Long> datasetIds, Long phenotypeId, String siteAverageText) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports the data for a list of {@link Dataset}s, two {@link Phenotype}s and a {@link Group}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The selected dataset ids
	 * @param firstId    The id of the first phenotype
	 * @param secondId   The id of the second phenotype
	 * @return The data for a list of {@link Dataset}s, two {@link Phenotype}s and a {@link Group}
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws DatabaseException                Thrown if the database interaction fails
	 * @throws IOException                      Thrown if the file creation fails
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to access the group
	 */
	ServerResult<String> exportPhenotypeScatter(RequestProperties properties, List<Long> datasetIds, Long firstId, Long secondId, Long groupId) throws InvalidSessionException, DatabaseException, IOException, InsufficientPermissionsException;

	/**
	 * Returns the list of years, the table data and the mapping between {@link TrialsRow.TrialsAttribute} and the associated d3 chart file.
	 *
	 * @param properties    The {@link RequestProperties}
	 * @param datasetIds    The {@link Dataset} ids
	 * @param phenotypes    The {@link Phenotype} ids
	 * @param selectedYears The list of selected years
	 * @return The list of years, the table data and the mapping between {@link TrialsRow.TrialsAttribute} and the associated d3 chart file.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 */
	ServerResult<Triple<List<String>, List<TrialsRow>, Map<TrialsRow.TrialsAttribute, String>>> getPhenotypeOverviewTable(RequestProperties properties, List<Long> datasetIds, List<Long> phenotypes, List<Integer> selectedYears) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns a list of all the years where trials have been conducted within the given {@link Dataset}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetIds The {@link Dataset} ids
	 * @return A list of all the years where trials have been conducted within the given {@link Dataset}s.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the database interaction fails
	 */
	ServerResult<List<Integer>> getTrialYears(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException;
}
