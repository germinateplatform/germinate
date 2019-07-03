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
import java.util.Map;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * Async version of {@link CommonService}
 *
 * @author Sebastian Raubach
 */
public interface CommonServiceAsync
{
	/**
	 * Returns the columns of the {@link GerminateDatabaseTable}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param table      The {@link GerminateDatabaseTable}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getColumnsOfTable(RequestProperties properties, GerminateDatabaseTable table, AsyncCallback<ServerResult<List<String>>> callback);

	/**
	 * Retrieves the settings from the server.
	 *
	 * @param callback The {@link AsyncCallback}
	 */
	void getSettings(AsyncCallback<GerminateSettings> callback);

	/**
	 * Returns the admin-specific settings.
	 *
	 * @param properties The {@link RequestProperties}
	 */
	void getAdminSettings(RequestProperties properties, AsyncCallback<ServerResult<GerminateSettings>> callback);

	/**
	 * Sets the admin-specific settings.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param settings   The {@link GerminateSettings}.
	 * @param callback   The {@link AsyncCallback}
	 */
	void setAdminSettings(RequestProperties properties, GerminateSettings settings, AsyncCallback<Void> callback);

	/**
	 * Retrieves the external links for the given {@link GerminateDatabaseTable} and the referenceId
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param referenceId    The reference id
	 * @param referenceTable The {@link GerminateDatabaseTable}
	 * @param callback       The {@link AsyncCallback}
	 */
	void getExternalLinks(RequestProperties properties, Long referenceId, GerminateDatabaseTable referenceTable, AsyncCallback<ServerResult<List<Link>>> callback);

	/**
	 * Returns the {@link Synonym}s associated with the element with the given id in the given {@link GerminateDatabaseTable}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param table      The {@link GerminateDatabaseTable}
	 * @param id         The id of the element
	 * @param callback   The {@link AsyncCallback}
	 */
	void getSynonyms(RequestProperties properties, GerminateDatabaseTable table, Long id, AsyncCallback<ServerResult<List<Synonym>>> callback);

	/**
	 * Exports the biological status statistics and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getBiologicalStatusStats(RequestProperties properties, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Exports the taxonomy statistics and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getTaxonomyStats(RequestProperties properties, AsyncCallback<ServerResult<String>> callback);

	/**
	 * Returns a list of {@link Country} objects with additional information about the {@link DatabaseObject#COUNT} of items from this country.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 */
	void getCountryStats(RequestProperties properties, AsyncCallback<ServerResult<List<Country>>> callback);

	/**
	 * Returns statistics (counts) for the main data types within Germinate. {@link Accession}s, {@link Marker}s, {@link Group}s and {@link Location}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param callback   The {@link AsyncCallback}
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	void getOverviewStats(RequestProperties properties, AsyncCallback<ServerResult<Map<String, Long>>> callback);

	/**
	 * Marks the files for the given {@link ExperimentType} publically available. The actual files are stored on the server in the session, so we don't need to pass them here.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param experimentType The {@link ExperimentType} of the files that should be made publically available.
	 * @param callback       The {@link AsyncCallback}
	 * @throws InvalidSessionException
	 */
	void makeFilesAvailablePublically(RequestProperties properties, ExperimentType experimentType, AsyncCallback<Void> callback);
}
