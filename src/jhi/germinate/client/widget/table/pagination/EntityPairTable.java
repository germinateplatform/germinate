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
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.enums.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class EntityPairTable extends DatabaseObjectPaginationTable<EntityPair>
{
	public EntityPairTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return EntityPairTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<EntityPair, ?> column;

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(EntityPair object)
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
			public SafeHtml getValue(EntityPair object)
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

		/* Add the entity type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(EntityPair object)
			{
				return object.getParent().getEntityType().getName();
			}

			@Override
			public Class getType()
			{
				return EntityType.class;
			}
		};
		column.setDataStoreName(EntityType.NAME);
		addColumn(column, Text.LANG.accessionsColumnEntityType(), false);

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(EntityPair object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getChild().getGeneralIdentifier());
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
			public SafeHtml getValue(EntityPair object)
			{
				return SimpleHtmlTemplate.INSTANCE.dummyAnchor(object.getChild().getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(PedigreeService.CHILD_NAME);
		addColumn(column, Text.LANG.pedigreeColumnsChildName(), sortingEnabled);

		/* Add the entity type column */
		column = new TextColumn()
		{
			@Override
			public String getValue(EntityPair object)
			{
				return object.getChild().getEntityType().getName();
			}

			@Override
			public Class getType()
			{
				return EntityType.class;
			}
		};
		column.setDataStoreName(EntityType.NAME);
		addColumn(column, Text.LANG.accessionsColumnEntityType(), false);
	}

	@Override
	protected void onItemSelected(NativeEvent event, EntityPair object, int column)
	{
		String storeName = getTable().getColumn(column).getDataStoreName();

		Long id = null;
		switch (storeName)
		{
			case PedigreeService.CHILD_GID:
			case PedigreeService.CHILD_NAME:
				id = object.getChild().getId();
				break;
			case PedigreeService.PARENT_GID:
			case PedigreeService.PARENT_NAME:
				id = object.getParent().getId();
				break;
		}

		if (id != null)
		{
			LongParameterStore.Inst.get().put(Parameter.accessionId, id);

			if (History.getToken().equals(Page.PASSPORT.name()))
				History.fireCurrentHistoryState();
			else
				History.newItem(Page.PASSPORT.name());
		}
	}
}
