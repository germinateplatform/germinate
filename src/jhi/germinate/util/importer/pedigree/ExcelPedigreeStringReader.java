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

package jhi.germinate.util.importer.pedigree;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPedigreeStringReader} implements {@link IStreamableReader} and reads and streams one {@link PedigreeDefinition} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelPedigreeStringReader extends ExcelStreamableReader<PedigreeDefinition>
{
	private Sheet dataSheet;
	private Row   row;

	private int rowCount   = 0;
	private int currentRow = 0;

	@Override
	public boolean hasNext()
	{
		return ++currentRow < rowCount;
	}

	@Override
	public PedigreeDefinition next()
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("DATA-STRING");

		rowCount = dataSheet.getPhysicalNumberOfRows();
	}

	private PedigreeDefinition parse()
	{
		return new PedigreeDefinition()
				.setAccession(new Accession().setGeneralIdentifier(utils.getCellValue(row, 0)))
				.setDefinition(utils.getCellValue(row, 1))
				.setNotation(new PedigreeNotation()
						.setName(utils.getCellValue(row, 2))
						.setDescription(utils.getCellValue(row, 2))
						.setCreatedOn(new Date())
						.setUpdatedOn(new Date()))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}
}
