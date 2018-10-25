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

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link TypedParameterStore} is the central instance holding the {@link Parameter}s of the current state.
 *
 * @author Sebastian Raubach
 */
public abstract class TypedParameterStore<T>
{
	protected final      Map<Parameter, T>           STATE  = new HashMap<>();
	private static final Set<TypedParameterStore<?>> STORES = new HashSet<>();

	public TypedParameterStore()
	{
		STORES.add(this);
	}

	/**
	 * Tries to get a {@link String} representation of the value stored for the given {@link Parameter}.
	 *
	 * @param parameter The {@link Parameter} for which to get the String representation.
	 * @return A {@link String} representation of the value stored for the given {@link Parameter}.
	 * @throws UnsupportedDataTypeException Thrown if the parameter type isn't supported, i.e. no suitable parameter store could be found.
	 */
	public static String getUntyped(Parameter parameter) throws UnsupportedDataTypeException
	{
		if (DebugInfo.class.equals(parameter.getType()))
		{
			return DebugInfoParameterStore.Inst.get().getAsString(parameter);
		}
		else if (FlapjackAllelefreqBinningResult.class.equals(parameter.getType()))
		{
			return FlapjackAllelefreqBinningResultParameterStore.Inst.get().getAsString(parameter);
		}
		else if (UnapprovedUser.class.equals(parameter.getType()))
		{
			return UnapprovedUserParameterStore.Inst.get().getAsString(parameter);
		}
		else if (Float.class.equals(parameter.getType()))
		{
			return FloatParameterStore.Inst.get().getAsString(parameter);
		}
		else if (Integer.class.equals(parameter.getType()))
		{
			return IntegerParameterStore.Inst.get().getAsString(parameter);
		}
		else if (List.class.equals(parameter.getType()))
		{
			return LongListParameterStore.Inst.get().getAsString(parameter);
		}
		else if (Long.class.equals(parameter.getType()))
		{
			return LongParameterStore.Inst.get().getAsString(parameter);
		}
		else if (String.class.equals(parameter.getType()))
		{
			return StringParameterStore.Inst.get().getAsString(parameter);
		}
		else
		{
			throw new UnsupportedDataTypeException();
		}
	}

	/**
	 * Tries to store the given {@link String} value for the {@link Parameter} based on its type.
	 *
	 * @param parameter The {@link Parameter} for which to store the given value.
	 * @param value     The String value.
	 * @throws UnsupportedDataTypeException Thrown if the parameter type isn't supported, i.e. no suitable parameter store could be found.
	 */
	public static void putUntyped(Parameter parameter, String value) throws UnsupportedDataTypeException
	{
		if (DebugInfo.class.equals(parameter.getType()))
		{
			DebugInfoParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (FlapjackAllelefreqBinningResult.class.equals(parameter.getType()))
		{
			FlapjackAllelefreqBinningResultParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (Float.class.equals(parameter.getType()))
		{
			FloatParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (Integer.class.equals(parameter.getType()))
		{
			IntegerParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (List.class.equals(parameter.getType()))
		{
			try
			{
				LongListParameterStore.Inst.get().putAsString(parameter, value);
			}
			catch (UnsupportedDataTypeException e)
			{
				StringListParameterStore.Inst.get().putAsString(parameter, value);
			}
		}
		else if (Long.class.equals(parameter.getType()))
		{
			LongParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (String.class.equals(parameter.getType()))
		{
			StringParameterStore.Inst.get().putAsString(parameter, value);
		}
		else
		{
			throw new UnsupportedDataTypeException();
		}
	}

	/**
	 * Clears all {@link TypedParameterStore}s by calling {@link TypedParameterStore#clear()} on all of them.
	 */
	public static void clearAllStores()
	{
		STORES.forEach(TypedParameterStore::clear);
	}

	/**
	 * Adds the given {@link Parameter} value combination to the store.
	 *
	 * @param parameter The {@link Parameter} for which to store the given value.
	 * @param value     The value to store.
	 * @return The previous value associated with the {@link Parameter}, if any.
	 */
	public final T put(Parameter parameter, T value)
	{
		if (parameter.getLifetime() != Parameter.ParameterLifetime.VOLATILE)
		{
			if (value == null)
				LocalStorage.remove(parameter.name());
			else
			{
				boolean expires = parameter.getLifetime() == Parameter.ParameterLifetime.TEMPORARY;
				LocalStorage.set(parameter.name(), valueToString(value), expires);
			}
		}

		if (value == null)
			return STATE.remove(parameter);
		else
			return STATE.put(parameter, value);
	}

	/**
	 * Adds the given {@link Parameter} value combination to the store.
	 *
	 * @param parameter The {@link Parameter} for which to store the given value.
	 * @param value     The String value to store.
	 * @return The previous value associated with the {@link Parameter}, if any.
	 * @throws UnsupportedDataTypeException Thrown if the parameter type isn't supported, i.e. no suitable parameter store could be found.
	 */
	public final T putAsString(Parameter parameter, String value) throws UnsupportedDataTypeException
	{
		return put(parameter, stringToValue(value));
	}

	/**
	 * Returns the value stored for the given {@link Parameter}.
	 *
	 * @param parameter The {@link Parameter} for which to get the value.
	 * @return The value stored for the given {@link Parameter}.
	 */
	public final T get(Parameter parameter)
	{
		if (parameter == null)
			return null;

		T result = STATE.get(parameter);

		if (result == null && parameter.getLifetime() != Parameter.ParameterLifetime.VOLATILE)
		{
			try
			{
				return stringToValue(LocalStorage.get(parameter.name()));
			}
			catch (UnsupportedDataTypeException e)
			{
				return null;
			}
		}
		else
			return result;
	}

	/**
	 * Returns the value stored for the given {@link Parameter} or the given fallback if no value is present.
	 *
	 * @param parameter The {@link Parameter} for which to get the value.
	 * @param fallback  A fallback value in case there is no value associated with the given parameter.
	 * @return The value stored for the given {@link Parameter} or the given fallback if no value is present.
	 */
	public final T get(Parameter parameter, T fallback)
	{
		if (parameter == null)
			return fallback;

		T result = STATE.get(parameter);

		if (result == null && parameter.getLifetime() != Parameter.ParameterLifetime.VOLATILE)
		{
			try
			{
				result = stringToValue(LocalStorage.get(parameter.name()));

				if (result == null)
					return fallback;
				else
					return result;
			}
			catch (UnsupportedDataTypeException e)
			{
				return fallback;
			}
		}
		else
			return result;
	}

	/**
	 * Returns the {@link String} representation of the value associated with the given {@link Parameter}.
	 *
	 * @param parameter The {@link Parameter} for which to get the String representation.
	 * @return The {@link String} representation of the value associated with the given {@link Parameter}.
	 */
	public final String getAsString(Parameter parameter)
	{
		return valueToString(get(parameter));
	}

	/**
	 * Removes the given {@link Parameter} from the store.
	 *
	 * @param parameter The {@link Parameter} to remove.
	 * @return The previous value associated with the given {@link Parameter}.
	 */
	public final T remove(Parameter parameter)
	{
		if (parameter.getLifetime() != Parameter.ParameterLifetime.VOLATILE)
			LocalStorage.remove(parameter.name());

		return STATE.remove(parameter);
	}

	/**
	 * Removes all {@link Parameter}s and their values from the store.
	 */
	public final void clear()
	{
		for (Parameter param : Parameter.values())
		{
			if (param.getLifetime() == Parameter.ParameterLifetime.TEMPORARY)
				LocalStorage.remove(param.name());
		}

		STATE.clear();
	}

	/**
	 * Converts the given {@link String} into a value of the store type.
	 *
	 * @param value The String value to convert.
	 * @return The converted value of the store type.
	 * @throws UnsupportedDataTypeException Thrown if the parameter type isn't supported, i.e. no suitable parameter store could be found.
	 */
	protected abstract T stringToValue(String value) throws UnsupportedDataTypeException;

	/**
	 * Converts the given value of the store type into a {@link String}.
	 *
	 * @param value The value of the store type to convert.
	 * @return The converted String value.
	 */
	protected abstract String valueToString(T value);
}
