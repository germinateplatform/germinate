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

package jhi.germinate.client.util.callback;

import java.util.*;

/**
 * {@link IterativeParentCallback} is the parent of {@link ParallelCallback} objects, used when 2 or more service calls should run in parallel, and
 * only when each is complete should something happen. The parent waits till all children are done/failed, then kicks off the {@link #handleSuccess()}
 * {@link #handleFailure(Exception)} respectively.
 *
 * @author Ben Northrop
 * @author Sebastian Raubach
 */
public abstract class IterativeParentCallback implements ParentCallback
{

	/** The number of service calls that have successfully completed. */
	private int doneCount = 0;

	private int calledCounter = -1;

	/**
	 * The children callbacks for which this parent checks to see if they are done.
	 */
	private ParallelCallback<?, ?>[] childCallbacks;

	private Exception failureReason = null;
	private boolean   failed        = false;

	/**
	 * Default constructor, passing in all child callbacks for the parent to check if they are done.
	 */
	public IterativeParentCallback(ParallelCallback<?, ?>... callbacks)
	{
		if (callbacks == null || callbacks.length == 0)
		{
			this.handleSuccess();
		}
		else
		{
			this.childCallbacks = callbacks;

			callNext();
		}
	}

	public IterativeParentCallback(List<ParallelCallback<?, ?>> callbacks)
	{
		this(callbacks.toArray(new ParallelCallback<?, ?>[callbacks.size()]));
	}

	protected synchronized void callNext()
	{
		calledCounter++;
		if (childCallbacks.length > calledCounter)
		{
			if (childCallbacks[calledCounter] == null)
				done();
			else
			{
				childCallbacks[calledCounter].setParent(this);
				childCallbacks[calledCounter].start();
			}
		}
	}

	@Override
	public synchronized void done()
	{
		doneCount++;

		if (doneCount == childCallbacks.length)
		{
			if (failed)
				handleFailure(failureReason);
			else
				handleSuccess();
		}
		else
			callNext();
	}

	@Override
	public synchronized void failed(Exception reason)
	{
		doneCount++;
		failureReason = reason;
		failed = true;

		if (doneCount == childCallbacks.length)
			handleFailure(reason);
		else
			callNext();
	}

	/**
	 * Get the data from the callback. Should only be called within the handleSuccess() block.
	 */
	@Override
	public <T> T getCallbackData(int index)
	{
		if (index < 0 || index >= childCallbacks.length)
		{
			throw new RuntimeException("Invalid child callback index");
		}

		return (T) childCallbacks[index].getData();
	}
}
