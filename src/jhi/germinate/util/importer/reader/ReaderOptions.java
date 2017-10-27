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

import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

/**
 * @author Sebastian Raubach
 */
public class ReaderOptions extends Options
{
	private static final String INPUT         = "i";
	private static final String INPUT_LONG    = "input";
	private static final String SERVER        = "s";
	private static final String SERVER_LONG   = "server";
	private static final String DATABASE      = "d";
	private static final String DATABASE_LONG = "database";
	private static final String USERNAME      = "u";
	private static final String USERNAME_LONG = "username";
	private static final String PASSWORD      = "p";
	private static final String PASSWORD_LONG = "password";
	private static final String PORT          = "t";
	private static final String PORT_LONG     = "port";
	private static final String READER        = "r";
	private static final String READER_LONG   = "reader";


	ReaderOptions withInputFile(boolean required)
	{
		return addOption(INPUT, INPUT_LONG, true, "FILE", "Input file", required);
	}

	ReaderOptions withServer(boolean required)
	{
		return addOption(SERVER, SERVER_LONG, true, "DATABASE_SERVER", "Database server", required);
	}

	ReaderOptions withDatabase(boolean required)
	{
		return addOption(DATABASE, DATABASE_LONG, true, "DATABASE", "Database", required);
	}

	ReaderOptions withUsername(boolean required)
	{
		return addOption(USERNAME, USERNAME_LONG, true, "USERNAME", "Database username", required);
	}

	ReaderOptions withPassword(boolean required)
	{
		return addOption(PASSWORD, PASSWORD_LONG, true, "PASSWORD", "Database password", required);
	}

	ReaderOptions withPort(boolean required)
	{
		return addOption(PORT, PORT_LONG, true, "PORT", "Database server port", required);
	}

	ReaderOptions withReader(boolean required)
	{
		return addOption(READER, READER_LONG, true, "READER", "Data import reader", required);
	}

	private ReaderOptions addOption(String opt, String longOpt, boolean hasArg, String argName, String description, boolean required)
	{
		Option option = new Option(opt, longOpt, hasArg, description);
		option.setArgName(argName);
		option.setRequired(required);

		addOption(option);

		return this;
	}

	void printHelp(String name)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.setOptionComparator(new OptionComparator());
		formatter.printHelp(name, this, true);
	}

	private static class OptionComparator implements Comparator<Option>
	{
		public int compare(Option o1, Option o2)
		{
			if (o1.isRequired() && !o2.isRequired())
				return -1;

			else if (o1.isRequired() && o2.isRequired() || !o1.isRequired() && !o2.isRequired())
			{
				String o1Key = o1.getOpt() == null ? o1.getLongOpt() : o1.getOpt();
				String o2Key = o2.getOpt() == null ? o2.getLongOpt() : o2.getOpt();
				return o1Key.compareToIgnoreCase(o2Key);
			}

			else
				return 1;
		}
	}

	public static File getInput(CommandLine line)
	{
		File input = null;

		if (line.hasOption(INPUT))
			input = new File(line.getOptionValue(INPUT));

		return input;
	}

	public static String getServer(CommandLine line)
	{
		return line.getOptionValue(SERVER);
	}

	public static String getDatabase(CommandLine line)
	{
		return line.getOptionValue(DATABASE);
	}

	public static String getUsername(CommandLine line)
	{
		return line.getOptionValue(USERNAME);
	}

	public static String getPassword(CommandLine line)
	{
		return line.getOptionValue(PASSWORD);
	}

	public static String getPort(CommandLine line)
	{
		return line.getOptionValue(PORT);
	}

	public static String getReader(CommandLine line)
	{
		return line.getOptionValue(READER);
	}
}
