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

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.*;
import com.google.gwt.safehtml.shared.*;
import com.google.gwt.user.client.rpc.*;

import java.util.Locale;

import jhi.germinate.client.i18n.Text;
import jhi.germinate.client.util.*;
import jhi.germinate.client.widget.table.column.*;
import jhi.germinate.shared.*;
import jhi.germinate.shared.Style;
import jhi.germinate.shared.datastructure.*;
import jhi.germinate.shared.datastructure.database.*;
import jhi.germinate.shared.search.*;

/**
 * @author Sebastian Raubach
 */
public abstract class InstitutionTable extends DatabaseObjectPaginationTable<Institution>
{
	public InstitutionTable(SelectionMode selectionMode, boolean sortingEnabled)
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
		return InstitutionTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Institution, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new TextColumn()
			{
				@Override
				public String getValue(Institution object)
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
			column.setDataStoreName(Institution.ID);
			addColumn(column, Text.LANG.institutionsColumnId(), sortingEnabled);
		}

		/* Add the institution name column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Institution object)
			{
				return object.getName();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Institution.NAME);
		addColumn(column, Text.LANG.institutionsColumnName(), sortingEnabled);

		/* Add the institution acronym column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Institution object)
			{
				return object.getAcronym();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Institution.ACRONYM);
		addColumn(column, Text.LANG.institutionsColumnAcronym(), sortingEnabled);

		/* Add the country column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Institution object)
			{
				if (object.getCountry() != null)
					return object.getCountry().getName();
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}

			@Override
			public String getCellStyle()
			{
				return Style.LAYOUT_WHITE_SPACE_NO_WRAP;
			}

			@Override
			public void render(Cell.Context context, Institution object, SafeHtmlBuilder sb)
			{
				String value = getValue(object);
				if (value != null)
				{
					sb.appendHtmlConstant("<span class=\"" + Style.COUNTRY_FLAG + " " + object.getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "\"></span>");
					sb.append(SafeHtmlUtils.fromString(value));
				}
				else
				{
					super.render(context, object, sb);
				}
			}
		};
		column.setDataStoreName(Country.COUNTRY_NAME);
		addColumn(column, Text.LANG.institutionsColumnCountry(), sortingEnabled);

		/* Add the institution contact column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Institution object)
			{
				return object.getContact();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Institution.CONTACT);
		addColumn(column, Text.LANG.institutionsColumnContact(), sortingEnabled);

		/* Add the institution phone column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Institution object)
			{
				return object.getPhone();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Institution.PHONE);
		addColumn(column, Text.LANG.institutionsColumnPhone(), sortingEnabled);

		/* Add the institution email column */
		column = new SafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Institution object)
			{
				if (!StringUtils.isEmpty(object.getEmail()))
					return SimpleHtmlTemplate.INSTANCE.mailto(object.getEmail());
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Institution.EMAIL);
		addColumn(column, Text.LANG.institutionsColumnEmail(), sortingEnabled);

		/* Add the institution address column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Institution object)
			{
				return object.getAddress();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Institution.ADDRESS);
		addColumn(column, Text.LANG.institutionsColumnAddress(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Institution object, int column)
	{
	}
}
