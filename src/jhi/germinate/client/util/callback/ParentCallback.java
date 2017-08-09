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

package jhi.germinate.client.util.callback;

public interface ParentCallback
{
	/**
	 * Called when the {@link ParentCallback} finishes successfully
	 */
	void handleSuccess();

	/**
	 * Called when the {@link ParentCallback} fails
	 *
	 * @param reason The reason for the failure
	 */
	void handleFailure(Exception reason);

	/**
	 * Called by the child {@link ParallelCallback}s on completion of the service call. Only when all children have completed does this parent kick
	 * off it's {@link #handleSuccess()} or {@link #handleFailure(Exception)} method depending on the success of the children.
	 */
	void done();

	/**
	 * Called by the child {@link ParallelCallback}s on failure of the service call. Only when all children have completed does this parent kick of
	 * it's {@link #handleFailure(Exception)} method.
	 *
	 * @param reason The exception
	 */
	void failed(Exception reason);

	<T> T getCallbackData(int index);
}
