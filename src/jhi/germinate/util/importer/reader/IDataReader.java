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

package jhi.germinate.util.importer.reader;

import org.apache.poi.openxml4j.exceptions.*;

import java.io.*;
import java.text.*;
import java.util.*;

import jhi.germinate.shared.*;

/**
 * {@link IDataReader} extends {@link AutoCloseable} and defines the {@link #init(InputStream)} method that should always be called first.
 *
 * @author Sebastian Raubach
 */
public interface IDataReader extends AutoCloseable
{
	SimpleDateFormat SDF_FULL_DASH  = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat SDF_FULL       = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat SDF_YEAR_MONTH = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat SDF_YEAR_DAY   = new SimpleDateFormat("yyyydd");
	SimpleDateFormat SDF_YEAR       = new SimpleDateFormat("yyyy");

	/**
	 * Passes the {@link File} to the {@link IDataReader} for initial preparations.
	 *
	 * @param is The {@link File} that contains the data.
	 * @throws IOException Thrown if the I/O fails.
	 */
	void init(File is) throws IOException, InvalidFormatException;

	public static Date getDate(String value)
	{
		Date date = null;
		if (!StringUtils.isEmpty(value))
		{
			if (value.length() == 10)
			{
				try
				{
					date = SDF_FULL_DASH.parse(value);
				}
				catch (Exception e)
				{
				}
			}
			else
			{
				// Replace all hyphens with zeros so that we only have one case to handle.
				value = value.replace("-", "0");

				try
				{
					boolean noMonth = value.substring(4, 6).equals("00");
					boolean noDay = value.substring(6, 8).equals("00");

					if (noDay && noMonth)
						date = SDF_YEAR.parse(value.substring(0, 4));
					else if (noDay)
						date = SDF_YEAR_MONTH.parse(value.substring(0, 6));
					else if (noMonth)
						date = SDF_YEAR_DAY.parse(value.substring(0, 4) + value.substring(6, 8));
					else
						date = SDF_FULL.parse(value);
				}
				catch (Exception e)
				{
				}
			}
		}

		return date;
	}
}
