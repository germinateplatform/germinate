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
import java.util.zip.*;

import de.micromata.opengis.kml.v_2_2_0.*;
import jhi.germinate.server.database.query.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.watcher.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.datastructure.database.Location;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link KMLCreatorLocation} is an implementation of {@link KMLCreator} creating KML files for locations.
 *
 * @author Sebastian Raubach
 */
public class KMLCreatorLocation extends KMLCreator
{
	private static final String QUERY_IDS         = "SELECT * FROM germinatebase WHERE location_id = ?";
	private static final String GENUS_SPECIES     = "<tr><td>Genus</td><td><i>%s</i></td></tr><tr><td>Species</td><td><i>%s</i></td></tr>";
	private static final String ROW_PART_ONE      = "<p>%s</p>";
	private static final String ROW_PART_ONE_LINK = "<a href='%s/? " + Parameter.accessionId.name() + "=%s#" + Page.PASSPORT.name() + "'>%s</a>";
	private static final String ROW_PART_TWO      = "</td></tr><tr><td>Name</td><td>%s</td></tr>";
	private static final String COLLDATE          = "<tr><td>Collection date</td><td>%s</td></tr>";

	public KMLCreatorLocation(DebugInfo info)
	{
		super(info);
	}

	@Override
	public void createKML(String baseUrl, Long id, File file) throws DatabaseException, IOException
	{
		File tempFile = File.createTempFile("locations", ".kml");

		Kml kml = new Kml();

		Folder folder = kml.createAndSetDocument()
						   .createAndAddFolder()
						   .withDescription("Accessions for collecting site: " + id)
						   .withName("Accessions for collecting site: " + id);


        /* Get the accessions */
		List<Accession> accessions = getAccessions(id);

		for (Accession accession : accessions)
		{
			Location location = accession.getLocation();
			if (location == null || location.getLatitude() == null || location.getLongitude() == null || location.getElevation() == null)
				continue;

			Placemark placemark = folder.createAndAddPlacemark()
										.withStyleUrl("#Marker")
										.withName(Long.toString(accession.getId()))
										.withDescription(getDescription(accession, baseUrl))
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

		kml.marshal(tempFile);

		/* Zip temporary file */
		FileOutputStream fos = new FileOutputStream(file);
		ZipOutputStream zos = new ZipOutputStream(fos);
		ZipEntry ze = new ZipEntry(tempFile.getName());
		zos.putNextEntry(ze);
		FileInputStream in = new FileInputStream(tempFile);

		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) > 0)
		{
			zos.write(buffer, 0, len);
		}

		in.close();
		zos.closeEntry();
		zos.close();

        /* Delete the temporary file */
		tempFile.delete();
	}

	/**
	 * Returns a {@link List} of germinatebase ids for the given collectingsite id
	 *
	 * @param id The collectingsite id
	 * @return The {@link List} of germinatebase ids
	 * @throws DatabaseException Thrown if the interaction with the database fails
	 */
	private List<Accession> getAccessions(Long id) throws DatabaseException
	{
		ServerResult<List<Accession>> result = new DatabaseObjectQuery<Accession>(QUERY_IDS, null)
				.setLong(id)
				.run()
				.getObjects(Accession.Parser.Inst.get());

		info.addAll(result.getDebugInfo());

		return result.getServerResult();
	}

	/**
	 * Assembles the description for the given collectingsite id
	 *
	 * @param accession The accession
	 * @param baseUrl   The url to link to
	 * @return The description for the given collectingsite that can be used for the marker
	 */
	private String getDescription(Accession accession, String baseUrl)
	{
		StringBuilder builder = new StringBuilder(STYLE);

		builder.append("<p><b>Detailed information about the accession:</b></p><table class='tftable' border='1'><tr><th>Passport Item</th><th>Value</th></tr>");

		/* Check which Germinate pages are available. We do this to only add links to those pages that are actually available */
		Set<Page> availablePages = PropertyWatcher.getSet(ServerProperty.GERMINATE_AVAILABLE_PAGES, Page.class);

		builder.append("<tr><td>Germinate Id</td><td>");
		if (availablePages.contains(Page.PASSPORT))
		{
			builder.append(String.format(ROW_PART_ONE_LINK, baseUrl, accession.getId(), accession.getId()));
		}
		else
		{
			builder.append(String.format(ROW_PART_ONE, accession.getId()));
		}

		builder.append(String.format(ROW_PART_TWO, accession.getName()));

		if (accession.getTaxonomy() != null)
		{
			builder.append(String.format(GENUS_SPECIES, accession.getTaxonomy().getGenus(), accession.getTaxonomy().getSpecies()));
		}

		builder.append(String.format(COLLDATE, accession.getCollDate() == null ? "" : Util.formatDate(accession.getCollDate())));

		builder.append("</table>");

		return builder.toString();
	}
}
