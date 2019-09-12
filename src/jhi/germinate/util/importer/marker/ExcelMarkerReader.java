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

import org.apache.poi.ss.usermodel.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMarkerReader} implements {@link IStreamableReader} and reads and streams one {@link MapDefinition} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelMarkerReader extends ExcelStreamableReader<MapDefinition>
{
	private static final String CHROMOSOME_UNKNOWN = "UNK";

	private Row   chromosomes;
	private Row   positions;
	private Row   markerNames;

	private int colCount   = 0;
	private int currentCol = 0;

	@Override
	public boolean hasNext()
	{
		return ++currentCol <= colCount;
	}

	@Override
	public MapDefinition next()
	{
		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		Sheet dataSheet = wb.getSheet("DATA");

		chromosomes = dataSheet.getRow(0);
		positions = dataSheet.getRow(1);
		markerNames = dataSheet.getRow(2);

		colCount = markerNames.getPhysicalNumberOfCells();
	}

	private MapDefinition parse()
	{
		String chromosome = utils.getCellValue(chromosomes, currentCol);
		String position = utils.getCellValue(positions, currentCol);

		if (StringUtils.isEmpty(chromosome))
			chromosome = CHROMOSOME_UNKNOWN;
		if (StringUtils.isEmpty(position))
			position = currentCol + "";

		return new MapDefinition()
				.setChromosome(chromosome)
				.setDefinitionStart(position)
				.setDefinitionEnd(position)
				.setMarker(new Marker()
						.setName(utils.getCellValue(markerNames, currentCol)));
	}
}
