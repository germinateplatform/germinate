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
import java.util.Map;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

@RemoteServiceRelativePath("common")
public interface CommonService extends RemoteService
{
	/**
	 * Marks the files for the given {@link ExperimentType} publically available. The actual files are stored on the server in the session, so we don't need to pass them here.
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param experimentType The {@link ExperimentType} of the files that should be made publically available.
	 * @return Nothing
	 * @throws InvalidSessionException
	 */
	Void makeFilesAvailablePublically(RequestProperties properties, ExperimentType experimentType) throws InvalidSessionException;

	/**
	 * Returns statistics (counts) for the main data types within Germinate. {@link Accession}s, {@link Marker}s, {@link Group}s and {@link Location}s.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return Statistics (counts) for the main data types within Germinate. {@link Accession}s, {@link Marker}s, {@link Group}s and {@link Location}s.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<Map<String, Long>> getOverviewStats(RequestProperties properties) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the columns of the {@link GerminateDatabaseTable}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param table      The {@link GerminateDatabaseTable}
	 * @return The columns of the {@link GerminateDatabaseTable}.
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<String>> getColumnsOfTable(RequestProperties properties, GerminateDatabaseTable table) throws InvalidSessionException, DatabaseException;

	/**
	 * Retrieves the settings from the server.
	 *
	 * @return The settings from the server.
	 */
	GerminateSettings getSettings();

	/**
	 * Returns the admin-specific settings.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The admin-specific settings.
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws InsufficientPermissionsException Thrown if the user permissions aren't sufficient to complete the request
	 */
	ServerResult<GerminateSettings> getAdminSettings(RequestProperties properties) throws DatabaseException, InvalidSessionException, InsufficientPermissionsException;

	/**
	 * Sets the admin-specific settings.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param settings   The {@link GerminateSettings}.
	 * @throws DatabaseException                Thrown if the query fails on the server
	 * @throws InvalidSessionException          Thrown if the current session is invalid
	 * @throws InsufficientPermissionsException Thrown if the user permissions aren't sufficient to complete the request
	 * @throws IOException                      Thrown if a file I/O fails
	 */
	void setAdminSettings(RequestProperties properties, GerminateSettings settings) throws DatabaseException, InvalidSessionException, InsufficientPermissionsException, IOException;

	/**
	 * Retrieves the external links for the given {@link GerminateDatabaseTable} and the referenceId
	 *
	 * @param properties     The {@link RequestProperties}
	 * @param referenceId    The reference id
	 * @param referenceTable The {@link GerminateDatabaseTable}
	 * @return The external links for the given {@link GerminateDatabaseTable} and the referenceId
	 * @throws InvalidSessionException Thrown if the current session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Link>> getExternalLinks(RequestProperties properties, Long referenceId, GerminateDatabaseTable referenceTable) throws InvalidSessionException, DatabaseException;

	/**
	 * Returns the {@link Synonym}s associated with the element with the given id in the given {@link GerminateDatabaseTable}.
	 *
	 * @param properties The {@link RequestProperties}
	 * @param table      The {@link GerminateDatabaseTable}
	 * @param id         The id of the element
	 * @return The {@link Synonym}s associated with the element with the given id in the given {@link GerminateDatabaseTable}.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<List<Synonym>> getSynonyms(RequestProperties properties, GerminateDatabaseTable table, Long id) throws InvalidSessionException, DatabaseException;

	/**
	 * Exports the biological status statistics and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if a file I/O fails
	 */
	ServerResult<String> getBiologicalStatusStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Exports the taxonomy statistics and returns the name of the result file.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return The name of the result file.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 * @throws IOException             Thrown if a file I/O fails
	 */
	ServerResult<String> getTaxonomyStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException;

	/**
	 * Returns a list of {@link Country} objects with additional information about the {@link DatabaseObject#COUNT} of items from this country.
	 *
	 * @param properties The {@link RequestProperties}
	 * @return A list of {@link Country} objects with additional information about the {@link DatabaseObject#COUNT} of items from this country.
	 * @throws InvalidSessionException Thrown if the session is invalid
	 * @throws DatabaseException       Thrown if the query fails on the server
	 */
	ServerResult<String> getCountryStats(RequestProperties properties) throws InvalidSessionException, DatabaseException, IOException;

	final class Inst
	{
		public static CommonServiceAsync get()
		{
			return InstanceHolder.INSTANCE;
		}

		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE}, not
		 * before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
		 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final CommonServiceAsync INSTANCE = GWT.create(CommonService.class);
		}
	}
}
