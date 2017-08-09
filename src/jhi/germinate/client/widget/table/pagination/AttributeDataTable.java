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
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class AttributeDataTable extends MarkableDatabaseObjectPaginationTable<AttributeData>
{
	public AttributeDataTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(ShoppingCart.ItemType.ACCESSION, selectionMode, sortingEnabled);
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
		Column<AttributeData, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(AttributeData object)
				{
					if (object.getAccession() != null)
						return Long.toString(object.getAccession().getId());
					else return null;
				}

				@Override
				public Class getType()
				{
					return Long.class;
				}

				@Override
				public String getCellStyleNames(Cell.Context context, AttributeData object)
				{
					return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
				}
			};
			column.setDataStoreName(Accession.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), true);
		}

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(AttributeData object)
			{
				if (object.getAccession() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getAccession().getGeneralIdentifier(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getAccession().getGeneralIdentifier());
				}
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.GENERAL_IDENTIFIER);
		addColumn(column, Text.LANG.accessionsColumnGeneralIdentifier(), true);

		/* Add the accession name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(AttributeData object)
			{
				if (object.getAccession() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getAccession().getName(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getAccession().getName());
				}
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.NAME);
		addColumn(column, Text.LANG.accessionsColumnName(), true);

		/* Add the attribute name column */
		column = new TextColumn()
		{
			@Override
			public String getValue(AttributeData object)
			{
				if (object.getAttribute() == null)
					return null;
				else
					return object.getAttribute().getName();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Attribute.NAME);
		addColumn(column, Text.LANG.passportColumnAttributeName(), true);

		/* Add the attribute description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(AttributeData object)
			{
				if (object.getAttribute() == null)
					return null;
				else
					return object.getAttribute().getDescription();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Attribute.DESCRIPTION);
		addColumn(column, Text.LANG.passportColumnAttributeDescription(), true);

		/* Add the data type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(AttributeData object)
			{
				if (object.getAttribute() != null)
					return object.getAttribute().getDataType();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Attribute.DATA_TYPE);
		addColumn(column, Text.LANG.passportColumnAttributeType(), true);

		/* Add the actual value column */
		column = new TextColumn()
		{
			@Override
			public String getValue(AttributeData object)
			{
				return object.getValue();
			}

			@Override
			public Class getType()
			{
				return Object.class;
			}
		};
		column.setDataStoreName(AttributeData.VALUE);
		addColumn(column, Text.LANG.passportColumnAttributeValue(), true);
	}

	@Override
	protected void onSelectionChanged(NativeEvent event, AttributeData object, int column)
	{
		/* Get the id */
		if (object.getAccession() != null)
			LongParameterStore.Inst.get().put(Parameter.accessionId, object.getAccession().getId());
	}
}
