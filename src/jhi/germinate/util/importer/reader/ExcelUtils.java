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

package jhi.germinate.util.importer.reader;

import com.eclipsesource.json.*;

import org.apache.poi.ss.usermodel.*;

import jhi.germinate.shared.*;

/**
 * {@link ExcelUtils} contains a static interface method that parses data from an excel spreadsheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelUtils
{
	private final FormulaEvaluator formulaEval;
	private DataFormatter formatter = new DataFormatter();

	public ExcelUtils(Workbook wb)
	{
		formulaEval = wb.getCreationHelper().createFormulaEvaluator();
	}

	public String getCellValue(Row row, int column)
	{
		if (row == null)
			return null;

		Cell c = row.getCell(column);
		if (c == null || c.getCellType() == CellType.BLANK)
		{
			return null;
		}
		else
		{
			if (c.getCellType() == CellType.FORMULA)
			{
				String value = formatter.formatCellValue(c, formulaEval);

				if (!StringUtils.isEmpty(value))
					value = value.trim();

				return value;
			}
			else
			{
				String value = formatter.formatCellValue(c);

				if (!StringUtils.isEmpty(value))
					value = value.trim();

				return value;
			}
		}
	}

	public Double getCellValueAsDouble(Row row, int column)
	{
		try
		{
			return Double.parseDouble(getCellValue(row, column));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public JsonValue getJson(Row row, int i)
	{
		String value = getCellValue(row, i);

		if (StringUtils.isEmpty(value))
			return null;
		else
			return Json.array(value);
	}
}
