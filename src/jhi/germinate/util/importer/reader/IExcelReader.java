/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.reader;

import org.apache.poi.ss.usermodel.*;

import jhi.germinate.shared.*;

/**
 * {@link IExcelReader} contains a static interface method that parses data from an excel spreadsheet.
 *
 * @author Sebastian Raubach
 */
public interface IExcelReader
{
	public DataFormatter formatter = new DataFormatter();

	static String getCellValue(Workbook wb, Row row, int column)
	{
		if (row == null)
			return null;

		Cell c = row.getCell(column);
		if (c == null || c.getCellTypeEnum() == CellType.BLANK)
			return null;
		else
		{
			if (c.getCellTypeEnum() == CellType.FORMULA)
			{
				FormulaEvaluator formulaEval = wb.getCreationHelper().createFormulaEvaluator();

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
}
