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

import java.io.*;
import java.io.IOException;

import jhi.germinate.server.config.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link RUtils} provides methods to run R code on the server
 *
 * @author Sebastian Raubach
 */
public class RUtils
{
	/**
	 * Runs the given script file and calls the given method. <p/> <b>Example</b>: <p/> <code>"makeBarChart(\"" + someFile.getAbsolutePath() + "\",
	 * 500, 500)"</code> <p/> This will call the method <code>makeBarChart</code> with three parameters.
	 *
	 * @param rScript    The R script
	 * @param methodCall The method to call (including parameters)
	 * @throws InterruptedException     Thrown if waiting for the {@link Process} fails
	 * @throws IOException              Thrown if any file interaction fails
	 * @throws MissingPropertyException Thrown if a required property isn't set
	 */
	public static void run(File rScript, String methodCall) throws InterruptedException, IOException, MissingPropertyException
	{
		checkRProperties(ServerProperty.PATH_R);

		ProcessBuilder pb = new ProcessBuilder(PropertyReader.get(ServerProperty.PATH_R), "--vanilla");
		pb.redirectErrorStream(true);

		Process proc = pb.start();

        /* Open up the output stream (to write to) (prog's in stream) */
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(proc.getOutputStream()));
		writer.println("source(\"" + rScript.getAbsolutePath() + "\")");
		writer.flush();
		writer.println(methodCall);
		writer.close();

        /* Open up the input stream (to readAll from) (prog's out stream) */
		StreamCatcher oStream = new StreamCatcher(proc.getInputStream(), true);

		proc.waitFor();

		while (oStream.isAlive())
			Thread.sleep(10);
	}

	private static void checkRProperties(ServerProperty path) throws MissingPropertyException
	{
		String rPath = PropertyReader.get(path);

		if (StringUtils.isEmpty(rPath))
			throw new MissingPropertyException();
	}

	private static class StreamCatcher extends Thread
	{
		private InputStream inputStream;
		private boolean showOutput = false;

		public StreamCatcher(InputStream in, boolean showOutput)
		{
			this.inputStream = inputStream;
			this.showOutput = showOutput;

			start();
		}

		@Override
		public void run()
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream)))
			{
				String line = reader.readLine();

				while (line != null)
				{
					if (showOutput)
						System.out.println(line);

					line = reader.readLine();
				}
			}
			catch (IOException e)
			{
			}
		}
	}
}
