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

package jhi.germinate.shared.datastructure;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;

/**
 * {@link ServerResult} is a wrapper for results returned from the server. They contain the (potentially empty) {@link DebugInfo} and the actual
 * result of the server request.
 *
 * @param <T> The type of the server result
 * @author Sebastian Raubach
 */
public class ServerResult<T> implements Serializable
{
	private static final long serialVersionUID = 1339833273635959337L;

	protected DebugInfo debugInfo;
	protected T         serverResult;

	public ServerResult()
	{

	}

	public ServerResult(T serverResult)
	{
		this.debugInfo = null;
		this.serverResult = serverResult;
	}

	public ServerResult(DebugInfo debugInfo, T serverResult)
	{
		this.debugInfo = debugInfo;
		this.serverResult = serverResult;
	}

	public boolean hasData()
	{
		if (serverResult instanceof Collection)
			return !CollectionUtils.isEmpty((Collection) serverResult);
		else if (serverResult instanceof Map)
			return ((Map) serverResult).size() > 0;
		else if (serverResult instanceof String)
			return StringUtils.isEmpty((String) serverResult);
		else
			return serverResult != null;
	}

	/**
	 * Returns the {@link DebugInfo} (potentially <code>null</code>)
	 *
	 * @return The {@link DebugInfo} (potentially <code>null</code>)
	 */
	public DebugInfo getDebugInfo()
	{
		return debugInfo;
	}

	public void setDebugInfo(DebugInfo debugInfo)
	{
		this.debugInfo = debugInfo;
	}

	/**
	 * Returns the actual server result
	 *
	 * @return The actual server result
	 */
	public T getServerResult()
	{
		return serverResult;
	}

	public void setServerResult(T serverResult)
	{
		this.serverResult = serverResult;
	}
}
