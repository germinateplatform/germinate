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

package jhi.germinate.client.util;

import com.google.gwt.i18n.client.*;

import java.util.*;

import jhi.germinate.client.i18n.*;

/**
 * {@link DateUtils} is a utility class containing methods evolving around dates. This class can be used to get localized month names as well as
 * localized dates in general.
 *
 * @author Sebastian Raubach
 */
public class DateUtils
{
	private static final DateTimeFormat FORMAT_DATABASE            = DateTimeFormat.getFormat("yyyy-MM-dd");
	private static final DateTimeFormat FORMAT_DATABASE_DATE_TIME  = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss'.0'");
	private static final DateTimeFormat FORMAT_FILE_NAME_OUTPUT    = DateTimeFormat.getFormat("yyyy-MM-dd-HH-mm-ss");
	private static final DateTimeFormat FORMAT_DATABASE_YEAR       = DateTimeFormat.getFormat("yyyy");
	private static final DateTimeFormat FORMAT_LOCALIZED           = DateTimeFormat.getFormat(Text.LANG.generalDateFormatShort());
	private static final DateTimeFormat FORMAT_LOCALIZED_DATE_TIME = DateTimeFormat.getFormat(Text.LANG.generalDateTimeFormat());

	public static final String[] MONTHS      = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsFull();
	public static final String[] MONTHS_ABBR = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().monthsShort();

	/**
	 * Returns the localized month given the month index
	 *
	 * @param month The month index (1 <= index <= 12)
	 * @return The localized month
	 */
	public static String getLocalizedMonth(int month)
	{
		return MONTHS[month - 1];
	}

	/**
	 * Returns the abbreviation of the localized month given the month index
	 *
	 * @param month The month index (1 <= index <= 12)
	 * @return The abbreviation of the localized month
	 */
	public static String getLocalizedMonthAbbr(int month)
	{
		return MONTHS_ABBR[month - 1];
	}

	/**
	 * Tries to parse the given date and returns an array containing day, month and year (input format: "yyyy-MM-dd")
	 *
	 * @param date The date to parse
	 * @return Day, month and year
	 */
	public static int[] getDayMonthYearFromDatabaseString(String date)
	{
		try
		{
			FORMAT_DATABASE.parse(date);
		}
		catch (Exception e)
		{
			return new int[]{1, 1, 1960};
		}

		String[] parts = date.split("-");

		int day = Integer.parseInt(parts[2]);
		int month = Integer.parseInt(parts[1]);
		int year = Integer.parseInt(parts[0]);

		return new int[]{day, month, year};
	}

	/**
	 * Tries to parse the given date and returns the time as a long (input format: "yyyy-MM-dd")
	 *
	 * @param date The date to parse
	 * @return The time as long
	 */
	public static Date getDateFromDatabaseString(String date)
	{
		return FORMAT_DATABASE.parse(date);
	}

	/**
	 * Tries to parse the given date and returns the time as a long (input format: "yyyy-MM-dd HH:mm:ss.0")
	 *
	 * @param date The date to parse
	 * @return The time as long
	 */
	public static long getDateTimeFromDatabaseString(String date)
	{
		return FORMAT_DATABASE_DATE_TIME.parse(date).getTime();
	}

	/**
	 * Returns the input time formatted as 'yyyy-MM-dd'
	 *
	 * @param input The time
	 * @return The time formatted as 'yyyy-MM-dd'
	 */
	public static String getDatabaseDate(Long input)
	{
		try
		{
			return FORMAT_DATABASE.format(new Date(input));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Returns the input time as a localized formatted string
	 *
	 * @param date The time
	 * @return The localized formatted string
	 */
	public static String getLocalizedDate(Long date)
	{
		if (date == null)
			return null;
		return FORMAT_LOCALIZED.format(new Date(date));
	}

	/**
	 * Returns the input time as a localized formatted string
	 *
	 * @param date The time
	 * @return The localized formatted string
	 */
	public static String getLocalizedDate(Date date)
	{
		if (date == null)
			return null;
		return FORMAT_LOCALIZED.format(date);
	}

	/**
	 * Returns the input time formatted as 'yyyy-MM-dd'
	 *
	 * @param date The date to parse
	 * @return The time formatted as 'yyyy-MM-dd'
	 */
	public static Date getDatabaseDate(String date)
	{
		return FORMAT_DATABASE.parse(date);
	}

	/**
	 * Returns the input time as a localized formatted string
	 *
	 * @param date The date to parse
	 * @return The localized formatted string
	 */
	public static Date getLocalizedDate(String date)
	{
		return FORMAT_LOCALIZED.parse(date);
	}

	/**
	 * Returns the input time as a localized formatted string
	 *
	 * @param input The time
	 * @return The localized formatted string
	 */
	public static String getLocalizedDateTime(Long input)
	{
		try
		{
			return FORMAT_LOCALIZED_DATE_TIME.format(new Date(input));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String getFilenameForDate(long input)
	{
		return FORMAT_FILE_NAME_OUTPUT.format(new Date(input));
	}

	public static int compare(Date first, Date second)
	{
		if (first == null && second == null)
			return 0;
		else if (first == null)
			return 1;
		else if (second == null)
			return -1;
		else
			return first.compareTo(second);
	}

	public static Date getYear(String value)
	{
		return FORMAT_DATABASE_YEAR.parse(value);
	}

	public static String getCurrentYear()
	{
		return FORMAT_DATABASE_YEAR.format(new Date());
	}
}