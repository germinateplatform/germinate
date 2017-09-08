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

import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class DatasetAttributeDataTable extends DatabaseObjectPaginationTable<AttributeData>
{
	public DatasetAttributeDataTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(selectionMode, sortingEnabled);
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
		return DatasetAttributeDataTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<AttributeData, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(AttributeData object)
				{
					if (object.getForeign() != null)
						return Long.toString(object.getForeign().getId());
					else return null;
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
			column.setDataStoreName(Accession.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), true);
		}

		/* Add the dataset description column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(AttributeData object)
			{
				if (object.getForeign() != null)
				{
					return SimpleHtmlTemplate.INSTANCE.text(((Dataset) object.getForeign()).getDescription());
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
		column.setDataStoreName(Dataset.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnDatasetDescription(), true);

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
	protected void onItemSelected(NativeEvent event, AttributeData object, int column)
	{
	}
}
