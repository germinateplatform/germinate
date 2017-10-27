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

package jhi.germinate.client.widget.table;

import com.google.gwt.safehtml.shared.*;

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.*;

/**
 * @author Sebastian Raubach
 */
public class TableUtils
{
	public static boolean isEvent(String toCheck, String... checkAgainst)
	{
		for (String c : checkAgainst)
		{
			if (Objects.equals(c, toCheck))
				return true;
		}

		return false;
	}

	/**
	 * Returns the hyperlink cell content. This method will either return a dummy anchor (if hyperlink is empty or null), a simple text (if value is
	 * emty or null) or a proper anchor (else).
	 *
	 * @param value     The cell content
	 * @param hyperlink The hyperlink
	 * @return The hyperlink cell content
	 */
	public static SafeHtml getHyperlinkValue(Long value, String hyperlink)
	{
		if (value == null)
			return SimpleHtmlTemplate.INSTANCE.text("");
		else
			return getHyperlinkValue(Long.toString(value), hyperlink);
	}

	/**
	 * Returns the hyperlink cell content. This method will either return a dummy anchor (if hyperlink is empty or null), a simple text (if value is
	 * emty or null) or a proper anchor (else).
	 *
	 * @param value     The cell content
	 * @param hyperlink The hyperlink
	 * @return The hyperlink cell content
	 */
	public static SafeHtml getHyperlinkValue(String value, String hyperlink)
	{
		if (value == null)
			value = "";

		/*
		 * If the hyperlink is empty, emulate a link and let the
		 * <code>handleSelectionEvent()</code> method handle the navigation
		 */
		if (StringUtils.isEmpty(hyperlink))
		{
			return SimpleHtmlTemplate.INSTANCE.dummyAnchor(value);
		}
		/* Else add a real anchor */
		else
		{
			SafeUri href = UriUtils.fromString(hyperlink);
			if (StringUtils.isEmpty(value))
				return SimpleHtmlTemplate.INSTANCE.text("");
			else
				return SimpleHtmlTemplate.INSTANCE.anchor(href, value);
		}
	}

	public static SafeHtml getHyperlinkValueWithIcon(String value, String hyperlink, String style)
	{
		if (StringUtils.isEmpty(hyperlink))
		{
			return SimpleHtmlTemplate.INSTANCE.dummyAnchorWithIcon(value, style);
		}
		else
		{
			SafeUri href = UriUtils.fromString(hyperlink);
			if (StringUtils.isEmpty(value))
				return SimpleHtmlTemplate.INSTANCE.textWithIcon("", style);
			else
				return SimpleHtmlTemplate.INSTANCE.anchorWithIcon(href, value, style);
		}
	}

	/**
	 * Returns the hyperlink cell content. This method will either return a dummy anchor (if hyperlink is empty or null), a simple text (if value is
	 * emty or null) or a proper anchor (else). Adds additional padding.
	 *
	 * @param value     The cell content
	 * @param hyperlink The hyperlink
	 * @return The hyperlink cell content
	 */
	public static SafeHtml getHyperlinkValuePadded(Long value, String hyperlink)
	{
		if (value == null)
			return SimpleHtmlTemplate.INSTANCE.textPadded("");
		else
			return getHyperlinkValuePadded(Long.toString(value), hyperlink);
	}

	/**
	 * Returns the hyperlink cell content. This method will either return a dummy anchor (if hyperlink is empty or null), a simple text (if value is
	 * emty or null) or a proper anchor (else). Adds additional padding.
	 *
	 * @param value     The cell content
	 * @param hyperlink The hyperlink
	 * @return The hyperlink cell content
	 */
	public static SafeHtml getHyperlinkValuePadded(String value, String hyperlink)
	{
		if (value == null)
			value = "";

            /*
			 * If the hyperlink is empty, emulate a link and let the
             * <code>handleSelectionEvent()</code> method handle the navigation
             */
		if (StringUtils.isEmpty(hyperlink))
		{
			return SimpleHtmlTemplate.INSTANCE.dummyAnchor(value);
		}
			/* Else add a real anchor */
		else
		{
			SafeUri href = UriUtils.fromString(hyperlink);
			if (StringUtils.isEmpty(value))
				return SimpleHtmlTemplate.INSTANCE.textPadded("");
			else
				return SimpleHtmlTemplate.INSTANCE.anchorPadded(href, value);
		}
	}

	/**
	 * Returns the cell content. This method will try to parse the content as a date and as a number and return
	 *
	 * @param value The text to wrap
	 * @return The wrapped content
	 */
	public static SafeHtml getCellValue(String value)
	{
		value = getCellValueAsString(value);

		if (value != null)
			return SimpleHtmlTemplate.INSTANCE.text(value);
		else
			return SimpleHtmlTemplate.INSTANCE.empty();
	}

	/**
	 * Returns the cell content. This method will try to parse the content as a date and as a number and return
	 *
	 * @param value The text to wrap
	 * @return The wrapped content
	 */
	public static SafeHtml getCellValueWithIcon(String value, String style)
	{
		value = getCellValueAsString(value);

		if (value != null)
			return SimpleHtmlTemplate.INSTANCE.textWithIcon(value, style);
		else
			return SimpleHtmlTemplate.INSTANCE.textWithIcon("", style);
	}

	public static String getCellValueAsString(String value, Class<?> clazz)
	{
		if (value != null)
		{
			if (Objects.equals(Date.class, clazz))
			{
				/* Check if it's a date */
				if (value.matches("^\\d{4}-\\d{2}-\\d{2}$"))
				{
					try
					{
						value = DateUtils.getLocalizedDate(DateUtils.getDateFromDatabaseString(value));
					}
					catch (IllegalArgumentException e)
					{
					}
				}
				/* Check if it's a date time */
				else if (value.matches("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}$"))
				{
					try
					{
						value = DateUtils.getLocalizedDateTime(DateUtils.getDateTimeFromDatabaseString(value));
					}
					catch (IllegalArgumentException e)
					{
					}
				}
			}
			else if (Objects.equals(Integer.class, clazz))
			{
				try
				{
					int intValue = Integer.parseInt(value);
					value = NumberUtils.INTEGER_FORMAT.format(intValue);
				}
				catch (NumberFormatException e)
				{
				}
			}
			else if (Objects.equals(Double.class, clazz))
			{
				try
				{
					double doubleValue = Double.parseDouble(value);
					value = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(doubleValue);
				}
				catch (NumberFormatException e)
				{
				}
			}
		}

		return value;
	}

	/**
	 * Returns the cell content as a string (i.e. without using the {@link SimpleHtmlTemplate#INSTANCE}).
	 *
	 * @param value The text to process
	 * @return The processed content
	 */
	public static String getCellValueAsString(String value)
	{
		if (value != null)
		{
			/* Check if it's a date */
			if (value.matches("^\\d{4}-\\d{2}-\\d{2}$"))
			{
				try
				{
					value = DateUtils.getLocalizedDate(DateUtils.getDateFromDatabaseString(value));
				}
				catch (IllegalArgumentException e)
				{
				}
			}
			else if (value.matches("^\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}$"))
			{
				/* Check if it's a date time */
				try
				{
					value = DateUtils.getLocalizedDateTime(DateUtils.getDateTimeFromDatabaseString(value));
				}
				catch (IllegalArgumentException e)
				{
				}
			}
			else
			{
				/* Check if it's a number */
				try
				{
					double doubleValue = Double.parseDouble(value);

					/*
					 * If it's either a double, or of it's an integer, but
					 * it's formatted as a double, e.g. 208.0000
					 */
					if (doubleValue != (int) doubleValue)
					{
						value = NumberUtils.DECIMAL_FORMAT_TWO_PLACES.format(doubleValue);
					}
					else if (value.matches("^[-+]?\\d*\\.0+$"))
					{
						value = NumberUtils.INTEGER_FORMAT.format(doubleValue);
					}
					else if (value.matches("^[-+]?\\d*$"))
					{
						value = NumberUtils.INTEGER_FORMAT.format(doubleValue);
					}
				}
				catch (NumberFormatException e)
				{
				}
			}

			return value;
		}
		else
			return null;
	}

	/**
	 * Returns the cell content as a string (i.e. without using the {@link SimpleHtmlTemplate#INSTANCE}).
	 *
	 * @param value The long to process
	 * @return The processed content
	 */
	public static String getCellValueAsString(Long value)
	{
		if (value != null)
			return getCellValueAsString(Long.toString(value));
		else
			return null;
	}
}
