/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
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

package jhi.germinate.server.util.kml;

import java.io.*;
import java.io.IOException;

import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link KMLCreator} is an abstract class used to create KML files
 *
 * @author Sebastian Raubach
 */
public abstract class KMLCreator
{
	protected static final String STYLE = "<style type='text/css'>table.tftable {font-size:12px;color:#333333;width:100%;border-width: 1px;border-color: #a9a9a9;border-collapse: collapse;}table.tftable th {font-size:12px;background-color:#b8b8b8;border-width: 1px;padding: 8px;border-style: solid;border-color: #a9a9a9;text-align:left;}table.tftable tr {background-color:#ffffff;}table.tftable td {font-size:12px;border-width: 1px;padding: 8px;border-style: solid;border-color: #a9a9a9;}</style>";

	protected DebugInfo info;

	public KMLCreator(DebugInfo info)
	{
		this.info = info;
	}

	/**
	 * This method will create the kml file and save it to the given filename
	 *
	 * @param baseUrl The base url of the service
	 * @param id      The overall id to use (context specific)
	 * @param file    The file for the final output
	 * @throws DatabaseException Thrown if any interaction with the database fails
	 * @throws IOException       Thrown if the file interaction fails
	 */
	public abstract void createKML(String baseUrl, Long id, File file) throws DatabaseException, IOException;

	protected static class DataHolder
	{
		protected String id;
		protected String name;
		protected Double latitude  = null;
		protected Double longitude = null;
		protected Double elevation = null;

		public DataHolder(String id, String name, String latitude, String longitude, String elevation)
		{
			this.id = id;
			this.name = name;
			try
			{
				this.latitude = Double.parseDouble(latitude);
			}
			catch (Exception e)
			{

			}
			try
			{
				this.longitude = Double.parseDouble(longitude);
			}
			catch (Exception e)
			{

			}
			try
			{
				this.elevation = Double.parseDouble(elevation);
			}
			catch (Exception e)
			{

			}
		}

		@Override
		public String toString()
		{
			return id + " " + name + " " + latitude + " " + longitude + " " + elevation;
		}
	}
}
