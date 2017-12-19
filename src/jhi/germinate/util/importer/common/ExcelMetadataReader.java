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

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMetadataReader} implements {@link IBatchReader} and reads the single {@link Dataset} object from the metadata sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelMetadataReader implements IBatchReader<Dataset>
{
	protected XSSFSheet    dataSheet;
	protected XSSFWorkbook wb;

	private ExperimentType type;

	public ExcelMetadataReader()
	{

	}

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
	public List<Dataset> readAll() throws IOException
	{
		List<Dataset> result = new ArrayList<>();

		result.add(new Dataset()
				.setDescription(IExcelReader.getCellValue(wb, dataSheet.getRow(2), 2))
				.setDateStart(IDataReader.getDate(IExcelReader.getCellValue(wb, dataSheet.getRow(4), 2)))
				.setContact(IExcelReader.getCellValue(wb, dataSheet.getRow(11), 2))
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
				.add("title", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("description", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("rights", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("date", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("publisher", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("format", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("language", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("source", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("type", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.add("subject", IExcelReader.getJson(wb, dataSheet.getRow(i++), 2))
				.toString();
	}

	private Location parseLocation()
	{
		XSSFSheet locationSheet = wb.getSheet("LOCATION");

		Location result = null;

		if (locationSheet != null)
		{
			XSSFRow row = locationSheet.getRow(1);

			int i = 0;

			result = new Location()
					.setName(IExcelReader.getCellValue(wb, row, i++))
					.setShortName(IExcelReader.getCellValue(wb, row, i++))
					.setType(LocationType.datasets)
					.setCountry(new Country()
							.setCountryCode2(IExcelReader.getCellValue(wb, row, i++)))
					.setElevation(IExcelReader.getCellValue(wb, row, i++))
					.setLatitude(IExcelReader.getCellValue(wb, row, i++))
					.setLongitude(IExcelReader.getCellValue(wb, row, i++));
		}

		return result;
	}

	private Experiment parseExperiment()
	{
		return new Experiment().setName(IExcelReader.getCellValue(wb, dataSheet.getRow(1), 2))
							   .setDescription(IExcelReader.getCellValue(wb, dataSheet.getRow(2), 2))
							   .setType(type)
							   .setCreatedOn(new Date())
							   .setUpdatedOn(new Date());
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("METADATA");
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private class JsonBuilder
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
