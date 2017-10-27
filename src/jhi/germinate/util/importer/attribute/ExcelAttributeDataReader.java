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

package jhi.germinate.util.importer.attribute;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelAttributeDataReader} implements {@link IStreamableReader} and reads and streams one {@link AttributeData} object at a time (one cell of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class ExcelAttributeDataReader implements IStreamableReader<AttributeData>
{
	private XSSFSheet dataSheet;

	private int rowCount   = 0;
	private int colCount   = 0;
	private int currentRow = 0;
	private int currentCol = 0;
	private XSSFRow      row;
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
	public AttributeData next() throws IOException
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("ADDITIONAL_ATTRIBUTES");

		rowCount = dataSheet.getPhysicalNumberOfRows();
		colCount = dataSheet.getRow(0).getPhysicalNumberOfCells();

		headerRow = dataSheet.getRow(0);

		currentRow++;
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private AttributeData parse()
	{
		return new AttributeData()
				.setForeign(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, row, 0)))
				.setAttribute(parseAttribute())
				.setValue(IExcelReader.getCellValue(wb, row, currentCol));
	}

	private Attribute parseAttribute()
	{
		return new Attribute()
				.setName(IExcelReader.getCellValue(wb, headerRow, currentCol))
				.setDescription(IExcelReader.getCellValue(wb, headerRow, currentCol))
				.setTargetTable(GerminateDatabaseTable.germinatebase.name())
				.setDataType("char");
	}
}
