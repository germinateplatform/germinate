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

import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

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

/**
 * @author Sebastian Raubach
 */
public abstract class MarkerTable extends MarkableDatabaseObjectPaginationTable<Marker>
{
	public MarkerTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(MarkedItemList.ItemType.MARKER, selectionMode, sortingEnabled);
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
		return MarkerTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Marker, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Marker object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.MARKER_DETAILS))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.MARKER_DETAILS);
					else
						return SimpleHtmlTemplate.INSTANCE.text(Long.toString(object.getId()));
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
			column.setDataStoreName(Marker.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), sortingEnabled);
		}

		/* Add the marker name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Marker object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.MARKER_DETAILS))
					return TableUtils.getHyperlinkValue(object.getName(), "#" + Page.MARKER_DETAILS);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Marker.MARKER_NAME);
		addColumn(column, Text.LANG.markersColumnName(), sortingEnabled);

		/* Add the synonyms column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Marker object)
			{
				return object.getSynonyms();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Synonym.SYNONYM);
		addColumn(column, Text.LANG.markersColumnSynonym(), false);

		/* Add the marker type description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Marker object)
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
		column.setDataStoreName(MarkerType.DESCRIPTION);
		addColumn(column, Text.LANG.markersColumnTypeDescription(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Marker object, int column)
	{
		/* Get the id */
		LongParameterStore.Inst.get().put(Parameter.markerId, object.getId());
	}
}
