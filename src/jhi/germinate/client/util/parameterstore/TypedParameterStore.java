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

	public static String getUntyped(Parameter parameter) throws UnsupportedDataTypeException
	{
		if (Boolean.class.equals(parameter.getType()))
		{
			return BooleanParameterStore.Inst.get().getAsString(parameter);
		}
		else if (DebugInfo.class.equals(parameter.getType()))
		{
			return DebugInfoParameterStore.Inst.get().getAsString(parameter);
		}
		else if (Double.class.equals(parameter.getType()))
		{
			return DoubleParameterStore.Inst.get().getAsString(parameter);
		}
		else if (FlapjackProjectCreationResult.class.equals(parameter.getType()))
		{
			return FlapjackAllelefreqBinningResultParameterStore.Inst.get().getAsString(parameter);
		}
		else if (Float.class.equals(parameter.getType()))
		{
			return FloatParameterStore.Inst.get().getAsString(parameter);
		}
		else if (GerminateDatabaseTable.class.equals(parameter.getType()))
		{
			return GerminateDatabaseTableParameterStore.Inst.get().getAsString(parameter);
		}
		else if (Integer.class.equals(parameter.getType()))
		{
			return IntegerParameterStore.Inst.get().getAsString(parameter);
		}
//		else if (MarkedItemList.ItemType.class.equals(parameter.getType()))
//		{
//			return ItemTypeParameterStore.Inst.get().getAsString(parameter);
//		}
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

	public static void putUntyped(Parameter parameter, String value) throws UnsupportedDataTypeException
	{
		if (Boolean.class.equals(parameter.getType()))
		{
			BooleanParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (DebugInfo.class.equals(parameter.getType()))
		{
			DebugInfoParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (Double.class.equals(parameter.getType()))
		{
			DoubleParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (FlapjackProjectCreationResult.class.equals(parameter.getType()))
		{
			FlapjackAllelefreqBinningResultParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (Float.class.equals(parameter.getType()))
		{
			FloatParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (GerminateDatabaseTable.class.equals(parameter.getType()))
		{
			GerminateDatabaseTableParameterStore.Inst.get().putAsString(parameter, value);
		}
		else if (Integer.class.equals(parameter.getType()))
		{
			IntegerParameterStore.Inst.get().putAsString(parameter, value);
		}
//		else if (MarkedItemList.ItemType.class.equals(parameter.getType()))
//		{
//			ItemTypeParameterStore.Inst.get().putAsString(parameter, value);
//		}
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

	public final T putAsString(Parameter parameter, String value) throws UnsupportedDataTypeException
	{
		return put(parameter, stringToValue(value));
	}

	public final T get(Parameter parameter)
	{
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

	public final T get(Parameter parameter, T fallback)
	{
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

	public final String getAsString(Parameter parameter)
	{
		return valueToString(get(parameter));
	}

	public final T remove(Parameter parameter)
	{
		if (parameter.getLifetime() != Parameter.ParameterLifetime.VOLATILE)
			LocalStorage.remove(parameter.name());

		return STATE.remove(parameter);
	}

	public final void clear()
	{
		for (Parameter param : Parameter.values())
		{
			if (param.getLifetime() == Parameter.ParameterLifetime.TEMPORARY)
				LocalStorage.remove(param.name());
		}

		STATE.clear();
	}

	protected abstract T stringToValue(String value) throws UnsupportedDataTypeException;

	protected abstract String valueToString(T value);

	public static void clearAll()
	{
		STORES.forEach(TypedParameterStore::clear);
	}
}
