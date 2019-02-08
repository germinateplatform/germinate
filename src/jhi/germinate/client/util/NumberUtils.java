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

	private static final NavigableMap<Long, String> suffixes    = new TreeMap<>();
	private static final NavigableMap<Long, String> suffixesBit = new TreeMap<>();

	static
	{
		suffixes.put(1000L, "k");
		suffixes.put(1000000L, "M");
		suffixes.put(1000000000L, "G");
		suffixes.put(1000000000000L, "T");
		suffixes.put(1000000000000000L, "P");
		suffixes.put(1000000000000000000L, "E");

		suffixesBit.put(1024L, "k");
		suffixesBit.put(1048576L, "M");
		suffixesBit.put(1073741824L, "G");
		suffixesBit.put(1099511627776L, "T");
		suffixesBit.put(1125899906842624L, "P");
		suffixesBit.put(1152921504606846976L, "E");
	}

	public static String format(long value, boolean bit)
	{
		//Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1, bit);
		if (value < 0) return "-" + format(-value, bit);
		if (value < (bit ? 1024 : 1000)) return Long.toString(value); //deal with easy case

		Map.Entry<Long, String> e = (bit ? suffixesBit : suffixes).floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		long truncated = value / (divideBy / 10); //the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

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
