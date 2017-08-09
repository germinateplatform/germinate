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

import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.client.i18n.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.Notification.*;
import jhi.germinate.client.util.event.*;
import jhi.germinate.client.widget.element.*;
import jhi.germinate.client.widget.structure.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link DefaultAsyncCallback} is an {@link AsyncCallback} that will take care of most failure cases. As an example: An {@link
 * InvalidSessionException} will result in the user being logged out.
 *
 * @param <T> The type of the return value that was declared in the synchronous version of the method. If the return type is a primitive, use the
 *            boxed version of that primitive (for example, an <code>int</code> return type becomes an {@link Integer} type argument, and a void
 *            return type becomes a {@link Void} type argument, which is always <code>null</code>).
 * @author Sebastian Raubach
 */
public class DefaultAsyncCallback<T> implements AsyncCallback<T>
{
	private LoadingIndicator indicator;

	public DefaultAsyncCallback()
	{
		this(false);
	}

	public DefaultAsyncCallback(boolean addLoadingIndicator)
	{
		super();

		Cookie.extend();

		if (addLoadingIndicator)
		{
			indicator = new LoadingIndicator(Text.LANG.notificationLongRunning());
			indicator.show();
		}
	}

	@Override
	public final void onFailure(Throwable caught)
	{
		if (indicator != null)
			indicator.hide();

		onFailureImpl(caught);
	}

	public void forceClose()
	{
		if (indicator != null)
			indicator.hide();
	}

	/**
	 * Called when an asynchronous call fails to complete normally. {@link IncompatibleRemoteServiceException}s, {@link InvocationException} s, or
	 * checked exceptions thrown by the service method are examples of the type of failures that can be passed to this method.
	 * <p/>
	 * If caught is an instance of an {@link IncompatibleRemoteServiceException} the application should try to get into a state where a browser
	 * refresh can be safely done.
	 *
	 * @param caught failure encountered while executing a remote procedure call
	 * @see #onFailure(Throwable)
	 */
	protected void onFailureImpl(Throwable caught)
	{
		if (caught instanceof IncompatibleRemoteServiceException)
		{
			if (!StringUtils.isEmpty(caught.getMessage()) && SystemUnderMaintenanceException.isInstance(caught))
			{
				GerminateEventBus.BUS.fireEvent(new ExceptionEvent(new SystemUnderMaintenanceException(new Exception(caught))));
			}
			else
			{
				Notification.notify(Type.ERROR, Text.LANG.notificationClientTooOld() + " " + Text.LANG.notificationReloadPage());
			}
		}
		else
		{
			GerminateEventBus.BUS.fireEvent(new ExceptionEvent(caught));
		}
	}

	@Override
	public final void onSuccess(T result)
	{
		if (indicator != null)
			indicator.hide();

		if (result instanceof ServerResult)
		{
			ServerResult sr = (ServerResult) result;
			DebugInfoPanel.addDebugInfo(getName(sr), sr.getDebugInfo());
		}

		onSuccessImpl(result);
	}

	public static String getName(Object object)
	{
		if (object instanceof PaginatedServerResult)
		{
			PaginatedServerResult serverResult = (PaginatedServerResult) object;
			return "ServerResult<" + getName(serverResult.getServerResult()) + ">: " + serverResult.getResultSize();
		}
		else if (object instanceof ServerResult)
		{
			ServerResult serverResult = (ServerResult) object;
			return "ServerResult<" + getName(serverResult.getServerResult()) + ">";
		}
		else if (object instanceof List)
		{
			List list = (List) object;
			if (!CollectionUtils.isEmpty(list))
				return "List<" + getName(list.get(0)) + ">: " + list.size();
			else
				return "Empty List<?>";
		}
		else if (object instanceof Set)
		{
			Set set = (Set) object;
			if (!CollectionUtils.isEmpty(set))
				return "Set<" + getName(set.iterator().next()) + ">: " + set.size();
			else
				return "Empty Set<?>";
		}
		else if (object instanceof Map)
		{
			Map map = (Map) object;
			if (map != null && map.size() > 0)
			{
				Map.Entry<?, ?> entry = (Map.Entry) map.entrySet().iterator().next();
				return "Map<" + getName(entry.getKey()) + ", " + getName(entry.getValue()) + ">: " + map.size();
			}
			else
				return "Empty Map<?, ?>";
		}
		else if (object != null)
		{
			return object.getClass().getSimpleName();
		}
		else
		{
			return "";
		}
	}

	/**
	 * Called when an asynchronous call completes successfully.
	 *
	 * @param result the return value of the remote produced call
	 * @see #onSuccess(Object)
	 */
	protected void onSuccessImpl(T result)
	{
	}
}
