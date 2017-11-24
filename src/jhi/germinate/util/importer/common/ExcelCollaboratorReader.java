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

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * @author Sebastian Raubach
 */
public class ExcelCollaboratorReader implements IBatchReader<Collaborator>
{
	private XSSFSheet dataSheet;

	private XSSFWorkbook wb;

	@Override
	public List<Collaborator> readAll() throws IOException
	{
		List<Collaborator> result = new ArrayList<>();

		int rowCount = dataSheet.getPhysicalNumberOfRows();

		for (int i = 2; i < rowCount; i++)
			result.add(parse(dataSheet.getRow(i)));

		return result;
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("COLLABORATORS");
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private Collaborator parse(XSSFRow row)
	{
		int i = 0;
		return new Collaborator()
				.setLastName(IExcelReader.getCellValue(wb, row, i++))
				.setFirstName(IExcelReader.getCellValue(wb, row, i++))
				.setEmail(IExcelReader.getCellValue(wb, row, i++))
				.setPhone(IExcelReader.getCellValue(wb, row, i++))
				.setInstitution(new Institution()
						.setName(IExcelReader.getCellValue(wb, row, i++))
						.setAddress(IExcelReader.getCellValue(wb, row, i++))
						.setCountry(new Country()
								.setCountryCode2(IExcelReader.getCellValue(wb, row, i++)))
						.setCreatedOn(new Date())
						.setUpdatedOn(new Date()))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}
}
