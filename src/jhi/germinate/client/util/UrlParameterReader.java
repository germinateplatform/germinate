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

package jhi.germinate.client.util;

import com.google.gwt.user.client.*;

import java.util.*;

import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class UrlParameterReader
{
	/**
	 * Reads the parameters from the url and saves them in the {@link TypedParameterStore}. If there is more than one value for a parameter, either of
	 * the following will happen: <ul> <li>If the parameter is of the type <code>List</code>: All parameter values will be stored in this list</li>
	 * <li>If the parameter is of any other type: Only the first value is stored</li> </ul>
	 */
	public static boolean readUrlParameters()
	{
		/* Get the parameters */
		Map<String, List<String>> parameters = Window.Location.getParameterMap();

		boolean atLeastOne = false;

		for (String key : parameters.keySet())
		{
			try
			{
				/* Check if it's a valid parameter */
				Parameter param = Parameter.valueOf(key);

				List<String> values = parameters.get(key);

                /* Add it to the parameter store */
				if (values.size() > 0)
				{
					if (param.getType().equals(List.class))
					{
						/* If the Parameter is of type List, store the whole
						 * List */

                        /* Make a copy first, since the type of List that GWT
						 * returns doesn't behave well when trying to send it to
                         * the server */
						List<String> listToStore = new ArrayList<>();
						listToStore.addAll(values);

						StringListParameterStore.Inst.get().put(param, listToStore);
					}
					else
					{
						/* If not, just store the individual value */
						try
						{
							String value = values.get(0);

                            /* Acecssion and marker ids are special cases, since
							 * they have to work for links from Flapjack
                             * although they aren't integers, but the id with
                             * something appended. */
							if ((param == Parameter.accessionId || param == Parameter.markerId) && value.matches("^\\d+\\|.*$"))
								LongParameterStore.Inst.get().putAsString(param, value.substring(0, value.indexOf("|")));
							else
							{
								TypedParameterStore.putUntyped(param, value);
							}
						}
						catch (UnsupportedDataTypeException | NumberFormatException e)
						{
							continue;
						}
					}

					atLeastOne = true;
				}
			}
			catch (IllegalArgumentException e)
			{
				/* Ignore exceptions here. It either works, or it doesn't... */
			}
			catch (Exception e)
			{
				if (GerminateSettingsHolder.get().debug.getValue())
					Notification.notify(Notification.Type.ERROR, e.getLocalizedMessage());
			}
		}

		return atLeastOne;
	}
}
