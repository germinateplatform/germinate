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

package jhi.germinate.server.manager;

import java.util.*;

import jhi.germinate.server.database.query.*;
import jhi.germinate.server.database.query.parser.*;
import jhi.germinate.server.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.exception.*;

/**
 * @author Sebastian Raubach
 */
public class ClimateManager extends AbstractManager<Climate>
{
	private static final String SELECT_ALL                 = "SELECT climates.* FROM climates WHERE EXISTS (SELECT 1 FROM climatedata WHERE climatedata.climate_id = climates.id AND climatedata.dataset_id IN (%s)) ORDER BY name";
	private static final String SELECT_ALL_HAVING_DATA     = "SELECT climates.* FROM climates WHERE EXISTS (SELECT 1 FROM climatedata WHERE climatedata.climate_id = climates.id) AND EXISTS (SELECT 1 FROM climatedata WHERE climatedata.climate_id = climates.id AND climatedata.dataset_id IN (%s)) ORDER BY name";
	private static final String SELECT_ALL_HAVING_OVERLAYS = "SELECT climates.* FROM climates WHERE EXISTS (SELECT 1 FROM climateoverlays WHERE climateoverlays.climate_id = climates.id) ORDER BY name";

	@Override
	protected String getTable()
	{
		return "climates";
	}

	@Override
	protected DatabaseObjectParser<Climate> getParser()
	{
		return Climate.Parser.Inst.get();
	}

	public static ServerResult<List<Climate>> getAll(UserAuth userAuth, List<Long> datasetIds, boolean hasClimateData) throws DatabaseException
	{
		DatasetManager.restrictToAvailableDatasets(userAuth, datasetIds);

		String query = hasClimateData ? SELECT_ALL_HAVING_DATA : SELECT_ALL;

		String formatted = String.format(query, Util.generateSqlPlaceholderString(datasetIds.size()));

		return new DatabaseObjectQuery<Climate>(formatted, userAuth)
				.setLongs(datasetIds)
				.run()
				.getObjects(Climate.Parser.Inst.get());
	}

	public static ServerResult<List<Climate>> getAllHavingOverlay(UserAuth userAuth) throws DatabaseException
	{
		return new DatabaseObjectQuery<Climate>(SELECT_ALL_HAVING_OVERLAYS, userAuth)
				.run()
				.getObjects(Climate.Parser.Inst.get());
	}
}
