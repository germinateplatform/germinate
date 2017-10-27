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
	analysismethods,
	attributedata,
	attributes,
	biologicalstatus,
	climatedata,
	climateoverlays,
	climates,
	collectingsources,
	comments,
	commenttypes,
	compounddata,
	compounds,
	countries,
	databaseversions,
	datasetmembers,
	datasetmembertypes,
	datasetmeta,
	datasetpermissions,
	datasets,
	datasetstates,
	droughtfreqdata,
	droughtfreqseverity,
	experiments,
	experimenttypes,
	germinatebase,
	groupdatasets,
	groupmembers,
	groups,
	grouptypes,
	images,
	imagetypes,
	institutions,
	licensedata,
	licenselogs,
	licenses,
	links,
	linktypes,
	locales,
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
	mlsstatus,
	news,
	newstypes,
	pedigreedefinitions,
	pedigreedescriptions,
	pedigreenotations,
	pedigrees,
	phenotypedata,
	phenotypes,
	schema_version,
	soils,
	storage,
	storagedata,
	subtaxa,
	synonyms,
	synonymtypes,
	taxonomies,
	temp_details,
	treatments,
	trialseries,
	units,
	usergroupmembers,
	usergroups;

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
}
