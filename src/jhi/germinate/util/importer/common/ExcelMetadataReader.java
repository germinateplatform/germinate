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

package jhi.germinate.util.importer.common;

import com.eclipsesource.json.*;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMetadataReader} implements {@link IBatchReader} and reads the single {@link Dataset} object from the metadata sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelMetadataReader extends ExcelBatchReader<Dataset>
{
	protected Sheet dataSheet;
	private   Sheet locationSheet;

	private ExperimentType type;

	public ExcelMetadataReader(ExperimentType type)
	{
		this.type = type;
	}

	public ExperimentType getType()
	{
		return type;
	}

	public ExcelMetadataReader setType(ExperimentType type)
	{
		this.type = type;
		return this;
	}

	@Override
	public List<Dataset> readAll()
	{
		List<Dataset> result = new ArrayList<>();

		result.add(new Dataset()
				.setName(utils.getCellValue(dataSheet.getRow(1), 2))
				.setDescription(utils.getCellValue(dataSheet.getRow(2), 2))
				.setDateStart(IDataReader.getDate(utils.getCellValue(dataSheet.getRow(4), 2)))
				.setContact(utils.getCellValue(dataSheet.getRow(11), 2))
				.setDublinCore(parseDublinCore())
				.setLocation(parseLocation())
				.setVersion("1")
				.setDatasetState(DatasetState.PUBLIC)
				.setIsExternal(false)
				.setExperiment(parseExperiment())
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date()));

		return result;
	}

	private String parseDublinCore()
	{
		int i = 1;
		return new JsonBuilder()
				.add("title", utils.getJson(dataSheet.getRow(i++), 2))
				.add("description", utils.getJson(dataSheet.getRow(i++), 2))
				.add("rights", utils.getJson(dataSheet.getRow(i++), 2))
				.add("date", utils.getJson(dataSheet.getRow(i++), 2))
				.add("publisher", utils.getJson(dataSheet.getRow(i++), 2))
				.add("format", utils.getJson(dataSheet.getRow(i++), 2))
				.add("language", utils.getJson(dataSheet.getRow(i++), 2))
				.add("source", utils.getJson(dataSheet.getRow(i++), 2))
				.add("type", utils.getJson(dataSheet.getRow(i++), 2))
				.add("subject", utils.getJson(dataSheet.getRow(i++), 2))
				.toString();
	}

	private Location parseLocation()
	{
		Location result = null;

		if (locationSheet != null)
		{
			Row row = locationSheet.getRow(1);

			int i = 0;

			result = new Location()
					.setName(utils.getCellValue(row, i++))
					.setShortName(utils.getCellValue(row, i++))
					.setType(LocationType.datasets)
					.setCountry(new Country()
							.setCountryCode2(utils.getCellValue(row, i++)))
					.setElevation(utils.getCellValue(row, i++))
					.setLatitude(utils.getCellValue(row, i++))
					.setLongitude(utils.getCellValue(row, i++));
		}

		return result;
	}

	private Experiment parseExperiment()
	{
		return new Experiment().setName(utils.getCellValue(dataSheet.getRow(1), 2))
							   .setDescription(utils.getCellValue(dataSheet.getRow(2), 2))
							   .setType(type)
							   .setCreatedOn(new Date())
							   .setUpdatedOn(new Date());
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("METADATA");
		locationSheet = wb.getSheet("LOCATION");
	}

	private static class JsonBuilder
	{
		private JsonObject json = Json.object();

		public JsonBuilder add(String key, JsonValue value)
		{
			if (!StringUtils.isEmpty(key) && value != null)
				json.add(key, value);

			return this;
		}

		public String toString()
		{
			return json.toString(WriterConfig.PRETTY_PRINT)
					   .replaceAll("\n", " ")
					   .replaceAll(" +", " ");
		}
	}
}
