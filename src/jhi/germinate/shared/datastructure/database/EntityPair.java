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

package jhi.germinate.shared.datastructure.database;

import com.google.gwt.core.shared.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class EntityPair extends DatabaseObject
{
	private static final String CHILD_ID  = "child.id";
	private static final String PARENT_ID = "parent.id";

	private Accession child;
	private Accession parent;

	public Accession getChild()
	{
		return child;
	}

	public EntityPair setChild(Accession child)
	{
		this.child = child;
		return this;
	}

	public Accession getParent()
	{
		return parent;
	}

	public EntityPair setParent(Accession parent)
	{
		this.parent = parent;
		return this;
	}

	@Override
	@GwtIncompatible
	public DatabaseObjectParser<? extends DatabaseObject> getDefaultParser()
	{
		return Attribute.Parser.Inst.get();
	}

	@GwtIncompatible
	public static class Parser extends DatabaseObjectParser<EntityPair>
	{
		private static DatabaseObjectCache<Accession> ACCESSION_CACHE;

		private Parser()
		{
			ACCESSION_CACHE = createCache(Accession.class, AccessionManager.class);
		}

		@Override
		public EntityPair parse(DatabaseResult row, UserAuth user, boolean foreignsFromResultSet) throws DatabaseException
		{
			try
			{
				return new EntityPair()
						.setChild(ACCESSION_CACHE.get(user, row.getLong(CHILD_ID), row, foreignsFromResultSet))
						.setParent(ACCESSION_CACHE.get(user, row.getLong(PARENT_ID), row, foreignsFromResultSet));
			}
			catch (InsufficientPermissionsException e)
			{
				return null;
			}
		}

		public static final class Inst
		{
			public static Parser get()
			{
				return Parser.Inst.InstanceHolder.INSTANCE;
			}

			private static final class InstanceHolder
			{
				private static final Parser INSTANCE = new Parser();
			}
		}
	}
}
