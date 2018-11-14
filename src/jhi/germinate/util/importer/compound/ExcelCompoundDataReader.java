/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.util.importer.compound;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelCompoundDataReader} implements {@link IStreamableReader} and reads and streams one {@link CompoundData} object at a time (one cell of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class ExcelCompoundDataReader extends ExcelStreamableReader<CompoundData>
{
	private Sheet dataSheetData;
	private Sheet dataSheetDates;
	private Row   rowData;
	private Row   rowDates;
	private Row   headerRow;

	private int rowCount   = 0;
	private int colCount   = 0;
	private int currentRow = 0;
	private int currentCol = 0;

	@Override
	public boolean hasNext()
	{
		if (++currentCol == colCount)
		{
			currentRow++;
			currentCol = 1;
		}

		return currentRow < rowCount && currentCol < colCount;
	}

	@Override
	public CompoundData next()
	{
		rowData = dataSheetData.getRow(currentRow);
		rowDates = dataSheetDates.getRow(currentRow);

		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		// We need information from both data sheets
		dataSheetData = wb.getSheet("DATA");
		dataSheetDates = wb.getSheet("RECORDING_DATES");

		rowCount = dataSheetData.getPhysicalNumberOfRows();
		colCount = dataSheetData.getRow(0).getPhysicalNumberOfCells();

		headerRow = dataSheetData.getRow(0);

		currentRow++;
	}

	private CompoundData parse()
	{
		return new CompoundData()
				.setAccession(getAccession())
				.setCompound(new Compound().setName(utils.getCellValue(headerRow, currentCol)))
				.setValue(utils.getCellValueAsDouble(rowData, currentCol))
				.setRecordingDate(IDataReader.getDate(utils.getCellValue(rowDates, currentCol)))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}

	private Accession getAccession()
	{
		return new Accession()
				.setGeneralIdentifier(utils.getCellValue(rowData, 0));
	}
}
