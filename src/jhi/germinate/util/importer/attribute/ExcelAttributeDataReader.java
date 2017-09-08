/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
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
