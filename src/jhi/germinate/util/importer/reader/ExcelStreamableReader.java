/*
 *  Copyright 2018 Information and Computational Sciences,
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

import org.apache.poi.openxml4j.exceptions.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;

/**
 * @author Sebastian Raubach
 */
public abstract class ExcelStreamableReader<T> implements IStreamableReader<T>
{
	protected ExcelUtils utils;
	private   Workbook   wb;

	@Override
	public final void init(File input) throws IOException, InvalidFormatException
	{
		wb = new XSSFWorkbook(input);
		utils = new ExcelUtils(wb);
		init(wb);
	}

	protected abstract void init(Workbook wb);

	@Override
	public final void close() throws Exception
	{
		if (wb != null)
			wb.close();
	}
}
