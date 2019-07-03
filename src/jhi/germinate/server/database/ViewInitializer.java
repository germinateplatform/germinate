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
	private static final String QUERY_ACCESSIONS_PER_TAXONOMY = "SELECT `taxonomies`.`genus`, `taxonomies`.`species`, `taxonomies`.`subtaxa`, COUNT( 1 ) AS `count` FROM `germinatebase` LEFT JOIN `taxonomies` ON `taxonomies`.`id` = `germinatebase`.`taxonomy_id` WHERE entitytype_id = 1 GROUP BY `taxonomies`.`id` ORDER BY COUNT(1) DESC";
	private static final String QUERY_DATA_OVERVIEW           = "SELECT ( SELECT count(1) FROM germinatebase ) AS accessions, ( SELECT count(1) FROM markers ) AS markers, ( SELECT count(1) FROM locations LEFT JOIN locationtypes ON locations.locationtype_id = locationtypes.id WHERE ( locationtypes.name = 'collectingsites' ) ) AS collectingsites, ( SELECT count(1) FROM phenotypedata LEFT JOIN datasets ON datasets.id = phenotypedata.dataset_id LEFT JOIN experiments ON experiments.id = datasets.experiment_id LEFT JOIN experimenttypes ON experimenttypes.id = experiments.experiment_type_id WHERE experimenttypes.description = 'phenotype' ) AS phenotypes";
	private static final String QUERY_PDCI                    = "select '0-1' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 0 AND pdci < 1 AND entitytype_id = 1 UNION select '1-2' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 1 AND pdci < 2 AND entitytype_id = 1 UNION select '2-3' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 2 AND pdci < 3 AND entitytype_id = 1 UNION select '3-4' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 3 AND pdci < 4 AND entitytype_id = 1 UNION select '4-5' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 4 AND pdci < 5 AND entitytype_id = 1 UNION select '5-6' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 5 AND pdci < 6 AND entitytype_id = 1 UNION select '6-7' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 6 AND pdci < 7 AND entitytype_id = 1 UNION select '7-8' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 7 AND pdci < 8 AND entitytype_id = 1 UNION select '8-9' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 8 AND pdci < 9 AND entitytype_id = 1 UNION select '9-10' AS bin, COUNT(1) AS count FROM germinatebase WHERE pdci >= 9 AND pdci <= 10 AND entitytype_id = 1";
	private static final String QUERY_MCPD                    = "SELECT puid AS PUID, institutions.CODE AS INSTCODE, general_identifier AS ACCENUMB, collnumb AS COLLNUMB, collcode AS COLLCODE, collname AS COLLNAME, institutions.address AS COLLINSTADDRESS, collmissid AS COLLMISSID, genus AS GENUS, species AS SPECIES, species_author AS SPAUTHOR, subtaxa AS SUBTAXA, subtaxa_author AS SUBTAUTHOR, cropname AS CROPNAME, g.`name` AS ACCENAME, REPLACE ( acqdate, \"-\", \"\" ) AS ACQDATE, country_code3 AS ORIGCTY, site_name AS COLLSITE, latitude AS DECLATITUDE, NULL AS LATITUDE, longitude AS DECLONGITUDE, NULL AS LONGITUDE, coordinate_uncertainty AS COORDUNCERT, coordinate_datum AS COORDDATUM, georeferencing_method AS GEOREFMETH, elevation AS ELEVATION, REPLACE ( colldate, \"-\", \"\" ) AS COLLDATE, breeders_code AS BREDCODE, breeders_name AS BREDNAME, biologicalstatus_id AS SAMPSTAT, definition AS ANCEST, collsrc_id AS COLLSRC, donor_code AS DONORCODE, donor_name AS DONORNAME, donor_number AS DONORNUMB, othernumb AS OTHERNUMB, duplsite AS DUPLSITE, duplinstname AS DUPLINSTNAME, GROUP_CONCAT( `storage`.description SEPARATOR \",\" ) AS `STORAGE`, mlsstatus_id AS MLSSTAT, ( SELECT VALUE FROM attributedata LEFT JOIN attributes ON attributes.id = attributedata.attribute_id WHERE attributes.target_table = 'germinatebase' AND attributes.`name` = 'Remarks' AND foreign_id = g.id LIMIT 1 ) AS REMARKS, 'Accession' AS `Entity Type`, NULL AS `Entity parent ACCENUMB` FROM germinatebase g LEFT JOIN taxonomies ON taxonomies.id = g.taxonomy_id LEFT JOIN locations ON locations.id = g.location_id LEFT JOIN countries ON countries.id = locations.country_id LEFT JOIN institutions ON institutions.id = g.institution_id LEFT JOIN pedigreedefinitions ON pedigreedefinitions.germinatebase_id = g.id LEFT JOIN storagedata ON storagedata.germinatebase_id = g.id LEFT JOIN `storage` ON `storage`.id = storagedata.storage_id LEFT JOIN attributedata ON attributedata.foreign_id = g.id GROUP BY g.id, pedigreedefinitions.id";
	private static final String QUERY_BIOLOGICAL_STATUS       = "select SUBSTRING_INDEX( `biologicalstatus`.`sampstat`, ' (', 1 ) AS biologicalstatus, count(1) AS `count` from biologicalstatus LEFT JOIN germinatebase ON germinatebase.biologicalstatus_id = biologicalstatus.id GROUP BY biologicalstatus.id order by count(1) desc";

	private static final String[] COLUMNS_ACCESSIONS_PER_COUNTRY  = {"country_code3", "country_name", "count"};
	private static final String[] COLUMNS_ACCESSIONS_PER_TAXONOMY = {"genus", "species", "subtaxa", "count"};
	private static final String[] COLUMNS_DATA_OVERVIEW           = {"accessions", "markers", "collectingsites", "phenotypes"};
	private static final String[] COLUMNS_PDCI                    = {"bin", "count"};
	private static final String[] COLUMNS_MCPD                    = {"PUID", "INSTCODE", "ACCENUMB", "COLLNUMB", "COLLCODE", "COLLNAME", "COLLINSTADDRESS", "COLLMISSID", "GENUS", "SPECIES", "SPAUTHOR", "SUBTAXA", "SUBTAUTHOR", "CROPNAME", "ACCENAME", "ACQDATE", "ORIGCTY", "COLLSITE", "DECLATITUDE", "LATITUDE", "DECLONGITUDE", "LONGITUDE", "COORDUNCERT", "COORDDATUM", "GEOREFMETH", "ELEVATION", "COLLDATE", "BREDCODE", "BREDNAME", "SAMPSTAT", "ANCEST", "COLLSRC", "DONORCODE", "DONORNAME", "DONORNUMB", "OTHERNUMB", "DUPLSITE", "DUPLINSTNAME", "STORAGE", "MLSSTAT", "REMARKS", "Entity Type", "Entity parent ACCENUMB"};
	private static final String[] COLUMNS_BIOLOGICAL_STATUS = {"biologicalstatus", "count"};

	private static final String DROP_VIEW   = "DROP VIEW IF EXISTS %s";
	private static final String CREATE_VIEW = "CREATE VIEW %s AS %s";

	@Override
	protected String[] getNames()
	{
		return Arrays.stream(View.values())
					 .map(View::getViewName)
					 .toArray(String[]::new);
	}

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
		PDCI_DISTRIBUTION("pdci_distribution", QUERY_PDCI, COLUMNS_PDCI),
		MCPD("mcpd", QUERY_MCPD, COLUMNS_MCPD),
		BIOLOGICALSTATUS("biological_status", QUERY_BIOLOGICAL_STATUS, COLUMNS_BIOLOGICAL_STATUS);

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
