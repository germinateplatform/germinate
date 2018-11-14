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

package jhi.germinate.util.importer.common;

import org.apache.poi.ss.usermodel.*;

import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * @author Sebastian Raubach
 */
public class ExcelCollaboratorReader extends ExcelBatchReader<Collaborator>
{
	private Sheet    dataSheet;

	@Override
	public List<Collaborator> readAll()
	{
		List<Collaborator> result = new ArrayList<>();

		int rowCount = dataSheet.getPhysicalNumberOfRows();

		for (int i = 2; i < rowCount; i++)
			result.add(parse(dataSheet.getRow(i)));

		return result;
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("COLLABORATORS");
	}

	private Collaborator parse(Row row)
	{
		int i = 0;
		return new Collaborator()
				.setLastName(utils.getCellValue(row, i++))
				.setFirstName(utils.getCellValue(row, i++))
				.setEmail(utils.getCellValue(row, i++))
				.setPhone(utils.getCellValue(row, i++))
				.setInstitution(new Institution()
						.setName(utils.getCellValue(row, i++))
						.setAddress(utils.getCellValue(row, i++))
						.setCountry(new Country()
								.setCountryCode2(utils.getCellValue(row, i++)))
						.setCreatedOn(new Date())
						.setUpdatedOn(new Date()))
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());
	}
}
