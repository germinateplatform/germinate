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

package jhi.germinate.util.ui.util;

import com.google.gson.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;

/**
 * @author Sebastian Raubach
 */
public class DatabaseProperties
{
	private static final String FILENAME = "germinate-importer.json";

	private           String server   = "";
	private           String database = "";
	private transient String username = "";
	private transient String password = "";
	private           String port     = "";

	public static DatabaseProperties readProperties()
	{
		Gson gson = new Gson();
		File file = new File(new File(System.getProperty("user.home"), ".germinate"), FILENAME);
		if (file.exists())
		{
			try
			{
				String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				return gson.fromJson(json, DatabaseProperties.class);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return new DatabaseProperties();
	}

	public static void writeProperties(DatabaseProperties properties)
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(properties);

		File file = new File(new File(System.getProperty("user.home"), ".germinate"), FILENAME);
		try
		{
			file.getParentFile().mkdirs();
			Files.write(file.toPath(), json.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public String getServer()
	{
		return server;
	}

	public DatabaseProperties setServer(String server)
	{
		this.server = server;
		return this;
	}

	public String getDatabase()
	{
		return database;
	}

	public DatabaseProperties setDatabase(String database)
	{
		this.database = database;
		return this;
	}

	public String getUsername()
	{
		return username;
	}

	public DatabaseProperties setUsername(String username)
	{
		this.username = username;
		return this;
	}

	public String getPassword()
	{
		return password;
	}

	public DatabaseProperties setPassword(String password)
	{
		this.password = password;
		return this;
	}

	public String getPort()
	{
		return port;
	}

	public DatabaseProperties setPort(String port)
	{
		this.port = port;
		return this;
	}
}
