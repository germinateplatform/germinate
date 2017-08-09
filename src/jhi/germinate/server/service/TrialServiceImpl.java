/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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

package jhi.germinate.server.service;

import java.io.*;
import java.util.*;
import java.util.Map;

import javax.servlet.annotation.*;

import jhi.germinate.client.service.*;
import jhi.germinate.server.config.*;
import jhi.germinate.server.database.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.manager.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.Util.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.Tuple.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;
import jhi.germinate.shared.exception.IOException;

/**
 * {@link TrialServiceImpl} is the implementation of {@link TrialService}.
 *
 * @author Sebastian Raubach
 */
@WebServlet(urlPatterns = {"/germinate/trial"})
public class TrialServiceImpl extends BaseRemoteServiceServlet implements TrialService
{
	private static final long serialVersionUID = -8532526273822554012L;

	private static final String QUERY_PHENOTYPE_YEAR_TABLE      = "SELECT phenotype_id, DATE_FORMAT(recording_date, '%%Y') as year, name AS phenotype, COUNT(1) AS count FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND germinatebase_id = ? AND dataset_id IN (%s) GROUP BY name, year, phenotype_id ORDER BY name, year ASC";
	private static final String QUERY_INDIVIDUAL_CHART_FILE_NEW = "SELECT treatments.name AS treatment, IF ( ISNULL(seriesname), site_name, CONCAT(site_name, ' (', seriesname, ')')) AS sitename_concat, AVG(phenotype_value) AS VALUE FROM phenotypedata LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN trialseries ON trialseries.id = phenotypedata.trialseries_id LEFT JOIN treatments ON treatments.id = phenotypedata.treatment_id WHERE experimenttypes.description = 'trials' AND phenotypedata.germinatebase_id = ? AND phenotypedata.dataset_id IN (%s) AND phenotypedata.phenotype_id = ? AND DATE_FORMAT(recording_date, '%%Y') = ? GROUP BY sitename_concat, seriesname, treatments.name ORDER BY sitename_concat, seriesname, treatments.name";
	private static final String QUERY_HISTOGRAM                 = "SELECT treatments.name AS treatment, ROUND(phenotype_value) AS category, COUNT(1) AS count FROM phenotypedata LEFT JOIN treatments ON treatments.id = phenotypedata.treatment_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN trialseries ON trialseries.id = phenotypedata.trialseries_id WHERE germinatebase_id = ? AND dataset_id IN (%s) AND phenotype_id = ? AND DATE_FORMAT(recording_date, '%%Y') = ? GROUP BY treatment, category ORDER BY treatment, category";
	private static final String QUERY_LINE_PERF_TREATMENT       = "SELECT A. year, A.site_name, x, y FROM ( SELECT '%s' AS year, DATE_FORMAT(recording_date, '%%Y') AS the_year, site_name, TRUNCATE (avg(phenotype_value), 2) AS x, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS firstId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN treatments ON treatments.id = phenotypedata.treatment_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? AND treatments.name = ? GROUP BY the_year, site_name, recording_date ORDER BY the_year, site_name ) AS A JOIN ( SELECT '%s' AS year, DATE_FORMAT(recording_date, '%%Y') AS the_year, site_name, TRUNCATE (avg(phenotype_value), 2) AS y, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS secondId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN treatments ON treatments.id = phenotypedata.treatment_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? AND treatments.name = ? GROUP BY the_year, site_name, recording_date ORDER BY the_year, site_name ) AS B ON A.firstId = B.secondId UNION SELECT A. year, A.site_name, x, y FROM ( SELECT DATE_FORMAT(recording_date, '%%Y') AS year, site_name, TRUNCATE (avg(phenotype_value), 2) AS x, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS firstId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN treatments ON treatments.id = phenotypedata.treatment_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? AND treatments.name = ? GROUP BY year, site_name, recording_date ORDER BY year, site_name ) AS A JOIN ( SELECT DATE_FORMAT(recording_date, '%%Y') AS year, site_name, TRUNCATE (avg(phenotype_value), 2) AS y, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS secondId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN treatments ON treatments.id = phenotypedata.treatment_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? AND treatments.name = ? AND germinatebase_id = ? GROUP BY year, site_name, recording_date ORDER BY year, site_name ) AS B ON A.firstId = B.secondId";
	private static final String QUERY_LINE_PERF                 = "SELECT A. year, A.site_name, x, y FROM ( SELECT '%s' AS year, DATE_FORMAT(recording_date, '%%Y') AS the_year, site_name, TRUNCATE (avg(phenotype_value), 2) AS x, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS firstId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? GROUP BY the_year, site_name, recording_date ORDER BY the_year, site_name ) AS A JOIN ( SELECT '%s' AS year, DATE_FORMAT(recording_date, '%%Y') AS the_year, site_name, TRUNCATE (avg(phenotype_value), 2) AS y, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS secondId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? GROUP BY the_year, site_name, recording_date ORDER BY the_year, site_name ) AS B ON A.firstId = B.secondId UNION SELECT A. year, A.site_name, x, y FROM ( SELECT DATE_FORMAT(recording_date, '%%Y') AS year, site_name, TRUNCATE (avg(phenotype_value), 2) AS x, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS firstId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? GROUP BY year, site_name, recording_date ORDER BY year, site_name ) AS A JOIN ( SELECT DATE_FORMAT(recording_date, '%%Y') AS year, site_name, TRUNCATE (avg(phenotype_value), 2) AS y, CONCAT( DATE_FORMAT(recording_date, '%%Y'), site_name ) AS secondId FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN locations ON locations.id = phenotypedata.location_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'trials' AND dataset_id IN (%s) AND phenotype_id = ? AND germinatebase_id = ? GROUP BY year, site_name, recording_date ORDER BY year, site_name ) AS B ON A.firstId = B.secondId";
	private static final String QUERY_TREATMENTS                = "SELECT DISTINCT  (treatments.name) FROM phenotypedata LEFT JOIN treatments ON  treatments.id = phenotypedata.treatment_id WHERE NOT ISNULL(treatments.name) AND dataset_id IN (%s) AND phenotype_id = ?";
	private static final String QUERY_PHENOTYPE_BY_PHENOTYPE    = "SELECT DATE_FORMAT(a.recording_date, '%%Y') AS recording_date, datasets.description AS dataset, treatments.name AS treatment, germinatebase.name AS name, a.germinatebase_id AS id, a.phenotype_value AS x, b.phenotype_value AS y FROM phenotypedata AS a JOIN phenotypedata AS b ON a.germinatebase_id = b.germinatebase_id AND a.location_id <=> b.location_id AND a.trialseries_id <=> b.trialseries_id %s AND a.treatment_id <=> b.treatment_id AND a.dataset_id = b.dataset_id AND a.dataset_id IN (%s) LEFT JOIN germinatebase ON germinatebase.id = a.germinatebase_id LEFT JOIN treatments ON a.treatment_id = treatments.id LEFT JOIN groupmembers ON groupmembers.foreign_id = a.germinatebase_id LEFT JOIN groups ON groups.id = groupmembers.group_id LEFT JOIN datasets ON datasets.id = a.dataset_id WHERE groups.id LIKE ? AND a.phenotype_id = ? AND b.phenotype_id = ? GROUP BY id, x, y, recording_date, treatment, datasets.id";
	private static final String QUERY_PHENOTYPES_OVERVIEW       = "SELECT phenotypedata.phenotype_value, %s AS germinatebase_identifier, DATE_FORMAT( phenotypedata.recording_date, '%%Y' ) AS recording_date, germinatebase.id, phenotypes.*, units.* FROM phenotypedata LEFT JOIN phenotypes ON phenotypes.id = phenotypedata.phenotype_id LEFT JOIN units ON units.id = phenotypes.unit_id LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON datasets.experiment_id = experiments.id LEFT JOIN experimenttypes ON experiments.experiment_type_id = experimenttypes.id LEFT JOIN germinatebase ON phenotypedata.germinatebase_id = germinatebase.id WHERE datasets.id IN (%s) AND phenotypes.id IN (%s) AND DATE_FORMAT(recording_date, '%%Y') IN (%s) AND phenotypes.datatype IN ('int', 'float') AND experimenttypes.description = 'trials'";
	private static final String QUERY_DISTINCT_YEARS            = "SELECT DISTINCT DATE_FORMAT(recording_date, '%%Y') AS recording_date FROM phenotypedata WHERE dataset_id IN (%s) ORDER BY recording_date";

	@Override
	public ServerResult<List<Integer>> getTrialYears(RequestProperties properties, List<Long> datasetIds) throws InvalidSessionException, DatabaseException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String formatted = String.format(QUERY_DISTINCT_YEARS, Util.generateSqlPlaceholderString(datasetIds.size()));

		return new ValueQuery(formatted)
				.setLongs(datasetIds)
				.run("recording_date")
				.getInts();
	}

	@Override
	public ServerResult<Pair<List<TrialAccessionYears>, List<String>>> getPhenotypeYearTable(RequestProperties properties, Long accessionId, List<Long> datasetIds)
			throws InvalidSessionException, DatabaseException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String formatted = String.format(QUERY_PHENOTYPE_YEAR_TABLE, Util.generateSqlPlaceholderString(datasetIds.size()));

		DefaultStreamer streamer = new DefaultQuery(properties, this, formatted)
				.setLong(accessionId)
				.setLongs(datasetIds)
				.getStreamer();

		List<TrialAccessionYears> result = new ArrayList<>();
		Set<String> overallColumns = new HashSet<>();

		DatabaseResult inputRow;
		TrialAccessionYears outputRow = new TrialAccessionYears();
		Phenotype phenotype = null;

		while ((inputRow = streamer.next()) != null)
		{
			try
			{
				String inputId = inputRow.getString("phenotype_id");

				/* Phenotypes are in increasing get, so we have a sequence of the same phenotype and then it changes, but never comes back */
				if (phenotype == null || !Objects.equals(Long.toString(phenotype.getId()), inputId))
				{
					Long phenotypeId = inputRow.getLong("phenotype_id");

					try
					{
						phenotype = new PhenotypeManager().getById(userAuth, phenotypeId).getServerResult();
					}
					catch (InsufficientPermissionsException e)
					{
						phenotype = null;
					}

					outputRow = new TrialAccessionYears(phenotype);

					result.add(outputRow);
				}

				String year = inputRow.getString("year");
				String count = inputRow.getString("count");

				overallColumns.add(year);

				outputRow.addValues(year, count);
			}
			catch (NumberFormatException e)
			{
				// Nothing to see here
			}
		}

		List<String> years = new ArrayList<>(overallColumns);

		Collections.sort(years);

		return new ServerResult<>(streamer.getDebugInfo(), new Pair<>(result, years));
	}

	@Override
	public ServerResult<String> exportIndividualData(RequestProperties properties, Long accessionId, List<Long> datasetIds, Long phenotypeId, String trialsYear)
			throws InvalidSessionException, DatabaseException, IOException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String formatted = String.format(QUERY_INDIVIDUAL_CHART_FILE_NEW, Util.generateSqlPlaceholderString(datasetIds.size()));

		DefaultStreamer streamer = new DefaultQuery(properties, this, formatted)
				.setLong(accessionId)
				.setLongs(datasetIds)
				.setLong(phenotypeId)
				.setString(trialsYear)
				.getStreamer();

		GerminateTable tempResult = new GerminateTable();

		DatabaseResult inputRow;
		GerminateRow outputRow = new GerminateRow();
		List<String> columns = new ArrayList<>();
		String site = "";

		columns.add("site");

		while ((inputRow = streamer.next()) != null)
		{
			String inputSite = inputRow.getString("sitename_concat");
			String manage = inputRow.getString("treatment");

			if (!Objects.equals(site, inputSite))
			{
				site = inputSite;

				outputRow = new GerminateRow();
				outputRow.put("site", site);
				tempResult.add(outputRow);
			}

			if (!columns.contains(manage))
				columns.add(manage);

			String value = inputRow.getString("value");

			outputRow.put(manage, value);
		}

		File resultFile = createTemporaryFile("trials_bar", FileType.txt.name());

		String[] columnNames = columns.toArray(new String[columns.size()]);

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), columnNames, tempResult, resultFile);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		// try
		// {
		// File rScript = new
		// File(FileUtils.getFileFolder(getThreadLocalRequest(),
		// FileLocation.res, null), "Test.R");
		// File outFile = FileUtils.createTemporaryFile(getThreadLocalRequest(),
		// "R_png", "png");
		// RUtils.run(rScript, "makeBarChart(\"" + outFile.getAbsolutePath() +
		// "\", 500, 500)");
		// }
		// catch (InterruptedException e)
		// {
		// e.printStackTrace();
		// }
		// catch (java.io.IOException e)
		// {
		// e.printStackTrace();
		// }

		return new ServerResult<>(streamer.getDebugInfo(), resultFile.getName());
	}

	@Override
	public ServerResult<Map<String, String>> exportHistogram(RequestProperties properties, Long accessionId, List<Long> datasetIds, Long phenotypeId, String trialsYear)
			throws InvalidSessionException, DatabaseException, IOException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String formatted = String.format(QUERY_HISTOGRAM, Util.generateSqlPlaceholderString(datasetIds.size()));

		ServerResult<GerminateTable> data = new GerminateTableQuery(properties, this, formatted, null)
				.setLong(accessionId)
				.setLongs(datasetIds)
				.setLong(phenotypeId)
				.setString(trialsYear)
				.run();

		Map<String, GerminateTable> splitByTreatment = data.getServerResult().splitOn("treatment");

		Map<String, String> resultFiles = new HashMap<>();

		for (String treatment : splitByTreatment.keySet())
		{
			File resultFile = createTemporaryFile("trials_hist", FileType.txt.name());

			try
			{
				Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, splitByTreatment.get(treatment), resultFile);
			}
			catch (java.io.IOException e)
			{
				throw new IOException(e);
			}

			resultFiles.put(treatment, resultFile.getName());
		}

		return new ServerResult<>(data.getDebugInfo(), resultFiles);
	}

	private ServerResult<List<String>> getDistinctTreatments(RequestProperties properties, List<Long> datasetIds, Long phenotypeId) throws InvalidSessionException, DatabaseException
	{
		String formatted = String.format(QUERY_TREATMENTS, Util.generateSqlPlaceholderString(datasetIds.size()));
		return new ValueQuery(properties, this, formatted)
				.setLongs(datasetIds)
				.setLong(phenotypeId)
				.run(Treatment.NAME)
				.getStrings();
	}

	@Override
	public ServerResult<Map<String, String>> exportLinePerformance(RequestProperties properties, Long accessionId, List<Long> datasetIds, Long phenotypeId, String siteAverageText)
			throws InvalidSessionException, DatabaseException, IOException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		ServerResult<List<String>> treatments = getDistinctTreatments(properties, datasetIds, phenotypeId);
		Map<String, String> resultFiles = new HashMap<>();

		if (CollectionUtils.isEmpty(treatments.getServerResult()))
		{
			treatments.setServerResult(new ArrayList<>());
			treatments.getServerResult().add("null");

			String formatted = String.format(QUERY_LINE_PERF, siteAverageText, Util.generateSqlPlaceholderString(datasetIds.size()), siteAverageText,
					Util.generateSqlPlaceholderString(datasetIds.size()), Util.generateSqlPlaceholderString(datasetIds.size()), Util.generateSqlPlaceholderString(datasetIds.size()));

			ServerResult<GerminateTable> data = new GerminateTableQuery(properties, this, formatted, null)
					.setLongs(datasetIds)
					.setLong(phenotypeId)
					.setLongs(datasetIds)
					.setLong(phenotypeId)
					.setLongs(datasetIds)
					.setLong(phenotypeId)
					.setLongs(datasetIds)
					.setLong(phenotypeId)
					.setLong(accessionId)
					.run();

			treatments.getDebugInfo().addAll(data.getDebugInfo());

			File resultFile = createTemporaryFile("trials_line_perf_null", FileType.txt.name());

			try
			{
				Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, data.getServerResult(), resultFile);
			}
			catch (java.io.IOException e)
			{
				throw new IOException(e);
			}

			resultFiles.put("null", resultFile.getName());
		}
		else
		{
			String formatted = String.format(QUERY_LINE_PERF_TREATMENT, siteAverageText, Util.generateSqlPlaceholderString(datasetIds.size()), siteAverageText,
					Util.generateSqlPlaceholderString(datasetIds.size()), Util.generateSqlPlaceholderString(datasetIds.size()), Util.generateSqlPlaceholderString(datasetIds.size()));

			for (String treatment : treatments.getServerResult())
			{
				ServerResult<GerminateTable> data = new GerminateTableQuery(properties, this, formatted, null)
						.setLongs(datasetIds)
						.setLong(phenotypeId)
						.setString(treatment)
						.setLongs(datasetIds)
						.setLong(phenotypeId)
						.setString(treatment)
						.setLongs(datasetIds)
						.setLong(phenotypeId)
						.setString(treatment)
						.setLongs(datasetIds)
						.setLong(phenotypeId)
						.setString(treatment)
						.setLong(accessionId)
						.run();

				treatments.getDebugInfo().addAll(data.getDebugInfo());

				File resultFile = createTemporaryFile("trials_line_perf_" + treatment, FileType.txt.name());

				try
				{
					Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, data.getServerResult(), resultFile);
				}
				catch (java.io.IOException e)
				{
					throw new IOException(e);
				}

				resultFiles.put(treatment, resultFile.getName());
			}
		}

		return new ServerResult<>(treatments.getDebugInfo(), resultFiles);
	}

	@Override
	public ServerResult<String> exportPhenotypeScatter(RequestProperties properties, List<Long> datasetIds, Long firstId, Long secondId, Long groupId) throws InvalidSessionException, DatabaseException,
			IOException, InsufficientPermissionsException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		if (CollectionUtils.isEmpty(datasetIds))
			return new ServerResult<>(DebugInfo.create(userAuth), "");

		String formatted;

		ExperimentType type = new DatasetManager().getById(userAuth, datasetIds.get(0)).getServerResult().getExperiment().getType();

		if (type == ExperimentType.phenotype)
			formatted = String.format(QUERY_PHENOTYPE_BY_PHENOTYPE, "", Util.generateSqlPlaceholderString(datasetIds.size()));
		else
			formatted = String.format(QUERY_PHENOTYPE_BY_PHENOTYPE, "AND a.recording_date <=> b.recording_date", Util.generateSqlPlaceholderString(datasetIds.size()));

		GerminateTableStreamer streamer = new GerminateTableQuery(properties, this, formatted, null)
				.setLongs(datasetIds)
				.setString((groupId == null || groupId == -1) ? "%" : Long.toString(groupId))
				.setLong(firstId)
				.setLong(secondId)
				.getStreamer();

		PhenotypeManager manager = new PhenotypeManager();

		Phenotype one = manager.getById(userAuth, firstId).getServerResult();
		Phenotype two = manager.getById(userAuth, secondId).getServerResult();

		File tempFile = createTemporaryFile("trials_p_by_p", FileType.txt.name());
		File finalFile = createTemporaryFile("trials_p_by_p", FileType.txt.name());

		try
		{
			Util.writeGerminateTableToFile(Util.getOperatingSystem(getThreadLocalRequest()), null, streamer, tempFile);
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

        /* Check if there are at least two rows. Also, replace the "x" and "y" headers with the actual phenotype names and units. */
		try (BufferedReader br = new BufferedReader(new FileReader(tempFile));
			 BufferedWriter bw = new BufferedWriter(new FileWriter(finalFile)))
		{
			int counter = 0;

			for (String line; (line = br.readLine()) != null; counter++)
			{
				/* If it's the header row, replace "x" and "y" column headers with the phenotype names and units */
				if (counter == 0)
				{
					String firstUnit = one.getUnit() != null ? one.getUnit().getAbbreviation() : null;
					String secondUnit = two.getUnit() != null ? two.getUnit().getAbbreviation() : null;
					String firstHeader = one.getName() + (StringUtils.isEmpty(firstUnit) ? "" : " [" + one.getUnit().getAbbreviation() + "]");
					String secondHeader = two.getName() + (StringUtils.isEmpty(secondUnit) ? "" : " [" + two.getUnit().getAbbreviation() + "]");
					line = line.replace("\tx\ty", "\t" + firstHeader + "\t" + secondHeader);
				}

				/* Then write to the final file */
				bw.write(line);
				bw.newLine();
			}

			tempFile.delete();

			if (counter < 2)
			{
				finalFile.delete();
				return new ServerResult<>(streamer.getDebugInfo(), null);
			}

//			String firstLine = br.readLine();
//			String secondLine = br.readLine();
//
//            /* If not, return null */
//			if (StringUtils.isEmpty(firstLine, secondLine))
//			{
//				tempFile.delete();
//				return new ServerResult<>(streamer.getDebugInfo(), null);
//			}
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

        /* If we get here, the file was successfully generated and contains data */
		return new ServerResult<>(streamer.getDebugInfo(), finalFile.getName());
	}

	@Override
	public ServerResult<Triple<List<String>, List<TrialsRow>, Map<TrialsRow.TrialsAttribute, String>>> getPhenotypeOverviewTable(RequestProperties properties, List<Long> datasetIds, List<Long> phenotypes, List<Integer> selectedYears) throws DatabaseException, InvalidSessionException
	{
		UserAuth userAuth = UserAuth.getFromSession(this, properties);

		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String formatted = String.format(QUERY_PHENOTYPES_OVERVIEW, "germinatebase." + PropertyReader.getAccessionDisplayName(), Util.generateSqlPlaceholderString(datasetIds.size()), Util.generateSqlPlaceholderString(phenotypes.size()), Util.generateSqlPlaceholderString(selectedYears.size()));

		List<TrialsRow> result = new ArrayList<>();

		Set<String> years = new TreeSet<>();
		Map<TrialsRow.TrialsAttribute, String> chartFiles = new HashMap<>();

		DatabaseResult streamer = new GerminateTableQuery(formatted, null)
				.setLongs(datasetIds)
				.setLongs(phenotypes)
				.setInts(selectedYears)
				.getResult();

		PhenotypeManager manager = new PhenotypeManager();

		while (streamer.next())
		{
			String year = streamer.getString("recording_date");
			String value = streamer.getString("phenotype_value");
			String accessionId = streamer.getString("id");
			String accessionName = streamer.getString("germinatebase_identifier");

			Phenotype phenotype = manager.getFromResult(userAuth, streamer);

			years.add(year);

			Optional<TrialsRow> row = result.stream()
											.filter(c -> Objects.equals(c.getPhenotype(), phenotype)) // Find matching phenotype
											.findFirst(); // Get the first one

			if (row.isPresent())
			{
				TrialsRow trialsRow = row.get();
				TrialsRow.TrialsCell cell = trialsRow.getYearToValues().get(year);

				if (cell == null)
					cell = new TrialsRow.TrialsCell();

				cell.add(value, accessionId, accessionName);

				trialsRow.addCell(year, cell);
			}
			else
			{
				/* If we get here, we need to create a new row */
				TrialsRow trialsRow = new TrialsRow();
				trialsRow.setPhenotype(phenotype);

				TrialsRow.TrialsCell cell = new TrialsRow.TrialsCell();
				cell.add(value, accessionId, accessionName);

				trialsRow.addCell(year, cell);

				result.add(trialsRow);
			}
		}

		streamer.close();

		List<String> sortedYears = new ArrayList<>(years);
		Collections.sort(sortedYears);

        /*
		 * Sort based on phenotype name here. Too costly to pre-sort it on the
         * database.
         */
		result.sort(Comparator.comparing(o -> o.getPhenotype().getName()));

        /* Export the data to .tsv files for the d3.js visualization charts */
		try
		{
			chartFiles.put(TrialsRow.TrialsAttribute.MIN, exportTrialsData(Util.getOperatingSystem(getThreadLocalRequest()), sortedYears, result, TrialsRow.TrialsAttribute.MIN));
			chartFiles.put(TrialsRow.TrialsAttribute.AVG, exportTrialsData(Util.getOperatingSystem(getThreadLocalRequest()), sortedYears, result, TrialsRow.TrialsAttribute.AVG));
			chartFiles.put(TrialsRow.TrialsAttribute.MAX, exportTrialsData(Util.getOperatingSystem(getThreadLocalRequest()), sortedYears, result, TrialsRow.TrialsAttribute.MAX));
			chartFiles.put(TrialsRow.TrialsAttribute.COUNT, exportTrialsData(Util.getOperatingSystem(getThreadLocalRequest()), sortedYears, result, TrialsRow.TrialsAttribute.COUNT));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return new ServerResult<>(null, new Triple<>(sortedYears, result, chartFiles));
	}

	private String exportTrialsData(OperatingSystem os, List<String> years, List<TrialsRow> table, TrialsRow.TrialsAttribute attr) throws IOException
	{
		File file = createTemporaryFile("trials-" + attr.name(), FileType.txt.name());

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8")))
		{
			bw.write("date");
			for (TrialsRow row : table)
			{
				bw.write("\t" + row.getPhenotype().getName());
			}
			bw.write(os.getNewLine());

			for (String year : years)
			{
				bw.write(year);

				for (TrialsRow row : table)
				{
					String value;
					TrialsRow.TrialsCell cell = row.getYearToValues().get(year);

					if (cell == null || StringUtils.isEmpty(cell.getAttribute(TrialsRow.TrialsAttribute.COUNT)))
						value = "";
					else
						value = cell.getAttribute(attr);

					bw.write("\t" + value);
				}

				bw.write(os.getNewLine());
			}
		}
		catch (java.io.IOException e)
		{
			throw new IOException(e);
		}

		return file.getName();
	}
}
