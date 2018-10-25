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

import java.util.*;

import jhi.germinate.client.util.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.search.*;

/**
 * Valid {@link Parameter}s of the {@link jhi.germinate.client.util.parameterstore.TypedParameterStore}
 *
 * @author Sebastian Raubach
 */
public enum Parameter
{
	paginationPageSize(Integer.class, ParameterLifetime.PERSISTENT),
	invisibleTableColumns(List.class, ParameterLifetime.PERSISTENT),
	accessionId(Long.class, ParameterLifetime.TEMPORARY),
	generalId(String.class, ParameterLifetime.TEMPORARY),
	accessionName(String.class, ParameterLifetime.TEMPORARY),
	searchString(String.class, ParameterLifetime.TEMPORARY),
	genotypeDatasets(List.class, ParameterLifetime.VOLATILE, false),
	genotypeDatasetIds(List.class, ParameterLifetime.TEMPORARY),
	allelefreqDatasets(List.class, ParameterLifetime.VOLATILE, false),
	allelefreqDatasetIds(List.class, ParameterLifetime.TEMPORARY),
	compoundDatasets(List.class, ParameterLifetime.VOLATILE, false),
	compoundDatasetIds(List.class, ParameterLifetime.TEMPORARY),
	climateDatasets(List.class, ParameterLifetime.VOLATILE, false),
	climateDatasetIds(List.class, ParameterLifetime.TEMPORARY),
	trialsDatasets(List.class, ParameterLifetime.VOLATILE, false),
	trialsDatasetIds(List.class, ParameterLifetime.TEMPORARY),
	tableFilterMapping(PartialSearchQuery.class, ParameterLifetime.VOLATILE),
	flapjackExportResult(FlapjackAllelefreqBinningResult.class, ParameterLifetime.VOLATILE),
	collectingsiteId(Long.class, ParameterLifetime.TEMPORARY),
	groupId(Long.class, ParameterLifetime.TEMPORARY),
	markerId(Long.class, ParameterLifetime.TEMPORARY),
	markerName(String.class, ParameterLifetime.VOLATILE),
	mapId(Long.class, ParameterLifetime.TEMPORARY),
	latitude(Float.class, ParameterLifetime.VOLATILE),
	longitude(Float.class, ParameterLifetime.VOLATILE),
	newsId(Long.class, ParameterLifetime.TEMPORARY),
	phenotypeId(Long.class, ParameterLifetime.TEMPORARY),
	markedAccessionIds(List.class, ParameterLifetime.PERSISTENT),
	markedMarkerIds(List.class, ParameterLifetime.PERSISTENT),
	markedCollectingsiteIds(List.class, ParameterLifetime.PERSISTENT),
	institutionId(Long.class, ParameterLifetime.TEMPORARY),
	debugInfo(DebugInfo.class, ParameterLifetime.VOLATILE),
	markedItemType(MarkedItemList.ItemType.class, ParameterLifetime.TEMPORARY),
	groupPreviewFile(String.class, ParameterLifetime.TEMPORARY),
	compoundId(Long.class, ParameterLifetime.TEMPORARY),
	experimentId(Long.class, ParameterLifetime.TEMPORARY),
	user(UnapprovedUser.class, ParameterLifetime.VOLATILE),


	tool_id(String.class, ParameterLifetime.TEMPORARY),
	GALAXY_URL(String.class, ParameterLifetime.TEMPORARY);

	private Class<?>          type;
	private boolean           acceptFromUrl = true;
	private ParameterLifetime lifetime;

	Parameter(Class<?> type, ParameterLifetime lifetime)
	{
		this.type = type;
		this.lifetime = lifetime;
	}

	Parameter(Class<?> type, ParameterLifetime lifetime, boolean acceptFromUrl)
	{
		this(type, lifetime);
		this.acceptFromUrl = acceptFromUrl;
	}

	public static Parameter getDatasetParam(Parameter param)
	{
		switch (param)
		{
			case trialsDatasetIds:
				return trialsDatasets;
			case climateDatasetIds:
				return climateDatasets;
			case compoundDatasetIds:
				return compoundDatasets;
			case allelefreqDatasetIds:
				return allelefreqDatasets;
			case genotypeDatasetIds:
				return genotypeDatasets;
			default:
				return null;
		}
	}

	public Class<?> getType()
	{
		return type;
	}

	public boolean getAcceptFromUrl()
	{
		return acceptFromUrl;
	}

	public ParameterLifetime getLifetime()
	{
		return lifetime;
	}

	public boolean isDatasetParam()
	{
		return this == trialsDatasets || this == climateDatasets || this == compoundDatasets || this == genotypeDatasets || this == allelefreqDatasets
				|| this == trialsDatasetIds || this == climateDatasetIds || this == compoundDatasetIds || this == genotypeDatasetIds || this == allelefreqDatasetIds;
	}

	public enum ParameterLifetime
	{
		/** Volatile parameters are NOT saved as cookies/localstorage. They are only kept until the user leaves the page or refreshes. */
		VOLATILE,
		/** Temporary parameters are saved as cookies/localstorage, but expire */
		TEMPORARY,
		/** Persistent parameters are saved as cookies/localstorage and don't expire */
		PERSISTENT
	}
}
