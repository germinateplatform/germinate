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
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.service.*;
import jhi.germinate.client.util.*;
import jhi.germinate.client.util.parameterstore.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class PedigreeTable extends DatabaseObjectPaginationTable<Pedigree>
{
	public PedigreeTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return PedigreeTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Pedigree, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(Pedigree object)
				{
					return Long.toString(object.getId());
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
			column.setDataStoreName(Pedigree.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), sortingEnabled);
		}

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Pedigree object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getAccession().getGeneralIdentifier());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeService.CHILD_GID);
		addColumn(column, Text.LANG.pedigreeColumnsChildGID(), sortingEnabled);

		/* Add the name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Pedigree object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getAccession().getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeService.CHILD_NAME);
		addColumn(column, Text.LANG.pedigreeColumnsChildName(), sortingEnabled);

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Pedigree object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getParent().getGeneralIdentifier());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeService.PARENT_GID);
		addColumn(column, Text.LANG.pedigreeColumnsParentGID(), sortingEnabled);

		/* Add the name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Pedigree object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getParent().getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeService.PARENT_NAME);
		addColumn(column, Text.LANG.pedigreeColumnsParentName(), sortingEnabled);

		/* Add the relationship type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Pedigree object)
			{
				if (object.getRelationshipType() != null)
					return object.getRelationshipType();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Pedigree.RELATIONSHIP_TYPE);
		addColumn(column, new HeaderConfig(Text.LANG.pedigreeColumnsRelationshipType(), Text.LANG.pedigreeColumnsHelpRelationshipType()), sortingEnabled);

		/* Add the relationship description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Pedigree object)
			{
				return object.getRelationShipDescription();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Pedigree.RELATIONSHIP_DESCRIPTION);
		addColumn(column, Text.LANG.pedigreeColumnsRelationshipDescription(), sortingEnabled);

		/* Add the pedigree description column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Pedigree object)
			{
				if (object.getPedigreeDescription() != null)
					return object.getPedigreeDescription().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeDescription.NAME);
		addColumn(column, Text.LANG.pedigreeColumnsPedigreeDescription(), sortingEnabled);

		/* Add the pedigree author column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Pedigree object)
			{
				if (object.getPedigreeDescription() != null)
					return object.getPedigreeDescription().getAuthor();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeDescription.AUTHOR);
		addColumn(column, Text.LANG.pedigreeColumnsPedigreeAuthor(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Pedigree object, int column)
	{
		String storeName = getTable().getColumn(column).getDataStoreName();

		switch (storeName)
		{
			case PedigreeService.CHILD_GID:
			case PedigreeService.CHILD_NAME:
				LongParameterStore.Inst.get().put(Parameter.accessionId, object.getAccession().getId());
				break;
			case PedigreeService.PARENT_GID:
			case PedigreeService.PARENT_NAME:
				LongParameterStore.Inst.get().put(Parameter.accessionId, object.getParent().getId());
				break;
		}

		if (History.getToken().equals(Page.PASSPORT.name()))
			History.fireCurrentHistoryState();
		else
			History.newItem(Page.PASSPORT.name());
	}
}
