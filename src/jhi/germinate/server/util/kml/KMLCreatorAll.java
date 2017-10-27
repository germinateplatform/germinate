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

import java.util.*;

import jhi.germinate.server.database.query.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * {@link KMLCreatorAll} is an implementation of {@link KMLCreator} creating KML files for mega environments.
 *
 * @author Sebastian Raubach
 */
public class KMLCreatorAll extends KMLCreatorMegaEnv
{
	private static final String QUERY_LOCATIONS = "SELECT locations.* FROM locations LEFT JOIN locationtypes ON locationtypes.id = locations.locationtype_id WHERE NOT ISNULL(locations.latitude) AND NOT ISNULL(locations.longitude) AND locationtypes.name = 'collectingsites' AND EXISTS ( SELECT 1 FROM germinatebase WHERE location_id = locations.id )";

	public KMLCreatorAll(DebugInfo info)
	{
		super(info);
	}

	@Override
	protected List<Location> getLocations(Long id) throws DatabaseException
	{
		ServerResult<List<Location>> result = new DatabaseObjectQuery<Location>(QUERY_LOCATIONS, null)
				.run()
				.getObjects(Location.Parser.Inst.get());

		info.addAll(result.getDebugInfo());

		return result.getServerResult();
	}

	@Override
	protected String getDescription(Long id)
	{
		return "Collecting sites";
	}

	@Override
	protected String getPrefix()
	{
		return "all_locations";
	}
}
