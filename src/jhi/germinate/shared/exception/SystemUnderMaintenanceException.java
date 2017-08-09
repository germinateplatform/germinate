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

package jhi.germinate.shared.exception;

import com.google.gwt.user.client.rpc.*;

import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class SystemUnderMaintenanceException extends BaseException implements IsSerializable
{
	private static final long serialVersionUID = 2546548641199550605L;

	private static final int MAX_RECURSION_DEPTH = 10;

	public SystemUnderMaintenanceException()
	{
		super();
	}

	public SystemUnderMaintenanceException(String message)
	{
		super(message);
	}

	public SystemUnderMaintenanceException(Exception e)
	{
		super(e);
	}

	public static boolean isInstance(Throwable e)
	{
		return isInstance(e, 0);
	}

	public static boolean isInstance(Throwable e, int i)
	{
		if (e == null || StringUtils.isEmpty(e.getMessage()))
			return false;
		if (e instanceof SystemUnderMaintenanceException || e.getMessage().contains(SystemUnderMaintenanceException.class.getName()))
			return true;
		else if (++i < MAX_RECURSION_DEPTH)
			return isInstance(e.getCause(), i);
		else
			return false;
	}
}
