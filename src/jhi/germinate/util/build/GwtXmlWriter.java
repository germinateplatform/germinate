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
public class GwtXmlWriter
{
	public static final void main(String[] args) throws IOException
	{
		// 0 = Path to config.properties file
		// 1 = Path to source .properties folder
		// 2 = Path to target .properties folder
		// 3 = Path to Germinate.gwt.template.xml file
		// 4 = Path to Germinate.gwt.xml
		// 5 = browser opt setting
		// 6 = compile opt setting
		if (args.length != 7)
			throw new RuntimeException("Invalid number of arguments");

		int i = 0;
		File config = new File(args[i++]);
		File source = new File(args[i++]);
		File target = new File(args[i++]);
		Path path = new File(args[i++]).toPath();
		Path finalXml = new File(args[i++]).toPath();
		String browserOptString = args[i++];
		String compileOptString = args[i++];

		List<String> supportedLocales = getSupportedLocales(config);
		StringBuilder supportedLocalesString = new StringBuilder();
		String template = "\n\t<extend-property name='locale' values='%s' />";

		try
		{
			Files.copy(new File(source, "Text.properties").toPath(), new File(target, "Text.properties").toPath(), StandardCopyOption.REPLACE_EXISTING);

			for (String locale : supportedLocales)
			{
				Files.copy(new File(source, "Text_" + locale + ".properties").toPath(), new File(target, "Text_" + locale + ".properties").toPath(), StandardCopyOption.REPLACE_EXISTING);
				supportedLocalesString.append(String.format(template, locale));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (browserOptString.equalsIgnoreCase("false"))
			browserOptString = "";
		else
			browserOptString = String.format("<set-property name='user.agent' value='%s' />", browserOptString);

		if (Boolean.parseBoolean(compileOptString))
			compileOptString = "<collapse-property name='locale' values='*'/>";
		else
			compileOptString = "";

		String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		content = content.replace("@supported_locales@", supportedLocalesString.toString())
						 .replace("@browser_opt@", browserOptString)
						 .replace("@compile_opt@", compileOptString);
		Files.write(finalXml, content.getBytes(StandardCharsets.UTF_8));
	}

	private static List<String> getSupportedLocales(File config)
	{
		List<String> result = new ArrayList<>();
		try (FileInputStream stream = new FileInputStream(config))
		{
			Properties properties = new Properties();
			properties.load(stream);

			String value = properties.getProperty(ServerProperty.GERMINATE_BUILD_ADDITIONAL_LOCALES.getKey());

			if (!StringUtils.isEmpty(value))
			{
				String[] locales = value.split(",");

				Arrays.stream(locales)
					  .map(String::trim)
					  .forEach(result::add);
			}
		}
		catch (IOException e)
		{
		}

		return result;
	}
}
