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

package jhi.germinate.shared.datastructure;

import com.google.gwt.core.shared.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import jhi.germinate.server.config.*;
import jhi.germinate.shared.enums.*;

/**
 * {@link DebugInfo} is a simple bean class containing a list of string debug texts and a boolean that determines if added content should be stored or
 * not
 *
 * @author Sebastian Raubach
 */
public class DebugInfo implements Serializable
{
	private static final long serialVersionUID = 1008472617295522302L;

	private List<String> debugInfo = null;
	private boolean      save      = false;


	DebugInfo()
	{
	}

	@GwtIncompatible
	public static DebugInfo create(UserAuth auth)
	{
		Logger.getLogger("").log(Level.INFO, "AUTH: " + (auth != null ? auth.toString() : "null"));
		return new DebugInfo(PropertyReader.getBoolean(ServerProperty.GERMINATE_DEBUG) && auth != null && auth.isAdmin());
	}

	/**
	 * Will initialize the internal list and allow addition of new content
	 */
	private DebugInfo(boolean save)
	{
		this.save = save;
		debugInfo = save ? new ArrayList<>() : null;
	}

	/**
	 * Adds the new debug string to the internal list (only if {@link #save} was set to true)
	 *
	 * @param debug The new debug string
	 * @return true, if the new item was added, false otherwise
	 */
	public boolean add(String debug)
	{
		return save && debugInfo.add(debug);
	}

	/**
	 * Retrieves the item at the given index from the internal list
	 *
	 * @param index The index of the requested item
	 * @return The requested item or null, if <code>save</code> was set to false
	 */
	public String get(int index)
	{
		return save ? debugInfo.get(index) : null;
	}

	/**
	 * Returns the size of the internal list
	 *
	 * @return The size of the internal list
	 */
	public int size()
	{
		return save ? debugInfo.size() : 0;
	}

	/**
	 * Adds all the debug strings from the given {@link DebugInfo} to this instance
	 *
	 * @param other The other DebugInfo
	 */
	public DebugInfo addAll(DebugInfo other)
	{
		if (other == null || !save)
			return this;

		for (int i = 0; i < other.size(); i++)
		{
			this.add(other.get(i));
		}

		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();

		for (String line : debugInfo)
			builder.append(line).append("\n");

		return builder.toString();
	}

}
