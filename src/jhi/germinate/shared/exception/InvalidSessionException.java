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

package jhi.germinate.shared.exception;

import java.io.*;

/**
 * {@link InvalidSessionException} extends {@link BaseException} to indicate that the session id used for the communication with the server is
 * not valid.
 *
 * @author Sebastian Raubach
 */
public class InvalidSessionException extends BaseException implements Serializable
{
	private static final long serialVersionUID = 140238143688252524L;

	public enum InvalidProperty
	{
		INVALID_COOKIE,
		INVALID_SESSION,
		INVALID_PAYLOAD,
		UNKNOWN
	}

	private InvalidProperty invalidProperty = InvalidProperty.UNKNOWN;

	public InvalidSessionException()
	{
		super();
	}

	public InvalidSessionException(InvalidProperty cause)
	{
		super();
		this.invalidProperty = cause;
	}

	public InvalidSessionException(InvalidProperty cause, String message)
	{
		super(message);
		this.invalidProperty = cause;
	}

	public InvalidSessionException(InvalidProperty cause, Exception e)
	{
		super(e);
		this.invalidProperty = cause;
	}

	public InvalidProperty getInvalidProperty()
	{
		return invalidProperty;
	}

	public void setInvalidProperty(InvalidProperty cause)
	{
		this.invalidProperty = cause;
	}
}
