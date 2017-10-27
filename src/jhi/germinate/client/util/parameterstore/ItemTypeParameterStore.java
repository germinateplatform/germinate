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

package jhi.germinate.client.util.parameterstore;

import jhi.germinate.client.util.*;

/**
 * @author Sebastian Raubach
 */
public class ItemTypeParameterStore extends TypedParameterStore<MarkedItemList.ItemType>
{
	public final static class Inst
	{
		/**
		 * {@link InstanceHolder} is loaded on the first execution of {@link Inst#get()} or the first access to {@link InstanceHolder#INSTANCE}, not
		 * before. <p/> This solution (<a href= "http://en.wikipedia.org/wiki/Initialization_on_demand_holder_idiom" >Initialization-on-demand holder
		 * idiom</a>) is thread-safe without requiring special language constructs (i.e. <code>volatile</code> or <code>synchronized</code>).
		 *
		 * @author Sebastian Raubach
		 */
		private static final class InstanceHolder
		{
			private static final ItemTypeParameterStore INSTANCE = new ItemTypeParameterStore();
		}

		public static ItemTypeParameterStore get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	@Override
	protected MarkedItemList.ItemType stringToValue(String value)
	{
		/* Take care of null here, because it would be parsed to false otherwise */
		if (value == null)
			return null;

		try
		{
			return MarkedItemList.ItemType.valueOf(value.toUpperCase());
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	protected String valueToString(MarkedItemList.ItemType value)
	{
		try
		{
			return value.name();
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
