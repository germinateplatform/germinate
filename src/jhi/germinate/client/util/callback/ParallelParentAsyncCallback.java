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

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.widget.element.*;

/**
 * {@link ParallelParentAsyncCallback} is the parent of {@link ParallelCallback} objects, used when 2 or more service calls should run in parallel,
 * and only when each is complete should something happen. The parent waits till all children are done/failed, then kicks off the {@link
 * #handleSuccess()} {@link #handleFailure(Exception)} respectively.
 *
 * @author Ben Northrop
 * @author Sebastian Raubach
 */
public abstract class ParallelParentAsyncCallback implements ParentCallback
{

	/** The number of service calls that have successfully completed. */
	private int doneCount = 0;

	/**
	 * The children callbacks for which this parent checks to see if they are done.
	 */
	private ParallelAsyncCallback<?>[] childCallbacks;

	private Exception failureReason = null;
	private boolean   failed        = false;

	private LoadingIndicator indicator;

	/**
	 * Default constructor, passing in all child callbacks for the parent to check if they are done.
	 *
	 * @param addLoadingIndicator Should a loading indicator be shown?
	 * @param callbacks           The {@link ParallelAsyncCallback}s
	 */
	public ParallelParentAsyncCallback(boolean addLoadingIndicator, ParallelAsyncCallback<?>... callbacks)
	{
		if (callbacks == null || callbacks.length == 0)
		{
			throw new RuntimeException("No callbacks passed to parent");
		}

		if (addLoadingIndicator)
		{
			indicator = new LoadingIndicator(Text.LANG.notificationLongRunning());
			indicator.show();
		}

		this.childCallbacks = callbacks;

		for (ParallelAsyncCallback<?> callback : callbacks)
		{
			if (callback == null)
				done();
			else
			{
				callback.setParent(this);
				callback.start();
			}
		}
	}

	/**
	 * Default constructor, passing in all child callbacks for the parent to check if they are done.
	 *
	 * @param callbacks The {@link ParallelAsyncCallback}s
	 */
	public ParallelParentAsyncCallback(ParallelAsyncCallback<?>... callbacks)
	{
		this(false, callbacks);
	}

	@Override
	public final synchronized void done()
	{
		doneCount++;

		if (doneCount == childCallbacks.length)
		{
			if (indicator != null)
			{
				indicator.hide();
				indicator = null;
			}

			if (failed)
				handleFailure(failureReason);
			else
				handleSuccess();
		}
	}

	@Override
	public final synchronized void failed(Exception reason)
	{
		doneCount++;
		failureReason = reason;
		failed = true;

		if (doneCount == childCallbacks.length)
		{
			if (indicator != null)
			{
				indicator.hide();
				indicator = null;
			}

			handleFailure(reason);
		}
	}

	/**
	 * Get the data from the callback. Should only be called within the handleSuccess() block.
	 */
	@Override
	public <T> T getCallbackData(int index)
	{
		if (index < 0 || index >= childCallbacks.length)
			throw new RuntimeException("Invalid child callback index");

		if (childCallbacks[index] != null)
			return (T) childCallbacks[index].getData();
		else
			return null;
	}

	protected ParallelAsyncCallback<?> getChild(int i)
	{
		return childCallbacks[i];
	}
}
