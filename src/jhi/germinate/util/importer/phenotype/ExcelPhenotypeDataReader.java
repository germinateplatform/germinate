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

package jhi.germinate.util.importer.phenotype;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPhenotypeDataReader} implements {@link IStreamableReader} and reads and streams one {@link PhenotypeData} object at a time (one cell of
 * the data matrix).
 *
 * @author Sebastian Raubach
 */
public class ExcelPhenotypeDataReader extends ExcelStreamableReader<PhenotypeData>
{
	public static final String EXTRA_REP       = "EXTRA_REP";
	public static final String EXTRA_TREATMENT = "EXTRA_TREATMENT";

	private Sheet dataSheetData;
	private Sheet dataSheetDates;
	private Row   rowData;
	private Row   rowDates;
	private Row   headerRow;

	private int rowCount   = 0;
	private int colCount   = 0;
	private int currentRow = 0;
	private int currentCol = 2;

	private String treatment;
	private String rep;

	@Override
	public boolean hasNext()
	{
		if (++currentCol == colCount)
		{
			currentRow++;
			currentCol = 3;
		}

		return currentRow < rowCount && currentCol < colCount;
	}

	@Override
	public PhenotypeData next()
	{
		rowData = dataSheetData.getRow(currentRow);
		rowDates = dataSheetDates.getRow(currentRow);

		if (currentCol == 3)
		{
			rep = utils.getCellValue(rowData, 1);
			treatment = utils.getCellValue(rowData, 2);
		}

		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		// We need information from both data sheets
		dataSheetData = wb.getSheet("DATA");
		dataSheetDates = wb.getSheet("RECORDING_DATES");

		rowCount = dataSheetData.getPhysicalNumberOfRows();
		colCount = dataSheetData.getRow(0).getPhysicalNumberOfCells();

		headerRow = dataSheetData.getRow(0);

		currentRow++;
	}

	private PhenotypeData parse()
	{
		return new PhenotypeData()
				.setAccession(getAccession())
				.setPhenotype(new Phenotype().setName(utils.getCellValue(headerRow, currentCol)))
				.setValue(utils.getCellValue(rowData, currentCol))
				.setRecordingDate(IDataReader.getDate(utils.getCellValue(rowDates, currentCol)))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}

	private Accession getAccession()
	{
		Accession accession = new Accession()
				.setGeneralIdentifier(utils.getCellValue(rowData, 0));

		if (!StringUtils.isEmpty(rep))
			accession.setExtra(EXTRA_REP, rep);

		if (!StringUtils.isEmpty(treatment))
			accession.setExtra(EXTRA_TREATMENT, treatment);

		return accession;
	}
}
