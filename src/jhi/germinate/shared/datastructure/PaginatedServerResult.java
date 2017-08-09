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

/**
 * {@link PaginatedServerResult} is a wrapper for results returned from the server. They contain the (potentially empty) {@link DebugInfo} and the
 * actual result of the server request.
 *
 * @param <T> The type of the server result
 * @author Sebastian Raubach
 */
public class PaginatedServerResult<T> extends ServerResult<T>
{
	private static final long    serialVersionUID = -4128618658515217389L;
	private              Integer resultSize       = null;

	PaginatedServerResult()
	{

	}

	public PaginatedServerResult(DebugInfo debugInfo, T serverResult, Integer resultSize)
	{
		super(debugInfo, serverResult);
		this.resultSize = resultSize;
	}

	public PaginatedServerResult(DebugInfo debugInfo, T serverResult, ServerResult<Integer> resultSize)
	{
		this(debugInfo, serverResult, resultSize.getServerResult());
		setResultSizeAndDebugInfo(resultSize);
	}

	public Integer getResultSize()
	{
		return resultSize;
	}

	public PaginatedServerResult<T> setResultSize(Integer resultSize)
	{
		this.resultSize = resultSize;
		return this;
	}

	public PaginatedServerResult<T> setResultSizeAndDebugInfo(ServerResult<Integer> countQueryResult)
	{
		if (countQueryResult != null)
		{
			this.resultSize = countQueryResult.getServerResult();

			if (this.debugInfo != null)
				this.debugInfo.addAll(countQueryResult.getDebugInfo());
		}
		return this;
	}
}
