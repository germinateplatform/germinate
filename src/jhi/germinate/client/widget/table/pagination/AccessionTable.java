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
import com.google.gwt.user.client.rpc.*;

import java.util.*;
import java.util.Locale;

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
public abstract class AccessionTable extends MarkableDatabaseObjectPaginationTable<Accession>
{
	public AccessionTable(SelectionMode selectionMode, boolean sortingEnabled)
	{
		super(MarkedItemList.ItemType.ACCESSION, selectionMode, sortingEnabled);
	}

	@Override
	public boolean supportsFullIdMarking()
	{
		return false;
	}

	@Override
	public void getIds(PartialSearchQuery filter, AsyncCallback<ServerResult<List<String>>> callback)
	{
		callback.onSuccess(null);
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
		return AccessionTable.class.getSimpleName();
	}

	@Override
	protected void createColumns()
	{
		DatabaseObjectFilterColumn<Accession, ?> column;

		if (!GerminateSettingsHolder.get().hideIdColumn.getValue())
		{
			column = new ClickableSafeHtmlColumn()
			{
				@Override
				public SafeHtml getValue(Accession object)
				{
					if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
						return TableUtils.getHyperlinkValue(object.getId(), "#" + Page.PASSPORT);
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
			column.setDataStoreName(Accession.ID);
			addColumn(column, Text.LANG.accessionsColumnId(), sortingEnabled);
		}

		/* Add the general identifier column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Accession object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
					return TableUtils.getHyperlinkValue(object.getGeneralIdentifier(), "#" + Page.PASSPORT);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getGeneralIdentifier());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.GENERAL_IDENTIFIER);
		addColumn(column, Text.LANG.accessionsColumnGeneralIdentifier(), sortingEnabled);

		/* Add the name column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Accession object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
					return TableUtils.getHyperlinkValue(object.getName(), "#" + Page.PASSPORT);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getName());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.NAME);
		addColumn(column, Text.LANG.accessionsColumnName(), sortingEnabled);

		/* Add the number column */
		column = new ClickableSafeHtmlColumn()
		{
			@Override
			public SafeHtml getValue(Accession object)
			{
				if (GerminateSettingsHolder.isPageAvailable(Page.PASSPORT))
					return TableUtils.getHyperlinkValue(object.getNumber(), "#" + Page.PASSPORT);
				else
					return SimpleHtmlTemplate.INSTANCE.text(object.getNumber());
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.NUMBER);
		addColumn(column, Text.LANG.accessionsColumnNumber(), sortingEnabled);

		/* Add the synonyms column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
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
		addColumn(column, Text.LANG.accessionsColumnSynonym(), false);

		/* Add the collector column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return object.getCollNumb();
			}

			@Override
			public Class getType()
			{
				return String.class;
			}
		};
		column.setDataStoreName(Accession.COLLNUMB);
		addColumn(column, Text.LANG.accessionsColumnCollNumber(), sortingEnabled);

		/* Add the genus column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getTaxonomy() != null)
					return object.getTaxonomy().getGenus();
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
				return Style.TEXT_ITALIC;
			}
		};
		column.setDataStoreName(Taxonomy.GENUS);
		addColumn(column, Text.LANG.passportColumnGenus(), sortingEnabled);

		/* Add the species column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getTaxonomy() != null)
					return object.getTaxonomy().getSpecies();
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
				return Style.TEXT_ITALIC;
			}
		};
		column.setDataStoreName(Taxonomy.SPECIES);
		addColumn(column, Text.LANG.passportColumnSpecies(), sortingEnabled);

		/* Add the subtaxa column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getSubtaxa() != null)
					return object.getSubtaxa().getTaxonomyIdentifier();
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
				return Style.TEXT_ITALIC;
			}
		};
		column.setDataStoreName(Subtaxa.TAXONOMY_IDENTIFIER);
		addColumn(column, Text.LANG.passportColumnSubtaxa(), sortingEnabled);

		/* Add the latitude column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getLocation() != null && object.getLocation().getLatitude() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getLocation().getLatitude()), Double.class);
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.LATITUDE);
		addColumn(column, Text.LANG.collectingsiteLatitude(), sortingEnabled);

		/* Add the longitude column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getLocation() != null && object.getLocation().getLongitude() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getLocation().getLongitude()), Double.class);
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.LONGITUDE);
		addColumn(column, Text.LANG.collectingsiteLongitude(), sortingEnabled);

		/* Add the elevation column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getLocation() != null && object.getLocation().getElevation() != null)
					return TableUtils.getCellValueAsString(Double.toString(object.getLocation().getElevation()));
				else
					return null;
			}

			@Override
			public Class getType()
			{
				return Double.class;
			}
		};
		column.setDataStoreName(Location.ELEVATION);
		addColumn(column, Text.LANG.collectingsiteElevation(), sortingEnabled);

		/* Add the country column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				if (object.getLocation() != null && object.getLocation().getCountry() != null)
					return object.getLocation().getCountry().getName();
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
			public void render(Cell.Context context, Accession object, SafeHtmlBuilder sb)
			{
				String value = getValue(object);
				if (value != null)
				{
					sb.appendHtmlConstant("<span class=\"" + Style.COUNTRY_FLAG + " " + object.getLocation().getCountry().getCountryCode2().toLowerCase(Locale.ENGLISH) + "\"></span>");
					sb.append(SafeHtmlUtils.fromString(value));
				}
				else
				{
					super.render(context, object, sb);
				}
			}
		};
		column.setDataStoreName(Country.COUNTRY_NAME);
		addColumn(column, Text.LANG.passportColumnCountry(), sortingEnabled);

		/* Add the collection date column */
		column = new TextColumn()
		{
			@Override
			public String getValue(Accession object)
			{
				return DateUtils.getLocalizedDate(object.getCollDate());
			}

			@Override
			public Class getType()
			{
				return Date.class;
			}
		};
		column.setDataStoreName(Accession.COLLDATE);
		addColumn(column, Text.LANG.passportColumnColldate(), sortingEnabled);
	}

	@Override
	protected void onItemSelected(NativeEvent event, Accession object, int column)
	{
		/* Get the id */
		LongParameterStore.Inst.get().put(Parameter.accessionId, object.getId());
	}
}
