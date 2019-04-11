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

package jhi.germinate.server.util.kml;

import java.io.*;
import java.io.IOException;
import java.util.*;
import java.util.Map;
import java.util.stream.*;

import de.micromata.opengis.kml.v_2_2_0.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Location;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link KMLCreatorMegaEnv} is an implementation of {@link KMLCreator} creating KML files for mega environments.
 *
 * @author Sebastian Raubach
 */
public class KMLCreatorMegaEnv extends KMLCreator
{
	private static final   String QUERY_ID       = "SELECT locations.* FROM locations LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id WHERE locationtypes.name = 'collectingsites' AND NOT EXISTS ( SELECT 1 FROM megaenvironmentdata WHERE location_id = locations.id )";
	private static final   String QUERY_ID_EMPTY = "SELECT locations.* FROM locations LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id LEFT JOIN megaenvironmentdata ON megaenvironmentdata.location_id = locations.id WHERE megaenvironmentdata.megaenvironment_id = ?";
	protected static final String QUERY_DATA     = "SELECT germinatebase.* FROM germinatebase WHERE location_id IN (%s)";

	protected static final String ROW_PART_ONE      = "<p>%s</p>";
	protected static final String ROW_PART_ONE_LINK = "<a href='%s/?" + Parameter.accessionId.name() + "=%s#" + Page.PASSPORT + "'>%s</a>";
	protected static final String ROW_PART_TWO      = "</td><td>%s</td></tr>";

	private Map<Long, List<Accession>> data = new HashMap<>();

	public KMLCreatorMegaEnv(DebugInfo info)
	{
		super(info);
	}

	@Override
	public void createKML(String baseUrl, Long id, File file) throws DatabaseException, IOException
	{
		Kml kml = new Kml();

		Folder folder = kml.createAndSetDocument()
						   .createAndAddFolder()
						   .withDescription(getDescription(id))
						   .withName(getDescription(id));

		/* Get the collectingsite ids */
		List<Location> locations = getLocations(id);

		/* Pre-fetch the description data in one database query instead of
		 * multiple individual queries */
		getData(locations);

		if (locations != null)
		{
			for (Location location : locations)
			{
				if (location == null || location.getLatitude() == null || location.getLongitude() == null || location.getElevation() == null)
					continue;

				Placemark placemark = folder.createAndAddPlacemark()
											.withStyleUrl("#Marker")
											.withName(Long.toString(location.getId()))
											.withDescription(getDescription(location, baseUrl))
											.withVisibility(true);

				placemark.createAndAddStyle()
						 .createAndSetIconStyle()
						 .withScale(1)
						 .createAndSetIcon()
						 .withHref("http://maps.google.com/mapfiles/kml/paddle/red-circle.png");

				placemark.createAndSetLookAt()
						 .withLatitude(location.getLatitude())
						 .withLongitude(location.getLongitude())
						 .withAltitude(location.getElevation())
						 .withRange(10000)
						 .withTilt(0);

				placemark.createAndSetPoint()
						 .addToCoordinates(location.getLongitude(), location.getLatitude(), location.getElevation())
						 .withAltitudeMode(AltitudeMode.ABSOLUTE);
			}
		}

		kml.marshalAsKmz(file.getAbsolutePath());
	}

	/**
	 * Retrieves the data for the given list of ids
	 *
	 * @param locations The list of location ids
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private void getData(List<Location> locations) throws DatabaseException
	{
		if (CollectionUtils.isEmpty(locations))
			return;

		String formatted = String.format(QUERY_DATA, StringUtils.generateSqlPlaceholderString(locations.size()));

		DatabaseObjectQuery<Accession> q = new DatabaseObjectQuery<>(formatted, null);

		for (Location location : locations)
			q.setLong(location.getId());

		ServerResult<List<Accession>> accessions = q.run()
													.getObjects(Accession.Parser.Inst.get());

		info.addAll(accessions.getDebugInfo());

		data = accessions.getServerResult()
						 .stream()
						 .collect(Collectors.groupingBy(accession -> accession.getLocation().getId()));
	}

	/**
	 * Gets the collectingsite ids for the given mega environment id
	 *
	 * @param id The mega environment id
	 * @return The {@link List} of collectingsite ids
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	protected List<Location> getLocations(Long id) throws DatabaseException
	{
		ServerResult<List<Location>> result;

		if (id != -1)
		{
			result = new DatabaseObjectQuery<Location>(QUERY_ID_EMPTY, null)
					.setLong(id)
					.run()
					.getObjects(Location.Parser.Inst.get());
		}
		else
		{
			result = new DatabaseObjectQuery<Location>(QUERY_ID, null)
					.run()
					.getObjects(Location.Parser.Inst.get());
		}

		info.addAll(result.getDebugInfo());

		return result.getServerResult();
	}

	/**
	 * Assembles the description for the given mega environment id
	 *
	 * @param location The location
	 * @param baseUrl  The url to link to
	 * @return The description to use for the marker
	 */
	private String getDescription(Location location, String baseUrl)
	{
		List<Accession> dataForLocation = data.get(location.getId());

		if (dataForLocation == null)
			return "";

		StringBuilder builder = new StringBuilder();

		builder.append(STYLE);
		builder.append("<p><table class='tftable' border='1'><tr><th>Site name</th><th>Elevation</th></tr><tr><td>")
			   .append(location.getName())
			   .append("</td><td>")
			   .append(location.getElevation())
			   .append("</td></tr></table></p><p><b>Accessions at this collecting site:</b></p><table class='tftable' border='1'><tr><th>Id</th><th>Name</th></tr>");

		/* Check which Germinate pages are available. We do this to only add links to those pages that are actually available */
		Set<Page> availablePages = PropertyWatcher.getSet(ServerProperty.GERMINATE_AVAILABLE_PAGES, Page.class);

		for (Accession accession : dataForLocation)
		{
			builder.append("<tr><td>");

			if (availablePages.contains(Page.PASSPORT))
			{
				builder.append(String.format(ROW_PART_ONE_LINK, baseUrl, accession.getId(), accession.getId()));
			}
			else
			{
				builder.append(String.format(ROW_PART_ONE, accession.getId()));
			}

			builder.append(String.format(ROW_PART_TWO, accession.getName()));
		}

		builder.append("</table>");

		return builder.toString();
	}

	protected String getPrefix()
	{
		return "mega_env";
	}

	protected String getDescription(Long id)
	{
		return "Collecting sites for mega-environment: " + id;
	}
}
