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

package jhi.germinate.client.util.parameterstore;

/**
 * @author Sebastian Raubach
 */
public class DoubleParameterStore extends TypedParameterStore<Double>
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
			private static final DoubleParameterStore INSTANCE = new DoubleParameterStore();
		}

		public static DoubleParameterStore get()
		{
			return InstanceHolder.INSTANCE;
		}
	}

	@Override
	protected Double stringToValue(String value)
	{
		try
		{
			return Double.parseDouble(value);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	protected String valueToString(Double value)
	{
		try
		{
			return Double.toString(value);
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
