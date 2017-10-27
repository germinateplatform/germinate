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

/**
 * This is used when 2 or more service calls should run in parallel, and only when each is complete/failed should something happen.
 *
 * @author Ben Northrop
 * @author Sebastian Raubach
 */
public abstract class ParallelAsyncCallback<T> extends DefaultAsyncCallback<T>
{

	/** The data that is returned from the service call. */
	private T data;

	/** A reference to the parent callback, which runs when all are complete. */
	private ParallelParentAsyncCallback parentCallback;

	/**
	 * Standard handleSuccess method, which is called when the service call completes.
	 */
	@Override
	public void onSuccessImpl(T t)
	{
		this.data = t;
		parentCallback.done();
	}

	/**
	 * Handle the error.
	 */
	@Override
	public void onFailureImpl(Throwable caught)
	{
		parentCallback.failed(new Exception(caught));
		super.onFailureImpl(caught);
	}

	protected abstract void start();

	/**
	 * Method that can be used by the parent callback to get the data from this service call and process it.
	 */
	public T getData()
	{
		return data;
	}

	/**
	 * Called by the parent callback, to inject a reference to itself into the child.
	 */
	protected void setParent(ParallelParentAsyncCallback parentCallback)
	{
		this.parentCallback = parentCallback;
	}
}
