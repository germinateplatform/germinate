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

import java.io.*;

/**
 * {@link InvalidOptionsException} extends {@link BaseException} to indicate that the selected {@link
 * jhi.germinate.shared.datastructure.MapExportOptions} doesn't contain all the necessary information.
 *
 * @author Sebastian Raubach
 */
public class InvalidOptionsException extends BaseException implements Serializable
{
	private static final long serialVersionUID = 140238143688252524L;

	public InvalidOptionsException()
	{
		super();
	}

	public InvalidOptionsException(String message)
	{
		super(message);
	}

	public InvalidOptionsException(Exception e)
	{
		super(e);
	}
}
