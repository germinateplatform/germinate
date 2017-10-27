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
 * {@link InvalidDatabaseTypeException} extends {@link DatabaseException} which will be thrown if the requested {@link
 * jhi.germinate.server.database.Database.DatabaseType} doesn't exist.
 *
 * @author Sebastian Raubach
 */
public class InvalidDatabaseTypeException extends DatabaseException implements Serializable
{
	private static final long serialVersionUID = 7493616630596282306L;

	public InvalidDatabaseTypeException()
	{
		super();
	}

	public InvalidDatabaseTypeException(String message)
	{
		super(message);
	}

	public InvalidDatabaseTypeException(Exception e)
	{
		super(e);
	}
}
