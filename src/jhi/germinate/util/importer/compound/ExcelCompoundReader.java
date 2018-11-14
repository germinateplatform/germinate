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
 * {@link ExcelCompoundReader} implements {@link IStreamableReader} and reads and streams one {@link Phenotype} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelCompoundReader extends ExcelStreamableReader<Compound>
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
	public Compound next()
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("COMPOUNDS");

		rowCount = dataSheet.getPhysicalNumberOfRows();
	}

	private Compound parse()
	{
		int i = 0;
		return new Compound()
				.setName(utils.getCellValue(row, i++))
				.setDescription(utils.getCellValue(row, i++))
				.setMolecularFormula(utils.getCellValue(row, i++))
				.setMonoisotopicMass(utils.getCellValueAsDouble(row, i++))
				.setAverageMass(utils.getCellValueAsDouble(row, i++))
				.setCompoundClass(utils.getCellValue(row, i++))
				.setUnit(new Unit().setName(utils.getCellValue(row, i++))
								   .setAbbreviation(utils.getCellValue(row, i++))
								   .setDescription(utils.getCellValue(row, i++))
								   .setCreatedOn(new Date())
								   .setUpdatedOn(new Date()))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}
}
