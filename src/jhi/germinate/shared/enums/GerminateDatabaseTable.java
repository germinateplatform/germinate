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

package jhi.germinate.shared.enums;

import jhi.germinate.client.service.*;

/**
 * This {@link Enum} contains all the tables of the database.
 *
 * @author Sebastian Raubach
 */
public enum GerminateDatabaseTable
{
	allelefrequencydata,
	analysismethod,
	attributedata,
	attributes,
	biologicalstatus,
	climatedata,
	climateoverlays,
	climates,
	collectingsources,
	compounds,
	compounddata,
	comments,
	commenttypes,
	countries,
	datasetpermissions,
	datasets,
	datasetstates,
	droughtfreqdata,
	droughtfreqseverity,
	experiments,
	experimenttypes,
	genotypes,
	germinatebase,
	groupmembers,
	groups,
	grouptypes,
	images,
	imagetypes,
	institutions,
	links,
	linktypes,
	locations,
	locationtypes,
	mapdefinitions,
	mapfeaturetypes,
	maps,
	markers,
	markertypes,
	megaenvironmentdata,
	megaenvironments,
	megaenvironmentsource,
	news,
	newstypes,
	pedigreedefinitions,
	pedigreedescriptions,
	pedigreenotations,
	pedigrees,
	phenotypedata,
	phenotypes,
	soils,
	subtaxa,
	synonyms,
	synonymtypes,
	taxonomies,
	treatments,
	trialseries,
	units;

	public boolean hasClimateData()
	{
		switch (this)
		{
			case locations:
				return true;
			default:
				return false;
		}
	}

	public String[] getColumnNames()
	{
		switch (this)
		{
			case markers:
				return MarkerService.COLUMNS_MAPDEFINITION_TABLE;
			case locations:
				return LocationService.COLUMNS_LOCATION_SORTABLE;
			case germinatebase:
				return AccessionService.COLUMNS_SORTABLE;
			default:
				return null;
		}
	}

	public enum Column
	{
		site_name,
		marker_name,
		number,
		name
	}
}
