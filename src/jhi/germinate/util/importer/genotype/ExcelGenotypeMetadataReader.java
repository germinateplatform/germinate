/**
 * Germinate 3 is written and developed by Sebastian Raubach and Paul Shaw from the Information and Computational Sciences Group at JHI Dundee. For
 * further information contact us at germinate@hutton.ac.uk or visit our webpages at https://ics.hutton.ac.uk/germinate
 *
 * Copyright © 2005-2017, Information & Computational Sciences, The James Hutton Institute. All rights reserved. Use is subject to the accompanying
 * licence terms.
 */

package jhi.germinate.util.importer.genotype;

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.util.importer.reader.*;

/**
 * {@link ExcelGenotypeMetadataReader} implements {@link IBatchReader} and reads the single {@link Dataset} object from the metadata sheet.
 *
 * @author Sebastian Raubach
 */
public class ExcelGenotypeMetadataReader implements IBatchReader<Dataset>
{
	private XSSFSheet dataSheet;

	private XSSFWorkbook wb;

	@Override
	public List<Dataset> readAll() throws IOException
	{
		List<Dataset> result = new ArrayList<>();

		String contact = IExcelReader.getCellValue(wb, dataSheet.getRow(0), 1);
		String email = IExcelReader.getCellValue(wb, dataSheet.getRow(1), 1);

		if (!StringUtils.isEmpty(contact, email))
			contact += " (" + email + ")";

		result.add(new Dataset()
				.setContact(contact)
				.setDescription(IExcelReader.getCellValue(wb, dataSheet.getRow(2), 1))
				.setDateStart(IDataReader.getDate(IExcelReader.getCellValue(wb, dataSheet.getRow(3), 1)))
				.setDateEnd(IDataReader.getDate(IExcelReader.getCellValue(wb, dataSheet.getRow(4), 1)))
				.setVersion(IExcelReader.getCellValue(wb, dataSheet.getRow(5), 1))
				.setDatasetState(DatasetState.PUBLIC)
				.setIsExternal(false)
				.setCreatedOn(new Date())
				.setUpdatedOn(new Date())
				.setExperiment(parseExperiment()));

		return result;
	}

	private Experiment parseExperiment()
	{
		return new Experiment().setName(IExcelReader.getCellValue(wb, dataSheet.getRow(7), 1))
							   .setType(ExperimentType.genotype)
							   .setCreatedOn(new Date())
							   .setUpdatedOn(new Date());
	}

	@Override
	public void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);

		dataSheet = wb.getSheet("METADATA");
	}

	@Override
	public void close() throws IOException
	{
		if (wb != null)
			wb.close();
	}
}
