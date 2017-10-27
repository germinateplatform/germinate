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

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPhenotypeDataReader} implements {@link IStreamableReader} and reads and streams one {@link PhenotypeData} object at a time (one cell of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class ExcelPhenotypeDataReader implements IStreamableReader<PhenotypeData>
{
	private XSSFSheet dataSheetData;
	private XSSFSheet dataSheetDates;

	private int rowCount   = 0;
	private int colCount   = 0;
	private int currentRow = 0;
	private int currentCol = 0;
	private XSSFRow      rowData;
	private XSSFRow      rowDates;
	private XSSFRow      headerRow;
	private XSSFWorkbook wb;

	@Override
	public boolean hasNext() throws IOException
	{
		if (++currentCol == colCount)
		{
			currentRow++;
			currentCol = 1;
		}

		return currentRow < rowCount && currentCol < colCount;
	}

	@Override
	public PhenotypeData next() throws IOException
	{
		rowData = dataSheetData.getRow(currentRow);
		rowDates = dataSheetDates.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		// We need information from both data sheets
		dataSheetData = wb.getSheet("DATA");
		dataSheetDates = wb.getSheet("RECORDING_DATES");

		rowCount = dataSheetData.getPhysicalNumberOfRows();
		colCount = dataSheetData.getRow(0).getPhysicalNumberOfCells();

		headerRow = dataSheetData.getRow(0);

		currentRow++;
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private PhenotypeData parse()
	{
		return new PhenotypeData()
				.setAccession(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, rowData, 0)))
				.setPhenotype(new Phenotype().setName(IExcelReader.getCellValue(wb, headerRow, currentCol)))
				.setValue(IExcelReader.getCellValue(wb, rowData, currentCol))
				.setRecordingDate(IDataReader.getDate(IExcelReader.getCellValue(wb, rowDates, currentCol)))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}
}
