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

import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMapReader} implements {@link IBatchReader} and reads the single {@link Map} object from the metadata sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelMapReader extends ExcelBatchReader<Map>
{
	private Sheet dataSheet;

	@Override
	public List<Map> readAll()
	{
		List<Map> result = new ArrayList<>();

		result.add(new Map()
				.setVisibility(true)
				.setName(getName())
				.setDescription(getName())
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date()));

		return result;
	}

	private String getName()
	{
		String name = utils.getCellValue(dataSheet.getRow(12), 2);

		if (StringUtils.isEmpty(name))
			name = "UNKNOWN MAP";

		return name;
	}

	@Override
	public void init(Workbook wb)
	{
		dataSheet = wb.getSheet("METADATA");
	}
}
