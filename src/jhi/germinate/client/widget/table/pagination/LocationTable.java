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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;
import java.util.Locale;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.operators.*;

/**
 * @author Sebastian Raubach
 */
public abstract class LocationTable extends MarkableDatabaseObjectPaginationTable<Location>
{
	public LocationTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(MarkedItemList.ItemType.LOCATION, selectionMode, sortingEnabled);
	}

	@Override
	public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	protected boolean supportsFiltering()
	{
		return false;
	}

	@Override
	protected boolean supportsDownload()
	{
		return false;
	}

	@Override
	protected void download(PartialSearchQuery filter, AsyncCallback<ServerResult<String>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	protected String getClassName()
	{
		return LocationTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Location, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Location object)
				{
					return getLink(object, Long.toString(object.getId()));
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyle()
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Location.ID);
			addColumn(column, Text.LANG.collectingsiteColumnId(), sortingEnabled);
		}

		/* Add the site name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Location object)
			{
				return getLink(object, object.getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Location.SITE_NAME);
		addColumn(column, Text.LANG.collectingsiteCollsite(), sortingEnabled);

		/* Add the region column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				return object.getRegion();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Location.REGION);
		addColumn(column, Text.LANG.collectingsiteRegion(), sortingEnabled);

		/* Add the state column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				return object.getState();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Location.STATE);
		addColumn(column, Text.LANG.collectingsiteState(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				if (object.getType() != null)
					return object.getType().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return LocationType.class;
			}
		};
		column.setDataStoreName(LocationType.NAME);
		addColumn(column, Text.LANG.locationColumnType(), sortingEnabled);

		/* Add the latitude column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				if (object.getLatitude() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getLatitude()), Double.class);
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.LATITUDE);
		addColumn(column, Text.LANG.collectingsiteLatitude(), sortingEnabled);

		/* Add the longitude column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				if (object.getLongitude() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getLongitude()), Double.class);
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.LONGITUDE);
		addColumn(column, Text.LANG.collectingsiteLongitude(), sortingEnabled);

		/* Add the elevation column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				if (object.getElevation() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getElevation()));
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.ELEVATION);
		addColumn(column, Text.LANG.collectingsiteElevation(), sortingEnabled);

		/* Add the country column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Location object)
			{
				if (object.getCountry() != null)
					return object.getCountry().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
			}

			@Override
			public void render(Cell.Context context, Location object, SafeHtmlBuilder sb)
			{
				String value = getValue(object);
				if (value != null)
				{
					sb.appendHtmlConstant("<span class=\"" + Style.COUNTRY_FLAG + " " + object.getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "\"></span>");
					sb.append(SafeHtmlUtils.fromString(value));
				}
				else
				{
					super.render(context, object, sb);
				}
			}
		};
		column.setDataStoreName(Country.COUNTRY_NAME);
		addColumn(column, Text.LANG.locationColumnCountry(), sortingEnabled);
	}

	protected SafeHtml getLink(Location object, String value)
	{
		Page page = null;

		switch (object.getType())
		{
			case collectingsites:
				page = Page.ACCESSIONS_FOR_COLLSITE;
				break;
			case trialsite:
				page = Page.TRIAL_SITE_DETAILS;
				break;
			case datasets:
				page = Page.DATASET_OVERVIEW;
				break;
		}

		if (GerminateSettingsHolder.isPageAvailable(page))
			return TableUtils.getHyperlinkValue(value, "#" + page);
		else
			return SimpleHtmlTemplate.INSTANCE.text(value);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Location object, int column)
	{
		Page page = null;

		switch (object.getType())
		{
			case collectingsites:
				page = Page.ACCESSIONS_FOR_COLLSITE;
				LongParameterStore.Inst.get().put(Parameter.collectingsiteId, object.getId());
				break;
			case trialsite:
				page = Page.TRIAL_SITE_DETAILS;
				LongParameterStore.Inst.get().put(Parameter.trialsiteId, object.getId());
				break;
			case datasets:
				page = Page.DATASET_OVERVIEW;
				PartialSearchQuery query = new PartialSearchQuery();
				query.add(new SearchCondition(Location.SITE_NAME, new Equal(), object.getName(), String.class));
				FilterMappingParameterStore.Inst.get().put(Parameter.tableFilterMapping, query);
				break;
		}

		if (!GerminateSettingsHolder.isPageAvailable(page))
			event.preventDefault();
	}
}
