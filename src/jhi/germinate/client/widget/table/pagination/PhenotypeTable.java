/*
 *  Copyright 2018 Information and Computational Sciences,
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
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;
import jhi.germinate.shared.search.datatype.*;

/**
 * @author Sebastian Raubach
 */
public abstract class PhenotypeTable extends DatabaseObjectPaginationTable<Phenotype>
{
	public PhenotypeTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return PhenotypeTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Phenotype, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Phenotype object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.TRAIT_DETAILS))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.TRAIT_DETAILS);
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
			column.setDataStoreName(Phenotype.ID);
			addColumn(column, Text.LANG.phenotypeColumnId(), sortingEnabled);
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
			public SafeHtml getValue(Phenotype object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.TRAIT_DETAILS))
					return TableUtils.getHyperlinkValue(object.getName(), "#" + Page.TRAIT_DETAILS);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getName());
			}
		};
		column.setDataStoreName(Phenotype.NAME);
		addColumn(column, Text.LANG.phenotypeColumnName(), sortingEnabled);

		/* Add the description column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(Phenotype object)
			{
				return object.getDescription();
			}
		};
		column.setDataStoreName(Phenotype.DESCRIPTION);
		addColumn(column, Text.LANG.phenotypeColumnDescription(), sortingEnabled);

		/* Add the synonyms column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Phenotype object)
			{
				return object.getSynonyms();
			}

			@Override
			public Class getType()
			{
				return Json.class;
			}
		};
		column.setDataStoreName(Synonym.SYNONYM);
		addColumn(column, Text.LANG.phenotypeColumnSynonym(), false);

		/* Add the unit column */
		column = new TextColumn()
		{
			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getValue(Phenotype object)
			{
				if (object.getUnit() == null)
					return null;
				else
					return object.getUnit().getName();
			}
		};
		column.setDataStoreName(Unit.NAME);
		addColumn(column, Text.LANG.phenotypeColumnUnitName(), sortingEnabled);

		column = new TextColumn()
		{
			@Override
			public String getValue(Phenotype object)
			{
				String count = object.getExtra(DatabaseObject.COUNT);

				if (StringUtils.isEmpty(count))
					return null;
				else
				{
					return count;
				}
			}

			@Override
			public Class getType()
			{
				return Long.class;
			}
		};
		column.setDataStoreName(DatabaseObject.COUNT);
		addColumn(column, Text.LANG.generalCount(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Phenotype object, int column)
	{
		/* Get the id */
		LongParameterStore.Inst.get().put(Parameter.phenotypeId, object.getId());
	}
}
