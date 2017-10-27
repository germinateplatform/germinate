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

package jhi.germinate.server.util;

import java.awt.image.*;
import java.io.*;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

import javax.imageio.*;
import javax.servlet.http.*;

import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.search.*;

/**
 * {@link Util} contains various utility methods.
 *
 * @author Sebastian Raubach
 */
public class Util
{
	private static final SimpleDateFormat SDF_DATE_TIME = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
	public static final  SimpleDateFormat SDF_DATE      = new SimpleDateFormat("yyyy-MM-dd");

	public enum OperatingSystem
	{
		LINUX(Pattern.compile("\\(.*?linux.*?\\)|\\(.*?x11.*?\\)"), "\n"),
		WINDOWS(Pattern.compile("\\(.*?windows.*?\\)"), "\r\n"),
		MAC(Pattern.compile("\\(.*?mac.*?\\)"), "\n");

		private final Pattern pattern;
		private final String  newLine;

		OperatingSystem(Pattern pattern, String newLine)
		{
			this.pattern = pattern;
			this.newLine = newLine;
		}

		private static OperatingSystem parseFromString(String input)
		{
			/* If there is no user-agent, assume linux */
			if (StringUtils.isEmpty(input))
				return LINUX;


			input = input.toLowerCase();
			if (LINUX.pattern.matcher(input).find())
				return LINUX;
			else if (WINDOWS.pattern.matcher(input).find())
				return WINDOWS;
			else if (MAC.pattern.matcher(input).find())
				return MAC;
			else
				return LINUX;
		}

		public Pattern getPattern()
		{
			return pattern;
		}

		public String getNewLine()
		{
			return newLine;
		}
	}

	/**
	 * Returns the formatted datetime of the {@link Long} obtained from {@link System#currentTimeMillis()}.
	 *
	 * @return The formatted date of the {@link Long} obtained from {@link System#currentTimeMillis()}.
	 */
	public static synchronized String getDateTime()
	{
		return SDF_DATE_TIME.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * Returns the formatted date of the {@link Long} obtained from {@link System#currentTimeMillis()}.
	 *
	 * @return The formatted date of the {@link Long} obtained from {@link System#currentTimeMillis()}.
	 */
	public static synchronized String getDate()
	{
		return SDF_DATE.format(new Date(System.currentTimeMillis()));
	}

	public static String formatDate(long value)
	{
		return SDF_DATE.format(new Date(value));
	}

	/**
	 * Joins the given input List with the given delimiter into a String
	 *
	 * @param input     The input array
	 * @param delimiter The delimiter to use
	 * @param checkSql  Check each part for validity, i.e. no SQL injection?
	 * @return The joined String
	 */
	public static <T> String joinCollection(Collection<T> input, String delimiter, boolean checkSql)
	{
		if (CollectionUtils.isEmpty(input))
			return "";

		StringBuilder builder = new StringBuilder();

		String joined = input.stream()
							 .filter(t -> checkSql && SearchCondition.checkSqlString(t.toString(), false))
							 .map(T::toString)
							 .collect(Collectors.joining(delimiter));

		builder.append(joined);

		return builder.toString();
	}

	/**
	 * Sets the dimenstions of the image file to the Image object
	 *
	 * @param image The image
	 * @param file  The file
	 * @throws IOException Thrown if I/O interaction with the image fails
	 */
	public static void setImageDimensions(Image image, File file) throws IOException
	{
		if (image == null || !file.exists())
			return;

		BufferedImage img = ImageIO.read(file);

		image.setWidth(img.getWidth());
		image.setHeight(img.getHeight());
	}

	/**
	 * Checks if the given {@link String} is contained in the given {@link String}[]. If so, it will be returned. If not, an exception will be thrown.
	 * If the given {@link String} is {@link StringUtils#isEmpty(String)}, the fallback will be returned.
	 *
	 * @param searchColumn     The given search column
	 * @param availableColumns The array of valid column names
	 * @param fallbackColumn   The fallback solution if the input is empty
	 * @return The given column or the fallback
	 * @throws InvalidColumnException Thrown if the given column is not empty AND is not contained in the given array.
	 */
	public static String checkSortColumn(String searchColumn, String[] availableColumns, String fallbackColumn) throws InvalidColumnException
	{
		if (StringUtils.isEmpty(searchColumn))
		{
			if (StringUtils.isEmpty(fallbackColumn))
				throw new InvalidColumnException(searchColumn + " is not a valid column.");
			else
				return fallbackColumn;
		}
		for (String available : availableColumns)
		{
			if (available.equals(searchColumn))
				return available;
		}

		if (searchColumn.contains(","))
		{
			String[] columns = StringUtils.splitAndTrim(searchColumn, ",");

			if (columns != null)
			{
				for (String c : columns)
					checkSortColumn(c, availableColumns, fallbackColumn);
			}

			return searchColumn;
		}

		if (searchColumn.contains("."))
		{
			for (GerminateDatabaseTable table : GerminateDatabaseTable.values())
			{
				for (String available : availableColumns)
				{
					if ((table.name() + "." + available).equals(searchColumn))
						return table.name() + "." + available;
				}
			}
		}

		throw new InvalidColumnException(searchColumn + " is not a valid column.");
	}

	/**
	 * Writes the given {@link GerminateTable} to the given {@link File}.
	 *
	 * @param os      The {@link OperatingSystem}
	 * @param columns The columns to export (data will be exported in this get)
	 * @param table   The {@link GerminateTable} to export
	 * @param file    The {@link File} to create
	 * @throws IOException Thrown if the file interaction fails
	 */
	public static void writeGerminateTableToFile(OperatingSystem os, String[] columns, GerminateTable table, File file) throws IOException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")))
		{
			if (ArrayUtils.isEmpty(columns))
				columns = table.getColumnNames();

			bw.write(Arrays.stream(columns)
						   .collect(Collectors.joining("\t")));
			bw.write(os.newLine);

			for (GerminateRow row : table)
			{
				String value;
				bw.write(row.get(columns[0]) == null ? "" : row.get(columns[0]));

				for (int i = 1; i < columns.length; i++)
				{
					value = row.get(columns[i]) == null ? "" : row.get(columns[i]);
					bw.write("\t" + value);
				}

				// bw.newLine();
				bw.write(os.newLine);
			}
		}
	}

	/**
	 * Writes the given {@link GerminateTableStreamer} to the given {@link File}
	 *
	 * @param os      The {@link OperatingSystem}
	 * @param columns The columns to export (data will be exported in this get)
	 * @param table   The {@link GerminateTableStreamer} to export
	 * @param file    The {@link File} to create
	 * @return The number or rows that have been exported and the first exported {@link GerminateRow}
	 * @throws IOException       Thrown if the file interaction fails
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	public static Pair<Integer, GerminateRow> writeGerminateTableToFile(OperatingSystem os, String[] columns, GerminateTableStreamer table, File file) throws IOException,
			DatabaseException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
			 GerminateTableStreamer streamer = table)
		{
			if (ArrayUtils.isEmpty(columns))
				columns = streamer.getColumnNames();

			bw.write(Arrays.stream(columns)
						   .collect(Collectors.joining("\t")));
			bw.write(os.newLine);

			int counter = 0;

			GerminateRow first = null;
			GerminateRow row;
			while ((row = streamer.next()) != null)
			{
				if (first == null)
					first = row;

				String value;
				bw.write(row.get(columns[0]) == null ? "" : row.get(columns[0]));

				for (int i = 1; i < columns.length; i++)
				{
					value = row.get(columns[i]) == null ? "" : row.get(columns[i]);
					bw.write("\t" + value);
				}

				// bw.newLine();
				bw.write(os.newLine);
				counter++;
			}

			return new Pair<>(counter, first);
		}
	}

	/**
	 * Generates a SQL placeholder String of the form: "?,?,?,?" for the given size.
	 *
	 * @param size The number of placeholders to generate
	 * @return The generated String
	 */
	public static String generateSqlPlaceholderString(int size)
	{
		if (size < 1)
			return "";

		return IntStream.range(0, size)
						.mapToObj(value -> "?")
						.collect(Collectors.joining(","));
	}

	/**
	 * Returns the {@link OperatingSystem} based on the information in the {@link HttpServletRequest} (user-agent).
	 *
	 * @param request The current {@link HttpServletRequest}
	 * @return The matching {@link OperatingSystem} (or {@link OperatingSystem#LINUX} if none match.
	 */
	public static OperatingSystem getOperatingSystem(HttpServletRequest request)
	{
		return OperatingSystem.parseFromString(request.getHeader("User-Agent"));
	}
}
