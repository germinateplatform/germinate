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

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPedigreeReader} implements {@link IStreamableReader} and reads and streams one {@link Pedigree} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelPedigreeReader extends ExcelStreamableReader<List<Pedigree>>
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
	public List<Pedigree> next()
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("DATA");

		rowCount = dataSheet.getPhysicalNumberOfRows();
	}

	private List<Pedigree> parse()
	{
		List<Pedigree> result = new ArrayList<>();

		String accession = utils.getCellValue(row, 0);
		String parentOne = utils.getCellValue(row, 1);
		String parentTwo = utils.getCellValue(row, 2);

		if (!StringUtils.isEmpty(accession) && !StringUtils.isEmpty(parentOne))
		{
		result.add(new Pedigree()
				.setAccession(new Accession().setGeneralIdentifier(accession))
				.setParent(new Accession().setGeneralIdentifier(parentOne))
				.setRelationShipDescription(utils.getCellValue(row, 3))
				.setRelationshipType("OTHER") // TODO: Add to templates
				.setPedigreeDescription(new PedigreeDescription().setName(utils.getCellValue(row, 4)).setDescription(utils.getCellValue(row, 4)).setAuthor(utils.getCellValue(row, 5))
																 .setCreatedOn(new Date())
																 .setUpdatedOn(new Date())
				)
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date())
		);
		}

		if (!StringUtils.isEmpty(accession) && !StringUtils.isEmpty(parentTwo))
		{
			result.add(new Pedigree()
					.setAccession(new Accession().setGeneralIdentifier(accession))
					.setParent(new Accession().setGeneralIdentifier(parentTwo))
					.setRelationShipDescription(utils.getCellValue(row, 3))
					.setRelationshipType("OTHER") // TODO: Add to templates
					.setPedigreeDescription(new PedigreeDescription().setName(utils.getCellValue(row, 4)).setDescription(utils.getCellValue(row, 4)).setAuthor(utils.getCellValue(row, 5))
																	 .setCreatedOn(new Date())
																	 .setUpdatedOn(new Date())
					)
					.setCreatedOn(new Date())
					.setUpdatedOn(new Date())
			);
		}

		return result;
	}
}
