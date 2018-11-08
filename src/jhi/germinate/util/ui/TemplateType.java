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

package jhi.germinate.util.ui;

import java.io.*;

import jhi.germinate.util.importer.compound.*;
import jhi.germinate.util.importer.genotype.*;
import jhi.germinate.util.importer.mcpd.*;
import jhi.germinate.util.importer.pedigree.*;
import jhi.germinate.util.importer.phenotype.*;

/**
 * @author Sebastian Raubach
 */
public enum TemplateType
{
	mcpd("Multi-Crop Passport Descriptors"),
	genotypic("Genotypic data (allele calls)"),
	trials("Trials data"),
	pedigree("Pedigree data"),
	compounds("Chemical compound data"),
	unknown("UNKNOWN");

	String description;

	TemplateType(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}


	@Override
	public String toString()
	{
		return description;
	}

	public void callImporter(File file, String server, String database, String port, String username, String password)
	{
		switch (this)
		{
			case mcpd:
				new ExcelMcpdImporter().run(file, server, database, username, password, port);
				break;
			case trials:
				new PhenotypeDataImporter().run(file, server, database, username, password, port);
				break;
			case compounds:
				new CompoundDataImporter().run(file, server, database, username, password, port);
				break;
			case genotypic:
				if (file.getName().endsWith(".xlsx"))
					new ExcelGenotypeDataImporter().run(file, server, database, username, password, port);
				else if (file.getName().endsWith(".txt"))
					new TabDelimitedGenotypeDataImporter().run(file, server, database, username, password, port);
				else
					throw new RuntimeException("Invalid genotypic data file.");
				break;
			case pedigree:
				new PedigreeImporter().run(file, server, database, username, password, port);
				new PedigreeStringImporter().run(file, server, database, username, password, port);
				break;
		}
	}
}
