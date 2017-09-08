/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.marker;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelMarkerMetadataReader} implements {@link IBatchReader} and reads the single {@link Map} object from the metadata sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelMarkerMetadataReader implements IBatchReader<Map>
{
	public static final String TECHNOLOGY = "Technology";
	public static final String TYPE       = "Type";
	public static final String UNIT       = "Unit";

	private XSSFSheet dataSheet;

	private XSSFWorkbook wb;

	@Override
	public List<Map> readAll() throws IOException
	{
		List<Map> result = new ArrayList<>();

		Map map = new Map()
				.setDescription(IExcelReader.getCellValue(wb, dataSheet.getRow(2), 1))
				.setVisibility(true)
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date());

		map.setExtra(TECHNOLOGY, IExcelReader.getCellValue(wb, dataSheet.getRow(3), 1));
		map.setExtra(TYPE, IExcelReader.getCellValue(wb, dataSheet.getRow(4), 1));
		map.setExtra(UNIT, IExcelReader.getCellValue(wb, dataSheet.getRow(5), 1));

		result.add(map);

		return result;
	}

	@Override
	public void init(File file) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(file);

		dataSheet = wb.getSheet("METADATA");
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}
}
