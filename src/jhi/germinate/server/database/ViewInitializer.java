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

package jhi.germinate.server.database;

import java.util.*;

/**
 * {@link ViewInitializer} contains methods to drop and create views.
 *
 * @author Sebastian Raubach
 */
public class ViewInitializer extends DatabaseInitializer
{
	private static final String QUERY_ACCESSIONS_PER_COUNTRY  = "SELECT IFNULL(`countries`.`country_code3`,'UNK') AS `country_code3`, IFNULL(`countries`.`country_name`,'UNKNOWN COUNTRY ORIGIN') AS `country_name`, count(1) AS `count` FROM ((`germinatebase` LEFT JOIN `locations` ON((`germinatebase`.`location_id` = `locations`.`id`))) LEFT JOIN `countries` ON((`countries`.`id` = `locations`.`country_id`))) WHERE entitytype_id = 1 GROUP BY `countries`.`id` ORDER BY count(1) DESC";
	private static final String QUERY_ACCESSIONS_PER_TAXONOMY = "SELECT IF(ISNULL(`taxonomies`.`genus`) AND ISNULL(`taxonomies`.`species`), 'No genus/species information found', CONCAT(`taxonomies`.`genus`,' ', LCASE(`taxonomies`.`species`))) AS `taxonomy`, count(0) AS `count` FROM (`germinatebase` LEFT JOIN `taxonomies` ON((`taxonomies`.`id` = `germinatebase`.`taxonomy_id`))) WHERE entitytype_id = 1 GROUP BY `taxonomies`.`id` ORDER BY count(0) DESC";
	private static final String QUERY_DATA_OVERVIEW           = "SELECT ( SELECT count(1) FROM germinatebase ) AS accessions, ( SELECT count(1) FROM markers ) AS markers, ( SELECT count(1) FROM locations LEFT JOIN locationtypes ON locations.locationtype_id = locationtypes.id WHERE ( locationtypes.name = 'collectingsites' ) ) AS collectingsites, ( SELECT count(1) FROM phenotypedata LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'phenotype' ) AS phenotypes";
	private static final String QUERY_PDCI                    = "select '0-1' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 0 AND pdci < 1 AND entitytype_id = 1 UNION select '1-2' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 1 AND pdci < 2 AND entitytype_id = 1 UNION select '2-3' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 2 AND pdci < 3 AND entitytype_id = 1 UNION select '3-4' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 3 AND pdci < 4 AND entitytype_id = 1 UNION select '4-5' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 4 AND pdci < 5 AND entitytype_id = 1 UNION select '5-6' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 5 AND pdci < 6 AND entitytype_id = 1 UNION select '6-7' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 6 AND pdci < 7 AND entitytype_id = 1 UNION select '7-8' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 7 AND pdci < 8 AND entitytype_id = 1 UNION select '8-9' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 8 AND pdci < 9 AND entitytype_id = 1 UNION select '9-10' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 9 AND pdci <= 10 AND entitytype_id = 1";

	private static final String[] COLUMNS_ACCESSIONS_PER_COUNTRY  = {"country_code3", "country_name", "count"};
	private static final String[] COLUMNS_ACCESSIONS_PER_TAXONOMY = {"taxonomy", "count"};
	private static final String[] COLUMNS_DATA_OVERVIEW           = {"accessions", "markers", "collectingsites", "phenotypes"};
	private static final String[] COLUMNS_PDCI                    = {"bin", "count"};

	@Override
	protected String[] getNames()
	{
		return Arrays.stream(View.values())
					 .map(View::getViewName)
					 .toArray(String[]::new);
	}

	private static final String DROP_VIEW   = "DROP VIEW IF EXISTS %s";
	private static final String CREATE_VIEW = "CREATE VIEW %s AS %s";

	@Override
	protected String[] getQueries()
	{
		return Arrays.stream(View.values())
					 .map(View::getQuery)
					 .toArray(String[]::new);
	}

	public enum View
	{
		ACCESSIONS_PER_COUNTRY("accessions_per_country", QUERY_ACCESSIONS_PER_COUNTRY, COLUMNS_ACCESSIONS_PER_COUNTRY),
		ACCESSIONS_PER_TAXONOMY("accessions_per_taxonomy", QUERY_ACCESSIONS_PER_TAXONOMY, COLUMNS_ACCESSIONS_PER_TAXONOMY),
		DATA_OVERVIEW("data_overview", QUERY_DATA_OVERVIEW, COLUMNS_DATA_OVERVIEW),
		PDCI_DISTRIBUTION("pdci_distribution", QUERY_PDCI, COLUMNS_PDCI);

		private final String   viewName;
		private final String   query;
		private final String[] columns;

		View(String viewName, String query, String[] columns)
		{
			this.viewName = viewName;
			this.query = query;
			this.columns = columns;
		}

		public String getViewName()
		{
			return viewName;
		}

		public String getQuery()
		{
			return query;
		}

		public String[] getColumns()
		{
			return columns;
		}

		public static View fromViewName(String viewName)
		{
			for (View view : View.values())
			{
				if (view.getViewName().equals(viewName))
					return view;
			}

			throw new IllegalArgumentException("Invalid View type: " + viewName);
		}
	}

	@Override
	protected String getDropStatement()
	{
		return DROP_VIEW;
	}

	@Override
	protected String getCreateStatement()
	{
		return CREATE_VIEW;
	}
}
