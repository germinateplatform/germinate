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

package jhi.germinate.server.util;

import java.util.Map;
import java.util.concurrent.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * DatabaseObjectCache is a utility class that can be used to retrieve and cache database entries. In more detail, this class will do: <ul> <li>When
 * {@link #get(UserAuth, Long, DatabaseResult, boolean)} is called, check if the DatabaseObject exists in the cache</li> <li>Use the appropriate
 * {@link AbstractManager} to get the DatabaseObject from the database if it doesn't exist in the cache and then cache it</li> <li>If asked to extract
 * from the DatabaseResult, it will extract all the necessary information from this object rather than starting a new query against the database.</li>
 * </ul> Calling {@link #clear()} will clear the cache. This is particularly advisable if one wants to prevent long term caching and returning
 * potentially outdated data.
 *
 * @author Sebastian Raubach
 */
public class DatabaseObjectCache<T extends DatabaseObject>
{
	private final Map<Long, T>                CACHE   = new ConcurrentHashMap<>();
	private final Map<Class, AbstractManager> MAPPING = new ConcurrentHashMap<>();

	private final Class clazz;
	private final Class managerClazz;

	public DatabaseObjectCache(Class<T> clazz, Class<? extends AbstractManager<T>> managerClazz)
	{
		this.clazz = clazz;
		this.managerClazz = managerClazz;
	}

	/**
	 * Returns the DatabaseObject either from the cache or fetches it from the database
	 *
	 * @param user       The UserAuth of the current user. Can be used for authentication and permission checking
	 * @param id         The id of the DatabaseObject to return
	 * @param res        The current DatabaseResult object
	 * @param fromResult Should the object be extracted from the DatabaseResult rather than starting a new database query?
	 * @return The DatabaseObject either from the cache or fetches it from the database
	 * @throws DatabaseException                Thrown if the communication with the database fails
	 * @throws InsufficientPermissionsException Thrown if the current user does not have sufficient permissions to access the database entry.
	 */
	public T get(UserAuth user, Long id, DatabaseResult res, boolean fromResult) throws DatabaseException, InsufficientPermissionsException
	{
		/* If we're supposed to query the database, but the id is null, return here */
		if (id == null && !fromResult)
			return null;

		/* Check the cache if we have an id */
		T result = id == null ? null : CACHE.get(id);

		/* If it's not in the cache, get it from the manager */
		if (result == null)
		{
			/* Either get it from the DatabaseResult */
			if (fromResult)
				result = getFromManager(user, res);
				/* Or get it from the database based on the id */
			else
				result = getFromManager(user, id);

			/* If there is a result */
			if (result != null)
			{
				/* Check if we have an id, otherwise get it */
				if (id == null)
					id = result.getId();

				/* Remember the mapping */
				CACHE.put(id, result);
			}
		}

		/* Return whatever it is we've got */
		return result;
	}

	/**
	 * Clears the cache
	 */
	public void clear()
	{
		CACHE.clear();
		/* Don't just clear this cache but also the caches used by the Class this cache represents
		 * i.e. LocationCache.clear() will not only clear all accessions, but also all caches that are used to parse a Location */
		try
		{
			((DatabaseObject) clazz.newInstance()).getDefaultParser().clearCache();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			/* Do nothing here */
		}
	}

	/**
	 * Returns the {@link DatabaseObject} with the given id from this cache.
	 *
	 * @param user The {@link UserAuth}
	 * @param id   The id of the {@link DatabaseObject}
	 * @return The {@link DatabaseObject} with the given id from this cache
	 * @throws DatabaseException                Thrown if the interaction with the database fails
	 * @throws InsufficientPermissionsException Thrown if the user doesn't have sufficient permissions to access the database object
	 */
	@SuppressWarnings("unchecked")
	private T getFromManager(UserAuth user, Long id) throws DatabaseException, InsufficientPermissionsException
	{
		AbstractManager manager = getManager();
		if (manager != null)
			return (T) manager.getById(user, id).getServerResult();
		else
			return null;
	}

	/**
	 * Returns the {@link DatabaseObject} by parsing it straight from the {@link DatabaseResult} without running another database query. <p>This
	 * obviously means that all the information required to parse the object needs to be part of this {@link DatabaseResult}.
	 *
	 * @param user The {@link UserAuth}
	 * @param res  The {@link DatabaseResult} containing all the required information to parse the {@link DatabaseObject}
	 * @return The {@link DatabaseObject} by parsing it straight from the {@link DatabaseResult} without running another database quer
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	@SuppressWarnings("unchecked")
	private T getFromManager(UserAuth user, DatabaseResult res) throws DatabaseException
	{
		AbstractManager manager = getManager();
		if (manager != null)
			return (T) manager.getFromResult(user, res);
		else
			return null;
	}

	private AbstractManager<T> getManager()
	{
		AbstractManager<T> result = MAPPING.get(managerClazz);

		if (result == null)
		{
			/* Let's do some reflection magic here. We get the AbstractManager by looking for the 'get' method and then invoke it. Then we can use the manager to get the actual object. */
			try
			{
				result = (AbstractManager) managerClazz.newInstance();
				MAPPING.put(managerClazz, result);
			}
			catch (InstantiationException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		return result;
	}
}
