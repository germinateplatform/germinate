/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.pedigree;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelPedigreeReader} implements {@link IStreamableReader} and reads and streams one {@link Pedigree} object at a time.
 *
 * @author Sebastian Raubach
 */
public class ExcelPedigreeReader implements IStreamableReader<List<Pedigree>>
{
	private XSSFSheet dataSheet;

	private int rowCount   = 0;
	private int currentRow = 0;
	private XSSFRow      row;
	private XSSFWorkbook wb;

	@Override
	public boolean hasNext() throws IOException
	{
		return ++currentRow < rowCount;
	}

	@Override
	public List<Pedigree> next() throws IOException
	{
		row = dataSheet.getRow(currentRow);
		return parse();
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("DATA");

		rowCount = dataSheet.getPhysicalNumberOfRows();
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}

	private List<Pedigree> parse()
	{
		List<Pedigree> result = new ArrayList<>();

		result.add(new Pedigree()
				.setAccession(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, row, 0)))
				.setParent(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, row, 1)))
				.setRelationShipDescription(IExcelReader.getCellValue(wb, row, 3))
				.setPedigreeDescription(new PedigreeDescription().setName(IExcelReader.getCellValue(wb, row, 4)).setDescription(IExcelReader.getCellValue(wb, row, 4)).setAuthor(IExcelReader.getCellValue(wb, row, 5))
																 .setCreatedOn(new Date())
																 .setUpdatedOn(new Date())
				)
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date())
		);

		result.add(new Pedigree()
				.setAccession(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, row, 0)))
				.setParent(new Accession().setGeneralIdentifier(IExcelReader.getCellValue(wb, row, 2)))
				.setRelationShipDescription(IExcelReader.getCellValue(wb, row, 3))
				.setPedigreeDescription(new PedigreeDescription().setName(IExcelReader.getCellValue(wb, row, 4)).setDescription(IExcelReader.getCellValue(wb, row, 4)).setAuthor(IExcelReader.getCellValue(wb, row, 5))
																 .setCreatedOn(new Date())
																 .setUpdatedOn(new Date())
				)
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date())
		);

		return result;
	}
}
