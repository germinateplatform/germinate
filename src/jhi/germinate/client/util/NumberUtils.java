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

/**
 * @author Sebastian Raubach
 */
public class NumberUtils
{
	/**
	 * The default decimal format as obtained from {@link NumberFormat#getDecimalFormat()}
	 */
	public static final NumberFormat DECIMAL_FORMAT             = NumberFormat.getDecimalFormat();
	/**
	 * The default decimal format as obtained from {@link NumberFormat#getDecimalFormat()} restricted to two decimal places
	 */
	public static final NumberFormat DECIMAL_FORMAT_TWO_PLACES  = NumberFormat.getFormat("#,##0.00");
	/**
	 * The default decimal format as obtained from {@link NumberFormat#getDecimalFormat()} restricted to four decimal places
	 */
	public static final NumberFormat DECIMAL_FORMAT_FOUR_PLACES = NumberFormat.getFormat("#,##0.0000");
	/**
	 * The format obtained from the pattern <code>"#,###"</code> with zero fraction digits
	 */
	public static final NumberFormat INTEGER_FORMAT             = NumberFormat.getFormat("#,###").overrideFractionDigits(0, 0);

	/**
	 * Parses a double from the given decimal in local format
	 *
	 * @param input The decimal in local format
	 * @return The double
	 */
	public static double toDouble(String input)
	{
		return DECIMAL_FORMAT.parse(input);
	}
}
