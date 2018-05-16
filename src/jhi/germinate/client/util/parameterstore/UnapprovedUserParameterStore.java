/*
 *  Copyright 2018 Information and Computational Sciences,
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

import jhi.germinate.shared.datastructure.*;

/**
 * @author Sebastian Raubach
 */
public class UnapprovedUserParameterStore extends TypedParameterStore<UnapprovedUser>
{
	@Override
	protected UnapprovedUser stringToValue(String value)
	{
		return null;
	}

	@Override
	protected String valueToString(UnapprovedUser value)
	{
		try
		{
			return value.toString();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public final static class Inst
	{
		public static UnapprovedUserParameterStore get()
		{
			return InstanceHolder.INSTANCE;
		}

		private static final class InstanceHolder
		{
			private static final UnapprovedUserParameterStore INSTANCE = new UnapprovedUserParameterStore();
		}
	}
}