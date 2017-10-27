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
 * {@link InvalidSearchQueryException} extends {@link BaseException} that will be used when the creation of a {@link
 * jhi.germinate.shared.search.SearchCondition} fails.
 *
 * @author Sebastian Raubach
 */
public class InvalidSearchQueryException extends BaseException implements Serializable
{
	private static final long serialVersionUID = 348295520618866981L;

	public InvalidSearchQueryException()
	{
		super();
	}

	public InvalidSearchQueryException(String message)
	{
		super(message);
	}

	public InvalidSearchQueryException(Exception e)
	{
		super(e);
	}
}
