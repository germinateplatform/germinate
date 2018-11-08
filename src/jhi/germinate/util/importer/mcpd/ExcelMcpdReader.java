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

package jhi.germinate.util.importer.mcpd;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMcpdReader} extends {@link TabDelimitedMcpdReader} and reads and streams one {@link Accession} at a time from an excel sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelMcpdReader extends TabDelimitedMcpdReader
{
	private XSSFSheet dataSheet;

	private int          rowCount   = 0;
	private int          currentRow = 0;
	private XSSFRow      row;
	private XSSFWorkbook wb;

	@Override
	public boolean hasNext()
	{
		return ++currentRow < rowCount;
	}

	@Override
	public Accession next()
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("DATA");

		rowCount = dataSheet.getPhysicalNumberOfRows();
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	@Override
	protected String getPart(McpdField field)
	{
		int column = field.ordinal();

		return IExcelReader.getCellValue(wb, row, column);
	}
}
