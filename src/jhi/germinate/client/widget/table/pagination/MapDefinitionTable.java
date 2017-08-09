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

package jhi.germinate.client.widget.table.pagination;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.cellview.client.*;
import com.google.gwt.user.client.rpc.*;

import java.util.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.Map;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class MapDefinitionTable extends MarkableDatabaseObjectPaginationTable<MapDefinition>
{
	public MapDefinitionTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(ShoppingCart.ItemType.MARKER, selectionMode, sortingEnabled);
	}

	@Override
	public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		callback.onSuccess(null);
	}

	@Override
	public boolean supportsFullIdMarking()
	{
		return false;
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
	protected void createColumns()
	{
		Column<MapDefinition, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(MapDefinition object)
				{
					if (object.getMarker() != null)
					{
						if (GerminateSettingsHolder.isPageAvailable(Page.MARKER_DETAILS))
							return TableUtils.getHyperlinkValue(object.getMarker().getId(), "#" + Page.MARKER_DETAILS);
						else
							return SimpleHtmlTemplate.INSTANCE.text(Long.toString(object.getMarker().getId()));
					}
					else
						return SimpleHtmlTemplate.INSTANCE.empty();
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyleNames(Cell.Context context, MapDefinition object)
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Marker.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), true);
		}

		/* Add the marker column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(MapDefinition object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.MARKER_DETAILS) && object.getMarker() != null)
					return TableUtils.getHyperlinkValue(object.getMarker().getName(), "#" + Page.MARKER_DETAILS);
				else if (object.getMarker() != null)
					return SimpleHtmlTemplate.INSTANCE.text(object.getMarker().getName());
				else
					return SimpleHtmlTemplate.INSTANCE.empty();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Marker.MARKER_NAME);
		addColumn(column, Text.LANG.markersColumnName(), true);

		/* Add the map feature type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(MapDefinition object)
			{
				if (object.getType() != null)
					return object.getType().getDescription();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(MapFeatureType.DESCRIPTION);
		addColumn(column, Text.LANG.markersColumnFeatureDescription(), true);

		/* Add the map description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(MapDefinition object)
			{
				if (object.getMap() != null)
					return object.getMap().getDescription();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Map.DESCRIPTION);
		addColumn(column, Text.LANG.mapsColumnsMapName(), true);

		/* Add the chromosome column */
		column = new TextColumn()
		{
			@Override
			public String getValue(MapDefinition object)
			{
				return object.getChromosome();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(MapDefinition.CHROMOSOME);
		addColumn(column, Text.LANG.markersColumnChromosome(), true);

		/* Add the definition start column */
		column = new TextColumn()
		{
			@Override
			public String getValue(MapDefinition object)
			{
				if (object.getDefinitionStart() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getDefinitionStart()));
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(MapDefinition.DEFINITION_START);
		addColumn(column, Text.LANG.markersColumnDefinitionStart(), true);
	}

	@Override
	protected void onSelectionChanged(NativeEvent event, MapDefinition object, int column)
	{
		/* Get the id */
		if (object.getMarker() != null)
			LongParameterStore.Inst.get().put(Parameter.markerId, object.getMarker().getId());
	}
}
