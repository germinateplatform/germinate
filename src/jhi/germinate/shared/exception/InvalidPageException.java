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
 * {@link InvalidPageException} extends {@link BaseException} to indicate that the selected page is invalid.
 *
 * @author Sebastian Raubach
 */
public class InvalidPageException extends BaseException implements Serializable
{
	private static final long serialVersionUID = -1835115907553089898L;

	private String page;

	public InvalidPageException()
	{
		super();
	}

	public InvalidPageException(String message)
	{
		super(message);

		this.page = message;
	}

	public InvalidPageException(Exception e)
	{
		super(e);
	}

	public String getPage()
	{
		return page;
	}

	public void setPage(String page)
	{
		this.page = page;
	}
}
