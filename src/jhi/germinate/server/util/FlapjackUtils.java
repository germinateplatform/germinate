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
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import jhi.flapjack.io.binning.*;
import jhi.germinate.client.service.AlleleFrequencyService.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link FlapjackUtils} contains methods to create flapjack files and temporary files used for the web interface that might end up being exported for
 * flapjack.
 *
 * @author Sebastian Raubach
 */
public class FlapjackUtils
{
	public static final String JAR_FILE                   = "flapjack.jar";
	public static final String CREATE_PROJECT             = "jhi.flapjack.io.cmd.CreateProject";
	public static final String ALLELE_FREQ_MAKE_HISTOGRAM = "jhi.flapjack.io.binning.MakeHistogram";
	public static final String ALLELE_FREQ_CREATE_IMAGE   = "jhi.flapjack.io.binning.CreateImage";
	public static final String ALLELE_FREQ_BIN_DATA       = "jhi.flapjack.io.binning.BinData";

	private static final boolean USE_FLAPJACK_API = true;

	public static class FlapjackParams
	{
		public enum Param
		{
			map,
			genotypes,
			project
		}

		private final Map<Param, String> parameters = new HashMap<>();

		public FlapjackParams()
		{
		}

		public FlapjackParams add(Param param, String value)
		{
			parameters.put(param, value);

			return this;
		}

		public FlapjackParams add(Param param, File value)
		{
			return add(param, value.getAbsolutePath());
		}

		public String getParam(Param param)
		{
			return parameters.get(param);
		}

		public Map<Param, String> getParams()
		{
			return parameters;
		}

		public List<String> getParamsForProcessBuilder()
		{
			return parameters.keySet()
							 .stream()
							 .map(param -> "-" + param.name() + "=" + parameters.get(param))
							 .collect(Collectors.toCollection(LinkedList::new));
		}

		public FlapjackParams removeParam(Param param)
		{
			parameters.remove(param);

			return this;
		}
	}

	/**
	 * Writes the given map data to a file
	 *
	 * @param mapData        The map data
	 * @param deletedMarkers Optional set of markers to ignore
	 * @return The created file
	 * @throws IOException Thrown if the file interaction fails
	 */
	public static File writeTemporaryMapFile(File filename, GerminateTableStreamer mapData, Collection<String> keptMarkers, Collection<String> deletedMarkers) throws IOException, DatabaseException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")))
		{
			bw.write("# fjFile = MAP");
			bw.newLine();

			for (GerminateRow row; (row = mapData.next()) != null; )
			{
				String markerName = row.get(Marker.MARKER_NAME);
				String chromosome = row.get(MapDefinition.CHROMOSOME);
				String definitionStart = row.get(MapDefinition.DEFINITION_START);

				if (keptMarkers.contains(markerName))
				{
					if (StringUtils.isEmpty(chromosome))
						chromosome = "";
					if (StringUtils.isEmpty(definitionStart))
						definitionStart = "0";

					bw.write(markerName + "\t");
					bw.write(chromosome + "\t");
					bw.write(definitionStart);
					bw.newLine();
				}
				else if (deletedMarkers != null)
				{
					deletedMarkers.add(markerName);
				}
			}

			return filename;
		}
	}

	/**
	 * Writes the given map data to a file
	 *
	 * @param mapData        The map data
	 * @param deletedMarkers Optional set of markers to ignore
	 * @throws IOException Thrown if the file interaction fails
	 */
	public static void writeTemporaryMapFile(File filename, GerminateTable mapData, Collection<String> keptMarkers, Collection<String> deletedMarkers) throws IOException
	{
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")))
		{
			bw.write("# fjFile = MAP");
			bw.newLine();

			for (GerminateRow row : mapData)
			{
				String markerName = row.get(Marker.MARKER_NAME);
				String chromosome = row.get(MapDefinition.CHROMOSOME);
				String definitionStart = row.get(MapDefinition.DEFINITION_START);

				if (keptMarkers.contains(markerName))
				{
					if (StringUtils.isEmpty(chromosome))
						chromosome = "";
					if (StringUtils.isEmpty(definitionStart))
						definitionStart = "0";

					bw.write(markerName + "\t");
					bw.write(chromosome + "\t");
					bw.write(definitionStart);
					bw.newLine();
				}
				else if (deletedMarkers != null)
				{
					deletedMarkers.add(markerName);
				}
			}
		}
	}

	/**
	 * Runs flapjack to create a new project file
	 *
	 * @param params The {@link FlapjackParams}
	 * @return The debug output
	 * @throws FlapjackException Thrown if running the flapjack project creation fails
	 */
	public static String createProject(FlapjackParams params) throws FlapjackException
	{
		try
		{
//			if (USE_FLAPJACK_API)
//			{
//				File map = new File(params.getParam(FlapjackParams.Param.map));
//				File genotypes = new File(params.getParam(FlapjackParams.Param.genotypes));
//				FlapjackFile project = new FlapjackFile(params.getParam(FlapjackParams.Param.project));
//				CreateProject cp = new CreateProject(map, genotypes, null, null, project, true);
//
//				return handleOutput(cp.doProjectCreation());
//
////				throw new FlapjackException("API for CreateProject not implemented yet.");
//			}
//			else
//			{
			String javaPath = PropertyReader.getJavaPath();

			List<String> parameters = new ArrayList<>();
			parameters.add(javaPath);
			parameters.add("-cp");
			parameters.add(getFlapjackPath());
			parameters.add(CREATE_PROJECT);
			parameters.addAll(params.getParamsForProcessBuilder());

			ProcessBuilder processBuilder = new ProcessBuilder(parameters);

			return runAndCaptureOutput(processBuilder);
//			}
		}
		catch (Exception e)
		{
			throw new FlapjackException(e);
		}
	}

	/**
	 * Runs flapjack to create a new project file for allele frequency data
	 *
	 * @param mapFile       The path to the map file
	 * @param inputFile     The path to the genotypes file
	 * @param histogramFile The path to the histogram file
	 * @param method        The {@link HistogramMethod} to use for binning
	 * @param resultFile    The path to the result file
	 * @return The debug output
	 */
	public static String createProjectAlleleFreq(String mapFile, String inputFile, String histogramFile, String resultFile, HistogramMethod method)
	{
		// TODO: implement
		return null;
	}

	/**
	 * Runs flapjack to create a histogram with the given number of bins
	 *
	 * @param inputFile    The path to the genotypes file
	 * @param outputFile   The path to the result file
	 * @param numberOfBins The number of histogram bins
	 * @return The debug output
	 * @throws FlapjackException Thrown if running the flapjack histogram creation fails
	 */
	public static String makeHistogram(String inputFile, String outputFile, int numberOfBins) throws FlapjackException
	{
		try
		{
			if (USE_FLAPJACK_API)
			{
				MakeHistogram makeHistogram = new MakeHistogram(numberOfBins, inputFile, outputFile);

				return handleOutput(makeHistogram.createHistogram());
			}
			else
			{
				String javaPath = PropertyReader.getJavaPath();

				String flapjackPath = getFlapjackPath();

				ProcessBuilder processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, ALLELE_FREQ_MAKE_HISTOGRAM, inputFile, outputFile, "" + numberOfBins);

				return runAndCaptureOutput(processBuilder);
			}
		}
		catch (Exception e)
		{
			throw new FlapjackException(e);
		}
	}

	/**
	 * Creates and saves an image representing the binning of Flapjack
	 *
	 * @param outputFile    The image file
	 * @param histogramFile The histogram file
	 * @param params        The {@link HistogramParams}
	 * @return The debug output of flapjack
	 * @throws FlapjackException Thrown if running the flapjack image creation fails
	 */
	public static String createImage(String outputFile, String histogramFile, HistogramParams params) throws FlapjackException
	{
		try
		{
			if (USE_FLAPJACK_API)
			{
				CreateImage createImage = new CreateImage(outputFile);

				switch (params.method)
				{
					case AUTO:
						createImage.createAutoImage(params.nrOfBins, histogramFile);
						break;
					case SPLIT:
						createImage.createSplitImage(params.nrOfBins, (float) params.splitPoint, params.nrOfBinsRight);
						break;
					case STANDARD:
						createImage.createStandardImage(params.nrOfBins);
						break;
				}

				return "";
			}
			else
			{
				String javaPath = PropertyReader.getJavaPath();

				String flapjackPath = getFlapjackPath();
				String flapjackMain = ALLELE_FREQ_CREATE_IMAGE;

				ProcessBuilder processBuilder = null;

				switch (params.method)
				{
					case AUTO:
						processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, flapjackMain, outputFile, "AUTO", "" + params.nrOfBins,
								histogramFile);
						break;
					case SPLIT:
						processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, flapjackMain, outputFile, "SPLIT", "" + params.nrOfBins, ""
								+ params.splitPoint, "" + params.nrOfBinsRight);
						break;
					case STANDARD:
						processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, flapjackMain, outputFile, "STANDARD", "" + params.nrOfBins);
						break;
				}

				return runAndCaptureOutput(processBuilder);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new FlapjackException(e);
		}
	}

	/**
	 * Creates the binned data file for flapjack based on the given {@link HistogramParams}
	 *
	 * @param params    The parameters for the export process
	 * @param input     The input file
	 * @param output    The output file
	 * @param histogram The histogram file
	 * @return The debug output of flapjack
	 */
	public static String createBinnedFile(HistogramParams params, String input, String output, String histogram) throws FlapjackException
	{
		try
		{
			if (USE_FLAPJACK_API)
			{
				BinData binData = new BinData(input, output);

				switch (params.method)
				{
					case AUTO:
						binData.writeAutoFile(params.nrOfBins, histogram);
						break;
					case SPLIT:
						binData.writeSplitFile(params.nrOfBins, (float) params.splitPoint, params.nrOfBinsRight);
						break;
					case STANDARD:
						binData.writeStandardFile(params.nrOfBins);
						break;
				}

				return "";
			}
			else
			{
				String javaPath = PropertyReader.getJavaPath();

				String flapjackPath = getFlapjackPath();
				String flapjackMain = ALLELE_FREQ_BIN_DATA;

				ProcessBuilder processBuilder;

				switch (params.method)
				{
					case AUTO:
						processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, flapjackMain, params.method.name(), input, output, params.nrOfBins + "", histogram);
						break;
					case SPLIT:
						processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, flapjackMain, params.method.name(), input, output, params.nrOfBins + "", params.splitPoint + "", params.nrOfBinsRight + "");
						break;
					case STANDARD:
						processBuilder = new ProcessBuilder(javaPath, "-cp", flapjackPath, flapjackMain, params.method.name(), input, output, params.nrOfBins + "");
						break;
					default:
						return null;
				}

				return runAndCaptureOutput(processBuilder);
			}

		}
		catch (Exception e)
		{
			throw new FlapjackException(e);
		}
	}

	private static String getFlapjackPath() throws FlapjackException
	{
		File folder = FileUtils.getFromPath(FileLocation.apps, null, ReferenceFolder.flapjack, "");

		if (folder != null)
		{
			File file = new File(folder, JAR_FILE);

			if (file.exists() && file.isFile())
				return file.getAbsolutePath();
		}

		throw new FlapjackException("Flapjack jar not found");
	}

	private static String handleOutput(List<String> lines)
	{
		StringBuilder builder = new StringBuilder();

		for (String line : lines)
		{
			builder.append(line);
			builder.append("<br/>");
		}

		return builder.toString();
	}

	private static String runAndCaptureOutput(ProcessBuilder processBuilder) throws IOException
	{
		if (processBuilder == null)
			return "";

		Process process = processBuilder.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

		StringBuilder builder = new StringBuilder();

		String line;

		while ((line = br.readLine()) != null)
		{
			builder.append(line);
			builder.append("<br/>");
		}

		return builder.toString();
	}
}
