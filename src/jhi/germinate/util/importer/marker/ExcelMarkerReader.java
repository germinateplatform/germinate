/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.marker;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMarkerReader} implements {@link IStreamableReader} and reads and streams one {@link MapDefinition} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelMarkerReader implements IStreamableReader<MapDefinition>
{
	private XSSFSheet dataSheet;

	private int rowCount   = 0;
	private int currentRow = 0;
	private XSSFRow      row;
	private XSSFWorkbook wb;

	@Override
	public boolean hasNext() throws IOException
	{
		return ++currentRow <= rowCount;
	}

	@Override
	public MapDefinition next() throws IOException
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("DATA");

		rowCount = dataSheet.getLastRowNum();
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private MapDefinition parse()
	{
		return new MapDefinition()
				.setMarker(new Marker().setName(IExcelReader.getCellValue(wb, row, 0)).setType(new MarkerType().setDescription(IExcelReader.getCellValue(wb, row, 2))))
				.setChromosome(IExcelReader.getCellValue(wb, row, 1))
				.setDefinitionStart(getDouble(IExcelReader.getCellValue(wb, row, 3)))
				.setDefinitionEnd(getDouble(IExcelReader.getCellValue(wb, row, 4)));
	}

	private Double getDouble(String value)
	{
		try
		{
			return Double.parseDouble(value);
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
