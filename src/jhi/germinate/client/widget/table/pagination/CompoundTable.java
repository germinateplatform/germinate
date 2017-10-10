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
public abstract class CompoundTable extends DatabaseObjectPaginationTable<Compound>
{
	public CompoundTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(selectionMode, sortingEnabled);
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
		return CompoundTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Compound, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Compound object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.COMPOUND_DETAILS))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.COMPOUND_DETAILS);
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
			column.setDataStoreName(Compound.ID);
			addColumn(column, Text.LANG.compoundColumnId(), sortingEnabled);
		}

		/* Add the name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public SafeHtml getValue(Compound object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.COMPOUND_DETAILS))
					return TableUtils.getHyperlinkValue(object.getName(), "#" + Page.COMPOUND_DETAILS);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getName());
			}
		};
		column.setDataStoreName(Compound.NAME);
		addColumn(column, Text.LANG.compoundColumnName(), sortingEnabled);

		/* Add the description column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(Compound object)
			{
				return object.getDescription();
			}
		};
		column.setDataStoreName(Compound.DESCRIPTION);
		addColumn(column, Text.LANG.compoundColumnDescription(), sortingEnabled);

		/* Add the molecular formula column */
		column = new SafeHtmlColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public SafeHtml getValue(Compound object)
			{
				return object.getFormattedMolecularFormula();
			}
		};
		column.setDataStoreName(Compound.MOLECULAR_FORMULA);
		addColumn(column, Text.LANG.compoundColumnMolecularFormula(), sortingEnabled);

		/* Add the monoisotopic mass column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return Double.class;
			}

			@Override
			public String getValue(Compound object)
			{
				if (object.getMonoisotopicMass() == null)
					return null;
				else
					return TableUtils.getCellValueAsString(Double.toString(object.getMonoisotopicMass()));
			}
		};
		column.setDataStoreName(Compound.MONOISOTOPIC_MASS);
		addColumn(column, Text.LANG.compoundColumnMonoisotonicMass(), sortingEnabled);

		/* Add the average mass column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return Double.class;
			}

			@Override
			public String getValue(Compound object)
			{
				if (object.getAverageMass() == null)
					return null;
				else
					return TableUtils.getCellValueAsString(Double.toString(object.getAverageMass()));
			}
		};
		column.setDataStoreName(Compound.AVERAGE_MASS);
		addColumn(column, Text.LANG.compoundColumnAverageMass(), sortingEnabled);

		/* Add the class column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(Compound object)
			{
				return object.getTheClass();
			}
		};
		column.setDataStoreName(Compound.CLASS);
		addColumn(column, Text.LANG.compoundColumnClass(), sortingEnabled);

		/* Add the unit column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(Compound object)
			{
				if (object.getUnit() == null)
					return null;
				else
					return object.getUnit().getName();
			}
		};
		column.setDataStoreName(Unit.NAME);
		addColumn(column, Text.LANG.compoundColumnUnitName(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Compound object, int column)
	{
		/* Get the id */
		LongParameterStore.Inst.get().put(Parameter.compoundId, object.getId());
	}
}
