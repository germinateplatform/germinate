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

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link GenotypeService} is a {@link RemoteService} providing methods to retrieve genotype data.
 *
 * @author Sebastian Raubach
 */
@RemoteServiceRelativePath("genotype")
public interface GenotypeService extends RemoteService
{
	final class Inst
	{
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link
		 * InstanceHolder#INSTANCE}, not before.
		 * <p/>
		 * This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder idiom</a>) is
		 * thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final GenotypeServiceAsync INSTANCE = GWT.create(GenotypeService.class);
		}

		public static GenotypeServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

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
	 * @return The {@link FlapjackProjectCreationResult} with all the required information.
	 * @throws InvalidSessionException  Thrown if the current session is invalid
	 * @throws DatabaseException        Thrown if the query fails on the server
	 * @throws IOException              Thrown if the file I/O fails
	 * @throws MissingPropertyException Thrown if a required flapjack property is missing from the properties file
	 */
	ServerResult<FlapjackProjectCreationResult> computeExportDataset(RequestProperties properties, List<Long> accessionGroups, List<Long> markerGroups, Long datasetId, boolean heterozygousFilter, boolean misingDataFilter, Long mapToUse) throws InvalidSessionException, DatabaseException, IOException, FlapjackException, MissingPropertyException, InvalidArgumentException;

	/**
	 * Exports the Hdf5 file of the specified {@link jhi.germinate.shared.datastructure.database.Dataset} id to a flat file and returns the result
	 * file name.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param datasetId  The {@link jhi.germinate.shared.datastructure.database.Dataset} id
	 * @return The result file name.
	 * @throws InvalidSessionException  Thrown if the current session is invalid
	 * @throws DatabaseException        Thrown if the query fails on the server
	 * @throws InvalidArgumentException Thrown if no data is available for the given selection
	 * @throws IOException              Thrown if the file I/O fails
	 * @throws FlapjackException        Thrown if Flapjack crashes
	 */
	ServerResult<String> convertHdf5ToFlapjack(RequestProperties properties, Long datasetId) throws InvalidSessionException, DatabaseException, InvalidArgumentException, IOException, FlapjackException;
}
