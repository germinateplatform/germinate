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

import java.util.*;

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
public abstract class CompoundDataTable extends MarkableDatabaseObjectPaginationTable<CompoundData>
{
	public CompoundDataTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(MarkedItemList.ItemType.ACCESSION, selectionMode, sortingEnabled);
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
		return CompoundDataTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<CompoundData, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(CompoundData object)
				{
					return Long.toString(object.getAccession().getId());
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
			column.setDataStoreName(CompoundData.ID);
			addColumn(column, Text.LANG.compoundDataColumnId(), sortingEnabled);
		}

		/* Add the accession gid column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public SafeHtml getValue(CompoundData object)
			{
				if (object.getAccession() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getAccession().getGeneralIdentifier(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getAccession().getGeneralIdentifier());
				}
				else
				{
					return SimpleHtmlTemplate.INSTANCE.empty();
				}
			}
		};
		column.setDataStoreName(Accession.GENERAL_IDENTIFIER);
		addColumn(column, Text.LANG.accessionsColumnGeneralIdentifier(), sortingEnabled);

		/* Add the accession name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public SafeHtml getValue(CompoundData object)
			{
				if (object.getAccession() != null)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getAccession().getName(), "#" + Page.PASSPORT);
					else
						return SimpleHtmlTemplate.INSTANCE.text(object.getAccession().getName());
				}
				else
				{
					return SimpleHtmlTemplate.INSTANCE.empty();
				}
			}
		};
		column.setDataStoreName(Accession.NAME);
		addColumn(column, Text.LANG.accessionsColumnName(), sortingEnabled);

		/* Add the entity type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(CompoundData object)
			{
				return object.getAccession().getEntityType().getName();
			}

			@Override
			public Class getType()
			{
				return EntityType.class;
			}
		};
		column.setDataStoreName(EntityType.NAME);
		addColumn(column, new HeaderConfig(Text.LANG.accessionsColumnEntityType(), Text.LANG.accessionsColumnHelpEntityType()), sortingEnabled);

		/* Add the compound name column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(CompoundData object)
			{
				return object.getCompound().getName();
			}
		};
		column.setDataStoreName(Compound.NAME);
		addColumn(column, Text.LANG.compoundColumnName(), sortingEnabled);

		/* Add the dataset name column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(CompoundData object)
			{
				return object.getDataset().getName();
			}
		};
		column.setDataStoreName(Dataset.NAME);
		addColumn(column, Text.LANG.datasetsColumnDatasetName(), sortingEnabled);

		/* Add the dataset description column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(CompoundData object)
			{
				return object.getDataset().getDescription();
			}
		};
		column.setDataStoreName(Dataset.DESCRIPTION);
		addColumn(column, Text.LANG.datasetsColumnDatasetDescription(), sortingEnabled);

		/* Add the analysismethod name column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(CompoundData object)
			{
				if (object.getAnalysisMethod() != null)
					return object.getAnalysisMethod().getName();
				else
					return null;
			}
		};
		column.setDataStoreName(AnalysisMethod.NAME);
		addColumn(column, Text.LANG.compoundDataAnalysisMethod(), sortingEnabled);

		/* Add the unit name column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(CompoundData object)
			{
				if (object.getCompound().getUnit() != null)
					return object.getCompound().getUnit().getName();
				else
					return null;
			}
		};
		column.setDataStoreName(Unit.NAME);
		addColumn(column, Text.LANG.compoundDataUnitName(), sortingEnabled);

		/* Add the compound value column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return Double.class;
			}

			@Override
			public String getValue(CompoundData object)
			{
				if (object.getValue() == null)
					return null;
				else
					return TableUtils.getCellValueAsString(Double.toString(object.getValue()), Double.class);
			}
		};
		column.setDataStoreName(CompoundData.COMPOUND_VALUE);
		addColumn(column, Text.LANG.compoundDataColumnValue(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, CompoundData object, int column)
	{
		if (object.getAccession() != null)
			LongParameterStore.Inst.get().put(Parameter.accessionId, object.getAccession().getId());
	}
}
