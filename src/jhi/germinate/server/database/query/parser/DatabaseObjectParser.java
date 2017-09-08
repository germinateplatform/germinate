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

package jhi.germinate.server.database.query.parser;

import java.util.*;
import java.util.Map;

import jhi.germinate.server.database.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link DatabaseObjectParser} is an abstract class defining how {@link DatabaseObject}s should be parsed from the database.
 *
 * @author Sebastian Raubach
 */
public abstract class DatabaseObjectParser<T extends DatabaseObject>
{
	/** Keeps track of all the {@link DatabaseObjectCache}s that have been created by the subclass */
	private Map<Class, DatabaseObjectCache<? extends DatabaseObject>> caches = new HashMap<>();

	/**
	 * Clears all caches
	 */
	public final void clearCache()
	{
		caches.values().forEach(DatabaseObjectCache::clear);
	}

	/**
	 * Creates a new {@link DatabaseObjectCache} for the given type
	 *
	 * @param clazz        The subclass of {@link DatabaseObject} that is being parsed by this class
	 * @param managerClazz The class of the {@link AbstractManager} that is used to parse the respective {@link DatabaseObject}
	 * @param <U>          The subclass of {@link DatabaseObject} that is being parsed by this class
	 * @param <V>          The class of the {@link AbstractManager} that is used to parse the respective {@link DatabaseObject}
	 * @return A new {@link DatabaseObjectCache} for the given type
	 */
	protected <U extends DatabaseObject, V extends AbstractManager<U>> DatabaseObjectCache<U> createCache(Class<U> clazz, Class<V> managerClazz)
	{
		DatabaseObjectCache<U> cache = (DatabaseObjectCache<U>) caches.get(clazz);

		if (cache == null)
			cache = new DatabaseObjectCache<>(clazz, managerClazz);

		caches.put(clazz, cache);
		return cache;
	}

	/**
	 * Returns the {@link DatabaseObject} that was parsed from the {@link DatabaseResult}.
	 *
	 * @param databaseRow           The {@link DatabaseResult} containing the data from the database.
	 * @param user                  The {@link UserAuth} of the current user
	 * @param foreignsFromResultSet Set to <code>true</code> if objects refered to by foreign keys can be extracted from the same {@link
	 *                              DatabaseResult}. If set to <code>false</code>, the {@link AbstractManager} is used to parse the object based in
	 *                              its foreign key id.
	 * @return The {@link DatabaseObject} that was parsed from the {@link DatabaseResult}.
	 * @throws DatabaseException Thrown if the interaction with the database fails.
	 */
	public abstract T parse(DatabaseResult databaseRow, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException;
}
