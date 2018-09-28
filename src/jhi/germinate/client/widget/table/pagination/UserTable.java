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
import com.google.gwt.user.client.rpc.*;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class UserTable extends DatabaseObjectPaginationTable<GatekeeperUser>
{
	public UserTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(selectionMode, sortingEnabled);
	}

	@Override
	protected boolean supportsFiltering()
	{
		return true;
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
		return UserTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		/* Add the user name column */
		DatabaseObjectFilterColumn<GatekeeperUser, ?> column = new TextColumn()
		{
			@Override
			public String getValue(GatekeeperUser object)
			{
				return object.getUsername();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(GatekeeperUser.USERNAME);
		addColumn(column, Text.LANG.userColumnUsername(), sortingEnabled);

		/* Add the user full name column */
		column = new TextColumn()
		{
			@Override
			public String getValue(GatekeeperUser object)
			{
				return object.getFullName();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(GatekeeperUser.FULL_NAME);
		addColumn(column, Text.LANG.userColumnFullName(), sortingEnabled);

		/* Add the user email column */
		column = new TextColumn()
		{
			@Override
			public String getValue(GatekeeperUser object)
			{
				return object.getEmail();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(GatekeeperUser.EMAIL);
		addColumn(column, Text.LANG.userColumnEmail(), sortingEnabled);

		/* Add the user institution column */
		column = new TextColumn()
		{
			@Override
			public String getValue(GatekeeperUser object)
			{
				if (object.getInstitution() != null)
					return object.getInstitution().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName("institutions.name");
		addColumn(column, Text.LANG.userColumnInstitution(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, GatekeeperUser object, int column)
	{
	}
}
