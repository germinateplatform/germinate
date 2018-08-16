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

package jhi.germinate.util.build;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

import jhi.germinate.shared.*;
import jhi.germinate.shared.enums.*;

/**
 * @author Sebastian Raubach
 */
public class WebXmlWriter
{
	public static final void main(String[] args) throws IOException
	{
		// 0 = Path to config.properties file
		// 1 = Path to web.template.xml file
		// 2 = Path to web.xml
		if (args.length != 3)
			throw new RuntimeException("Invalid number of arguments");

		int i = 0;
		File config = new File(args[i++]);
		Path path = new File(args[i++]).toPath();
		Path finalXml = new File(args[i++]).toPath();

		String name = getDisplayName(config);

		String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		content = content.replace("@display_name@", name);
		Files.write(finalXml, content.getBytes(StandardCharsets.UTF_8));
	}

	private static String getDisplayName(File config)
	{
		String result = "Germinate 3";
		try (FileInputStream stream = new FileInputStream(config))
		{
			Properties properties = new Properties();
			properties.load(stream);

			String name = properties.getProperty(ServerProperty.GERMINATE_TEMPLATE_DATABASE_NAME.getKey());

			if (!StringUtils.isEmpty(name))
				result = name;
		}
		catch (IOException e)
		{
		}

		return result;
	}
}
