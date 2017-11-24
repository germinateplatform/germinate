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

	private int colCount   = 0;
	private int currentCol = 0;
	private XSSFWorkbook wb;
	private XSSFRow      chromosomes;
	private XSSFRow      positions;
	private XSSFRow      markerNames;

	@Override
	public boolean hasNext() throws IOException
	{
		return ++currentCol <= colCount;
	}

	@Override
	public MapDefinition next() throws IOException
	{
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("DATA");

		chromosomes = dataSheet.getRow(0);
		positions = dataSheet.getRow(1);
		markerNames = dataSheet.getRow(2);

		colCount = markerNames.getPhysicalNumberOfCells();
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
				.setChromosome(IExcelReader.getCellValue(wb, chromosomes, currentCol))
				.setDefinitionStart(IExcelReader.getCellValue(wb, positions, currentCol))
				.setDefinitionEnd(IExcelReader.getCellValue(wb, positions, currentCol))
				.setMarker(new Marker()
						.setName(IExcelReader.getCellValue(wb, markerNames, currentCol)));
	}
}
