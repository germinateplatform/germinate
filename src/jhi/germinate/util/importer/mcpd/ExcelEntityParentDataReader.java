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

package jhi.germinate.util.importer.mcpd;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelEntityParentDataReader} extends {@link TabDelimitedMcpdReader} and reads and streams one {@link Accession} at a time from an excel sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelEntityParentDataReader extends ExcelStreamableReader<Accession>
{
	public static final String ENTITY_PARENT = "ENTITY_PARENT";

	private Sheet dataSheet;

	private int          rowCount   = 0;
	private int          currentRow = 0;
	private Row      row;

	@Override
	public boolean hasNext()
	{
		return ++currentRow < rowCount;
	}

	@Override
	public Accession next()
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

	private String getPart(McpdField field)
	{
		int column = field.ordinal();

		return utils.getCellValue(row, column);
	}

	private void setEntityDetails(Accession accession)
	{
		Row header = dataSheet.getRow(0);

		for (int col = 0; col < header.getPhysicalNumberOfCells(); col++)
		{
			if (Objects.equals(utils.getCellValue(header, col), "Entity type"))
			{
				accession.setEntityType(EntityType.getByName(utils.getCellValue(row, col)));
			}
			if (Objects.equals(utils.getCellValue(header, col), "Entity parent ACCENUMB"))
			{
				accession.setExtra(ENTITY_PARENT, utils.getCellValue(row, col));
			}
		}
	}

	private Accession parse()
	{
		Accession accession = new Accession()
			.setPuid(getPart(McpdField.PUID))
			.setGeneralIdentifier(getPart(McpdField.ACCENUMB))
			.setName(getPart(McpdField.ACCENUMB));

		setEntityDetails(accession);

		return accession;
	}
}
