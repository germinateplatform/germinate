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

import org.apache.poi.ss.usermodel.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelAttributeDataReader} implements {@link IStreamableReader} and reads and streams one {@link AttributeData} object at a time (one cell of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class ExcelAttributeDataReader extends ExcelStreamableReader<AttributeData>
{
	private Sheet dataSheet;
	private Row   row;
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
	public AttributeData next()
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("ADDITIONAL_ATTRIBUTES");

		rowCount = dataSheet.getPhysicalNumberOfRows();
		colCount = dataSheet.getRow(0).getPhysicalNumberOfCells();

		headerRow = dataSheet.getRow(0);

		currentRow++;
	}

	private AttributeData parse()
	{
		return new AttributeData()
				.setForeign(new Accession().setGeneralIdentifier(utils.getCellValue(row, 0)))
				.setAttribute(parseAttribute())
				.setValue(utils.getCellValue(row, currentCol));
	}

	private Attribute parseAttribute()
	{
		return new Attribute()
				.setName(utils.getCellValue(headerRow, currentCol))
				.setDescription(utils.getCellValue(headerRow, currentCol))
				.setTargetTable(GerminateDatabaseTable.germinatebase.name())
				.setDataType("char");
	}
}
