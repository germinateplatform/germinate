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

package jhi.germinate.util.importer.genotype;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelGenotypeDataReader} implements {@link IStreamableReader} and reads and streams one {@link PhenotypeData} object at a time (one cell of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class ExcelGenotypeDataReader implements IStreamableReader<String[]>
{
	private XSSFSheet dataSheet;

	private int rowCount   = 0;
	private int colCount   = 0;
	private int currentRow = 1;
	private XSSFRow      row;
	private XSSFWorkbook wb;

	@Override
	public boolean hasNext() throws IOException
	{
		return ++currentRow < rowCount;
	}

	@Override
	public String[] next() throws IOException
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
		colCount = dataSheet.getRow(2).getPhysicalNumberOfCells();
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	public List<MapDefinition> getMapDefinitions()
	{
		List<MapDefinition> markers = new ArrayList<>();

		XSSFRow chromosomes = dataSheet.getRow(0);
		XSSFRow positions = dataSheet.getRow(1);
		XSSFRow markerNames = dataSheet.getRow(2);

		for (int i = 1; i < colCount; i++)
		{
			markers.add(new MapDefinition()
					.setChromosome(IExcelReader.getCellValue(wb, chromosomes, i))
					.setDefinitionStart(IExcelReader.getCellValue(wb, positions, i))
					.setDefinitionEnd(IExcelReader.getCellValue(wb, positions, i))
					.setMarker(new Marker()
							.setName(IExcelReader.getCellValue(wb, markerNames, i))));
		}

		return markers;
	}

	private String[] parse()
	{
		String[] result = new String[colCount];

		for (int i = 0; i < colCount; i++)
			result[i] = IExcelReader.getCellValue(wb, row, i);

		return result;
	}
}
