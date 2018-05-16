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

package jhi.germinate.util.importer.pedigree;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPedigreeStringReader} implements {@link IStreamableReader} and reads and streams one {@link PedigreeDefinition} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelPedigreeStringReader implements IStreamableReader<PedigreeDefinition>
{
	private XSSFSheet dataSheet;

	private int rowCount   = 0;
	private int currentRow = 0;
	private XSSFRow      row;
	private XSSFWorkbook wb;

	@Override
	public boolean hasNext() throws IOException
	{
		return ++currentRow < rowCount;
	}

	@Override
	public PedigreeDefinition next() throws IOException
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("DATA-STRING");

		rowCount = dataSheet.getPhysicalNumberOfRows();
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private PedigreeDefinition parse()
	{
		return new PedigreeDefinition()
				.setAccession(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, row, 0)))
				.setDefinition(IExcelReader.getCellValue(wb, row, 1))
				.setNotation(new PedigreeNotation()
						.setName(IExcelReader.getCellValue(wb, row, 2))
						.setDescription(IExcelReader.getCellValue(wb, row, 2))
						.setCreatedOn(new Date())
						.setUpdatedOn(new Date()))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}
}
