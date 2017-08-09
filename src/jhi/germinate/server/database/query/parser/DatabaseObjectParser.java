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
 * @author Sebastian Raubach
 */
public abstract class DatabaseObjectParser<T extends DatabaseObject>
{
	private Map<Class, DatabaseObjectCache<? extends DatabaseObject>> CACHES = new HashMap<>();

	public final void clearCache()
	{
		CACHES.values().forEach(DatabaseObjectCache::clear);
	}

	protected <U extends DatabaseObject, V extends AbstractManager<U>> DatabaseObjectCache<U> createCache(Class<U> clazz, Class<V> managerClazz)
	{
		DatabaseObjectCache<U> cache = (DatabaseObjectCache<U>) CACHES.get(clazz);

		if (cache == null)
			cache = new DatabaseObjectCache<>(clazz, managerClazz);

		CACHES.put(clazz, cache);
		return cache;
	}

	public abstract T parse(DatabaseResult databaseRow, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException;
}
