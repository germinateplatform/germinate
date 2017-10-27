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

package jhi.germinate.util.importer.phenotype;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPhenotypeMetadataReader} implements {@link IBatchReader} and reads the single {@link Dataset} object from the metadata sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelPhenotypeMetadataReader implements IBatchReader<Dataset>
{
	private XSSFSheet dataSheet;

	private XSSFWorkbook wb;

	@Override
	public List<Dataset> readAll() throws IOException
	{
		List<Dataset> result = new ArrayList<>();

		String contact = IExcelReader.getCellValue(wb, dataSheet.getRow(0), 1);
		String email = IExcelReader.getCellValue(wb, dataSheet.getRow(1), 1);

		if (!StringUtils.isEmpty(contact, email))
			contact += " (" + email + ")";

		result.add(new Dataset()
				.setContact(contact)
				.setDescription(IExcelReader.getCellValue(wb, dataSheet.getRow(2), 1))
				.setDateStart(IDataReader.getDate(IExcelReader.getCellValue(wb, dataSheet.getRow(3), 1)))
				.setDateEnd(IDataReader.getDate(IExcelReader.getCellValue(wb, dataSheet.getRow(4), 1)))
				.setVersion(IExcelReader.getCellValue(wb, dataSheet.getRow(5), 1))
				.setDatasetState(DatasetState.PUBLIC)
				.setIsExternal(false)
				.setLocation(new Location().setName(IExcelReader.getCellValue(wb, dataSheet.getRow(6), 1)))
				.setExperiment(parseExperiment())
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date()));

		return result;
	}

	private Experiment parseExperiment()
	{
		return new Experiment().setName(IExcelReader.getCellValue(wb, dataSheet.getRow(7), 1))
							   .setType(ExperimentType.trials)
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
}
